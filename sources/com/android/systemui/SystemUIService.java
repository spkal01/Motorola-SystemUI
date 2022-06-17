package com.android.systemui;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import com.android.internal.os.BinderInternal;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpHandler;
import com.android.systemui.dump.LogBufferFreezer;
import com.android.systemui.dump.SystemUIAuxiliaryDumpService;
import com.android.systemui.statusbar.policy.BatteryStateNotifier;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.android.systemui.sensors.TapSensorController;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class SystemUIService extends Service {
    final ContentObserver mAODTapEnableObserver = new ContentObserver((Handler) null) {
        public void onChange(boolean z) {
            Log.i("SystemUIService", "mAODTapEnableObserver onChange.");
            if (SystemUIService.this.mTapSensorController == null) {
                SystemUIService systemUIService = SystemUIService.this;
                TapSensorController unused = systemUIService.mTapSensorController = new TapSensorController(systemUIService);
            }
            if (!SystemUIService.this.isAODEnable() || !SystemUIService.this.isTapToWakeUpEnable()) {
                SystemUIService.this.mTapSensorController.unregisterListener();
            } else {
                SystemUIService.this.mTapSensorController.registerListener();
            }
        }
    };
    private final BatteryStateNotifier mBatteryStateNotifier;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final DumpHandler mDumpHandler;
    private final LogBufferFreezer mLogBufferFreezer;
    private final Handler mMainHandler;
    /* access modifiers changed from: private */
    public TapSensorController mTapSensorController;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public SystemUIService(Handler handler, DumpHandler dumpHandler, BroadcastDispatcher broadcastDispatcher, LogBufferFreezer logBufferFreezer, BatteryStateNotifier batteryStateNotifier) {
        this.mMainHandler = handler;
        this.mDumpHandler = dumpHandler;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mLogBufferFreezer = logBufferFreezer;
        this.mBatteryStateNotifier = batteryStateNotifier;
    }

    public void onCreate() {
        super.onCreate();
        ((SystemUIApplication) getApplication()).startServicesIfNeeded();
        this.mLogBufferFreezer.attach(this.mBroadcastDispatcher);
        if (getResources().getBoolean(R$bool.config_showNotificationForUnknownBatteryState)) {
            this.mBatteryStateNotifier.startListening();
        }
        if (!Build.IS_DEBUGGABLE || !SystemProperties.getBoolean("debug.crash_sysui", false)) {
            if (Build.IS_DEBUGGABLE) {
                BinderInternal.nSetBinderProxyCountEnabled(true);
                BinderInternal.nSetBinderProxyCountWatermarks(1000, 900);
                BinderInternal.setBinderProxyCountCallback(new BinderInternal.BinderProxyLimitListener() {
                    public void onLimitReached(int i) {
                        Slog.w("SystemUIService", "uid " + i + " sent too many Binder proxies to uid " + Process.myUid());
                    }
                }, this.mMainHandler);
            }
            startServiceAsUser(new Intent(getApplicationContext(), SystemUIAuxiliaryDumpService.class), UserHandle.SYSTEM);
            getContentResolver().registerContentObserver(Settings.Secure.getUriFor("doze_enabled"), false, this.mAODTapEnableObserver, -1);
            getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("is_tap_to_wake_enable"), false, this.mAODTapEnableObserver, -1);
            if (isAODEnable() && isTapToWakeUpEnable()) {
                TapSensorController tapSensorController = new TapSensorController(this);
                this.mTapSensorController = tapSensorController;
                tapSensorController.registerListener();
                return;
            }
            return;
        }
        throw new RuntimeException();
    }

    /* access modifiers changed from: private */
    public boolean isAODEnable() {
        boolean z = false;
        if (Settings.Secure.getIntForUser(getContentResolver(), "doze_enabled", 0, -2) == 2) {
            z = true;
        }
        Log.i("SystemUIService", "AOD enable: " + z);
        return z;
    }

    /* access modifiers changed from: private */
    public boolean isTapToWakeUpEnable() {
        boolean z = false;
        if (MotorolaSettings.Secure.getIntForUser(getContentResolver(), "is_tap_to_wake_enable", 0, -2) == 1) {
            z = true;
        }
        Log.i("SystemUIService", "Tap to wake up enable: " + z);
        return z;
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr.length == 0) {
            strArr = new String[]{"--dump-priority", "CRITICAL"};
        }
        this.mDumpHandler.dump(fileDescriptor, printWriter, strArr);
    }
}
