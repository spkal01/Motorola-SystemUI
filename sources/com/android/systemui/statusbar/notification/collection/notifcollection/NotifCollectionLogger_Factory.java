package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.log.LogBuffer;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotifCollectionLogger_Factory implements Factory<NotifCollectionLogger> {
    private final Provider<LogBuffer> bufferProvider;

    public NotifCollectionLogger_Factory(Provider<LogBuffer> provider) {
        this.bufferProvider = provider;
    }

    public NotifCollectionLogger get() {
        return newInstance(this.bufferProvider.get());
    }

    public static NotifCollectionLogger_Factory create(Provider<LogBuffer> provider) {
        return new NotifCollectionLogger_Factory(provider);
    }

    public static NotifCollectionLogger newInstance(LogBuffer logBuffer) {
        return new NotifCollectionLogger(logBuffer);
    }
}
