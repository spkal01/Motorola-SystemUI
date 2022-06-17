package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.hardware.biometrics.BiometricSourceType;
import com.android.systemui.Dumpable;
import com.android.systemui.R$integer;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.tuner.TunerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: KeyguardBypassController.kt */
public class KeyguardBypassController implements Dumpable, StackScrollAlgorithm.BypassController {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private boolean altBouncerShowing;
    private boolean bouncerShowing;
    /* access modifiers changed from: private */
    public boolean bypassEnabled;
    private final int bypassOverride;
    private boolean hasFaceFeature;
    private boolean isPulseExpanding;
    private boolean launchingAffordance;
    @NotNull
    private final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    @Nullable
    public PendingUnlock pendingUnlock;
    private boolean qSExpanded;
    @NotNull
    private final StatusBarStateController statusBarStateController;
    public BiometricUnlockController unlockController;
    private boolean userHasDeviceEntryIntent;

    public final boolean getUserHasDeviceEntryIntent() {
        return this.userHasDeviceEntryIntent;
    }

    public final void setUserHasDeviceEntryIntent(boolean z) {
        this.userHasDeviceEntryIntent = z;
    }

    /* compiled from: KeyguardBypassController.kt */
    private static final class PendingUnlock {
        private final boolean isStrongBiometric;
        @NotNull
        private final BiometricSourceType pendingUnlockType;

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof PendingUnlock)) {
                return false;
            }
            PendingUnlock pendingUnlock = (PendingUnlock) obj;
            return this.pendingUnlockType == pendingUnlock.pendingUnlockType && this.isStrongBiometric == pendingUnlock.isStrongBiometric;
        }

        public int hashCode() {
            int hashCode = this.pendingUnlockType.hashCode() * 31;
            boolean z = this.isStrongBiometric;
            if (z) {
                z = true;
            }
            return hashCode + (z ? 1 : 0);
        }

        @NotNull
        public String toString() {
            return "PendingUnlock(pendingUnlockType=" + this.pendingUnlockType + ", isStrongBiometric=" + this.isStrongBiometric + ')';
        }

        public PendingUnlock(@NotNull BiometricSourceType biometricSourceType, boolean z) {
            Intrinsics.checkNotNullParameter(biometricSourceType, "pendingUnlockType");
            this.pendingUnlockType = biometricSourceType;
            this.isStrongBiometric = z;
        }

        @NotNull
        public final BiometricSourceType getPendingUnlockType() {
            return this.pendingUnlockType;
        }

        public final boolean isStrongBiometric() {
            return this.isStrongBiometric;
        }
    }

    @NotNull
    public final BiometricUnlockController getUnlockController() {
        BiometricUnlockController biometricUnlockController = this.unlockController;
        if (biometricUnlockController != null) {
            return biometricUnlockController;
        }
        Intrinsics.throwUninitializedPropertyAccessException("unlockController");
        throw null;
    }

    public final void setUnlockController(@NotNull BiometricUnlockController biometricUnlockController) {
        Intrinsics.checkNotNullParameter(biometricUnlockController, "<set-?>");
        this.unlockController = biometricUnlockController;
    }

    public final void setPulseExpanding(boolean z) {
        this.isPulseExpanding = z;
    }

    public boolean isBypassEnabled() {
        return getBypassEnabled();
    }

    public final boolean getBypassEnabled() {
        int i = this.bypassOverride;
        if (!(i != 1 ? i != 2 ? this.bypassEnabled : false : true) || !this.mKeyguardStateController.isFaceAuthEnabled()) {
            return false;
        }
        return true;
    }

    public final void setBouncerShowing(boolean z) {
        this.bouncerShowing = z;
    }

    public final boolean getAltBouncerShowing() {
        return this.altBouncerShowing;
    }

    public final void setAltBouncerShowing(boolean z) {
        this.altBouncerShowing = z;
    }

    public final void setLaunchingAffordance(boolean z) {
        this.launchingAffordance = z;
    }

    public final void setQSExpanded(boolean z) {
        boolean z2 = this.qSExpanded != z;
        this.qSExpanded = z;
        if (z2 && !z) {
            maybePerformPendingUnlock();
        }
    }

    public KeyguardBypassController(@NotNull Context context, @NotNull final TunerService tunerService, @NotNull StatusBarStateController statusBarStateController2, @NotNull NotificationLockscreenUserManager notificationLockscreenUserManager, @NotNull KeyguardStateController keyguardStateController, @NotNull DumpManager dumpManager) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(tunerService, "tunerService");
        Intrinsics.checkNotNullParameter(statusBarStateController2, "statusBarStateController");
        Intrinsics.checkNotNullParameter(notificationLockscreenUserManager, "lockscreenUserManager");
        Intrinsics.checkNotNullParameter(keyguardStateController, "keyguardStateController");
        Intrinsics.checkNotNullParameter(dumpManager, "dumpManager");
        this.mKeyguardStateController = keyguardStateController;
        this.statusBarStateController = statusBarStateController2;
        this.bypassOverride = context.getResources().getInteger(R$integer.config_face_unlock_bypass_override);
        boolean hasSystemFeature = context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face");
        this.hasFaceFeature = hasSystemFeature;
        if (hasSystemFeature) {
            dumpManager.registerDumpable("KeyguardBypassController", this);
            statusBarStateController2.addCallback(new StatusBarStateController.StateListener(this) {
                final /* synthetic */ KeyguardBypassController this$0;

                {
                    this.this$0 = r1;
                }

                public void onStateChanged(int i) {
                    if (i != 1) {
                        this.this$0.pendingUnlock = null;
                    }
                }
            });
            final int i = context.getResources().getBoolean(17891603) ? 1 : 0;
            tunerService.addTunable(new TunerService.Tunable(this) {
                final /* synthetic */ KeyguardBypassController this$0;

                {
                    this.this$0 = r1;
                }

                public void onTuningChanged(@Nullable String str, @Nullable String str2) {
                    this.this$0.bypassEnabled = tunerService.getValue(str, i) != 0;
                }
            }, "face_unlock_dismisses_keyguard");
            notificationLockscreenUserManager.addUserChangedListener(new NotificationLockscreenUserManager.UserChangedListener(this) {
                final /* synthetic */ KeyguardBypassController this$0;

                {
                    this.this$0 = r1;
                }

                public void onUserChanged(int i) {
                    this.this$0.pendingUnlock = null;
                }
            });
        }
    }

    public final boolean onBiometricAuthenticated(@NotNull BiometricSourceType biometricSourceType, boolean z) {
        Intrinsics.checkNotNullParameter(biometricSourceType, "biometricSourceType");
        if (biometricSourceType != BiometricSourceType.FACE || !getBypassEnabled()) {
            return true;
        }
        boolean canBypass = canBypass();
        if (!canBypass && (this.isPulseExpanding || this.qSExpanded)) {
            this.pendingUnlock = new PendingUnlock(biometricSourceType, z);
        }
        return canBypass;
    }

    public final void maybePerformPendingUnlock() {
        PendingUnlock pendingUnlock2 = this.pendingUnlock;
        if (pendingUnlock2 != null) {
            Intrinsics.checkNotNull(pendingUnlock2);
            BiometricSourceType pendingUnlockType = pendingUnlock2.getPendingUnlockType();
            PendingUnlock pendingUnlock3 = this.pendingUnlock;
            Intrinsics.checkNotNull(pendingUnlock3);
            if (onBiometricAuthenticated(pendingUnlockType, pendingUnlock3.isStrongBiometric())) {
                BiometricUnlockController unlockController2 = getUnlockController();
                PendingUnlock pendingUnlock4 = this.pendingUnlock;
                Intrinsics.checkNotNull(pendingUnlock4);
                BiometricSourceType pendingUnlockType2 = pendingUnlock4.getPendingUnlockType();
                PendingUnlock pendingUnlock5 = this.pendingUnlock;
                Intrinsics.checkNotNull(pendingUnlock5);
                unlockController2.startWakeAndUnlock(pendingUnlockType2, pendingUnlock5.isStrongBiometric());
                this.pendingUnlock = null;
            }
        }
    }

    public final boolean canBypass() {
        if (!getBypassEnabled()) {
            return false;
        }
        if (!this.bouncerShowing && !this.altBouncerShowing && (this.statusBarStateController.getState() != 1 || this.launchingAffordance || this.isPulseExpanding || this.qSExpanded)) {
            return false;
        }
        return true;
    }

    public final boolean canPlaySubtleWindowAnimations() {
        if (!getBypassEnabled() || this.statusBarStateController.getState() != 1 || this.qSExpanded) {
            return false;
        }
        return true;
    }

    public final void onStartedGoingToSleep() {
        this.pendingUnlock = null;
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println("KeyguardBypassController:");
        PendingUnlock pendingUnlock2 = this.pendingUnlock;
        if (pendingUnlock2 != null) {
            Intrinsics.checkNotNull(pendingUnlock2);
            printWriter.println(Intrinsics.stringPlus("  mPendingUnlock.pendingUnlockType: ", pendingUnlock2.getPendingUnlockType()));
            PendingUnlock pendingUnlock3 = this.pendingUnlock;
            Intrinsics.checkNotNull(pendingUnlock3);
            printWriter.println(Intrinsics.stringPlus("  mPendingUnlock.isStrongBiometric: ", Boolean.valueOf(pendingUnlock3.isStrongBiometric())));
        } else {
            printWriter.println(Intrinsics.stringPlus("  mPendingUnlock: ", pendingUnlock2));
        }
        printWriter.println(Intrinsics.stringPlus("  bypassEnabled: ", Boolean.valueOf(getBypassEnabled())));
        printWriter.println(Intrinsics.stringPlus("  canBypass: ", Boolean.valueOf(canBypass())));
        printWriter.println(Intrinsics.stringPlus("  bouncerShowing: ", Boolean.valueOf(this.bouncerShowing)));
        printWriter.println(Intrinsics.stringPlus("  altBouncerShowing: ", Boolean.valueOf(this.altBouncerShowing)));
        printWriter.println(Intrinsics.stringPlus("  isPulseExpanding: ", Boolean.valueOf(this.isPulseExpanding)));
        printWriter.println(Intrinsics.stringPlus("  launchingAffordance: ", Boolean.valueOf(this.launchingAffordance)));
        printWriter.println(Intrinsics.stringPlus("  qSExpanded: ", Boolean.valueOf(this.qSExpanded)));
        printWriter.println(Intrinsics.stringPlus("  hasFaceFeature: ", Boolean.valueOf(this.hasFaceFeature)));
        printWriter.println(Intrinsics.stringPlus("  userHasDeviceEntryIntent: ", Boolean.valueOf(this.userHasDeviceEntryIntent)));
    }

    /* compiled from: KeyguardBypassController.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
