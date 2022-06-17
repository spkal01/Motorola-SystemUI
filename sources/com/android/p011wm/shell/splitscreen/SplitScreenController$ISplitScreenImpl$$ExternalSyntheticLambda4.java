package com.android.p011wm.shell.splitscreen;

import com.android.p011wm.shell.splitscreen.SplitScreenController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.splitscreen.SplitScreenController$ISplitScreenImpl$$ExternalSyntheticLambda4 */
public final /* synthetic */ class SplitScreenController$ISplitScreenImpl$$ExternalSyntheticLambda4 implements Consumer {
    public final /* synthetic */ SplitScreenController.ISplitScreenImpl f$0;

    public /* synthetic */ SplitScreenController$ISplitScreenImpl$$ExternalSyntheticLambda4(SplitScreenController.ISplitScreenImpl iSplitScreenImpl) {
        this.f$0 = iSplitScreenImpl;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$unregisterSplitScreenListener$1((SplitScreenController) obj);
    }
}
