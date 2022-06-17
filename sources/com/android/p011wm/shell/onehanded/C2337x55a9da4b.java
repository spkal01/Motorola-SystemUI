package com.android.p011wm.shell.onehanded;

import android.view.SurfaceControl;
import com.android.p011wm.shell.onehanded.OneHandedAnimationController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.onehanded.OneHandedAnimationController$OneHandedTransitionAnimator$$ExternalSyntheticLambda4 */
public final /* synthetic */ class C2337x55a9da4b implements Consumer {
    public final /* synthetic */ OneHandedAnimationController.OneHandedTransitionAnimator f$0;
    public final /* synthetic */ SurfaceControl.Transaction f$1;

    public /* synthetic */ C2337x55a9da4b(OneHandedAnimationController.OneHandedTransitionAnimator oneHandedTransitionAnimator, SurfaceControl.Transaction transaction) {
        this.f$0 = oneHandedTransitionAnimator;
        this.f$1 = transaction;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onAnimationEnd$1(this.f$1, (OneHandedAnimationCallback) obj);
    }
}
