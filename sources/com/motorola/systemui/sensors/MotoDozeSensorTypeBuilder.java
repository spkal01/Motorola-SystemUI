package com.motorola.systemui.sensors;

import android.hardware.SensorManager;
import android.util.Log;
import com.motorola.systemui.sensors.IMotoDozeSensor;
import java.util.ArrayList;
import java.util.List;

public class MotoDozeSensorTypeBuilder {
    private static final int[] SENSOR_TYPES_WITH_MULTIPLE = {65556};
    private static final int[] SENSOR_TYPES_WITH_ONCE = {25};
    private static MotoDozeSensorTypeBuilder sBuilder;
    private boolean mApproachEnabled;
    private boolean mIsSupportFod;
    private SensorManager mSensorManager;
    private List<MotoDozeSensorType> mSensorTypes;
    private boolean mSupportCli;
    private boolean mUsePickup;

    public static MotoDozeSensorTypeBuilder getInstance() {
        if (sBuilder == null) {
            sBuilder = new MotoDozeSensorTypeBuilder();
        }
        return sBuilder;
    }

    public List<MotoDozeSensorType> build(boolean z, SensorManager sensorManager, boolean z2, boolean z3, boolean z4) {
        this.mSensorManager = sensorManager;
        this.mUsePickup = z2;
        this.mIsSupportFod = z;
        this.mSupportCli = z3;
        this.mApproachEnabled = z4;
        this.mSensorTypes = new ArrayList();
        buildOnceTypeSensors();
        buildMultipleTypeSensors();
        buildGlanceSensors();
        return this.mSensorTypes;
    }

    private void buildOnceTypeSensors() {
        for (int i : SENSOR_TYPES_WITH_ONCE) {
            if (isSensorAvailable(i) && (i != 25 || this.mUsePickup)) {
                this.mSensorTypes.add(new MotoDozeSensorType(i, IMotoDozeSensor.TriggerMode.ONCE));
            }
        }
    }

    private void buildMultipleTypeSensors() {
        for (int i : SENSOR_TYPES_WITH_MULTIPLE) {
            if (isSensorAvailable(i) && ((i != 65556 || !this.mUsePickup) && (i != 8 || this.mIsSupportFod))) {
                this.mSensorTypes.add(new MotoDozeSensorType(i, IMotoDozeSensor.TriggerMode.MULTIPLE));
            }
        }
    }

    private void buildGlanceSensors() {
        if (this.mApproachEnabled && this.mSupportCli && isSensorAvailable(65565)) {
            this.mSensorTypes.add(new MotoDozeSensorType(65565, IMotoDozeSensor.TriggerMode.MULTIPLE));
        } else if (isSensorAvailable(65548)) {
            this.mSensorTypes.add(new MotoDozeSensorType(65548, IMotoDozeSensor.TriggerMode.MULTIPLE));
        }
    }

    private boolean isSensorAvailable(int i) {
        boolean z = true;
        if (this.mSensorManager.getDefaultSensor(i, true) == null) {
            z = false;
        }
        if (!z) {
            Log.e("MotoDozeSensorTypeBuilder", "This sensor type(" + i + ") is not supported.");
        }
        return z;
    }

    class MotoDozeSensorType {
        private IMotoDozeSensor.TriggerMode triggerMode;
        private int type;

        MotoDozeSensorType(int i, IMotoDozeSensor.TriggerMode triggerMode2) {
            this.triggerMode = triggerMode2;
            this.type = i;
        }

        public int getType() {
            return this.type;
        }

        public IMotoDozeSensor.TriggerMode getTriggerMode() {
            return this.triggerMode;
        }
    }
}
