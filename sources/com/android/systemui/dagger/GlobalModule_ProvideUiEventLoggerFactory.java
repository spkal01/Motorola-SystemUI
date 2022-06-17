package com.android.systemui.dagger;

import com.android.internal.logging.UiEventLogger;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class GlobalModule_ProvideUiEventLoggerFactory implements Factory<UiEventLogger> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final GlobalModule_ProvideUiEventLoggerFactory INSTANCE = new GlobalModule_ProvideUiEventLoggerFactory();
    }

    public UiEventLogger get() {
        return provideUiEventLogger();
    }

    public static GlobalModule_ProvideUiEventLoggerFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static UiEventLogger provideUiEventLogger() {
        return (UiEventLogger) Preconditions.checkNotNullFromProvides(GlobalModule.provideUiEventLogger());
    }
}
