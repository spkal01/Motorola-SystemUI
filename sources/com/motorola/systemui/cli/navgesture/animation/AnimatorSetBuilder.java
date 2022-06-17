package com.motorola.systemui.cli.navgesture.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import java.util.ArrayList;
import java.util.List;

public class AnimatorSetBuilder {
    private final ArrayList<Animator> mAnimators = new ArrayList<>();
    /* access modifiers changed from: private */
    public List<Runnable> mOnFinishRunnableList = new ArrayList();

    public void play(Animator animator) {
        this.mAnimators.add(animator);
    }

    public void addOnFinishRunnable(Runnable runnable) {
        this.mOnFinishRunnableList.add(runnable);
    }

    public AnimatorSet build() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(this.mAnimators);
        if (!this.mOnFinishRunnableList.isEmpty()) {
            animatorSet.addListener(new AnimationSuccessListener() {
                public void onAnimationSuccess(Animator animator) {
                    for (Runnable run : AnimatorSetBuilder.this.mOnFinishRunnableList) {
                        run.run();
                    }
                    AnimatorSetBuilder.this.mOnFinishRunnableList.clear();
                }
            });
        }
        return animatorSet;
    }
}
