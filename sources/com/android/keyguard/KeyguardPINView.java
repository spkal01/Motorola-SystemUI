package com.android.keyguard;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateXAnimation;
import android.widget.LinearLayout;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import java.util.List;

public class KeyguardPINView extends KeyguardPinBasedInputView {
    private final AppearAnimationUtils mAppearAnimationUtils;
    private ViewGroup mContainer;
    private final DisappearAnimationUtils mDisappearAnimationUtils;
    private final DisappearAnimationUtils mDisappearAnimationUtilsLocked;
    private int mDisappearYTranslation;
    private ViewGroup mRow0;
    private ViewGroup mRow1;
    private ViewGroup mRow2;
    private ViewGroup mRow3;
    private boolean mUsePrcSixPin;
    private View[][] mViews;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean needsTranslationAnimation() {
        return true;
    }

    public KeyguardPINView(Context context) {
        this(context, (AttributeSet) null);
    }

    public KeyguardPINView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAppearAnimationUtils = new AppearAnimationUtils(context);
        Context context2 = context;
        this.mDisappearAnimationUtils = new DisappearAnimationUtils(context2, 125, 0.6f, 0.45f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearAnimationUtilsLocked = new DisappearAnimationUtils(context2, 187, 0.6f, 0.45f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearYTranslation = getResources().getDimensionPixelSize(R$dimen.disappear_y_translation);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        updateMargins();
    }

    /* access modifiers changed from: protected */
    public int getPasswordTextViewId() {
        return R$id.pinEntry;
    }

    private void updateMargins() {
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.num_pad_row_margin_bottom);
        for (ViewGroup layoutParams : List.of(this.mRow1, this.mRow2, this.mRow3)) {
            ((LinearLayout.LayoutParams) layoutParams.getLayoutParams()).setMargins(0, 0, 0, dimensionPixelSize);
        }
        ((LinearLayout.LayoutParams) this.mRow0.getLayoutParams()).setMargins(0, 0, 0, this.mContext.getResources().getDimensionPixelSize(R$dimen.num_pad_entry_row_margin_bottom));
        if (this.mEcaView != null) {
            ((LinearLayout.LayoutParams) this.mEcaView.getLayoutParams()).setMargins(0, this.mContext.getResources().getDimensionPixelSize(R$dimen.keyguard_eca_top_margin), 0, this.mContext.getResources().getDimensionPixelSize(R$dimen.keyguard_eca_bottom_margin));
        }
        View findViewById = findViewById(R$id.pinEntry);
        ViewGroup.LayoutParams layoutParams2 = findViewById.getLayoutParams();
        layoutParams2.height = this.mContext.getResources().getDimensionPixelSize(R$dimen.keyguard_password_height);
        findViewById.setLayoutParams(layoutParams2);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContainer = (ViewGroup) findViewById(R$id.pin_container);
        this.mRow0 = (ViewGroup) findViewById(R$id.row0);
        this.mRow1 = (ViewGroup) findViewById(R$id.row1);
        this.mRow2 = (ViewGroup) findViewById(R$id.row2);
        this.mRow3 = (ViewGroup) findViewById(R$id.row3);
        this.mViews = new View[][]{new View[]{this.mRow0, null, null}, new View[]{findViewById(R$id.key1), findViewById(R$id.key2), findViewById(R$id.key3)}, new View[]{findViewById(R$id.key4), findViewById(R$id.key5), findViewById(R$id.key6)}, new View[]{findViewById(R$id.key7), findViewById(R$id.key8), findViewById(R$id.key9)}, new View[]{findViewById(R$id.delete_button), findViewById(R$id.key0), findViewById(R$id.key_enter)}, new View[]{null, this.mEcaView, null}};
        if (this.mUsePrcSixPin) {
            this.mOkButton.setEnabled(false);
        }
    }

    public int getWrongPasswordStringId() {
        return R$string.kg_wrong_pin;
    }

    public void startAppearAnimation() {
        enableClipping(false);
        setAlpha(1.0f);
        if (needsTranslationAnimation()) {
            setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
            AppearAnimationUtils.startTranslationYAnimation(this, 0, 500, 0.0f, this.mAppearAnimationUtils.getInterpolator(), getAnimationListener(19));
        }
        this.mAppearAnimationUtils.startAnimation2d(this.mViews, new Runnable() {
            public void run() {
                KeyguardPINView.this.enableClipping(true);
            }
        });
    }

    public boolean startDisappearAnimation(boolean z, Runnable runnable) {
        DisappearAnimationUtils disappearAnimationUtils;
        enableClipping(false);
        if (needsTranslationAnimation()) {
            setTranslationY(0.0f);
            AppearAnimationUtils.startTranslationYAnimation(this, 0, 280, (float) this.mDisappearYTranslation, this.mDisappearAnimationUtils.getInterpolator(), getAnimationListener(22));
        }
        if (!needDisappearAnim()) {
            return true;
        }
        if (z) {
            disappearAnimationUtils = this.mDisappearAnimationUtilsLocked;
        } else {
            disappearAnimationUtils = this.mDisappearAnimationUtils;
        }
        disappearAnimationUtils.startAnimation2d(this.mViews, new KeyguardPINView$$ExternalSyntheticLambda0(this, runnable));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startDisappearAnimation$0(Runnable runnable) {
        enableClipping(true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public boolean needDisappearAnim() {
        return !MotoFeature.isLidClosed(this.mContext);
    }

    /* access modifiers changed from: private */
    public void enableClipping(boolean z) {
        this.mContainer.setClipToPadding(z);
        this.mContainer.setClipChildren(z);
        this.mRow1.setClipToPadding(z);
        this.mRow2.setClipToPadding(z);
        this.mRow3.setClipToPadding(z);
        setClipChildren(z);
    }

    /* access modifiers changed from: protected */
    public void setViews(View[][] viewArr) {
        this.mViews = viewArr;
    }

    /* access modifiers changed from: protected */
    public void setPasswordEntryEnabled(boolean z) {
        super.setPasswordEntryEnabled(z);
        if (this.mUsePrcSixPin) {
            this.mOkButton.setEnabled(false);
        }
    }

    /* access modifiers changed from: protected */
    public void setPasswordEntryInputEnabled(boolean z) {
        super.setPasswordEntryInputEnabled(z);
        if (this.mUsePrcSixPin) {
            this.mOkButton.setEnabled(false);
        }
    }

    /* access modifiers changed from: protected */
    public void resetPasswordText(boolean z, boolean z2) {
        super.resetPasswordText(z, z2);
        if (this.mUsePrcSixPin && z && !z2) {
            TranslateXAnimation translateXAnimation = new TranslateXAnimation(-15.0f, 15.0f);
            translateXAnimation.setInterpolator(new LinearInterpolator());
            translateXAnimation.setDuration(60);
            translateXAnimation.setRepeatCount(4);
            translateXAnimation.setRepeatMode(2);
            this.mPasswordEntry.startAnimation(translateXAnimation);
        }
    }

    public void setUsePrcSixPin() {
        this.mUsePrcSixPin = true;
        this.mPasswordEntry.setUsePrcSixPin();
        updatePaddingForPrc();
    }

    private void updatePaddingForPrc() {
        this.mRow0.setPaddingRelative(0, 0, 0, this.mContext.getResources().getDimensionPixelSize(R$dimen.prc_six_pin_pwd_entry_row_padding_bottom));
    }
}
