package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.p011wm.shell.animation.Interpolators;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;

public class TapAgainView extends TextView {
    public TapAgainView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        updateColor();
    }

    /* access modifiers changed from: package-private */
    public void updateColor() {
        setTextColor(getResources().getColor(R$color.notif_pill_text, this.mContext.getTheme()));
        setBackground(getResources().getDrawable(R$drawable.rounded_bg_full, this.mContext.getTheme()));
    }

    public void animateIn() {
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.keyguard_indication_y_translation);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{1.0f});
        ofFloat.setStartDelay(150);
        ofFloat.setDuration(317);
        ofFloat.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) dimensionPixelSize, 0.0f});
        ofFloat2.setDuration(600);
        ofFloat2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animator) {
                TapAgainView.this.setTranslationY(0.0f);
            }
        });
        animatorSet.playTogether(new Animator[]{ofFloat2, ofFloat});
        animatorSet.start();
        setVisibility(0);
    }

    public void animateOut() {
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.keyguard_indication_y_translation);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f});
        ofFloat.setDuration(167);
        ofFloat.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{0.0f, (float) (-dimensionPixelSize)});
        ofFloat2.setDuration(167);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                TapAgainView.this.setVisibility(8);
            }

            public void onAnimationCancel(Animator animator) {
                TapAgainView.this.setVisibility(8);
            }
        });
        animatorSet.playTogether(new Animator[]{ofFloat2, ofFloat});
        animatorSet.start();
    }
}
