package com.android.systemui.statusbar.policy;

import com.android.settingslib.AccessibilityContentDescriptions;
import com.android.settingslib.R$drawable;
import com.android.settingslib.SignalIcon$IconGroup;

public class WifiIcons {
    public static final int[][] QS_WIFI_6_SIGNAL_STRENGTH;
    public static final int[][] QS_WIFI_SIGNAL_STRENGTH;
    public static final int[][] QS_WIFI_SIGNAL_STRENGTH_H;
    public static final int[][] QS_WIFI_SIGNAL_STRENGTH_R;
    public static final SignalIcon$IconGroup UNMERGED_WIFI;
    public static final SignalIcon$IconGroup UNMERGED_WIFI_6;
    static final int[] WIFI_6_FULL_ICONS;
    private static final int[] WIFI_6_NO_INTERNET_ICONS;
    static final int[][] WIFI_6_SIGNAL_STRENGTH;
    static final int[] WIFI_FULL_ICONS;
    static final int WIFI_LEVEL_COUNT;
    private static final int[] WIFI_NO_INTERNET_ICONS;
    static final int[][] WIFI_SIGNAL_STRENGTH;
    public static final int[][] WIFI_SIGNAL_STRENGTH_H;
    public static final int[][] WIFI_SIGNAL_STRENGTH_R;

    static {
        int[] iArr = {17303763, 17303764, 17303765, 17303766, 17303767};
        WIFI_FULL_ICONS = iArr;
        int[] iArr2 = {R$drawable.ic_no_internet_wifi_signal_0, R$drawable.ic_no_internet_wifi_signal_1, R$drawable.ic_no_internet_wifi_signal_2, R$drawable.ic_no_internet_wifi_signal_3, R$drawable.ic_no_internet_wifi_signal_4};
        WIFI_NO_INTERNET_ICONS = iArr2;
        int[][] iArr3 = {iArr2, iArr};
        QS_WIFI_SIGNAL_STRENGTH = iArr3;
        WIFI_SIGNAL_STRENGTH = iArr3;
        int[] iArr4 = {R$drawable.ic_wifi_6_signal_0, R$drawable.ic_wifi_6_signal_1, R$drawable.ic_wifi_6_signal_2, R$drawable.ic_wifi_6_signal_3, R$drawable.ic_wifi_6_signal_4};
        WIFI_6_FULL_ICONS = iArr4;
        int[] iArr5 = {R$drawable.ic_qs_wifi_6_0, R$drawable.ic_qs_wifi_6_1, R$drawable.ic_qs_wifi_6_2, R$drawable.ic_qs_wifi_6_3, R$drawable.ic_qs_wifi_6_4};
        WIFI_6_NO_INTERNET_ICONS = iArr5;
        int[][] iArr6 = {iArr5, iArr4};
        QS_WIFI_6_SIGNAL_STRENGTH = iArr6;
        WIFI_6_SIGNAL_STRENGTH = iArr6;
        WIFI_SIGNAL_STRENGTH_H = new int[][]{iArr, new int[]{R$drawable.ic_wifi_h_000_16dp, R$drawable.ic_wifi_h_025_16dp, R$drawable.ic_wifi_h_050_16dp, R$drawable.ic_wifi_h_075_16dp, R$drawable.ic_wifi_h_100_16dp}};
        int i = R$drawable.ic_qs_wifi_0;
        int i2 = R$drawable.ic_qs_wifi_1;
        int i3 = R$drawable.ic_qs_wifi_2;
        int i4 = R$drawable.ic_qs_wifi_3;
        int i5 = R$drawable.ic_qs_wifi_4;
        QS_WIFI_SIGNAL_STRENGTH_H = new int[][]{new int[]{i, i2, i3, i4, i5}, new int[]{R$drawable.ic_wifi_h_000_24dp, R$drawable.ic_wifi_h_025_24dp, R$drawable.ic_wifi_h_050_24dp, R$drawable.ic_wifi_h_075_24dp, R$drawable.ic_wifi_h_100_24dp}};
        WIFI_SIGNAL_STRENGTH_R = new int[][]{iArr, new int[]{R$drawable.ic_wifi_r_000_16dp, R$drawable.ic_wifi_r_025_16dp, R$drawable.ic_wifi_r_050_16dp, R$drawable.ic_wifi_r_075_16dp, R$drawable.ic_wifi_r_100_16dp}};
        QS_WIFI_SIGNAL_STRENGTH_R = new int[][]{new int[]{i, i2, i3, i4, i5}, new int[]{R$drawable.ic_wifi_r_000_24dp, R$drawable.ic_wifi_r_025_24dp, R$drawable.ic_wifi_r_050_24dp, R$drawable.ic_wifi_r_075_24dp, R$drawable.ic_wifi_r_100_24dp}};
        WIFI_LEVEL_COUNT = iArr3[0].length;
        int[] iArr7 = AccessibilityContentDescriptions.WIFI_CONNECTION_STRENGTH;
        int i6 = AccessibilityContentDescriptions.WIFI_NO_CONNECTION;
        int[][] iArr8 = iArr6;
        UNMERGED_WIFI = new SignalIcon$IconGroup("Wi-Fi Icons", iArr3, iArr3, iArr7, 17303763, 17303763, 17303763, 17303763, i6);
        UNMERGED_WIFI_6 = new SignalIcon$IconGroup("Wi-Fi 6 Icons", iArr8, iArr8, iArr7, 17303763, 17303763, 17303763, 17303763, i6);
    }
}
