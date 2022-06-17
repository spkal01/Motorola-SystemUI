package com.android.systemui.navigationbar.buttons;

import android.content.Context;
import android.graphics.Color;

public class ContextualButton extends ButtonDispatcher {
    private ContextualButtonGroup mGroup;
    protected final int mIconResId;
    protected final Context mLightContext;
    private ContextButtonListener mListener;

    public interface ContextButtonListener {
        void onVisibilityChanged(ContextualButton contextualButton, boolean z);
    }

    public ContextualButton(int i, Context context, int i2) {
        super(i);
        this.mLightContext = context;
        this.mIconResId = i2;
    }

    public void updateIcon(int i, int i2) {
        if (this.mIconResId != 0) {
            KeyButtonDrawable imageDrawable = getImageDrawable();
            KeyButtonDrawable newDrawable = getNewDrawable(i, i2);
            if (imageDrawable != null) {
                newDrawable.setDarkIntensity(imageDrawable.getDarkIntensity());
            }
            setImageDrawable(newDrawable);
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        KeyButtonDrawable imageDrawable = getImageDrawable();
        if (!(i == 0 || imageDrawable == null || !imageDrawable.canAnimate())) {
            imageDrawable.clearAnimationCallbacks();
            imageDrawable.resetAnimation();
        }
        ContextButtonListener contextButtonListener = this.mListener;
        if (contextButtonListener != null) {
            contextButtonListener.onVisibilityChanged(this, i == 0);
        }
    }

    public void setListener(ContextButtonListener contextButtonListener) {
        this.mListener = contextButtonListener;
    }

    public boolean show() {
        ContextualButtonGroup contextualButtonGroup = this.mGroup;
        if (contextualButtonGroup == null) {
            setVisibility(0);
            return true;
        } else if (contextualButtonGroup.setButtonVisibility(getId(), true) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hide() {
        ContextualButtonGroup contextualButtonGroup = this.mGroup;
        if (contextualButtonGroup == null) {
            setVisibility(4);
            return false;
        } else if (contextualButtonGroup.setButtonVisibility(getId(), false) != 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void attachToGroup(ContextualButtonGroup contextualButtonGroup) {
        this.mGroup = contextualButtonGroup;
    }

    /* access modifiers changed from: protected */
    public KeyButtonDrawable getNewDrawable(int i, int i2) {
        return KeyButtonDrawable.create(this.mLightContext, i, i2, this.mIconResId, false, (Color) null);
    }
}
