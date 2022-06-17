package com.motorola.systemui.cli.navgesture.animation;

import android.animation.AnimatorSet;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.animation.OverviewCommandHelper;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationProvider;

/* renamed from: com.motorola.systemui.cli.navgesture.animation.OverviewCommandHelper$RecentsActivityCommand$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2703x67c32af7 implements RemoteAnimationProvider {
    public final /* synthetic */ OverviewCommandHelper.RecentsActivityCommand f$0;

    public /* synthetic */ C2703x67c32af7(OverviewCommandHelper.RecentsActivityCommand recentsActivityCommand) {
        this.f$0 = recentsActivityCommand;
    }

    public final AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
        return this.f$0.createWindowAnimation(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2);
    }
}
