package com.android.systemui.animation;

import android.view.RemoteAnimationAdapter;
import com.android.systemui.animation.ActivityLaunchAnimator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.Nullable;

/* compiled from: ActivityLaunchAnimator.kt */
final class ActivityLaunchAnimator$startPendingIntentWithAnimation$1 extends Lambda implements Function1<RemoteAnimationAdapter, Integer> {
    final /* synthetic */ ActivityLaunchAnimator.PendingIntentStarter $intentStarter;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ActivityLaunchAnimator$startPendingIntentWithAnimation$1(ActivityLaunchAnimator.PendingIntentStarter pendingIntentStarter) {
        super(1);
        this.$intentStarter = pendingIntentStarter;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Integer.valueOf(invoke((RemoteAnimationAdapter) obj));
    }

    public final int invoke(@Nullable RemoteAnimationAdapter remoteAnimationAdapter) {
        return this.$intentStarter.startPendingIntent(remoteAnimationAdapter);
    }
}
