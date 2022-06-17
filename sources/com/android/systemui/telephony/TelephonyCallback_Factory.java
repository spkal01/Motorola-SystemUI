package com.android.systemui.telephony;

import dagger.internal.Factory;

public final class TelephonyCallback_Factory implements Factory<TelephonyCallback> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final TelephonyCallback_Factory INSTANCE = new TelephonyCallback_Factory();
    }

    public TelephonyCallback get() {
        return newInstance();
    }

    public static TelephonyCallback_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static TelephonyCallback newInstance() {
        return new TelephonyCallback();
    }
}
