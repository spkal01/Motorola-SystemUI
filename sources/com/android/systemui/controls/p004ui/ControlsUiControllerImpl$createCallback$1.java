package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;
import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$createCallback$1 */
/* compiled from: ControlsUiControllerImpl.kt */
public final class ControlsUiControllerImpl$createCallback$1 implements ControlsListingController.ControlsListingCallback {
    final /* synthetic */ Function1<List<SelectionItem>, Unit> $onResult;
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$createCallback$1(ControlsUiControllerImpl controlsUiControllerImpl, Function1<? super List<SelectionItem>, Unit> function1) {
        this.this$0 = controlsUiControllerImpl;
        this.$onResult = function1;
    }

    public void onServicesUpdated(@NotNull List<ControlsServiceInfo> list) {
        Intrinsics.checkNotNullParameter(list, "serviceInfos");
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (ControlsServiceInfo controlsServiceInfo : list) {
            int i = controlsServiceInfo.getServiceInfo().applicationInfo.uid;
            CharSequence loadLabel = controlsServiceInfo.loadLabel();
            if (loadLabel == null) {
                loadLabel = "";
            }
            Drawable loadIcon = controlsServiceInfo.loadIcon();
            Intrinsics.checkNotNullExpressionValue(loadIcon, "it?.loadIcon()");
            ComponentName componentName = controlsServiceInfo.componentName;
            Intrinsics.checkNotNullExpressionValue(componentName, "it?.componentName");
            arrayList.add(new SelectionItem(loadLabel, "", loadIcon, componentName, i));
        }
        this.this$0.getUiExecutor().execute(new ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1(this.this$0, arrayList, this.$onResult));
    }
}
