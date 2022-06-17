package com.android.systemui.util.sensors;

import com.android.systemui.plugins.SensorManagerPlugin;

public final /* synthetic */ class AsyncSensorManager$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ AsyncSensorManager f$0;
    public final /* synthetic */ SensorManagerPlugin.Sensor f$1;
    public final /* synthetic */ SensorManagerPlugin.SensorEventListener f$2;

    public /* synthetic */ AsyncSensorManager$$ExternalSyntheticLambda8(AsyncSensorManager asyncSensorManager, SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener sensorEventListener) {
        this.f$0 = asyncSensorManager;
        this.f$1 = sensor;
        this.f$2 = sensorEventListener;
    }

    public final void run() {
        this.f$0.lambda$unregisterPluginListener$6(this.f$1, this.f$2);
    }
}
