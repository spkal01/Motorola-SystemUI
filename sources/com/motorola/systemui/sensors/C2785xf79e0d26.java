package com.motorola.systemui.sensors;

import android.hardware.TriggerEvent;
import com.motorola.systemui.sensors.MotoDozeSensorManager;

/* renamed from: com.motorola.systemui.sensors.MotoDozeSensorManager$MotoTriggerEventListener$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2785xf79e0d26 implements Runnable {
    public final /* synthetic */ MotoDozeSensorManager.MotoTriggerEventListener f$0;
    public final /* synthetic */ TriggerEvent f$1;

    public /* synthetic */ C2785xf79e0d26(MotoDozeSensorManager.MotoTriggerEventListener motoTriggerEventListener, TriggerEvent triggerEvent) {
        this.f$0 = motoTriggerEventListener;
        this.f$1 = triggerEvent;
    }

    public final void run() {
        this.f$0.lambda$onTrigger$0(this.f$1);
    }
}
