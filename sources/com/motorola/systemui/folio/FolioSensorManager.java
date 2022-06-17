package com.motorola.systemui.folio;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.CopyOnWriteArrayList;

public class FolioSensorManager {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static FolioSensorManager mFSM;
    /* access modifiers changed from: private */
    public boolean mAttachState;
    private CopyOnWriteArrayList<Callback> mCallbacks = new CopyOnWriteArrayList<>();
    /* access modifiers changed from: private */
    public boolean mCoverState;
    /* access modifiers changed from: private */
    public boolean mFolioClosed;
    private SensorManager mSm;

    public interface Callback {
        void onFolioCloseChanged(boolean z);
    }

    /* access modifiers changed from: private */
    public boolean isCovered(float f) {
        return f == 1.0f || f == 2.0f || f == 3.0f;
    }

    private FolioSensorManager(Context context) {
        this.mSm = (SensorManager) context.getSystemService("sensor");
        registerSensor();
    }

    public static FolioSensorManager getInstance(Context context) {
        if (mFSM == null) {
            mFSM = new FolioSensorManager(context);
        }
        return mFSM;
    }

    private void registerSensor() {
        FolioSensorEventListener folioSensorEventListener = new FolioSensorEventListener();
        for (Sensor next : this.mSm.getSensorList(65580)) {
            if (isAttachSensor(next) || isCoverSensor(next)) {
                Log.i("FolioSensorManager", "Register sensor: " + next.toString());
                this.mSm.registerListener(folioSensorEventListener, next, 3);
            }
        }
    }

    public void addSensorChangeListener(Callback callback) {
        if (findSensorChangeListener(callback) < 0) {
            this.mCallbacks.add(callback);
        }
    }

    public void removeSensorChangeListener(Callback callback) {
        int findSensorChangeListener = findSensorChangeListener(callback);
        if (findSensorChangeListener >= 0) {
            this.mCallbacks.remove(findSensorChangeListener);
        }
    }

    private int findSensorChangeListener(Callback callback) {
        int size = this.mCallbacks.size();
        for (int i = 0; i < size; i++) {
            if (this.mCallbacks.get(i) == callback) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public void notifyFolioSensorChanged() {
        if (DEBUG) {
            Log.d("FolioSensorManager", "notifyFolioSensorChanged");
        }
        int size = this.mCallbacks.size();
        for (int i = 0; i < size; i++) {
            Callback callback = this.mCallbacks.get(i);
            if (callback != null) {
                callback.onFolioCloseChanged(this.mFolioClosed);
            }
        }
    }

    private final class FolioSensorEventListener implements SensorEventListener {
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        private FolioSensorEventListener() {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            boolean access$700;
            StringBuilder sb = new StringBuilder();
            sb.append("onSensorChanged:  type: ");
            sb.append(sensorEvent.sensor.getType());
            sb.append(" name: ");
            sb.append(sensorEvent.sensor.getName());
            sb.append(" subType: ");
            boolean z = false;
            sb.append(sensorEvent.values[0]);
            Log.d("FolioSensorManager", sb.toString());
            if (FolioSensorManager.this.isAttachSensor(sensorEvent.sensor)) {
                boolean z2 = sensorEvent.values[0] == 1.0f;
                if (z2 != FolioSensorManager.this.mAttachState) {
                    FolioSensorManager folioSensorManager = FolioSensorManager.this;
                    boolean unused = folioSensorManager.mFolioClosed = folioSensorManager.mCoverState && z2;
                    boolean unused2 = FolioSensorManager.this.mAttachState = z2;
                    FolioSensorManager.this.notifyFolioSensorChanged();
                }
            }
            if (FolioSensorManager.this.isCoverSensor(sensorEvent.sensor) && (access$700 = FolioSensorManager.this.isCovered(sensorEvent.values[0])) != FolioSensorManager.this.mCoverState) {
                FolioSensorManager folioSensorManager2 = FolioSensorManager.this;
                if (folioSensorManager2.mAttachState && access$700) {
                    z = true;
                }
                boolean unused3 = folioSensorManager2.mFolioClosed = z;
                boolean unused4 = FolioSensorManager.this.mCoverState = access$700;
                FolioSensorManager.this.notifyFolioSensorChanged();
            }
            Log.i("FolioSensorManager", "The Folio is closed: " + FolioSensorManager.this.mFolioClosed);
        }
    }

    /* access modifiers changed from: private */
    public boolean isCoverSensor(Sensor sensor) {
        String name = sensor.getName();
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        if (name.contains("Cover") || name.contains("cover")) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isAttachSensor(Sensor sensor) {
        String name = sensor.getName();
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        if (name.contains("Attach") || name.contains("attach")) {
            return true;
        }
        return false;
    }
}
