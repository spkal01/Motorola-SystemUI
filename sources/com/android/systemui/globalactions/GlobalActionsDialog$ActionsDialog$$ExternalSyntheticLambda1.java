package com.android.systemui.globalactions;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

public final /* synthetic */ class GlobalActionsDialog$ActionsDialog$$ExternalSyntheticLambda1 implements View.OnApplyWindowInsetsListener {
    public final /* synthetic */ ViewGroup f$0;

    public /* synthetic */ GlobalActionsDialog$ActionsDialog$$ExternalSyntheticLambda1(ViewGroup viewGroup) {
        this.f$0 = viewGroup;
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.f$0.setPadding(windowInsets.getStableInsetLeft(), windowInsets.getStableInsetTop(), windowInsets.getStableInsetRight(), windowInsets.getStableInsetBottom());
    }
}
