package com.android.p011wm.shell.splitscreen;

import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.splitscreen.SplitScreenController$ISplitScreenImpl$$ExternalSyntheticLambda7 */
public final /* synthetic */ class SplitScreenController$ISplitScreenImpl$$ExternalSyntheticLambda7 implements Consumer {
    public final /* synthetic */ boolean f$0;

    public /* synthetic */ SplitScreenController$ISplitScreenImpl$$ExternalSyntheticLambda7(boolean z) {
        this.f$0 = z;
    }

    public final void accept(Object obj) {
        ((SplitScreenController) obj).exitSplitScreenOnHide(this.f$0);
    }
}
