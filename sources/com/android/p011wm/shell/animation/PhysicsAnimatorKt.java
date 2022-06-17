package com.android.p011wm.shell.animation;

import android.view.View;
import com.android.p011wm.shell.animation.PhysicsAnimator;
import java.util.WeakHashMap;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.animation.PhysicsAnimatorKt */
/* compiled from: PhysicsAnimator.kt */
public final class PhysicsAnimatorKt {
    /* access modifiers changed from: private */
    public static final float UNSET = -3.4028235E38f;
    @NotNull
    private static final WeakHashMap<Object, PhysicsAnimator<?>> animators = new WeakHashMap<>();
    /* access modifiers changed from: private */
    @NotNull
    public static final PhysicsAnimator.FlingConfig globalDefaultFling = new PhysicsAnimator.FlingConfig(1.0f, UNSET, Float.MAX_VALUE);
    /* access modifiers changed from: private */
    @NotNull
    public static final PhysicsAnimator.SpringConfig globalDefaultSpring = new PhysicsAnimator.SpringConfig(1500.0f, 0.5f);
    /* access modifiers changed from: private */
    public static boolean verboseLogging;

    @NotNull
    public static final <T extends View> PhysicsAnimator<T> getPhysicsAnimator(@NotNull T t) {
        Intrinsics.checkNotNullParameter(t, "<this>");
        return PhysicsAnimator.Companion.getInstance(t);
    }

    @NotNull
    public static final WeakHashMap<Object, PhysicsAnimator<?>> getAnimators() {
        return animators;
    }
}
