package com.android.systemui.controls.p004ui;

import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$show$5 */
/* compiled from: ControlsUiControllerImpl.kt */
/* synthetic */ class ControlsUiControllerImpl$show$5 extends FunctionReferenceImpl implements Function1<List<? extends SelectionItem>, Unit> {
    ControlsUiControllerImpl$show$5(ControlsUiControllerImpl controlsUiControllerImpl) {
        super(1, controlsUiControllerImpl, ControlsUiControllerImpl.class, "showControlsView", "showControlsView(Ljava/util/List;)V", 0);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((List<SelectionItem>) (List) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(@NotNull List<SelectionItem> list) {
        Intrinsics.checkNotNullParameter(list, "p0");
        ((ControlsUiControllerImpl) this.receiver).showControlsView(list);
    }
}
