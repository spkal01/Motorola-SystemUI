package com.android.systemui.util.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.os.Handler;

public final /* synthetic */ class AsyncSensorManager$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ AsyncSensorManager f$0;
    public final /* synthetic */ SensorEventListener f$1;
    public final /* synthetic */ Sensor f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ Handler f$5;

    public /* synthetic */ AsyncSensorManager$$ExternalSyntheticLambda2(AsyncSensorManager asyncSensorManager, SensorEventListener sensorEventListener, Sensor sensor, int i, int i2, Handler handler) {
        this.f$0 = asyncSensorManager;
        this.f$1 = sensorEventListener;
        this.f$2 = sensor;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = handler;
    }

    public final void run() {
        this.f$0.lambda$registerListenerImpl$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
