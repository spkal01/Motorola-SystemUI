package com.motorola.systemui.cli.navgesture;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.CancellationSignal;
import android.os.Handler;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.animation.GestureRecentsAppTransitionManager;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationProvider;
import java.util.function.BiPredicate;

public class LauncherInitListener extends ActivityInitListener<AbstractRecentGestureLauncher> {
    private final BiPredicate<AbstractRecentGestureLauncher, Boolean> mOnInitListener;
    private RemoteAnimationProvider mRemoteAnimationProvider;

    public LauncherInitListener(BiPredicate<AbstractRecentGestureLauncher, Boolean> biPredicate) {
        super(biPredicate, AbstractRecentGestureLauncher.ACTIVITY_TRACKER);
        this.mOnInitListener = biPredicate;
    }

    /* access modifiers changed from: protected */
    public boolean handleInit(AbstractRecentGestureLauncher abstractRecentGestureLauncher, boolean z) {
        if (this.mRemoteAnimationProvider != null) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            ((GestureRecentsAppTransitionManager) abstractRecentGestureLauncher.getAppTransitionManager()).setRemoteAnimationProvider(new LauncherInitListener$$ExternalSyntheticLambda0(this, cancellationSignal, abstractRecentGestureLauncher), cancellationSignal);
        }
        return this.mOnInitListener.test(abstractRecentGestureLauncher, Boolean.valueOf(z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ AnimatorSet lambda$handleInit$0(CancellationSignal cancellationSignal, AbstractRecentGestureLauncher abstractRecentGestureLauncher, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
        cancellationSignal.cancel();
        RemoteAnimationProvider remoteAnimationProvider = this.mRemoteAnimationProvider;
        this.mRemoteAnimationProvider = null;
        if (remoteAnimationProvider == null || !abstractRecentGestureLauncher.getStateManager().getState().overview()) {
            return null;
        }
        return remoteAnimationProvider.createWindowAnimation(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2);
    }

    public void unregister() {
        this.mRemoteAnimationProvider = null;
        super.unregister();
    }

    public void registerAndStartActivity(Intent intent, RemoteAnimationProvider remoteAnimationProvider, Context context, Handler handler, long j) {
        this.mRemoteAnimationProvider = remoteAnimationProvider;
        super.registerAndStartActivity(intent, remoteAnimationProvider, context, handler, j);
    }
}
