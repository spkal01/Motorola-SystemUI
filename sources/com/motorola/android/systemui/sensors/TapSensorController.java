package com.motorola.android.systemui.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.List;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: TapSensorController.kt */
public final class TapSensorController {
    @NotNull
    private final String TAG = "TapSensorController";
    private final int TYPE_MOTO_SINGLE_TAP = 65566;
    @NotNull
    private Context mContext;
    @NotNull
    private final SensorEventListener mSingleTapListener = new TapSensorController$mSingleTapListener$1();
    @NotNull
    private SensorManager mSm;

    public TapSensorController(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.mContext = context;
        Object systemService = context.getSystemService("sensor");
        Objects.requireNonNull(systemService, "null cannot be cast to non-null type android.hardware.SensorManager");
        this.mSm = (SensorManager) systemService;
    }

    public final void registerListener() {
        List<Sensor> sensorList = this.mSm.getSensorList(this.TYPE_MOTO_SINGLE_TAP);
        if (sensorList.size() > 0) {
            Sensor sensor = sensorList.get(0);
            Intrinsics.checkNotNullExpressionValue(sensor, "sensorList[0]");
            Log.i(this.TAG, Intrinsics.stringPlus("register to Single Tap sensor, result = ", Boolean.valueOf(this.mSm.registerListener(this.mSingleTapListener, sensor, 0))));
            return;
        }
        Log.i(this.TAG, "This phone is not support single tap.");
    }

    public final void unregisterListener() {
        Log.i(this.TAG, "SystemUI unregisterListener Tap sensor.");
        this.mSm.unregisterListener(this.mSingleTapListener);
    }
}
