package com.android.systemui.globalactions;

import android.view.View;
import com.android.systemui.globalactions.GlobalActionsDialogLite;

/* renamed from: com.android.systemui.globalactions.GlobalActionsDialogLite$MyPowerOptionsAdapter$$ExternalSyntheticLambda1 */
public final /* synthetic */ class C0972xb6603ee6 implements View.OnLongClickListener {
    public final /* synthetic */ GlobalActionsDialogLite.MyPowerOptionsAdapter f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ C0972xb6603ee6(GlobalActionsDialogLite.MyPowerOptionsAdapter myPowerOptionsAdapter, int i) {
        this.f$0 = myPowerOptionsAdapter;
        this.f$1 = i;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$getView$1(this.f$1, view);
    }
}
