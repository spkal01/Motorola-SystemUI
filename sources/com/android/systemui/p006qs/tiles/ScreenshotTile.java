package com.android.systemui.p006qs.tiles;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.ScreenshotHelper;
import com.android.systemui.Dependency;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.PanelExpansionListener;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.tiles.ScreenshotTile */
public class ScreenshotTile extends QSTileImpl<QSTile.BooleanState> {
    private long mClickTime = 0;
    private boolean mHandlerClick = false;
    private final NotificationPanelViewController mNotificationPanelViewController;
    private final PanelExpansionListener mPanelExpansionListener;
    private final ScreenshotHelper mScreenshotHelper;

    public int getMetricsCategory() {
        return 0;
    }

    public ScreenshotTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mScreenshotHelper = new ScreenshotHelper(qSHost.getContext());
        NotificationPanelViewController panelController = ((StatusBar) Dependency.get(StatusBar.class)).getPanelController();
        this.mNotificationPanelViewController = panelController;
        ScreenshotTile$$ExternalSyntheticLambda0 screenshotTile$$ExternalSyntheticLambda0 = new ScreenshotTile$$ExternalSyntheticLambda0(this);
        this.mPanelExpansionListener = screenshotTile$$ExternalSyntheticLambda0;
        panelController.addExpansionListener(screenshotTile$$ExternalSyntheticLambda0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(float f, boolean z) {
        if (this.mHandlerClick && f == 0.0f && !this.mNotificationPanelViewController.isExpanding()) {
            if (System.currentTimeMillis() - this.mClickTime <= 1500) {
                takeScreenshot();
            } else {
                Log.d(this.TAG, "cancel screenshot");
            }
            this.mHandlerClick = false;
        }
    }

    public QSTile.BooleanState newTileState() {
        Log.d(this.TAG, "init state");
        QSTile.BooleanState booleanState = new QSTile.BooleanState();
        booleanState.value = true;
        booleanState.icon = QSTileImpl.ResourceIcon.get(R$drawable.zz_moto_ic_qs_screenshot);
        booleanState.state = 1;
        booleanState.handlesLongClick = false;
        return booleanState;
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        Log.d(this.TAG, "click screenshot tile");
        if (((KeyguardStateController) Dependency.get(KeyguardStateController.class)).isShowing()) {
            this.mUiHandler.post(new ScreenshotTile$$ExternalSyntheticLambda2(this));
            return;
        }
        this.mHandlerClick = true;
        this.mClickTime = System.currentTimeMillis();
        this.mHost.forceCollapsePanels();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleClick$1() {
        this.mNotificationPanelViewController.animateCloseQs(false, new ScreenshotTile$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public void takeScreenshot() {
        this.mUiHandler.postDelayed(new ScreenshotTile$$ExternalSyntheticLambda1(this), 250);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$takeScreenshot$2() {
        Log.d(this.TAG, "action screenshot");
        if (this.mNotificationPanelViewController.isQsExpanded()) {
            Log.d(this.TAG, "cancel screenshot");
        } else {
            this.mScreenshotHelper.takeScreenshot(1, true, true, 0, this.mUiHandler, (Consumer) null);
        }
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        Log.d(this.TAG, "update state");
        booleanState.label = this.mContext.getString(R$string.global_action_screenshot);
    }

    public Intent getLongClickIntent() {
        return new Intent();
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.global_action_screenshot);
    }

    public void destroy() {
        this.mNotificationPanelViewController.removeExpansionListener(this.mPanelExpansionListener);
        super.destroy();
    }
}
