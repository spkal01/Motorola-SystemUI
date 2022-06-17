package com.android.keyguard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: KeyguardListenModel.kt */
public final class KeyguardFingerprintListenModel extends KeyguardListenModel {
    private final boolean biometricEnabledForUser;
    private final boolean bouncer;
    private final boolean canSkipBouncer;
    private final boolean credentialAttempted;
    private final boolean deviceInteractive;
    private final boolean dreaming;
    private final boolean encryptedOrLockdown;
    private final boolean fingerprintDisabled;
    private final boolean fingerprintLockedOut;
    private final boolean goingToSleep;
    private final boolean keyguardGoingAway;
    private final boolean keyguardIsVisible;
    private final boolean keyguardOccluded;
    private final boolean listening;
    private final int modality = 8;
    private final boolean occludingAppRequestingFp;
    private final boolean primaryUser;
    private final boolean shouldListenForFingerprintAssistant;
    private final boolean switchingUser;
    private final long timeMillis;
    private final boolean udfps;
    private final boolean userDoesNotHaveTrust;
    private final int userId;
    private final boolean userNeedsStrongAuth;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof KeyguardFingerprintListenModel)) {
            return false;
        }
        KeyguardFingerprintListenModel keyguardFingerprintListenModel = (KeyguardFingerprintListenModel) obj;
        return getTimeMillis() == keyguardFingerprintListenModel.getTimeMillis() && getUserId() == keyguardFingerprintListenModel.getUserId() && getListening() == keyguardFingerprintListenModel.getListening() && this.biometricEnabledForUser == keyguardFingerprintListenModel.biometricEnabledForUser && this.bouncer == keyguardFingerprintListenModel.bouncer && this.canSkipBouncer == keyguardFingerprintListenModel.canSkipBouncer && this.credentialAttempted == keyguardFingerprintListenModel.credentialAttempted && this.deviceInteractive == keyguardFingerprintListenModel.deviceInteractive && this.dreaming == keyguardFingerprintListenModel.dreaming && this.encryptedOrLockdown == keyguardFingerprintListenModel.encryptedOrLockdown && this.fingerprintDisabled == keyguardFingerprintListenModel.fingerprintDisabled && this.fingerprintLockedOut == keyguardFingerprintListenModel.fingerprintLockedOut && this.goingToSleep == keyguardFingerprintListenModel.goingToSleep && this.keyguardGoingAway == keyguardFingerprintListenModel.keyguardGoingAway && this.keyguardIsVisible == keyguardFingerprintListenModel.keyguardIsVisible && this.keyguardOccluded == keyguardFingerprintListenModel.keyguardOccluded && this.occludingAppRequestingFp == keyguardFingerprintListenModel.occludingAppRequestingFp && this.primaryUser == keyguardFingerprintListenModel.primaryUser && this.shouldListenForFingerprintAssistant == keyguardFingerprintListenModel.shouldListenForFingerprintAssistant && this.switchingUser == keyguardFingerprintListenModel.switchingUser && this.udfps == keyguardFingerprintListenModel.udfps && this.userDoesNotHaveTrust == keyguardFingerprintListenModel.userDoesNotHaveTrust && this.userNeedsStrongAuth == keyguardFingerprintListenModel.userNeedsStrongAuth;
    }

    public int hashCode() {
        int hashCode = ((Long.hashCode(getTimeMillis()) * 31) + Integer.hashCode(getUserId())) * 31;
        boolean listening2 = getListening();
        boolean z = true;
        if (listening2) {
            listening2 = true;
        }
        int i = (hashCode + (listening2 ? 1 : 0)) * 31;
        boolean z2 = this.biometricEnabledForUser;
        if (z2) {
            z2 = true;
        }
        int i2 = (i + (z2 ? 1 : 0)) * 31;
        boolean z3 = this.bouncer;
        if (z3) {
            z3 = true;
        }
        int i3 = (i2 + (z3 ? 1 : 0)) * 31;
        boolean z4 = this.canSkipBouncer;
        if (z4) {
            z4 = true;
        }
        int i4 = (i3 + (z4 ? 1 : 0)) * 31;
        boolean z5 = this.credentialAttempted;
        if (z5) {
            z5 = true;
        }
        int i5 = (i4 + (z5 ? 1 : 0)) * 31;
        boolean z6 = this.deviceInteractive;
        if (z6) {
            z6 = true;
        }
        int i6 = (i5 + (z6 ? 1 : 0)) * 31;
        boolean z7 = this.dreaming;
        if (z7) {
            z7 = true;
        }
        int i7 = (i6 + (z7 ? 1 : 0)) * 31;
        boolean z8 = this.encryptedOrLockdown;
        if (z8) {
            z8 = true;
        }
        int i8 = (i7 + (z8 ? 1 : 0)) * 31;
        boolean z9 = this.fingerprintDisabled;
        if (z9) {
            z9 = true;
        }
        int i9 = (i8 + (z9 ? 1 : 0)) * 31;
        boolean z10 = this.fingerprintLockedOut;
        if (z10) {
            z10 = true;
        }
        int i10 = (i9 + (z10 ? 1 : 0)) * 31;
        boolean z11 = this.goingToSleep;
        if (z11) {
            z11 = true;
        }
        int i11 = (i10 + (z11 ? 1 : 0)) * 31;
        boolean z12 = this.keyguardGoingAway;
        if (z12) {
            z12 = true;
        }
        int i12 = (i11 + (z12 ? 1 : 0)) * 31;
        boolean z13 = this.keyguardIsVisible;
        if (z13) {
            z13 = true;
        }
        int i13 = (i12 + (z13 ? 1 : 0)) * 31;
        boolean z14 = this.keyguardOccluded;
        if (z14) {
            z14 = true;
        }
        int i14 = (i13 + (z14 ? 1 : 0)) * 31;
        boolean z15 = this.occludingAppRequestingFp;
        if (z15) {
            z15 = true;
        }
        int i15 = (i14 + (z15 ? 1 : 0)) * 31;
        boolean z16 = this.primaryUser;
        if (z16) {
            z16 = true;
        }
        int i16 = (i15 + (z16 ? 1 : 0)) * 31;
        boolean z17 = this.shouldListenForFingerprintAssistant;
        if (z17) {
            z17 = true;
        }
        int i17 = (i16 + (z17 ? 1 : 0)) * 31;
        boolean z18 = this.switchingUser;
        if (z18) {
            z18 = true;
        }
        int i18 = (i17 + (z18 ? 1 : 0)) * 31;
        boolean z19 = this.udfps;
        if (z19) {
            z19 = true;
        }
        int i19 = (i18 + (z19 ? 1 : 0)) * 31;
        boolean z20 = this.userDoesNotHaveTrust;
        if (z20) {
            z20 = true;
        }
        int i20 = (i19 + (z20 ? 1 : 0)) * 31;
        boolean z21 = this.userNeedsStrongAuth;
        if (!z21) {
            z = z21;
        }
        return i20 + (z ? 1 : 0);
    }

    @NotNull
    public String toString() {
        return "KeyguardFingerprintListenModel(timeMillis=" + getTimeMillis() + ", userId=" + getUserId() + ", listening=" + getListening() + ", biometricEnabledForUser=" + this.biometricEnabledForUser + ", bouncer=" + this.bouncer + ", canSkipBouncer=" + this.canSkipBouncer + ", credentialAttempted=" + this.credentialAttempted + ", deviceInteractive=" + this.deviceInteractive + ", dreaming=" + this.dreaming + ", encryptedOrLockdown=" + this.encryptedOrLockdown + ", fingerprintDisabled=" + this.fingerprintDisabled + ", fingerprintLockedOut=" + this.fingerprintLockedOut + ", goingToSleep=" + this.goingToSleep + ", keyguardGoingAway=" + this.keyguardGoingAway + ", keyguardIsVisible=" + this.keyguardIsVisible + ", keyguardOccluded=" + this.keyguardOccluded + ", occludingAppRequestingFp=" + this.occludingAppRequestingFp + ", primaryUser=" + this.primaryUser + ", shouldListenForFingerprintAssistant=" + this.shouldListenForFingerprintAssistant + ", switchingUser=" + this.switchingUser + ", udfps=" + this.udfps + ", userDoesNotHaveTrust=" + this.userDoesNotHaveTrust + ", userNeedsStrongAuth=" + this.userNeedsStrongAuth + ')';
    }

    public long getTimeMillis() {
        return this.timeMillis;
    }

    public int getUserId() {
        return this.userId;
    }

    public boolean getListening() {
        return this.listening;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public KeyguardFingerprintListenModel(long j, int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11, boolean z12, boolean z13, boolean z14, boolean z15, boolean z16, boolean z17, boolean z18, boolean z19, boolean z20, boolean z21) {
        super((DefaultConstructorMarker) null);
        this.timeMillis = j;
        this.userId = i;
        this.listening = z;
        this.biometricEnabledForUser = z2;
        this.bouncer = z3;
        this.canSkipBouncer = z4;
        this.credentialAttempted = z5;
        this.deviceInteractive = z6;
        this.dreaming = z7;
        this.encryptedOrLockdown = z8;
        this.fingerprintDisabled = z9;
        this.fingerprintLockedOut = z10;
        this.goingToSleep = z11;
        this.keyguardGoingAway = z12;
        this.keyguardIsVisible = z13;
        this.keyguardOccluded = z14;
        this.occludingAppRequestingFp = z15;
        this.primaryUser = z16;
        this.shouldListenForFingerprintAssistant = z17;
        this.switchingUser = z18;
        this.udfps = z19;
        this.userDoesNotHaveTrust = z20;
        this.userNeedsStrongAuth = z21;
    }
}
