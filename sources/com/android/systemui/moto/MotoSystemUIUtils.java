package com.android.systemui.moto;

import android.telephony.ServiceState;

public class MotoSystemUIUtils {
    private static boolean isEvdo(int i) {
        return i == 7 || i == 8 || i == 12 || i == 13;
    }

    public static boolean updateCdmaFemtoIcon(ServiceState serviceState) {
        if (serviceState == null || 3 == serviceState.getDataRoamingType() || 3 == serviceState.getVoiceRoamingType() || isEvdo(serviceState.getRilDataRadioTechnology()) || serviceState.getCdmaNetworkId() < 250 || serviceState.getCdmaNetworkId() >= 255) {
            return false;
        }
        return true;
    }
}
