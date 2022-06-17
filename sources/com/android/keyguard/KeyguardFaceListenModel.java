package com.android.keyguard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: KeyguardListenModel.kt */
public final class KeyguardFaceListenModel extends KeyguardListenModel {
    private final boolean authInterruptActive;
    private final boolean becauseCannotSkipBouncer;
    private final boolean biometricSettingEnabledForUser;
    private final boolean bouncer;
    private final boolean faceAuthenticated;
    private final boolean faceDisabled;
    private final boolean keyguardAwake;
    private final boolean keyguardGoingAway;
    private final boolean listening;
    private final boolean listeningForFaceAssistant;
    private final boolean lockIconPressed;
    private final int modality = 2;
    private final boolean occludingAppRequestingFaceAuth;
    private final boolean primaryUser;
    private final boolean scanningAllowedByStrongAuth;
    private final boolean secureCameraLaunched;
    private final boolean switchingUser;
    private final long timeMillis;
    private final int userId;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof KeyguardFaceListenModel)) {
            return false;
        }
        KeyguardFaceListenModel keyguardFaceListenModel = (KeyguardFaceListenModel) obj;
        return getTimeMillis() == keyguardFaceListenModel.getTimeMillis() && getUserId() == keyguardFaceListenModel.getUserId() && getListening() == keyguardFaceListenModel.getListening() && this.authInterruptActive == keyguardFaceListenModel.authInterruptActive && this.becauseCannotSkipBouncer == keyguardFaceListenModel.becauseCannotSkipBouncer && this.biometricSettingEnabledForUser == keyguardFaceListenModel.biometricSettingEnabledForUser && this.bouncer == keyguardFaceListenModel.bouncer && this.faceAuthenticated == keyguardFaceListenModel.faceAuthenticated && this.faceDisabled == keyguardFaceListenModel.faceDisabled && this.keyguardAwake == keyguardFaceListenModel.keyguardAwake && this.keyguardGoingAway == keyguardFaceListenModel.keyguardGoingAway && this.listeningForFaceAssistant == keyguardFaceListenModel.listeningForFaceAssistant && this.lockIconPressed == keyguardFaceListenModel.lockIconPressed && this.occludingAppRequestingFaceAuth == keyguardFaceListenModel.occludingAppRequestingFaceAuth && this.primaryUser == keyguardFaceListenModel.primaryUser && this.scanningAllowedByStrongAuth == keyguardFaceListenModel.scanningAllowedByStrongAuth && this.secureCameraLaunched == keyguardFaceListenModel.secureCameraLaunched && this.switchingUser == keyguardFaceListenModel.switchingUser;
    }

    public int hashCode() {
        int hashCode = ((Long.hashCode(getTimeMillis()) * 31) + Integer.hashCode(getUserId())) * 31;
        boolean listening2 = getListening();
        boolean z = true;
        if (listening2) {
            listening2 = true;
        }
        int i = (hashCode + (listening2 ? 1 : 0)) * 31;
        boolean z2 = this.authInterruptActive;
        if (z2) {
            z2 = true;
        }
        int i2 = (i + (z2 ? 1 : 0)) * 31;
        boolean z3 = this.becauseCannotSkipBouncer;
        if (z3) {
            z3 = true;
        }
        int i3 = (i2 + (z3 ? 1 : 0)) * 31;
        boolean z4 = this.biometricSettingEnabledForUser;
        if (z4) {
            z4 = true;
        }
        int i4 = (i3 + (z4 ? 1 : 0)) * 31;
        boolean z5 = this.bouncer;
        if (z5) {
            z5 = true;
        }
        int i5 = (i4 + (z5 ? 1 : 0)) * 31;
        boolean z6 = this.faceAuthenticated;
        if (z6) {
            z6 = true;
        }
        int i6 = (i5 + (z6 ? 1 : 0)) * 31;
        boolean z7 = this.faceDisabled;
        if (z7) {
            z7 = true;
        }
        int i7 = (i6 + (z7 ? 1 : 0)) * 31;
        boolean z8 = this.keyguardAwake;
        if (z8) {
            z8 = true;
        }
        int i8 = (i7 + (z8 ? 1 : 0)) * 31;
        boolean z9 = this.keyguardGoingAway;
        if (z9) {
            z9 = true;
        }
        int i9 = (i8 + (z9 ? 1 : 0)) * 31;
        boolean z10 = this.listeningForFaceAssistant;
        if (z10) {
            z10 = true;
        }
        int i10 = (i9 + (z10 ? 1 : 0)) * 31;
        boolean z11 = this.lockIconPressed;
        if (z11) {
            z11 = true;
        }
        int i11 = (i10 + (z11 ? 1 : 0)) * 31;
        boolean z12 = this.occludingAppRequestingFaceAuth;
        if (z12) {
            z12 = true;
        }
        int i12 = (i11 + (z12 ? 1 : 0)) * 31;
        boolean z13 = this.primaryUser;
        if (z13) {
            z13 = true;
        }
        int i13 = (i12 + (z13 ? 1 : 0)) * 31;
        boolean z14 = this.scanningAllowedByStrongAuth;
        if (z14) {
            z14 = true;
        }
        int i14 = (i13 + (z14 ? 1 : 0)) * 31;
        boolean z15 = this.secureCameraLaunched;
        if (z15) {
            z15 = true;
        }
        int i15 = (i14 + (z15 ? 1 : 0)) * 31;
        boolean z16 = this.switchingUser;
        if (!z16) {
            z = z16;
        }
        return i15 + (z ? 1 : 0);
    }

    @NotNull
    public String toString() {
        return "KeyguardFaceListenModel(timeMillis=" + getTimeMillis() + ", userId=" + getUserId() + ", listening=" + getListening() + ", authInterruptActive=" + this.authInterruptActive + ", becauseCannotSkipBouncer=" + this.becauseCannotSkipBouncer + ", biometricSettingEnabledForUser=" + this.biometricSettingEnabledForUser + ", bouncer=" + this.bouncer + ", faceAuthenticated=" + this.faceAuthenticated + ", faceDisabled=" + this.faceDisabled + ", keyguardAwake=" + this.keyguardAwake + ", keyguardGoingAway=" + this.keyguardGoingAway + ", listeningForFaceAssistant=" + this.listeningForFaceAssistant + ", lockIconPressed=" + this.lockIconPressed + ", occludingAppRequestingFaceAuth=" + this.occludingAppRequestingFaceAuth + ", primaryUser=" + this.primaryUser + ", scanningAllowedByStrongAuth=" + this.scanningAllowedByStrongAuth + ", secureCameraLaunched=" + this.secureCameraLaunched + ", switchingUser=" + this.switchingUser + ')';
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
    public KeyguardFaceListenModel(long j, int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11, boolean z12, boolean z13, boolean z14, boolean z15, boolean z16) {
        super((DefaultConstructorMarker) null);
        this.timeMillis = j;
        this.userId = i;
        this.listening = z;
        this.authInterruptActive = z2;
        this.becauseCannotSkipBouncer = z3;
        this.biometricSettingEnabledForUser = z4;
        this.bouncer = z5;
        this.faceAuthenticated = z6;
        this.faceDisabled = z7;
        this.keyguardAwake = z8;
        this.keyguardGoingAway = z9;
        this.listeningForFaceAssistant = z10;
        this.lockIconPressed = z11;
        this.occludingAppRequestingFaceAuth = z12;
        this.primaryUser = z13;
        this.scanningAllowedByStrongAuth = z14;
        this.secureCameraLaunched = z15;
        this.switchingUser = z16;
    }
}
