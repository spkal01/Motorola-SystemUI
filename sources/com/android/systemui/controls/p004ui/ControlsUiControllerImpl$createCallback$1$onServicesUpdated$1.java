package com.android.systemui.controls.p004ui;

import android.view.ViewGroup;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1 */
/* compiled from: ControlsUiControllerImpl.kt */
final class ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1 implements Runnable {
    final /* synthetic */ List<SelectionItem> $lastItems;
    final /* synthetic */ Function1<List<SelectionItem>, Unit> $onResult;
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1(ControlsUiControllerImpl controlsUiControllerImpl, List<SelectionItem> list, Function1<? super List<SelectionItem>, Unit> function1) {
        this.this$0 = controlsUiControllerImpl;
        this.$lastItems = list;
        this.$onResult = function1;
    }

    public final void run() {
        ViewGroup access$getParent$p = this.this$0.parent;
        if (access$getParent$p != null) {
            access$getParent$p.removeAllViews();
            if (this.$lastItems.size() > 0) {
                this.$onResult.invoke(this.$lastItems);
                return;
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }
}
