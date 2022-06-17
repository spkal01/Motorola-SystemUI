package com.android.p011wm.shell.startingsurface;

import com.android.internal.util.function.TriConsumer;
import com.android.p011wm.shell.startingsurface.StartingWindowController;

/* renamed from: com.android.wm.shell.startingsurface.StartingWindowController$IStartingWindowImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2406x795f7bd0 implements TriConsumer {
    public final /* synthetic */ StartingWindowController.IStartingWindowImpl f$0;

    public /* synthetic */ C2406x795f7bd0(StartingWindowController.IStartingWindowImpl iStartingWindowImpl) {
        this.f$0 = iStartingWindowImpl;
    }

    public final void accept(Object obj, Object obj2, Object obj3) {
        this.f$0.notifyIStartingWindowListener(((Integer) obj).intValue(), ((Integer) obj2).intValue(), ((Integer) obj3).intValue());
    }
}
