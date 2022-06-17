package com.android.keyguard;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.res.ColorStateList;
import android.metrics.LogMaker;
import android.util.Log;
import android.util.Slog;
import android.view.MotionEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.AdminSecondaryLockScreenController;
import com.android.keyguard.KeyguardSecurityContainer;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.DejankUtils;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.ViewController;

public class KeyguardSecurityContainerController extends ViewController<KeyguardSecurityContainer> implements KeyguardSecurityView {
    private static final boolean DEBUG = KeyguardConstants.DEBUG;
    private final AdminSecondaryLockScreenController mAdminSecondaryLockScreenController;
    private final ConfigurationController mConfigurationController;
    private ConfigurationController.ConfigurationListener mConfigurationListener;
    private KeyguardSecurityModel.SecurityMode mCurrentSecurityMode;
    private final Gefingerpoken mGlobalTouchListener;
    /* access modifiers changed from: private */
    public KeyguardSecurityCallback mKeyguardSecurityCallback;
    private final KeyguardStateController mKeyguardStateController;
    private int mLastOrientation;
    /* access modifiers changed from: private */
    public final LockPatternUtils mLockPatternUtils;
    /* access modifiers changed from: private */
    public final MetricsLogger mMetricsLogger;
    /* access modifiers changed from: private */
    public final KeyguardSecurityContainer.SecurityCallback mSecurityCallback;
    private final KeyguardSecurityModel mSecurityModel;
    /* access modifiers changed from: private */
    public final KeyguardSecurityViewFlipperController mSecurityViewFlipperController;
    private KeyguardSecurityContainer.SwipeListener mSwipeListener;
    /* access modifiers changed from: private */
    public final UiEventLogger mUiEventLogger;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mUpdateMonitor;

    private KeyguardSecurityContainerController(KeyguardSecurityContainer keyguardSecurityContainer, AdminSecondaryLockScreenController.Factory factory, LockPatternUtils lockPatternUtils, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardSecurityModel keyguardSecurityModel, MetricsLogger metricsLogger, UiEventLogger uiEventLogger, KeyguardStateController keyguardStateController, KeyguardSecurityContainer.SecurityCallback securityCallback, KeyguardSecurityViewFlipperController keyguardSecurityViewFlipperController, ConfigurationController configurationController) {
        super(keyguardSecurityContainer);
        this.mLastOrientation = 0;
        this.mCurrentSecurityMode = KeyguardSecurityModel.SecurityMode.Invalid;
        this.mGlobalTouchListener = new Gefingerpoken() {
            private MotionEvent mTouchDown;

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return false;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == 0) {
                    MotionEvent motionEvent2 = this.mTouchDown;
                    if (motionEvent2 != null) {
                        motionEvent2.recycle();
                        this.mTouchDown = null;
                    }
                    this.mTouchDown = MotionEvent.obtain(motionEvent);
                    return false;
                } else if (this.mTouchDown == null) {
                    return false;
                } else {
                    if (motionEvent.getActionMasked() != 1 && motionEvent.getActionMasked() != 3) {
                        return false;
                    }
                    this.mTouchDown.recycle();
                    this.mTouchDown = null;
                    return false;
                }
            }
        };
        this.mKeyguardSecurityCallback = new KeyguardSecurityCallback() {
            public void userActivity() {
                if (KeyguardSecurityContainerController.this.mSecurityCallback != null) {
                    KeyguardSecurityContainerController.this.mSecurityCallback.userActivity();
                }
            }

            public void onUserInput() {
                KeyguardSecurityContainerController.this.mUpdateMonitor.cancelFaceAuth();
            }

            public void dismiss(boolean z, int i) {
                dismiss(z, i, false);
            }

            public void dismiss(boolean z, int i, boolean z2) {
                KeyguardSecurityContainerController.this.mSecurityCallback.dismiss(z, i, z2);
            }

            public void reportUnlockAttempt(int i, boolean z, int i2) {
                KeyguardSecurityContainer.BouncerUiEvent bouncerUiEvent;
                if (z) {
                    SysUiStatsLog.write(64, 2);
                    KeyguardSecurityContainerController.this.mLockPatternUtils.reportSuccessfulPasswordAttempt(i);
                    ThreadUtils.postOnBackgroundThread(KeyguardSecurityContainerController$2$$ExternalSyntheticLambda0.INSTANCE);
                } else {
                    SysUiStatsLog.write(64, 1);
                    KeyguardSecurityContainerController.this.reportFailedUnlockAttempt(i, i2);
                }
                KeyguardSecurityContainerController.this.mMetricsLogger.write(new LogMaker(197).setType(z ? 10 : 11));
                UiEventLogger access$400 = KeyguardSecurityContainerController.this.mUiEventLogger;
                if (z) {
                    bouncerUiEvent = KeyguardSecurityContainer.BouncerUiEvent.BOUNCER_PASSWORD_SUCCESS;
                } else {
                    bouncerUiEvent = KeyguardSecurityContainer.BouncerUiEvent.BOUNCER_PASSWORD_FAILURE;
                }
                access$400.log(bouncerUiEvent);
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$reportUnlockAttempt$0() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException unused) {
                }
                System.gc();
                System.runFinalization();
                System.gc();
            }

            public void reset() {
                KeyguardSecurityContainerController.this.mSecurityCallback.reset();
            }

            public void onCancelClicked() {
                KeyguardSecurityContainerController.this.mSecurityCallback.onCancelClicked();
            }
        };
        this.mSwipeListener = new KeyguardSecurityContainer.SwipeListener() {
            public void onSwipeUp() {
                if (!KeyguardSecurityContainerController.this.mUpdateMonitor.isFaceDetectionRunning()) {
                    KeyguardSecurityContainerController.this.mUpdateMonitor.requestFaceAuth(true);
                    KeyguardSecurityContainerController.this.mKeyguardSecurityCallback.userActivity();
                    KeyguardSecurityContainerController.this.showMessage((CharSequence) null, (ColorStateList) null);
                }
            }
        };
        this.mConfigurationListener = new ConfigurationController.ConfigurationListener() {
            public void onOverlayChanged() {
                KeyguardSecurityContainerController.this.mSecurityViewFlipperController.reloadColors();
            }

            public void onUiModeChanged() {
                KeyguardSecurityContainerController.this.mSecurityViewFlipperController.reloadColors();
            }
        };
        this.mLockPatternUtils = lockPatternUtils;
        this.mUpdateMonitor = keyguardUpdateMonitor;
        this.mSecurityModel = keyguardSecurityModel;
        this.mMetricsLogger = metricsLogger;
        this.mUiEventLogger = uiEventLogger;
        this.mKeyguardStateController = keyguardStateController;
        this.mSecurityCallback = securityCallback;
        this.mSecurityViewFlipperController = keyguardSecurityViewFlipperController;
        this.mAdminSecondaryLockScreenController = factory.create(this.mKeyguardSecurityCallback);
        this.mConfigurationController = configurationController;
        this.mLastOrientation = getResources().getConfiguration().orientation;
    }

    public void onInit() {
        this.mSecurityViewFlipperController.init();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        ((KeyguardSecurityContainer) this.mView).setSwipeListener(this.mSwipeListener);
        ((KeyguardSecurityContainer) this.mView).addMotionEventListener(this.mGlobalTouchListener);
        this.mConfigurationController.addCallback(this.mConfigurationListener);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mConfigurationController.removeCallback(this.mConfigurationListener);
        ((KeyguardSecurityContainer) this.mView).removeMotionEventListener(this.mGlobalTouchListener);
    }

    public void onPause() {
        this.mAdminSecondaryLockScreenController.hide();
        if (this.mCurrentSecurityMode != KeyguardSecurityModel.SecurityMode.None) {
            getCurrentSecurityController().onPause();
        }
        ((KeyguardSecurityContainer) this.mView).onPause();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ KeyguardSecurityModel.SecurityMode lambda$showPrimarySecurityScreen$0() {
        return this.mSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
    }

    public void showPrimarySecurityScreen(boolean z) {
        KeyguardSecurityModel.SecurityMode securityMode = (KeyguardSecurityModel.SecurityMode) DejankUtils.whitelistIpcs(new KeyguardSecurityContainerController$$ExternalSyntheticLambda0(this));
        if (DEBUG) {
            Log.v("KeyguardSecurityView", "showPrimarySecurityScreen(turningOff=" + z + ")");
        }
        showSecurityScreen(securityMode);
    }

    public void showPromptReason(int i) {
        if (this.mCurrentSecurityMode != KeyguardSecurityModel.SecurityMode.None) {
            if (i != 0) {
                Log.i("KeyguardSecurityView", "Strong auth required, reason: " + i);
            }
            getCurrentSecurityController().showPromptReason(i);
        }
    }

    public void showMessage(CharSequence charSequence, ColorStateList colorStateList) {
        if (this.mCurrentSecurityMode != KeyguardSecurityModel.SecurityMode.None) {
            getCurrentSecurityController().showMessage(charSequence, colorStateList);
        }
    }

    public KeyguardSecurityModel.SecurityMode getCurrentSecurityMode() {
        return this.mCurrentSecurityMode;
    }

    public void reset() {
        ((KeyguardSecurityContainer) this.mView).reset();
        this.mSecurityViewFlipperController.reset();
    }

    public CharSequence getTitle() {
        return ((KeyguardSecurityContainer) this.mView).getTitle();
    }

    public void onResume(int i) {
        if (this.mCurrentSecurityMode != KeyguardSecurityModel.SecurityMode.None) {
            getCurrentSecurityController().onResume(i);
        }
        ((KeyguardSecurityContainer) this.mView).onResume(this.mSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser()), this.mKeyguardStateController.isFaceAuthEnabled());
    }

    public void startAppearAnimation() {
        if (this.mCurrentSecurityMode != KeyguardSecurityModel.SecurityMode.None) {
            getCurrentSecurityController().startAppearAnimation();
        }
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        ((KeyguardSecurityContainer) this.mView).startDisappearAnimation(getCurrentSecurityMode());
        if (this.mCurrentSecurityMode != KeyguardSecurityModel.SecurityMode.None) {
            return getCurrentSecurityController().startDisappearAnimation(runnable);
        }
        return false;
    }

    public void onStartingToHide() {
        if (this.mCurrentSecurityMode != KeyguardSecurityModel.SecurityMode.None) {
            getCurrentSecurityController().onStartingToHide();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00c9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00f2  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showNextSecurityScreenOrFinish(boolean r9, int r10, boolean r11) {
        /*
            r8 = this;
            boolean r0 = DEBUG
            java.lang.String r1 = "KeyguardSecurityView"
            if (r0 == 0) goto L_0x001f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "showNextSecurityScreenOrFinish("
            r0.append(r2)
            r0.append(r9)
            java.lang.String r2 = ")"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r1, r0)
        L_0x001f:
            com.android.keyguard.KeyguardSecurityContainer$BouncerUiEvent r0 = com.android.keyguard.KeyguardSecurityContainer.BouncerUiEvent.UNKNOWN
            com.android.keyguard.KeyguardUpdateMonitor r2 = r8.mUpdateMonitor
            boolean r2 = r2.getUserHasTrust(r10)
            r3 = -1
            r4 = 0
            r5 = 1
            if (r2 == 0) goto L_0x0033
            r9 = 3
            com.android.keyguard.KeyguardSecurityContainer$BouncerUiEvent r1 = com.android.keyguard.KeyguardSecurityContainer.BouncerUiEvent.BOUNCER_DISMISS_EXTENDED_ACCESS
        L_0x002f:
            r6 = r4
            r2 = r5
            goto L_0x00bd
        L_0x0033:
            com.android.keyguard.KeyguardUpdateMonitor r2 = r8.mUpdateMonitor
            boolean r2 = r2.getUserUnlockedWithBiometric(r10)
            if (r2 == 0) goto L_0x003f
            r9 = 2
            com.android.keyguard.KeyguardSecurityContainer$BouncerUiEvent r1 = com.android.keyguard.KeyguardSecurityContainer.BouncerUiEvent.BOUNCER_DISMISS_BIOMETRIC
            goto L_0x002f
        L_0x003f:
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r2 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.None
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r6 = r8.getCurrentSecurityMode()
            if (r2 == r6) goto L_0x00a8
            boolean r6 = r8.isSimPinOrPukWithLoaed()
            if (r6 == 0) goto L_0x0050
            if (r9 != 0) goto L_0x0050
            goto L_0x00a8
        L_0x0050:
            if (r9 == 0) goto L_0x00a3
            int[] r9 = com.android.keyguard.KeyguardSecurityContainerController.C06145.f53xdc0e830a
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r6 = r8.getCurrentSecurityMode()
            int r6 = r6.ordinal()
            r9 = r9[r6]
            switch(r9) {
                case 1: goto L_0x009e;
                case 2: goto L_0x009e;
                case 3: goto L_0x009e;
                case 4: goto L_0x0082;
                case 5: goto L_0x0082;
                case 6: goto L_0x0082;
                default: goto L_0x0061;
            }
        L_0x0061:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r2 = "Bad security screen "
            r9.append(r2)
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r2 = r8.getCurrentSecurityMode()
            r9.append(r2)
            java.lang.String r2 = ", fail safe"
            r9.append(r2)
            java.lang.String r9 = r9.toString()
            android.util.Log.v(r1, r9)
            r8.showPrimarySecurityScreen(r4)
            goto L_0x00a3
        L_0x0082:
            com.android.keyguard.KeyguardSecurityModel r9 = r8.mSecurityModel
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r9 = r9.getSecurityMode(r10)
            if (r9 != r2) goto L_0x009a
            com.android.internal.widget.LockPatternUtils r1 = r8.mLockPatternUtils
            int r2 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()
            boolean r1 = r1.isLockScreenDisabled(r2)
            if (r1 == 0) goto L_0x009a
            r9 = 4
            com.android.keyguard.KeyguardSecurityContainer$BouncerUiEvent r1 = com.android.keyguard.KeyguardSecurityContainer.BouncerUiEvent.BOUNCER_DISMISS_SIM
            goto L_0x002f
        L_0x009a:
            r8.showSecurityScreen(r9)
            goto L_0x00a3
        L_0x009e:
            com.android.keyguard.KeyguardSecurityContainer$BouncerUiEvent r1 = com.android.keyguard.KeyguardSecurityContainer.BouncerUiEvent.BOUNCER_DISMISS_PASSWORD
            r9 = r5
            r2 = r9
            goto L_0x00a6
        L_0x00a3:
            r1 = r0
            r9 = r3
            r2 = r4
        L_0x00a6:
            r6 = r2
            goto L_0x00bd
        L_0x00a8:
            com.android.keyguard.KeyguardSecurityModel r9 = r8.mSecurityModel
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r9 = r9.getSecurityMode(r10)
            if (r2 != r9) goto L_0x00b6
            com.android.keyguard.KeyguardSecurityContainer$BouncerUiEvent r9 = com.android.keyguard.KeyguardSecurityContainer.BouncerUiEvent.BOUNCER_DISMISS_NONE_SECURITY
            r1 = r9
            r9 = r4
            r2 = r5
            goto L_0x00bc
        L_0x00b6:
            r8.showSecurityScreen(r9)
            r1 = r0
            r9 = r3
            r2 = r4
        L_0x00bc:
            r6 = r4
        L_0x00bd:
            com.android.keyguard.KeyguardUpdateMonitor r7 = r8.mUpdateMonitor
            boolean r7 = r7.getRdpAuthenticateState()
            if (r7 == 0) goto L_0x00c6
            goto L_0x00c7
        L_0x00c6:
            r5 = r2
        L_0x00c7:
            if (r5 == 0) goto L_0x00d9
            if (r11 != 0) goto L_0x00d9
            com.android.keyguard.KeyguardUpdateMonitor r11 = r8.mUpdateMonitor
            android.content.Intent r11 = r11.getSecondaryLockscreenRequirement(r10)
            if (r11 == 0) goto L_0x00d9
            com.android.keyguard.AdminSecondaryLockScreenController r8 = r8.mAdminSecondaryLockScreenController
            r8.show(r11)
            return r4
        L_0x00d9:
            if (r9 == r3) goto L_0x00f0
            com.android.internal.logging.MetricsLogger r11 = r8.mMetricsLogger
            android.metrics.LogMaker r2 = new android.metrics.LogMaker
            r3 = 197(0xc5, float:2.76E-43)
            r2.<init>(r3)
            r3 = 5
            android.metrics.LogMaker r2 = r2.setType(r3)
            android.metrics.LogMaker r9 = r2.setSubtype(r9)
            r11.write(r9)
        L_0x00f0:
            if (r1 == r0) goto L_0x00f7
            com.android.internal.logging.UiEventLogger r9 = r8.mUiEventLogger
            r9.log(r1)
        L_0x00f7:
            if (r5 == 0) goto L_0x011d
            android.content.Intent r9 = new android.content.Intent
            r9.<init>()
            java.lang.String r11 = "com.motorola.UNLOCK_SUCCESS"
            r9.setAction(r11)
            r11 = 16777216(0x1000000, float:2.3509887E-38)
            r9.addFlags(r11)
            r11 = 268435456(0x10000000, float:2.5243549E-29)
            r9.addFlags(r11)
            com.android.keyguard.KeyguardUpdateMonitor r11 = r8.mUpdateMonitor
            android.content.Context r11 = r11.getContext()
            android.os.UserHandle r0 = android.os.UserHandle.OWNER
            r11.sendBroadcastAsUser(r9, r0)
            com.android.keyguard.KeyguardSecurityContainer$SecurityCallback r8 = r8.mSecurityCallback
            r8.finish(r6, r10)
        L_0x011d:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardSecurityContainerController.showNextSecurityScreenOrFinish(boolean, int, boolean):boolean");
    }

    /* renamed from: com.android.keyguard.KeyguardSecurityContainerController$5 */
    static /* synthetic */ class C06145 {

        /* renamed from: $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode */
        static final /* synthetic */ int[] f53xdc0e830a;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|14) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.keyguard.KeyguardSecurityModel$SecurityMode[] r0 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f53xdc0e830a = r0
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Pattern     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f53xdc0e830a     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Password     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f53xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.PIN     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f53xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.SimPin     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = f53xdc0e830a     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.SimPuk     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = f53xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.PAKS     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardSecurityContainerController.C06145.<clinit>():void");
        }
    }

    public boolean needsInput() {
        return getCurrentSecurityController().needsInput();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void showSecurityScreen(KeyguardSecurityModel.SecurityMode securityMode) {
        if (DEBUG) {
            Log.d("KeyguardSecurityView", "showSecurityScreen(" + securityMode + ")");
        }
        if (securityMode != KeyguardSecurityModel.SecurityMode.Invalid && securityMode != this.mCurrentSecurityMode) {
            KeyguardInputViewController<KeyguardInputView> currentSecurityController = getCurrentSecurityController();
            boolean z = true;
            if (currentSecurityController != null) {
                currentSecurityController.pauseCallback(true);
                currentSecurityController.onPause();
            }
            KeyguardInputViewController<KeyguardInputView> changeSecurityMode = changeSecurityMode(securityMode);
            if (changeSecurityMode != null) {
                changeSecurityMode.pauseCallback(false);
                changeSecurityMode.onResume(2);
                this.mSecurityViewFlipperController.show(changeSecurityMode);
                ((KeyguardSecurityContainer) this.mView).updateLayoutForSecurityMode(securityMode);
            }
            KeyguardSecurityContainer.SecurityCallback securityCallback = this.mSecurityCallback;
            if (changeSecurityMode == null || !changeSecurityMode.needsInput()) {
                z = false;
            }
            securityCallback.onSecurityModeChanged(securityMode, z);
        }
    }

    public void reportFailedUnlockAttempt(int i, int i2) {
        int i3 = 1;
        int currentFailedPasswordAttempts = this.mLockPatternUtils.getCurrentFailedPasswordAttempts(i) + 1;
        if (DEBUG) {
            Log.d("KeyguardSecurityView", "reportFailedPatternAttempt: #" + currentFailedPasswordAttempts);
        }
        DevicePolicyManager devicePolicyManager = this.mLockPatternUtils.getDevicePolicyManager();
        int maximumFailedPasswordsForWipe = devicePolicyManager.getMaximumFailedPasswordsForWipe((ComponentName) null, i);
        int i4 = maximumFailedPasswordsForWipe > 0 ? maximumFailedPasswordsForWipe - currentFailedPasswordAttempts : Integer.MAX_VALUE;
        if (i4 < 5) {
            int profileWithMinimumFailedPasswordsForWipe = devicePolicyManager.getProfileWithMinimumFailedPasswordsForWipe(i);
            if (profileWithMinimumFailedPasswordsForWipe == i) {
                if (profileWithMinimumFailedPasswordsForWipe != 0) {
                    i3 = 3;
                }
            } else if (profileWithMinimumFailedPasswordsForWipe != -10000) {
                i3 = 2;
            }
            if (i4 > 0) {
                ((KeyguardSecurityContainer) this.mView).showAlmostAtWipeDialog(currentFailedPasswordAttempts, i4, i3);
                ((KeyguardSecurityContainer) this.mView).turnOffScreenDelay(2000);
            } else {
                Slog.i("KeyguardSecurityView", "Too many unlock attempts; user " + profileWithMinimumFailedPasswordsForWipe + " will be wiped!");
                ((KeyguardSecurityContainer) this.mView).showWipeDialog(currentFailedPasswordAttempts, i3);
            }
        }
        this.mLockPatternUtils.reportFailedPasswordAttempt(i);
        if (i2 > 0) {
            this.mLockPatternUtils.reportPasswordLockout(i2, i);
            ((KeyguardSecurityContainer) this.mView).showTimeoutDialog(i, i2, this.mLockPatternUtils, this.mSecurityModel.getSecurityMode(i));
            ((KeyguardSecurityContainer) this.mView).turnOffScreenDelay(2000);
        }
    }

    private KeyguardInputViewController<KeyguardInputView> getCurrentSecurityController() {
        return this.mSecurityViewFlipperController.getSecurityView(this.mCurrentSecurityMode, this.mKeyguardSecurityCallback);
    }

    private KeyguardInputViewController<KeyguardInputView> changeSecurityMode(KeyguardSecurityModel.SecurityMode securityMode) {
        this.mCurrentSecurityMode = securityMode;
        return getCurrentSecurityController();
    }

    public void updateResources() {
        int i = getResources().getConfiguration().orientation;
        if (i != this.mLastOrientation) {
            this.mLastOrientation = i;
            ((KeyguardSecurityContainer) this.mView).updateLayoutForSecurityMode(this.mCurrentSecurityMode);
        }
    }

    public void updateKeyguardPosition(float f) {
        ((KeyguardSecurityContainer) this.mView).updateKeyguardPosition(f);
    }

    static class Factory {
        private final AdminSecondaryLockScreenController.Factory mAdminSecondaryLockScreenControllerFactory;
        private final ConfigurationController mConfigurationController;
        private final KeyguardSecurityModel mKeyguardSecurityModel;
        private final KeyguardStateController mKeyguardStateController;
        private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
        private final LockPatternUtils mLockPatternUtils;
        private final MetricsLogger mMetricsLogger;
        private final KeyguardSecurityViewFlipperController mSecurityViewFlipperController;
        private final UiEventLogger mUiEventLogger;
        private final KeyguardSecurityContainer mView;

        Factory(KeyguardSecurityContainer keyguardSecurityContainer, AdminSecondaryLockScreenController.Factory factory, LockPatternUtils lockPatternUtils, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardSecurityModel keyguardSecurityModel, MetricsLogger metricsLogger, UiEventLogger uiEventLogger, KeyguardStateController keyguardStateController, KeyguardSecurityViewFlipperController keyguardSecurityViewFlipperController, ConfigurationController configurationController) {
            this.mView = keyguardSecurityContainer;
            this.mAdminSecondaryLockScreenControllerFactory = factory;
            this.mLockPatternUtils = lockPatternUtils;
            this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
            this.mKeyguardSecurityModel = keyguardSecurityModel;
            this.mMetricsLogger = metricsLogger;
            this.mUiEventLogger = uiEventLogger;
            this.mKeyguardStateController = keyguardStateController;
            this.mSecurityViewFlipperController = keyguardSecurityViewFlipperController;
            this.mConfigurationController = configurationController;
        }

        public KeyguardSecurityContainerController create(KeyguardSecurityContainer.SecurityCallback securityCallback) {
            return new KeyguardSecurityContainerController(this.mView, this.mAdminSecondaryLockScreenControllerFactory, this.mLockPatternUtils, this.mKeyguardUpdateMonitor, this.mKeyguardSecurityModel, this.mMetricsLogger, this.mUiEventLogger, this.mKeyguardStateController, securityCallback, this.mSecurityViewFlipperController, this.mConfigurationController);
        }
    }

    private boolean isSimPinOrPukWithLoaed() {
        KeyguardSecurityModel.SecurityMode securityMode = KeyguardSecurityModel.SecurityMode.SimPin;
        KeyguardSecurityModel.SecurityMode securityMode2 = this.mCurrentSecurityMode;
        return (securityMode == securityMode2 || KeyguardSecurityModel.SecurityMode.SimPuk == securityMode2) && ((KeyguardSecurityContainer) this.mView).isSimReadyAndLoaded();
    }
}
