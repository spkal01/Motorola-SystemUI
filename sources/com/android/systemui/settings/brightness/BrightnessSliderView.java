package com.android.systemui.settings.brightness;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.R$color;
import com.android.systemui.R$id;
import com.android.systemui.moto.MotoFeature;

public class BrightnessSliderView extends FrameLayout {
    private DispatchTouchEventListener mListener;
    private Gefingerpoken mOnInterceptListener;
    private Drawable mProgressDrawable;
    private float mScale;
    private ToggleSeekBar mSlider;

    @FunctionalInterface
    interface DispatchTouchEventListener {
        boolean onDispatchTouchEvent(MotionEvent motionEvent);
    }

    public BrightnessSliderView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BrightnessSliderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mScale = 1.0f;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        if (MotoFeature.isCliContext(this.mContext)) {
            this.mSlider = (ToggleSeekBar) requireViewById(R$id.cli_slider);
        } else {
            this.mSlider = (ToggleSeekBar) requireViewById(R$id.slider);
        }
        this.mSlider.setAccessibilityLabel(getContentDescription().toString());
        try {
            this.mProgressDrawable = ((LayerDrawable) ((DrawableWrapper) ((LayerDrawable) this.mSlider.getProgressDrawable()).findDrawableByLayerId(16908301)).getDrawable()).findDrawableByLayerId(R$id.slider_foreground);
            if (MotoFeature.getInstance(getContext()).isCustomPanelView()) {
                updateColorPrc();
            }
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (MotoFeature.getInstance(getContext()).isCustomPanelView()) {
            updateColorPrc();
        }
    }

    public void updateColorPrc() {
        int color = this.mContext.getResources().getColor(R$color.prcBrightnessSliderbg);
        int color2 = this.mContext.getResources().getColor(R$color.prcQSTileActiveColorForFixed);
        this.mContext.getResources().getColor(R$color.prcQSTileInactiveColorForUnfiexed);
        this.mProgressDrawable.setTint(color2);
        ((DrawableWrapper) ((LayerDrawable) this.mSlider.getProgressDrawable()).findDrawableByLayerId(16908288)).setTint(color);
    }

    public void setOnDispatchTouchEventListener(DispatchTouchEventListener dispatchTouchEventListener) {
        this.mListener = dispatchTouchEventListener;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        DispatchTouchEventListener dispatchTouchEventListener = this.mListener;
        if (dispatchTouchEventListener != null) {
            dispatchTouchEventListener.onDispatchTouchEvent(motionEvent);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.requestDisallowInterceptTouchEvent(z);
        }
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mSlider.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    public void setEnforcedAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mSlider.setEnabled(enforcedAdmin == null);
        this.mSlider.setEnforcedAdmin(enforcedAdmin);
    }

    public int getMax() {
        return this.mSlider.getMax();
    }

    public void setMax(int i) {
        this.mSlider.setMax(i);
    }

    public void setValue(int i) {
        this.mSlider.setProgress(i);
    }

    public int getValue() {
        return this.mSlider.getProgress();
    }

    public void setOnInterceptListener(Gefingerpoken gefingerpoken) {
        this.mOnInterceptListener = gefingerpoken;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        Gefingerpoken gefingerpoken = this.mOnInterceptListener;
        if (gefingerpoken != null) {
            return gefingerpoken.onInterceptTouchEvent(motionEvent);
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        applySliderScale();
    }

    public void setSliderScaleY(float f) {
        if (f != this.mScale) {
            this.mScale = f;
            applySliderScale();
        }
    }

    private void applySliderScale() {
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            Rect bounds = drawable.getBounds();
            int intrinsicHeight = (int) (((float) this.mProgressDrawable.getIntrinsicHeight()) * this.mScale);
            int intrinsicHeight2 = (this.mProgressDrawable.getIntrinsicHeight() - intrinsicHeight) / 2;
            this.mProgressDrawable.setBounds(bounds.left, intrinsicHeight2, bounds.right, intrinsicHeight + intrinsicHeight2);
        }
    }

    public float getSliderScaleY() {
        return this.mScale;
    }
}
