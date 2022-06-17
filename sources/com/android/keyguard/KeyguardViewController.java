package com.android.keyguard;

import android.os.Bundle;
import android.os.IRemoteCallback;
import android.view.ViewRootImpl;

public interface KeyguardViewController {
    void blockPanelExpansionFromCurrentTouch();

    boolean bouncerIsOrWillBeShowing();

    void dismissAndCollapse();

    ViewRootImpl getViewRootImpl();

    void hide(long j, long j2);

    boolean isBouncerShowing();

    boolean isGoingToNotificationShade();

    boolean isShowing();

    boolean isUnlockWithWallpaper();

    void keyguardGoingAway();

    void notifyKeyguardAuthenticated(boolean z);

    void onCancelClicked();

    void onFinishedGoingToSleep() {
    }

    void onScreenTurnedOn() {
    }

    void onScreenTurningOn() {
    }

    void onStartedGoingToSleep() {
    }

    void onStartedWakingUp() {
    }

    void requestUnlock(IRemoteCallback iRemoteCallback, boolean z, boolean z2);

    void reset(boolean z);

    void setKeyguardGoingAwayState(boolean z);

    void setNeedsInput(boolean z);

    void setOccluded(boolean z, boolean z2);

    boolean shouldDisableWindowAnimationsForUnlock();

    boolean shouldSubtleWindowAnimationsForUnlock();

    void show(Bundle bundle);

    void showBouncer(boolean z);

    void startPreHideAnimation(Runnable runnable);
}
