package com.android.systemui.moto;

import com.android.systemui.R$string;

public class MotoAccessibilityContentDescriptions {
    private static final int[] PHONE_SIGNAL_STRENGTH;
    public static final int[] ZZ_MOTO_PHONE_SIGNAL_STRENGTH;

    static {
        int i = R$string.accessibility_no_phone;
        int i2 = R$string.accessibility_phone_one_bar;
        int i3 = R$string.accessibility_phone_two_bars;
        int i4 = R$string.accessibility_phone_three_bars;
        int i5 = R$string.accessibility_phone_signal_full;
        PHONE_SIGNAL_STRENGTH = new int[]{i, i2, i3, i4, i5};
        ZZ_MOTO_PHONE_SIGNAL_STRENGTH = new int[]{i, i2, i3, i4, R$string.zz_moto_accessibility_phone_four_bars, i5};
    }

    public static int getContentDescription(int i, int i2) {
        int[] iArr = PHONE_SIGNAL_STRENGTH;
        if (i == iArr.length - 1) {
            if (i2 <= i) {
                i = i2;
            }
            return iArr[i];
        }
        int[] iArr2 = ZZ_MOTO_PHONE_SIGNAL_STRENGTH;
        if (i2 >= iArr2.length) {
            i2 = iArr2.length - 1;
        }
        return iArr2[i2];
    }
}
