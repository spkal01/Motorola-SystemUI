package com.motorola.taskbar;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.NetworkController;
import com.motorola.systemui.desktop.DesktopDisplayRootModulesManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class MotoTaskBarController_Factory implements Factory<MotoTaskBarController> {
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DesktopDisplayRootModulesManager> desktopDisplayRootModulesManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NetworkController> networkControllerProvider;

    public MotoTaskBarController_Factory(Provider<Context> provider, Provider<Handler> provider2, Provider<Handler> provider3, Provider<CommandQueue> provider4, Provider<DesktopDisplayRootModulesManager> provider5, Provider<NetworkController> provider6) {
        this.contextProvider = provider;
        this.mainHandlerProvider = provider2;
        this.bgHandlerProvider = provider3;
        this.commandQueueProvider = provider4;
        this.desktopDisplayRootModulesManagerProvider = provider5;
        this.networkControllerProvider = provider6;
    }

    public MotoTaskBarController get() {
        return newInstance(this.contextProvider.get(), this.mainHandlerProvider.get(), this.bgHandlerProvider.get(), this.commandQueueProvider.get(), this.desktopDisplayRootModulesManagerProvider.get(), this.networkControllerProvider.get());
    }

    public static MotoTaskBarController_Factory create(Provider<Context> provider, Provider<Handler> provider2, Provider<Handler> provider3, Provider<CommandQueue> provider4, Provider<DesktopDisplayRootModulesManager> provider5, Provider<NetworkController> provider6) {
        return new MotoTaskBarController_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }

    public static MotoTaskBarController newInstance(Context context, Handler handler, Handler handler2, CommandQueue commandQueue, DesktopDisplayRootModulesManager desktopDisplayRootModulesManager, NetworkController networkController) {
        return new MotoTaskBarController(context, handler, handler2, commandQueue, desktopDisplayRootModulesManager, networkController);
    }
}
