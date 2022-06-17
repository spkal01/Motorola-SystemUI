package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.policy.KeyguardStateController;

public class RDPUnlockController extends KeyguardUpdateMonitorCallback {
    private Context mContext;
    private DozeScrimController mDozeScrimController;
    private final KeyguardStateController mKeyguardStateController;
    private KeyguardViewMediator mKeyguardViewMediator;
    private final NotificationMediaManager mMediaManager = ((NotificationMediaManager) Dependency.get(NotificationMediaManager.class));
    private int mMode;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    /* access modifiers changed from: private */
    public boolean mPendingShowBouncer;
    private final ShadeController mShadeController;
    private StatusBar mStatusBar;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private final WakefulnessLifecycle.Observer mWakefulnessObserver;

    public RDPUnlockController(Context context, StatusBar statusBar, KeyguardViewMediator keyguardViewMediator, DozeScrimController dozeScrimController, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardStateController keyguardStateController, NotificationShadeWindowController notificationShadeWindowController, ShadeController shadeController, StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        C18931 r0 = new WakefulnessLifecycle.Observer() {
            public void onFinishedWakingUp() {
                if (RDPUnlockController.this.mPendingShowBouncer) {
                    RDPUnlockController.this.showBouncer();
                }
            }
        };
        this.mWakefulnessObserver = r0;
        this.mContext = context;
        this.mStatusBar = statusBar;
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mDozeScrimController = dozeScrimController;
        this.mUpdateMonitor = keyguardUpdateMonitor;
        this.mKeyguardStateController = keyguardStateController;
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mShadeController = shadeController;
        keyguardUpdateMonitor.registerCallback(this);
        ((WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class)).addObserver(r0);
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
    }

    public void onExternalUnlockAuthenticated() {
        if (!this.mUpdateMonitor.isGoingToSleep()) {
            Log.i("RDPUnlockController", "RDP: onExternalUnlockAuthenticated");
            if (((StatusBar) Dependency.get(StatusBar.class)).isOccluded()) {
                this.mUpdateMonitor.dismissSecurity();
            } else {
                startWakeAndUnlock(calculateMode());
            }
        }
    }

    private void startWakeAndUnlock(int i) {
        Log.i("RDPUnlockController", "RDP: startWakeAndUnlock(" + i + ")");
        boolean isDeviceInteractive = this.mUpdateMonitor.isDeviceInteractive();
        this.mMode = i;
        switch (i) {
            case 1:
            case 2:
            case 6:
                this.mMediaManager.updateMediaMetaData(false, true);
                if (this.mMode == 6) {
                    this.mUpdateMonitor.awakenFromDream();
                }
                this.mNotificationShadeWindowController.setNotificationShadeFocusable(false);
                this.mKeyguardViewMediator.onWakeAndUnlocking();
                if (this.mStatusBar.getNavigationBarView() != null) {
                    this.mStatusBar.getNavigationBarView().setWakeAndUnlocking(true);
                    return;
                }
                return;
            case 3:
                if (!isDeviceInteractive) {
                    this.mPendingShowBouncer = true;
                    return;
                } else {
                    showBouncer();
                    return;
                }
            case 5:
                if (!isDeviceInteractive) {
                    this.mPendingShowBouncer = true;
                    return;
                }
                this.mPendingShowBouncer = false;
                this.mStatusBarKeyguardViewManager.notifyKeyguardAuthenticated(false);
                return;
            case 7:
            case 8:
                this.mStatusBarKeyguardViewManager.notifyKeyguardAuthenticated(false);
                return;
            default:
                return;
        }
    }

    private int calculateMode() {
        boolean isDreaming = this.mUpdateMonitor.isDreaming();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                return 4;
            }
            if (this.mDozeScrimController.isPulsing()) {
                return 2;
            }
            return !this.mKeyguardStateController.isMethodSecure() ? 1 : 3;
        } else if (isDreaming) {
            return 6;
        } else {
            if (this.mStatusBarKeyguardViewManager.isShowing()) {
                return this.mStatusBarKeyguardViewManager.bouncerIsOrWillBeShowing() ? 8 : 5;
            }
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public void showBouncer() {
        if (this.mMode == 3) {
            this.mStatusBarKeyguardViewManager.showBouncer(false);
        }
        this.mShadeController.animateCollapsePanels(0, true, false, 2.0f);
        this.mPendingShowBouncer = false;
    }
}
