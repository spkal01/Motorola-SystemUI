package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.PresentationKgPasswordView;

public abstract class PtKgSecurityView extends LinearLayout {
    public KeyguardSecurityModel.SecurityMode mCurrentSecurityMode;
    protected PtKDMCallback mPtKDMCallback;

    public void setImeWindowStatus(PresentationKgPasswordView.ImeStatusCallback imeStatusCallback) {
    }

    public abstract void setKpMessageUpdateListener(KpMessageUpdateListener kpMessageUpdateListener);

    public void showBouncer() {
    }

    public void showClock() {
    }

    public PtKgSecurityView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PtKgSecurityView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PtKgSecurityView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setCurrentSecurityMode(KeyguardSecurityModel.SecurityMode securityMode) {
        this.mCurrentSecurityMode = securityMode;
    }

    public void setPtKDMCallback(PtKDMCallback ptKDMCallback) {
        this.mPtKDMCallback = ptKDMCallback;
    }
}
