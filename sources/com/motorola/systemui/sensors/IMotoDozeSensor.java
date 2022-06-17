package com.motorola.systemui.sensors;

import android.hardware.SensorEventListener;
import android.hardware.TriggerEventListener;

public interface IMotoDozeSensor {

    public enum TriggerMode {
        ONCE,
        MULTIPLE
    }

    void activate();

    TriggerMode getTriggerMode();

    int getType();

    void setListener(SensorEventListener sensorEventListener);

    void setListener(TriggerEventListener triggerEventListener);
}
