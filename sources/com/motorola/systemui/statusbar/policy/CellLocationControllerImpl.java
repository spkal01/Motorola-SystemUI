package com.motorola.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.ICellBroadcastService;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.SparseArray;
import com.motorola.systemui.areacode.CBPhoneStateListener;
import com.motorola.systemui.areacode.CBUtil;
import com.motorola.systemui.areacode.LogUtil;
import java.util.List;

public final class CellLocationControllerImpl extends BroadcastReceiver implements CellLocationController {
    private static SparseArray<String> mLatestMessageBySlotIndex;
    private CellBroadcastServiceConnection mCellBroadcastServiceConnection;
    private final Context mContext;
    private int mListenersCount;
    private boolean mShowLatestAreaInfo;

    public CellLocationControllerImpl(Context context) {
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CellLocationController", ": init CellLocationControllerImpl");
        }
        this.mContext = context != null ? context.getApplicationContext() : null;
        mLatestMessageBySlotIndex = new SparseArray<>();
    }

    public void onReceive(Context context, Intent intent) {
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CellLocationController", "onReceive " + intent);
        }
        if (intent != null) {
            String action = intent.getAction();
            if ("android.telephony.action.AREA_INFO_UPDATED".equals(action)) {
                int intExtra = intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1);
                if (intExtra == -1) {
                    if (LogUtil.isLoggableD()) {
                        LogUtil.m95d("CellLocationController", "SMS message with no slotIndex!");
                    }
                    updateAreaInfoText();
                } else {
                    if (LogUtil.isLoggableD()) {
                        LogUtil.m95d("CellLocationController", "SMS message for slot: " + intExtra);
                    }
                    updateAreaInfoTextBySlot(intExtra);
                }
            }
            boolean z = false;
            if ("android.telephony.action.SIM_APPLICATION_STATE_CHANGED".equals(action)) {
                int intExtra2 = intent.getIntExtra("android.telephony.extra.SIM_STATE", 0);
                int intExtra3 = intent.getIntExtra("subscription", -1);
                if (intExtra2 == 5 || intExtra2 == 10) {
                    if (LogUtil.isLoggableD()) {
                        LogUtil.m95d("CellLocationController", "Register Service or Location change when sim Loaded");
                    }
                    if (CBUtil.isBrazil(this.mContext) && CBUtil.isBrazilSettingsEnabled(this.mContext)) {
                        z = true;
                    }
                    this.mShowLatestAreaInfo = z;
                    if (!SubscriptionManager.isValidSubscriptionId(intExtra3)) {
                        int intExtra4 = intent.getIntExtra("slot", Integer.MAX_VALUE);
                        if (LogUtil.isLoggableD()) {
                            LogUtil.m95d("CellLocationController", "In case of invalid subId, try slotId " + intExtra4);
                        }
                        intExtra3 = CBUtil.getSubIdBySlotIndex(intExtra4);
                    }
                    if (SubscriptionManager.isValidSubscriptionId(intExtra3)) {
                        configureAreaInfo(intExtra3);
                        return;
                    }
                    return;
                }
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CellLocationController", "Unregister Location change when sim is not Ready or Loaded");
                }
                CBPhoneStateListener.unregisterStateListener(context, intExtra3);
                CBUtil.clearAreaInfoOnSystemUI(context, intExtra3);
            } else if ("android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action)) {
                if (CBPhoneStateListener.hasRegisteredListeners()) {
                    CBPhoneStateListener.unregisterAllListeners(context);
                    CBUtil.clearAreaInfoOnSystemUI(context);
                }
                CBPhoneStateListener.registerAllListeners(context);
            } else if ("com.android.systemui.GET_LATEST_SYSTEMUI_AREA_INFO_ACTION".equals(action)) {
                if (CBUtil.isBrazilSettingsEnabled(context) || CBUtil.isBrazil(context)) {
                    int intExtra5 = intent.getIntExtra("subscription", -1);
                    if (LogUtil.isLoggableD()) {
                        LogUtil.m95d("CellLocationController", "GET_LATEST_SYSTEMUI_AREA_INFO_ACTION for subId: " + intExtra5);
                    }
                    broadcastLatestAreaInfo();
                }
            } else if ("com.android.cellbroadcastreceiver.action.AREA_UPDATE_INFO_ENABLED".equals(action)) {
                boolean booleanExtra = intent.getBooleanExtra("enable", false);
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CellLocationController", "AREA_UPDATE_ENABLED_ACTION enable:" + booleanExtra);
                }
                CBUtil.saveAreaUpdateBroadcastState(booleanExtra, this.mContext);
                if (this.mShowLatestAreaInfo) {
                    CBUtil.broadcastEnableIntent(this.mContext, booleanExtra);
                }
            }
        } else if (intent == null && !CBPhoneStateListener.hasRegisteredListeners()) {
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CellLocationController", "Restore the listener state");
            }
            CBUtil.restoreListenerState(context);
        }
    }

    private void broadcastLatestAreaInfo() {
        for (int i = 0; i < mLatestMessageBySlotIndex.size(); i++) {
            String valueAt = mLatestMessageBySlotIndex.valueAt(i);
            int subIdBySlotIndex = CBUtil.getSubIdBySlotIndex(i);
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CellLocationController", "Latest stored message for slotIndex: " + i + " and subId: " + subIdBySlotIndex + " is: " + valueAt);
            }
            CBUtil.broadcastAreaInfo(this.mContext, valueAt, subIdBySlotIndex);
        }
    }

    public void registerListeners(int i) {
        mLatestMessageBySlotIndex.put(i, (Object) null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telephony.action.AREA_INFO_UPDATED");
        intentFilter.addAction("android.telephony.action.SIM_APPLICATION_STATE_CHANGED");
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        intentFilter.addAction("com.android.systemui.GET_LATEST_SYSTEMUI_AREA_INFO_ACTION");
        intentFilter.addAction("com.android.cellbroadcastreceiver.action.AREA_UPDATE_INFO_ENABLED");
        if (this.mContext != null) {
            if (this.mListenersCount == 0) {
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CellLocationController", "Registering Listeners");
                }
                this.mContext.registerReceiver(this, intentFilter);
            }
            this.mListenersCount++;
        }
    }

    public void unregisterListeners() {
        int i = this.mListenersCount;
        if (i != 0 && this.mContext != null) {
            int i2 = i - 1;
            this.mListenersCount = i2;
            if (i2 == 0) {
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CellLocationController", "Unregistering Listener");
                }
                this.mContext.unregisterReceiver(this);
                CBPhoneStateListener.unregisterAllListeners(this.mContext);
                unbindCellBroacastService();
                mLatestMessageBySlotIndex.clear();
            }
        }
    }

    public void setLatestBrazilAreaInfo(int i, String str) {
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CellLocationController", "Saving message: " + str + " for slotIndex: " + i);
        }
        mLatestMessageBySlotIndex.put(i, str);
    }

    public void requestAreaInfo(int i) {
        if (this.mShowLatestAreaInfo) {
            updateAreaInfoTextBySlot(CBUtil.getSlotIndexBySubId(i));
        }
    }

    private void configureAreaInfo(int i) {
        this.mShowLatestAreaInfo = CBUtil.isBrazil(this.mContext) || CBUtil.isBrazilSettingsEnabled(this.mContext);
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CellLocationController", ": configureAreaInfo");
            LogUtil.m95d("CellLocationController", ": mShowLatestAreaInfo is " + this.mShowLatestAreaInfo);
        }
        if (this.mShowLatestAreaInfo) {
            CBUtil.broadcastEnableIntent(this.mContext, CBUtil.isAreaUpdateBroadcastEnabled(this.mContext));
            if (this.mCellBroadcastServiceConnection == null) {
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CellLocationController", ": bind CellBroadcastService");
                }
                bindCellBroadcastService();
            } else {
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CellLocationController", ": CellBroadcastService bound, updating area info");
                }
                updateAreaInfoText();
            }
            CBPhoneStateListener.registerStateListener(this.mContext, i);
        }
    }

    private void bindCellBroadcastService() {
        this.mCellBroadcastServiceConnection = new CellBroadcastServiceConnection();
        Intent intent = new Intent("android.telephony.CellBroadcastService");
        String cellBroadcastServicePackage = getCellBroadcastServicePackage();
        if (!TextUtils.isEmpty(cellBroadcastServicePackage)) {
            intent.setPackage(cellBroadcastServicePackage);
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (cellBroadcastServiceConnection == null || cellBroadcastServiceConnection.getService() != null) {
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CellLocationController", "skipping bindService because connection already exists");
                }
            } else if (!this.mContext.bindService(intent, this.mCellBroadcastServiceConnection, 1) && LogUtil.isLoggableD()) {
                LogUtil.m95d("CellLocationController", "Unable to bind to service");
            }
        }
    }

    private String getCellBroadcastServicePackage() {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent("android.telephony.CellBroadcastService"), 1048576);
        if (LogUtil.isLoggableD() && queryIntentServices != null) {
            LogUtil.m95d("CellLocationController", "getCellBroadcastServicePackageName: found " + queryIntentServices.size() + " CBS packages");
        }
        for (ResolveInfo resolveInfo : queryIntentServices) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                String str = serviceInfo.packageName;
                if (!TextUtils.isEmpty(str)) {
                    if (packageManager.checkPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", str) == 0) {
                        if (LogUtil.isLoggableD()) {
                            LogUtil.m95d("CellLocationController", "getCellBroadcastServicePackageName: " + str);
                        }
                        return str;
                    } else if (LogUtil.isLoggableD()) {
                        LogUtil.m95d("CellLocationController", "getCellBroadcastServicePackageName: " + str + " does not have READ_PRIVILEGED_PHONE_STATE permission");
                    }
                } else if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CellLocationController", "getCellBroadcastServicePackageName: found a CBS package but packageName is null/empty");
                }
            }
        }
        if (!LogUtil.isLoggableD()) {
            return null;
        }
        LogUtil.m95d("CellLocationController", "getCellBroadcastServicePackageName: package name not found");
        return null;
    }

    public void unbindCellBroacastService() {
        if (this.mShowLatestAreaInfo) {
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (!(cellBroadcastServiceConnection == null || cellBroadcastServiceConnection.getService() == null)) {
                this.mContext.unbindService(this.mCellBroadcastServiceConnection);
            }
            this.mCellBroadcastServiceConnection = null;
        }
    }

    /* access modifiers changed from: private */
    public void updateAreaInfoText() {
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CellLocationController", ": updateAreaInfoText");
        }
        for (int i = 0; i < mLatestMessageBySlotIndex.size(); i++) {
            updateAreaInfoTextBySlot(mLatestMessageBySlotIndex.keyAt(i));
        }
    }

    private void updateAreaInfoTextBySlot(int i) {
        CellBroadcastServiceConnection cellBroadcastServiceConnection;
        ICellBroadcastService asInterface;
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CellLocationController", ": updateAreaInfoTextBySlot: " + i);
        }
        if (this.mShowLatestAreaInfo && (cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection) != null && (asInterface = ICellBroadcastService.Stub.asInterface(cellBroadcastServiceConnection.getService())) != null) {
            try {
                String valueOf = String.valueOf(asInterface.getCellBroadcastAreaInfo(i));
                int subIdBySlotIndex = CBUtil.getSubIdBySlotIndex(i);
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CellLocationController", ": message is = " + valueOf + "for slot: " + i + " and subId:" + subIdBySlotIndex);
                }
                if (CBPhoneStateListener.hasService(subIdBySlotIndex)) {
                    if (LogUtil.isLoggableD()) {
                        LogUtil.m95d("CellLocationController", "CBPhoneStateListener ok");
                    }
                    if (CBPhoneStateListener.isRegisteredOn2GNetwork(subIdBySlotIndex)) {
                        if (LogUtil.isLoggableD()) {
                            LogUtil.m95d("CellLocationController", "SIM is registered on 2G network - should update area info from channel 50");
                        }
                        mLatestMessageBySlotIndex.put(i, valueOf);
                        CBUtil.broadcastAreaInfo(this.mContext, valueOf, subIdBySlotIndex);
                    }
                }
            } catch (RemoteException e) {
                LogUtil.m96e("CellLocationController", "Can't get area info. e=" + e);
            }
        }
    }

    private class CellBroadcastServiceConnection implements ServiceConnection {
        private IBinder mService;

        private CellBroadcastServiceConnection() {
        }

        public IBinder getService() {
            return this.mService;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CellLocationController", ": onServiceConnected to CellBroadcastService");
            }
            this.mService = iBinder;
            CellLocationControllerImpl.this.updateAreaInfoText();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            this.mService = null;
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CellLocationController", "mICellBroadcastService has disconnected unexpectedly");
            }
        }

        public void onBindingDied(ComponentName componentName) {
            this.mService = null;
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CellLocationController", "Binding died");
            }
        }

        public void onNullBinding(ComponentName componentName) {
            this.mService = null;
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CellLocationController", "Null binding");
            }
        }
    }
}
