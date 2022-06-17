package com.motorola.taskbar;

import android.graphics.Rect;
import com.motorola.taskbar.MotoTaskBarController;

public final /* synthetic */ class MotoTaskBarController$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MotoTaskBarController.C27931 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Rect f$2;

    public /* synthetic */ MotoTaskBarController$1$$ExternalSyntheticLambda0(MotoTaskBarController.C27931 r1, int i, Rect rect) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = rect;
    }

    public final void run() {
        this.f$0.lambda$requestSwitchVolumeDialog$0(this.f$1, this.f$2);
    }
}
