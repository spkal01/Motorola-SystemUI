package com.android.systemui.power;

public class MotoPowerUtil {
    public static boolean isTurboPowerCharge(int i) {
        return i == 3 || i == 5 || i == 4;
    }
}
