package com.motorola.systemui.cli.navgesture.animation;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.util.Property;

public class PropertySetter {
    public static final PropertySetter NO_ANIM_PROPERTY_SETTER = new PropertySetter();

    public <T> void setFloat(T t, Property<T, Float> property, float f, TimeInterpolator timeInterpolator) {
        property.set(t, Float.valueOf(f));
    }

    public static class AnimatedPropertySetter extends PropertySetter {
        private long mDuration;
        private AnimatorSetBuilder mStateAnimator;

        public AnimatedPropertySetter(long j, AnimatorSetBuilder animatorSetBuilder) {
            this.mDuration = j;
            this.mStateAnimator = animatorSetBuilder;
        }

        public <T> void setFloat(T t, Property<T, Float> property, float f, TimeInterpolator timeInterpolator) {
            if (property.get(t).floatValue() != f) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(t, property, new float[]{f});
                ofFloat.setDuration(this.mDuration).setInterpolator(timeInterpolator);
                this.mStateAnimator.play(ofFloat);
            }
        }
    }
}
