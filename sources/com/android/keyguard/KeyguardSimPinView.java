package com.android.keyguard;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import com.android.systemui.R$id;

public class KeyguardSimPinView extends KeyguardPinBasedInputView {
    /* access modifiers changed from: protected */
    public int getPromptReasonStringRes(int i) {
        return 0;
    }

    public void startAppearAnimation() {
    }

    public KeyguardSimPinView(Context context) {
        this(context, (AttributeSet) null);
    }

    public KeyguardSimPinView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setEsimLocked(boolean z) {
        ((KeyguardEsimArea) findViewById(R$id.keyguard_esim_area)).setVisibility(z ? 0 : 8);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        resetState();
    }

    /* access modifiers changed from: protected */
    public int getPasswordTextViewId() {
        return R$id.simPinEntry;
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
        return getContext().getString(17040472);
    }
}
