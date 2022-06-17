package com.android.p011wm.shell.splitscreen;

import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.splitscreen.SplitScreenController$ISplitScreenImpl$$ExternalSyntheticLambda8 */
public final /* synthetic */ class SplitScreenController$ISplitScreenImpl$$ExternalSyntheticLambda8 implements Consumer {
    public final /* synthetic */ boolean f$0;

    public /* synthetic */ SplitScreenController$ISplitScreenImpl$$ExternalSyntheticLambda8(boolean z) {
        this.f$0 = z;
    }

    public final void accept(Object obj) {
        ((SplitScreenController) obj).setSideStageVisibility(this.f$0);
    }
}
