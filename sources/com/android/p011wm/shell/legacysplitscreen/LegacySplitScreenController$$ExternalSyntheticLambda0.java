package com.android.p011wm.shell.legacysplitscreen;

import android.window.WindowContainerTransaction;
import com.android.p011wm.shell.common.DisplayChangeController;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class LegacySplitScreenController$$ExternalSyntheticLambda0 implements DisplayChangeController.OnDisplayChangingListener {
    public final /* synthetic */ LegacySplitScreenController f$0;

    public /* synthetic */ LegacySplitScreenController$$ExternalSyntheticLambda0(LegacySplitScreenController legacySplitScreenController) {
        this.f$0 = legacySplitScreenController;
    }

    public final void onRotateDisplay(int i, int i2, int i3, WindowContainerTransaction windowContainerTransaction) {
        this.f$0.lambda$new$0(i, i2, i3, windowContainerTransaction);
    }
}
