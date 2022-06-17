package com.android.p011wm.shell.startingsurface;

import com.android.p011wm.shell.startingsurface.StartingWindowController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.startingsurface.StartingWindowController$IStartingWindowImpl$$ExternalSyntheticLambda1 */
public final /* synthetic */ class C2407x795f7bd1 implements Consumer {
    public final /* synthetic */ StartingWindowController.IStartingWindowImpl f$0;
    public final /* synthetic */ IStartingWindowListener f$1;

    public /* synthetic */ C2407x795f7bd1(StartingWindowController.IStartingWindowImpl iStartingWindowImpl, IStartingWindowListener iStartingWindowListener) {
        this.f$0 = iStartingWindowImpl;
        this.f$1 = iStartingWindowListener;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$setStartingWindowListener$0(this.f$1, (StartingWindowController) obj);
    }
}
