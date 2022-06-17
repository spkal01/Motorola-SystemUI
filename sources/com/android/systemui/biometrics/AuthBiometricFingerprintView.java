package com.android.systemui.biometrics;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;

public class AuthBiometricFingerprintView extends AuthBiometricView {
    private boolean shouldAnimateForTransition(int i, int i2) {
        return (i2 == 1 || i2 == 2) ? i == 4 || i == 3 : i2 == 3 || i2 == 4;
    }

    /* access modifiers changed from: protected */
    public int getDelayAfterAuthenticatedDurationMs() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getStateForAfterError() {
        return 2;
    }

    /* access modifiers changed from: protected */
    public boolean supportsSmallDialog() {
        return false;
    }

    public AuthBiometricFingerprintView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AuthBiometricFingerprintView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void handleResetAfterError() {
        showTouchSensorString();
    }

    /* access modifiers changed from: protected */
    public void handleResetAfterHelp() {
        showTouchSensorString();
    }

    public void updateState(int i) {
        updateIcon(this.mState, i);
        super.updateState(i);
    }

    /* access modifiers changed from: package-private */
    public void onAttachedToWindowInternal() {
        super.onAttachedToWindowInternal();
        showTouchSensorString();
    }

    private void showTouchSensorString() {
        this.mIndicatorView.setText(R$string.fingerprint_dialog_touch_sensor);
        this.mIndicatorView.setTextColor(this.mTextColorHint);
    }

    private void updateIcon(int i, int i2) {
        Drawable animationForTransition = getAnimationForTransition(i, i2);
        if (animationForTransition == null) {
            Log.e("BiometricPrompt/AuthBiometricFingerprintView", "Animation not found, " + i + " -> " + i2);
            return;
        }
        AnimatedVectorDrawable animatedVectorDrawable = animationForTransition instanceof AnimatedVectorDrawable ? (AnimatedVectorDrawable) animationForTransition : null;
        this.mIconView.setImageDrawable(animationForTransition);
        CharSequence iconContentDescription = getIconContentDescription(i2);
        if (iconContentDescription != null) {
            this.mIconView.setContentDescription(iconContentDescription);
        }
        if (animatedVectorDrawable != null && shouldAnimateForTransition(i, i2)) {
            animatedVectorDrawable.forceAnimationOnUI();
            animatedVectorDrawable.start();
        }
    }

    private CharSequence getIconContentDescription(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 5:
            case 6:
                return this.mContext.getString(R$string.accessibility_fingerprint_dialog_fingerprint_icon);
            case 3:
            case 4:
                return this.mContext.getString(R$string.biometric_dialog_try_again);
            default:
                return null;
        }
    }

    private Drawable getAnimationForTransition(int i, int i2) {
        int i3;
        if (i2 == 1 || i2 == 2) {
            if (i == 4 || i == 3) {
                i3 = R$drawable.fingerprint_dialog_error_to_fp;
            } else {
                i3 = R$drawable.fingerprint_dialog_fp_to_error;
            }
        } else if (i2 == 3 || i2 == 4) {
            i3 = R$drawable.fingerprint_dialog_fp_to_error;
        } else if (i2 != 6) {
            return null;
        } else {
            i3 = R$drawable.fingerprint_dialog_fp_to_error;
        }
        return this.mContext.getDrawable(i3);
    }
}
