package com.android.systemui.screenshot;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class MotoGlobalScreenshot_Factory implements Factory<MotoGlobalScreenshot> {
    private final Provider<Context> contextProvider;
    private final Provider<ImageExporter> imageExporterProvider;
    private final Provider<ScreenshotNotificationsController> screenshotNotificationsControllerProvider;
    private final Provider<ScreenshotSmartActions> screenshotSmartActionsProvider;

    public MotoGlobalScreenshot_Factory(Provider<Context> provider, Provider<ScreenshotNotificationsController> provider2, Provider<ImageExporter> provider3, Provider<ScreenshotSmartActions> provider4) {
        this.contextProvider = provider;
        this.screenshotNotificationsControllerProvider = provider2;
        this.imageExporterProvider = provider3;
        this.screenshotSmartActionsProvider = provider4;
    }

    public MotoGlobalScreenshot get() {
        return newInstance(this.contextProvider.get(), this.screenshotNotificationsControllerProvider.get(), this.imageExporterProvider.get(), this.screenshotSmartActionsProvider.get());
    }

    public static MotoGlobalScreenshot_Factory create(Provider<Context> provider, Provider<ScreenshotNotificationsController> provider2, Provider<ImageExporter> provider3, Provider<ScreenshotSmartActions> provider4) {
        return new MotoGlobalScreenshot_Factory(provider, provider2, provider3, provider4);
    }

    public static MotoGlobalScreenshot newInstance(Context context, ScreenshotNotificationsController screenshotNotificationsController, Object obj, ScreenshotSmartActions screenshotSmartActions) {
        return new MotoGlobalScreenshot(context, screenshotNotificationsController, (ImageExporter) obj, screenshotSmartActions);
    }
}
