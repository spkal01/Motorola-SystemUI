package com.android.systemui.util.sensors;

import com.android.systemui.util.sensors.ThresholdSensorImpl;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class SensorModule_ProvideSecondaryProxSensorFactory implements Factory<ThresholdSensor> {
    private final Provider<ThresholdSensorImpl.Builder> thresholdSensorBuilderProvider;

    public SensorModule_ProvideSecondaryProxSensorFactory(Provider<ThresholdSensorImpl.Builder> provider) {
        this.thresholdSensorBuilderProvider = provider;
    }

    public ThresholdSensor get() {
        return provideSecondaryProxSensor(this.thresholdSensorBuilderProvider.get());
    }

    public static SensorModule_ProvideSecondaryProxSensorFactory create(Provider<ThresholdSensorImpl.Builder> provider) {
        return new SensorModule_ProvideSecondaryProxSensorFactory(provider);
    }

    public static ThresholdSensor provideSecondaryProxSensor(Object obj) {
        return (ThresholdSensor) Preconditions.checkNotNullFromProvides(SensorModule.provideSecondaryProxSensor((ThresholdSensorImpl.Builder) obj));
    }
}
