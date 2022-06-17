package com.android.systemui.dagger;

import android.app.IWallpaperManager;
import dagger.internal.Factory;

public final class FrameworkServicesModule_ProvideIWallPaperManagerFactory implements Factory<IWallpaperManager> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final FrameworkServicesModule_ProvideIWallPaperManagerFactory INSTANCE = new FrameworkServicesModule_ProvideIWallPaperManagerFactory();
    }

    public IWallpaperManager get() {
        return provideIWallPaperManager();
    }

    public static FrameworkServicesModule_ProvideIWallPaperManagerFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static IWallpaperManager provideIWallPaperManager() {
        return FrameworkServicesModule.provideIWallPaperManager();
    }
}
