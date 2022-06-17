package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.android.systemui.statusbar.LightRevealScrim;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;

/* compiled from: UnlockedScreenOffAnimationController.kt */
public final class UnlockedScreenOffAnimationController$lightRevealAnimator$1$2 extends AnimatorListenerAdapter {
    final /* synthetic */ UnlockedScreenOffAnimationController this$0;

    UnlockedScreenOffAnimationController$lightRevealAnimator$1$2(UnlockedScreenOffAnimationController unlockedScreenOffAnimationController) {
        this.this$0 = unlockedScreenOffAnimationController;
    }

    public void onAnimationCancel(@Nullable Animator animator) {
        LightRevealScrim access$getLightRevealScrim$p = this.this$0.lightRevealScrim;
        if (access$getLightRevealScrim$p != null) {
            access$getLightRevealScrim$p.setRevealAmount(1.0f);
            this.this$0.lightRevealAnimationPlaying = false;
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("lightRevealScrim");
        throw null;
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        this.this$0.lightRevealAnimationPlaying = false;
    }
}
