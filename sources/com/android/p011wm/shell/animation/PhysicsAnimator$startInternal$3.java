package com.android.p011wm.shell.animation;

import androidx.dynamicanimation.animation.AnimationHandler;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.android.p011wm.shell.animation.PhysicsAnimator;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.animation.PhysicsAnimator$startInternal$3 */
/* compiled from: PhysicsAnimator.kt */
public final class PhysicsAnimator$startInternal$3 implements PhysicsAnimator.EndListener<T> {
    final /* synthetic */ FloatPropertyCompat<? super T> $animatedProperty;
    final /* synthetic */ float $flingMax;
    final /* synthetic */ float $flingMin;
    final /* synthetic */ PhysicsAnimator.SpringConfig $springConfig;
    final /* synthetic */ PhysicsAnimator<T> this$0;

    PhysicsAnimator$startInternal$3(FloatPropertyCompat<? super T> floatPropertyCompat, float f, float f2, PhysicsAnimator.SpringConfig springConfig, PhysicsAnimator<T> physicsAnimator) {
        this.$animatedProperty = floatPropertyCompat;
        this.$flingMin = f;
        this.$flingMax = f2;
        this.$springConfig = springConfig;
        this.this$0 = physicsAnimator;
    }

    public void onAnimationEnd(T t, @NotNull FloatPropertyCompat<? super T> floatPropertyCompat, boolean z, boolean z2, float f, float f2, boolean z3) {
        Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
        if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) this.$animatedProperty) && z && !z2) {
            boolean z4 = true;
            boolean z5 = Math.abs(f2) > 0.0f;
            boolean z6 = !(this.$flingMin <= f && f <= this.$flingMax);
            if (z5 || z6) {
                this.$springConfig.mo25512x3e6fa5c5(f2);
                if (this.$springConfig.mo25508x5f9681b1() != PhysicsAnimatorKt.UNSET) {
                    z4 = false;
                }
                if (z4) {
                    if (z5) {
                        this.$springConfig.mo25511xb5ce1325(f2 < 0.0f ? this.$flingMin : this.$flingMax);
                    } else if (z6) {
                        PhysicsAnimator.SpringConfig springConfig = this.$springConfig;
                        float f3 = this.$flingMin;
                        if (f >= f3) {
                            f3 = this.$flingMax;
                        }
                        springConfig.mo25511xb5ce1325(f3);
                    }
                }
                SpringAnimation access$getSpringAnimation = this.this$0.getSpringAnimation(this.$animatedProperty, t);
                AnimationHandler access$getCustomAnimationHandler$p = this.this$0.customAnimationHandler;
                if (access$getCustomAnimationHandler$p == null) {
                    access$getCustomAnimationHandler$p = access$getSpringAnimation.getAnimationHandler();
                    Intrinsics.checkNotNullExpressionValue(access$getCustomAnimationHandler$p, "springAnim.animationHandler");
                }
                access$getSpringAnimation.setAnimationHandler(access$getCustomAnimationHandler$p);
                this.$springConfig.mo25504xe3030f23(access$getSpringAnimation);
                access$getSpringAnimation.start();
            }
        }
    }
}
