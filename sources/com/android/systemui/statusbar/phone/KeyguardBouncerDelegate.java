package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.hardware.biometrics.BiometricSourceType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.keyguard.KeyguardConstants;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.moto.CliAlertDialog;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.phone.KeyguardBouncer;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.PrintWriter;

public class KeyguardBouncerDelegate {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    private BiometricUnlockController mBiometricUnlockController;
    /* access modifiers changed from: private */
    public KeyguardBouncer.BouncerExpansionCallback mBouncerExpansionCallback;
    private Callback mCallback;
    private CliAlertDialog mCliAlertDialog;
    private KeyguardBouncer mCliBouncer;
    private KeyguardBouncer.BouncerExpansionCallback mCliBouncerExpansionCallback;
    private ViewGroup mCliContainer;
    private Context mCliContext;
    private KeyguardUpdateMonitorCallback mCliUpdateMonitorCallback;
    private ViewMediatorCallback mCliViewMediatorCallback;
    private boolean mFpsLockoutAlerted;
    private KeyguardStateController mKeyguardStateController;
    private KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private KeyguardBouncer mMainBouncer;
    /* access modifiers changed from: private */
    public ViewMediatorCallback mViewMediatorCallback;

    public interface Callback {
        void onSensorPulse(int i, float f, float f2, float[] fArr);
    }

    private boolean isFingerprintLockout(int i) {
        return i == 7 || i == 9;
    }

    public void setMainBouncer(KeyguardBouncer keyguardBouncer, ViewMediatorCallback viewMediatorCallback, KeyguardBouncer.BouncerExpansionCallback bouncerExpansionCallback) {
        this.mMainBouncer = keyguardBouncer;
        this.mViewMediatorCallback = viewMediatorCallback;
        this.mBouncerExpansionCallback = bouncerExpansionCallback;
    }

    public void setCliBouncer(KeyguardBouncer keyguardBouncer, Context context, ViewGroup viewGroup) {
        this.mCliBouncer = keyguardBouncer;
        this.mCliContext = context;
        this.mCliContainer = viewGroup;
        KeyguardUpdateMonitor keyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        keyguardUpdateMonitor.registerCallback(getCliKeyguardUpdateMonitorCallback());
        this.mKeyguardStateController = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
    }

    private KeyguardUpdateMonitorCallback getCliKeyguardUpdateMonitorCallback() {
        if (this.mCliUpdateMonitorCallback == null) {
            this.mCliUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
                public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "onBiometricAuthenticated sourcetype=" + biometricSourceType + ";isStrongBiometric=" + z);
                    }
                    if (KeyguardBouncerDelegate.this.isFingerprintType(biometricSourceType) && isBouncerPromptReasonNone()) {
                        KeyguardBouncerDelegate.this.dismissCliUnlockAlert();
                    }
                }

                public void onBiometricAuthFailed(BiometricSourceType biometricSourceType) {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "onBiometricAuthenticated sourcetype=" + biometricSourceType);
                    }
                }

                public void onBiometricHelp(int i, String str, BiometricSourceType biometricSourceType) {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "onBiometricHelp msgId=" + i + ";text=" + str + ";type=" + biometricSourceType);
                    }
                    if (KeyguardBouncerDelegate.this.isFingerprintType(biometricSourceType)) {
                        KeyguardBouncerDelegate.this.handleCliFingerprintUnlock(false, i, str);
                    }
                }

                public void onBiometricError(int i, String str, BiometricSourceType biometricSourceType) {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "onBiometricError msgId=" + i + ";text=" + str + ";type=" + biometricSourceType);
                    }
                    if (KeyguardBouncerDelegate.this.isFingerprintType(biometricSourceType)) {
                        KeyguardBouncerDelegate.this.handleCliFingerprintUnlock(true, i, str);
                    }
                }

                private boolean isBouncerPromptReasonNone() {
                    if (KeyguardBouncerDelegate.this.mViewMediatorCallback == null || KeyguardBouncerDelegate.this.mViewMediatorCallback.getBouncerPromptReason() == 0) {
                        return true;
                    }
                    return false;
                }
            };
        }
        return this.mCliUpdateMonitorCallback;
    }

    public ViewMediatorCallback getCliViewMediatorCallback() {
        if (this.mCliViewMediatorCallback == null) {
            this.mCliViewMediatorCallback = new ViewMediatorCallback() {
                public void userActivity() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli userActivity");
                    }
                    KeyguardBouncerDelegate.this.mViewMediatorCallback.userActivity();
                }

                public void keyguardDone(boolean z, int i) {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli keyguardDone strongAuth=" + z + ";targetUserId=" + i);
                    }
                    KeyguardBouncerDelegate.this.mViewMediatorCallback.keyguardDone(z, i);
                }

                public void keyguardDoneDrawing() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli keyguardDoneDrawing");
                    }
                }

                public void setNeedsInput(boolean z) {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli setNeedsInput needsInput=" + z);
                    }
                }

                public void keyguardDonePending(boolean z, int i) {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli keyguardDonePending strongAuth=" + z + ";targetUserId" + i);
                    }
                    KeyguardBouncerDelegate.this.mViewMediatorCallback.keyguardDonePending(z, i);
                }

                public void keyguardGone() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli keyguardGone");
                    }
                }

                public void readyForKeyguardDone() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli readyForKeyguardDone");
                    }
                }

                public void resetKeyguard() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli resetKeyguard");
                    }
                    KeyguardBouncerDelegate.this.mViewMediatorCallback.resetKeyguard();
                }

                public void onBouncerVisiblityChanged(boolean z) {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli onBouncerVisiblityChanged, shown = " + z);
                    }
                }

                public void playTrustedSound() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli playTrustedSound");
                    }
                }

                public boolean isScreenOn() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli isScreenOn");
                    }
                    return KeyguardBouncerDelegate.this.mViewMediatorCallback.isScreenOn();
                }

                public int getBouncerPromptReason() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli getBouncerPromptReason");
                    }
                    return KeyguardBouncerDelegate.this.mViewMediatorCallback.getBouncerPromptReason();
                }

                public CharSequence consumeCustomMessage() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli consumeCustomMessage");
                    }
                    return KeyguardBouncerDelegate.this.mViewMediatorCallback.consumeCustomMessage();
                }

                public boolean isExternalEnabled() {
                    return KeyguardBouncerDelegate.this.mViewMediatorCallback.isExternalEnabled();
                }

                public void onCancelClicked() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli onCancelClicked");
                    }
                }
            };
        }
        return this.mCliViewMediatorCallback;
    }

    public KeyguardBouncer.BouncerExpansionCallback getCliBouncerExpansionCallback() {
        if (this.mCliBouncerExpansionCallback == null) {
            this.mCliBouncerExpansionCallback = new KeyguardBouncer.BouncerExpansionCallback() {
                public void onFullyShown() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli onFullyShown");
                    }
                }

                public void onStartingToHide() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli onStartingToHide");
                    }
                }

                public void onStartingToShow() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli onStartingToShow");
                    }
                }

                public void onFullyHidden() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli onFullyHidden");
                    }
                }

                public void hideBouncerFromCli() {
                    if (KeyguardBouncerDelegate.DEBUG) {
                        Log.d("CLI_KeyguardBouncerDelegate", "Cli hideBouncer");
                    }
                    KeyguardBouncerDelegate.this.mBouncerExpansionCallback.hideBouncerFromCli();
                }
            };
        }
        return this.mCliBouncerExpansionCallback;
    }

    public void show(boolean z) {
        this.mMainBouncer.show(z);
        if (this.mCliBouncer != null && canShowCliBouncer()) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "show resetSecuritySelection=" + z);
            }
            this.mCliBouncer.show(z);
            setupCliBouncerLayout();
        }
    }

    public void show(boolean z, boolean z2) {
        this.mMainBouncer.show(z, z2);
        if (this.mCliBouncer != null && canShowCliBouncer()) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "show resetSecuritySelection=" + z + ";isScrimmed=" + z2);
            }
            this.mCliBouncer.show(z, z2);
            setupCliBouncerLayout();
        }
    }

    public boolean isScrimmed() {
        return this.mMainBouncer.isScrimmed();
    }

    public void showMessage(String str, ColorStateList colorStateList) {
        this.mMainBouncer.showMessage(str, colorStateList);
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "showMessage message=" + str);
            }
            if (!isCliUnlockAlertShowing()) {
                this.mCliBouncer.showMessage(str, colorStateList);
            }
        }
    }

    public void showWithDismissAction(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable) {
        this.mMainBouncer.showWithDismissAction(onDismissAction, runnable);
        if (this.mCliBouncer != null && canShowCliBouncer()) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "showWithDismissAction");
            }
            this.mCliBouncer.showWithDismissAction(onDismissAction, runnable);
            setupCliBouncerLayout();
        }
    }

    public void setDismissAction(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable) {
        this.mMainBouncer.setDismissAction(onDismissAction, runnable);
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "setOnDismissAction");
            }
            this.mCliBouncer.setDismissAction(onDismissAction, runnable);
        }
    }

    public void hide(boolean z) {
        KeyguardBouncer keyguardBouncer = this.mMainBouncer;
        if (keyguardBouncer != null) {
            keyguardBouncer.hide(z);
        }
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "hide destroyView=" + z);
            }
            this.mCliBouncer.hide(z);
            dismissCliUnlockAlert();
        }
    }

    public void startPreHideAnimation(Runnable runnable) {
        this.mMainBouncer.startPreHideAnimation(runnable);
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "startPreHideAnimation");
            }
            this.mCliBouncer.startPreHideAnimation(runnable);
        }
    }

    public void onScreenTurnedOff() {
        this.mMainBouncer.onScreenTurnedOff();
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "onScreenTurnedOff");
            }
            this.mCliBouncer.onScreenTurnedOff();
            dismissCliUnlockAlert();
        }
    }

    public boolean isShowing() {
        return this.mMainBouncer.isShowing();
    }

    public boolean getShowingSoon() {
        return this.mMainBouncer.getShowingSoon();
    }

    public boolean isAnimatingAway() {
        return this.mMainBouncer.isAnimatingAway();
    }

    public void prepare() {
        this.mMainBouncer.prepare();
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "prepare");
            }
            this.mCliBouncer.prepare();
        }
    }

    public void setExpansion(float f) {
        this.mMainBouncer.setExpansion(f);
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "setExpansion fraction=" + f);
            }
            this.mCliBouncer.setExpansion(f);
        }
    }

    public boolean willDismissWithAction() {
        return this.mMainBouncer.willDismissWithAction();
    }

    public boolean needsFullscreenBouncer() {
        return this.mMainBouncer.needsFullscreenBouncer();
    }

    public boolean isFullscreenBouncer() {
        return this.mMainBouncer.isFullscreenBouncer();
    }

    public boolean isSecure() {
        return this.mMainBouncer.isSecure();
    }

    public boolean shouldDismissOnMenuPressed() {
        return this.mMainBouncer.shouldDismissOnMenuPressed();
    }

    public boolean interceptMediaKey(KeyEvent keyEvent) {
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "interceptMediaKey event=" + keyEvent);
            }
            this.mCliBouncer.interceptMediaKey(keyEvent);
        }
        return this.mMainBouncer.interceptMediaKey(keyEvent);
    }

    public boolean dispatchBackKeyEventPreIme() {
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "dispatchBackKeyEventPreIme");
            }
            this.mCliBouncer.dispatchBackKeyEventPreIme();
        }
        return this.mMainBouncer.dispatchBackKeyEventPreIme();
    }

    public void notifyKeyguardAuthenticated(boolean z) {
        this.mMainBouncer.notifyKeyguardAuthenticated(z);
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "notifyKeyguardAuthenticated strongAuth=" + z);
            }
            this.mCliBouncer.notifyKeyguardAuthenticated(z);
        }
    }

    public void updateResources() {
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "updateResources");
            }
            this.mCliBouncer.updateResources();
        }
        KeyguardBouncer keyguardBouncer = this.mMainBouncer;
        if (keyguardBouncer != null) {
            keyguardBouncer.updateResources();
        }
    }

    public void updateKeyguardPosition(float f) {
        if (this.mCliBouncer != null) {
            if (DEBUG) {
                Log.d("CLI_KeyguardBouncerDelegate", "updateKeyguardPosition=" + f);
            }
            this.mCliBouncer.updateKeyguardPosition(f);
        }
        this.mMainBouncer.updateKeyguardPosition(f);
    }

    public void setBiometricUnlockController(BiometricUnlockController biometricUnlockController) {
        this.mBiometricUnlockController = biometricUnlockController;
    }

    private boolean isBiometricUnlock() {
        BiometricUnlockController biometricUnlockController = this.mBiometricUnlockController;
        if (biometricUnlockController != null) {
            return biometricUnlockController.isBiometricUnlock();
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isFingerprintType(BiometricSourceType biometricSourceType) {
        return biometricSourceType == BiometricSourceType.FINGERPRINT;
    }

    private boolean isCliBouncerSecurityMode() {
        KeyguardSecurityModel.SecurityMode securityMode = this.mMainBouncer.getSecurityMode();
        return securityMode == KeyguardSecurityModel.SecurityMode.PIN || securityMode == KeyguardSecurityModel.SecurityMode.Pattern;
    }

    private boolean isPasswordSecurityMode() {
        return this.mMainBouncer.getSecurityMode() == KeyguardSecurityModel.SecurityMode.Password;
    }

    /* renamed from: com.android.systemui.statusbar.phone.KeyguardBouncerDelegate$4 */
    static /* synthetic */ class C18174 {

        /* renamed from: $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode */
        static final /* synthetic */ int[] f132xdc0e830a;

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|12) */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.keyguard.KeyguardSecurityModel$SecurityMode[] r0 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f132xdc0e830a = r0
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.PIN     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f132xdc0e830a     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Pattern     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f132xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Password     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f132xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.SimPin     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = f132xdc0e830a     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.SimPuk     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.KeyguardBouncerDelegate.C18174.<clinit>():void");
        }
    }

    private boolean canShowCliBouncer() {
        if (isBiometricUnlock()) {
            return false;
        }
        int i = C18174.f132xdc0e830a[this.mMainBouncer.getSecurityMode().ordinal()];
        if (i == 1 || i == 2) {
            return true;
        }
        if (i != 3) {
            if (i != 4 && i != 5) {
                return false;
            }
            showCliUnlockAlert((CharSequence) null, true);
            return false;
        } else if (this.mKeyguardUpdateMonitor.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser()) || this.mKeyguardStateController.isKeyguardGoingAway()) {
            return false;
        } else {
            showCliUnlockAlert((CharSequence) null, true);
            return false;
        }
    }

    private void setupCliBouncerLayout() {
        ViewGroup viewGroup = this.mCliContainer;
        if (viewGroup != null) {
            View findViewById = viewGroup.findViewById(R$id.view_flipper);
            View findViewById2 = this.mCliContainer.findViewById(R$id.close_bouncer);
            if (findViewById != null) {
                findViewById.setPadding(0, 0, 0, 0);
            }
            if (findViewById2 != null) {
                findViewById2.setOnClickListener(new KeyguardBouncerDelegate$$ExternalSyntheticLambda1(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupCliBouncerLayout$0(View view) {
        KeyguardBouncer.BouncerExpansionCallback bouncerExpansionCallback = this.mCliBouncerExpansionCallback;
        if (bouncerExpansionCallback != null) {
            bouncerExpansionCallback.hideBouncerFromCli();
        }
    }

    /* access modifiers changed from: private */
    public void handleCliFingerprintUnlock(boolean z, int i, String str) {
        boolean isUnlocked = this.mKeyguardStateController.isUnlocked();
        boolean isShowing = isShowing();
        boolean isLidClosed = MotoFeature.isLidClosed(this.mCliContext);
        boolean isGoingToSleep = this.mKeyguardUpdateMonitor.isGoingToSleep();
        boolean isFingerprintLockout = isFingerprintLockout(i);
        boolean isCliUnlockAlertShowing = isCliUnlockAlertShowing();
        boolean z2 = DEBUG;
        if (z2) {
            Log.d("CLI_KeyguardBouncerDelegate", "Handle CLI fingerprint unlock. error: " + z + ", msgId: " + i + ", text: " + str + "\n, isUnLocked: " + isUnlocked + ", isBouncerShowing: " + isShowing + ", isCliShowing: " + isLidClosed + ", isGoingToSleep: " + isGoingToSleep + ", isFpsLockout: " + isFingerprintLockout + ", isAlertShowing: " + isCliUnlockAlertShowing + ", mFpsLockoutAlerted: " + this.mFpsLockoutAlerted);
        }
        if (isUnlocked || !isLidClosed || isGoingToSleep || ((isFingerprintLockout && this.mFpsLockoutAlerted) || (z && i == 5))) {
            if (z2) {
                Log.v("CLI_KeyguardBouncerDelegate", "Skipped CLI fingerprint unlock alert");
            }
            if (isFingerprintLockout && !isLidClosed) {
                this.mFpsLockoutAlerted = true;
                return;
            }
            return;
        }
        if (isFingerprintLockout) {
            this.mFpsLockoutAlerted = true;
        }
        if (!isShowing || isFingerprintLockout) {
            showCliUnlockAlert(str, false);
        }
    }

    private void showCliUnlockAlert(CharSequence charSequence, boolean z) {
        if (this.mCliAlertDialog == null) {
            CliAlertDialog createNotifyDialog = CliAlertDialog.createNotifyDialog(this.mCliContext);
            this.mCliAlertDialog = createNotifyDialog;
            createNotifyDialog.setOnDismissListener(new KeyguardBouncerDelegate$$ExternalSyntheticLambda0(this));
        }
        CharSequence cliUnlockPrompt = z ? getCliUnlockPrompt() : null;
        if (DEBUG) {
            Log.d("CLI_KeyguardBouncerDelegate", "Show CLI unlock alert. message: " + charSequence + ", prompt: " + cliUnlockPrompt);
        }
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onSensorPulse(12, -1.0f, -1.0f, (float[]) null);
        }
        this.mCliAlertDialog.setMessage(charSequence, cliUnlockPrompt);
        this.mCliAlertDialog.setEmergencyEnabled(!isCliBouncerSecurityMode());
        KeyguardBouncer keyguardBouncer = this.mCliBouncer;
        if (keyguardBouncer != null) {
            keyguardBouncer.getKeyguardBouncerComponent().inject(this.mCliAlertDialog);
        }
        this.mCliAlertDialog.show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showCliUnlockAlert$1(DialogInterface dialogInterface) {
        KeyguardBouncer.BouncerExpansionCallback bouncerExpansionCallback;
        if (isPasswordSecurityMode() && MotoFeature.isLidClosed(this.mCliContext) && (bouncerExpansionCallback = this.mCliBouncerExpansionCallback) != null) {
            bouncerExpansionCallback.hideBouncerFromCli();
        }
    }

    /* access modifiers changed from: private */
    public void dismissCliUnlockAlert() {
        CliAlertDialog cliAlertDialog = this.mCliAlertDialog;
        if (cliAlertDialog != null) {
            cliAlertDialog.lambda$new$0();
            this.mCliAlertDialog = null;
        }
    }

    private boolean isCliUnlockAlertShowing() {
        CliAlertDialog cliAlertDialog = this.mCliAlertDialog;
        if (cliAlertDialog != null) {
            return cliAlertDialog.isShowing();
        }
        return false;
    }

    private CharSequence getCliUnlockPrompt() {
        int i = C18174.f132xdc0e830a[this.mMainBouncer.getSecurityMode().ordinal()];
        if (i == 1) {
            return this.mCliContext.getText(R$string.zz_moto_cli_kg_unlock_pin);
        }
        if (i == 2) {
            return this.mCliContext.getText(R$string.zz_moto_cli_kg_unlock_pattern);
        }
        if (i == 3) {
            return this.mCliContext.getText(R$string.zz_moto_cli_kg_unlock_password);
        }
        if (i == 4) {
            return this.mCliContext.getText(R$string.zz_moto_cli_kg_unlock_sim_pin);
        }
        if (i != 5) {
            return null;
        }
        return this.mCliContext.getText(R$string.zz_moto_cli_kg_unlock_sim_puk);
    }

    public void dump(PrintWriter printWriter) {
        this.mMainBouncer.dump(printWriter);
        if (this.mCliBouncer != null) {
            printWriter.println("========CLI=======");
            this.mCliBouncer.dump(printWriter);
        }
    }

    public void setOnSensor(Callback callback) {
        this.mCallback = callback;
    }
}
