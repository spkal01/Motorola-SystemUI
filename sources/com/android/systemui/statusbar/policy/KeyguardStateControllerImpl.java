package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Build;
import android.os.SystemProperties;
import android.os.Trace;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dumpable;
import com.android.systemui.R$bool;
import com.android.systemui.shared.system.smartspace.SmartspaceTransitionController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class KeyguardStateControllerImpl implements KeyguardStateController, Dumpable {
    private boolean mBypassFadingAnimation;
    private final ArrayList<KeyguardStateController.Callback> mCallbacks = new ArrayList<>();
    private boolean mCanDismissLockScreen;
    private final Context mContext;
    private boolean mDebugUnlocked;
    private float mDismissAmount;
    private boolean mDismissingFromTouch;
    private boolean mFaceAuthEnabled;
    private boolean mFlingingToDismissKeyguard;
    private boolean mFlingingToDismissKeyguardDuringSwipeGesture;
    private boolean mKeyguardFadingAway;
    private long mKeyguardFadingAwayDelay;
    private long mKeyguardFadingAwayDuration;
    private boolean mKeyguardGoingAway;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private boolean mLaunchTransitionFadingAway;
    private final LockPatternUtils mLockPatternUtils;
    private boolean mOccluded;
    private boolean mSecure;
    private boolean mShowing;
    private final SmartspaceTransitionController mSmartspaceTransitionController;
    private boolean mSnappingKeyguardBackAfterSwipe;
    private boolean mTrustManaged;
    private boolean mTrusted;

    /* renamed from: com.android.systemui.statusbar.policy.KeyguardStateControllerImpl$1 */
    class C20241 extends BroadcastReceiver {
    }

    public KeyguardStateControllerImpl(Context context, KeyguardUpdateMonitor keyguardUpdateMonitor, LockPatternUtils lockPatternUtils, SmartspaceTransitionController smartspaceTransitionController) {
        UpdateMonitorCallback updateMonitorCallback = new UpdateMonitorCallback(this, (C20241) null);
        this.mKeyguardUpdateMonitorCallback = updateMonitorCallback;
        this.mDebugUnlocked = false;
        this.mDismissAmount = 0.0f;
        this.mDismissingFromTouch = false;
        this.mFlingingToDismissKeyguard = false;
        this.mFlingingToDismissKeyguardDuringSwipeGesture = false;
        this.mSnappingKeyguardBackAfterSwipe = false;
        this.mContext = context;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mLockPatternUtils = lockPatternUtils;
        keyguardUpdateMonitor.registerCallback(updateMonitorCallback);
        this.mSmartspaceTransitionController = smartspaceTransitionController;
        update(true);
        boolean z = Build.IS_DEBUGGABLE;
    }

    public void addCallback(KeyguardStateController.Callback callback) {
        Objects.requireNonNull(callback, "Callback must not be null. b/128895449");
        if (!this.mCallbacks.contains(callback)) {
            this.mCallbacks.add(callback);
        }
    }

    public void removeCallback(KeyguardStateController.Callback callback) {
        Objects.requireNonNull(callback, "Callback must not be null. b/128895449");
        this.mCallbacks.remove(callback);
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public boolean isMethodSecure() {
        return this.mSecure;
    }

    public boolean isOccluded() {
        return this.mOccluded;
    }

    public boolean isTrusted() {
        return this.mTrusted;
    }

    public void notifyKeyguardState(boolean z, boolean z2) {
        if (this.mShowing != z || this.mOccluded != z2) {
            this.mShowing = z;
            this.mOccluded = z2;
            notifyKeyguardChanged();
            notifyKeyguardDismissAmountChanged(z ? 0.0f : 1.0f, false);
        }
    }

    /* access modifiers changed from: private */
    public void notifyKeyguardChanged() {
        Trace.beginSection("KeyguardStateController#notifyKeyguardChanged");
        new ArrayList(this.mCallbacks).forEach(KeyguardStateControllerImpl$$ExternalSyntheticLambda0.INSTANCE);
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$notifyKeyguardChanged$0(KeyguardStateController.Callback callback) {
        if (callback != null) {
            callback.onKeyguardShowingChanged();
        }
    }

    private void notifyUnlockedChanged() {
        Trace.beginSection("KeyguardStateController#notifyUnlockedChanged");
        new ArrayList(this.mCallbacks).forEach(KeyguardStateControllerImpl$$ExternalSyntheticLambda3.INSTANCE);
        Trace.endSection();
    }

    public void notifyKeyguardFadingAway(long j, long j2, boolean z) {
        this.mKeyguardFadingAwayDelay = j;
        this.mKeyguardFadingAwayDuration = j2;
        this.mBypassFadingAnimation = z;
        setKeyguardFadingAway(true);
    }

    private void setKeyguardFadingAway(boolean z) {
        if (this.mKeyguardFadingAway != z) {
            this.mKeyguardFadingAway = z;
            ArrayList arrayList = new ArrayList(this.mCallbacks);
            for (int i = 0; i < arrayList.size(); i++) {
                Optional.ofNullable((KeyguardStateController.Callback) arrayList.get(i)).ifPresent(KeyguardStateControllerImpl$$ExternalSyntheticLambda1.INSTANCE);
            }
        }
    }

    public void notifyKeyguardDoneFading() {
        this.mKeyguardGoingAway = false;
        setKeyguardFadingAway(false);
    }

    /* access modifiers changed from: package-private */
    public void update(boolean z) {
        boolean z2;
        Trace.beginSection("KeyguardStateController#update");
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        boolean isSecure = this.mLockPatternUtils.isSecure(currentUser);
        boolean z3 = false;
        if (!isSecure || this.mKeyguardUpdateMonitor.getUserCanSkipBouncer(currentUser)) {
            z2 = true;
        } else {
            boolean z4 = Build.IS_DEBUGGABLE;
            z2 = false;
        }
        boolean userTrustIsManaged = this.mKeyguardUpdateMonitor.getUserTrustIsManaged(currentUser);
        boolean userHasTrust = this.mKeyguardUpdateMonitor.getUserHasTrust(currentUser);
        boolean isFaceAuthEnabledForUser = this.mKeyguardUpdateMonitor.isFaceAuthEnabledForUser(currentUser);
        if (!(isSecure == this.mSecure && z2 == this.mCanDismissLockScreen && userTrustIsManaged == this.mTrustManaged && this.mTrusted == userHasTrust && this.mFaceAuthEnabled == isFaceAuthEnabledForUser)) {
            z3 = true;
        }
        if (z3 || z) {
            this.mSecure = isSecure;
            this.mCanDismissLockScreen = z2;
            this.mTrusted = userHasTrust;
            this.mTrustManaged = userTrustIsManaged;
            this.mFaceAuthEnabled = isFaceAuthEnabledForUser;
            notifyUnlockedChanged();
        }
        Trace.endSection();
    }

    public boolean canDismissLockScreen() {
        return this.mCanDismissLockScreen;
    }

    public boolean canPerformSmartSpaceTransition() {
        return canDismissLockScreen() && this.mSmartspaceTransitionController.isSmartspaceTransitionPossible();
    }

    public boolean isKeyguardScreenRotationAllowed() {
        if (SystemProperties.getBoolean("lockscreen.rot_override", false) || this.mContext.getResources().getBoolean(R$bool.config_enableLockScreenRotation)) {
            return true;
        }
        return false;
    }

    public boolean isFaceAuthEnabled() {
        return this.mFaceAuthEnabled;
    }

    public boolean isKeyguardFadingAway() {
        return this.mKeyguardFadingAway;
    }

    public boolean isKeyguardGoingAway() {
        return this.mKeyguardGoingAway;
    }

    public boolean isBypassFadingAnimation() {
        return this.mBypassFadingAnimation;
    }

    public long getKeyguardFadingAwayDelay() {
        return this.mKeyguardFadingAwayDelay;
    }

    public long getKeyguardFadingAwayDuration() {
        return this.mKeyguardFadingAwayDuration;
    }

    public long calculateGoingToFullShadeDelay() {
        return this.mKeyguardFadingAwayDelay + this.mKeyguardFadingAwayDuration;
    }

    public boolean isFlingingToDismissKeyguard() {
        return this.mFlingingToDismissKeyguard;
    }

    public boolean isFlingingToDismissKeyguardDuringSwipeGesture() {
        return this.mFlingingToDismissKeyguardDuringSwipeGesture;
    }

    public boolean isSnappingKeyguardBackAfterSwipe() {
        return this.mSnappingKeyguardBackAfterSwipe;
    }

    public float getDismissAmount() {
        return this.mDismissAmount;
    }

    public boolean isDismissingFromSwipe() {
        return this.mDismissingFromTouch;
    }

    public void notifyKeyguardGoingAway(boolean z) {
        this.mKeyguardGoingAway = z;
    }

    public void notifyPanelFlingEnd() {
        this.mFlingingToDismissKeyguard = false;
        this.mFlingingToDismissKeyguardDuringSwipeGesture = false;
        this.mSnappingKeyguardBackAfterSwipe = false;
    }

    public void notifyPanelFlingStart(boolean z) {
        this.mFlingingToDismissKeyguard = z;
        this.mFlingingToDismissKeyguardDuringSwipeGesture = z && this.mDismissingFromTouch;
        this.mSnappingKeyguardBackAfterSwipe = !z;
    }

    public void notifyKeyguardDismissAmountChanged(float f, boolean z) {
        this.mDismissAmount = f;
        this.mDismissingFromTouch = z;
        new ArrayList(this.mCallbacks).forEach(KeyguardStateControllerImpl$$ExternalSyntheticLambda2.INSTANCE);
    }

    public void setLaunchTransitionFadingAway(boolean z) {
        this.mLaunchTransitionFadingAway = z;
    }

    public boolean isLaunchTransitionFadingAway() {
        return this.mLaunchTransitionFadingAway;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("KeyguardStateController:");
        printWriter.println("  mSecure: " + this.mSecure);
        printWriter.println("  mCanDismissLockScreen: " + this.mCanDismissLockScreen);
        printWriter.println("  mTrustManaged: " + this.mTrustManaged);
        printWriter.println("  mTrusted: " + this.mTrusted);
        printWriter.println("  mDebugUnlocked: " + this.mDebugUnlocked);
        printWriter.println("  mFaceAuthEnabled: " + this.mFaceAuthEnabled);
    }

    private class UpdateMonitorCallback extends KeyguardUpdateMonitorCallback {
        private UpdateMonitorCallback() {
        }

        /* synthetic */ UpdateMonitorCallback(KeyguardStateControllerImpl keyguardStateControllerImpl, C20241 r2) {
            this();
        }

        public void onUserSwitchComplete(int i) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onTrustChanged(int i) {
            KeyguardStateControllerImpl.this.update(false);
            KeyguardStateControllerImpl.this.notifyKeyguardChanged();
        }

        public void onTrustManagedChanged(int i) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onStartedWakingUp() {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
            Trace.beginSection("KeyguardUpdateMonitorCallback#onBiometricAuthenticated");
            if (KeyguardStateControllerImpl.this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed(z)) {
                KeyguardStateControllerImpl.this.update(false);
            }
            Trace.endSection();
        }

        public void onFaceUnlockStateChanged(boolean z, int i) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onStrongAuthStateChanged(int i) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onKeyguardVisibilityChanged(boolean z) {
            KeyguardStateControllerImpl.this.update(false);
        }

        public void onBiometricsCleared() {
            KeyguardStateControllerImpl.this.update(false);
        }
    }
}
