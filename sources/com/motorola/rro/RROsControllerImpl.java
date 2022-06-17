package com.motorola.rro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.OverlayInfo;
import android.content.om.OverlayManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.settings.CurrentUserTracker;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RROsControllerImpl extends BroadcastReceiver implements RROsController {
    static final boolean DEBUG = (!Build.IS_USER);
    private List<SubscriptionInfo> mActivitySubscriptions = new ArrayList();
    private BroadcastDispatcher mBroadcastDispatcher;
    private int mCarrierId;
    private final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentUserId;
    private MotoExtTelephonyManager mMotoExtTM;
    OverlayManager mOverlayManager;
    String mRROPkg = null;
    private final Handler mReceiverHandler;
    String mRoCarrier = "unknown";
    private final SubscriptionManager mSubscriptionManager;
    UserHandle mUserHandle;
    private final CurrentUserTracker mUserTracker;

    public RROsControllerImpl(Context context, Looper looper, Looper looper2, BroadcastDispatcher broadcastDispatcher) {
        this.mContext = context;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mReceiverHandler = new Handler(looper2);
        this.mSubscriptionManager = SubscriptionManager.from(context);
        this.mMotoExtTM = new MotoExtTelephonyManager(context);
        C26421 r2 = new CurrentUserTracker(broadcastDispatcher) {
            public void onUserSwitched(int i) {
                int unused = RROsControllerImpl.this.mCurrentUserId = i;
                RROsControllerImpl.this.mUserHandle = new UserHandle(RROsControllerImpl.this.mCurrentUserId);
            }
        };
        this.mUserTracker = r2;
        r2.startTracking();
        this.mOverlayManager = (OverlayManager) context.getSystemService(OverlayManager.class);
        this.mUserHandle = new UserHandle(this.mCurrentUserId);
        disableRROResource();
        updateActiveSubscriptions();
        updateRROsByCarrierId();
        registerListeners();
    }

    private void registerListeners() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, this.mReceiverHandler);
    }

    public void onReceive(Context context, Intent intent) {
        if (DEBUG) {
            Log.d("RROsController", "onReceive: intent=" + intent);
        }
        String action = intent.getAction();
        action.hashCode();
        if (action.equals("android.telephony.action.CARRIER_CONFIG_CHANGED")) {
            updateRROsByCarrierId();
        } else if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
            updateActiveSubscriptions();
            updateRROsByCarrierId();
        }
    }

    private void updateActiveSubscriptions() {
        this.mActivitySubscriptions.clear();
        List<SubscriptionInfo> completeActiveSubscriptionInfoList = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        if (completeActiveSubscriptionInfoList != null && completeActiveSubscriptionInfoList.size() != 0) {
            for (SubscriptionInfo subscriptionInfo : completeActiveSubscriptionInfoList) {
                if (this.mMotoExtTM.getCurrentUiccCardProvisioningStatus(subscriptionInfo.getSubscriptionId()) == 1) {
                    this.mActivitySubscriptions.add(subscriptionInfo);
                }
            }
        }
    }

    private void updateRROsByCarrierId() {
        if (MotoFeature.isPrcProduct() || isPrcProductByRoCarrier()) {
            updateRROsByCarrierIdPRC();
        } else if (this.mActivitySubscriptions.size() != 1) {
            if (DEBUG) {
                Log.i("RROsController", "mActivitySubscriptions.size() = " + this.mActivitySubscriptions.size());
            }
            updateRROsByRoCarrier();
        } else {
            int carrierId = this.mActivitySubscriptions.get(0).getCarrierId();
            this.mCarrierId = carrierId;
            String str = null;
            switch (carrierId) {
                case 1:
                    str = "com.motorola.android.systemui.overlay.tmo";
                    break;
                case 1187:
                case 1779:
                case 2119:
                case 2120:
                case 2504:
                case 10000:
                case 10012:
                case 10021:
                case 10022:
                    str = "com.motorola.android.systemui.overlay.att";
                    break;
                case 1788:
                case 2128:
                    str = "com.motorola.android.systemui.overlay.sprint";
                    break;
                case 1839:
                case 2146:
                    str = "com.motorola.android.systemui.overlay.vzw";
                    break;
                case 1952:
                    str = "com.motorola.android.systemui.overlay.usc";
                    break;
            }
            Log.i("RROsController", "updateRROsByCarrierId " + this.mCarrierId + " pkg = " + str);
            if (str == null) {
                updateRROsByRoCarrier();
            } else {
                updateRROs(str);
            }
        }
    }

    public boolean isVisibleCarrier() {
        return this.mCarrierId == 2146;
    }

    private void updateRROsByRoCarrier() {
        String roCarrier = MotoFeature.getInstance(this.mContext).getRoCarrier();
        this.mRoCarrier = roCarrier;
        if (roCarrier == null) {
            this.mRoCarrier = "unknown";
        }
        String str = null;
        String str2 = this.mRoCarrier;
        str2.hashCode();
        char c = 65535;
        switch (str2.hashCode()) {
            case -1407244190:
                if (str2.equals("attpre")) {
                    c = 0;
                    break;
                }
                break;
            case -895679974:
                if (str2.equals("sprint")) {
                    c = 1;
                    break;
                }
                break;
            case -800401520:
                if (str2.equals("vzwpre")) {
                    c = 2;
                    break;
                }
                break;
            case -433962713:
                if (str2.equals("metropcs")) {
                    c = 3;
                    break;
                }
                break;
            case 96929:
                if (str2.equals("att")) {
                    c = 4;
                    break;
                }
                break;
            case 114966:
                if (str2.equals("tmo")) {
                    c = 5;
                    break;
                }
                break;
            case 116101:
                if (str2.equals("usc")) {
                    c = 6;
                    break;
                }
                break;
            case 117299:
                if (str2.equals("vzw")) {
                    c = 7;
                    break;
                }
                break;
            case 93922211:
                if (str2.equals("boost")) {
                    c = 8;
                    break;
                }
                break;
            case 1032299505:
                if (str2.equals("cricket")) {
                    c = 9;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 4:
            case 9:
                str = "com.motorola.android.systemui.overlay.att";
                break;
            case 1:
                str = "com.motorola.android.systemui.overlay.sprint";
                break;
            case 2:
            case 7:
                str = "com.motorola.android.systemui.overlay.vzw";
                break;
            case 3:
            case 5:
            case 8:
                str = "com.motorola.android.systemui.overlay.tmo";
                break;
            case 6:
                str = "com.motorola.android.systemui.overlay.usc";
                break;
        }
        Log.i("RROsController", "updateRROsByRoCarrier ro.carrier = " + this.mRoCarrier + " pkg = " + str);
        updateRROs(str);
    }

    private void updateRROsByCarrierIdPRC() {
        if (this.mActivitySubscriptions.size() != 1) {
            if (DEBUG) {
                Log.i("RROsController", "mActivitySubscriptions.size() = " + this.mActivitySubscriptions.size());
            }
            updateRROsByRoCarrierPRC();
            return;
        }
        int carrierId = this.mActivitySubscriptions.get(0).getCarrierId();
        this.mCarrierId = carrierId;
        String str = carrierId != 1435 ? carrierId != 1436 ? carrierId != 2237 ? "com.motorola.android.systemui.overlay.retcn" : "com.motorola.android.systemui.overlay.ctcn" : "com.motorola.android.systemui.overlay.cucn" : "com.motorola.android.systemui.overlay.cmcc";
        Log.i("RROsController", "updateRROsByCarrierIdPRC " + this.mCarrierId + " pkg = " + str);
        updateRROs(str);
    }

    private void updateRROsByRoCarrierPRC() {
        String str;
        String roCarrier = MotoFeature.getInstance(this.mContext).getRoCarrier();
        this.mRoCarrier = roCarrier;
        if (roCarrier == null) {
            this.mRoCarrier = "unknown";
        }
        String str2 = this.mRoCarrier;
        str2.hashCode();
        char c = 65535;
        switch (str2.hashCode()) {
            case 3057226:
                if (str2.equals("cmcc")) {
                    c = 0;
                    break;
                }
                break;
            case 3063964:
                if (str2.equals("ctcn")) {
                    c = 1;
                    break;
                }
                break;
            case 3064925:
                if (str2.equals("cucn")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                str = "com.motorola.android.systemui.overlay.cmcc";
                break;
            case 1:
                str = "com.motorola.android.systemui.overlay.ctcn";
                break;
            case 2:
                str = "com.motorola.android.systemui.overlay.cucn";
                break;
            default:
                str = "com.motorola.android.systemui.overlay.retcn";
                break;
        }
        Log.i("RROsController", "updateRROsByRoCarrierPRC ro.carrier = " + this.mRoCarrier + " pkg = " + str);
        updateRROs(str);
    }

    public boolean isPrcProductByRoCarrier() {
        String roCarrier = MotoFeature.getInstance(this.mContext).getRoCarrier();
        this.mRoCarrier = roCarrier;
        return roCarrier.equals("cmcc") || this.mRoCarrier.equals("cucn") || this.mRoCarrier.equals("ctcn") || this.mRoCarrier.equals("retcn");
    }

    private void updateRROs(String str) {
        if (str != null) {
            enableRROResource(str);
        } else {
            disableRROResource();
        }
    }

    private void enableRROResource(String str) {
        OverlayInfo overlayInfo = this.mOverlayManager.getOverlayInfo(str, this.mUserHandle);
        if (overlayInfo == null || !overlayInfo.isEnabled()) {
            try {
                Log.i("RROsController", "RROOverlay enableRROResource = " + str);
                this.mOverlayManager.setEnabledExclusiveInCategory(str, this.mUserHandle);
                this.mRROPkg = str;
            } catch (Exception e) {
                Log.e("RROsController", "RROOverlay enableRROResource failed " + e.toString());
            }
        } else {
            Log.i("RROsController", "RROOverlay RRO " + str + " had enable.");
        }
    }

    private void disableRROResource() {
        for (OverlayInfo overlayInfo : this.mOverlayManager.getOverlayInfosForTarget("com.android.systemui", this.mUserHandle)) {
            try {
                if (overlayInfo.isEnabled() && "com.android.systemui.icon.overlay".equals(overlayInfo.category)) {
                    Log.i("RROsController", "RROOverlay disableRROResource = " + overlayInfo.packageName);
                    this.mOverlayManager.setEnabled(overlayInfo.packageName, false, this.mUserHandle);
                    this.mRROPkg = null;
                }
            } catch (IllegalStateException unused) {
                Log.e("RROsController", "RROOverlay disableRROResource failed");
            }
        }
    }

    public String getRROPkg() {
        return this.mRROPkg;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("RROsController: Current: " + this.mRROPkg);
        try {
            for (OverlayInfo overlayInfo : this.mOverlayManager.getOverlayInfosForTarget("com.android.systemui", this.mUserHandle)) {
                if (overlayInfo.isEnabled()) {
                    printWriter.println("  mPackageName.....: " + overlayInfo.getPackageName());
                }
            }
            printWriter.println();
        } catch (IllegalStateException unused) {
            Log.e("RROsController", "\nRROOverlay dump failed");
        }
    }
}
