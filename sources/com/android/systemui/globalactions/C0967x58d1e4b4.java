package com.android.systemui.globalactions;

import android.view.View;
import android.widget.AdapterView;
import com.android.systemui.globalactions.GlobalActionsDialogLite;

/* renamed from: com.android.systemui.globalactions.GlobalActionsDialogLite$ActionsDialogLite$$ExternalSyntheticLambda6 */
public final /* synthetic */ class C0967x58d1e4b4 implements AdapterView.OnItemClickListener {
    public final /* synthetic */ GlobalActionsDialogLite.ActionsDialogLite f$0;

    public /* synthetic */ C0967x58d1e4b4(GlobalActionsDialogLite.ActionsDialogLite actionsDialogLite) {
        this.f$0 = actionsDialogLite;
    }

    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        this.f$0.lambda$createPowerOverflowPopup$0(adapterView, view, i, j);
    }
}
