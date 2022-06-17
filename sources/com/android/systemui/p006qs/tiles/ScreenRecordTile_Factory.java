package com.android.systemui.p006qs.tiles;

import android.os.Handler;
import android.os.Looper;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.settings.UserContextProvider;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.ScreenRecordTile_Factory */
public final class ScreenRecordTile_Factory implements Factory<ScreenRecordTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<Looper> backgroundLooperProvider;
    private final Provider<RecordingController> controllerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<KeyguardDismissUtil> keyguardDismissUtilProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<QSLogger> qsLoggerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<UserContextProvider> userContextTrackerProvider;

    public ScreenRecordTile_Factory(Provider<QSHost> provider, Provider<Looper> provider2, Provider<Handler> provider3, Provider<FalsingManager> provider4, Provider<MetricsLogger> provider5, Provider<StatusBarStateController> provider6, Provider<UserContextProvider> provider7, Provider<ActivityStarter> provider8, Provider<QSLogger> provider9, Provider<RecordingController> provider10, Provider<KeyguardDismissUtil> provider11) {
        this.hostProvider = provider;
        this.backgroundLooperProvider = provider2;
        this.mainHandlerProvider = provider3;
        this.falsingManagerProvider = provider4;
        this.metricsLoggerProvider = provider5;
        this.statusBarStateControllerProvider = provider6;
        this.userContextTrackerProvider = provider7;
        this.activityStarterProvider = provider8;
        this.qsLoggerProvider = provider9;
        this.controllerProvider = provider10;
        this.keyguardDismissUtilProvider = provider11;
    }

    public ScreenRecordTile get() {
        return newInstance(this.hostProvider.get(), this.backgroundLooperProvider.get(), this.mainHandlerProvider.get(), this.falsingManagerProvider.get(), this.metricsLoggerProvider.get(), this.statusBarStateControllerProvider.get(), this.userContextTrackerProvider.get(), this.activityStarterProvider.get(), this.qsLoggerProvider.get(), this.controllerProvider.get(), this.keyguardDismissUtilProvider.get());
    }

    public static ScreenRecordTile_Factory create(Provider<QSHost> provider, Provider<Looper> provider2, Provider<Handler> provider3, Provider<FalsingManager> provider4, Provider<MetricsLogger> provider5, Provider<StatusBarStateController> provider6, Provider<UserContextProvider> provider7, Provider<ActivityStarter> provider8, Provider<QSLogger> provider9, Provider<RecordingController> provider10, Provider<KeyguardDismissUtil> provider11) {
        return new ScreenRecordTile_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
    }

    public static ScreenRecordTile newInstance(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, UserContextProvider userContextProvider, ActivityStarter activityStarter, QSLogger qSLogger, RecordingController recordingController, KeyguardDismissUtil keyguardDismissUtil) {
        return new ScreenRecordTile(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, userContextProvider, activityStarter, qSLogger, recordingController, keyguardDismissUtil);
    }
}
