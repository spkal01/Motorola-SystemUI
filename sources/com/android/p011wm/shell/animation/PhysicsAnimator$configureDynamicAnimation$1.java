package com.android.p011wm.shell.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;

/* renamed from: com.android.wm.shell.animation.PhysicsAnimator$configureDynamicAnimation$1 */
/* compiled from: PhysicsAnimator.kt */
final class PhysicsAnimator$configureDynamicAnimation$1 implements DynamicAnimation.OnAnimationUpdateListener {
    final /* synthetic */ FloatPropertyCompat<? super T> $property;
    final /* synthetic */ PhysicsAnimator<T> this$0;

    PhysicsAnimator$configureDynamicAnimation$1(PhysicsAnimator<T> physicsAnimator, FloatPropertyCompat<? super T> floatPropertyCompat) {
        this.this$0 = physicsAnimator;
        this.$property = floatPropertyCompat;
    }

    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
        int size = this.this$0.mo25469x79845134().size();
        if (size > 0) {
            int i = 0;
            while (true) {
                int i2 = i + 1;
                this.this$0.mo25469x79845134().get(i).mo25503x996316b9(this.$property, f, f2);
                if (i2 < size) {
                    i = i2;
                } else {
                    return;
                }
            }
        }
    }
}
