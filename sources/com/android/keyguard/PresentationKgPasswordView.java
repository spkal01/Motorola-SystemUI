package com.android.keyguard;

import android.content.Context;
import android.os.IBinder;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.statusbar.CommandQueue;
import com.motorola.android.provider.MotorolaSettings;

public class PresentationKgPasswordView extends PresentationKgAbsKeyInputView implements TextView.OnEditorActionListener, View.OnClickListener, CommandQueue.Callbacks {
    private CommandQueue mCommandQueue;
    private ImageButton mEnterDone;
    private float mFontScale;
    private ImeStatusCallback mImeStatusCallback;
    private InputMethodManager mImm;
    private EditText mPasswordEntry;
    private TextViewInputDisabler mPasswordEntryDisabler;

    interface ImeStatusCallback {
        void updateImeStatus(boolean z);
    }

    public PresentationKgPasswordView(Context context) {
        super(context);
    }

    public PresentationKgPasswordView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCommandQueue = (CommandQueue) Dependency.get(CommandQueue.class);
        this.mFontScale = MotorolaSettings.Global.getFloat(this.mContext.getContentResolver(), "desktop_font_size_scale", 1.0f);
        this.mImm = (InputMethodManager) getContext().getSystemService("input_method");
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPasswordEntry = (EditText) findViewById(getPasswordTextViewId());
        this.mPasswordEntryDisabler = new TextViewInputDisabler(this.mPasswordEntry);
        this.mPasswordEntry.setKeyListener(TextKeyListener.getInstance());
        this.mPasswordEntry.setInputType(129);
        this.mPasswordEntry.setOnEditorActionListener(this);
        ImageButton imageButton = (ImageButton) findViewById(R$id.enter_done);
        this.mEnterDone = imageButton;
        imageButton.setOnClickListener(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        showSoftInput();
        updateInputState();
        updateBottomMessage(false);
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mCommandQueue.removeCallback((CommandQueue.Callbacks) this);
    }

    public void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z) {
        if (PresentationKgAbsKeyInputView.DEBUG) {
            Log.d("PresentationKgPasswordView", "displayId=" + i + "  vis=" + i2 + "  backDisposition=" + i3 + "  showImeSwitcher=" + z);
        }
        boolean z2 = true;
        if (i2 != 1) {
            if ((i2 & 2) == 0 || i3 != 0) {
                z2 = false;
            }
            this.mImeStatusCallback.updateImeStatus(z2);
        }
    }

    /* access modifiers changed from: protected */
    public int getPasswordTextViewId() {
        return R$id.passwordEntry;
    }

    /* access modifiers changed from: protected */
    public void resetPasswordText(boolean z, boolean z2) {
        this.mPasswordEntry.setText("");
    }

    /* access modifiers changed from: protected */
    public LockscreenCredential getEnteredCredential() {
        return LockscreenCredential.createPasswordOrNone(this.mPasswordEntry.getText());
    }

    /* access modifiers changed from: protected */
    public void setPasswordEntryInputEnabled(boolean z) {
        this.mPasswordEntryDisabler.setInputEnabled(z);
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean z = keyEvent == null && (i == 0 || i == 6 || i == 5);
        boolean z2 = keyEvent != null && KeyEvent.isConfirmKey(keyEvent.getKeyCode()) && keyEvent.getAction() == 0;
        if (!z && !z2) {
            return false;
        }
        verifyPasswordAndUnlock();
        return true;
    }

    public void showSoftInput() {
        post(new PresentationKgPasswordView$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSoftInput$0() {
        if (PresentationKgAbsKeyInputView.DEBUG) {
            Log.d("PresentationKgPasswordView", "RDP: showSoftInput: isShown()=" + isShown());
        }
        if (isShown() && this.mPasswordEntry.isEnabled()) {
            this.mPasswordEntry.requestFocus();
            this.mImm.showSoftInput(this.mPasswordEntry, 1);
        }
    }

    public void onClick(View view) {
        verifyPasswordAndUnlock();
    }

    /* access modifiers changed from: protected */
    public void updateBottomMessage(boolean z) {
        int i;
        PtKDMCallback ptKDMCallback = this.mPtKDMCallback;
        if (ptKDMCallback == null) {
            Log.d("PresentationKgPasswordView", "RDP: updateBottomMessage()  mPtKDMCallback is null.");
            return;
        }
        if (ptKDMCallback.getFailedCount() >= 5 || this.mPtKDMCallback.onceLockout()) {
            i = R$string.zz_moto_rdp_password_max_wrong_tip;
        } else if (z) {
            i = R$string.kg_wrong_password;
        } else {
            i = R$string.zz_moto_rdp_password_tip;
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

    public void setImeWindowStatus(ImeStatusCallback imeStatusCallback) {
        this.mImeStatusCallback = imeStatusCallback;
    }

    public void showBouncer() {
        showSoftInput();
    }

    public void showClock() {
        hideSoftInput();
        this.mPasswordEntry.setText("");
    }

    private void hideSoftInput() {
        this.mPasswordEntry.clearFocus();
    }
}
