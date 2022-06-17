package com.android.systemui.biometrics;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.PathInterpolator;
import com.android.systemui.statusbar.LightRevealScrim;
import com.android.systemui.statusbar.charging.RippleShader;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: AuthRippleView.kt */
public final class AuthRippleView extends View {
    @NotNull
    private PointF origin = new PointF();
    private float radius;
    /* access modifiers changed from: private */
    public boolean rippleInProgress;
    @NotNull
    private final Paint ripplePaint;
    /* access modifiers changed from: private */
    @NotNull
    public final RippleShader rippleShader;

    /* JADX WARNING: type inference failed for: r2v1, types: [com.android.systemui.statusbar.charging.RippleShader, android.graphics.Shader] */
    public AuthRippleView(@Nullable Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        ? rippleShader2 = new RippleShader();
        this.rippleShader = rippleShader2;
        Paint paint = new Paint();
        this.ripplePaint = paint;
        rippleShader2.setColor(-1);
        rippleShader2.setProgress(0.0f);
        rippleShader2.setSparkleStrength(0.4f);
        paint.setShader(rippleShader2);
        setVisibility(8);
    }

    private final void setRadius(float f) {
        this.rippleShader.setRadius(f);
        this.radius = f;
    }

    private final void setOrigin(PointF pointF) {
        this.rippleShader.setOrigin(pointF);
        this.origin = pointF;
    }

    public final void setSensorLocation(@NotNull PointF pointF) {
        Intrinsics.checkNotNullParameter(pointF, "location");
        setOrigin(pointF);
        setRadius(ComparisonsKt___ComparisonsJvmKt.maxOf(pointF.x, pointF.y, ((float) getWidth()) - pointF.x, ((float) getHeight()) - pointF.y));
    }

    public final void startRipple(@Nullable Runnable runnable, @Nullable LightRevealScrim lightRevealScrim) {
        if (!this.rippleInProgress) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.setInterpolator(new PathInterpolator(0.4f, 0.0f, 0.0f, 1.0f));
            ofFloat.setDuration(1533);
            ofFloat.addUpdateListener(new AuthRippleView$startRipple$rippleAnimator$1$1(this, lightRevealScrim));
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat2.setInterpolator(ofFloat.getInterpolator());
            ofFloat2.setStartDelay(10);
            ofFloat2.setDuration(ofFloat.getDuration());
            ofFloat2.addUpdateListener(new AuthRippleView$startRipple$revealAnimator$1$1(lightRevealScrim));
            ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{0, 127});
            ofInt.setDuration(167);
            ofInt.addUpdateListener(new AuthRippleView$startRipple$alphaInAnimator$1$1(this));
            ValueAnimator ofInt2 = ValueAnimator.ofInt(new int[]{127, 0});
            ofInt2.setStartDelay(417);
            ofInt2.setDuration(1116);
            ofInt2.addUpdateListener(new AuthRippleView$startRipple$alphaOutAnimator$1$1(this));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2, ofInt, ofInt2});
            animatorSet.addListener(new AuthRippleView$startRipple$animatorSet$1$1(this, runnable));
            animatorSet.start();
        }
    }

    public final void setColor(int i) {
        this.rippleShader.setColor(i);
    }

    /* access modifiers changed from: protected */
    public void onDraw(@Nullable Canvas canvas) {
        float f = (float) 1;
        float progress = (f - (((f - this.rippleShader.getProgress()) * (f - this.rippleShader.getProgress())) * (f - this.rippleShader.getProgress()))) * this.radius * 1.5f;
        if (canvas != null) {
            PointF pointF = this.origin;
            canvas.drawCircle(pointF.x, pointF.y, progress, this.ripplePaint);
        }
    }
}
