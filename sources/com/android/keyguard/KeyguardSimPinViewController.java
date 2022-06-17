package com.android.keyguard;

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
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.moto.MotoFeature;

public class KeyguardSimPinViewController extends KeyguardPinBasedInputViewController<KeyguardSimPinView> {
    /* access modifiers changed from: private */
    public CheckSimPin mCheckSimPinThread;
    /* access modifiers changed from: private */
    public TextView mDSSkipText;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    LiftToActivateListener mLiftToActivateListener;
    /* access modifiers changed from: private */
    public int mRemainingAttempts;
    private AlertDialog mRemainingAttemptsDialog;
    /* access modifiers changed from: private */
    public boolean mShowDefaultMessage;
    private ImageView mSimImageView;
    /* access modifiers changed from: private */
    public ProgressDialog mSimUnlockProgressDialog;
    private int mSlotId;
    /* access modifiers changed from: private */
    public int mSubId = -1;
    private boolean mSupportDsSkipped = false;
    /* access modifiers changed from: private */
    public final TelephonyManager mTelephonyManager;
    KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onSimStateChanged(int i, int i2, int i3) {
            Log.v("KeyguardSimPinView", "onSimStateChanged(subId=" + i + ",state=" + i3 + ")");
            if (SubscriptionManager.isValidSubscriptionId(i) && KeyguardSimPinViewController.this.mSubId != i) {
                KeyguardSimPinViewController.this.resetState();
            } else if (i3 == 3) {
                if (KeyguardSimPinViewController.this.getKeyguardSecurityCallback() != null) {
                    KeyguardSimPinViewController.this.getKeyguardSecurityCallback().dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                }
            } else if (i3 == 5) {
                int unused = KeyguardSimPinViewController.this.mRemainingAttempts = -1;
                KeyguardSimPinViewController.this.resetState();
            } else {
                KeyguardSimPinViewController.this.resetState();
            }
        }
    };

    public boolean startDisappearAnimation(Runnable runnable) {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    protected KeyguardSimPinViewController(KeyguardSimPinView keyguardSimPinView, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardSecurityModel.SecurityMode securityMode, LockPatternUtils lockPatternUtils, KeyguardSecurityCallback keyguardSecurityCallback, KeyguardMessageAreaController.Factory factory, LatencyTracker latencyTracker, LiftToActivateListener liftToActivateListener, TelephonyManager telephonyManager, FalsingCollector falsingCollector, EmergencyButtonController emergencyButtonController) {
        super(keyguardSimPinView, keyguardUpdateMonitor, securityMode, lockPatternUtils, keyguardSecurityCallback, factory, latencyTracker, liftToActivateListener, emergencyButtonController, falsingCollector);
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mTelephonyManager = telephonyManager;
        this.mSimImageView = (ImageView) ((KeyguardSimPinView) this.mView).findViewById(R$id.keyguard_sim);
        this.mLiftToActivateListener = liftToActivateListener;
        initDDSkipView();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
    }

    /* access modifiers changed from: package-private */
    public void resetState() {
        super.resetState();
        Log.v("KeyguardSimPinView", "Resetting state");
        handleSubInfoChangeIfNeeded();
        this.mMessageAreaController.setMessage((CharSequence) "");
        if (this.mShowDefaultMessage) {
            showDefaultMessage();
        }
        T t = this.mView;
        ((KeyguardSimPinView) t).setEsimLocked(KeyguardEsimArea.isEsimLocked(((KeyguardSimPinView) t).getContext(), this.mSubId));
    }

    public void onResume(int i) {
        super.onResume(i);
        this.mKeyguardUpdateMonitor.registerCallback(this.mUpdateMonitorCallback);
        ((KeyguardSimPinView) this.mView).resetState();
    }

    public void onPause() {
        this.mKeyguardUpdateMonitor.removeCallback(this.mUpdateMonitorCallback);
        ProgressDialog progressDialog = this.mSimUnlockProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.mSimUnlockProgressDialog = null;
        }
    }

    /* access modifiers changed from: protected */
    public void verifyPasswordAndUnlock() {
        String text = this.mPasswordEntry.getText();
        if (text.length() < 4 || text.length() > 8) {
            this.mMessageAreaController.setMessage(R$string.kg_invalid_sim_pin_hint);
            ((KeyguardSimPinView) this.mView).resetPasswordText(true, true);
            getKeyguardSecurityCallback().userActivity();
            return;
        }
        showDialogWithoutBackButton(getSimUnlockProgressDialog());
        if (this.mCheckSimPinThread == null) {
            C06172 r0 = new CheckSimPin(this.mPasswordEntry.getText(), this.mSubId) {
                /* access modifiers changed from: package-private */
                public void onSimCheckResponse(PinResult pinResult) {
                    ((KeyguardSimPinView) KeyguardSimPinViewController.this.mView).post(new KeyguardSimPinViewController$2$$ExternalSyntheticLambda0(this, pinResult));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onSimCheckResponse$0(PinResult pinResult) {
                    int unused = KeyguardSimPinViewController.this.mRemainingAttempts = pinResult.getAttemptsRemaining();
                    if (KeyguardSimPinViewController.this.mSimUnlockProgressDialog != null) {
                        KeyguardSimPinViewController.this.mSimUnlockProgressDialog.hide();
                    }
                    ((KeyguardSimPinView) KeyguardSimPinViewController.this.mView).resetPasswordText(true, pinResult.getResult() != 0);
                    if (pinResult.getResult() == 0) {
                        KeyguardSimPinViewController.this.mKeyguardUpdateMonitor.reportSimUnlocked(KeyguardSimPinViewController.this.mSubId);
                        int unused2 = KeyguardSimPinViewController.this.mRemainingAttempts = -1;
                        boolean unused3 = KeyguardSimPinViewController.this.mShowDefaultMessage = true;
                        KeyguardSimPinViewController.this.getKeyguardSecurityCallback().dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                    } else {
                        boolean unused4 = KeyguardSimPinViewController.this.mShowDefaultMessage = false;
                        if (pinResult.getResult() != 1) {
                            KeyguardSimPinViewController keyguardSimPinViewController = KeyguardSimPinViewController.this;
                            keyguardSimPinViewController.mMessageAreaController.setMessage((CharSequence) ((KeyguardSimPinView) keyguardSimPinViewController.mView).getResources().getString(R$string.kg_password_pin_failed));
                        } else if (pinResult.getAttemptsRemaining() <= 2) {
                            KeyguardSimPinViewController keyguardSimPinViewController2 = KeyguardSimPinViewController.this;
                            keyguardSimPinViewController2.showDialogWithoutBackButton(keyguardSimPinViewController2.getSimRemainingAttemptsDialog(pinResult.getAttemptsRemaining()));
                        } else {
                            KeyguardSimPinViewController keyguardSimPinViewController3 = KeyguardSimPinViewController.this;
                            keyguardSimPinViewController3.mMessageAreaController.setMessage((CharSequence) keyguardSimPinViewController3.getPinPasswordErrorMessage(pinResult.getAttemptsRemaining(), false));
                        }
                        Log.d("KeyguardSimPinView", "verifyPasswordAndUnlock  CheckSimPin.onSimCheckResponse: " + pinResult + " attemptsRemaining=" + pinResult.getAttemptsRemaining());
                    }
                    KeyguardSimPinViewController.this.getKeyguardSecurityCallback().userActivity();
                    CheckSimPin unused5 = KeyguardSimPinViewController.this.mCheckSimPinThread = null;
                }
            };
            this.mCheckSimPinThread = r0;
            r0.start();
        }
    }

    private Dialog getSimUnlockProgressDialog() {
        if (this.mSimUnlockProgressDialog == null) {
            ProgressDialog progressDialog = new ProgressDialog(((KeyguardSimPinView) this.mView).getContext());
            this.mSimUnlockProgressDialog = progressDialog;
            progressDialog.setMessage(((KeyguardSimPinView) this.mView).getResources().getString(R$string.kg_sim_unlock_progress_dialog_message));
            this.mSimUnlockProgressDialog.setIndeterminate(true);
            this.mSimUnlockProgressDialog.setCancelable(false);
            this.mSimUnlockProgressDialog.getWindow().setType(2009);
        }
        return this.mSimUnlockProgressDialog;
    }

    /* access modifiers changed from: private */
    public Dialog getSimRemainingAttemptsDialog(int i) {
        String pinPasswordErrorMessage = getPinPasswordErrorMessage(i, false);
        this.mMessageAreaController.setMessage((CharSequence) pinPasswordErrorMessage);
        AlertDialog alertDialog = this.mRemainingAttemptsDialog;
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(((KeyguardSimPinView) this.mView).getContext());
            builder.setMessage(pinPasswordErrorMessage);
            builder.setCancelable(false);
            builder.setNeutralButton(R$string.f72ok, (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            this.mRemainingAttemptsDialog = create;
            create.getWindow().setType(2009);
        } else {
            alertDialog.setMessage(pinPasswordErrorMessage);
        }
        return this.mRemainingAttemptsDialog;
    }

    /* access modifiers changed from: private */
    public void showDialogWithoutBackButton(Dialog dialog) {
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(4194304);
    }

    /* access modifiers changed from: private */
    public String getPinPasswordErrorMessage(int i, boolean z) {
        String str;
        if (i == 0) {
            str = ((KeyguardSimPinView) this.mView).getResources().getString(R$string.kg_password_wrong_pin_code_pukked);
        } else if (i > 0) {
            Resources resources = getContext().getResources();
            int i2 = R$plurals.kg_password_wrong_pin_code_with_sim_id;
            Object[] objArr = new Object[2];
            int i3 = this.mSlotId;
            objArr[0] = i3 > 0 ? String.valueOf(i3) : "";
            objArr[1] = Integer.valueOf(i);
            str = resources.getQuantityString(i2, i, objArr);
        } else {
            str = ((KeyguardSimPinView) this.mView).getResources().getString(z ? R$string.kg_sim_pin_instructions : R$string.kg_password_pin_failed);
        }
        if (KeyguardEsimArea.isEsimLocked(((KeyguardSimPinView) this.mView).getContext(), this.mSubId)) {
            str = ((KeyguardSimPinView) this.mView).getResources().getString(R$string.kg_sim_lock_esim_instructions, new Object[]{str});
        }
        Log.d("KeyguardSimPinView", "getPinPasswordErrorMessage: attemptsRemaining=" + i + " displayMessage=" + str);
        return str;
    }

    private void showDefaultMessage() {
        setLockedSimMessage();
        if (this.mRemainingAttempts < 0) {
            new CheckSimPin("", this.mSubId) {
                /* access modifiers changed from: package-private */
                public void onSimCheckResponse(PinResult pinResult) {
                    Log.d("KeyguardSimPinView", "onSimCheckResponse  empty One result " + pinResult.toString());
                    if (pinResult.getAttemptsRemaining() >= 0) {
                        int unused = KeyguardSimPinViewController.this.mRemainingAttempts = pinResult.getAttemptsRemaining();
                        KeyguardSimPinViewController.this.setLockedSimMessage();
                    }
                }
            }.start();
        }
    }

    private abstract class CheckSimPin extends Thread {
        private final String mPin;
        private int mSubId;

        /* access modifiers changed from: package-private */
        /* renamed from: onSimCheckResponse */
        public abstract void lambda$run$0(PinResult pinResult);

        protected CheckSimPin(String str, int i) {
            this.mPin = str;
            this.mSubId = i;
        }

        public void run() {
            Log.v("KeyguardSimPinView", "call supplyIccLockPin(subid=" + this.mSubId + ")");
            PinResult supplyIccLockPin = KeyguardSimPinViewController.this.mTelephonyManager.createForSubscriptionId(this.mSubId).supplyIccLockPin(this.mPin);
            Log.v("KeyguardSimPinView", "supplyIccLockPin returned: " + supplyIccLockPin.toString());
            ((KeyguardSimPinView) KeyguardSimPinViewController.this.mView).post(new C0620xfea10a16(this, supplyIccLockPin));
        }
    }

    /* access modifiers changed from: private */
    public void setLockedSimMessage() {
        String str;
        boolean isEsimLocked = KeyguardEsimArea.isEsimLocked(((KeyguardSimPinView) this.mView).getContext(), this.mSubId);
        this.mSlotId = SubscriptionManager.getSlotIndex(this.mSubId) + 1;
        TelephonyManager telephonyManager = this.mTelephonyManager;
        int activeModemCount = telephonyManager != null ? telephonyManager.getActiveModemCount() : 1;
        Resources resources = ((KeyguardSimPinView) this.mView).getResources();
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(((KeyguardSimPinView) this.mView).getContext(), 16842806);
        int i = 2;
        if (activeModemCount < 2) {
            str = resources.getString(R$string.kg_sim_pin_instructions);
            this.mKeyguardUpdateMonitor.clearSkippedSubId();
            TextView textView = this.mDSSkipText;
            if (textView != null) {
                textView.setVisibility(8);
            }
            this.mSlotId = -1;
        } else {
            SubscriptionInfo subscriptionInfoForSubId = this.mKeyguardUpdateMonitor.getSubscriptionInfoForSubId(this.mSubId);
            String string = resources.getString(R$string.zz_moto_kg_sim_pin_instructions_multi, new Object[]{Integer.valueOf(this.mSlotId), subscriptionInfoForSubId != null ? subscriptionInfoForSubId.getDisplayName() : ""});
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
                int i2 = R$drawable.zz_moto_ic_sim_prev;
                this.mDSSkipText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R$drawable.zz_moto_ic_sim_skip, 0);
                if (this.mKeyguardUpdateMonitor.getLockedSimCount() >= 2) {
                    if (this.mSlotId != 1) {
                        i = 1;
                    }
                    if (!SubscriptionManager.isValidSubscriptionId(this.mKeyguardUpdateMonitor.getSkippedSubId())) {
                        string2 = resources.getString(R$string.ds_skip_sim_go, new Object[]{Integer.valueOf(i)});
                    } else if (this.mKeyguardUpdateMonitor.isSubIdSkipped(this.mSubId)) {
                        this.mKeyguardUpdateMonitor.clearSkippedSubId();
                        string2 = resources.getString(R$string.ds_skip_sim_go, new Object[]{Integer.valueOf(i)});
                    } else {
                        string2 = resources.getString(R$string.ds_skip_sim_back, new Object[]{Integer.valueOf(i)});
                        this.mDSSkipText.setCompoundDrawablesRelativeWithIntrinsicBounds(i2, 0, 0, 0);
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
        if (((KeyguardSimPinView) this.mView).getVisibility() == 0) {
            this.mMessageAreaController.setMessage((CharSequence) str);
        }
        this.mSimImageView.setImageTintList(ColorStateList.valueOf(colorAttrDefaultColor));
    }

    private void handleSubInfoChangeIfNeeded() {
        if (IccCardConstants.State.PIN_REQUIRED != this.mKeyguardUpdateMonitor.getFirstUnSkippedLockedSIMState(true)) {
            this.mSubId = -1;
            this.mShowDefaultMessage = false;
            return;
        }
        int nextSubIdForState = this.mKeyguardUpdateMonitor.getNextSubIdForState(2);
        if (nextSubIdForState != this.mSubId && SubscriptionManager.isValidSubscriptionId(nextSubIdForState)) {
            this.mSubId = nextSubIdForState;
            this.mShowDefaultMessage = true;
            this.mRemainingAttempts = -1;
        }
    }

    private void initDDSkipView() {
        boolean supportDualSimPINSkiped = MotoFeature.getInstance(((KeyguardSimPinView) this.mView).getContext()).supportDualSimPINSkiped();
        this.mSupportDsSkipped = supportDualSimPINSkiped;
        if (supportDualSimPINSkiped) {
            TextView textView = (TextView) ((KeyguardSimPinView) this.mView).findViewById(R$id.ds_skip);
            this.mDSSkipText = textView;
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        ((KeyguardSimPinView) KeyguardSimPinViewController.this.mView).doHapticKeyClick();
                        if (TelephonyManager.getDefault().getSimCount() < 2) {
                            KeyguardSimPinViewController.this.mKeyguardUpdateMonitor.clearSkippedSubId();
                            KeyguardSimPinViewController.this.mDSSkipText.setVisibility(8);
                            return;
                        }
                        int skippedSubId = KeyguardSimPinViewController.this.mKeyguardUpdateMonitor.getSkippedSubId();
                        if (!SubscriptionManager.isValidSubscriptionId(skippedSubId)) {
                            KeyguardSimPinViewController.this.mKeyguardUpdateMonitor.setSkippedSubId(KeyguardSimPinViewController.this.mSubId);
                        } else if (skippedSubId != KeyguardSimPinViewController.this.mSubId) {
                            KeyguardSimPinViewController.this.mKeyguardUpdateMonitor.clearSkippedSubId();
                        }
                        IccCardConstants.State firstUnSkippedLockedSIMState = KeyguardSimPinViewController.this.mKeyguardUpdateMonitor.getFirstUnSkippedLockedSIMState(true);
                        if (firstUnSkippedLockedSIMState == IccCardConstants.State.UNKNOWN) {
                            KeyguardSimPinViewController.this.mKeyguardUpdateMonitor.reportSimUnlocked(KeyguardSimPinViewController.this.mSubId);
                            KeyguardSimPinViewController.this.mKeyguardUpdateMonitor.setRecentlySkipped(true);
                            if (KeyguardSimPinViewController.this.getKeyguardSecurityCallback() != null) {
                                KeyguardSimPinViewController.this.getKeyguardSecurityCallback().dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                            }
                        } else if (firstUnSkippedLockedSIMState == IccCardConstants.State.intToState(KeyguardSimPinViewController.this.mKeyguardUpdateMonitor.getSimState(KeyguardSimPinViewController.this.mSubId))) {
                            KeyguardSimPinViewController.this.reset();
                            KeyguardSimPinViewController.this.mPasswordEntry.invalidate();
                        } else if (KeyguardSimPinViewController.this.getKeyguardSecurityCallback() != null) {
                            KeyguardSimPinViewController.this.getKeyguardSecurityCallback().dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                        }
                    }
                });
                this.mDSSkipText.setOnHoverListener(this.mLiftToActivateListener);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onUserInput() {
        if (getKeyguardSecurityCallback() != null) {
            getKeyguardSecurityCallback().userActivity();
        }
    }
}
