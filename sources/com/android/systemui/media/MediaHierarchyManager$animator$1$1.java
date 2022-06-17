package com.android.systemui.media;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.util.MathUtils;

/* compiled from: MediaHierarchyManager.kt */
final class MediaHierarchyManager$animator$1$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ ValueAnimator $this_apply;
    final /* synthetic */ MediaHierarchyManager this$0;

    MediaHierarchyManager$animator$1$1(MediaHierarchyManager mediaHierarchyManager, ValueAnimator valueAnimator) {
        this.this$0 = mediaHierarchyManager;
        this.$this_apply = valueAnimator;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        float f;
        this.this$0.updateTargetState();
        float animatedFraction = this.$this_apply.getAnimatedFraction();
        if (this.this$0.isCrossFadeAnimatorRunning) {
            MediaHierarchyManager mediaHierarchyManager = this.this$0;
            mediaHierarchyManager.animationCrossFadeProgress = MathUtils.lerp(mediaHierarchyManager.animationStartCrossFadeProgress, 1.0f, this.$this_apply.getAnimatedFraction());
            animatedFraction = this.this$0.animationCrossFadeProgress < 0.5f ? 0.0f : 1.0f;
            MediaHierarchyManager mediaHierarchyManager2 = this.this$0;
            f = mediaHierarchyManager2.calculateAlphaFromCrossFade(mediaHierarchyManager2.animationCrossFadeProgress, false);
        } else {
            f = MathUtils.lerp(this.this$0.animationStartAlpha, 1.0f, this.$this_apply.getAnimatedFraction());
        }
        float f2 = f;
        MediaHierarchyManager mediaHierarchyManager3 = this.this$0;
        Rect unused = mediaHierarchyManager3.interpolateBounds(mediaHierarchyManager3.animationStartBounds, this.this$0.targetBounds, animatedFraction, this.this$0.currentBounds);
        MediaHierarchyManager mediaHierarchyManager4 = this.this$0;
        MediaHierarchyManager.applyState$default(mediaHierarchyManager4, mediaHierarchyManager4.currentBounds, f2, false, 4, (Object) null);
    }
}
