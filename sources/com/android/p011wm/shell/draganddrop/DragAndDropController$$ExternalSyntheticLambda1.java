package com.android.p011wm.shell.draganddrop;

import android.view.SurfaceControl;
import com.android.p011wm.shell.draganddrop.DragAndDropController;

/* renamed from: com.android.wm.shell.draganddrop.DragAndDropController$$ExternalSyntheticLambda1 */
public final /* synthetic */ class DragAndDropController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ DragAndDropController f$0;
    public final /* synthetic */ DragAndDropController.PerDisplay f$1;
    public final /* synthetic */ SurfaceControl f$2;

    public /* synthetic */ DragAndDropController$$ExternalSyntheticLambda1(DragAndDropController dragAndDropController, DragAndDropController.PerDisplay perDisplay, SurfaceControl surfaceControl) {
        this.f$0 = dragAndDropController;
        this.f$1 = perDisplay;
        this.f$2 = surfaceControl;
    }

    public final void run() {
        this.f$0.lambda$handleDrop$1(this.f$1, this.f$2);
    }
}
