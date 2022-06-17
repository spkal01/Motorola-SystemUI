package com.android.systemui.media;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import org.jetbrains.annotations.Nullable;

/* compiled from: IlluminationDrawable.kt */
public final class IlluminationDrawable$animateBackground$1$2 extends AnimatorListenerAdapter {
    final /* synthetic */ IlluminationDrawable this$0;

    IlluminationDrawable$animateBackground$1$2(IlluminationDrawable illuminationDrawable) {
        this.this$0 = illuminationDrawable;
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        this.this$0.backgroundAnimation = null;
    }
}
