package com.android.systemui.statusbar.policy;

import android.content.SharedPreferences;
import android.util.Log;
import com.android.systemui.controls.controller.SeedResponse;
import com.android.systemui.controls.management.ControlsListingController;
import java.util.Optional;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: DeviceControlsControllerImpl.kt */
final class DeviceControlsControllerImpl$seedFavorites$2 implements Consumer<SeedResponse> {
    final /* synthetic */ SharedPreferences $prefs;
    final /* synthetic */ DeviceControlsControllerImpl this$0;

    DeviceControlsControllerImpl$seedFavorites$2(DeviceControlsControllerImpl deviceControlsControllerImpl, SharedPreferences sharedPreferences) {
        this.this$0 = deviceControlsControllerImpl;
        this.$prefs = sharedPreferences;
    }

    public final void accept(@NotNull SeedResponse seedResponse) {
        Intrinsics.checkNotNullParameter(seedResponse, "response");
        Log.d("DeviceControlsControllerImpl", Intrinsics.stringPlus("Controls seeded: ", seedResponse));
        if (seedResponse.getAccepted()) {
            DeviceControlsControllerImpl deviceControlsControllerImpl = this.this$0;
            SharedPreferences sharedPreferences = this.$prefs;
            Intrinsics.checkNotNullExpressionValue(sharedPreferences, "prefs");
            deviceControlsControllerImpl.addPackageToSeededSet(sharedPreferences, seedResponse.getPackageName());
            if (this.this$0.mo23583x51e59865() == null) {
                this.this$0.mo23584xa7d03371(7);
            }
            this.this$0.fireControlsUpdate();
            Optional<ControlsListingController> controlsListingController = this.this$0.controlsComponent.getControlsListingController();
            final DeviceControlsControllerImpl deviceControlsControllerImpl2 = this.this$0;
            controlsListingController.ifPresent(new Consumer<ControlsListingController>() {
                public final void accept(@NotNull ControlsListingController controlsListingController) {
                    Intrinsics.checkNotNullParameter(controlsListingController, "it");
                    controlsListingController.removeCallback(deviceControlsControllerImpl2.listingCallback);
                }
            });
        }
    }
}
