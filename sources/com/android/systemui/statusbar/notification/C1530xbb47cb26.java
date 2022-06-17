package com.android.systemui.statusbar.notification;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$id;
import java.util.Objects;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.statusbar.notification.ViewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$animator$1$1 */
/* compiled from: ViewGroupFadeHelper.kt */
final class C1530xbb47cb26 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ ViewGroup $root;
    final /* synthetic */ Set<View> $viewsToFadeOut;

    C1530xbb47cb26(ViewGroup viewGroup, Set<View> set) {
        this.$root = viewGroup;
        this.$viewsToFadeOut = set;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        Float f = (Float) this.$root.getTag(R$id.view_group_fade_helper_previous_value_tag);
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        float floatValue = ((Float) animatedValue).floatValue();
        for (View next : this.$viewsToFadeOut) {
            if (!Intrinsics.areEqual(next.getAlpha(), f)) {
                next.setTag(R$id.view_group_fade_helper_restore_tag, Float.valueOf(next.getAlpha()));
            }
            next.setAlpha(floatValue);
        }
        this.$root.setTag(R$id.view_group_fade_helper_previous_value_tag, Float.valueOf(floatValue));
    }
}
