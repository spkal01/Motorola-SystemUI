package com.android.systemui.p006qs.tiles;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$drawable;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.ReduceBrightColorsController;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;

/* renamed from: com.android.systemui.qs.tiles.ReduceBrightColorsTile */
public class ReduceBrightColorsTile extends QSTileImpl<QSTile.BooleanState> implements ReduceBrightColorsController.Listener {
    private final QSTile.Icon mIcon = QSTileImpl.ResourceIcon.get(R$drawable.ic_reduce_bright_colors);
    private final boolean mIsAvailable;
    private final ReduceBrightColorsController mReduceBrightColorsController;

    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ReduceBrightColorsTile(boolean z, ReduceBrightColorsController reduceBrightColorsController, QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mReduceBrightColorsController = reduceBrightColorsController;
        reduceBrightColorsController.observe(getLifecycle(), this);
        this.mIsAvailable = z;
    }

    public boolean isAvailable() {
        return this.mIsAvailable;
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.REDUCE_BRIGHT_COLORS_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        this.mReduceBrightColorsController.setReduceBrightColorsActivated(!((QSTile.BooleanState) this.mState).value);
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(17041306);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        boolean isReduceBrightColorsActivated = this.mReduceBrightColorsController.isReduceBrightColorsActivated();
        booleanState.value = isReduceBrightColorsActivated;
        booleanState.state = isReduceBrightColorsActivated ? 2 : 1;
        booleanState.label = this.mContext.getString(17041306);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
        booleanState.icon = this.mIcon;
    }

    public void onActivated(boolean z) {
        refreshState();
    }
}
