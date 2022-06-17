package com.android.systemui.p008tv;

import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.os.Handler;
import android.os.PowerManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.p007tv.notifications.TvNotificationHandler;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryControllerImpl;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl;

/* renamed from: com.android.systemui.tv.TvSystemUIModule */
public abstract class TvSystemUIModule {
    static boolean provideAllowNotificationLongPress() {
        return true;
    }

    static String provideLeakReportEmail() {
        return null;
    }

    static BatteryController provideBatteryController(Context context, EnhancedEstimates enhancedEstimates, PowerManager powerManager, BroadcastDispatcher broadcastDispatcher, DemoModeController demoModeController, Handler handler, Handler handler2) {
        BatteryControllerImpl batteryControllerImpl = new BatteryControllerImpl(context, enhancedEstimates, powerManager, broadcastDispatcher, demoModeController, handler, handler2);
        batteryControllerImpl.init();
        return batteryControllerImpl;
    }

    static SensorPrivacyController provideSensorPrivacyController(SensorPrivacyManager sensorPrivacyManager) {
        SensorPrivacyControllerImpl sensorPrivacyControllerImpl = new SensorPrivacyControllerImpl(sensorPrivacyManager);
        sensorPrivacyControllerImpl.init();
        return sensorPrivacyControllerImpl;
    }

    static IndividualSensorPrivacyController provideIndividualSensorPrivacyController(SensorPrivacyManager sensorPrivacyManager) {
        IndividualSensorPrivacyControllerImpl individualSensorPrivacyControllerImpl = new IndividualSensorPrivacyControllerImpl(sensorPrivacyManager);
        individualSensorPrivacyControllerImpl.init();
        return individualSensorPrivacyControllerImpl;
    }

    static HeadsUpManagerPhone provideHeadsUpManagerPhone(Context context, StatusBarStateController statusBarStateController, KeyguardBypassController keyguardBypassController, NotificationGroupManagerLegacy notificationGroupManagerLegacy, ConfigurationController configurationController) {
        return new HeadsUpManagerPhone(context, statusBarStateController, keyguardBypassController, notificationGroupManagerLegacy, configurationController);
    }

    static Recents provideRecents(Context context, RecentsImplementation recentsImplementation, CommandQueue commandQueue) {
        return new Recents(context, recentsImplementation, commandQueue);
    }

    static TvNotificationHandler provideTvNotificationHandler(Context context, NotificationListener notificationListener) {
        return new TvNotificationHandler(context, notificationListener);
    }
}
