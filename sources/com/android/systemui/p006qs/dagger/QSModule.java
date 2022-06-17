package com.android.systemui.p006qs.dagger;

import android.content.Context;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import com.android.systemui.p006qs.AutoAddTracker;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.p006qs.ReduceBrightColorsController;
import com.android.systemui.statusbar.phone.AudioFxController;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceControlsController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.WalletController;
import com.android.systemui.util.settings.SecureSettings;

/* renamed from: com.android.systemui.qs.dagger.QSModule */
public interface QSModule {
    static AutoTileManager provideAutoTileManager(Context context, AutoAddTracker.Builder builder, QSTileHost qSTileHost, Handler handler, SecureSettings secureSettings, HotspotController hotspotController, DataSaverController dataSaverController, ManagedProfileController managedProfileController, NightDisplayListener nightDisplayListener, CastController castController, ReduceBrightColorsController reduceBrightColorsController, DeviceControlsController deviceControlsController, WalletController walletController, boolean z, AudioFxController audioFxController) {
        AutoTileManager autoTileManager = new AutoTileManager(context, builder, qSTileHost, handler, secureSettings, hotspotController, dataSaverController, managedProfileController, nightDisplayListener, castController, reduceBrightColorsController, deviceControlsController, walletController, z, audioFxController);
        autoTileManager.init();
        return autoTileManager;
    }
}
