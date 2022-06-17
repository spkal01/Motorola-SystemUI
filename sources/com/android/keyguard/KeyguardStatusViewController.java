package com.android.keyguard;

import android.graphics.Rect;
import android.util.Slog;
import com.android.systemui.keyguard.KeyguardUnlockAnimationController;
import com.android.systemui.shared.system.smartspace.SmartspaceTransitionController;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.UnlockedScreenOffAnimationController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.ViewController;
import java.util.TimeZone;

public class KeyguardStatusViewController extends ViewController<KeyguardStatusView> {
    private static final AnimationProperties CLOCK_ANIMATION_PROPERTIES = new AnimationProperties().setDuration(360);
    /* access modifiers changed from: private */
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    private final Rect mClipBounds = new Rect();
    private final ConfigurationController mConfigurationController;
    private final ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onLocaleListChanged() {
            KeyguardStatusViewController.this.refreshTime();
        }

        public void onDensityOrFontScaleChanged() {
            KeyguardStatusViewController.this.mKeyguardClockSwitchController.onDensityOrFontScaleChanged();
        }
    };
    private final DozeParameters mDozeParameters;
    private KeyguardUpdateMonitorCallback mInfoCallback = new KeyguardUpdateMonitorCallback() {
        public void onLockScreenModeChanged(int i) {
            KeyguardStatusViewController.this.mKeyguardSliceViewController.updateLockScreenMode(i);
        }

        public void onTimeChanged() {
            KeyguardStatusViewController.this.refreshTime();
        }

        public void onTimeFormatChanged(String str) {
            KeyguardStatusViewController.this.mKeyguardClockSwitchController.refreshFormat();
        }

        public void onTimeZoneChanged(TimeZone timeZone) {
            KeyguardStatusViewController.this.mKeyguardClockSwitchController.updateTimeZone(timeZone);
        }

        public void onKeyguardVisibilityChanged(boolean z) {
            if (z) {
                if (KeyguardStatusViewController.DEBUG) {
                    Slog.v("KeyguardStatusViewController", "refresh statusview showing:" + z);
                }
                KeyguardStatusViewController.this.refreshTime();
            }
        }

        public void onUserSwitchComplete(int i) {
            KeyguardStatusViewController.this.mKeyguardClockSwitchController.refreshFormat();
        }
    };
    /* access modifiers changed from: private */
    public final KeyguardClockSwitchController mKeyguardClockSwitchController;
    /* access modifiers changed from: private */
    public final KeyguardSliceViewController mKeyguardSliceViewController;
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    private KeyguardStateController.Callback mKeyguardStateControllerCallback = new KeyguardStateController.Callback() {
        public void onKeyguardShowingChanged() {
            if (KeyguardStatusViewController.this.mKeyguardStateController.isShowing()) {
                ((KeyguardStatusView) KeyguardStatusViewController.this.mView).setChildrenAlphaExcludingClockView(1.0f);
            }
        }
    };
    private final KeyguardUnlockAnimationController mKeyguardUnlockAnimationController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardVisibilityHelper mKeyguardVisibilityHelper;
    private SmartspaceTransitionController mSmartspaceTransitionController;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public KeyguardStatusViewController(KeyguardStatusView keyguardStatusView, KeyguardSliceViewController keyguardSliceViewController, KeyguardClockSwitchController keyguardClockSwitchController, KeyguardStateController keyguardStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, ConfigurationController configurationController, DozeParameters dozeParameters, KeyguardUnlockAnimationController keyguardUnlockAnimationController, SmartspaceTransitionController smartspaceTransitionController, UnlockedScreenOffAnimationController unlockedScreenOffAnimationController) {
        super(keyguardStatusView);
        this.mKeyguardSliceViewController = keyguardSliceViewController;
        this.mKeyguardClockSwitchController = keyguardClockSwitchController;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mConfigurationController = configurationController;
        DozeParameters dozeParameters2 = dozeParameters;
        this.mDozeParameters = dozeParameters2;
        KeyguardStateController keyguardStateController2 = keyguardStateController;
        this.mKeyguardStateController = keyguardStateController2;
        this.mKeyguardVisibilityHelper = new KeyguardVisibilityHelper(this.mView, keyguardStateController2, dozeParameters2, unlockedScreenOffAnimationController, true);
        this.mKeyguardUnlockAnimationController = keyguardUnlockAnimationController;
        this.mSmartspaceTransitionController = smartspaceTransitionController;
    }

    public void onInit() {
        this.mKeyguardClockSwitchController.init();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        this.mKeyguardUpdateMonitor.registerCallback(this.mInfoCallback);
        this.mConfigurationController.addCallback(this.mConfigurationListener);
        this.mKeyguardStateController.addCallback(this.mKeyguardStateControllerCallback);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mKeyguardUpdateMonitor.removeCallback(this.mInfoCallback);
        this.mConfigurationController.removeCallback(this.mConfigurationListener);
        this.mKeyguardStateController.removeCallback(this.mKeyguardStateControllerCallback);
    }

    public void dozeTimeTick() {
        refreshTime();
        this.mKeyguardSliceViewController.refresh();
    }

    public void setDarkAmount(float f) {
        ((KeyguardStatusView) this.mView).setDarkAmount(f);
    }

    public void setHasVisibleNotifications(boolean z) {
        this.mKeyguardClockSwitchController.setHasVisibleNotifications(z);
    }

    public boolean hasCustomClock() {
        return this.mKeyguardClockSwitchController.hasCustomClock();
    }

    public void setAlpha(float f) {
        if (this.mKeyguardVisibilityHelper.isVisibilityAnimating()) {
            return;
        }
        if (this.mKeyguardUnlockAnimationController.isUnlockingWithSmartSpaceTransition()) {
            ((KeyguardStatusView) this.mView).setChildrenAlphaExcludingClockView(f);
            this.mKeyguardClockSwitchController.setChildrenAlphaExcludingSmartspace(f);
        } else if (!this.mKeyguardVisibilityHelper.isVisibilityAnimating()) {
            ((KeyguardStatusView) this.mView).setAlpha(f);
            if (((KeyguardStatusView) this.mView).getChildrenAlphaExcludingSmartSpace() < 1.0f) {
                ((KeyguardStatusView) this.mView).setChildrenAlphaExcludingClockView(1.0f);
                this.mKeyguardClockSwitchController.setChildrenAlphaExcludingSmartspace(1.0f);
            }
        }
    }

    public void setPivotX(float f) {
        ((KeyguardStatusView) this.mView).setPivotX(f);
    }

    public void setPivotY(float f) {
        ((KeyguardStatusView) this.mView).setPivotY(f);
    }

    public float getClockTextSize() {
        return this.mKeyguardClockSwitchController.getClockTextSize();
    }

    public int getLockscreenHeight() {
        return ((KeyguardStatusView) this.mView).getHeight() - this.mKeyguardClockSwitchController.getNotificationIconAreaHeight();
    }

    public void setStatusAccessibilityImportance(int i) {
        ((KeyguardStatusView) this.mView).setImportantForAccessibility(i);
    }

    public void updatePosition(int i, int i2, float f, boolean z) {
        AnimationProperties animationProperties = CLOCK_ANIMATION_PROPERTIES;
        PropertyAnimator.setProperty((KeyguardStatusView) this.mView, AnimatableProperty.f131Y, (float) i2, animationProperties, z);
        this.mKeyguardClockSwitchController.updatePosition(i, f, animationProperties, z);
    }

    public void setKeyguardStatusViewVisibility(int i, boolean z, boolean z2, int i2, boolean z3) {
        this.mKeyguardVisibilityHelper.setViewVisibility(i, z, z2, i2, z3);
    }

    /* access modifiers changed from: private */
    public void refreshTime() {
        this.mKeyguardClockSwitchController.refresh();
    }

    public void setClipBounds(Rect rect) {
        if (rect != null) {
            this.mClipBounds.set(rect.left, (int) (((float) rect.top) - ((KeyguardStatusView) this.mView).getY()), rect.right, (int) (((float) rect.bottom) - ((KeyguardStatusView) this.mView).getY()));
            ((KeyguardStatusView) this.mView).setClipBounds(this.mClipBounds);
            return;
        }
        ((KeyguardStatusView) this.mView).setClipBounds((Rect) null);
    }
}
