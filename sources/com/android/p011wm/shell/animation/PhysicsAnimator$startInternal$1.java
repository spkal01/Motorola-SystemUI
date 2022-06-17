package com.android.p011wm.shell.animation;

import androidx.dynamicanimation.animation.AnimationHandler;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import com.android.p011wm.shell.animation.PhysicsAnimator;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.wm.shell.animation.PhysicsAnimator$startInternal$1 */
/* compiled from: PhysicsAnimator.kt */
final class PhysicsAnimator$startInternal$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ FloatPropertyCompat<? super T> $animatedProperty;
    final /* synthetic */ float $currentValue;
    final /* synthetic */ PhysicsAnimator.FlingConfig $flingConfig;
    final /* synthetic */ T $target;
    final /* synthetic */ PhysicsAnimator<T> this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    PhysicsAnimator$startInternal$1(PhysicsAnimator.FlingConfig flingConfig, PhysicsAnimator<T> physicsAnimator, FloatPropertyCompat<? super T> floatPropertyCompat, T t, float f) {
        super(0);
        this.$flingConfig = flingConfig;
        this.this$0 = physicsAnimator;
        this.$animatedProperty = floatPropertyCompat;
        this.$target = t;
        this.$currentValue = f;
    }

    public final void invoke() {
        PhysicsAnimator.FlingConfig flingConfig = this.$flingConfig;
        float f = this.$currentValue;
        flingConfig.setMin(Math.min(f, flingConfig.getMin()));
        flingConfig.setMax(Math.max(f, flingConfig.getMax()));
        this.this$0.cancel(this.$animatedProperty);
        FlingAnimation access$getFlingAnimation = this.this$0.getFlingAnimation(this.$animatedProperty, this.$target);
        AnimationHandler access$getCustomAnimationHandler$p = this.this$0.customAnimationHandler;
        if (access$getCustomAnimationHandler$p == null) {
            access$getCustomAnimationHandler$p = access$getFlingAnimation.getAnimationHandler();
            Intrinsics.checkNotNullExpressionValue(access$getCustomAnimationHandler$p, "flingAnim.animationHandler");
        }
        access$getFlingAnimation.setAnimationHandler(access$getCustomAnimationHandler$p);
        this.$flingConfig.mo25490xe3030f23(access$getFlingAnimation);
        access$getFlingAnimation.start();
    }
}
