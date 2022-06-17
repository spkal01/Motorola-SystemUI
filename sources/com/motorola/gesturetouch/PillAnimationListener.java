package com.motorola.gesturetouch;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;

class PillAnimationListener {
    private static final boolean DEBUG = (!Build.IS_USER);
    private final int SWIPE_ANIMATION_DURATION_MS = 200;
    private Context mContext;
    /* access modifiers changed from: private */
    public View mEdgeTouchPillView;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams mLayoutParams;
    private EdgeTouchPillController mPillController;
    ValueAnimator mSwipDownAnimation;
    ValueAnimator mSwipUpAnimation;
    /* access modifiers changed from: private */
    public WindowManager mWindowManager;

    public PillAnimationListener(Context context, View view, EdgeTouchPillController edgeTouchPillController) {
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mContext = context;
        this.mEdgeTouchPillView = view;
        this.mLayoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        this.mPillController = edgeTouchPillController;
        Resources resources = context.getResources();
        int i = R$dimen.pill_swipe_animation_distance;
        this.mSwipUpAnimation = getSwipeValueAnimator(-resources.getDimensionPixelSize(i));
        this.mSwipDownAnimation = getSwipeValueAnimator(context.getResources().getDimensionPixelSize(i));
    }

    public void excuteAction(int i) {
        if (i == 1 || i == 2) {
            playPillGlowAnimation();
        } else if (i == 3) {
            playPillSwipeUpAnimation();
        } else if (i == 4) {
            playPillSwipeDownAnimation();
        }
    }

    private void playPillGlowAnimation() {
        View findViewById = this.mEdgeTouchPillView.findViewById(R$id.clickable_area);
        ImageView imageView = (ImageView) findViewById.findViewById(R$id.pill);
        ImageView imageView2 = (ImageView) findViewById.findViewById(R$id.dot_top);
        ImageView imageView3 = (ImageView) findViewById.findViewById(R$id.dot_bottom);
        imageView.setImageDrawable(getPillGlowAnimationDrawable(this.mPillController.isLightTheme()));
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.stop();
        animationDrawable.start();
        imageView2.setImageDrawable(getPillDotsAnimationDrawable(this.mPillController.isLightTheme()));
        AnimationDrawable animationDrawable2 = (AnimationDrawable) imageView2.getDrawable();
        animationDrawable2.stop();
        animationDrawable2.start();
        imageView3.setImageDrawable(getPillDotsAnimationDrawable(this.mPillController.isLightTheme()));
        AnimationDrawable animationDrawable3 = (AnimationDrawable) imageView3.getDrawable();
        animationDrawable3.stop();
        animationDrawable3.start();
    }

    private void playPillSwipeUpAnimation() {
        if (!this.mSwipUpAnimation.isRunning()) {
            this.mSwipUpAnimation.start();
        }
        playPillGlowAnimation();
    }

    private void playPillSwipeDownAnimation() {
        if (!this.mSwipDownAnimation.isRunning()) {
            this.mSwipDownAnimation.start();
        }
        playPillGlowAnimation();
    }

    private Drawable getPillGlowAnimationDrawable(boolean z) {
        Drawable drawable;
        Resources resources = this.mContext.getResources();
        if (!z) {
            drawable = resources.getDrawable(R$drawable.pill_animation_dark, (Resources.Theme) null);
        } else if (this.mPillController.isActionBarOnLeft()) {
            drawable = resources.getDrawable(R$drawable.pill_animation_light_left, (Resources.Theme) null);
        } else {
            drawable = resources.getDrawable(R$drawable.pill_animation_light_right, (Resources.Theme) null);
        }
        drawable.mutate();
        if (drawable instanceof AnimationDrawable) {
            int pillWidth = this.mPillController.getPillWidth();
            int pillHeight = this.mPillController.getPillHeight();
            int i = 0;
            while (true) {
                AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                if (i > animationDrawable.getNumberOfFrames()) {
                    break;
                }
                Drawable frame = animationDrawable.getFrame(i);
                if (frame != null) {
                    frame.mutate();
                    if (frame instanceof GradientDrawable) {
                        ((GradientDrawable) frame).setSize(((int) resources.getDimension(R$dimen.pill_stroke_width)) + pillWidth, pillHeight);
                    } else if (frame instanceof LayerDrawable) {
                        LayerDrawable layerDrawable = (LayerDrawable) frame;
                        layerDrawable.setLayerSize(layerDrawable.getNumberOfLayers() - 1, pillWidth, pillHeight);
                    }
                }
                i++;
            }
        }
        return drawable;
    }

    private Drawable getPillDotsAnimationDrawable(boolean z) {
        if (z) {
            return this.mContext.getResources().getDrawable(R$drawable.dots_animation_light, (Resources.Theme) null);
        }
        return this.mContext.getResources().getDrawable(R$drawable.dots_animation_dark, (Resources.Theme) null);
    }

    public void updateSwipeAnimationParams() {
        this.mLayoutParams = (WindowManager.LayoutParams) this.mEdgeTouchPillView.getLayoutParams();
        Resources resources = this.mContext.getResources();
        int i = R$dimen.pill_swipe_animation_distance;
        this.mSwipUpAnimation = getSwipeValueAnimator(-resources.getDimensionPixelSize(i));
        this.mSwipDownAnimation = getSwipeValueAnimator(this.mContext.getResources().getDimensionPixelSize(i));
    }

    private ValueAnimator getSwipeValueAnimator(int i) {
        int i2 = this.mLayoutParams.y;
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{i2, i + i2, i2});
        ofInt.setDuration(200);
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PillAnimationListener.this.mLayoutParams.y = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                PillAnimationListener.this.mWindowManager.updateViewLayout(PillAnimationListener.this.mEdgeTouchPillView, PillAnimationListener.this.mLayoutParams);
            }
        });
        return ofInt;
    }

    public void onUserSwitch(Context context) {
        this.mContext = context;
    }
}
