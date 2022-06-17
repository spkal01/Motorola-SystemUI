package com.android.systemui.toast;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ToastDefaultAnimation.kt */
public final class ToastDefaultAnimation {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);

    /* compiled from: ToastDefaultAnimation.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @Nullable
        public final AnimatorSet toastIn(@NotNull View view) {
            Intrinsics.checkNotNullParameter(view, "view");
            View findViewById = view.findViewById(R$id.icon);
            View findViewById2 = view.findViewById(R$id.text);
            if (findViewById == null || findViewById2 == null) {
                return null;
            }
            LinearInterpolator linearInterpolator = new LinearInterpolator();
            PathInterpolator pathInterpolator = new PathInterpolator(0.0f, 0.0f, 0.0f, 1.0f);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "scaleX", new float[]{0.9f, 1.0f});
            ofFloat.setInterpolator(pathInterpolator);
            ofFloat.setDuration(333);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, "scaleY", new float[]{0.9f, 1.0f});
            ofFloat2.setInterpolator(pathInterpolator);
            ofFloat2.setDuration(333);
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(view, "alpha", new float[]{0.0f, 1.0f});
            ofFloat3.setInterpolator(linearInterpolator);
            ofFloat3.setDuration(66);
            findViewById2.setAlpha(0.0f);
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(findViewById2, "alpha", new float[]{0.0f, 1.0f});
            ofFloat4.setInterpolator(linearInterpolator);
            ofFloat4.setDuration(283);
            ofFloat4.setStartDelay(50);
            findViewById.setAlpha(0.0f);
            ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(findViewById, "alpha", new float[]{0.0f, 1.0f});
            ofFloat5.setInterpolator(linearInterpolator);
            ofFloat5.setDuration(283);
            ofFloat5.setStartDelay(50);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2, ofFloat3, ofFloat4, ofFloat5});
            return animatorSet;
        }

        @Nullable
        public final AnimatorSet toastOut(@NotNull View view) {
            Intrinsics.checkNotNullParameter(view, "view");
            View findViewById = view.findViewById(R$id.icon);
            View findViewById2 = view.findViewById(R$id.text);
            if (findViewById == null || findViewById2 == null) {
                return null;
            }
            LinearInterpolator linearInterpolator = new LinearInterpolator();
            PathInterpolator pathInterpolator = new PathInterpolator(0.3f, 0.0f, 1.0f, 1.0f);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "scaleX", new float[]{1.0f, 0.9f});
            ofFloat.setInterpolator(pathInterpolator);
            ofFloat.setDuration(250);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, "scaleY", new float[]{1.0f, 0.9f});
            ofFloat2.setInterpolator(pathInterpolator);
            ofFloat2.setDuration(250);
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(view, "elevation", new float[]{view.getElevation(), 0.0f});
            ofFloat3.setInterpolator(linearInterpolator);
            ofFloat3.setDuration(40);
            ofFloat3.setStartDelay(150);
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(view, "alpha", new float[]{1.0f, 0.0f});
            ofFloat4.setInterpolator(linearInterpolator);
            ofFloat4.setDuration(100);
            ofFloat4.setStartDelay(150);
            ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(findViewById2, "alpha", new float[]{1.0f, 0.0f});
            ofFloat5.setInterpolator(linearInterpolator);
            ofFloat5.setDuration(166);
            ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(findViewById, "alpha", new float[]{1.0f, 0.0f});
            ofFloat6.setInterpolator(linearInterpolator);
            ofFloat6.setDuration(166);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2, ofFloat3, ofFloat4, ofFloat5, ofFloat6});
            return animatorSet;
        }
    }
}
