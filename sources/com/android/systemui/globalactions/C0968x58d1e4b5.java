package com.android.systemui.globalactions;

import android.view.View;
import android.widget.AdapterView;
import com.android.systemui.globalactions.GlobalActionsDialogLite;

/* renamed from: com.android.systemui.globalactions.GlobalActionsDialogLite$ActionsDialogLite$$ExternalSyntheticLambda7 */
public final /* synthetic */ class C0968x58d1e4b5 implements AdapterView.OnItemLongClickListener {
    public final /* synthetic */ GlobalActionsDialogLite.ActionsDialogLite f$0;

    public /* synthetic */ C0968x58d1e4b5(GlobalActionsDialogLite.ActionsDialogLite actionsDialogLite) {
        this.f$0 = actionsDialogLite;
    }

    public final boolean onItemLongClick(AdapterView adapterView, View view, int i, long j) {
        return this.f$0.lambda$createPowerOverflowPopup$1(adapterView, view, i, j);
    }
}
