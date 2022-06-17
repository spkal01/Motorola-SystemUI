package com.android.systemui.screenshot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.display.DisplayManager;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;

public class CliGlobalScreenshot {
    /* access modifiers changed from: private */
    public ImageView mBackgroundView;
    private float mBgPadding;
    /* access modifiers changed from: private */
    public float mBgPaddingScale;
    private Context mContext;
    private Display mDisplay;
    private DisplayMetrics mDisplayMetrics;
    /* access modifiers changed from: private */
    public Bitmap mScreenBitmap;
    /* access modifiers changed from: private */
    public AnimatorSet mScreenshotAnimation;
    /* access modifiers changed from: private */
    public ImageView mScreenshotFlash = ((ImageView) this.mScreenshotLayout.findViewById(R$id.global_screenshot_legacy_flash));
    /* access modifiers changed from: private */
    public View mScreenshotLayout;
    /* access modifiers changed from: private */
    public ImageView mScreenshotView = ((ImageView) this.mScreenshotLayout.findViewById(R$id.global_screenshot_legacy));
    private WindowManager.LayoutParams mWindowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager mWindowManager;

    public CliGlobalScreenshot(Context context, int i) {
        Resources resources = context.getResources();
        this.mContext = context;
        View inflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R$layout.global_screenshot_legacy, (ViewGroup) null);
        this.mScreenshotLayout = inflate;
        this.mBackgroundView = (ImageView) inflate.findViewById(R$id.global_screenshot_legacy_background);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 0, 0, 2036, 525568, -3);
        this.mWindowLayoutParams = layoutParams;
        layoutParams.setTitle("CLIScreenshotAnimation");
        this.mWindowLayoutParams.layoutInDisplayCutoutMode = 3;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mDisplay = ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplay(i);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mDisplayMetrics = displayMetrics;
        this.mDisplay.getRealMetrics(displayMetrics);
        float dimensionPixelSize = (float) resources.getDimensionPixelSize(R$dimen.global_screenshot_bg_padding);
        this.mBgPadding = dimensionPixelSize;
        this.mBgPaddingScale = dimensionPixelSize / ((float) this.mDisplayMetrics.widthPixels);
    }

    public void startAnimation(Bitmap bitmap) {
        DisplayMetrics displayMetrics = this.mDisplayMetrics;
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        this.mScreenBitmap = bitmap.copy(bitmap.getConfig(), false);
        if (((PowerManager) this.mContext.getSystemService("power")).isPowerSaveMode()) {
            Toast.makeText(this.mContext, R$string.screenshot_saved_title, 0).show();
        }
        this.mScreenshotView.setImageBitmap(this.mScreenBitmap);
        this.mScreenshotLayout.requestFocus();
        AnimatorSet animatorSet = this.mScreenshotAnimation;
        if (animatorSet != null) {
            if (animatorSet.isStarted()) {
                this.mScreenshotAnimation.end();
            }
            this.mScreenshotAnimation.removeAllListeners();
        }
        this.mWindowManager.addView(this.mScreenshotLayout, this.mWindowLayoutParams);
        ValueAnimator createScreenshotDropInAnimation = createScreenshotDropInAnimation();
        ValueAnimator createScreenshotDropOutAnimation = createScreenshotDropOutAnimation(i, i2, false, false);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mScreenshotAnimation = animatorSet2;
        animatorSet2.playSequentially(new Animator[]{createScreenshotDropInAnimation, createScreenshotDropOutAnimation});
        this.mScreenshotAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                CliGlobalScreenshot.this.mWindowManager.removeView(CliGlobalScreenshot.this.mScreenshotLayout);
                Bitmap unused = CliGlobalScreenshot.this.mScreenBitmap = null;
                CliGlobalScreenshot.this.mScreenshotView.setImageBitmap((Bitmap) null);
            }

            public void onAnimationCancel(Animator animator) {
                CliGlobalScreenshot.this.mWindowManager.removeView(CliGlobalScreenshot.this.mScreenshotLayout);
                Bitmap unused = CliGlobalScreenshot.this.mScreenBitmap = null;
                CliGlobalScreenshot.this.mScreenshotView.setImageBitmap((Bitmap) null);
            }
        });
        this.mScreenshotLayout.post(new Runnable() {
            public void run() {
                CliGlobalScreenshot.this.mScreenshotView.setLayerType(2, (Paint) null);
                CliGlobalScreenshot.this.mScreenshotView.buildLayer();
                CliGlobalScreenshot.this.mScreenshotAnimation.start();
            }
        });
    }

    private ValueAnimator createScreenshotDropInAnimation() {
        final C12963 r0 = new Interpolator() {
            public float getInterpolation(float f) {
                if (f <= 0.60465115f) {
                    return (float) Math.sin(((double) (f / 0.60465115f)) * 3.141592653589793d);
                }
                return 0.0f;
            }
        };
        final C12974 r1 = new Interpolator() {
            public float getInterpolation(float f) {
                if (f < 0.30232558f) {
                    return 0.0f;
                }
                return (f - 0.60465115f) / 0.39534885f;
            }
        };
        if ((this.mContext.getResources().getConfiguration().uiMode & 48) == 32) {
            this.mScreenshotView.getBackground().setTint(-16777216);
        } else {
            this.mScreenshotView.getBackground().setTintList((ColorStateList) null);
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(430);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                CliGlobalScreenshot.this.mBackgroundView.setAlpha(0.0f);
                CliGlobalScreenshot.this.mBackgroundView.setVisibility(0);
                CliGlobalScreenshot.this.mScreenshotView.setAlpha(0.0f);
                CliGlobalScreenshot.this.mScreenshotView.setTranslationX(0.0f);
                CliGlobalScreenshot.this.mScreenshotView.setTranslationY(0.0f);
                CliGlobalScreenshot.this.mScreenshotView.setScaleX(CliGlobalScreenshot.this.mBgPaddingScale + 1.0f);
                CliGlobalScreenshot.this.mScreenshotView.setScaleY(CliGlobalScreenshot.this.mBgPaddingScale + 1.0f);
                CliGlobalScreenshot.this.mScreenshotView.setVisibility(0);
                CliGlobalScreenshot.this.mScreenshotFlash.setAlpha(0.0f);
                CliGlobalScreenshot.this.mScreenshotFlash.setVisibility(0);
            }

            public void onAnimationEnd(Animator animator) {
                CliGlobalScreenshot.this.mScreenshotFlash.setVisibility(8);
            }
        });
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                float access$600 = (CliGlobalScreenshot.this.mBgPaddingScale + 1.0f) - (r1.getInterpolation(floatValue) * 0.27499998f);
                CliGlobalScreenshot.this.mBackgroundView.setAlpha(r1.getInterpolation(floatValue) * 0.5f);
                CliGlobalScreenshot.this.mScreenshotView.setAlpha(floatValue);
                CliGlobalScreenshot.this.mScreenshotView.setScaleX(access$600);
                CliGlobalScreenshot.this.mScreenshotView.setScaleY(access$600);
                CliGlobalScreenshot.this.mScreenshotFlash.setAlpha(r0.getInterpolation(floatValue));
            }
        });
        return ofFloat;
    }

    private ValueAnimator createScreenshotDropOutAnimation(int i, int i2, boolean z, boolean z2) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setStartDelay(500);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                CliGlobalScreenshot.this.mBackgroundView.setVisibility(8);
                CliGlobalScreenshot.this.mScreenshotView.setVisibility(8);
                CliGlobalScreenshot.this.mScreenshotView.setLayerType(0, (Paint) null);
            }
        });
        if (!z || !z2) {
            ofFloat.setDuration(320);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    float access$600 = (CliGlobalScreenshot.this.mBgPaddingScale + 0.725f) - (0.125f * floatValue);
                    float f = 1.0f - floatValue;
                    CliGlobalScreenshot.this.mBackgroundView.setAlpha(0.5f * f);
                    CliGlobalScreenshot.this.mScreenshotView.setAlpha(f);
                    CliGlobalScreenshot.this.mScreenshotView.setScaleX(access$600);
                    CliGlobalScreenshot.this.mScreenshotView.setScaleY(access$600);
                }
            });
        } else {
            final C13029 r6 = new Interpolator() {
                public float getInterpolation(float f) {
                    if (f < 0.8604651f) {
                        return (float) (1.0d - Math.pow((double) (1.0f - (f / 0.8604651f)), 2.0d));
                    }
                    return 1.0f;
                }
            };
            float f = this.mBgPadding;
            float f2 = (((float) i) - (f * 2.0f)) / 2.0f;
            float f3 = (((float) i2) - (f * 2.0f)) / 2.0f;
            final PointF pointF = new PointF((-f2) + (f2 * 0.45f), (-f3) + (f3 * 0.45f));
            ofFloat.setDuration(430);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    float access$600 = (CliGlobalScreenshot.this.mBgPaddingScale + 0.725f) - (r6.getInterpolation(floatValue) * 0.27500004f);
                    CliGlobalScreenshot.this.mBackgroundView.setAlpha((1.0f - floatValue) * 0.5f);
                    CliGlobalScreenshot.this.mScreenshotView.setAlpha(1.0f - r6.getInterpolation(floatValue));
                    CliGlobalScreenshot.this.mScreenshotView.setScaleX(access$600);
                    CliGlobalScreenshot.this.mScreenshotView.setScaleY(access$600);
                    CliGlobalScreenshot.this.mScreenshotView.setTranslationX(pointF.x * floatValue);
                    CliGlobalScreenshot.this.mScreenshotView.setTranslationY(floatValue * pointF.y);
                }
            });
        }
        return ofFloat;
    }
}
