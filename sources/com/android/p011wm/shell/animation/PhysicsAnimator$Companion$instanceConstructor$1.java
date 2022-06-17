package com.android.p011wm.shell.animation;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.animation.PhysicsAnimator$Companion$instanceConstructor$1 */
/* compiled from: PhysicsAnimator.kt */
/* synthetic */ class PhysicsAnimator$Companion$instanceConstructor$1 extends FunctionReferenceImpl implements Function1<Object, PhysicsAnimator<Object>> {
    public static final PhysicsAnimator$Companion$instanceConstructor$1 INSTANCE = new PhysicsAnimator$Companion$instanceConstructor$1();

    PhysicsAnimator$Companion$instanceConstructor$1() {
        super(1, PhysicsAnimator.class, "<init>", "<init>(Ljava/lang/Object;)V", 0);
    }

    @NotNull
    public final PhysicsAnimator<T> invoke(@NotNull Object obj) {
        Intrinsics.checkNotNullParameter(obj, "p0");
        return new PhysicsAnimator<>(obj, (DefaultConstructorMarker) null);
    }
}
