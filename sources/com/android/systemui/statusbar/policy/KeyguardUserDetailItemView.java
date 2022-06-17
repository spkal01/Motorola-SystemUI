package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.android.keyguard.KeyguardConstants;
import com.android.systemui.R$dimen;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.p006qs.tiles.UserDetailItemView;

public class KeyguardUserDetailItemView extends UserDetailItemView {
    private static final boolean DEBUG = KeyguardConstants.DEBUG;
    private float mDarkAmount;
    private int mTextColor;

    public KeyguardUserDetailItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public KeyguardUserDetailItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyguardUserDetailItemView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public KeyguardUserDetailItemView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public int getFontSizeDimen() {
        return R$dimen.kg_user_switcher_text_size;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTextColor = this.mName.getCurrentTextColor();
        updateDark();
    }

    /* access modifiers changed from: package-private */
    public void updateVisibilities(boolean z, boolean z2, boolean z3) {
        int i = 0;
        if (DEBUG) {
            Log.d("KeyguardUserDetailItemView", String.format("updateVisibilities itemIsShown=%b nameIsShown=%b animate=%b", new Object[]{Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3)}));
        }
        getBackground().setAlpha((!z || !z2) ? 0 : 255);
        if (z) {
            if (z2) {
                this.mName.setVisibility(0);
                if (z3) {
                    this.mName.setAlpha(0.0f);
                    this.mName.animate().alpha(1.0f).setDuration(240).setInterpolator(Interpolators.ALPHA_IN);
                } else {
                    this.mName.setAlpha(1.0f);
                }
            } else if (z3) {
                this.mName.setVisibility(0);
                this.mName.setAlpha(1.0f);
                this.mName.animate().alpha(0.0f).setDuration(240).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new KeyguardUserDetailItemView$$ExternalSyntheticLambda0(this));
            } else {
                this.mName.setVisibility(8);
                this.mName.setAlpha(1.0f);
            }
            setVisibility(0);
            setAlpha(1.0f);
            return;
        }
        setVisibility(8);
        setAlpha(1.0f);
        TextView textView = this.mName;
        if (!z2) {
            i = 8;
        }
        textView.setVisibility(i);
        this.mName.setAlpha(1.0f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateVisibilities$0() {
        this.mName.setVisibility(8);
        this.mName.setAlpha(1.0f);
    }

    public void setDarkAmount(float f) {
        if (this.mDarkAmount != f) {
            this.mDarkAmount = f;
            updateDark();
        }
    }

    private void updateDark() {
        this.mName.setTextColor(ColorUtils.blendARGB(this.mTextColor, -1, this.mDarkAmount));
    }
}
