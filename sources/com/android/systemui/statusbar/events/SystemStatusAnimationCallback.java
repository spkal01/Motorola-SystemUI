package com.android.systemui.statusbar.events;

import android.animation.Animator;
import android.animation.ValueAnimator;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SystemStatusAnimationScheduler.kt */
public interface SystemStatusAnimationCallback {
    @Nullable
    Animator onHidePersistentDot() {
        return null;
    }

    void onSystemChromeAnimationEnd() {
    }

    void onSystemChromeAnimationStart() {
    }

    void onSystemChromeAnimationUpdate(@NotNull ValueAnimator valueAnimator) {
        Intrinsics.checkNotNullParameter(valueAnimator, "animator");
    }

    @Nullable
    Animator onSystemStatusAnimationTransitionToPersistentDot(@Nullable String str) {
        return null;
    }
}
