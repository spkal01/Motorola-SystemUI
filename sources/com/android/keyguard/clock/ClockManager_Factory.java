package com.android.keyguard.clock;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.InjectionInflationController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ClockManager_Factory implements Factory<ClockManager> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<InjectionInflationController> injectionInflaterProvider;
    private final Provider<PluginManager> pluginManagerProvider;

    public ClockManager_Factory(Provider<Context> provider, Provider<InjectionInflationController> provider2, Provider<PluginManager> provider3, Provider<SysuiColorExtractor> provider4, Provider<DockManager> provider5, Provider<BroadcastDispatcher> provider6) {
        this.contextProvider = provider;
        this.injectionInflaterProvider = provider2;
        this.pluginManagerProvider = provider3;
        this.colorExtractorProvider = provider4;
        this.dockManagerProvider = provider5;
        this.broadcastDispatcherProvider = provider6;
    }

    public ClockManager get() {
        return newInstance(this.contextProvider.get(), this.injectionInflaterProvider.get(), this.pluginManagerProvider.get(), this.colorExtractorProvider.get(), this.dockManagerProvider.get(), this.broadcastDispatcherProvider.get());
    }

    public static ClockManager_Factory create(Provider<Context> provider, Provider<InjectionInflationController> provider2, Provider<PluginManager> provider3, Provider<SysuiColorExtractor> provider4, Provider<DockManager> provider5, Provider<BroadcastDispatcher> provider6) {
        return new ClockManager_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }

    public static ClockManager newInstance(Context context, InjectionInflationController injectionInflationController, PluginManager pluginManager, SysuiColorExtractor sysuiColorExtractor, DockManager dockManager, BroadcastDispatcher broadcastDispatcher) {
        return new ClockManager(context, injectionInflationController, pluginManager, sysuiColorExtractor, dockManager, broadcastDispatcher);
    }
}
