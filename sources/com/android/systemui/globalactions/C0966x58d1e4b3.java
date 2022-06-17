package com.android.systemui.globalactions;

import android.view.MotionEvent;
import android.view.View;
import com.android.systemui.globalactions.GlobalActionsDialogLite;

/* renamed from: com.android.systemui.globalactions.GlobalActionsDialogLite$ActionsDialogLite$$ExternalSyntheticLambda5 */
public final /* synthetic */ class C0966x58d1e4b3 implements View.OnTouchListener {
    public final /* synthetic */ GlobalActionsDialogLite.ActionsDialogLite f$0;

    public /* synthetic */ C0966x58d1e4b3(GlobalActionsDialogLite.ActionsDialogLite actionsDialogLite) {
        this.f$0 = actionsDialogLite;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.lambda$initializeLayout$2(view, motionEvent);
    }
}
