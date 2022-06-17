package com.android.systemui.moto;

import android.content.Intent;
import android.telephony.TelephonyManager;

public class SimStates {
    private int mPhoneCount = 0;
    private int[] mSimStates;
    private TelephonyManager mTelephonyManager;

    public SimStates(TelephonyManager telephonyManager) {
        if (telephonyManager == null) {
            this.mSimStates = new int[0];
            return;
        }
        int phoneCount = telephonyManager.getPhoneCount();
        this.mPhoneCount = phoneCount;
        this.mSimStates = new int[phoneCount];
        for (int i = 0; i < this.mPhoneCount; i++) {
            this.mSimStates[i] = telephonyManager.getSimState(i);
        }
        this.mTelephonyManager = telephonyManager;
    }

    public int size() {
        return this.mPhoneCount;
    }

    public void updateFromIntent(Intent intent) {
        int intExtra = intent.getIntExtra("phone", -1);
        if (intExtra >= 0 && intExtra < this.mPhoneCount) {
            String stringExtra = intent.getStringExtra("ss");
            if ("ABSENT".equals(stringExtra)) {
                setState(intExtra, 1);
            } else if ("UNKNOWN".equals(stringExtra)) {
                setState(intExtra, 0);
            } else if ("CARD_IO_ERROR".equals(stringExtra)) {
                setState(intExtra, 8);
            } else if ("PERM_DISABLED".equals(stringExtra)) {
                setState(intExtra, 7);
            } else if ("LOCKED".equals(stringExtra)) {
                setState(intExtra, 2);
            } else {
                setState(intExtra, -1);
            }
        }
    }

    public void setState(int i, int i2) {
        if (i >= 0 && i < this.mPhoneCount) {
            this.mSimStates[i] = i2;
        }
    }

    public int getState(int i) {
        if (i < 0 || i >= this.mPhoneCount) {
            return -1;
        }
        return this.mSimStates[i];
    }

    public boolean isSimAbsent(int i) {
        if (getState(i) == 1 || getState(i) == 0) {
            return true;
        }
        return false;
    }

    public boolean isSimError(int i) {
        return getState(i) == 8;
    }

    public boolean isSimPermDisabled(int i) {
        return getState(i) == 7;
    }

    public boolean isSimLocked(int i) {
        return getState(i) == 2;
    }

    public SimStates clone() {
        SimStates simStates = new SimStates(this.mTelephonyManager);
        for (int i = 0; i < this.mPhoneCount; i++) {
            simStates.setState(i, getState(i));
        }
        return simStates;
    }

    public boolean equals(SimStates simStates) {
        if (simStates == null || simStates.size() != this.mPhoneCount) {
            return false;
        }
        for (int i = 0; i < this.mPhoneCount; i++) {
            if (simStates.getState(i) != getState(i)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        String str = "SimStates: ";
        for (int i = 0; i < this.mPhoneCount; i++) {
            if (i > 0) {
                str = str + ", ";
            }
            str = str + "Phone [" + i + "]: " + this.mSimStates[i];
        }
        return str;
    }
}
