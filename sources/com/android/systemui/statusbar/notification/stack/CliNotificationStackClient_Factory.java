package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class CliNotificationStackClient_Factory implements Factory<CliNotificationStackClient> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;

    public CliNotificationStackClient_Factory(Provider<Context> provider, Provider<BroadcastDispatcher> provider2) {
        this.contextProvider = provider;
        this.broadcastDispatcherProvider = provider2;
    }

    public CliNotificationStackClient get() {
        return newInstance(this.contextProvider.get(), this.broadcastDispatcherProvider.get());
    }

    public static CliNotificationStackClient_Factory create(Provider<Context> provider, Provider<BroadcastDispatcher> provider2) {
        return new CliNotificationStackClient_Factory(provider, provider2);
    }

    public static CliNotificationStackClient newInstance(Context context, BroadcastDispatcher broadcastDispatcher) {
        return new CliNotificationStackClient(context, broadcastDispatcher);
    }
}
