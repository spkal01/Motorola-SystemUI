package com.android.systemui.doze.dagger;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Handler;
import com.android.systemui.R$string;
import com.android.systemui.doze.DozeAuthRemover;
import com.android.systemui.doze.DozeBrightnessHostForwarder;
import com.android.systemui.doze.DozeDockHandler;
import com.android.systemui.doze.DozeFalsingManagerAdapter;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.doze.DozePauser;
import com.android.systemui.doze.DozeScreenBrightness;
import com.android.systemui.doze.DozeScreenState;
import com.android.systemui.doze.DozeScreenStatePreventingAdapter;
import com.android.systemui.doze.DozeSensors;
import com.android.systemui.doze.DozeSuspendScreenStatePreventingAdapter;
import com.android.systemui.doze.DozeTriggers;
import com.android.systemui.doze.DozeUi;
import com.android.systemui.doze.DozeWallpaperState;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
import java.util.Optional;

public abstract class DozeModule {
    static DozeMachine.Part[] providesDozeMachinePartes(DozePauser dozePauser, DozeFalsingManagerAdapter dozeFalsingManagerAdapter, DozeTriggers dozeTriggers, DozeUi dozeUi, DozeScreenState dozeScreenState, DozeScreenBrightness dozeScreenBrightness, DozeWallpaperState dozeWallpaperState, DozeDockHandler dozeDockHandler, DozeAuthRemover dozeAuthRemover) {
        return new DozeMachine.Part[]{dozePauser, dozeFalsingManagerAdapter, dozeTriggers, dozeUi, dozeScreenState, dozeScreenBrightness, dozeWallpaperState, dozeDockHandler, dozeAuthRemover};
    }

    static DozeMachine.Service providesWrappedService(DozeMachine.Service service, DozeHost dozeHost, DozeParameters dozeParameters) {
        return DozeSuspendScreenStatePreventingAdapter.wrapIfNeeded(DozeScreenStatePreventingAdapter.wrapIfNeeded(new DozeBrightnessHostForwarder(service, dozeHost), dozeParameters), dozeParameters);
    }

    static WakeLock providesDozeWakeLock(DelayedWakeLock.Builder builder, Handler handler) {
        return builder.setHandler(handler).setTag("Doze").build();
    }

    static Optional<Sensor> providesBrightnessSensor(AsyncSensorManager asyncSensorManager, Context context) {
        return Optional.ofNullable(DozeSensors.findSensorWithType(asyncSensorManager, context.getString(R$string.doze_brightness_sensor_type)));
    }
}
