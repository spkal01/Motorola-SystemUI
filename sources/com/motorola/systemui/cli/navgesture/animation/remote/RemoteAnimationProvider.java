package com.motorola.systemui.cli.navgesture.animation.remote;

import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.os.Handler;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.RemoteAnimationAdapterCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.animation.remote.LauncherRemoteAnimationRunner;

@FunctionalInterface
public interface RemoteAnimationProvider {
    AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2);

    ActivityOptions toActivityOptions(Handler handler, long j) {
        return ActivityOptionsCompat.makeRemoteAnimation(new RemoteAnimationAdapterCompat(new LauncherRemoteAnimationRunner(handler, false) {
            public void onCreateAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, LauncherRemoteAnimationRunner.AnimationResult animationResult) {
                animationResult.setAnimation(RemoteAnimationProvider.this.createWindowAnimation(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2));
            }
        }, j, 0));
    }

    static int getLayer(RemoteAnimationTargetCompat remoteAnimationTargetCompat, int i) {
        if (remoteAnimationTargetCompat.mode == i) {
            return remoteAnimationTargetCompat.prefixOrderIndex + 800570000;
        }
        return remoteAnimationTargetCompat.prefixOrderIndex;
    }
}
