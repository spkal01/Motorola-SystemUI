package com.motorola.systemui.desktop.overwrites.statusbar.phone;

import android.content.Context;
import android.os.Bundle;
import android.os.IRemoteCallback;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardMessageAreaController;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.dock.DockManager;
import com.android.systemui.keyguard.FaceAuthScreenBrightnessController;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.KeyguardBouncer;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.UnlockedScreenOffAnimationController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.Optional;

public class DesktopStatusBarKeyguardViewManager extends StatusBarKeyguardViewManager {
    public void blockPanelExpansionFromCurrentTouch() {
    }

    public boolean bouncerIsOrWillBeShowing() {
        return false;
    }

    public void dismissAndCollapse() {
    }

    public ViewRootImpl getViewRootImpl() {
        return null;
    }

    public void hide(long j, long j2) {
    }

    public boolean isBouncerShowing() {
        return false;
    }

    public boolean isGoingToNotificationShade() {
        return false;
    }

    public boolean isShowing() {
        return false;
    }

    public boolean isUnlockWithWallpaper() {
        return false;
    }

    public void keyguardGoingAway() {
    }

    public void notifyKeyguardAuthenticated(boolean z) {
    }

    public void onCancelClicked() {
    }

    public void onNavigationModeChanged(int i) {
    }

    public void onPanelExpansionChanged(float f, boolean z) {
    }

    public void registerStatusBar(StatusBar statusBar, ViewGroup viewGroup, NotificationPanelViewController notificationPanelViewController, BiometricUnlockController biometricUnlockController, View view, KeyguardBypassController keyguardBypassController) {
    }

    public void requestUnlock(IRemoteCallback iRemoteCallback, boolean z, boolean z2) {
    }

    public void reset(boolean z) {
    }

    public void setKeyguardGoingAwayState(boolean z) {
    }

    public void setNeedsInput(boolean z) {
    }

    public void setOccluded(boolean z, boolean z2) {
    }

    public boolean shouldDisableWindowAnimationsForUnlock() {
        return false;
    }

    public boolean shouldSubtleWindowAnimationsForUnlock() {
        return false;
    }

    public void show(Bundle bundle) {
    }

    public void showBouncer(boolean z) {
    }

    public void startPreHideAnimation(Runnable runnable) {
    }

    public DesktopStatusBarKeyguardViewManager() {
        super((Context) null, (ViewMediatorCallback) null, (LockPatternUtils) null, (SysuiStatusBarStateController) null, (ConfigurationController) null, (KeyguardUpdateMonitor) null, (NavigationModeController) null, (DockManager) null, (NotificationShadeWindowController) null, (KeyguardStateController) null, (Optional<FaceAuthScreenBrightnessController>) null, (NotificationMediaManager) null, (KeyguardBouncer.Factory) null, (WakefulnessLifecycle) null, (UnlockedScreenOffAnimationController) null, (KeyguardMessageAreaController.Factory) null);
    }
}
