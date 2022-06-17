package com.android.systemui.util.sensors;

import android.content.Context;
import android.hardware.HardwareBuffer;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorDirectChannel;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.Handler;
import android.os.MemoryFile;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.SensorManagerPlugin;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.concurrency.ThreadFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class AsyncSensorManager extends SensorManager implements PluginListener<SensorManagerPlugin> {
    private final Executor mExecutor;
    private final SensorManager mInner;
    private final List<SensorManagerPlugin> mPlugins = new ArrayList();
    private final List<Sensor> mSensorCache;

    public AsyncSensorManager(SensorManager sensorManager, ThreadFactory threadFactory, PluginManager pluginManager) {
        this.mInner = sensorManager;
        this.mExecutor = threadFactory.buildExecutorOnNewThread("async_sensor");
        this.mSensorCache = sensorManager.getSensorList(-1);
        if (pluginManager != null) {
            pluginManager.addPluginListener(this, (Class<?>) SensorManagerPlugin.class, true);
        }
    }

    /* access modifiers changed from: protected */
    public List<Sensor> getFullSensorList() {
        return this.mSensorCache;
    }

    /* access modifiers changed from: protected */
    public List<Sensor> getFullDynamicSensorList() {
        return this.mInner.getSensorList(-1);
    }

    /* access modifiers changed from: protected */
    public boolean registerListenerImpl(SensorEventListener sensorEventListener, Sensor sensor, int i, Handler handler, int i2, int i3) {
        this.mExecutor.execute(new AsyncSensorManager$$ExternalSyntheticLambda2(this, sensorEventListener, sensor, i, i2, handler));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$registerListenerImpl$0(SensorEventListener sensorEventListener, Sensor sensor, int i, int i2, Handler handler) {
        if (!this.mInner.registerListener(sensorEventListener, sensor, i, i2, handler)) {
            Log.e("AsyncSensorManager", "Registering " + sensorEventListener + " for " + sensor + " failed.");
        }
    }

    /* access modifiers changed from: protected */
    public boolean flushImpl(SensorEventListener sensorEventListener) {
        return this.mInner.flush(sensorEventListener);
    }

    /* access modifiers changed from: protected */
    public SensorDirectChannel createDirectChannelImpl(MemoryFile memoryFile, HardwareBuffer hardwareBuffer) {
        throw new UnsupportedOperationException("not implemented");
    }

    /* access modifiers changed from: protected */
    public void destroyDirectChannelImpl(SensorDirectChannel sensorDirectChannel) {
        throw new UnsupportedOperationException("not implemented");
    }

    /* access modifiers changed from: protected */
    public int configureDirectChannelImpl(SensorDirectChannel sensorDirectChannel, Sensor sensor, int i) {
        throw new UnsupportedOperationException("not implemented");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$registerDynamicSensorCallbackImpl$1(SensorManager.DynamicSensorCallback dynamicSensorCallback, Handler handler) {
        this.mInner.registerDynamicSensorCallback(dynamicSensorCallback, handler);
    }

    /* access modifiers changed from: protected */
    public void registerDynamicSensorCallbackImpl(SensorManager.DynamicSensorCallback dynamicSensorCallback, Handler handler) {
        this.mExecutor.execute(new AsyncSensorManager$$ExternalSyntheticLambda4(this, dynamicSensorCallback, handler));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$unregisterDynamicSensorCallbackImpl$2(SensorManager.DynamicSensorCallback dynamicSensorCallback) {
        this.mInner.unregisterDynamicSensorCallback(dynamicSensorCallback);
    }

    /* access modifiers changed from: protected */
    public void unregisterDynamicSensorCallbackImpl(SensorManager.DynamicSensorCallback dynamicSensorCallback) {
        this.mExecutor.execute(new AsyncSensorManager$$ExternalSyntheticLambda3(this, dynamicSensorCallback));
    }

    /* access modifiers changed from: protected */
    public boolean requestTriggerSensorImpl(TriggerEventListener triggerEventListener, Sensor sensor) {
        if (triggerEventListener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        } else if (sensor != null) {
            this.mExecutor.execute(new AsyncSensorManager$$ExternalSyntheticLambda5(this, triggerEventListener, sensor));
            return true;
        } else {
            throw new IllegalArgumentException("sensor cannot be null");
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestTriggerSensorImpl$3(TriggerEventListener triggerEventListener, Sensor sensor) {
        if (!this.mInner.requestTriggerSensor(triggerEventListener, sensor)) {
            Log.e("AsyncSensorManager", "Requesting " + triggerEventListener + " for " + sensor + " failed.");
        }
    }

    /* access modifiers changed from: protected */
    public boolean cancelTriggerSensorImpl(TriggerEventListener triggerEventListener, Sensor sensor, boolean z) {
        Preconditions.checkArgument(z);
        this.mExecutor.execute(new AsyncSensorManager$$ExternalSyntheticLambda6(this, triggerEventListener, sensor));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelTriggerSensorImpl$4(TriggerEventListener triggerEventListener, Sensor sensor) {
        if (!this.mInner.cancelTriggerSensor(triggerEventListener, sensor)) {
            Log.e("AsyncSensorManager", "Canceling " + triggerEventListener + " for " + sensor + " failed.");
        }
    }

    public boolean registerPluginListener(SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener sensorEventListener) {
        if (this.mPlugins.isEmpty()) {
            Log.w("AsyncSensorManager", "No plugins registered");
            return false;
        }
        this.mExecutor.execute(new AsyncSensorManager$$ExternalSyntheticLambda7(this, sensor, sensorEventListener));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$registerPluginListener$5(SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener sensorEventListener) {
        for (int i = 0; i < this.mPlugins.size(); i++) {
            this.mPlugins.get(i).registerListener(sensor, sensorEventListener);
        }
    }

    public void unregisterPluginListener(SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener sensorEventListener) {
        this.mExecutor.execute(new AsyncSensorManager$$ExternalSyntheticLambda8(this, sensor, sensorEventListener));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$unregisterPluginListener$6(SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener sensorEventListener) {
        for (int i = 0; i < this.mPlugins.size(); i++) {
            this.mPlugins.get(i).unregisterListener(sensor, sensorEventListener);
        }
    }

    /* access modifiers changed from: protected */
    public boolean initDataInjectionImpl(boolean z) {
        throw new UnsupportedOperationException("not implemented");
    }

    /* access modifiers changed from: protected */
    public boolean injectSensorDataImpl(Sensor sensor, float[] fArr, int i, long j) {
        throw new UnsupportedOperationException("not implemented");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setOperationParameterImpl$7(SensorAdditionalInfo sensorAdditionalInfo) {
        this.mInner.setOperationParameter(sensorAdditionalInfo);
    }

    /* access modifiers changed from: protected */
    public boolean setOperationParameterImpl(SensorAdditionalInfo sensorAdditionalInfo) {
        this.mExecutor.execute(new AsyncSensorManager$$ExternalSyntheticLambda1(this, sensorAdditionalInfo));
        return true;
    }

    /* access modifiers changed from: protected */
    public void unregisterListenerImpl(SensorEventListener sensorEventListener, Sensor sensor) {
        this.mExecutor.execute(new AsyncSensorManager$$ExternalSyntheticLambda0(this, sensor, sensorEventListener));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$unregisterListenerImpl$8(Sensor sensor, SensorEventListener sensorEventListener) {
        if (sensor == null) {
            this.mInner.unregisterListener(sensorEventListener);
        } else {
            this.mInner.unregisterListener(sensorEventListener, sensor);
        }
    }

    public void onPluginConnected(SensorManagerPlugin sensorManagerPlugin, Context context) {
        this.mPlugins.add(sensorManagerPlugin);
    }

    public void onPluginDisconnected(SensorManagerPlugin sensorManagerPlugin) {
        this.mPlugins.remove(sensorManagerPlugin);
    }
}
