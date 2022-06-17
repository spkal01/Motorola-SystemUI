package com.motorola.rro;

import android.content.Context;
import android.os.Looper;
import com.android.systemui.broadcast.BroadcastDispatcher;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class RROsControllerImpl_Factory implements Factory<RROsControllerImpl> {
    private final Provider<Looper> bgLooperProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Looper> mainLooperProvider;

    public RROsControllerImpl_Factory(Provider<Context> provider, Provider<Looper> provider2, Provider<Looper> provider3, Provider<BroadcastDispatcher> provider4) {
        this.contextProvider = provider;
        this.mainLooperProvider = provider2;
        this.bgLooperProvider = provider3;
        this.broadcastDispatcherProvider = provider4;
    }

    public RROsControllerImpl get() {
        return newInstance(this.contextProvider.get(), this.mainLooperProvider.get(), this.bgLooperProvider.get(), this.broadcastDispatcherProvider.get());
    }

    public static RROsControllerImpl_Factory create(Provider<Context> provider, Provider<Looper> provider2, Provider<Looper> provider3, Provider<BroadcastDispatcher> provider4) {
        return new RROsControllerImpl_Factory(provider, provider2, provider3, provider4);
    }

    public static RROsControllerImpl newInstance(Context context, Looper looper, Looper looper2, BroadcastDispatcher broadcastDispatcher) {
        return new RROsControllerImpl(context, looper, looper2, broadcastDispatcher);
    }
}
