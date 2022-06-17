package com.android.systemui.screenshot;

import dagger.internal.Factory;

public final class LongScreenshotData_Factory implements Factory<LongScreenshotData> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final LongScreenshotData_Factory INSTANCE = new LongScreenshotData_Factory();
    }

    public LongScreenshotData get() {
        return newInstance();
    }

    public static LongScreenshotData_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static LongScreenshotData newInstance() {
        return new LongScreenshotData();
    }
}
