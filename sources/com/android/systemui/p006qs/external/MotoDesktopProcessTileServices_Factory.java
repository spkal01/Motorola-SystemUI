package com.android.systemui.p006qs.external;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.UserTracker;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.external.MotoDesktopProcessTileServices_Factory */
public final class MotoDesktopProcessTileServices_Factory implements Factory<MotoDesktopProcessTileServices> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<UserTracker> userTrackerProvider;

    public MotoDesktopProcessTileServices_Factory(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<Handler> provider3, Provider<UserTracker> provider4) {
        this.contextProvider = provider;
        this.broadcastDispatcherProvider = provider2;
        this.handlerProvider = provider3;
        this.userTrackerProvider = provider4;
    }

    public MotoDesktopProcessTileServices get() {
        return newInstance(this.contextProvider.get(), this.broadcastDispatcherProvider.get(), this.handlerProvider.get(), this.userTrackerProvider.get());
    }

    public static MotoDesktopProcessTileServices_Factory create(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<Handler> provider3, Provider<UserTracker> provider4) {
        return new MotoDesktopProcessTileServices_Factory(provider, provider2, provider3, provider4);
    }

    public static MotoDesktopProcessTileServices newInstance(Context context, BroadcastDispatcher broadcastDispatcher, Handler handler, UserTracker userTracker) {
        return new MotoDesktopProcessTileServices(context, broadcastDispatcher, handler, userTracker);
    }
}
