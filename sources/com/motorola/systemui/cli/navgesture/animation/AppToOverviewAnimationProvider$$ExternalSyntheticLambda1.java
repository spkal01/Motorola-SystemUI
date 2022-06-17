package com.motorola.systemui.cli.navgesture.animation;

import android.animation.ValueAnimator;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationTargetSet;
import com.motorola.systemui.cli.navgesture.util.ClipAnimationHelper;

public final /* synthetic */ class AppToOverviewAnimationProvider$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ClipAnimationHelper.TransformParams f$0;
    public final /* synthetic */ ClipAnimationHelper f$1;
    public final /* synthetic */ RemoteAnimationTargetSet f$2;

    public /* synthetic */ AppToOverviewAnimationProvider$$ExternalSyntheticLambda1(ClipAnimationHelper.TransformParams transformParams, ClipAnimationHelper clipAnimationHelper, RemoteAnimationTargetSet remoteAnimationTargetSet) {
        this.f$0 = transformParams;
        this.f$1 = clipAnimationHelper;
        this.f$2 = remoteAnimationTargetSet;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        AppToOverviewAnimationProvider.lambda$createWindowAnimation$1(this.f$0, this.f$1, this.f$2, valueAnimator);
    }
}
