package com.android.p011wm.shell.transition;

import android.animation.ValueAnimator;
import java.util.ArrayList;

/* renamed from: com.android.wm.shell.transition.DefaultTransitionHandler$$ExternalSyntheticLambda3 */
public final /* synthetic */ class DefaultTransitionHandler$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ArrayList f$0;
    public final /* synthetic */ ValueAnimator f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ DefaultTransitionHandler$$ExternalSyntheticLambda3(ArrayList arrayList, ValueAnimator valueAnimator, Runnable runnable) {
        this.f$0 = arrayList;
        this.f$1 = valueAnimator;
        this.f$2 = runnable;
    }

    public final void run() {
        DefaultTransitionHandler.lambda$startAnimInternal$2(this.f$0, this.f$1, this.f$2);
    }
}
