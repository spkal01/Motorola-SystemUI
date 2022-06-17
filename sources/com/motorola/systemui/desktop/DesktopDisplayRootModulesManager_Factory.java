package com.motorola.systemui.desktop;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.statusbar.CommandQueue;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DesktopDisplayRootModulesManager_Factory implements Factory<DesktopDisplayRootModulesManager> {
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;

    public DesktopDisplayRootModulesManager_Factory(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<Handler> provider3) {
        this.contextProvider = provider;
        this.commandQueueProvider = provider2;
        this.bgHandlerProvider = provider3;
    }

    public DesktopDisplayRootModulesManager get() {
        return newInstance(this.contextProvider.get(), this.commandQueueProvider.get(), this.bgHandlerProvider.get());
    }

    public static DesktopDisplayRootModulesManager_Factory create(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<Handler> provider3) {
        return new DesktopDisplayRootModulesManager_Factory(provider, provider2, provider3);
    }

    public static DesktopDisplayRootModulesManager newInstance(Context context, CommandQueue commandQueue, Handler handler) {
        return new DesktopDisplayRootModulesManager(context, commandQueue, handler);
    }
}
