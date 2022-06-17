package com.motorola.systemui.cli.navgesture;

import android.animation.AnimatorSet;
import android.os.CancellationSignal;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationProvider;

public final /* synthetic */ class LauncherInitListener$$ExternalSyntheticLambda0 implements RemoteAnimationProvider {
    public final /* synthetic */ LauncherInitListener f$0;
    public final /* synthetic */ CancellationSignal f$1;
    public final /* synthetic */ AbstractRecentGestureLauncher f$2;

    public /* synthetic */ LauncherInitListener$$ExternalSyntheticLambda0(LauncherInitListener launcherInitListener, CancellationSignal cancellationSignal, AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
        this.f$0 = launcherInitListener;
        this.f$1 = cancellationSignal;
        this.f$2 = abstractRecentGestureLauncher;
    }

    public final AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
        return this.f$0.lambda$handleInit$0(this.f$1, this.f$2, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2);
    }
}
