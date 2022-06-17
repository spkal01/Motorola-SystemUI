package com.motorola.systemui.cli.navgesture;

import android.graphics.Path;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;

public class Interpolators {
    public static final Interpolator ACCEL = new AccelerateInterpolator();
    public static final Interpolator ACCEL_1_5 = new AccelerateInterpolator(1.5f);
    public static final Interpolator ACCEL_2 = new AccelerateInterpolator(2.0f);
    public static final Interpolator ACCEL_DEACCEL = new AccelerateDecelerateInterpolator();
    public static final Interpolator AGGRESSIVE_EASE = new PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f);
    public static final Interpolator AGGRESSIVE_EASE_IN_OUT = new PathInterpolator(0.6f, 0.0f, 0.4f, 1.0f);
    public static final Interpolator ALPHA_IN = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator ALPHA_OUT = new PathInterpolator(0.0f, 0.0f, 0.8f, 1.0f);
    public static final Interpolator DEACCEL = new DecelerateInterpolator();
    public static final Interpolator DEACCEL_1_5 = new DecelerateInterpolator(1.5f);
    public static final Interpolator DEACCEL_1_7 = new DecelerateInterpolator(1.7f);
    public static final Interpolator DEACCEL_2 = new DecelerateInterpolator(2.0f);
    public static final Interpolator DEACCEL_3 = new DecelerateInterpolator(3.0f);
    public static final Interpolator EXAGGERATED_EASE;
    public static final Interpolator FAST_OUT_LINEAR_IN = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator INSTANT = Interpolators$$ExternalSyntheticLambda3.INSTANCE;
    public static final Interpolator LINEAR = new LinearInterpolator();
    public static final Interpolator LINEAR_OUT_SLOW_IN = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator SCROLL = Interpolators$$ExternalSyntheticLambda2.INSTANCE;
    public static final Interpolator TOUCH_RESPONSE_INTERPOLATOR = new PathInterpolator(0.3f, 0.0f, 0.1f, 1.0f);
    public static final Interpolator ZOOM_IN = new Interpolator() {
        public float getInterpolation(float f) {
            return Interpolators.DEACCEL_3.getInterpolation(1.0f - Interpolators.ZOOM_OUT.getInterpolation(1.0f - f));
        }
    };
    public static final Interpolator ZOOM_OUT = new Interpolator() {
        private float zInterpolate(float f) {
            return (1.0f - (0.35f / (f + 0.35f))) / 0.7407408f;
        }

        public float getInterpolation(float f) {
            return zInterpolate(f);
        }
    };

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$static$0(float f) {
        return 1.0f;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$static$1(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    static {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        Path path2 = path;
        path2.cubicTo(0.05f, 0.0f, 0.133333f, 0.08f, 0.166666f, 0.4f);
        path2.cubicTo(0.225f, 0.94f, 0.5f, 1.0f, 1.0f, 1.0f);
        EXAGGERATED_EASE = new PathInterpolator(path);
    }

    public static Interpolator clampToProgress(Interpolator interpolator, float f, float f2) {
        if (f2 > f) {
            return new Interpolators$$ExternalSyntheticLambda0(f, f2, interpolator);
        }
        throw new IllegalArgumentException("lowerBound must be less than upperBound");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$clampToProgress$2(float f, float f2, Interpolator interpolator, float f3) {
        if (f3 < f) {
            return 0.0f;
        }
        if (f3 > f2) {
            return 1.0f;
        }
        return interpolator.getInterpolation((f3 - f) / (f2 - f));
    }

    public static Interpolator mapToProgress(Interpolator interpolator, float f, float f2) {
        return new Interpolators$$ExternalSyntheticLambda1(interpolator, f, f2);
    }
}
