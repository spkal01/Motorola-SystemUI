package com.motorola.systemui.cli.navgesture.animation.remote;

import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.animation.remote.LauncherRemoteAnimationRunner;
import com.motorola.systemui.cli.navgesture.animation.remote.WrappedAnimationRunnerImpl;
import java.lang.ref.WeakReference;

public class WrappedLauncherRemoteAnimationRunner<R extends WrappedAnimationRunnerImpl> extends LauncherRemoteAnimationRunner {
    private WeakReference<R> mImpl;

    public WrappedLauncherRemoteAnimationRunner(R r, boolean z) {
        super(r.getHandler(), z);
        this.mImpl = new WeakReference<>(r);
    }

    public void onCreateAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, LauncherRemoteAnimationRunner.AnimationResult animationResult) {
        WrappedAnimationRunnerImpl wrappedAnimationRunnerImpl = (WrappedAnimationRunnerImpl) this.mImpl.get();
        if (wrappedAnimationRunnerImpl != null) {
            wrappedAnimationRunnerImpl.onCreateAnimation(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, animationResult);
        }
    }
}
