package com.android.p011wm.shell.startingsurface;

import android.os.IBinder;
import android.window.StartingWindowInfo;

/* renamed from: com.android.wm.shell.startingsurface.StartingWindowController$$ExternalSyntheticLambda4 */
public final /* synthetic */ class StartingWindowController$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ StartingWindowController f$0;
    public final /* synthetic */ StartingWindowInfo f$1;
    public final /* synthetic */ IBinder f$2;

    public /* synthetic */ StartingWindowController$$ExternalSyntheticLambda4(StartingWindowController startingWindowController, StartingWindowInfo startingWindowInfo, IBinder iBinder) {
        this.f$0 = startingWindowController;
        this.f$1 = startingWindowInfo;
        this.f$2 = iBinder;
    }

    public final void run() {
        this.f$0.lambda$addStartingWindow$0(this.f$1, this.f$2);
    }
}
