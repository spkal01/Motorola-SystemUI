package com.motorola.systemui.cli.navgesture.animation;

import android.animation.ValueAnimator;
import com.android.systemui.shared.system.TransactionCompat;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationTargetSet;

public final /* synthetic */ class AppToOverviewAnimationProvider$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ RemoteAnimationTargetSet f$0;
    public final /* synthetic */ TransactionCompat f$1;

    public /* synthetic */ AppToOverviewAnimationProvider$$ExternalSyntheticLambda0(RemoteAnimationTargetSet remoteAnimationTargetSet, TransactionCompat transactionCompat) {
        this.f$0 = remoteAnimationTargetSet;
        this.f$1 = transactionCompat;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        AppToOverviewAnimationProvider.lambda$createWindowAnimation$2(this.f$0, this.f$1, valueAnimator);
    }
}
