package com.android.systemui.p006qs.tiles;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.ManagedProfileController;

/* renamed from: com.android.systemui.qs.tiles.WorkModeTile */
public class WorkModeTile extends QSTileImpl<QSTile.BooleanState> implements ManagedProfileController.Callback {
    private final QSTile.Icon mIcon = QSTileImpl.ResourceIcon.get(R$drawable.stat_sys_managed_profile_status);
    private final ManagedProfileController mProfileController;

    public int getMetricsCategory() {
        return 257;
    }

    public WorkModeTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, ManagedProfileController managedProfileController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mProfileController = managedProfileController;
        managedProfileController.observe(getLifecycle(), this);
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.MANAGED_PROFILE_SETTINGS");
    }

    public void handleClick(View view) {
        this.mProfileController.setWorkModeEnabled(!((QSTile.BooleanState) this.mState).value);
    }

    public boolean isAvailable() {
        return this.mProfileController.hasActiveProfile();
    }

    public void onManagedProfileChanged() {
        refreshState(Boolean.valueOf(this.mProfileController.isWorkModeEnabled()));
    }

    public void onManagedProfileRemoved() {
        this.mHost.removeTile(getTileSpec());
        this.mHost.unmarkTileAsAutoAdded(getTileSpec());
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_work_mode_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        if (!isAvailable()) {
            onManagedProfileRemoved();
        }
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        if (obj instanceof Boolean) {
            booleanState.value = ((Boolean) obj).booleanValue();
        } else {
            booleanState.value = this.mProfileController.isWorkModeEnabled();
        }
        booleanState.icon = this.mIcon;
        int i = 1;
        if (booleanState.value) {
            booleanState.slash.isSlashed = false;
        } else {
            booleanState.slash.isSlashed = true;
        }
        CharSequence tileLabel = getTileLabel();
        booleanState.label = tileLabel;
        booleanState.contentDescription = tileLabel;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        if (booleanState.value) {
            i = 2;
        }
        booleanState.state = i;
    }
}
