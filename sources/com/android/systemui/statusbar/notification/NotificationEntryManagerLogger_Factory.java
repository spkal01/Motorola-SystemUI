package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogBuffer;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotificationEntryManagerLogger_Factory implements Factory<NotificationEntryManagerLogger> {
    private final Provider<LogBuffer> bufferProvider;

    public NotificationEntryManagerLogger_Factory(Provider<LogBuffer> provider) {
        this.bufferProvider = provider;
    }

    public NotificationEntryManagerLogger get() {
        return newInstance(this.bufferProvider.get());
    }

    public static NotificationEntryManagerLogger_Factory create(Provider<LogBuffer> provider) {
        return new NotificationEntryManagerLogger_Factory(provider);
    }

    public static NotificationEntryManagerLogger newInstance(LogBuffer logBuffer) {
        return new NotificationEntryManagerLogger(logBuffer);
    }
}
