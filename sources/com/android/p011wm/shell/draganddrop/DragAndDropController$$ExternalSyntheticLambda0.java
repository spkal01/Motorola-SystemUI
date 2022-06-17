package com.android.p011wm.shell.draganddrop;

import com.android.p011wm.shell.draganddrop.DragAndDropController;

/* renamed from: com.android.wm.shell.draganddrop.DragAndDropController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class DragAndDropController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ DragAndDropController f$0;
    public final /* synthetic */ DragAndDropController.PerDisplay f$1;

    public /* synthetic */ DragAndDropController$$ExternalSyntheticLambda0(DragAndDropController dragAndDropController, DragAndDropController.PerDisplay perDisplay) {
        this.f$0 = dragAndDropController;
        this.f$1 = perDisplay;
    }

    public final void run() {
        this.f$0.lambda$onDrag$0(this.f$1);
    }
}
