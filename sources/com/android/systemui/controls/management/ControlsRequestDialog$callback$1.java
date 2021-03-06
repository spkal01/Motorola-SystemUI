package com.android.systemui.controls.management;

import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsRequestDialog.kt */
public final class ControlsRequestDialog$callback$1 implements ControlsListingController.ControlsListingCallback {
    public void onServicesUpdated(@NotNull List<ControlsServiceInfo> list) {
        Intrinsics.checkNotNullParameter(list, "serviceInfos");
    }

    ControlsRequestDialog$callback$1() {
    }
}
