package com.android.systemui.util.sensors;

import android.hardware.SensorManager;
import android.os.Handler;

public final /* synthetic */ class AsyncSensorManager$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ AsyncSensorManager f$0;
    public final /* synthetic */ SensorManager.DynamicSensorCallback f$1;
    public final /* synthetic */ Handler f$2;

    public /* synthetic */ AsyncSensorManager$$ExternalSyntheticLambda4(AsyncSensorManager asyncSensorManager, SensorManager.DynamicSensorCallback dynamicSensorCallback, Handler handler) {
        this.f$0 = asyncSensorManager;
        this.f$1 = dynamicSensorCallback;
        this.f$2 = handler;
    }

    public final void run() {
        this.f$0.lambda$registerDynamicSensorCallbackImpl$1(this.f$1, this.f$2);
    }
}
