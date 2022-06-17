package com.android.systemui.p008tv;

import android.hardware.SensorPrivacyManager;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.tv.TvSystemUIModule_ProvideSensorPrivacyControllerFactory */
public final class TvSystemUIModule_ProvideSensorPrivacyControllerFactory implements Factory<SensorPrivacyController> {
    private final Provider<SensorPrivacyManager> sensorPrivacyManagerProvider;

    public TvSystemUIModule_ProvideSensorPrivacyControllerFactory(Provider<SensorPrivacyManager> provider) {
        this.sensorPrivacyManagerProvider = provider;
    }

    public SensorPrivacyController get() {
        return provideSensorPrivacyController(this.sensorPrivacyManagerProvider.get());
    }

    public static TvSystemUIModule_ProvideSensorPrivacyControllerFactory create(Provider<SensorPrivacyManager> provider) {
        return new TvSystemUIModule_ProvideSensorPrivacyControllerFactory(provider);
    }

    public static SensorPrivacyController provideSensorPrivacyController(SensorPrivacyManager sensorPrivacyManager) {
        return (SensorPrivacyController) Preconditions.checkNotNullFromProvides(TvSystemUIModule.provideSensorPrivacyController(sensorPrivacyManager));
    }
}
