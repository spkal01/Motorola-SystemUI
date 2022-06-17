package com.android.systemui.dagger;

import android.hardware.SensorPrivacyManager;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.dagger.SystemUIDefaultModule_ProvideIndividualSensorPrivacyControllerFactory */
public final class C0914x217e5105 implements Factory<IndividualSensorPrivacyController> {
    private final Provider<SensorPrivacyManager> sensorPrivacyManagerProvider;

    public C0914x217e5105(Provider<SensorPrivacyManager> provider) {
        this.sensorPrivacyManagerProvider = provider;
    }

    public IndividualSensorPrivacyController get() {
        return provideIndividualSensorPrivacyController(this.sensorPrivacyManagerProvider.get());
    }

    public static C0914x217e5105 create(Provider<SensorPrivacyManager> provider) {
        return new C0914x217e5105(provider);
    }

    public static IndividualSensorPrivacyController provideIndividualSensorPrivacyController(SensorPrivacyManager sensorPrivacyManager) {
        return (IndividualSensorPrivacyController) Preconditions.checkNotNullFromProvides(SystemUIDefaultModule.provideIndividualSensorPrivacyController(sensorPrivacyManager));
    }
}
