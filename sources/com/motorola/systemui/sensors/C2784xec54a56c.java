package com.motorola.systemui.sensors;

import android.hardware.SensorEvent;
import com.motorola.systemui.sensors.MotoDozeSensorManager;

/* renamed from: com.motorola.systemui.sensors.MotoDozeSensorManager$MotoDozeSensorEventListener$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2784xec54a56c implements Runnable {
    public final /* synthetic */ MotoDozeSensorManager.MotoDozeSensorEventListener f$0;
    public final /* synthetic */ SensorEvent f$1;

    public /* synthetic */ C2784xec54a56c(MotoDozeSensorManager.MotoDozeSensorEventListener motoDozeSensorEventListener, SensorEvent sensorEvent) {
        this.f$0 = motoDozeSensorEventListener;
        this.f$1 = sensorEvent;
    }

    public final void run() {
        this.f$0.lambda$onSensorChanged$0(this.f$1);
    }
}
