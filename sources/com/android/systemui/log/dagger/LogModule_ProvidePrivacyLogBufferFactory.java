package com.android.systemui.log.dagger;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogBufferFactory;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class LogModule_ProvidePrivacyLogBufferFactory implements Factory<LogBuffer> {
    private final Provider<LogBufferFactory> factoryProvider;

    public LogModule_ProvidePrivacyLogBufferFactory(Provider<LogBufferFactory> provider) {
        this.factoryProvider = provider;
    }

    public LogBuffer get() {
        return providePrivacyLogBuffer(this.factoryProvider.get());
    }

    public static LogModule_ProvidePrivacyLogBufferFactory create(Provider<LogBufferFactory> provider) {
        return new LogModule_ProvidePrivacyLogBufferFactory(provider);
    }

    public static LogBuffer providePrivacyLogBuffer(LogBufferFactory logBufferFactory) {
        return (LogBuffer) Preconditions.checkNotNullFromProvides(LogModule.providePrivacyLogBuffer(logBufferFactory));
    }
}
