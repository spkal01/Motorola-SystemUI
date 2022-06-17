package com.android.systemui.wmshell;

import com.android.p011wm.shell.common.ShellExecutor;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class WMShellConcurrencyModule_ProvideSplashScreenExecutorFactory implements Factory<ShellExecutor> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final WMShellConcurrencyModule_ProvideSplashScreenExecutorFactory INSTANCE = new WMShellConcurrencyModule_ProvideSplashScreenExecutorFactory();
    }

    public ShellExecutor get() {
        return provideSplashScreenExecutor();
    }

    public static WMShellConcurrencyModule_ProvideSplashScreenExecutorFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static ShellExecutor provideSplashScreenExecutor() {
        return (ShellExecutor) Preconditions.checkNotNullFromProvides(WMShellConcurrencyModule.provideSplashScreenExecutor());
    }
}
