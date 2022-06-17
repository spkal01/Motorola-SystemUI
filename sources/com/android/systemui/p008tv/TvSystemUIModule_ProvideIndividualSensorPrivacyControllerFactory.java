package com.android.systemui.p008tv;

import android.hardware.SensorPrivacyManager;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.tv.TvSystemUIModule_ProvideIndividualSensorPrivacyControllerFactory */
public final class TvSystemUIModule_ProvideIndividualSensorPrivacyControllerFactory implements Factory<IndividualSensorPrivacyController> {
    private final Provider<SensorPrivacyManager> sensorPrivacyManagerProvider;

    public TvSystemUIModule_ProvideIndividualSensorPrivacyControllerFactory(Provider<SensorPrivacyManager> provider) {
        this.sensorPrivacyManagerProvider = provider;
    }

    public IndividualSensorPrivacyController get() {
        return provideIndividualSensorPrivacyController(this.sensorPrivacyManagerProvider.get());
    }

    public static TvSystemUIModule_ProvideIndividualSensorPrivacyControllerFactory create(Provider<SensorPrivacyManager> provider) {
        return new TvSystemUIModule_ProvideIndividualSensorPrivacyControllerFactory(provider);
    }

    public static IndividualSensorPrivacyController provideIndividualSensorPrivacyController(SensorPrivacyManager sensorPrivacyManager) {
        return (IndividualSensorPrivacyController) Preconditions.checkNotNullFromProvides(TvSystemUIModule.provideIndividualSensorPrivacyController(sensorPrivacyManager));
    }
}
