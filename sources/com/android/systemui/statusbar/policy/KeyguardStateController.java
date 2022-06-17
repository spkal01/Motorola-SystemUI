package com.android.systemui.statusbar.policy;

public interface KeyguardStateController extends CallbackController<Callback> {

    public interface Callback {
        void onKeyguardDismissAmountChanged() {
        }

        void onKeyguardFadingAwayChanged() {
        }

        void onKeyguardShowingChanged() {
        }

        void onUnlockedChanged() {
        }
    }

    long calculateGoingToFullShadeDelay();

    boolean canDismissLockScreen();

    boolean canPerformSmartSpaceTransition();

    float getDismissAmount();

    long getKeyguardFadingAwayDelay();

    long getKeyguardFadingAwayDuration();

    boolean isBypassFadingAnimation() {
        return false;
    }

    boolean isDismissingFromSwipe();

    boolean isFaceAuthEnabled() {
        return false;
    }

    boolean isFlingingToDismissKeyguard();

    boolean isFlingingToDismissKeyguardDuringSwipeGesture();

    boolean isKeyguardFadingAway();

    boolean isKeyguardGoingAway();

    boolean isKeyguardScreenRotationAllowed();

    boolean isLaunchTransitionFadingAway();

    boolean isMethodSecure();

    boolean isOccluded();

    boolean isShowing();

    boolean isSnappingKeyguardBackAfterSwipe();

    boolean isTrusted();

    void notifyKeyguardDismissAmountChanged(float f, boolean z) {
    }

    void notifyKeyguardDoneFading() {
    }

    void notifyKeyguardFadingAway(long j, long j2, boolean z) {
    }

    void notifyKeyguardGoingAway(boolean z) {
    }

    void notifyKeyguardState(boolean z, boolean z2) {
    }

    void notifyPanelFlingEnd();

    void notifyPanelFlingStart(boolean z);

    void setLaunchTransitionFadingAway(boolean z) {
    }

    boolean isUnlocked() {
        return !isShowing() || canDismissLockScreen();
    }

    long getShortenedFadingAwayDuration() {
        if (isBypassFadingAnimation()) {
            return getKeyguardFadingAwayDuration();
        }
        return getKeyguardFadingAwayDuration() / 2;
    }
}
