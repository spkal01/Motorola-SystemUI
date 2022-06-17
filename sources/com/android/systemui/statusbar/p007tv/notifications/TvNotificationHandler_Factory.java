package com.android.systemui.statusbar.p007tv.notifications;

import android.content.Context;
import com.android.systemui.statusbar.NotificationListener;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.tv.notifications.TvNotificationHandler_Factory */
public final class TvNotificationHandler_Factory implements Factory<TvNotificationHandler> {
    private final Provider<Context> contextProvider;
    private final Provider<NotificationListener> notificationListenerProvider;

    public TvNotificationHandler_Factory(Provider<Context> provider, Provider<NotificationListener> provider2) {
        this.contextProvider = provider;
        this.notificationListenerProvider = provider2;
    }

    public TvNotificationHandler get() {
        return newInstance(this.contextProvider.get(), this.notificationListenerProvider.get());
    }

    public static TvNotificationHandler_Factory create(Provider<Context> provider, Provider<NotificationListener> provider2) {
        return new TvNotificationHandler_Factory(provider, provider2);
    }

    public static TvNotificationHandler newInstance(Context context, NotificationListener notificationListener) {
        return new TvNotificationHandler(context, notificationListener);
    }
}
