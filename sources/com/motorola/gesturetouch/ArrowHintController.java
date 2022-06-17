package com.motorola.gesturetouch;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.airbnb.lottie.LottieAnimationView;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$raw;

class ArrowHintController {
    private static float ARROW_HINT_ANIMATION_SPEED = 3.0f;
    private static String ARROW_HINT_LAYOUT_TITLE = "Systemui arrow hint view";
    private static final boolean DEBUG = (!Build.IS_USER);
    Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }

        public void onAnimationEnd(Animator animator) {
            ArrowHintController.this.removeArrowHint();
            ArrowHintController.this.mGestureActionController.arrowHintViewCallback(ArrowHintController.this.mIsSwipeUp);
        }

        public void onAnimationCancel(Animator animator) {
            ArrowHintController.this.removeArrowHint();
        }
    };
    private LottieAnimationView mArrowHinAnimationView;
    private View mArrowHintLayout;
    private Context mContext;
    /* access modifiers changed from: private */
    public GestureActionController mGestureActionController;
    /* access modifiers changed from: private */
    public boolean mIsSwipeUp;
    WindowManager.LayoutParams mLayoutParams = null;
    private GestureTouchController mPillController;
    private WindowManager mWindowManager;

    public ArrowHintController(Context context, GestureTouchController gestureTouchController, GestureActionController gestureActionController) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mPillController = gestureTouchController;
        this.mGestureActionController = gestureActionController;
    }

    public void showArrowHint(boolean z) {
        removeArrowHint();
        this.mIsSwipeUp = z;
        if (this.mLayoutParams == null) {
            this.mLayoutParams = creareArrowLayoutParams();
        }
        int i = this.mPillController.isLightTheme() ? R$raw.swipe_start : R$raw.swipe_start_dark;
        View inflate = RelativeLayout.inflate(this.mContext, this.mIsSwipeUp ? R$layout.arrow_hint_up_overlay : R$layout.arrow_hint_down_overlay, (ViewGroup) null);
        this.mArrowHintLayout = inflate;
        LottieAnimationView lottieAnimationView = (LottieAnimationView) inflate.findViewById(R$id.arrow_hint_lottie_start);
        this.mArrowHinAnimationView = lottieAnimationView;
        lottieAnimationView.setAnimation(i);
        this.mArrowHinAnimationView.setSpeed(ARROW_HINT_ANIMATION_SPEED);
        if (this.mIsSwipeUp) {
            this.mArrowHinAnimationView.setRotation(180.0f);
        }
        this.mArrowHinAnimationView.addAnimatorListener(this.mAnimatorListener);
        this.mArrowHinAnimationView.playAnimation();
        this.mWindowManager.addView(this.mArrowHintLayout, this.mLayoutParams);
    }

    private WindowManager.LayoutParams creareArrowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -1, 2038, 792, -3);
        layoutParams.setTitle(ARROW_HINT_LAYOUT_TITLE);
        return layoutParams;
    }

    /* access modifiers changed from: private */
    public void removeArrowHint() {
        if (this.mArrowHintLayout != null) {
            try {
                if (DEBUG) {
                    Log.i("GestureTouch_Arrow", "removeArrowHint");
                }
                this.mWindowManager.removeView(this.mArrowHintLayout);
                this.mArrowHintLayout = null;
            } catch (IllegalArgumentException e) {
                Log.i("GestureTouch_Arrow", "removeArrowHint fail " + e);
            }
        }
    }
}
