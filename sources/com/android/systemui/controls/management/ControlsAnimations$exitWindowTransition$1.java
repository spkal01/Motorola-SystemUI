package com.android.systemui.controls.management;

import android.animation.Animator;
import android.view.View;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsAnimations.kt */
final class ControlsAnimations$exitWindowTransition$1 extends Lambda implements Function1<View, Animator> {
    public static final ControlsAnimations$exitWindowTransition$1 INSTANCE = new ControlsAnimations$exitWindowTransition$1();

    ControlsAnimations$exitWindowTransition$1() {
        super(1);
    }

    @NotNull
    public final Animator invoke(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "view");
        ControlsAnimations controlsAnimations = ControlsAnimations.INSTANCE;
        return ControlsAnimations.exitAnimation$default(view, (Runnable) null, 2, (Object) null);
    }
}
