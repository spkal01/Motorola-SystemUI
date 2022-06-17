package com.android.systemui.privacy;

import android.provider.DeviceConfig;
import com.android.systemui.privacy.PrivacyItemController;
import java.lang.ref.WeakReference;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: PrivacyItemController.kt */
public final class PrivacyItemController$devicePropertiesChangedListener$1 implements DeviceConfig.OnPropertiesChangedListener {
    final /* synthetic */ PrivacyItemController this$0;

    PrivacyItemController$devicePropertiesChangedListener$1(PrivacyItemController privacyItemController) {
        this.this$0 = privacyItemController;
    }

    public void onPropertiesChanged(@NotNull DeviceConfig.Properties properties) {
        Intrinsics.checkNotNullParameter(properties, "properties");
        if (!"privacy".equals(properties.getNamespace())) {
            return;
        }
        if (properties.getKeyset().contains("camera_mic_icons_enabled") || properties.getKeyset().contains("location_indicators_enabled")) {
            boolean z = false;
            if (properties.getKeyset().contains("camera_mic_icons_enabled")) {
                this.this$0.micCameraAvailable = properties.getBoolean("camera_mic_icons_enabled", true);
                PrivacyItemController privacyItemController = this.this$0;
                privacyItemController.setAllIndicatorsAvailable(privacyItemController.getMicCameraAvailable() && this.this$0.getLocationAvailable());
                List<WeakReference> access$getCallbacks$p = this.this$0.callbacks;
                PrivacyItemController privacyItemController2 = this.this$0;
                for (WeakReference weakReference : access$getCallbacks$p) {
                    PrivacyItemController.Callback callback = (PrivacyItemController.Callback) weakReference.get();
                    if (callback != null) {
                        callback.onFlagMicCameraChanged(privacyItemController2.getMicCameraAvailable());
                    }
                }
            }
            if (properties.getKeyset().contains("location_indicators_enabled")) {
                this.this$0.setLocationAvailable(properties.getBoolean("location_indicators_enabled", false));
                PrivacyItemController privacyItemController3 = this.this$0;
                if (privacyItemController3.getMicCameraAvailable() && this.this$0.getLocationAvailable()) {
                    z = true;
                }
                privacyItemController3.setAllIndicatorsAvailable(z);
                List<WeakReference> access$getCallbacks$p2 = this.this$0.callbacks;
                PrivacyItemController privacyItemController4 = this.this$0;
                for (WeakReference weakReference2 : access$getCallbacks$p2) {
                    PrivacyItemController.Callback callback2 = (PrivacyItemController.Callback) weakReference2.get();
                    if (callback2 != null) {
                        callback2.onFlagLocationChanged(privacyItemController4.getLocationAvailable());
                    }
                }
            }
            this.this$0.internalUiExecutor.updateListeningState();
        }
    }
}
