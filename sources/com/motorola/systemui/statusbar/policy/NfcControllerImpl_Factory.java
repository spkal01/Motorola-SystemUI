package com.motorola.systemui.statusbar.policy;

import android.content.Context;
import android.os.Looper;
import com.android.systemui.broadcast.BroadcastDispatcher;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NfcControllerImpl_Factory implements Factory<NfcControllerImpl> {
    private final Provider<Looper> bgLooperProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Looper> mainLooperProvider;

    public NfcControllerImpl_Factory(Provider<Context> provider, Provider<Looper> provider2, Provider<Looper> provider3, Provider<BroadcastDispatcher> provider4) {
        this.contextProvider = provider;
        this.mainLooperProvider = provider2;
        this.bgLooperProvider = provider3;
        this.broadcastDispatcherProvider = provider4;
    }

    public NfcControllerImpl get() {
        return newInstance(this.contextProvider.get(), this.mainLooperProvider.get(), this.bgLooperProvider.get(), this.broadcastDispatcherProvider.get());
    }

    public static NfcControllerImpl_Factory create(Provider<Context> provider, Provider<Looper> provider2, Provider<Looper> provider3, Provider<BroadcastDispatcher> provider4) {
        return new NfcControllerImpl_Factory(provider, provider2, provider3, provider4);
    }

    public static NfcControllerImpl newInstance(Context context, Looper looper, Looper looper2, BroadcastDispatcher broadcastDispatcher) {
        return new NfcControllerImpl(context, looper, looper2, broadcastDispatcher);
    }
}
