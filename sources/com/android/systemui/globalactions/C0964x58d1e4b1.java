package com.android.systemui.globalactions;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

/* renamed from: com.android.systemui.globalactions.GlobalActionsDialogLite$ActionsDialogLite$$ExternalSyntheticLambda3 */
public final /* synthetic */ class C0964x58d1e4b1 implements View.OnApplyWindowInsetsListener {
    public final /* synthetic */ ViewGroup f$0;

    public /* synthetic */ C0964x58d1e4b1(ViewGroup viewGroup) {
        this.f$0 = viewGroup;
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.f$0.setPadding(windowInsets.getStableInsetLeft(), windowInsets.getStableInsetTop(), windowInsets.getStableInsetRight(), windowInsets.getStableInsetBottom());
    }
}
