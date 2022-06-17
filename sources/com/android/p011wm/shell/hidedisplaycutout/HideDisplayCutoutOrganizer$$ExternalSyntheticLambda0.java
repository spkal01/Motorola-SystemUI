package com.android.p011wm.shell.hidedisplaycutout;

import android.view.SurfaceControl;
import android.window.WindowContainerToken;
import android.window.WindowContainerTransaction;
import java.util.function.BiConsumer;

/* renamed from: com.android.wm.shell.hidedisplaycutout.HideDisplayCutoutOrganizer$$ExternalSyntheticLambda0 */
public final /* synthetic */ class HideDisplayCutoutOrganizer$$ExternalSyntheticLambda0 implements BiConsumer {
    public final /* synthetic */ HideDisplayCutoutOrganizer f$0;
    public final /* synthetic */ WindowContainerTransaction f$1;
    public final /* synthetic */ SurfaceControl.Transaction f$2;

    public /* synthetic */ HideDisplayCutoutOrganizer$$ExternalSyntheticLambda0(HideDisplayCutoutOrganizer hideDisplayCutoutOrganizer, WindowContainerTransaction windowContainerTransaction, SurfaceControl.Transaction transaction) {
        this.f$0 = hideDisplayCutoutOrganizer;
        this.f$1 = windowContainerTransaction;
        this.f$2 = transaction;
    }

    public final void accept(Object obj, Object obj2) {
        this.f$0.lambda$applyAllBoundsAndOffsets$0(this.f$1, this.f$2, (WindowContainerToken) obj, (SurfaceControl) obj2);
    }
}
