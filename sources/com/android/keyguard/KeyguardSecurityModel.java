package com.android.keyguard;

import android.content.res.Resources;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;

public class KeyguardSecurityModel {
    private final boolean mIsPukScreenAvailable;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final LockPatternUtils mLockPatternUtils;

    public enum SecurityMode {
        Invalid,
        None,
        Pattern,
        Password,
        PIN,
        SimPin,
        SimPuk,
        PAKS
    }

    KeyguardSecurityModel(Resources resources, LockPatternUtils lockPatternUtils, KeyguardUpdateMonitor keyguardUpdateMonitor) {
        this.mIsPukScreenAvailable = resources.getBoolean(17891597);
        this.mLockPatternUtils = lockPatternUtils;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
    }

    public SecurityMode getSecurityMode(int i) {
        if (((Boolean) DejankUtils.whitelistIpcs(new KeyguardSecurityModel$$ExternalSyntheticLambda0(this))).booleanValue()) {
            return SecurityMode.PAKS;
        }
        IccCardConstants.State firstUnSkippedLockedSIMState = ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).getFirstUnSkippedLockedSIMState(this.mIsPukScreenAvailable);
        if (firstUnSkippedLockedSIMState == IccCardConstants.State.PIN_REQUIRED) {
            return SecurityMode.SimPin;
        }
        if (firstUnSkippedLockedSIMState == IccCardConstants.State.PUK_REQUIRED) {
            return SecurityMode.SimPuk;
        }
        int intValue = ((Integer) DejankUtils.whitelistIpcs(new KeyguardSecurityModel$$ExternalSyntheticLambda1(this, i))).intValue();
        if (intValue == 0) {
            return SecurityMode.None;
        }
        if (intValue == 65536) {
            return SecurityMode.Pattern;
        }
        if (intValue == 131072 || intValue == 196608) {
            return SecurityMode.PIN;
        }
        if (intValue == 262144 || intValue == 327680 || intValue == 393216 || intValue == 524288) {
            return SecurityMode.Password;
        }
        throw new IllegalStateException("Unknown security quality:" + intValue);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$getSecurityMode$0() {
        return Boolean.valueOf(this.mLockPatternUtils.isPAKSLocked());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Integer lambda$getSecurityMode$1(int i) {
        return Integer.valueOf(this.mLockPatternUtils.getActivePasswordQuality(i));
    }

    public static boolean isSecurityViewOneHanded(SecurityMode securityMode) {
        return securityMode == SecurityMode.Pattern || securityMode == SecurityMode.PIN;
    }
}
