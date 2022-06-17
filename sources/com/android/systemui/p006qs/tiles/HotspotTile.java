package com.android.systemui.p006qs.tiles;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import androidx.lifecycle.LifecycleOwner;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$bool;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.motorola.settingslib.hotspothelper.HotspotHelper;

/* renamed from: com.android.systemui.qs.tiles.HotspotTile */
public class HotspotTile extends QSTileImpl<QSTile.BooleanState> {
    private final HotspotAndDataSaverCallbacks mCallbacks;
    private final DataSaverController mDataSaverController;
    private final QSTile.Icon mEnabledStatic = QSTileImpl.ResourceIcon.get(R$drawable.ic_hotspot);
    private final HotspotController mHotspotController;
    private boolean mListening;

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_windowFixedHeightMajor;
    }

    public HotspotTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, HotspotController hotspotController, DataSaverController dataSaverController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        HotspotAndDataSaverCallbacks hotspotAndDataSaverCallbacks = new HotspotAndDataSaverCallbacks();
        this.mCallbacks = hotspotAndDataSaverCallbacks;
        this.mHotspotController = hotspotController;
        this.mDataSaverController = dataSaverController;
        hotspotController.observe((LifecycleOwner) this, hotspotAndDataSaverCallbacks);
        dataSaverController.observe((LifecycleOwner) this, hotspotAndDataSaverCallbacks);
    }

    public boolean isAvailable() {
        return this.mHotspotController.isHotspotSupported();
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        if (this.mListening != z) {
            this.mListening = z;
            if (z) {
                refreshState();
            }
        }
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.TETHER_SETTINGS");
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        boolean z = ((QSTile.BooleanState) this.mState).value;
        if (!z && this.mDataSaverController.isDataSaverEnabled()) {
            return;
        }
        if (!shouldWarnWifiConflict(z)) {
            switchHotspotEnabled();
        } else if (HotspotHelper.getDoNotShowHotspotWarning(this.mContext)) {
            this.mHotspotController.setHotspotEnabled(!z);
        } else {
            createWarningDialog().show();
        }
    }

    private boolean shouldWarnWifiConflict(boolean z) {
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        boolean z2 = !z && wifiManager.isWifiEnabled();
        boolean shouldWarnWhenEnablingTethering = HotspotHelper.shouldWarnWhenEnablingTethering(this.mContext);
        boolean isStaApConcurrencySupported = wifiManager.isStaApConcurrencySupported();
        if (!shouldWarnWhenEnablingTethering || !z2 || isStaApConcurrencySupported) {
            return false;
        }
        return true;
    }

    private SystemUIDialog createWarningDialog() {
        SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext);
        systemUIDialog.setTitle(HotspotHelper.getWarningDialogTitle(this.mContext));
        systemUIDialog.setMessage(HotspotHelper.getTetheringWarningDialogMessage(this.mContext));
        systemUIDialog.setPositiveButton(17039370, new HotspotTile$$ExternalSyntheticLambda0(this, systemUIDialog));
        systemUIDialog.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        systemUIDialog.setCanceledOnTouchOutside(false);
        systemUIDialog.setView(systemUIDialog.getLayoutInflater().inflate(R$layout.do_not_show_again_checkbox, (ViewGroup) null));
        return systemUIDialog;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createWarningDialog$0(SystemUIDialog systemUIDialog, DialogInterface dialogInterface, int i) {
        if (((CheckBox) systemUIDialog.findViewById(R$id.do_not_show_again)).isChecked()) {
            HotspotHelper.setDoNotShowHotspotWarning(this.mContext);
        }
        switchHotspotEnabled();
    }

    private void switchHotspotEnabled() {
        Object obj;
        boolean z = ((QSTile.BooleanState) this.mState).value;
        if (z) {
            obj = null;
        } else {
            obj = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        }
        refreshState(obj);
        this.mHotspotController.setHotspotEnabled(!z);
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_hotspot_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        boolean z;
        int i;
        int i2 = 1;
        boolean z2 = obj == QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        boolean z3 = z2 || this.mHotspotController.isHotspotTransient();
        checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_config_tethering");
        if (obj instanceof CallbackInfo) {
            CallbackInfo callbackInfo = (CallbackInfo) obj;
            booleanState.value = z3 || callbackInfo.isHotspotEnabled;
            i = callbackInfo.numConnectedDevices;
            z = callbackInfo.isDataSaverEnabled;
        } else {
            booleanState.value = z2 || this.mHotspotController.isHotspotEnabled();
            i = this.mHotspotController.getNumConnectedDevices();
            z = this.mDataSaverController.isDataSaverEnabled();
        }
        boolean z4 = this.mContext.getResources().getBoolean(R$bool.config_skip_hotspot_transient_animation);
        booleanState.icon = this.mEnabledStatic;
        booleanState.label = this.mContext.getString(R$string.quick_settings_hotspot_label);
        booleanState.isTransient = z3;
        booleanState.slash.isSlashed = !booleanState.value && !z3;
        if (!z4 && z3) {
            booleanState.icon = QSTileImpl.ResourceIcon.get(17303336);
        }
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
        boolean z5 = booleanState.value || booleanState.isTransient;
        if (z) {
            booleanState.state = 0;
        } else {
            if (z5) {
                i2 = 2;
            }
            booleanState.state = i2;
        }
        String secondaryLabel = getSecondaryLabel(z5, z3, z, i);
        booleanState.secondaryLabel = secondaryLabel;
        booleanState.stateDescription = secondaryLabel;
    }

    private String getSecondaryLabel(boolean z, boolean z2, boolean z3, int i) {
        if (z2) {
            return this.mContext.getString(R$string.quick_settings_hotspot_secondary_label_transient);
        }
        if (z3) {
            return this.mContext.getString(R$string.quick_settings_hotspot_secondary_label_data_saver_enabled);
        }
        if (i <= 0 || !z) {
            return null;
        }
        return this.mContext.getResources().getQuantityString(R$plurals.quick_settings_hotspot_secondary_label_num_devices, i, new Object[]{Integer.valueOf(i)});
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_hotspot_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_hotspot_changed_off);
    }

    /* renamed from: com.android.systemui.qs.tiles.HotspotTile$HotspotAndDataSaverCallbacks */
    private final class HotspotAndDataSaverCallbacks implements HotspotController.Callback, DataSaverController.Listener {
        CallbackInfo mCallbackInfo;

        private HotspotAndDataSaverCallbacks() {
            this.mCallbackInfo = new CallbackInfo();
        }

        public void onDataSaverChanged(boolean z) {
            CallbackInfo callbackInfo = this.mCallbackInfo;
            callbackInfo.isDataSaverEnabled = z;
            HotspotTile.this.refreshState(callbackInfo);
        }

        public void onHotspotChanged(boolean z, int i) {
            CallbackInfo callbackInfo = this.mCallbackInfo;
            callbackInfo.isHotspotEnabled = z;
            callbackInfo.numConnectedDevices = i;
            HotspotTile.this.refreshState(callbackInfo);
        }

        public void onHotspotAvailabilityChanged(boolean z) {
            if (!z) {
                Log.d(HotspotTile.this.TAG, "Tile removed. Hotspot no longer available");
                HotspotTile.this.mHost.removeTile(HotspotTile.this.getTileSpec());
            }
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.HotspotTile$CallbackInfo */
    protected static final class CallbackInfo {
        boolean isDataSaverEnabled;
        boolean isHotspotEnabled;
        int numConnectedDevices;

        protected CallbackInfo() {
        }

        public String toString() {
            return "CallbackInfo[" + "isHotspotEnabled=" + this.isHotspotEnabled + ",numConnectedDevices=" + this.numConnectedDevices + ",isDataSaverEnabled=" + this.isDataSaverEnabled + ']';
        }
    }
}
