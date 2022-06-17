package com.android.keyguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.telephony.PinResult;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardMessageAreaController;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.settingslib.Utils;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.moto.MotoFeature;

public class KeyguardSimPukViewController extends KeyguardPinBasedInputViewController<KeyguardSimPukView> {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    /* access modifiers changed from: private */
    public CheckSimPuk mCheckSimPukThread;
    /* access modifiers changed from: private */
    public TextView mDSSkipText;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    LiftToActivateListener mLiftToActivateListener;
    /* access modifiers changed from: private */
    public String mPinText;
    /* access modifiers changed from: private */
    public String mPukText;
    /* access modifiers changed from: private */
    public int mRemainingAttempts;
    private AlertDialog mRemainingAttemptsDialog;
    /* access modifiers changed from: private */
    public boolean mShowDefaultMessage;
    private ImageView mSimImageView;
    /* access modifiers changed from: private */
    public ProgressDialog mSimUnlockProgressDialog;
    /* access modifiers changed from: private */
    public StateMachine mStateMachine = new StateMachine();
    /* access modifiers changed from: private */
    public int mSubId = -1;
    private boolean mSupportDsSkipped = false;
    /* access modifiers changed from: private */
    public final TelephonyManager mTelephonyManager;
    KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onSimStateChanged(int i, int i2, int i3) {
            if (KeyguardSimPukViewController.DEBUG) {
                Log.v("KeyguardSimPukView", "onSimStateChanged(subId=" + i + ",state=" + i3 + ")");
            }
            if (!SubscriptionManager.isValidSubscriptionId(i) || KeyguardSimPukViewController.this.mSubId == i) {
                if (i3 == 5) {
                    int unused = KeyguardSimPukViewController.this.mRemainingAttempts = -1;
                    boolean unused2 = KeyguardSimPukViewController.this.mShowDefaultMessage = true;
                    KeyguardSimPukViewController.this.getKeyguardSecurityCallback().dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                }
                KeyguardSimPukViewController.this.resetState();
                return;
            }
            KeyguardSimPukViewController.this.resetState();
        }
    };

    /* access modifiers changed from: protected */
    public boolean shouldLockout(long j) {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    protected KeyguardSimPukViewController(KeyguardSimPukView keyguardSimPukView, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardSecurityModel.SecurityMode securityMode, LockPatternUtils lockPatternUtils, KeyguardSecurityCallback keyguardSecurityCallback, KeyguardMessageAreaController.Factory factory, LatencyTracker latencyTracker, LiftToActivateListener liftToActivateListener, TelephonyManager telephonyManager, FalsingCollector falsingCollector, EmergencyButtonController emergencyButtonController) {
        super(keyguardSimPukView, keyguardUpdateMonitor, securityMode, lockPatternUtils, keyguardSecurityCallback, factory, latencyTracker, liftToActivateListener, emergencyButtonController, falsingCollector);
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mTelephonyManager = telephonyManager;
        this.mSimImageView = (ImageView) ((KeyguardSimPukView) this.mView).findViewById(R$id.keyguard_sim);
        this.mLiftToActivateListener = liftToActivateListener;
        initDDSkipView();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        this.mKeyguardUpdateMonitor.registerCallback(this.mUpdateMonitorCallback);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        super.onViewDetached();
        this.mKeyguardUpdateMonitor.removeCallback(this.mUpdateMonitorCallback);
    }

    /* access modifiers changed from: package-private */
    public void resetState() {
        super.resetState();
        this.mStateMachine.reset();
    }

    /* access modifiers changed from: protected */
    public void verifyPasswordAndUnlock() {
        this.mStateMachine.next();
    }

    private class StateMachine {
        private int mState;

        private StateMachine() {
            this.mState = 0;
        }

        public void next() {
            int i;
            int i2 = this.mState;
            if (i2 == 0) {
                if (KeyguardSimPukViewController.this.checkPuk()) {
                    this.mState = 1;
                    i = R$string.kg_puk_enter_pin_hint;
                } else {
                    i = R$string.kg_invalid_sim_puk_hint;
                }
            } else if (i2 == 1) {
                if (KeyguardSimPukViewController.this.checkPin()) {
                    this.mState = 2;
                    i = R$string.kg_enter_confirm_pin_hint;
                } else {
                    i = R$string.kg_invalid_sim_pin_hint;
                }
            } else if (i2 != 2) {
                i = 0;
            } else if (KeyguardSimPukViewController.this.confirmPin()) {
                this.mState = 3;
                i = R$string.keyguard_sim_unlock_progress_dialog_message;
                KeyguardSimPukViewController.this.updateSim();
            } else {
                this.mState = 1;
                i = R$string.kg_invalid_confirm_pin_hint;
            }
            ((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).resetPasswordText(true, true);
            if (i != 0) {
                KeyguardSimPukViewController.this.mMessageAreaController.setMessage(i);
            }
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            String unused = KeyguardSimPukViewController.this.mPinText = "";
            String unused2 = KeyguardSimPukViewController.this.mPukText = "";
            int i = 0;
            this.mState = 0;
            KeyguardSimPukViewController.this.handleSubInfoChangeIfNeeded();
            if (KeyguardSimPukViewController.this.mShowDefaultMessage) {
                KeyguardSimPukViewController.this.showDefaultMessage();
            }
            boolean isEsimLocked = KeyguardEsimArea.isEsimLocked(((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).getContext(), KeyguardSimPukViewController.this.mSubId);
            KeyguardEsimArea keyguardEsimArea = (KeyguardEsimArea) ((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).findViewById(R$id.keyguard_esim_area);
            if (!isEsimLocked) {
                i = 8;
            }
            keyguardEsimArea.setVisibility(i);
            KeyguardSimPukViewController.this.mPasswordEntry.requestFocus();
        }
    }

    /* access modifiers changed from: private */
    public void showDefaultMessage() {
        String str;
        CharSequence charSequence;
        int i = this.mRemainingAttempts;
        if (i >= 0) {
            KeyguardMessageAreaController keyguardMessageAreaController = this.mMessageAreaController;
            T t = this.mView;
            keyguardMessageAreaController.setMessage((CharSequence) ((KeyguardSimPukView) t).getPukPasswordErrorMessage(i, true, KeyguardEsimArea.isEsimLocked(((KeyguardSimPukView) t).getContext(), this.mSubId)));
            return;
        }
        int slotIndex = SubscriptionManager.getSlotIndex(this.mSubId) + 1;
        boolean isEsimLocked = KeyguardEsimArea.isEsimLocked(((KeyguardSimPukView) this.mView).getContext(), this.mSubId);
        TelephonyManager telephonyManager = this.mTelephonyManager;
        int activeModemCount = telephonyManager != null ? telephonyManager.getActiveModemCount() : 1;
        Resources resources = ((KeyguardSimPukView) this.mView).getResources();
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(((KeyguardSimPukView) this.mView).getContext(), 16842806);
        int i2 = 2;
        if (activeModemCount < 2) {
            str = resources.getString(R$string.kg_puk_enter_puk_hint);
            this.mKeyguardUpdateMonitor.clearSkippedSubId();
            TextView textView = this.mDSSkipText;
            if (textView != null) {
                textView.setVisibility(8);
            }
        } else {
            SubscriptionInfo subscriptionInfoForSubId = this.mKeyguardUpdateMonitor.getSubscriptionInfoForSubId(this.mSubId);
            if (subscriptionInfoForSubId != null) {
                charSequence = subscriptionInfoForSubId.getDisplayName();
            } else {
                charSequence = "";
            }
            String string = resources.getString(R$string.zz_moto_kg_puk_enter_puk_hint_multi, new Object[]{Integer.valueOf(slotIndex), charSequence});
            if (subscriptionInfoForSubId != null) {
                colorAttrDefaultColor = subscriptionInfoForSubId.getIconTint();
            }
            if (!this.mSupportDsSkipped || this.mKeyguardUpdateMonitor.getValidSimCount() < 2) {
                this.mKeyguardUpdateMonitor.clearSkippedSubId();
                TextView textView2 = this.mDSSkipText;
                if (textView2 != null) {
                    textView2.setVisibility(8);
                }
            } else {
                String string2 = resources.getString(R$string.ds_skip);
                int i3 = R$drawable.zz_moto_ic_sim_prev;
                this.mDSSkipText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R$drawable.zz_moto_ic_sim_skip, 0);
                if (this.mKeyguardUpdateMonitor.getLockedSimCount() >= 2) {
                    int skippedSubId = this.mKeyguardUpdateMonitor.getSkippedSubId();
                    if (slotIndex != 1) {
                        i2 = 1;
                    }
                    if (!SubscriptionManager.isValidSubscriptionId(skippedSubId)) {
                        string2 = resources.getString(R$string.ds_skip_sim_go, new Object[]{Integer.valueOf(i2)});
                    } else if (this.mKeyguardUpdateMonitor.isSubIdSkipped(this.mSubId)) {
                        this.mKeyguardUpdateMonitor.clearSkippedSubId();
                        string2 = resources.getString(R$string.ds_skip_sim_go, new Object[]{Integer.valueOf(i2)});
                    } else {
                        string2 = resources.getString(R$string.ds_skip_sim_back, new Object[]{Integer.valueOf(i2)});
                        this.mDSSkipText.setCompoundDrawablesRelativeWithIntrinsicBounds(i3, 0, 0, 0);
                    }
                }
                TextView textView3 = this.mDSSkipText;
                if (textView3 != null) {
                    textView3.setText(string2);
                    this.mDSSkipText.setContentDescription(string2);
                    this.mDSSkipText.setVisibility(0);
                }
            }
            str = string;
        }
        if (isEsimLocked) {
            str = resources.getString(R$string.kg_sim_lock_esim_instructions, new Object[]{str});
        }
        this.mMessageAreaController.setMessage((CharSequence) str);
        this.mSimImageView.setImageTintList(ColorStateList.valueOf(colorAttrDefaultColor));
        new CheckSimPuk("", "", this.mSubId) {
            /* access modifiers changed from: package-private */
            public void onSimLockChangedResponse(PinResult pinResult) {
                if (pinResult == null) {
                    Log.e("KeyguardSimPukView", "onSimCheckResponse, pin result is NULL");
                    return;
                }
                Log.d("KeyguardSimPukView", "onSimCheckResponse  empty One result " + pinResult.toString());
                if (pinResult.getAttemptsRemaining() >= 0) {
                    int unused = KeyguardSimPukViewController.this.mRemainingAttempts = pinResult.getAttemptsRemaining();
                    KeyguardSimPukViewController keyguardSimPukViewController = KeyguardSimPukViewController.this;
                    keyguardSimPukViewController.mMessageAreaController.setMessage((CharSequence) ((KeyguardSimPukView) keyguardSimPukViewController.mView).getPukPasswordErrorMessage(pinResult.getAttemptsRemaining(), true, KeyguardEsimArea.isEsimLocked(((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).getContext(), KeyguardSimPukViewController.this.mSubId)));
                }
            }
        }.start();
    }

    /* access modifiers changed from: private */
    public boolean checkPuk() {
        if (this.mPasswordEntry.getText().length() < 8) {
            return false;
        }
        this.mPukText = this.mPasswordEntry.getText();
        return true;
    }

    /* access modifiers changed from: private */
    public boolean checkPin() {
        int length = this.mPasswordEntry.getText().length();
        if (length < 4 || length > 8) {
            return false;
        }
        this.mPinText = this.mPasswordEntry.getText();
        return true;
    }

    public boolean confirmPin() {
        return this.mPinText.equals(this.mPasswordEntry.getText());
    }

    /* access modifiers changed from: private */
    public void updateSim() {
        showDialogWithoutBackButton(getSimUnlockProgressDialog());
        if (this.mCheckSimPukThread == null) {
            C06233 r0 = new CheckSimPuk(this.mPukText, this.mPinText, this.mSubId) {
                /* access modifiers changed from: package-private */
                public void onSimLockChangedResponse(PinResult pinResult) {
                    ((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).post(new KeyguardSimPukViewController$3$$ExternalSyntheticLambda0(this, pinResult));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onSimLockChangedResponse$0(PinResult pinResult) {
                    if (KeyguardSimPukViewController.this.mSimUnlockProgressDialog != null) {
                        KeyguardSimPukViewController.this.mSimUnlockProgressDialog.hide();
                    }
                    ((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).resetPasswordText(true, pinResult.getResult() != 0);
                    if (pinResult.getResult() == 0) {
                        KeyguardSimPukViewController.this.mKeyguardUpdateMonitor.reportSimUnlocked(KeyguardSimPukViewController.this.mSubId);
                        int unused = KeyguardSimPukViewController.this.mRemainingAttempts = -1;
                        boolean unused2 = KeyguardSimPukViewController.this.mShowDefaultMessage = true;
                        KeyguardSimPukViewController.this.getKeyguardSecurityCallback().dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                    } else {
                        boolean unused3 = KeyguardSimPukViewController.this.mShowDefaultMessage = false;
                        if (pinResult.getResult() == 1) {
                            KeyguardSimPukViewController keyguardSimPukViewController = KeyguardSimPukViewController.this;
                            keyguardSimPukViewController.mMessageAreaController.setMessage((CharSequence) ((KeyguardSimPukView) keyguardSimPukViewController.mView).getPukPasswordErrorMessage(pinResult.getAttemptsRemaining(), false, KeyguardEsimArea.isEsimLocked(((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).getContext(), KeyguardSimPukViewController.this.mSubId)));
                            if (pinResult.getAttemptsRemaining() <= 2) {
                                KeyguardSimPukViewController keyguardSimPukViewController2 = KeyguardSimPukViewController.this;
                                keyguardSimPukViewController2.showDialogWithoutBackButton(keyguardSimPukViewController2.getPukRemainingAttemptsDialog(pinResult.getAttemptsRemaining()));
                            } else {
                                KeyguardSimPukViewController keyguardSimPukViewController3 = KeyguardSimPukViewController.this;
                                keyguardSimPukViewController3.mMessageAreaController.setMessage((CharSequence) ((KeyguardSimPukView) keyguardSimPukViewController3.mView).getPukPasswordErrorMessage(pinResult.getAttemptsRemaining(), false, KeyguardEsimArea.isEsimLocked(((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).getContext(), KeyguardSimPukViewController.this.mSubId)));
                            }
                        } else {
                            KeyguardSimPukViewController keyguardSimPukViewController4 = KeyguardSimPukViewController.this;
                            keyguardSimPukViewController4.mMessageAreaController.setMessage((CharSequence) ((KeyguardSimPukView) keyguardSimPukViewController4.mView).getResources().getString(R$string.kg_password_puk_failed));
                        }
                        if (KeyguardSimPukViewController.DEBUG) {
                            Log.d("KeyguardSimPukView", "verifyPasswordAndUnlock  UpdateSim.onSimCheckResponse:  attemptsRemaining=" + pinResult.getAttemptsRemaining());
                        }
                    }
                    KeyguardSimPukViewController.this.mStateMachine.reset();
                    CheckSimPuk unused4 = KeyguardSimPukViewController.this.mCheckSimPukThread = null;
                }
            };
            this.mCheckSimPukThread = r0;
            r0.start();
        }
    }

    private Dialog getSimUnlockProgressDialog() {
        if (this.mSimUnlockProgressDialog == null) {
            ProgressDialog progressDialog = new ProgressDialog(((KeyguardSimPukView) this.mView).getContext());
            this.mSimUnlockProgressDialog = progressDialog;
            progressDialog.setMessage(((KeyguardSimPukView) this.mView).getResources().getString(R$string.kg_sim_unlock_progress_dialog_message));
            this.mSimUnlockProgressDialog.setIndeterminate(true);
            this.mSimUnlockProgressDialog.setCancelable(false);
            if (!(((KeyguardSimPukView) this.mView).getContext() instanceof Activity)) {
                this.mSimUnlockProgressDialog.getWindow().setType(2009);
            }
        }
        return this.mSimUnlockProgressDialog;
    }

    /* access modifiers changed from: private */
    public void handleSubInfoChangeIfNeeded() {
        if (IccCardConstants.State.PUK_REQUIRED != this.mKeyguardUpdateMonitor.getFirstUnSkippedLockedSIMState(true)) {
            if (getKeyguardSecurityCallback() != null) {
                getKeyguardSecurityCallback().dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
            }
            this.mSubId = -1;
            this.mShowDefaultMessage = false;
            return;
        }
        int nextSubIdForState = this.mKeyguardUpdateMonitor.getNextSubIdForState(3);
        if (nextSubIdForState != this.mSubId && SubscriptionManager.isValidSubscriptionId(nextSubIdForState)) {
            this.mSubId = nextSubIdForState;
            this.mShowDefaultMessage = true;
            this.mRemainingAttempts = -1;
        }
    }

    /* access modifiers changed from: private */
    public Dialog getPukRemainingAttemptsDialog(int i) {
        T t = this.mView;
        String pukPasswordErrorMessage = ((KeyguardSimPukView) t).getPukPasswordErrorMessage(i, false, KeyguardEsimArea.isEsimLocked(((KeyguardSimPukView) t).getContext(), this.mSubId));
        this.mMessageAreaController.setMessage((CharSequence) pukPasswordErrorMessage);
        AlertDialog alertDialog = this.mRemainingAttemptsDialog;
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(((KeyguardSimPukView) this.mView).getContext());
            builder.setMessage(pukPasswordErrorMessage);
            builder.setCancelable(false);
            builder.setNeutralButton(R$string.f72ok, (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            this.mRemainingAttemptsDialog = create;
            create.getWindow().setType(2009);
        } else {
            alertDialog.setMessage(pukPasswordErrorMessage);
        }
        return this.mRemainingAttemptsDialog;
    }

    public void onPause() {
        ProgressDialog progressDialog = this.mSimUnlockProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.mSimUnlockProgressDialog = null;
        }
    }

    private abstract class CheckSimPuk extends Thread {
        private final String mPin;
        private final String mPuk;
        private final int mSubId;

        /* access modifiers changed from: package-private */
        /* renamed from: onSimLockChangedResponse */
        public abstract void lambda$run$0(PinResult pinResult);

        protected CheckSimPuk(String str, String str2, int i) {
            this.mPuk = str;
            this.mPin = str2;
            this.mSubId = i;
        }

        public void run() {
            if (KeyguardSimPukViewController.DEBUG) {
                Log.v("KeyguardSimPukView", "call supplyIccLockPuk(subid=" + this.mSubId + ")");
            }
            PinResult supplyIccLockPuk = KeyguardSimPukViewController.this.mTelephonyManager.createForSubscriptionId(this.mSubId).supplyIccLockPuk(this.mPuk, this.mPin);
            if (KeyguardSimPukViewController.DEBUG) {
                Log.v("KeyguardSimPukView", "supplyIccLockPuk returned: " + supplyIccLockPuk.toString());
            }
            ((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).post(new C0625x2e140b38(this, supplyIccLockPuk));
        }
    }

    private void initDDSkipView() {
        boolean supportDualSimPINSkiped = MotoFeature.getInstance(((KeyguardSimPukView) this.mView).getContext()).supportDualSimPINSkiped();
        this.mSupportDsSkipped = supportDualSimPINSkiped;
        if (supportDualSimPINSkiped) {
            TextView textView = (TextView) ((KeyguardSimPukView) this.mView).findViewById(R$id.ds_skip);
            this.mDSSkipText = textView;
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        ((KeyguardSimPukView) KeyguardSimPukViewController.this.mView).doHapticKeyClick();
                        if (TelephonyManager.getDefault().getSimCount() < 2) {
                            KeyguardSimPukViewController.this.mKeyguardUpdateMonitor.clearSkippedSubId();
                            KeyguardSimPukViewController.this.mDSSkipText.setVisibility(8);
                            return;
                        }
                        int skippedSubId = KeyguardSimPukViewController.this.mKeyguardUpdateMonitor.getSkippedSubId();
                        if (!SubscriptionManager.isValidSubscriptionId(skippedSubId)) {
                            KeyguardSimPukViewController.this.mKeyguardUpdateMonitor.setSkippedSubId(KeyguardSimPukViewController.this.mSubId);
                        } else if (skippedSubId != KeyguardSimPukViewController.this.mSubId) {
                            KeyguardSimPukViewController.this.mKeyguardUpdateMonitor.clearSkippedSubId();
                        }
                        IccCardConstants.State firstUnSkippedLockedSIMState = KeyguardSimPukViewController.this.mKeyguardUpdateMonitor.getFirstUnSkippedLockedSIMState(true);
                        if (firstUnSkippedLockedSIMState == IccCardConstants.State.UNKNOWN) {
                            KeyguardSimPukViewController.this.mKeyguardUpdateMonitor.reportSimUnlocked(KeyguardSimPukViewController.this.mSubId);
                            KeyguardSimPukViewController.this.mKeyguardUpdateMonitor.setRecentlySkipped(true);
                            if (KeyguardSimPukViewController.this.getKeyguardSecurityCallback() != null) {
                                KeyguardSimPukViewController.this.getKeyguardSecurityCallback().dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                            }
                        } else if (firstUnSkippedLockedSIMState == IccCardConstants.State.intToState(KeyguardSimPukViewController.this.mKeyguardUpdateMonitor.getSimState(KeyguardSimPukViewController.this.mSubId))) {
                            KeyguardSimPukViewController.this.reset();
                            KeyguardSimPukViewController.this.mPasswordEntry.invalidate();
                        } else if (KeyguardSimPukViewController.this.getKeyguardSecurityCallback() != null) {
                            KeyguardSimPukViewController.this.getKeyguardSecurityCallback().dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                        }
                    }
                });
                this.mDSSkipText.setOnHoverListener(this.mLiftToActivateListener);
            }
        }
    }

    public void onResume(int i) {
        super.onResume(i);
        if (DEBUG) {
            Log.d("KeyguardSimPukView", "onResume");
        }
        this.mSubId = -1;
        resetState();
    }

    /* access modifiers changed from: protected */
    public void onUserInput() {
        super.onUserInput();
        getKeyguardSecurityCallback().userActivity();
    }

    /* access modifiers changed from: private */
    public void showDialogWithoutBackButton(Dialog dialog) {
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(4194304);
    }
}
