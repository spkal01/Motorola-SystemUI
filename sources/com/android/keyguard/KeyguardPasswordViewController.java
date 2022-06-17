package com.android.keyguard;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.UserHandle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardMessageAreaController;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.settingslib.Utils;
import com.android.systemui.R$bool;
import com.android.systemui.R$id;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.List;

public class KeyguardPasswordViewController extends KeyguardAbsKeyInputViewController<KeyguardPasswordView> {
    private final InputMethodManager mInputMethodManager;
    /* access modifiers changed from: private */
    public final KeyguardSecurityCallback mKeyguardSecurityCallback;
    private final DelayableExecutor mMainExecutor;
    private boolean mNeedWaitFocus = false;
    private final TextView.OnEditorActionListener mOnEditorActionListener = new KeyguardPasswordViewController$$ExternalSyntheticLambda3(this);
    private EditText mPasswordEntry;
    private final boolean mShowImeAtScreenOn;
    private ImageView mSwitchImeButton;
    private final TextWatcher mTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            KeyguardPasswordViewController.this.mKeyguardSecurityCallback.userActivity();
        }

        public void afterTextChanged(Editable editable) {
            if (!TextUtils.isEmpty(editable)) {
                KeyguardPasswordViewController.this.onUserInput();
            }
        }
    };

    public boolean needsInput() {
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
        boolean z = keyEvent == null && (i == 0 || i == 6 || i == 5);
        boolean z2 = keyEvent != null && KeyEvent.isConfirmKey(keyEvent.getKeyCode()) && keyEvent.getAction() == 0;
        if (!z && !z2) {
            return false;
        }
        verifyPasswordAndUnlock();
        return true;
    }

    public void reloadColors() {
        super.reloadColors();
        int defaultColor = Utils.getColorAttr(((KeyguardPasswordView) this.mView).getContext(), 16842806).getDefaultColor();
        this.mPasswordEntry.setTextColor(defaultColor);
        this.mPasswordEntry.setHighlightColor(defaultColor);
        this.mPasswordEntry.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        this.mPasswordEntry.setForegroundTintList(ColorStateList.valueOf(defaultColor));
        this.mSwitchImeButton.setImageTintList(ColorStateList.valueOf(defaultColor));
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    protected KeyguardPasswordViewController(KeyguardPasswordView keyguardPasswordView, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardSecurityModel.SecurityMode securityMode, LockPatternUtils lockPatternUtils, KeyguardSecurityCallback keyguardSecurityCallback, KeyguardMessageAreaController.Factory factory, LatencyTracker latencyTracker, InputMethodManager inputMethodManager, EmergencyButtonController emergencyButtonController, DelayableExecutor delayableExecutor, Resources resources, FalsingCollector falsingCollector) {
        super(keyguardPasswordView, keyguardUpdateMonitor, securityMode, lockPatternUtils, keyguardSecurityCallback, factory, latencyTracker, falsingCollector, emergencyButtonController);
        this.mKeyguardSecurityCallback = keyguardSecurityCallback;
        this.mInputMethodManager = inputMethodManager;
        this.mMainExecutor = delayableExecutor;
        this.mShowImeAtScreenOn = resources.getBoolean(R$bool.kg_show_ime_at_screen_on);
        T t = this.mView;
        this.mPasswordEntry = (EditText) ((KeyguardPasswordView) t).findViewById(((KeyguardPasswordView) t).getPasswordTextViewId());
        this.mSwitchImeButton = (ImageView) ((KeyguardPasswordView) this.mView).findViewById(R$id.switch_ime_button);
        ((KeyguardPasswordView) this.mView).setOnWindowFocusChangeListener(new KeyguardPasswordViewController$$ExternalSyntheticLambda4(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(boolean z) {
        if (this.mNeedWaitFocus && z) {
            showInput();
        }
        this.mNeedWaitFocus = false;
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        this.mPasswordEntry.setTextOperationUser(UserHandle.of(KeyguardUpdateMonitor.getCurrentUser()));
        this.mPasswordEntry.setKeyListener(TextKeyListener.getInstance());
        this.mPasswordEntry.setInputType(129);
        this.mPasswordEntry.setSelected(true);
        this.mPasswordEntry.setOnEditorActionListener(this.mOnEditorActionListener);
        this.mPasswordEntry.addTextChangedListener(this.mTextWatcher);
        this.mPasswordEntry.setOnClickListener(new KeyguardPasswordViewController$$ExternalSyntheticLambda1(this));
        this.mSwitchImeButton.setOnClickListener(new KeyguardPasswordViewController$$ExternalSyntheticLambda0(this));
        View findViewById = ((KeyguardPasswordView) this.mView).findViewById(R$id.cancel_button);
        if (findViewById != null) {
            findViewById.setOnClickListener(new KeyguardPasswordViewController$$ExternalSyntheticLambda2(this));
        }
        updateSwitchImeButton();
        this.mMainExecutor.executeDelayed(new KeyguardPasswordViewController$$ExternalSyntheticLambda7(this), 500);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$2(View view) {
        this.mKeyguardSecurityCallback.userActivity();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$3(View view) {
        this.mKeyguardSecurityCallback.userActivity();
        this.mInputMethodManager.showInputMethodPickerFromSystem(false, ((KeyguardPasswordView) this.mView).getContext().getDisplayId());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$4(View view) {
        this.mKeyguardSecurityCallback.reset();
        this.mKeyguardSecurityCallback.onCancelClicked();
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        super.onViewDetached();
        this.mPasswordEntry.setOnEditorActionListener((TextView.OnEditorActionListener) null);
    }

    /* access modifiers changed from: package-private */
    public void resetState() {
        this.mPasswordEntry.setTextOperationUser(UserHandle.of(KeyguardUpdateMonitor.getCurrentUser()));
        this.mMessageAreaController.setMessage((CharSequence) "");
        boolean isEnabled = this.mPasswordEntry.isEnabled();
        ((KeyguardPasswordView) this.mView).setPasswordEntryEnabled(true);
        ((KeyguardPasswordView) this.mView).setPasswordEntryInputEnabled(true);
        if (this.mResumed && this.mPasswordEntry.isVisibleToUser() && isEnabled) {
            showInput();
        }
    }

    public void onResume(int i) {
        super.onResume(i);
        boolean hasWindowFocus = ((KeyguardPasswordView) this.mView).hasWindowFocus();
        this.mNeedWaitFocus = !hasWindowFocus;
        if (!hasWindowFocus) {
            return;
        }
        if (i != 1 || this.mShowImeAtScreenOn) {
            showInput();
        }
    }

    private void showInput() {
        ((KeyguardPasswordView) this.mView).post(new KeyguardPasswordViewController$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showInput$5() {
        if (((KeyguardPasswordView) this.mView).isShown()) {
            this.mPasswordEntry.requestFocus();
            this.mInputMethodManager.showSoftInput(this.mPasswordEntry, 1);
        }
    }

    public void onPause() {
        if (!this.mPasswordEntry.isVisibleToUser()) {
            super.onPause();
        } else {
            ((KeyguardPasswordView) this.mView).setOnFinishImeAnimationRunnable(new KeyguardPasswordViewController$$ExternalSyntheticLambda5(this));
        }
        if (this.mPasswordEntry.isAttachedToWindow()) {
            this.mPasswordEntry.getWindowInsetsController().hide(WindowInsets.Type.ime());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPause$6() {
        this.mPasswordEntry.clearFocus();
        super.onPause();
    }

    public void onStartingToHide() {
        if (this.mPasswordEntry.isAttachedToWindow()) {
            this.mPasswordEntry.getWindowInsetsController().hide(WindowInsets.Type.ime());
        }
    }

    /* access modifiers changed from: private */
    public void updateSwitchImeButton() {
        boolean z = this.mSwitchImeButton.getVisibility() == 0;
        boolean hasMultipleEnabledIMEsOrSubtypes = hasMultipleEnabledIMEsOrSubtypes(this.mInputMethodManager, false);
        if (z != hasMultipleEnabledIMEsOrSubtypes) {
            this.mSwitchImeButton.setVisibility(hasMultipleEnabledIMEsOrSubtypes ? 0 : 8);
        }
        if (this.mSwitchImeButton.getVisibility() != 0) {
            ViewGroup.LayoutParams layoutParams = this.mPasswordEntry.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) layoutParams).setMarginStart(0);
                this.mPasswordEntry.setLayoutParams(layoutParams);
            }
        }
    }

    private boolean hasMultipleEnabledIMEsOrSubtypes(InputMethodManager inputMethodManager, boolean z) {
        int i = 0;
        for (InputMethodInfo inputMethodInfo : inputMethodManager.getEnabledInputMethodListAsUser(KeyguardUpdateMonitor.getCurrentUser())) {
            if (i > 1) {
                return true;
            }
            List<InputMethodSubtype> enabledInputMethodSubtypeList = inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true);
            if (!enabledInputMethodSubtypeList.isEmpty()) {
                int i2 = 0;
                for (InputMethodSubtype isAuxiliary : enabledInputMethodSubtypeList) {
                    if (isAuxiliary.isAuxiliary()) {
                        i2++;
                    }
                }
                if (enabledInputMethodSubtypeList.size() - i2 <= 0) {
                    if (z) {
                        if (i2 <= 1) {
                        }
                    }
                }
            }
            i++;
        }
        if (i > 1 || inputMethodManager.getEnabledInputMethodSubtypeList((InputMethodInfo) null, false).size() > 1) {
            return true;
        }
        return false;
    }
}
