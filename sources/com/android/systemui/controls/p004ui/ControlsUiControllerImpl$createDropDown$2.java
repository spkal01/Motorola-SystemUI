package com.android.systemui.controls.p004ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.android.systemui.globalactions.GlobalActionsPopupMenu;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$createDropDown$2 */
/* compiled from: ControlsUiControllerImpl.kt */
public final class ControlsUiControllerImpl$createDropDown$2 implements View.OnClickListener {
    final /* synthetic */ Ref$ObjectRef<ItemAdapter> $adapter;
    final /* synthetic */ ViewGroup $anchor;
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$createDropDown$2(ControlsUiControllerImpl controlsUiControllerImpl, ViewGroup viewGroup, Ref$ObjectRef<ItemAdapter> ref$ObjectRef) {
        this.this$0 = controlsUiControllerImpl;
        this.$anchor = viewGroup;
        this.$adapter = ref$ObjectRef;
    }

    public void onClick(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "v");
        ControlsUiControllerImpl controlsUiControllerImpl = this.this$0;
        GlobalActionsPopupMenu globalActionsPopupMenu = new GlobalActionsPopupMenu(this.this$0.popupThemedContext, true);
        ViewGroup viewGroup = this.$anchor;
        Ref$ObjectRef<ItemAdapter> ref$ObjectRef = this.$adapter;
        ControlsUiControllerImpl controlsUiControllerImpl2 = this.this$0;
        globalActionsPopupMenu.setAnchorView(viewGroup);
        globalActionsPopupMenu.setAdapter((ListAdapter) ref$ObjectRef.element);
        globalActionsPopupMenu.setOnItemClickListener(new ControlsUiControllerImpl$createDropDown$2$onClick$1$1(controlsUiControllerImpl2, globalActionsPopupMenu));
        globalActionsPopupMenu.show();
        Unit unit = Unit.INSTANCE;
        controlsUiControllerImpl.popup = globalActionsPopupMenu;
    }
}
