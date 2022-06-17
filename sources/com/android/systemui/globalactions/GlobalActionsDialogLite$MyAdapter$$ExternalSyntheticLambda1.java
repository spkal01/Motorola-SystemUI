package com.android.systemui.globalactions;

import android.view.View;
import com.android.systemui.globalactions.GlobalActionsDialogLite;

public final /* synthetic */ class GlobalActionsDialogLite$MyAdapter$$ExternalSyntheticLambda1 implements View.OnLongClickListener {
    public final /* synthetic */ GlobalActionsDialogLite.MyAdapter f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ GlobalActionsDialogLite$MyAdapter$$ExternalSyntheticLambda1(GlobalActionsDialogLite.MyAdapter myAdapter, int i) {
        this.f$0 = myAdapter;
        this.f$1 = i;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$getView$1(this.f$1, view);
    }
}
