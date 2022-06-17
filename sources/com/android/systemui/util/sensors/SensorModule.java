package com.android.systemui.util.sensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import com.android.systemui.R$dimen;
import com.android.systemui.R$string;
import com.android.systemui.util.sensors.ThresholdSensorImpl;

public class SensorModule {
    static ThresholdSensor providePrimaryProxSensor(SensorManager sensorManager, ThresholdSensorImpl.Builder builder) {
        try {
            return builder.setSensorDelay(3).setSensorResourceId(R$string.proximity_sensor_type, true).setThresholdResourceId(R$dimen.proximity_sensor_threshold).setThresholdLatchResourceId(R$dimen.proximity_sensor_threshold_latch).build();
        } catch (IllegalStateException unused) {
            Sensor defaultSensor = sensorManager.getDefaultSensor(8, true);
            return builder.setSensor(defaultSensor).setThresholdValue(defaultSensor != null ? defaultSensor.getMaximumRange() : 0.0f).build();
        }
    }

    static ThresholdSensor provideSecondaryProxSensor(ThresholdSensorImpl.Builder builder) {
        try {
            return builder.setSensorResourceId(R$string.proximity_sensor_secondary_type, true).setThresholdResourceId(R$dimen.proximity_sensor_secondary_threshold).setThresholdLatchResourceId(R$dimen.proximity_sensor_secondary_threshold_latch).build();
        } catch (IllegalStateException unused) {
            return builder.setSensor((Sensor) null).setThresholdValue(0.0f).build();
        }
    }
}
