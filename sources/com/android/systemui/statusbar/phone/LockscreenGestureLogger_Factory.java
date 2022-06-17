package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;

public final class LockscreenGestureLogger_Factory implements Factory<LockscreenGestureLogger> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final LockscreenGestureLogger_Factory INSTANCE = new LockscreenGestureLogger_Factory();
    }

    public LockscreenGestureLogger get() {
        return newInstance();
    }

    public static LockscreenGestureLogger_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static LockscreenGestureLogger newInstance() {
        return new LockscreenGestureLogger();
    }
}
