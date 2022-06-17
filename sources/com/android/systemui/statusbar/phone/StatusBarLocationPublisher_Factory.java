package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;

public final class StatusBarLocationPublisher_Factory implements Factory<StatusBarLocationPublisher> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final StatusBarLocationPublisher_Factory INSTANCE = new StatusBarLocationPublisher_Factory();
    }

    public StatusBarLocationPublisher get() {
        return newInstance();
    }

    public static StatusBarLocationPublisher_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static StatusBarLocationPublisher newInstance() {
        return new StatusBarLocationPublisher();
    }
}
