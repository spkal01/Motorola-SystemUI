package com.android.keyguard;

import android.content.Context;
import com.android.keyguard.dagger.KeyguardStatusViewComponent;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.statusbar.CommandQueue;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class KeyguardDisplayManager_Factory implements Factory<KeyguardDisplayManager> {
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardStatusViewComponent.Factory> keyguardStatusViewComponentFactoryProvider;
    private final Provider<NavigationBarController> navigationBarControllerLazyProvider;
    private final Provider<Executor> uiBgExecutorProvider;

    public KeyguardDisplayManager_Factory(Provider<Context> provider, Provider<NavigationBarController> provider2, Provider<KeyguardStatusViewComponent.Factory> provider3, Provider<CommandQueue> provider4, Provider<Executor> provider5) {
        this.contextProvider = provider;
        this.navigationBarControllerLazyProvider = provider2;
        this.keyguardStatusViewComponentFactoryProvider = provider3;
        this.commandQueueProvider = provider4;
        this.uiBgExecutorProvider = provider5;
    }

    public KeyguardDisplayManager get() {
        return newInstance(this.contextProvider.get(), DoubleCheck.lazy(this.navigationBarControllerLazyProvider), this.keyguardStatusViewComponentFactoryProvider.get(), this.commandQueueProvider.get(), this.uiBgExecutorProvider.get());
    }

    public static KeyguardDisplayManager_Factory create(Provider<Context> provider, Provider<NavigationBarController> provider2, Provider<KeyguardStatusViewComponent.Factory> provider3, Provider<CommandQueue> provider4, Provider<Executor> provider5) {
        return new KeyguardDisplayManager_Factory(provider, provider2, provider3, provider4, provider5);
    }

    public static KeyguardDisplayManager newInstance(Context context, Lazy<NavigationBarController> lazy, KeyguardStatusViewComponent.Factory factory, CommandQueue commandQueue, Executor executor) {
        return new KeyguardDisplayManager(context, lazy, factory, commandQueue, executor);
    }
}
