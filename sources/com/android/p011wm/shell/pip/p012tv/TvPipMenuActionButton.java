package com.android.p011wm.shell.pip.p012tv;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.p011wm.shell.C2219R;

/* renamed from: com.android.wm.shell.pip.tv.TvPipMenuActionButton */
public class TvPipMenuActionButton extends RelativeLayout implements View.OnClickListener {
    private Animator mButtonFocusGainAnimator;
    private Animator mButtonFocusLossAnimator;
    private final ImageView mButtonImageView;
    private final TextView mDescriptionTextView;
    private final ImageView mIconImageView;
    private View.OnClickListener mOnClickListener;
    private Animator mTextFocusGainAnimator;
    private Animator mTextFocusLossAnimator;

    public TvPipMenuActionButton(Context context) {
        this(context, (AttributeSet) null, 0, 0);
    }

    public TvPipMenuActionButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0, 0);
    }

    public TvPipMenuActionButton(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public TvPipMenuActionButton(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C2219R.layout.tv_pip_menu_action_button, this);
        this.mIconImageView = (ImageView) findViewById(C2219R.C2222id.icon);
        this.mButtonImageView = (ImageView) findViewById(C2219R.C2222id.button);
        this.mDescriptionTextView = (TextView) findViewById(C2219R.C2222id.desc);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{16843033, 16843087}, i, i2);
        setImageResource(obtainStyledAttributes.getResourceId(0, 0));
        int resourceId = obtainStyledAttributes.getResourceId(1, 0);
        if (resourceId != 0) {
            setTextAndDescription(getContext().getString(resourceId));
        }
        obtainStyledAttributes.recycle();
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        this.mButtonImageView.setOnFocusChangeListener(new TvPipMenuActionButton$$ExternalSyntheticLambda0(this));
        Context context = getContext();
        int i = C2219R.anim.tv_pip_controls_focus_gain_animation;
        Animator loadAnimator = AnimatorInflater.loadAnimator(context, i);
        this.mTextFocusGainAnimator = loadAnimator;
        loadAnimator.setTarget(this.mDescriptionTextView);
        Animator loadAnimator2 = AnimatorInflater.loadAnimator(getContext(), i);
        this.mButtonFocusGainAnimator = loadAnimator2;
        loadAnimator2.setTarget(this.mButtonImageView);
        Context context2 = getContext();
        int i2 = C2219R.anim.tv_pip_controls_focus_loss_animation;
        Animator loadAnimator3 = AnimatorInflater.loadAnimator(context2, i2);
        this.mTextFocusLossAnimator = loadAnimator3;
        loadAnimator3.setTarget(this.mDescriptionTextView);
        Animator loadAnimator4 = AnimatorInflater.loadAnimator(getContext(), i2);
        this.mButtonFocusLossAnimator = loadAnimator4;
        loadAnimator4.setTarget(this.mButtonImageView);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$0(View view, boolean z) {
        if (z) {
            startFocusGainAnimation();
        } else {
            startFocusLossAnimation();
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
        ImageView imageView = this.mButtonImageView;
        if (onClickListener == null) {
            this = null;
        }
        imageView.setOnClickListener(this);
    }

    public void onClick(View view) {
        View.OnClickListener onClickListener = this.mOnClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    public void setImageDrawable(Drawable drawable) {
        this.mIconImageView.setImageDrawable(drawable);
    }

    public void setImageResource(int i) {
        if (i != 0) {
            this.mIconImageView.setImageResource(i);
        }
    }

    public void setTextAndDescription(CharSequence charSequence) {
        this.mButtonImageView.setContentDescription(charSequence);
        this.mDescriptionTextView.setText(charSequence);
    }

    private static void cancelAnimator(Animator animator) {
        if (animator.isStarted()) {
            animator.cancel();
        }
    }

    public void startFocusGainAnimation() {
        cancelAnimator(this.mButtonFocusLossAnimator);
        cancelAnimator(this.mTextFocusLossAnimator);
        this.mTextFocusGainAnimator.start();
        if (this.mButtonImageView.getAlpha() < 1.0f) {
            this.mButtonFocusGainAnimator.start();
        }
    }

    public void startFocusLossAnimation() {
        cancelAnimator(this.mButtonFocusGainAnimator);
        cancelAnimator(this.mTextFocusGainAnimator);
        this.mTextFocusLossAnimator.start();
        if (this.mButtonImageView.hasFocus()) {
            this.mButtonFocusLossAnimator.start();
        }
    }
}
