package com.android.systemui.statusbar.phone;

import android.view.View;

public final /* synthetic */ class CollapsedStatusBarFragment$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ View f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ CollapsedStatusBarFragment$$ExternalSyntheticLambda1(View view, int i) {
        this.f$0 = view;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.setVisibility(this.f$1);
    }
}
