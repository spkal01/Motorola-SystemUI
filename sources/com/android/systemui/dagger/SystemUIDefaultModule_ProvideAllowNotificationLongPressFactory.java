package com.android.systemui.dagger;

import dagger.internal.Factory;

public final class SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory implements Factory<Boolean> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory INSTANCE = new SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory();
    }

    public Boolean get() {
        return Boolean.valueOf(provideAllowNotificationLongPress());
    }

    public static SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static boolean provideAllowNotificationLongPress() {
        return SystemUIDefaultModule.provideAllowNotificationLongPress();
    }
}
