package com.android.systemui.controls.p004ui;

import android.os.VibrationEffect;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.Vibrations */
/* compiled from: Vibrations.kt */
public final class Vibrations {
    @NotNull
    public static final Vibrations INSTANCE;
    @NotNull
    private static final VibrationEffect rangeEdgeEffect;
    @NotNull
    private static final VibrationEffect rangeMiddleEffect;

    private Vibrations() {
    }

    static {
        Vibrations vibrations = new Vibrations();
        INSTANCE = vibrations;
        rangeEdgeEffect = vibrations.initRangeEdgeEffect();
        rangeMiddleEffect = vibrations.initRangeMiddleEffect();
    }

    @NotNull
    public final VibrationEffect getRangeEdgeEffect() {
        return rangeEdgeEffect;
    }

    @NotNull
    public final VibrationEffect getRangeMiddleEffect() {
        return rangeMiddleEffect;
    }

    private final VibrationEffect initRangeEdgeEffect() {
        VibrationEffect.Composition startComposition = VibrationEffect.startComposition();
        startComposition.addPrimitive(7, 0.5f);
        VibrationEffect compose = startComposition.compose();
        Intrinsics.checkNotNullExpressionValue(compose, "composition.compose()");
        return compose;
    }

    private final VibrationEffect initRangeMiddleEffect() {
        VibrationEffect.Composition startComposition = VibrationEffect.startComposition();
        startComposition.addPrimitive(7, 0.1f);
        VibrationEffect compose = startComposition.compose();
        Intrinsics.checkNotNullExpressionValue(compose, "composition.compose()");
        return compose;
    }
}
