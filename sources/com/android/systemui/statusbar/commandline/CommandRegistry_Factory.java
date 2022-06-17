package com.android.systemui.statusbar.commandline;

import android.content.Context;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class CommandRegistry_Factory implements Factory<CommandRegistry> {
    private final Provider<Context> contextProvider;
    private final Provider<Executor> mainExecutorProvider;

    public CommandRegistry_Factory(Provider<Context> provider, Provider<Executor> provider2) {
        this.contextProvider = provider;
        this.mainExecutorProvider = provider2;
    }

    public CommandRegistry get() {
        return newInstance(this.contextProvider.get(), this.mainExecutorProvider.get());
    }

    public static CommandRegistry_Factory create(Provider<Context> provider, Provider<Executor> provider2) {
        return new CommandRegistry_Factory(provider, provider2);
    }

    public static CommandRegistry newInstance(Context context, Executor executor) {
        return new CommandRegistry(context, executor);
    }
}
