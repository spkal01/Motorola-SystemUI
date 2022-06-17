package com.android.systemui.controls.management;

import android.content.pm.ServiceInfo;
import android.util.Log;
import com.android.settingslib.applications.ServiceListing;
import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsListingControllerImpl.kt */
final class ControlsListingControllerImpl$serviceListingCallback$1 implements ServiceListing.Callback {
    final /* synthetic */ ControlsListingControllerImpl this$0;

    ControlsListingControllerImpl$serviceListingCallback$1(ControlsListingControllerImpl controlsListingControllerImpl) {
        this.this$0 = controlsListingControllerImpl;
    }

    public final void onServicesReloaded(List<ServiceInfo> list) {
        Intrinsics.checkNotNullExpressionValue(list, "it");
        final List<T> list2 = CollectionsKt___CollectionsKt.toList(list);
        final LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (T componentName : list2) {
            linkedHashSet.add(componentName.getComponentName());
        }
        Executor access$getBackgroundExecutor$p = this.this$0.backgroundExecutor;
        final ControlsListingControllerImpl controlsListingControllerImpl = this.this$0;
        access$getBackgroundExecutor$p.execute(new Runnable() {
            public final void run() {
                if (controlsListingControllerImpl.userChangeInProgress.get() <= 0 && !linkedHashSet.equals(controlsListingControllerImpl.availableComponents)) {
                    Log.d("ControlsListingControllerImpl", Intrinsics.stringPlus("ServiceConfig reloaded, count: ", Integer.valueOf(linkedHashSet.size())));
                    controlsListingControllerImpl.availableComponents = linkedHashSet;
                    controlsListingControllerImpl.availableServices = list2;
                    List<ControlsServiceInfo> currentServices = controlsListingControllerImpl.getCurrentServices();
                    for (ControlsListingController.ControlsListingCallback onServicesUpdated : controlsListingControllerImpl.callbacks) {
                        onServicesUpdated.onServicesUpdated(currentServices);
                    }
                }
            }
        });
    }
}
