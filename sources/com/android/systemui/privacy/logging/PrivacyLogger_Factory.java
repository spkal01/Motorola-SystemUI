package com.android.systemui.privacy.logging;

import com.android.systemui.log.LogBuffer;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PrivacyLogger_Factory implements Factory<PrivacyLogger> {
    private final Provider<LogBuffer> bufferProvider;

    public PrivacyLogger_Factory(Provider<LogBuffer> provider) {
        this.bufferProvider = provider;
    }

    public PrivacyLogger get() {
        return newInstance(this.bufferProvider.get());
    }

    public static PrivacyLogger_Factory create(Provider<LogBuffer> provider) {
        return new PrivacyLogger_Factory(provider);
    }

    public static PrivacyLogger newInstance(LogBuffer logBuffer) {
        return new PrivacyLogger(logBuffer);
    }
}
