package com.android.p011wm.shell.animation;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
import java.util.Set;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.animation.PhysicsAnimator$cancelAction$1 */
/* compiled from: PhysicsAnimator.kt */
/* synthetic */ class PhysicsAnimator$cancelAction$1 extends FunctionReferenceImpl implements Function1<Set<? extends FloatPropertyCompat<? super T>>, Unit> {
    PhysicsAnimator$cancelAction$1(PhysicsAnimator<T> physicsAnimator) {
        super(1, physicsAnimator, PhysicsAnimator.class, "cancelInternal", "cancelInternal$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell(Ljava/util/Set;)V", 0);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Set) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(@NotNull Set<? extends FloatPropertyCompat<? super T>> set) {
        Intrinsics.checkNotNullParameter(set, "p0");
        ((PhysicsAnimator) this.receiver).mo25465xf11df47f(set);
    }
}
