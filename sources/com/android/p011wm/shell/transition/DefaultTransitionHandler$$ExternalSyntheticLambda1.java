package com.android.p011wm.shell.transition;

import android.animation.ValueAnimator;
import android.view.SurfaceControl;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import java.util.ArrayList;

/* renamed from: com.android.wm.shell.transition.DefaultTransitionHandler$$ExternalSyntheticLambda1 */
public final /* synthetic */ class DefaultTransitionHandler$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ DefaultTransitionHandler f$0;
    public final /* synthetic */ ValueAnimator f$1;
    public final /* synthetic */ SurfaceControl.Transaction f$2;
    public final /* synthetic */ SurfaceControl f$3;
    public final /* synthetic */ Animation f$4;
    public final /* synthetic */ Transformation f$5;
    public final /* synthetic */ float[] f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ Runnable f$8;

    public /* synthetic */ DefaultTransitionHandler$$ExternalSyntheticLambda1(DefaultTransitionHandler defaultTransitionHandler, ValueAnimator valueAnimator, SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Animation animation, Transformation transformation, float[] fArr, ArrayList arrayList, Runnable runnable) {
        this.f$0 = defaultTransitionHandler;
        this.f$1 = valueAnimator;
        this.f$2 = transaction;
        this.f$3 = surfaceControl;
        this.f$4 = animation;
        this.f$5 = transformation;
        this.f$6 = fArr;
        this.f$7 = arrayList;
        this.f$8 = runnable;
    }

    public final void run() {
        this.f$0.lambda$startAnimInternal$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
