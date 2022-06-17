package com.android.p011wm.shell.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import com.android.p011wm.shell.animation.PhysicsAnimator;
import java.util.ArrayList;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.animation.PhysicsAnimator$configureDynamicAnimation$2 */
/* compiled from: PhysicsAnimator.kt */
final class PhysicsAnimator$configureDynamicAnimation$2 implements DynamicAnimation.OnAnimationEndListener {
    final /* synthetic */ DynamicAnimation<?> $anim;
    final /* synthetic */ FloatPropertyCompat<? super T> $property;
    final /* synthetic */ PhysicsAnimator<T> this$0;

    PhysicsAnimator$configureDynamicAnimation$2(PhysicsAnimator<T> physicsAnimator, FloatPropertyCompat<? super T> floatPropertyCompat, DynamicAnimation<?> dynamicAnimation) {
        this.this$0 = physicsAnimator;
        this.$property = floatPropertyCompat;
        this.$anim = dynamicAnimation;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        ArrayList<PhysicsAnimator<T>.InternalListener> internalListeners$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell = this.this$0.mo25469x79845134();
        final FloatPropertyCompat<? super T> floatPropertyCompat = this.$property;
        final DynamicAnimation<?> dynamicAnimation2 = this.$anim;
        final boolean z2 = z;
        final float f3 = f;
        final float f4 = f2;
        boolean unused = CollectionsKt__MutableCollectionsKt.removeAll(internalListeners$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell, new Function1<PhysicsAnimator<T>.InternalListener, Boolean>() {
            public /* bridge */ /* synthetic */ Object invoke(Object obj) {
                return Boolean.valueOf(invoke((PhysicsAnimator<T>.InternalListener) (PhysicsAnimator.InternalListener) obj));
            }

            public final boolean invoke(@NotNull PhysicsAnimator<T>.InternalListener internalListener) {
                Intrinsics.checkNotNullParameter(internalListener, "it");
                return internalListener.mo25502xf6e998bb(floatPropertyCompat, z2, f3, f4, dynamicAnimation2 instanceof FlingAnimation);
            }
        });
        if (Intrinsics.areEqual(this.this$0.springAnimations.get(this.$property), (Object) this.$anim)) {
            this.this$0.springAnimations.remove(this.$property);
        }
        if (Intrinsics.areEqual(this.this$0.flingAnimations.get(this.$property), (Object) this.$anim)) {
            this.this$0.flingAnimations.remove(this.$property);
        }
    }
}
