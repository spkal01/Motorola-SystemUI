package com.android.p011wm.shell.bubbles.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;

/* renamed from: com.android.wm.shell.bubbles.animation.OneTimeEndListener */
public class OneTimeEndListener implements DynamicAnimation.OnAnimationEndListener {
    public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        dynamicAnimation.removeEndListener(this);
    }
}
