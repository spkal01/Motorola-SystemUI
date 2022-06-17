package com.android.systemui.log.dagger;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogBufferFactory;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class LogModule_ProvideQuickSettingsLogBufferFactory implements Factory<LogBuffer> {
    private final Provider<LogBufferFactory> factoryProvider;

    public LogModule_ProvideQuickSettingsLogBufferFactory(Provider<LogBufferFactory> provider) {
        this.factoryProvider = provider;
    }

    public LogBuffer get() {
        return provideQuickSettingsLogBuffer(this.factoryProvider.get());
    }

    public static LogModule_ProvideQuickSettingsLogBufferFactory create(Provider<LogBufferFactory> provider) {
        return new LogModule_ProvideQuickSettingsLogBufferFactory(provider);
    }

    public static LogBuffer provideQuickSettingsLogBuffer(LogBufferFactory logBufferFactory) {
        return (LogBuffer) Preconditions.checkNotNullFromProvides(LogModule.provideQuickSettingsLogBuffer(logBufferFactory));
    }
}
