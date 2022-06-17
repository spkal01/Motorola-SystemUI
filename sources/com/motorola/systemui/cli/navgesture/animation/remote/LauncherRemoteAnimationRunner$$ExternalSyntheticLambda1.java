package com.motorola.systemui.cli.navgesture.animation.remote;

import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

public final /* synthetic */ class LauncherRemoteAnimationRunner$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ LauncherRemoteAnimationRunner f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ RemoteAnimationTargetCompat[] f$2;
    public final /* synthetic */ RemoteAnimationTargetCompat[] f$3;

    public /* synthetic */ LauncherRemoteAnimationRunner$$ExternalSyntheticLambda1(LauncherRemoteAnimationRunner launcherRemoteAnimationRunner, Runnable runnable, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
        this.f$0 = launcherRemoteAnimationRunner;
        this.f$1 = runnable;
        this.f$2 = remoteAnimationTargetCompatArr;
        this.f$3 = remoteAnimationTargetCompatArr2;
    }

    public final void run() {
        this.f$0.lambda$onAnimationStart$0(this.f$1, this.f$2, this.f$3);
    }
}
