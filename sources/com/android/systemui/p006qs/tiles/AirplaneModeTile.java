package com.android.systemui.p006qs.tiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.Settings;
import android.sysprop.TelephonyProperties;
import android.view.View;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p006qs.GlobalSetting;
import com.android.systemui.p006qs.MotoConfirmationStubActivity;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import dagger.Lazy;

/* renamed from: com.android.systemui.qs.tiles.AirplaneModeTile */
public class AirplaneModeTile extends QSTileImpl<QSTile.BooleanState> {
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final QSTile.Icon mIcon = QSTileImpl.ResourceIcon.get(17303693);
    private final Lazy<ConnectivityManager> mLazyConnectivityManager;
    private boolean mListening;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                AirplaneModeTile.this.refreshState();
            }
        }
    };
    private final GlobalSetting mSetting;

    public int getMetricsCategory() {
        return 112;
    }

    public AirplaneModeTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, BroadcastDispatcher broadcastDispatcher, Lazy<ConnectivityManager> lazy) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mLazyConnectivityManager = lazy;
        this.mSetting = new GlobalSetting(this.mContext, this.mHandler, "airplane_mode_on") {
            /* access modifiers changed from: protected */
            public void handleValueChanged(int i) {
                AirplaneModeTile.this.handleRefreshState(Integer.valueOf(i));
            }
        };
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    public void handleClick(View view) {
        boolean z = ((QSTile.BooleanState) this.mState).value;
        MetricsLogger.action(this.mContext, getMetricsCategory(), !z);
        if (z || !((Boolean) TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)).booleanValue()) {
            setEnabled(!z);
        } else {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS"), 0);
        }
    }

    private void setEnabled(boolean z) {
        if (!z || !isAirplanemodeDialogEnabled()) {
            this.mLazyConnectivityManager.get().setAirplaneMode(z);
        }
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.airplane_mode);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_airplane_mode");
        int i = 1;
        boolean z = (obj instanceof Integer ? ((Integer) obj).intValue() : this.mSetting.getValue()) != 0;
        booleanState.value = z;
        booleanState.label = this.mContext.getString(R$string.airplane_mode);
        booleanState.icon = this.mIcon;
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        booleanState.slash.isSlashed = !z;
        if (z) {
            i = 2;
        }
        booleanState.state = i;
        booleanState.contentDescription = booleanState.label;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_airplane_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_airplane_changed_off);
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        if (this.mListening != z) {
            this.mListening = z;
            if (z) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
                this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter);
            } else {
                this.mBroadcastDispatcher.unregisterReceiver(this.mReceiver);
            }
            this.mSetting.setListening(z);
        }
    }

    private boolean isAirplanemodeDialogEnabled() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "mot_show_airplanemode_dialog", 1);
        Intent intentResolvedToSystemActivity = MotoConfirmationStubActivity.getIntentResolvedToSystemActivity(this.mContext, "com.motorola.settings.action.AIRPLANE_MODE_ON");
        if (i != 1 || intentResolvedToSystemActivity == null || MotoConfirmationStubActivity.isOnCli(this.mContext)) {
            return false;
        }
        intentResolvedToSystemActivity.setFlags(268435456);
        intentResolvedToSystemActivity.putExtra("launched_from_qs", true);
        this.mContext.startActivityAsUser(intentResolvedToSystemActivity, UserHandle.CURRENT_OR_SELF);
        return true;
    }
}
