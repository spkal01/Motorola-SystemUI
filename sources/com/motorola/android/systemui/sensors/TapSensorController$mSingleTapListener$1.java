package com.motorola.android.systemui.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: TapSensorController.kt */
public final class TapSensorController$mSingleTapListener$1 implements SensorEventListener {
    public void onAccuracyChanged(@NotNull Sensor sensor, int i) {
        Intrinsics.checkNotNullParameter(sensor, "sensor");
    }

    public void onSensorChanged(@NotNull SensorEvent sensorEvent) {
        Intrinsics.checkNotNullParameter(sensorEvent, "event");
    }

    TapSensorController$mSingleTapListener$1() {
    }
}
