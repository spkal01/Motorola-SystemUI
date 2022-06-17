package com.motorola.systemui.cli.navgesture.animation.remote;

import android.os.Handler;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.animation.remote.LauncherRemoteAnimationRunner;

public interface WrappedAnimationRunnerImpl {
    Handler getHandler();

    void onCreateAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, LauncherRemoteAnimationRunner.AnimationResult animationResult);
}
