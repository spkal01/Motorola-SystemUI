package com.motorola.systemui.cli.navgesture.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.FloatProperty;

public class AnimatedFloat {
    public static FloatProperty<AnimatedFloat> VALUE = new FloatProperty<AnimatedFloat>("value") {
        public void setValue(AnimatedFloat animatedFloat, float f) {
            animatedFloat.updateValue(f);
        }

        public Float get(AnimatedFloat animatedFloat) {
            return Float.valueOf(animatedFloat.value);
        }
    };
    private final Runnable mUpdateCallback;
    /* access modifiers changed from: private */
    public ObjectAnimator mValueAnimator;
    public float value;

    public AnimatedFloat(Runnable runnable) {
        this.mUpdateCallback = runnable;
    }

    public ObjectAnimator animateToValue(float f, float f2) {
        cancelAnimation();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, VALUE, new float[]{f, f2});
        this.mValueAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (AnimatedFloat.this.mValueAnimator == animator) {
                    ObjectAnimator unused = AnimatedFloat.this.mValueAnimator = null;
                }
            }
        });
        return this.mValueAnimator;
    }

    public void updateValue(float f) {
        if (Float.compare(f, this.value) != 0) {
            this.value = f;
            this.mUpdateCallback.run();
        }
    }

    public void cancelAnimation() {
        ObjectAnimator objectAnimator = this.mValueAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }
}
