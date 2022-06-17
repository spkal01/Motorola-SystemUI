package com.android.systemui.p006qs.tiles;

import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.tiles.DeviceControlsTile$listingCallback$1 */
/* compiled from: DeviceControlsTile.kt */
public final class DeviceControlsTile$listingCallback$1 implements ControlsListingController.ControlsListingCallback {
    final /* synthetic */ DeviceControlsTile this$0;

    DeviceControlsTile$listingCallback$1(DeviceControlsTile deviceControlsTile) {
        this.this$0 = deviceControlsTile;
    }

    public void onServicesUpdated(@NotNull List<ControlsServiceInfo> list) {
        Intrinsics.checkNotNullParameter(list, "serviceInfos");
        if (this.this$0.hasControlsApps.compareAndSet(list.isEmpty(), !list.isEmpty())) {
            this.this$0.refreshState();
        }
    }
}
