package com.android.systemui.navigationbar.gestural;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.systemui.R$dimen;
import com.android.systemui.R$layout;
import com.android.systemui.navigationbar.RotationButton;
import com.android.systemui.navigationbar.RotationButtonController;
import com.android.systemui.navigationbar.buttons.KeyButtonDrawable;
import com.android.systemui.navigationbar.buttons.KeyButtonView;
import java.util.function.Consumer;

public class FloatingRotationButton implements RotationButton {
    private boolean mCanShow = true;
    private final Context mContext;
    private final int mDiameter;
    /* access modifiers changed from: private */
    public boolean mIsShowing;
    private KeyButtonDrawable mKeyButtonDrawable;
    /* access modifiers changed from: private */
    public final KeyButtonView mKeyButtonView;
    private final int mMargin;
    private RotationButtonController mRotationButtonController;
    /* access modifiers changed from: private */
    public Consumer<Boolean> mVisibilityChangedCallback;
    private final WindowManager mWindowManager;

    public FloatingRotationButton(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        KeyButtonView keyButtonView = (KeyButtonView) LayoutInflater.from(context).inflate(R$layout.rotate_suggestion, (ViewGroup) null);
        this.mKeyButtonView = keyButtonView;
        keyButtonView.setVisibility(0);
        Resources resources = context.getResources();
        this.mDiameter = resources.getDimensionPixelSize(R$dimen.floating_rotation_button_diameter);
        this.mMargin = Math.max(resources.getDimensionPixelSize(R$dimen.floating_rotation_button_min_margin), resources.getDimensionPixelSize(R$dimen.rounded_corner_content_padding));
    }

    public void setRotationButtonController(RotationButtonController rotationButtonController) {
        this.mRotationButtonController = rotationButtonController;
        updateIcon(rotationButtonController.getLightIconColor(), this.mRotationButtonController.getDarkIconColor());
    }

    public void setVisibilityChangedCallback(Consumer<Boolean> consumer) {
        this.mVisibilityChangedCallback = consumer;
    }

    public View getCurrentView() {
        return this.mKeyButtonView;
    }

    public boolean show() {
        if (!this.mCanShow || this.mIsShowing) {
            return false;
        }
        this.mIsShowing = true;
        int i = this.mDiameter;
        int i2 = this.mMargin;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(i, i, i2, i2, 2024, 8, -3);
        layoutParams.privateFlags |= 16;
        layoutParams.setTitle("FloatingRotationButton");
        int rotation = this.mWindowManager.getDefaultDisplay().getRotation();
        if (rotation == 0) {
            layoutParams.gravity = 83;
        } else if (rotation == 1) {
            layoutParams.gravity = 85;
        } else if (rotation == 2) {
            layoutParams.gravity = 53;
        } else if (rotation == 3) {
            layoutParams.gravity = 51;
        }
        this.mWindowManager.addView(this.mKeyButtonView, layoutParams);
        KeyButtonDrawable keyButtonDrawable = this.mKeyButtonDrawable;
        if (keyButtonDrawable != null && keyButtonDrawable.canAnimate()) {
            this.mKeyButtonDrawable.resetAnimation();
            this.mKeyButtonDrawable.startAnimation();
        }
        this.mKeyButtonView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                if (FloatingRotationButton.this.mIsShowing && FloatingRotationButton.this.mVisibilityChangedCallback != null) {
                    FloatingRotationButton.this.mVisibilityChangedCallback.accept(Boolean.TRUE);
                }
                FloatingRotationButton.this.mKeyButtonView.removeOnLayoutChangeListener(this);
            }
        });
        return true;
    }

    public boolean hide() {
        if (!this.mIsShowing) {
            return false;
        }
        this.mWindowManager.removeViewImmediate(this.mKeyButtonView);
        this.mIsShowing = false;
        Consumer<Boolean> consumer = this.mVisibilityChangedCallback;
        if (consumer == null) {
            return true;
        }
        consumer.accept(Boolean.FALSE);
        return true;
    }

    public boolean isVisible() {
        return this.mIsShowing;
    }

    public void updateIcon(int i, int i2) {
        int i3 = i;
        int i4 = i2;
        KeyButtonDrawable create = KeyButtonDrawable.create(this.mRotationButtonController.getContext(), i3, i4, this.mRotationButtonController.getIconResId(), false, Color.valueOf((float) Color.red(i2), (float) Color.green(i2), (float) Color.blue(i2), 0.92f));
        this.mKeyButtonDrawable = create;
        this.mKeyButtonView.setImageDrawable(create);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mKeyButtonView.setOnClickListener(onClickListener);
    }

    public void setOnHoverListener(View.OnHoverListener onHoverListener) {
        this.mKeyButtonView.setOnHoverListener(onHoverListener);
    }

    public KeyButtonDrawable getImageDrawable() {
        return this.mKeyButtonDrawable;
    }

    public void setDarkIntensity(float f) {
        this.mKeyButtonView.setDarkIntensity(f);
    }

    public void setCanShowRotationButton(boolean z) {
        this.mCanShow = z;
        if (!z) {
            hide();
        }
    }
}
