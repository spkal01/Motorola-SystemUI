package com.android.systemui.controls.management;

import android.animation.Animator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsAnimations.kt */
public final class WindowTransition extends Transition {
    @NotNull
    private final Function1<View, Animator> animator;

    public WindowTransition(@NotNull Function1<? super View, ? extends Animator> function1) {
        Intrinsics.checkNotNullParameter(function1, "animator");
        this.animator = function1;
    }

    public void captureStartValues(@NotNull TransitionValues transitionValues) {
        Intrinsics.checkNotNullParameter(transitionValues, "tv");
        Map map = transitionValues.values;
        Intrinsics.checkNotNullExpressionValue(map, "tv.values");
        map.put("item", Float.valueOf(0.0f));
    }

    public void captureEndValues(@NotNull TransitionValues transitionValues) {
        Intrinsics.checkNotNullParameter(transitionValues, "tv");
        Map map = transitionValues.values;
        Intrinsics.checkNotNullExpressionValue(map, "tv.values");
        map.put("item", Float.valueOf(1.0f));
    }

    @Nullable
    public Animator createAnimator(@NotNull ViewGroup viewGroup, @Nullable TransitionValues transitionValues, @Nullable TransitionValues transitionValues2) {
        Intrinsics.checkNotNullParameter(viewGroup, "sceneRoot");
        Function1<View, Animator> function1 = this.animator;
        Intrinsics.checkNotNull(transitionValues);
        View view = transitionValues.view;
        Intrinsics.checkNotNullExpressionValue(view, "!!.view");
        return function1.invoke(view);
    }
}
