package com.android.systemui.util;

import dagger.internal.Factory;

public final class DeviceConfigProxy_Factory implements Factory<DeviceConfigProxy> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final DeviceConfigProxy_Factory INSTANCE = new DeviceConfigProxy_Factory();
    }

    public DeviceConfigProxy get() {
        return newInstance();
    }

    public static DeviceConfigProxy_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static DeviceConfigProxy newInstance() {
        return new DeviceConfigProxy();
    }
}
