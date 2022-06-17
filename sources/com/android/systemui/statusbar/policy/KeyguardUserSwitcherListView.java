package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.android.keyguard.AlphaOptimizedLinearLayout;
import com.android.keyguard.KeyguardConstants;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.android.systemui.animation.Interpolators;

public class KeyguardUserSwitcherListView extends AlphaOptimizedLinearLayout {
    private static final boolean DEBUG = KeyguardConstants.DEBUG;
    private boolean mAnimating;
    private final AppearAnimationUtils mAppearAnimationUtils;
    private final DisappearAnimationUtils mDisappearAnimationUtils;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public KeyguardUserSwitcherListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAppearAnimationUtils = new AppearAnimationUtils(context, 220, -0.5f, 0.5f, Interpolators.FAST_OUT_SLOW_IN);
        this.mDisappearAnimationUtils = new DisappearAnimationUtils(context, 220, 0.2f, 0.2f, Interpolators.FAST_OUT_SLOW_IN_REVERSE);
    }

    /* access modifiers changed from: package-private */
    public void setDarkAmount(float f) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof KeyguardUserDetailItemView) {
                ((KeyguardUserDetailItemView) childAt).setDarkAmount(f);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAnimating() {
        return this.mAnimating;
    }

    /* access modifiers changed from: package-private */
    public void updateVisibilities(boolean z, boolean z2) {
        if (DEBUG) {
            Log.d("KeyguardUserSwitcherListView", String.format("updateVisibilities: open=%b animate=%b childCount=%d", new Object[]{Boolean.valueOf(z), Boolean.valueOf(z2), Integer.valueOf(getChildCount())}));
        }
        this.mAnimating = false;
        int childCount = getChildCount();
        KeyguardUserDetailItemView[] keyguardUserDetailItemViewArr = new KeyguardUserDetailItemView[childCount];
        for (int i = 0; i < childCount; i++) {
            keyguardUserDetailItemViewArr[i] = (KeyguardUserDetailItemView) getChildAt(i);
            keyguardUserDetailItemViewArr[i].clearAnimation();
            if (i == 0) {
                keyguardUserDetailItemViewArr[i].updateVisibilities(true, z, z2);
                keyguardUserDetailItemViewArr[i].setClickable(true);
            } else {
                keyguardUserDetailItemViewArr[i].setClickable(z);
                keyguardUserDetailItemViewArr[i].updateVisibilities(z2 || z, true, false);
            }
        }
        if (z2) {
            keyguardUserDetailItemViewArr[0] = null;
            setClipChildren(false);
            setClipToPadding(false);
            this.mAnimating = true;
            (z ? this.mAppearAnimationUtils : this.mDisappearAnimationUtils).startAnimation(keyguardUserDetailItemViewArr, new KeyguardUserSwitcherListView$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateVisibilities$0() {
        setClipChildren(true);
        setClipToPadding(true);
        this.mAnimating = false;
    }

    /* access modifiers changed from: package-private */
    public void replaceView(KeyguardUserDetailItemView keyguardUserDetailItemView, int i) {
        removeViewAt(i);
        addView(keyguardUserDetailItemView, i);
    }

    /* access modifiers changed from: package-private */
    public void removeLastView() {
        removeViewAt(getChildCount() - 1);
    }
}
