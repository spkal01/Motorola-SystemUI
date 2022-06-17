package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import com.android.internal.widget.LockscreenCredential;
import com.android.systemui.R$id;
import com.android.systemui.R$string;

public abstract class KeyguardAbsKeyInputView extends KeyguardInputView {
    protected View mEcaView;
    protected boolean mEnableHaptics;
    private KeyDownListener mKeyDownListener;

    public interface KeyDownListener {
        boolean onKeyDown(int i, KeyEvent keyEvent);
    }

    /* access modifiers changed from: protected */
    public abstract LockscreenCredential getEnteredCredential();

    /* access modifiers changed from: protected */
    public abstract int getPasswordTextViewId();

    /* access modifiers changed from: protected */
    public abstract int getPromptReasonStringRes(int i);

    /* access modifiers changed from: protected */
    public abstract void resetPasswordText(boolean z, boolean z2);

    /* access modifiers changed from: protected */
    public abstract void setPasswordEntryEnabled(boolean z);

    /* access modifiers changed from: protected */
    public abstract void setPasswordEntryInputEnabled(boolean z);

    public KeyguardAbsKeyInputView(Context context) {
        this(context, (AttributeSet) null);
    }

    public KeyguardAbsKeyInputView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: package-private */
    public void setEnableHaptics(boolean z) {
        this.mEnableHaptics = z;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        this.mEcaView = findViewById(R$id.keyguard_selector_fade_container);
    }

    /* access modifiers changed from: protected */
    public int getWrongPasswordStringId() {
        return R$string.kg_wrong_password;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        KeyDownListener keyDownListener = this.mKeyDownListener;
        return keyDownListener != null && keyDownListener.onKeyDown(i, keyEvent);
    }

    public void doHapticKeyClick() {
        if (this.mEnableHaptics) {
            performHapticFeedback(1, 3);
        }
    }

    public void setKeyDownListener(KeyDownListener keyDownListener) {
        this.mKeyDownListener = keyDownListener;
    }
}
