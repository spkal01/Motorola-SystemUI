package com.motorola.systemui.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import com.motorola.systemui.sensors.IMotoDozeSensor;
import com.motorola.systemui.sensors.MotoDozeSensorTypeBuilder;

public class MotoDozeSensorImpl implements IMotoDozeSensor {
    private Object mListener;
    private SensorManager mSensorManager;
    private int mSensorType;
    private IMotoDozeSensor.TriggerMode mTriggerMode;

    public MotoDozeSensorImpl(SensorManager sensorManager, MotoDozeSensorTypeBuilder.MotoDozeSensorType motoDozeSensorType) {
        this.mSensorManager = sensorManager;
        this.mSensorType = motoDozeSensorType.getType();
        this.mTriggerMode = motoDozeSensorType.getTriggerMode();
    }

    public void activate() {
        Sensor defaultSensor = this.mSensorManager.getDefaultSensor(this.mSensorType, true);
        IMotoDozeSensor.TriggerMode triggerMode = this.mTriggerMode;
        if (triggerMode == IMotoDozeSensor.TriggerMode.ONCE) {
            Object obj = this.mListener;
            if (obj instanceof TriggerEventListener) {
                this.mSensorManager.requestTriggerSensor((TriggerEventListener) obj, defaultSensor);
                return;
            }
        }
        if (triggerMode == IMotoDozeSensor.TriggerMode.MULTIPLE) {
            Object obj2 = this.mListener;
            if (obj2 instanceof SensorEventListener) {
                this.mSensorManager.registerListener((SensorEventListener) obj2, defaultSensor, 3);
            }
        }
    }

    public void setListener(SensorEventListener sensorEventListener) {
        this.mListener = sensorEventListener;
    }

    public void setListener(TriggerEventListener triggerEventListener) {
        this.mListener = triggerEventListener;
    }

    public int getType() {
        return this.mSensorType;
    }

    public IMotoDozeSensor.TriggerMode getTriggerMode() {
        return this.mTriggerMode;
    }
}
