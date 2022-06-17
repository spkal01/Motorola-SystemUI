package com.android.systemui.log.dagger;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogBufferFactory;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class LogModule_ProvideDozeLogBufferFactory implements Factory<LogBuffer> {
    private final Provider<LogBufferFactory> factoryProvider;

    public LogModule_ProvideDozeLogBufferFactory(Provider<LogBufferFactory> provider) {
        this.factoryProvider = provider;
    }

    public LogBuffer get() {
        return provideDozeLogBuffer(this.factoryProvider.get());
    }

    public static LogModule_ProvideDozeLogBufferFactory create(Provider<LogBufferFactory> provider) {
        return new LogModule_ProvideDozeLogBufferFactory(provider);
    }

    public static LogBuffer provideDozeLogBuffer(LogBufferFactory logBufferFactory) {
        return (LogBuffer) Preconditions.checkNotNullFromProvides(LogModule.provideDozeLogBuffer(logBufferFactory));
    }
}
