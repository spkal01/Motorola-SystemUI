package com.android.systemui.p006qs.tiles;

import android.os.Handler;
import android.os.Looper;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.settings.SecureSettings;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.BatterySaverTile_Factory */
public final class BatterySaverTile_Factory implements Factory<BatterySaverTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<Looper> backgroundLooperProvider;
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<QSLogger> qsLoggerProvider;
    private final Provider<SecureSettings> secureSettingsProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public BatterySaverTile_Factory(Provider<QSHost> provider, Provider<Looper> provider2, Provider<Handler> provider3, Provider<FalsingManager> provider4, Provider<MetricsLogger> provider5, Provider<StatusBarStateController> provider6, Provider<ActivityStarter> provider7, Provider<QSLogger> provider8, Provider<BatteryController> provider9, Provider<SecureSettings> provider10) {
        this.hostProvider = provider;
        this.backgroundLooperProvider = provider2;
        this.mainHandlerProvider = provider3;
        this.falsingManagerProvider = provider4;
        this.metricsLoggerProvider = provider5;
        this.statusBarStateControllerProvider = provider6;
        this.activityStarterProvider = provider7;
        this.qsLoggerProvider = provider8;
        this.batteryControllerProvider = provider9;
        this.secureSettingsProvider = provider10;
    }

    public BatterySaverTile get() {
        return newInstance(this.hostProvider.get(), this.backgroundLooperProvider.get(), this.mainHandlerProvider.get(), this.falsingManagerProvider.get(), this.metricsLoggerProvider.get(), this.statusBarStateControllerProvider.get(), this.activityStarterProvider.get(), this.qsLoggerProvider.get(), this.batteryControllerProvider.get(), this.secureSettingsProvider.get());
    }

    public static BatterySaverTile_Factory create(Provider<QSHost> provider, Provider<Looper> provider2, Provider<Handler> provider3, Provider<FalsingManager> provider4, Provider<MetricsLogger> provider5, Provider<StatusBarStateController> provider6, Provider<ActivityStarter> provider7, Provider<QSLogger> provider8, Provider<BatteryController> provider9, Provider<SecureSettings> provider10) {
        return new BatterySaverTile_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10);
    }

    public static BatterySaverTile newInstance(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, BatteryController batteryController, SecureSettings secureSettings) {
        return new BatterySaverTile(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger, batteryController, secureSettings);
    }
}
