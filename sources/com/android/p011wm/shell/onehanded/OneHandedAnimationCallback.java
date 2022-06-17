package com.android.p011wm.shell.onehanded;

import android.view.SurfaceControl;
import com.android.p011wm.shell.onehanded.OneHandedAnimationController;

/* renamed from: com.android.wm.shell.onehanded.OneHandedAnimationCallback */
public interface OneHandedAnimationCallback {
    void onAnimationUpdate(SurfaceControl.Transaction transaction, float f, float f2) {
    }

    void onOneHandedAnimationCancel(OneHandedAnimationController.OneHandedTransitionAnimator oneHandedTransitionAnimator) {
    }

    void onOneHandedAnimationEnd(SurfaceControl.Transaction transaction, OneHandedAnimationController.OneHandedTransitionAnimator oneHandedTransitionAnimator) {
    }

    void onOneHandedAnimationStart(OneHandedAnimationController.OneHandedTransitionAnimator oneHandedTransitionAnimator) {
    }
}
