package com.android.keyguard;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.widget.LockscreenCredential;
import com.android.systemui.R$id;
import com.android.systemui.R$string;

public class PresentationKgPINView extends PresentationKgAbsKeyInputView implements View.OnTouchListener, View.OnKeyListener {
    private View mDeleteButton;
    private View mOkButton;
    protected PasswordTextView mPasswordEntry;

    private boolean isDirectionKey(int i) {
        switch (i) {
            case 19:
            case 20:
            case 21:
            case 22:
                return true;
            default:
                return false;
        }
    }

    public PresentationKgPINView(Context context) {
        super(context);
    }

    public PresentationKgPINView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        return this.mPasswordEntry.requestFocus(i, rect);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        PasswordTextView passwordTextView = (PasswordTextView) findViewById(getPasswordTextViewId());
        this.mPasswordEntry = passwordTextView;
        passwordTextView.setOnKeyListener(this);
        View findViewById = findViewById(R$id.key_enter);
        this.mOkButton = findViewById;
        if (findViewById != null) {
            findViewById.setOnTouchListener(this);
            this.mOkButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (PresentationKgPINView.this.mPasswordEntry.isEnabled()) {
                        PresentationKgPINView.this.verifyPasswordAndUnlock();
                    }
                }
            });
            this.mOkButton.setOnHoverListener(new LiftToActivateListener((AccessibilityManager) this.mContext.getSystemService("accessibility")));
        }
        View findViewById2 = findViewById(R$id.delete_button);
        this.mDeleteButton = findViewById2;
        findViewById2.setVisibility(0);
        this.mDeleteButton.setOnTouchListener(this);
        this.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PresentationKgPINView.this.mPasswordEntry.isEnabled()) {
                    PresentationKgPINView.this.mPasswordEntry.deleteLastChar();
                }
            }
        });
        this.mDeleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (PresentationKgPINView.this.mPasswordEntry.isEnabled()) {
                    PresentationKgPINView.this.resetPasswordText(true, true);
                }
                PresentationKgPINView.this.doHapticKeyClick();
                return true;
            }
        });
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateInputState();
        updateBottomMessage(false);
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 0) {
            return false;
        }
        doHapticKeyClick();
        return false;
    }

    /* access modifiers changed from: protected */
    public int getPasswordTextViewId() {
        return R$id.pinEntry;
    }

    /* access modifiers changed from: protected */
    public void resetPasswordText(boolean z, boolean z2) {
        this.mPasswordEntry.reset(z, z2);
    }

    /* access modifiers changed from: protected */
    public LockscreenCredential getEnteredCredential() {
        return LockscreenCredential.createPinOrNone(this.mPasswordEntry.getText());
    }

    /* access modifiers changed from: protected */
    public void setPasswordEntryInputEnabled(boolean z) {
        this.mPasswordEntry.setEnabled(z);
        this.mOkButton.setEnabled(z);
        if (z && !this.mPasswordEntry.hasFocus()) {
            this.mPasswordEntry.requestFocus();
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if ((i >= 7 && i <= 16) || (i >= 144 && i <= 153)) {
            String keyCodeToString = KeyEvent.keyCodeToString(i);
            this.mPasswordEntry.append(keyCodeToString.charAt(keyCodeToString.length() - 1));
        } else if (i == 67) {
            this.mPasswordEntry.deleteLastChar();
        } else if (KeyEvent.isConfirmKey(i)) {
            verifyPasswordAndUnlock();
        } else if (isDirectionKey(i)) {
            return super.onKeyDown(i, keyEvent);
        }
        return true;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            return onKeyDown(i, keyEvent);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void updateBottomMessage(boolean z) {
        int i;
        PtKDMCallback ptKDMCallback = this.mPtKDMCallback;
        if (ptKDMCallback == null) {
            Log.d("PresentationKgAbsKeyInputView", "RDP: updateBottomMessage()  mPtKDMCallback is null.");
            return;
        }
        if (ptKDMCallback.getFailedCount() >= 5 || this.mPtKDMCallback.onceLockout()) {
            i = R$string.zz_moto_rdp_pin_max_wrong_tip;
        } else if (z) {
            i = R$string.kg_wrong_pin;
        } else {
            i = R$string.zz_moto_rdp_pin_tip;
        }
        this.mKpMessageUpdateListener.update(getResources().getString(i));
    }

    /* access modifiers changed from: protected */
    public void updateInputState() {
        PtKDMCallback ptKDMCallback = this.mPtKDMCallback;
        if (ptKDMCallback == null) {
            return;
        }
        if (ptKDMCallback.getFailedCount() >= 5 || this.mPtKDMCallback.onceLockout()) {
            setPasswordEntryInputEnabled(false);
        }
    }

    public void showBouncer() {
        this.mPasswordEntry.requestFocus();
    }

    public void showClock() {
        this.mPasswordEntry.clearFocus();
        resetPasswordText(false, false);
    }
}
