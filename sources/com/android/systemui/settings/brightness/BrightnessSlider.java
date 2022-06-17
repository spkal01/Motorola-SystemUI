package com.android.systemui.settings.brightness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.R$layout;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.settings.brightness.BrightnessSliderView;
import com.android.systemui.settings.brightness.ToggleSlider;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.util.ViewController;

public class BrightnessSlider extends ViewController<BrightnessSliderView> implements ToggleSlider {
    /* access modifiers changed from: private */
    public final FalsingManager mFalsingManager;
    /* access modifiers changed from: private */
    public ToggleSlider.Listener mListener;
    private ToggleSlider mMirror;
    /* access modifiers changed from: private */
    public BrightnessMirrorController mMirrorController;
    private final Gefingerpoken mOnInterceptListener = new Gefingerpoken() {
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 1 && actionMasked != 3) {
                return false;
            }
            BrightnessSlider.this.mFalsingManager.isFalseTouch(10);
            return false;
        }
    };
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (BrightnessSlider.this.mListener != null) {
                BrightnessSlider.this.mListener.onChanged(BrightnessSlider.this.mTracking, i, false);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            boolean unused = BrightnessSlider.this.mTracking = true;
            if (BrightnessSlider.this.mListener != null) {
                BrightnessSlider.this.mListener.onChanged(BrightnessSlider.this.mTracking, BrightnessSlider.this.getValue(), false);
            }
            if (BrightnessSlider.this.mMirrorController != null) {
                BrightnessSlider.this.mMirrorController.showMirror();
                BrightnessSlider.this.mMirrorController.setLocationAndSize(BrightnessSlider.this.mView);
            }
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            boolean unused = BrightnessSlider.this.mTracking = false;
            if (BrightnessSlider.this.mListener != null) {
                BrightnessSlider.this.mListener.onChanged(BrightnessSlider.this.mTracking, BrightnessSlider.this.getValue(), true);
            }
            if (BrightnessSlider.this.mMirrorController != null) {
                BrightnessSlider.this.mMirrorController.hideMirror();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mTracking;

    public BrightnessSlider(BrightnessSliderView brightnessSliderView, FalsingManager falsingManager) {
        super(brightnessSliderView);
        this.mFalsingManager = falsingManager;
    }

    public View getRootView() {
        return this.mView;
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        ((BrightnessSliderView) this.mView).setOnSeekBarChangeListener(this.mSeekListener);
        ((BrightnessSliderView) this.mView).setOnInterceptListener(this.mOnInterceptListener);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        ((BrightnessSliderView) this.mView).setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) null);
        ((BrightnessSliderView) this.mView).setOnDispatchTouchEventListener((BrightnessSliderView.DispatchTouchEventListener) null);
        ((BrightnessSliderView) this.mView).setOnInterceptListener((Gefingerpoken) null);
    }

    public boolean mirrorTouchEvent(MotionEvent motionEvent) {
        if (this.mMirror != null) {
            return copyEventToMirror(motionEvent);
        }
        return ((BrightnessSliderView) this.mView).dispatchTouchEvent(motionEvent);
    }

    private boolean copyEventToMirror(MotionEvent motionEvent) {
        MotionEvent copy = motionEvent.copy();
        boolean mirrorTouchEvent = this.mMirror.mirrorTouchEvent(copy);
        copy.recycle();
        return mirrorTouchEvent;
    }

    public void setEnforcedAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        ((BrightnessSliderView) this.mView).setEnforcedAdmin(enforcedAdmin);
    }

    private void setMirror(ToggleSlider toggleSlider) {
        this.mMirror = toggleSlider;
        if (toggleSlider != null) {
            toggleSlider.setMax(((BrightnessSliderView) this.mView).getMax());
            this.mMirror.setValue(((BrightnessSliderView) this.mView).getValue());
            ((BrightnessSliderView) this.mView).setOnDispatchTouchEventListener(new BrightnessSlider$$ExternalSyntheticLambda0(this));
            return;
        }
        ((BrightnessSliderView) this.mView).setOnDispatchTouchEventListener((BrightnessSliderView.DispatchTouchEventListener) null);
    }

    public void setMirrorControllerAndMirror(BrightnessMirrorController brightnessMirrorController) {
        this.mMirrorController = brightnessMirrorController;
        if (brightnessMirrorController != null) {
            setMirror(brightnessMirrorController.getToggleSlider());
        } else {
            ((BrightnessSliderView) this.mView).setOnDispatchTouchEventListener((BrightnessSliderView.DispatchTouchEventListener) null);
        }
    }

    public void setOnChangedListener(ToggleSlider.Listener listener) {
        this.mListener = listener;
    }

    public void setMax(int i) {
        ((BrightnessSliderView) this.mView).setMax(i);
        ToggleSlider toggleSlider = this.mMirror;
        if (toggleSlider != null) {
            toggleSlider.setMax(i);
        }
    }

    public void setValue(int i) {
        ((BrightnessSliderView) this.mView).setValue(i);
        ToggleSlider toggleSlider = this.mMirror;
        if (toggleSlider != null) {
            toggleSlider.setValue(i);
        }
    }

    public int getValue() {
        return ((BrightnessSliderView) this.mView).getValue();
    }

    public static class Factory {
        private final FalsingManager mFalsingManager;

        public Factory(FalsingManager falsingManager) {
            this.mFalsingManager = falsingManager;
        }

        public BrightnessSlider create(Context context, ViewGroup viewGroup) {
            return new BrightnessSlider((BrightnessSliderView) LayoutInflater.from(context).inflate(getLayout(), viewGroup, false), this.mFalsingManager);
        }

        private int getLayout() {
            return R$layout.quick_settings_brightness_dialog;
        }
    }
}
