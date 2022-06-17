package com.android.systemui.doze;

import com.android.systemui.log.LogBuffer;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DozeLogger_Factory implements Factory<DozeLogger> {
    private final Provider<LogBuffer> bufferProvider;

    public DozeLogger_Factory(Provider<LogBuffer> provider) {
        this.bufferProvider = provider;
    }

    public DozeLogger get() {
        return newInstance(this.bufferProvider.get());
    }

    public static DozeLogger_Factory create(Provider<LogBuffer> provider) {
        return new DozeLogger_Factory(provider);
    }

    public static DozeLogger newInstance(LogBuffer logBuffer) {
        return new DozeLogger(logBuffer);
    }
}
