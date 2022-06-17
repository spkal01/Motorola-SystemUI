package com.motorola.settingslib.moto5gmenu;

import android.content.Context;
import android.telephony.RadioAccessFamily;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Moto5gNetworkUtils {
    private static final String TAG = "Moto5gNetworkUtils";

    public static boolean modemSupports5g() {
        boolean z = true;
        int parseInt = Integer.parseInt(TelephonyManager.getTelephonyProperty(0, "ro.telephony.default_network", Integer.toString(1)));
        long rafFromNetworkType = (long) RadioAccessFamily.getRafFromNetworkType(parseInt);
        if ((524288 & rafFromNetworkType) <= 0) {
            z = false;
        }
        Log.d(TAG, "modemSupports5g: networkMode:" + parseInt + " networkModeRaf: " + rafFromNetworkType + " isNrSupported:" + z);
        return z;
    }

    public static boolean is5g(int i) {
        return getHighestRafCapability(i) == 4;
    }

    public static boolean isSameHighestRat(int i, int i2) {
        return getHighestRafCapability(i) == getHighestRafCapability(i2);
    }

    private static int getHighestRafCapability(int i) {
        int rafFromNetworkType = RadioAccessFamily.getRafFromNetworkType(i);
        if ((524288 & rafFromNetworkType) > 0) {
            return 4;
        }
        if ((266240 & rafFromNetworkType) > 0) {
            return 3;
        }
        if ((93108 & rafFromNetworkType) > 0) {
            return 2;
        }
        return (rafFromNetworkType & 32843) > 0 ? 1 : 0;
    }

    public static boolean carrierAllowed5g(Context context, int i) {
        boolean z = (TelephonyManager.from(context).createForSubscriptionId(i).getAllowedNetworkTypesForReason(2) & 524288) > 0;
        String str = TAG;
        Log.d(str, "carrierAllowed5g: " + z);
        return z;
    }

    public static int getPnt(Context context, int i) {
        return RadioAccessFamily.getNetworkTypeFromRaf((int) TelephonyManager.from(context).createForSubscriptionId(i).getAllowedNetworkTypesForReason(0));
    }
}
