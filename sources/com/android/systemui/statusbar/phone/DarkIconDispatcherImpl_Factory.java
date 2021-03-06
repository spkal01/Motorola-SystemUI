package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DarkIconDispatcherImpl_Factory implements Factory<DarkIconDispatcherImpl> {
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;

    public DarkIconDispatcherImpl_Factory(Provider<Context> provider, Provider<CommandQueue> provider2) {
        this.contextProvider = provider;
        this.commandQueueProvider = provider2;
    }

    public DarkIconDispatcherImpl get() {
        return newInstance(this.contextProvider.get(), this.commandQueueProvider.get());
    }

    public static DarkIconDispatcherImpl_Factory create(Provider<Context> provider, Provider<CommandQueue> provider2) {
        return new DarkIconDispatcherImpl_Factory(provider, provider2);
    }

    public static DarkIconDispatcherImpl newInstance(Context context, CommandQueue commandQueue) {
        return new DarkIconDispatcherImpl(context, commandQueue);
    }
}
