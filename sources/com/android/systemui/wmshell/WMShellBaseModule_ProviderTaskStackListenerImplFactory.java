package com.android.systemui.wmshell;

import android.os.Handler;
import com.android.p011wm.shell.common.TaskStackListenerImpl;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class WMShellBaseModule_ProviderTaskStackListenerImplFactory implements Factory<TaskStackListenerImpl> {
    private final Provider<Handler> mainHandlerProvider;

    public WMShellBaseModule_ProviderTaskStackListenerImplFactory(Provider<Handler> provider) {
        this.mainHandlerProvider = provider;
    }

    public TaskStackListenerImpl get() {
        return providerTaskStackListenerImpl(this.mainHandlerProvider.get());
    }

    public static WMShellBaseModule_ProviderTaskStackListenerImplFactory create(Provider<Handler> provider) {
        return new WMShellBaseModule_ProviderTaskStackListenerImplFactory(provider);
    }

    public static TaskStackListenerImpl providerTaskStackListenerImpl(Handler handler) {
        return (TaskStackListenerImpl) Preconditions.checkNotNullFromProvides(WMShellBaseModule.providerTaskStackListenerImpl(handler));
    }
}
