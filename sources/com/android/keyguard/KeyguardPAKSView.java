package com.android.keyguard;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.R$id;

public class KeyguardPAKSView extends KeyguardInputView {
    private TextView mLockMessage;
    private LockPatternUtils mLockPatternUtils;
    KeyguardMessageArea mSecurityMessageDisplay;

    public CharSequence getTitle() {
        return "";
    }

    public KeyguardPAKSView(Context context) {
        this(context, (AttributeSet) null);
    }

    public KeyguardPAKSView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyguardPAKSView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLockPatternUtils = new LockPatternUtils(context);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLockMessage = (TextView) findViewById(R$id.paks_lock_msg);
        updateLockMessage();
    }

    public void updateLockMessage() {
        this.mLockMessage.setText(this.mLockPatternUtils.getDeviceOwnerInfo());
        cleanSecurityMessage();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSecurityMessageDisplay = KeyguardMessageArea.findSecurityMessageDisplay(this);
        updateLockMessage();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateLockMessage();
    }

    private void cleanSecurityMessage() {
        KeyguardMessageArea keyguardMessageArea = this.mSecurityMessageDisplay;
        if (keyguardMessageArea != null) {
            keyguardMessageArea.setMessage((CharSequence) "");
        }
    }
}
