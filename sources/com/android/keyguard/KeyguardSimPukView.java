package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.android.systemui.R$id;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;

public class KeyguardSimPukView extends KeyguardPinBasedInputView {
    private static final boolean DEBUG = KeyguardConstants.DEBUG;

    /* access modifiers changed from: protected */
    public int getPromptReasonStringRes(int i) {
        return 0;
    }

    public void startAppearAnimation() {
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        return false;
    }

    public KeyguardSimPukView(Context context) {
        this(context, (AttributeSet) null);
    }

    public KeyguardSimPukView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: package-private */
    public String getPukPasswordErrorMessage(int i, boolean z, boolean z2) {
        String str;
        int i2;
        int i3;
        if (i == 0) {
            str = getContext().getString(R$string.kg_password_wrong_puk_code_dead);
        } else if (i > 0) {
            if (z) {
                i3 = R$plurals.kg_password_default_puk_message;
            } else {
                i3 = R$plurals.kg_password_wrong_puk_code;
            }
            str = getContext().getResources().getQuantityString(i3, i, new Object[]{Integer.valueOf(i)});
        } else {
            if (z) {
                i2 = R$string.kg_puk_enter_puk_hint;
            } else {
                i2 = R$string.kg_password_puk_failed;
            }
            str = getContext().getString(i2);
        }
        if (z2) {
            str = getResources().getString(R$string.kg_sim_lock_esim_instructions, new Object[]{str});
        }
        if (DEBUG) {
            Log.d("KeyguardSimPukView", "getPukPasswordErrorMessage: attemptsRemaining=" + i + " displayMessage=" + str);
        }
        return str;
    }

    /* access modifiers changed from: protected */
    public int getPasswordTextViewId() {
        return R$id.pukEntry;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View view = this.mEcaView;
        if (view instanceof EmergencyCarrierArea) {
            ((EmergencyCarrierArea) view).setCarrierTextVisible(true);
        }
    }

    public CharSequence getTitle() {
        return getContext().getString(17040473);
    }
}
