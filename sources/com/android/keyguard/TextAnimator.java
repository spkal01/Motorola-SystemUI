package com.android.keyguard;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout;
import android.util.SparseArray;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: TextAnimator.kt */
public final class TextAnimator {
    @NotNull
    private ValueAnimator animator;
    /* access modifiers changed from: private */
    @NotNull
    public final Function0<Unit> invalidateCallback;
    @NotNull
    private TextInterpolator textInterpolator;
    @NotNull
    private final SparseArray<Typeface> typefaceCache = new SparseArray<>();

    public TextAnimator(@NotNull Layout layout, @NotNull Function0<Unit> function0) {
        Intrinsics.checkNotNullParameter(layout, "layout");
        Intrinsics.checkNotNullParameter(function0, "invalidateCallback");
        this.invalidateCallback = function0;
        this.textInterpolator = new TextInterpolator(layout);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f});
        ofFloat.setDuration(300);
        ofFloat.addUpdateListener(new TextAnimator$animator$1$1(this));
        ofFloat.addListener(new TextAnimator$animator$1$2(this));
        Unit unit = Unit.INSTANCE;
        Intrinsics.checkNotNullExpressionValue(ofFloat, "ofFloat(1f).apply {\n        duration = DEFAULT_ANIMATION_DURATION\n        addUpdateListener {\n            textInterpolator.progress = it.animatedValue as Float\n            invalidateCallback()\n        }\n        addListener(object : AnimatorListenerAdapter() {\n            override fun onAnimationEnd(animation: Animator?) {\n                textInterpolator.rebase()\n            }\n            override fun onAnimationCancel(animation: Animator?) = textInterpolator.rebase()\n        })\n    }");
        this.animator = ofFloat;
    }

    @NotNull
    /* renamed from: getTextInterpolator$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final TextInterpolator mo9921x92092a50() {
        return this.textInterpolator;
    }

    @NotNull
    /* renamed from: getAnimator$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final ValueAnimator mo9920x2f3d2b23() {
        return this.animator;
    }

    public final void updateLayout(@NotNull Layout layout) {
        Intrinsics.checkNotNullParameter(layout, "layout");
        this.textInterpolator.setLayout(layout);
    }

    public final boolean isRunning() {
        return this.animator.isRunning();
    }

    public final void draw(@NotNull Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "c");
        this.textInterpolator.draw(canvas);
    }

    public final void setTextStyle(int i, float f, @Nullable Integer num, boolean z, long j, @Nullable TimeInterpolator timeInterpolator, long j2, @Nullable Runnable runnable) {
        if (z) {
            this.animator.cancel();
            this.textInterpolator.rebase();
        }
        if (f >= 0.0f) {
            this.textInterpolator.getTargetPaint().setTextSize(f);
        }
        if (i >= 0) {
            this.textInterpolator.getTargetPaint().setTypeface((Typeface) TextAnimatorKt.getOrElse(this.typefaceCache, i, new TextAnimator$setTextStyle$1(this, i)));
        }
        if (num != null) {
            this.textInterpolator.getTargetPaint().setColor(num.intValue());
        }
        this.textInterpolator.onTargetPaintModified();
        if (z) {
            this.animator.setStartDelay(j2);
            ValueAnimator valueAnimator = this.animator;
            if (j == -1) {
                j = 300;
            }
            valueAnimator.setDuration(j);
            if (timeInterpolator != null) {
                mo9920x2f3d2b23().setInterpolator(timeInterpolator);
            }
            if (runnable != null) {
                this.animator.addListener(new TextAnimator$setTextStyle$listener$1(runnable, this));
            }
            this.animator.start();
            return;
        }
        this.textInterpolator.setProgress(1.0f);
        this.textInterpolator.rebase();
    }
}
