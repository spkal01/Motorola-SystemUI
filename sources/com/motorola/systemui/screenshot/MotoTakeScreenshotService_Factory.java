package com.motorola.systemui.screenshot;

import android.os.UserManager;
import com.android.systemui.screenshot.MotoGlobalScreenshot;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class MotoTakeScreenshotService_Factory implements Factory<MotoTakeScreenshotService> {
    private final Provider<MotoGlobalScreenshot> globalScreenshotProvider;
    private final Provider<UserManager> userManagerProvider;

    public MotoTakeScreenshotService_Factory(Provider<MotoGlobalScreenshot> provider, Provider<UserManager> provider2) {
        this.globalScreenshotProvider = provider;
        this.userManagerProvider = provider2;
    }

    public MotoTakeScreenshotService get() {
        return newInstance(this.globalScreenshotProvider.get(), this.userManagerProvider.get());
    }

    public static MotoTakeScreenshotService_Factory create(Provider<MotoGlobalScreenshot> provider, Provider<UserManager> provider2) {
        return new MotoTakeScreenshotService_Factory(provider, provider2);
    }

    public static MotoTakeScreenshotService newInstance(MotoGlobalScreenshot motoGlobalScreenshot, UserManager userManager) {
        return new MotoTakeScreenshotService(motoGlobalScreenshot, userManager);
    }
}
