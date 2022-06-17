package com.android.systemui.p006qs;

import android.os.Build;
import android.util.Log;
import android.view.View;
import com.android.systemui.p006qs.TouchAnimator;
import java.util.List;

/* renamed from: com.android.systemui.qs.QSPrcAnimator */
public class QSPrcAnimator {
    private static final boolean DEBUG = (!Build.IS_USER);
    private TouchAnimator mAlphaAnimator;
    private float mPosition;
    private final QSPrcPanelContainerController mPrcPanelContainerController;
    private boolean mRequestUpdateAnimator = false;

    public QSPrcAnimator(QSPrcPanelContainerController qSPrcPanelContainerController) {
        this.mPrcPanelContainerController = qSPrcPanelContainerController;
        updateAnimator();
    }

    public void requestAnimatorUpdate() {
        if (DEBUG) {
            Log.d("QSPrcAnimator", "requestAnimatorUpdate");
        }
        this.mRequestUpdateAnimator = true;
    }

    public void updateAnimator() {
        this.mRequestUpdateAnimator = false;
        List<View> animatorView = this.mPrcPanelContainerController.getAnimatorView();
        TouchAnimator.Builder builder = new TouchAnimator.Builder();
        for (View next : animatorView) {
            if (next != null) {
                builder.addFloat(next, "alpha", 0.0f, 1.0f);
            }
        }
        this.mAlphaAnimator = builder.build();
        if (DEBUG) {
            Log.d("QSPrcAnimator", "updateAnimator: animatorViews -> " + animatorView.size());
        }
    }

    public void setPosition(float f) {
        if (DEBUG) {
            Log.d("QSPrcAnimator", "setPosition: position, requestUpdate -> " + f + ", " + this.mRequestUpdateAnimator);
        }
        if (this.mRequestUpdateAnimator) {
            updateAnimator();
        }
        this.mPosition = f;
        this.mAlphaAnimator.setPosition(f);
    }
}
