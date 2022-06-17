package com.android.systemui.p006qs.tiles;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.lifecycle.LifecycleOwner;
import com.android.internal.app.MediaRouteDialogPresenter;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSDetailItems;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.NetworkController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/* renamed from: com.android.systemui.qs.tiles.CastTile */
public class CastTile extends QSTileImpl<QSTile.BooleanState> {
    /* access modifiers changed from: private */
    public static final Intent CAST_SETTINGS = new Intent("android.settings.CAST_SETTINGS");
    private final Callback mCallback;
    /* access modifiers changed from: private */
    public final CastController mController;
    private final CastDetailAdapter mDetailAdapter = new CastDetailAdapter();
    private Dialog mDialog;
    private final HotspotController.Callback mHotspotCallback;
    /* access modifiers changed from: private */
    public boolean mHotspotConnected;
    /* access modifiers changed from: private */
    public boolean mIsWifiDisplayEnabled;
    private final KeyguardStateController mKeyguard;
    private final NetworkController mNetworkController;
    private final NetworkController.SignalCallback mSignalCallback;
    /* access modifiers changed from: private */
    public boolean mWifiConnected;
    /* access modifiers changed from: private */
    public boolean mWifiEnabled;

    public int getMetricsCategory() {
        return 114;
    }

    public CastTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, CastController castController, KeyguardStateController keyguardStateController, NetworkController networkController, HotspotController hotspotController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        Callback callback = new Callback();
        this.mCallback = callback;
        C12461 r3 = new NetworkController.SignalCallback() {
            /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
                r0 = r3.qsIcon;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void setWifiIndicators(com.android.systemui.statusbar.policy.NetworkController.WifiIndicators r3) {
                /*
                    r2 = this;
                    boolean r0 = r3.enabled
                    if (r0 == 0) goto L_0x000f
                    com.android.systemui.statusbar.policy.NetworkController$IconState r0 = r3.qsIcon
                    if (r0 != 0) goto L_0x0009
                    goto L_0x000f
                L_0x0009:
                    boolean r0 = r0.visible
                    if (r0 == 0) goto L_0x000f
                    r0 = 1
                    goto L_0x0010
                L_0x000f:
                    r0 = 0
                L_0x0010:
                    com.android.systemui.qs.tiles.CastTile r1 = com.android.systemui.p006qs.tiles.CastTile.this
                    boolean r1 = r1.mWifiConnected
                    if (r0 == r1) goto L_0x002a
                    com.android.systemui.qs.tiles.CastTile r1 = com.android.systemui.p006qs.tiles.CastTile.this
                    boolean unused = r1.mWifiConnected = r0
                    com.android.systemui.qs.tiles.CastTile r0 = com.android.systemui.p006qs.tiles.CastTile.this
                    boolean r0 = r0.mHotspotConnected
                    if (r0 != 0) goto L_0x002a
                    com.android.systemui.qs.tiles.CastTile r0 = com.android.systemui.p006qs.tiles.CastTile.this
                    r0.refreshState()
                L_0x002a:
                    com.android.systemui.qs.tiles.CastTile r0 = com.android.systemui.p006qs.tiles.CastTile.this
                    boolean r0 = r0.mIsWifiDisplayEnabled
                    if (r0 == 0) goto L_0x0050
                    boolean r0 = r3.enabled
                    com.android.systemui.qs.tiles.CastTile r1 = com.android.systemui.p006qs.tiles.CastTile.this
                    boolean r1 = r1.mWifiEnabled
                    if (r0 == r1) goto L_0x0050
                    com.android.systemui.qs.tiles.CastTile r0 = com.android.systemui.p006qs.tiles.CastTile.this
                    boolean r3 = r3.enabled
                    boolean unused = r0.mWifiEnabled = r3
                    com.android.systemui.qs.tiles.CastTile r3 = com.android.systemui.p006qs.tiles.CastTile.this
                    boolean r3 = r3.mHotspotConnected
                    if (r3 != 0) goto L_0x0050
                    com.android.systemui.qs.tiles.CastTile r2 = com.android.systemui.p006qs.tiles.CastTile.this
                    r2.refreshState()
                L_0x0050:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p006qs.tiles.CastTile.C12461.setWifiIndicators(com.android.systemui.statusbar.policy.NetworkController$WifiIndicators):void");
            }
        };
        this.mSignalCallback = r3;
        C12472 r4 = new HotspotController.Callback() {
            public void onHotspotChanged(boolean z, int i) {
                boolean z2 = z && i > 0;
                if (z2 != CastTile.this.mHotspotConnected) {
                    boolean unused = CastTile.this.mHotspotConnected = z2;
                    if (!CastTile.this.mWifiConnected) {
                        CastTile.this.refreshState();
                    }
                }
            }
        };
        this.mHotspotCallback = r4;
        this.mController = castController;
        this.mKeyguard = keyguardStateController;
        this.mNetworkController = networkController;
        castController.observe((LifecycleOwner) this, callback);
        keyguardStateController.observe((LifecycleOwner) this, callback);
        networkController.observe((LifecycleOwner) this, r3);
        hotspotController.observe((LifecycleOwner) this, r4);
        this.mIsWifiDisplayEnabled = this.mContext.getResources().getBoolean(17891587);
        if (QSTileImpl.DEBUG) {
            String str = this.TAG;
            Log.d(str, "mIsWifiDisplayEnabled: " + this.mIsWifiDisplayEnabled);
        }
    }

    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    public QSTile.BooleanState newTileState() {
        QSTile.BooleanState booleanState = new QSTile.BooleanState();
        booleanState.handlesLongClick = false;
        return booleanState;
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        if (QSTileImpl.DEBUG) {
            String str = this.TAG;
            Log.d(str, "handleSetListening " + z);
        }
        if (!z) {
            this.mController.setDiscovering(false);
        }
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitch(int i) {
        super.handleUserSwitch(i);
        this.mController.setCurrentUserId(i);
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.CAST_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleLongClick(View view) {
        handleClick(view);
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        if (((QSTile.BooleanState) getState()).state != 0) {
            List<CastController.CastDevice> activeDevices = getActiveDevices();
            if (activeDevices.isEmpty() || (activeDevices.get(0).tag instanceof MediaRouter.RouteInfo)) {
                this.mActivityStarter.postQSRunnableDismissingKeyguard(new CastTile$$ExternalSyntheticLambda1(this));
            } else {
                this.mController.stopCasting(activeDevices.get(0));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleClick$0() {
        showDetail(true);
    }

    private List<CastController.CastDevice> getActiveDevices() {
        ArrayList arrayList = new ArrayList();
        for (CastController.CastDevice next : this.mController.getCastDevices()) {
            int i = next.state;
            if (i == 2 || i == 1) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public void showDetail(boolean z) {
        Context context;
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isLidClosed(this.mContext)) {
            context = this.mContext;
        } else {
            context = MotoFeature.getCliContext(this.mContext);
        }
        this.mUiHandler.post(new CastTile$$ExternalSyntheticLambda3(this, context));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDetail$3(Context context) {
        Dialog createDialog = MediaRouteDialogPresenter.createDialog(context, 4, new CastTile$$ExternalSyntheticLambda0(this));
        this.mDialog = createDialog;
        createDialog.getWindow().setType(2009);
        SystemUIDialog.setShowForAllUsers(this.mDialog, true);
        SystemUIDialog.registerDismissListener(this.mDialog);
        if (DesktopFeature.isDesktopDisplayContext(context)) {
            this.mDialog.getWindow().setType(2041);
        } else {
            SystemUIDialog.setWindowOnTop(this.mDialog);
        }
        this.mUiHandler.post(new CastTile$$ExternalSyntheticLambda2(this));
        this.mHost.collapsePanels();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDetail$1(View view) {
        this.mDialog.dismiss();
        this.mActivityStarter.postStartActivityDismissingKeyguard(getLongClickIntent(), 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDetail$2() {
        this.mDialog.show();
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_cast_title);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        int i;
        int i2;
        String string = this.mContext.getString(R$string.quick_settings_cast_title);
        booleanState.label = string;
        booleanState.contentDescription = string;
        booleanState.stateDescription = "";
        booleanState.value = false;
        List<CastController.CastDevice> castDevices = this.mController.getCastDevices();
        Iterator<CastController.CastDevice> it = castDevices.iterator();
        boolean z = false;
        while (true) {
            i = 2;
            if (!it.hasNext()) {
                break;
            }
            CastController.CastDevice next = it.next();
            int i3 = next.state;
            if (i3 == 2) {
                booleanState.value = true;
                booleanState.secondaryLabel = getDeviceName(next);
                booleanState.stateDescription += "," + this.mContext.getString(R$string.accessibility_cast_name, new Object[]{booleanState.label});
                z = false;
                break;
            } else if (i3 == 1) {
                z = true;
            }
        }
        if (z && !booleanState.value) {
            booleanState.secondaryLabel = this.mContext.getString(R$string.quick_settings_connecting);
        }
        if (booleanState.value) {
            i2 = R$drawable.ic_cast_connected;
        } else {
            i2 = R$drawable.ic_cast;
        }
        booleanState.icon = QSTileImpl.ResourceIcon.get(i2);
        if (canCastToWifi() || booleanState.value || (this.mIsWifiDisplayEnabled && this.mWifiEnabled)) {
            boolean z2 = booleanState.value;
            if (!z2) {
                i = 1;
            }
            booleanState.state = i;
            if (!z2) {
                booleanState.secondaryLabel = "";
            }
            booleanState.contentDescription += "," + this.mContext.getString(R$string.accessibility_quick_settings_open_details);
            booleanState.expandedAccessibilityClassName = Button.class.getName();
        } else {
            booleanState.state = 0;
            booleanState.secondaryLabel = this.mContext.getString(R$string.quick_settings_cast_no_wifi);
        }
        booleanState.stateDescription += ", " + booleanState.secondaryLabel;
        this.mDetailAdapter.updateItems(castDevices);
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (!((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_casting_turned_off);
        }
        return null;
    }

    /* access modifiers changed from: private */
    public String getDeviceName(CastController.CastDevice castDevice) {
        String str = castDevice.name;
        return str != null ? str : this.mContext.getString(R$string.quick_settings_cast_device_default_name);
    }

    private boolean canCastToWifi() {
        return this.mWifiConnected || this.mHotspotConnected;
    }

    /* renamed from: com.android.systemui.qs.tiles.CastTile$Callback */
    private final class Callback implements CastController.Callback, KeyguardStateController.Callback {
        private Callback() {
        }

        public void onCastDevicesChanged() {
            CastTile.this.refreshState();
        }

        public void onKeyguardShowingChanged() {
            CastTile.this.refreshState();
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.CastTile$CastDetailAdapter */
    private final class CastDetailAdapter implements DetailAdapter, QSDetailItems.Callback {
        private QSDetailItems mItems;
        /* access modifiers changed from: private */
        public final LinkedHashMap<String, CastController.CastDevice> mVisibleOrder;

        public int getMetricsCategory() {
            return 151;
        }

        public Boolean getToggleState() {
            return null;
        }

        public void setToggleState(boolean z) {
        }

        private CastDetailAdapter() {
            this.mVisibleOrder = new LinkedHashMap<>();
        }

        public CharSequence getTitle() {
            return CastTile.this.mContext.getString(R$string.quick_settings_cast_title);
        }

        public Intent getSettingsIntent() {
            return CastTile.CAST_SETTINGS;
        }

        public View createDetailView(Context context, View view, ViewGroup viewGroup) {
            QSDetailItems convertOrInflate = QSDetailItems.convertOrInflate(context, view, viewGroup);
            this.mItems = convertOrInflate;
            convertOrInflate.setTagSuffix("Cast");
            if (view == null) {
                if (QSTileImpl.DEBUG) {
                    Log.d(CastTile.this.TAG, "addOnAttachStateChangeListener");
                }
                this.mItems.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    public void onViewAttachedToWindow(View view) {
                        if (QSTileImpl.DEBUG) {
                            Log.d(CastTile.this.TAG, "onViewAttachedToWindow");
                        }
                    }

                    public void onViewDetachedFromWindow(View view) {
                        if (QSTileImpl.DEBUG) {
                            Log.d(CastTile.this.TAG, "onViewDetachedFromWindow");
                        }
                        CastDetailAdapter.this.mVisibleOrder.clear();
                    }
                });
            }
            this.mItems.setEmptyState(R$drawable.ic_qs_cast_detail_empty, R$string.quick_settings_cast_detail_empty_text);
            this.mItems.setCallback(this);
            updateItems(CastTile.this.mController.getCastDevices());
            CastTile.this.mController.setDiscovering(true);
            return this.mItems;
        }

        /* access modifiers changed from: private */
        public void updateItems(List<CastController.CastDevice> list) {
            int i;
            if (this.mItems != null) {
                QSDetailItems.Item[] itemArr = null;
                if (list != null && !list.isEmpty()) {
                    Iterator<CastController.CastDevice> it = list.iterator();
                    while (true) {
                        i = 0;
                        if (!it.hasNext()) {
                            break;
                        }
                        CastController.CastDevice next = it.next();
                        if (next.state == 2) {
                            QSDetailItems.Item item = new QSDetailItems.Item();
                            item.iconResId = R$drawable.ic_cast_connected;
                            item.line1 = CastTile.this.getDeviceName(next);
                            item.line2 = CastTile.this.mContext.getString(R$string.quick_settings_connected);
                            item.tag = next;
                            item.canDisconnect = true;
                            itemArr = new QSDetailItems.Item[]{item};
                            break;
                        }
                    }
                    if (itemArr == null) {
                        for (CastController.CastDevice next2 : list) {
                            this.mVisibleOrder.put(next2.f134id, next2);
                        }
                        itemArr = new QSDetailItems.Item[list.size()];
                        for (String str : this.mVisibleOrder.keySet()) {
                            CastController.CastDevice castDevice = this.mVisibleOrder.get(str);
                            if (list.contains(castDevice)) {
                                QSDetailItems.Item item2 = new QSDetailItems.Item();
                                item2.iconResId = R$drawable.ic_cast;
                                item2.line1 = CastTile.this.getDeviceName(castDevice);
                                if (castDevice.state == 1) {
                                    item2.line2 = CastTile.this.mContext.getString(R$string.quick_settings_connecting);
                                }
                                item2.tag = castDevice;
                                itemArr[i] = item2;
                                i++;
                            }
                        }
                    }
                }
                this.mItems.setItems(itemArr);
            }
        }

        public void onDetailItemClick(QSDetailItems.Item item) {
            if (item != null && item.tag != null) {
                MetricsLogger.action(CastTile.this.mContext, 157);
                CastTile.this.mController.startCasting((CastController.CastDevice) item.tag);
            }
        }

        public void onDetailItemDisconnect(QSDetailItems.Item item) {
            if (item != null && item.tag != null) {
                MetricsLogger.action(CastTile.this.mContext, 158);
                CastTile.this.mController.stopCasting((CastController.CastDevice) item.tag);
            }
        }
    }
}
