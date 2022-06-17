package com.android.systemui.p006qs.tiles;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$string;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.SecureSetting;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.settings.SecureSettings;

/* renamed from: com.android.systemui.qs.tiles.BatterySaverTile */
public class BatterySaverTile extends QSTileImpl<QSTile.BooleanState> implements BatteryController.BatteryStateChangeCallback {
    private final BatteryController mBatteryController;
    private boolean mCharging;
    private QSTile.Icon mIcon = QSTileImpl.ResourceIcon.get(17303695);
    private int mLevel;
    private boolean mPluggedIn;
    private boolean mPowerSave;
    @VisibleForTesting
    protected final SecureSetting mSetting;

    public int getMetricsCategory() {
        return 261;
    }

    public BatterySaverTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, BatteryController batteryController, SecureSettings secureSettings) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mBatteryController = batteryController;
        batteryController.observe(getLifecycle(), this);
        SecureSettings secureSettings2 = secureSettings;
        this.mSetting = new SecureSetting(secureSettings2, this.mHandler, "low_power_warning_acknowledged", qSHost.getUserContext().getUserId()) {
            /* access modifiers changed from: protected */
            public void handleValueChanged(int i, boolean z) {
                BatterySaverTile.this.handleRefreshState((Object) null);
            }
        };
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
        this.mSetting.setListening(false);
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitch(int i) {
        this.mSetting.setUserId(i);
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        this.mSetting.setListening(z);
    }

    public Intent getLongClickIntent() {
        return new Intent("android.intent.action.POWER_USAGE_SUMMARY");
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        if (((QSTile.BooleanState) getState()).state != 0) {
            this.mBatteryController.setPowerSaveMode(!this.mPowerSave);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.battery_detail_switch_title);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        int i;
        boolean z = true;
        if (this.mPluggedIn) {
            i = 0;
        } else {
            i = this.mPowerSave ? 2 : 1;
        }
        booleanState.state = i;
        booleanState.icon = this.mIcon;
        String string = this.mContext.getString(R$string.battery_detail_switch_title);
        booleanState.label = string;
        booleanState.secondaryLabel = "";
        booleanState.contentDescription = string;
        booleanState.value = this.mPowerSave;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        if (this.mSetting.getValue() != 0) {
            z = false;
        }
        booleanState.showRippleEffect = z;
    }

    public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        this.mLevel = i;
        this.mPluggedIn = z;
        this.mCharging = z2;
        refreshState(Integer.valueOf(i));
    }

    public void onPowerSaveChanged(boolean z) {
        this.mPowerSave = z;
        refreshState((Object) null);
    }
}
