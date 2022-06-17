package com.android.systemui.p006qs.tiles;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.AlphaControlledSignalTileView;
import com.android.systemui.p006qs.QSDetailItems;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.plugins.p005qs.QSIconView;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.wifitrackerlib.WifiEntry;
import com.motorola.settingslib.hotspothelper.HotspotHelper;
import java.util.List;

/* renamed from: com.android.systemui.qs.tiles.WifiTile */
public class WifiTile extends QSTileImpl<QSTile.SignalState> {
    /* access modifiers changed from: private */
    public static final Intent WIFI_SETTINGS = new Intent("android.settings.WIFI_SETTINGS");
    protected final NetworkController mController;
    /* access modifiers changed from: private */
    public final WifiDetailAdapter mDetailAdapter = ((WifiDetailAdapter) createDetailAdapter());
    private boolean mExpectDisabled;
    protected final WifiSignalCallback mSignalCallback;
    private final QSTile.SignalState mStateBeforeClick;
    /* access modifiers changed from: private */
    public final NetworkController.AccessPointController mWifiController;

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_windowNoTitle;
    }

    public WifiTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, NetworkController networkController, NetworkController.AccessPointController accessPointController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        QSTile.SignalState newTileState = newTileState();
        this.mStateBeforeClick = newTileState;
        WifiSignalCallback wifiSignalCallback = new WifiSignalCallback();
        this.mSignalCallback = wifiSignalCallback;
        this.mController = networkController;
        this.mWifiController = accessPointController;
        networkController.observe(getLifecycle(), wifiSignalCallback);
        newTileState.spec = "wifi";
    }

    public QSTile.SignalState newTileState() {
        return new QSTile.SignalState();
    }

    public void setDetailListening(boolean z) {
        if (z) {
            this.mWifiController.addAccessPointCallback(this.mDetailAdapter);
        } else {
            this.mWifiController.removeAccessPointCallback(this.mDetailAdapter);
        }
    }

    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    /* access modifiers changed from: protected */
    public DetailAdapter createDetailAdapter() {
        return new WifiDetailAdapter();
    }

    public QSIconView createTileView(Context context) {
        return new AlphaControlledSignalTileView(context);
    }

    public Intent getLongClickIntent() {
        return WIFI_SETTINGS;
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        boolean z = ((QSTile.SignalState) this.mState).value;
        MetricsLogger.action(this.mContext, getMetricsCategory(), !z);
        if (!shouldWarnHotspotConflict(z)) {
            handleClickWifiTile();
        } else if (HotspotHelper.getDoNotShowWifiWarning(this.mContext)) {
            HotspotHelper.disableWifiHotspot(this.mContext);
            handleClickWifiTile();
        } else {
            createWarningDialog().show();
        }
    }

    private boolean shouldWarnHotspotConflict(boolean z) {
        boolean z2 = !z && HotspotHelper.isWifiHotspotActive(this.mContext) && !isDualInterfaceSupported();
        if (!HotspotHelper.shouldWarnWhenEnablingWifi(this.mContext) || !z2) {
            return false;
        }
        return true;
    }

    private boolean isDualInterfaceSupported() {
        return ((WifiManager) this.mContext.getSystemService("wifi")).isStaApConcurrencySupported();
    }

    private SystemUIDialog createWarningDialog() {
        SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext);
        systemUIDialog.setTitle(HotspotHelper.getWarningDialogTitle(this.mContext));
        systemUIDialog.setMessage(HotspotHelper.getWifiWarningDialogMessage(this.mContext));
        systemUIDialog.setPositiveButton(17039370, new WifiTile$$ExternalSyntheticLambda0(this, systemUIDialog));
        systemUIDialog.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        systemUIDialog.setCanceledOnTouchOutside(false);
        systemUIDialog.setView(systemUIDialog.getLayoutInflater().inflate(R$layout.do_not_show_again_checkbox, (ViewGroup) null));
        return systemUIDialog;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createWarningDialog$0(SystemUIDialog systemUIDialog, DialogInterface dialogInterface, int i) {
        if (((CheckBox) systemUIDialog.findViewById(R$id.do_not_show_again)).isChecked()) {
            HotspotHelper.setDoNotShowWifiWarning(this.mContext);
        }
        HotspotHelper.disableWifiHotspot(this.mContext);
        handleClickWifiTile();
    }

    private void handleClickWifiTile() {
        Object obj;
        ((QSTile.SignalState) this.mState).copyTo(this.mStateBeforeClick);
        boolean z = ((QSTile.SignalState) this.mState).value;
        if (z) {
            obj = null;
        } else {
            obj = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        }
        refreshState(obj);
        this.mController.setWifiEnabled(!z);
        this.mExpectDisabled = z;
        if (z) {
            this.mHandler.postDelayed(new WifiTile$$ExternalSyntheticLambda1(this), 350);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleClickWifiTile$1() {
        if (this.mExpectDisabled) {
            this.mExpectDisabled = false;
            refreshState();
        }
    }

    /* access modifiers changed from: protected */
    public void handleSecondaryClick(View view) {
        if (!this.mWifiController.canConfigWifi()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.WIFI_SETTINGS"), 0);
            return;
        }
        showDetail(true);
        if (!((QSTile.SignalState) this.mState).value) {
            this.mController.setWifiEnabled(true);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_wifi_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.SignalState signalState, Object obj) {
        int i;
        if (QSTileImpl.DEBUG) {
            String str = this.TAG;
            Log.d(str, "handleUpdateState arg=" + obj);
        }
        CallbackInfo callbackInfo = this.mSignalCallback.mInfo;
        if (this.mExpectDisabled) {
            if (!callbackInfo.enabled) {
                this.mExpectDisabled = false;
            } else {
                return;
            }
        }
        boolean z = obj == QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        boolean z2 = callbackInfo.enabled;
        boolean z3 = z2 && (i = callbackInfo.wifiSignalIconId) > 0 && !(callbackInfo.ssid == null && i == 17303763);
        boolean z4 = callbackInfo.ssid == null && callbackInfo.wifiSignalIconId == 17303763;
        if (signalState.value != z2) {
            this.mDetailAdapter.setItemsVisible(z2);
            fireToggleStateChanged(callbackInfo.enabled);
        }
        if (signalState.slash == null) {
            QSTile.SlashState slashState = new QSTile.SlashState();
            signalState.slash = slashState;
            slashState.rotation = 6.0f;
        }
        signalState.slash.isSlashed = false;
        boolean z5 = z || callbackInfo.isTransient;
        signalState.secondaryLabel = getSecondaryLabel(z5, callbackInfo.statusLabel);
        signalState.state = 2;
        signalState.dualTarget = true;
        signalState.value = z || callbackInfo.enabled;
        boolean z6 = callbackInfo.enabled;
        signalState.activityIn = z6 && callbackInfo.activityIn;
        signalState.activityOut = z6 && callbackInfo.activityOut;
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        Resources resources = this.mContext.getResources();
        if (z5) {
            signalState.icon = QSTileImpl.ResourceIcon.get(17303731);
            signalState.label = resources.getString(R$string.quick_settings_wifi_label);
        } else if (!signalState.value) {
            signalState.slash.isSlashed = true;
            signalState.state = 1;
            signalState.icon = QSTileImpl.ResourceIcon.get(17303763);
            signalState.label = resources.getString(R$string.quick_settings_wifi_label);
        } else if (z3) {
            signalState.icon = QSTileImpl.ResourceIcon.get(callbackInfo.wifiSignalIconId);
            String str2 = callbackInfo.ssid;
            signalState.label = str2 != null ? removeDoubleQuotes(str2) : getTileLabel();
        } else if (z4) {
            signalState.icon = QSTileImpl.ResourceIcon.get(17303763);
            signalState.label = resources.getString(R$string.quick_settings_wifi_label);
        } else {
            signalState.icon = QSTileImpl.ResourceIcon.get(17303763);
            signalState.label = resources.getString(R$string.quick_settings_wifi_label);
        }
        stringBuffer.append(this.mContext.getString(R$string.quick_settings_wifi_label));
        stringBuffer.append(",");
        if (signalState.value && z3) {
            stringBuffer2.append(callbackInfo.wifiSignalContentDescription);
            stringBuffer.append(removeDoubleQuotes(callbackInfo.ssid));
            if (!TextUtils.isEmpty(signalState.secondaryLabel)) {
                stringBuffer.append(",");
                stringBuffer.append(signalState.secondaryLabel);
            } else {
                signalState.secondaryLabel = " ";
            }
        }
        signalState.stateDescription = stringBuffer2.toString();
        signalState.contentDescription = stringBuffer.toString();
        signalState.dualLabelContentDescription = resources.getString(R$string.accessibility_quick_settings_open_settings, new Object[]{getTileLabel()});
        signalState.expandedAccessibilityClassName = Switch.class.getName();
    }

    private CharSequence getSecondaryLabel(boolean z, String str) {
        return z ? this.mContext.getString(R$string.quick_settings_wifi_secondary_label_transient) : str;
    }

    /* access modifiers changed from: protected */
    public boolean shouldAnnouncementBeDelayed() {
        return this.mStateBeforeClick.value == ((QSTile.SignalState) this.mState).value;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((QSTile.SignalState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_wifi_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_wifi_changed_off);
    }

    public boolean isAvailable() {
        return (!FeatureFlags.isProviderModelSettingEnabled(this.mContext) || MotoFeature.getInstance(this.mContext).isCustomPanelView()) && this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi");
    }

    private static String removeDoubleQuotes(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length <= 1 || str.charAt(0) != '\"') {
            return str;
        }
        int i = length - 1;
        return str.charAt(i) == '\"' ? str.substring(1, i) : str;
    }

    /* renamed from: com.android.systemui.qs.tiles.WifiTile$CallbackInfo */
    protected static final class CallbackInfo {
        boolean activityIn;
        boolean activityOut;
        boolean connected;
        boolean enabled;
        boolean isTransient;
        String ssid;
        public String statusLabel;
        String wifiSignalContentDescription;
        int wifiSignalIconId;

        protected CallbackInfo() {
        }

        public String toString() {
            return "CallbackInfo[" + "enabled=" + this.enabled + ",connected=" + this.connected + ",wifiSignalIconId=" + this.wifiSignalIconId + ",ssid=" + this.ssid + ",activityIn=" + this.activityIn + ",activityOut=" + this.activityOut + ",wifiSignalContentDescription=" + this.wifiSignalContentDescription + ",isTransient=" + this.isTransient + ']';
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.WifiTile$WifiSignalCallback */
    protected final class WifiSignalCallback implements NetworkController.SignalCallback {
        final CallbackInfo mInfo = new CallbackInfo();

        protected WifiSignalCallback() {
        }

        public void setWifiIndicators(NetworkController.WifiIndicators wifiIndicators) {
            if (QSTileImpl.DEBUG) {
                String access$100 = WifiTile.this.TAG;
                Log.d(access$100, "onWifiSignalChanged enabled=" + wifiIndicators.enabled);
            }
            NetworkController.IconState iconState = wifiIndicators.qsIcon;
            if (iconState != null) {
                CallbackInfo callbackInfo = this.mInfo;
                callbackInfo.enabled = wifiIndicators.enabled;
                callbackInfo.connected = iconState.visible;
                callbackInfo.wifiSignalIconId = iconState.icon;
                callbackInfo.ssid = wifiIndicators.description;
                callbackInfo.activityIn = wifiIndicators.motoQSActivityIn;
                callbackInfo.activityOut = wifiIndicators.motoQSActivityOut;
                callbackInfo.wifiSignalContentDescription = iconState.contentDescription;
                callbackInfo.isTransient = wifiIndicators.isTransient;
                callbackInfo.statusLabel = wifiIndicators.statusLabel;
                if (WifiTile.this.isShowingDetail()) {
                    WifiTile.this.mDetailAdapter.updateItems();
                }
                WifiTile.this.refreshState();
            }
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.WifiTile$WifiDetailAdapter */
    protected class WifiDetailAdapter implements DetailAdapter, NetworkController.AccessPointController.AccessPointCallback, QSDetailItems.Callback {
        private WifiEntry[] mAccessPoints;
        private QSDetailItems mItems;

        public int getMetricsCategory() {
            return 152;
        }

        public void onDetailItemDisconnect(QSDetailItems.Item item) {
        }

        protected WifiDetailAdapter() {
        }

        public CharSequence getTitle() {
            if (!MotoFeature.getInstance(WifiTile.this.mContext).isSupportCli() || !MotoFeature.isLidClosed(WifiTile.this.mContext)) {
                return WifiTile.this.mContext.getString(R$string.quick_settings_wifi_label);
            }
            return WifiTile.this.mContext.getString(R$string.cli_qs_detail_wifi_label);
        }

        public Intent getSettingsIntent() {
            return WifiTile.WIFI_SETTINGS;
        }

        public Boolean getToggleState() {
            return Boolean.valueOf(((QSTile.SignalState) WifiTile.this.mState).value);
        }

        public void setToggleState(boolean z) {
            if (QSTileImpl.DEBUG) {
                String access$1200 = WifiTile.this.TAG;
                Log.d(access$1200, "setToggleState " + z);
            }
            MetricsLogger.action(WifiTile.this.mContext, 153, z);
            WifiTile.this.mController.setWifiEnabled(z);
        }

        public View createDetailView(Context context, View view, ViewGroup viewGroup) {
            if (QSTileImpl.DEBUG) {
                String access$1500 = WifiTile.this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("createDetailView convertView=");
                sb.append(view != null);
                Log.d(access$1500, sb.toString());
            }
            this.mAccessPoints = null;
            QSDetailItems convertOrInflate = QSDetailItems.convertOrInflate(context, view, viewGroup);
            this.mItems = convertOrInflate;
            convertOrInflate.setTagSuffix("Wifi");
            this.mItems.setCallback(this);
            WifiTile.this.mWifiController.scanForAccessPoints();
            setItemsVisible(((QSTile.SignalState) WifiTile.this.mState).value);
            return this.mItems;
        }

        public void onAccessPointsChanged(List<WifiEntry> list) {
            this.mAccessPoints = (WifiEntry[]) list.toArray(new WifiEntry[list.size()]);
            if (!MotoFeature.getInstance(WifiTile.this.mContext).isSupportCli() || !MotoFeature.isLidClosed(WifiTile.this.mContext)) {
                filterUnreachableAPs();
            } else {
                filterSavedAPs();
            }
            updateItems();
        }

        private void filterUnreachableAPs() {
            int i = 0;
            for (WifiEntry access$2000 : this.mAccessPoints) {
                if (WifiTile.isWifiEntryReachable(access$2000)) {
                    i++;
                }
            }
            WifiEntry[] wifiEntryArr = this.mAccessPoints;
            if (i != wifiEntryArr.length) {
                this.mAccessPoints = new WifiEntry[i];
                int i2 = 0;
                for (WifiEntry wifiEntry : wifiEntryArr) {
                    if (WifiTile.isWifiEntryReachable(wifiEntry)) {
                        this.mAccessPoints[i2] = wifiEntry;
                        i2++;
                    }
                }
            }
        }

        private void filterSavedAPs() {
            int i = 0;
            for (WifiEntry isSaved : this.mAccessPoints) {
                if (isSaved.isSaved()) {
                    i++;
                }
            }
            WifiEntry[] wifiEntryArr = this.mAccessPoints;
            if (i != wifiEntryArr.length) {
                this.mAccessPoints = new WifiEntry[i];
                int i2 = 0;
                for (WifiEntry wifiEntry : wifiEntryArr) {
                    if (wifiEntry.isSaved()) {
                        this.mAccessPoints[i2] = wifiEntry;
                        i2++;
                    }
                }
            }
            if (QSTileImpl.DEBUG) {
                Log.d(WifiTile.this.TAG, "filterSavedAPs mAccessPoints.length = " + this.mAccessPoints.length);
            }
        }

        public void onSettingsActivityTriggered(Intent intent) {
            WifiTile.this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }

        public void onDetailItemClick(QSDetailItems.Item item) {
            Object obj;
            if (item != null && (obj = item.tag) != null) {
                WifiEntry wifiEntry = (WifiEntry) obj;
                if (wifiEntry.getConnectedState() == 0 && WifiTile.this.mWifiController.connect(wifiEntry)) {
                    WifiTile.this.mHost.collapsePanels();
                }
                WifiTile.this.showDetail(false);
            }
        }

        public void setItemsVisible(boolean z) {
            QSDetailItems qSDetailItems = this.mItems;
            if (qSDetailItems != null) {
                qSDetailItems.setItemsVisible(z);
            }
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x002f  */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x003c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateItems() {
            /*
                r5 = this;
                com.android.systemui.qs.QSDetailItems r0 = r5.mItems
                if (r0 != 0) goto L_0x0005
                return
            L_0x0005:
                com.android.wifitrackerlib.WifiEntry[] r0 = r5.mAccessPoints
                r1 = 0
                if (r0 == 0) goto L_0x000d
                int r0 = r0.length
                if (r0 > 0) goto L_0x0017
            L_0x000d:
                com.android.systemui.qs.tiles.WifiTile r0 = com.android.systemui.p006qs.tiles.WifiTile.this
                com.android.systemui.qs.tiles.WifiTile$WifiSignalCallback r2 = r0.mSignalCallback
                com.android.systemui.qs.tiles.WifiTile$CallbackInfo r2 = r2.mInfo
                boolean r2 = r2.enabled
                if (r2 != 0) goto L_0x001d
            L_0x0017:
                com.android.systemui.qs.tiles.WifiTile r0 = com.android.systemui.p006qs.tiles.WifiTile.this
                r0.fireScanStateChanged(r1)
                goto L_0x0021
            L_0x001d:
                r2 = 1
                r0.fireScanStateChanged(r2)
            L_0x0021:
                com.android.systemui.qs.tiles.WifiTile r0 = com.android.systemui.p006qs.tiles.WifiTile.this
                com.android.systemui.qs.tiles.WifiTile$WifiSignalCallback r0 = r0.mSignalCallback
                com.android.systemui.qs.tiles.WifiTile$CallbackInfo r0 = r0.mInfo
                boolean r0 = r0.enabled
                r2 = 0
                r3 = 17303763(0x10808d3, float:2.4985586E-38)
                if (r0 != 0) goto L_0x003c
                com.android.systemui.qs.QSDetailItems r0 = r5.mItems
                int r1 = com.android.systemui.R$string.wifi_is_off
                r0.setEmptyState(r3, r1)
                com.android.systemui.qs.QSDetailItems r5 = r5.mItems
                r5.setItems(r2)
                return
            L_0x003c:
                com.android.systemui.qs.QSDetailItems r0 = r5.mItems
                int r4 = com.android.systemui.R$string.quick_settings_wifi_detail_empty_text
                r0.setEmptyState(r3, r4)
                com.android.wifitrackerlib.WifiEntry[] r0 = r5.mAccessPoints
                if (r0 == 0) goto L_0x0081
                int r0 = r0.length
                com.android.systemui.qs.QSDetailItems$Item[] r2 = new com.android.systemui.p006qs.QSDetailItems.Item[r0]
            L_0x004a:
                com.android.wifitrackerlib.WifiEntry[] r0 = r5.mAccessPoints
                int r3 = r0.length
                if (r1 >= r3) goto L_0x0081
                r0 = r0[r1]
                com.android.systemui.qs.QSDetailItems$Item r3 = new com.android.systemui.qs.QSDetailItems$Item
                r3.<init>()
                r3.tag = r0
                com.android.systemui.qs.tiles.WifiTile r4 = com.android.systemui.p006qs.tiles.WifiTile.this
                com.android.systemui.statusbar.policy.NetworkController$AccessPointController r4 = r4.mWifiController
                int r4 = r4.getIcon(r0)
                r3.iconResId = r4
                java.lang.String r4 = r0.getSsid()
                r3.line1 = r4
                java.lang.String r4 = r0.getSummary()
                r3.line2 = r4
                int r0 = r0.getSecurity()
                if (r0 == 0) goto L_0x0079
                int r0 = com.android.systemui.R$drawable.qs_ic_wifi_lock
                goto L_0x007a
            L_0x0079:
                r0 = -1
            L_0x007a:
                r3.icon2 = r0
                r2[r1] = r3
                int r1 = r1 + 1
                goto L_0x004a
            L_0x0081:
                com.android.systemui.qs.QSDetailItems r5 = r5.mItems
                r5.setItems(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p006qs.tiles.WifiTile.WifiDetailAdapter.updateItems():void");
        }
    }

    /* access modifiers changed from: private */
    public static boolean isWifiEntryReachable(WifiEntry wifiEntry) {
        return wifiEntry.getLevel() != -1;
    }
}
