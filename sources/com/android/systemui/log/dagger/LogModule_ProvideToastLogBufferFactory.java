package com.android.systemui.log.dagger;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogBufferFactory;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class LogModule_ProvideToastLogBufferFactory implements Factory<LogBuffer> {
    private final Provider<LogBufferFactory> factoryProvider;

    public LogModule_ProvideToastLogBufferFactory(Provider<LogBufferFactory> provider) {
        this.factoryProvider = provider;
    }

    public LogBuffer get() {
        return provideToastLogBuffer(this.factoryProvider.get());
    }

    public static LogModule_ProvideToastLogBufferFactory create(Provider<LogBufferFactory> provider) {
        return new LogModule_ProvideToastLogBufferFactory(provider);
    }

    public static LogBuffer provideToastLogBuffer(LogBufferFactory logBufferFactory) {
        return (LogBuffer) Preconditions.checkNotNullFromProvides(LogModule.provideToastLogBuffer(logBufferFactory));
    }
}
