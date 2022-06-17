package com.android.keyguard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.widget.FrameLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.systemui.Dependency;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.R$bool;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.moto.CliAlertDialog;
import com.android.systemui.moto.MotoFeature;
import java.util.ArrayList;
import java.util.List;

public class KeyguardSecurityContainer extends FrameLayout {
    private int mActivePointerId;
    /* access modifiers changed from: private */
    public AlertDialog mAlertDialog;
    /* access modifiers changed from: private */
    public CliAlertDialog mCliAlertDialog;
    /* access modifiers changed from: private */
    public boolean mDisappearAnimRunning;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private boolean mIsDragging;
    /* access modifiers changed from: private */
    public final boolean mIsPukScreenAvailable;
    private boolean mIsSecurityViewLeftAligned;
    private float mLastTouchY;
    private final List<Gefingerpoken> mMotionEventListeners;
    private boolean mOneHandedMode;
    /* access modifiers changed from: private */
    public ViewPropertyAnimator mRunningOneHandedAnimator;
    private KeyguardSecurityModel.SecurityMode mSecurityMode;
    KeyguardSecurityViewFlipper mSecurityViewFlipper;
    /* access modifiers changed from: private */
    public boolean mSimReadyAndLoaded;
    private final SpringAnimation mSpringAnimation;
    private float mStartTouchY;
    private SwipeListener mSwipeListener;
    private boolean mSwipeUpToRetry;
    /* access modifiers changed from: private */
    public final Runnable mTurnOffScreenRunnable;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mUpdateMonitor;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    private final VelocityTracker mVelocityTracker;
    private final ViewConfiguration mViewConfiguration;
    private final WindowInsetsAnimation.Callback mWindowInsetsAnimationCallback;

    public interface SecurityCallback {
        boolean dismiss(boolean z, int i, boolean z2);

        void finish(boolean z, int i);

        void onCancelClicked();

        void onSecurityModeChanged(KeyguardSecurityModel.SecurityMode securityMode, boolean z);

        void reset();

        void userActivity();
    }

    public interface SwipeListener {
        void onSwipeUp();
    }

    public boolean shouldDelayChildPressedState() {
        return true;
    }

    public enum BouncerUiEvent implements UiEventLogger.UiEventEnum {
        UNKNOWN(0),
        BOUNCER_DISMISS_EXTENDED_ACCESS(413),
        BOUNCER_DISMISS_BIOMETRIC(414),
        BOUNCER_DISMISS_NONE_SECURITY(415),
        BOUNCER_DISMISS_PASSWORD(416),
        BOUNCER_DISMISS_SIM(417),
        BOUNCER_PASSWORD_SUCCESS(418),
        BOUNCER_PASSWORD_FAILURE(419);
        
        private final int mId;

        private BouncerUiEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    public KeyguardSecurityContainer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyguardSecurityContainer(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public KeyguardSecurityContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mMotionEventListeners = new ArrayList();
        this.mLastTouchY = -1.0f;
        this.mActivePointerId = -1;
        this.mStartTouchY = -1.0f;
        this.mIsSecurityViewLeftAligned = true;
        this.mOneHandedMode = false;
        this.mSecurityMode = KeyguardSecurityModel.SecurityMode.Invalid;
        this.mHandler = new Handler();
        this.mTurnOffScreenRunnable = new Runnable() {
            public void run() {
                ((PowerManager) KeyguardSecurityContainer.this.mContext.getSystemService("power")).goToSleep(SystemClock.uptimeMillis());
            }
        };
        C06042 r4 = new KeyguardUpdateMonitorCallback() {
            public void onStartedWakingUp() {
                KeyguardSecurityContainer.this.mHandler.removeCallbacks(KeyguardSecurityContainer.this.mTurnOffScreenRunnable);
            }

            public void onSimStateChanged(int i, int i2, int i3) {
                IccCardConstants.State firstUnSkippedLockedSIMState = KeyguardSecurityContainer.this.mUpdateMonitor.getFirstUnSkippedLockedSIMState(KeyguardSecurityContainer.this.mIsPukScreenAvailable);
                if (!SubscriptionManager.isValidSubscriptionId(i) || IccCardConstants.State.PIN_REQUIRED == firstUnSkippedLockedSIMState || IccCardConstants.State.PUK_REQUIRED == firstUnSkippedLockedSIMState) {
                    boolean unused = KeyguardSecurityContainer.this.mSimReadyAndLoaded = false;
                } else {
                    boolean unused2 = KeyguardSecurityContainer.this.mSimReadyAndLoaded = true;
                }
                if (KeyguardConstants.DEBUG) {
                    Log.d("KeyguardSecurityView", "onSimStateChanged: mSimReadyAndLoaded=" + KeyguardSecurityContainer.this.mSimReadyAndLoaded);
                }
            }
        };
        this.mUpdateMonitorCallback = r4;
        this.mWindowInsetsAnimationCallback = new WindowInsetsAnimation.Callback(0) {
            private final Rect mFinalBounds = new Rect();
            private final Rect mInitialBounds = new Rect();

            public void onPrepare(WindowInsetsAnimation windowInsetsAnimation) {
                KeyguardSecurityContainer.this.mSecurityViewFlipper.getBoundsOnScreen(this.mInitialBounds);
            }

            public WindowInsetsAnimation.Bounds onStart(WindowInsetsAnimation windowInsetsAnimation, WindowInsetsAnimation.Bounds bounds) {
                if (!KeyguardSecurityContainer.this.mDisappearAnimRunning) {
                    KeyguardSecurityContainer.this.beginJankInstrument(17);
                } else {
                    KeyguardSecurityContainer.this.beginJankInstrument(20);
                }
                KeyguardSecurityContainer.this.mSecurityViewFlipper.getBoundsOnScreen(this.mFinalBounds);
                return bounds;
            }

            public WindowInsets onProgress(WindowInsets windowInsets, List<WindowInsetsAnimation> list) {
                int i;
                if (KeyguardSecurityContainer.this.mDisappearAnimRunning) {
                    i = -(this.mFinalBounds.bottom - this.mInitialBounds.bottom);
                } else {
                    i = this.mInitialBounds.bottom - this.mFinalBounds.bottom;
                }
                float f = (float) i;
                float f2 = KeyguardSecurityContainer.this.mDisappearAnimRunning ? -(((float) (this.mFinalBounds.bottom - this.mInitialBounds.bottom)) * 0.75f) : 0.0f;
                int i2 = 0;
                float f3 = 1.0f;
                for (WindowInsetsAnimation next : list) {
                    if ((next.getTypeMask() & WindowInsets.Type.ime()) != 0) {
                        f3 = next.getInterpolatedFraction();
                        i2 += (int) MathUtils.lerp(f, f2, f3);
                    }
                }
                KeyguardSecurityContainer keyguardSecurityContainer = KeyguardSecurityContainer.this;
                keyguardSecurityContainer.mSecurityViewFlipper.animateForIme(i2, f3, !keyguardSecurityContainer.mDisappearAnimRunning);
                return windowInsets;
            }

            public void onEnd(WindowInsetsAnimation windowInsetsAnimation) {
                if (!KeyguardSecurityContainer.this.mDisappearAnimRunning) {
                    KeyguardSecurityContainer.this.endJankInstrument(17);
                    KeyguardSecurityContainer.this.mSecurityViewFlipper.animateForIme(0, 1.0f, true);
                    return;
                }
                KeyguardSecurityContainer.this.endJankInstrument(20);
            }
        };
        this.mSpringAnimation = new SpringAnimation(this, DynamicAnimation.f33Y);
        this.mViewConfiguration = ViewConfiguration.get(context);
        KeyguardUpdateMonitor keyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mUpdateMonitor = keyguardUpdateMonitor;
        keyguardUpdateMonitor.registerCallback(r4);
        this.mIsPukScreenAvailable = getResources().getBoolean(17891597);
    }

    /* access modifiers changed from: package-private */
    public void onResume(KeyguardSecurityModel.SecurityMode securityMode, boolean z) {
        this.mSecurityMode = securityMode;
        this.mSecurityViewFlipper.setWindowInsetsAnimationCallback(this.mWindowInsetsAnimationCallback);
        updateBiometricRetry(securityMode, z);
        updateLayoutForSecurityMode(securityMode);
    }

    /* access modifiers changed from: package-private */
    public void updateLayoutForSecurityMode(KeyguardSecurityModel.SecurityMode securityMode) {
        this.mSecurityMode = securityMode;
        boolean canUseOneHandedBouncer = canUseOneHandedBouncer();
        this.mOneHandedMode = canUseOneHandedBouncer;
        if (canUseOneHandedBouncer) {
            this.mIsSecurityViewLeftAligned = isOneHandedKeyguardLeftAligned(this.mContext);
        }
        updateSecurityViewGravity();
        updateSecurityViewLocation(false);
    }

    public void updateKeyguardPosition(float f) {
        if (this.mOneHandedMode) {
            moveBouncerForXCoordinate(f, false);
        }
    }

    private boolean canUseOneHandedBouncer() {
        if (getResources().getBoolean(17891565) && KeyguardSecurityModel.isSecurityViewOneHanded(this.mSecurityMode)) {
            return getResources().getBoolean(R$bool.can_use_one_handed_bouncer);
        }
        return false;
    }

    private boolean isOneHandedKeyguardLeftAligned(Context context) {
        try {
            return Settings.Global.getInt(context.getContentResolver(), "one_handed_keyguard_side") == 0;
        } catch (Settings.SettingNotFoundException unused) {
            return true;
        }
    }

    private void updateSecurityViewGravity() {
        KeyguardSecurityViewFlipper findKeyguardSecurityView = findKeyguardSecurityView();
        if (findKeyguardSecurityView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) findKeyguardSecurityView.getLayoutParams();
            if (this.mOneHandedMode) {
                layoutParams.gravity = 83;
            } else {
                layoutParams.gravity = 1;
            }
            findKeyguardSecurityView.setLayoutParams(layoutParams);
        }
    }

    private void updateSecurityViewLocation(boolean z) {
        KeyguardSecurityViewFlipper findKeyguardSecurityView = findKeyguardSecurityView();
        if (findKeyguardSecurityView != null) {
            if (!this.mOneHandedMode) {
                findKeyguardSecurityView.setTranslationX(0.0f);
                return;
            }
            ViewPropertyAnimator viewPropertyAnimator = this.mRunningOneHandedAnimator;
            if (viewPropertyAnimator != null) {
                viewPropertyAnimator.cancel();
                this.mRunningOneHandedAnimator = null;
            }
            int measuredWidth = this.mIsSecurityViewLeftAligned ? 0 : (int) (((float) getMeasuredWidth()) / 2.0f);
            if (z) {
                ViewPropertyAnimator translationX = findKeyguardSecurityView.animate().translationX((float) measuredWidth);
                this.mRunningOneHandedAnimator = translationX;
                translationX.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
                this.mRunningOneHandedAnimator.setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ViewPropertyAnimator unused = KeyguardSecurityContainer.this.mRunningOneHandedAnimator = null;
                    }
                });
                this.mRunningOneHandedAnimator.setDuration(360);
                this.mRunningOneHandedAnimator.start();
                return;
            }
            findKeyguardSecurityView.setTranslationX((float) measuredWidth);
        }
    }

    private KeyguardSecurityViewFlipper findKeyguardSecurityView() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (isKeyguardSecurityView(childAt)) {
                return (KeyguardSecurityViewFlipper) childAt;
            }
        }
        return null;
    }

    private boolean isKeyguardSecurityView(View view) {
        return view instanceof KeyguardSecurityViewFlipper;
    }

    public void onPause() {
        this.mSecurityViewFlipper.setWindowInsetsAnimationCallback((WindowInsetsAnimation.Callback) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0029, code lost:
        if (r3 != 3) goto L_0x007c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            java.util.List<com.android.systemui.Gefingerpoken> r0 = r5.mMotionEventListeners
            java.util.stream.Stream r0 = r0.stream()
            com.android.keyguard.KeyguardSecurityContainer$$ExternalSyntheticLambda1 r1 = new com.android.keyguard.KeyguardSecurityContainer$$ExternalSyntheticLambda1
            r1.<init>(r6)
            boolean r0 = r0.anyMatch(r1)
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x001c
            boolean r0 = super.onInterceptTouchEvent(r6)
            if (r0 == 0) goto L_0x001a
            goto L_0x001c
        L_0x001a:
            r0 = r1
            goto L_0x001d
        L_0x001c:
            r0 = r2
        L_0x001d:
            int r3 = r6.getActionMasked()
            if (r3 == 0) goto L_0x0067
            if (r3 == r2) goto L_0x0064
            r4 = 2
            if (r3 == r4) goto L_0x002c
            r6 = 3
            if (r3 == r6) goto L_0x0064
            goto L_0x007c
        L_0x002c:
            boolean r3 = r5.mIsDragging
            if (r3 == 0) goto L_0x0031
            return r2
        L_0x0031:
            boolean r3 = r5.mSwipeUpToRetry
            if (r3 != 0) goto L_0x0036
            return r1
        L_0x0036:
            com.android.keyguard.KeyguardSecurityViewFlipper r3 = r5.mSecurityViewFlipper
            com.android.keyguard.KeyguardInputView r3 = r3.getSecurityView()
            boolean r3 = r3.disallowInterceptTouch(r6)
            if (r3 == 0) goto L_0x0043
            return r1
        L_0x0043:
            int r1 = r5.mActivePointerId
            int r1 = r6.findPointerIndex(r1)
            android.view.ViewConfiguration r3 = r5.mViewConfiguration
            int r3 = r3.getScaledTouchSlop()
            float r3 = (float) r3
            r4 = 1082130432(0x40800000, float:4.0)
            float r3 = r3 * r4
            r4 = -1
            if (r1 == r4) goto L_0x007c
            float r4 = r5.mStartTouchY
            float r6 = r6.getY(r1)
            float r4 = r4 - r6
            int r6 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x007c
            r5.mIsDragging = r2
            return r2
        L_0x0064:
            r5.mIsDragging = r1
            goto L_0x007c
        L_0x0067:
            int r1 = r6.getActionIndex()
            float r2 = r6.getY(r1)
            r5.mStartTouchY = r2
            int r6 = r6.getPointerId(r1)
            r5.mActivePointerId = r6
            android.view.VelocityTracker r5 = r5.mVelocityTracker
            r5.clear()
        L_0x007c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardSecurityContainer.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x007c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            int r0 = r7.getActionMasked()
            java.util.List<com.android.systemui.Gefingerpoken> r1 = r6.mMotionEventListeners
            java.util.stream.Stream r1 = r1.stream()
            com.android.keyguard.KeyguardSecurityContainer$$ExternalSyntheticLambda2 r2 = new com.android.keyguard.KeyguardSecurityContainer$$ExternalSyntheticLambda2
            r2.<init>(r7)
            boolean r1 = r1.anyMatch(r2)
            if (r1 != 0) goto L_0x0019
            boolean r1 = super.onTouchEvent(r7)
        L_0x0019:
            r1 = 0
            r2 = -1082130432(0xffffffffbf800000, float:-1.0)
            r3 = 1
            if (r0 == r3) goto L_0x006a
            r4 = 2
            if (r0 == r4) goto L_0x0045
            r4 = 3
            if (r0 == r4) goto L_0x006a
            r2 = 6
            if (r0 == r2) goto L_0x0029
            goto L_0x007a
        L_0x0029:
            int r2 = r7.getActionIndex()
            int r4 = r7.getPointerId(r2)
            int r5 = r6.mActivePointerId
            if (r4 != r5) goto L_0x007a
            if (r2 != 0) goto L_0x0038
            r1 = r3
        L_0x0038:
            float r2 = r7.getY(r1)
            r6.mLastTouchY = r2
            int r1 = r7.getPointerId(r1)
            r6.mActivePointerId = r1
            goto L_0x007a
        L_0x0045:
            android.view.VelocityTracker r1 = r6.mVelocityTracker
            r1.addMovement(r7)
            int r1 = r6.mActivePointerId
            int r1 = r7.findPointerIndex(r1)
            float r1 = r7.getY(r1)
            float r4 = r6.mLastTouchY
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 == 0) goto L_0x0067
            float r2 = r1 - r4
            float r4 = r6.getTranslationY()
            r5 = 1048576000(0x3e800000, float:0.25)
            float r2 = r2 * r5
            float r4 = r4 + r2
            r6.setTranslationY(r4)
        L_0x0067:
            r6.mLastTouchY = r1
            goto L_0x007a
        L_0x006a:
            r4 = -1
            r6.mActivePointerId = r4
            r6.mLastTouchY = r2
            r6.mIsDragging = r1
            android.view.VelocityTracker r1 = r6.mVelocityTracker
            float r1 = r1.getYVelocity()
            r6.startSpringAnimation(r1)
        L_0x007a:
            if (r0 != r3) goto L_0x00a2
            float r0 = r6.getTranslationY()
            float r0 = -r0
            r1 = 1092616192(0x41200000, float:10.0)
            android.content.res.Resources r2 = r6.getResources()
            android.util.DisplayMetrics r2 = r2.getDisplayMetrics()
            float r1 = android.util.TypedValue.applyDimension(r3, r1, r2)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x009b
            com.android.keyguard.KeyguardSecurityContainer$SwipeListener r6 = r6.mSwipeListener
            if (r6 == 0) goto L_0x00a2
            r6.onSwipeUp()
            goto L_0x00a2
        L_0x009b:
            boolean r0 = r6.mIsDragging
            if (r0 != 0) goto L_0x00a2
            r6.handleTap(r7)
        L_0x00a2:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardSecurityContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: package-private */
    public void addMotionEventListener(Gefingerpoken gefingerpoken) {
        this.mMotionEventListeners.add(gefingerpoken);
    }

    /* access modifiers changed from: package-private */
    public void removeMotionEventListener(Gefingerpoken gefingerpoken) {
        this.mMotionEventListeners.remove(gefingerpoken);
    }

    private void handleTap(MotionEvent motionEvent) {
        if (this.mOneHandedMode) {
            moveBouncerForXCoordinate(motionEvent.getX(), true);
        }
    }

    private void moveBouncerForXCoordinate(float f, boolean z) {
        if ((this.mIsSecurityViewLeftAligned && f > ((float) getWidth()) / 2.0f) || (!this.mIsSecurityViewLeftAligned && f < ((float) getWidth()) / 2.0f)) {
            this.mIsSecurityViewLeftAligned = !this.mIsSecurityViewLeftAligned;
            Settings.Global.putInt(this.mContext.getContentResolver(), "one_handed_keyguard_side", this.mIsSecurityViewLeftAligned ^ true ? 1 : 0);
            updateSecurityViewLocation(z);
        }
    }

    /* access modifiers changed from: package-private */
    public void setSwipeListener(SwipeListener swipeListener) {
        this.mSwipeListener = swipeListener;
    }

    private void startSpringAnimation(float f) {
        ((SpringAnimation) this.mSpringAnimation.setStartVelocity(f)).animateToFinalPosition(0.0f);
    }

    public void startDisappearAnimation(KeyguardSecurityModel.SecurityMode securityMode) {
        this.mDisappearAnimRunning = true;
    }

    /* access modifiers changed from: private */
    public void beginJankInstrument(int i) {
        KeyguardInputView securityView = this.mSecurityViewFlipper.getSecurityView();
        if (securityView != null) {
            InteractionJankMonitor.getInstance().begin(securityView, i);
        }
    }

    /* access modifiers changed from: private */
    public void endJankInstrument(int i) {
        InteractionJankMonitor.getInstance().end(i);
    }

    private void updateBiometricRetry(KeyguardSecurityModel.SecurityMode securityMode, boolean z) {
        this.mSwipeUpToRetry = (!z || securityMode == KeyguardSecurityModel.SecurityMode.SimPin || securityMode == KeyguardSecurityModel.SecurityMode.SimPuk || securityMode == KeyguardSecurityModel.SecurityMode.None) ? false : true;
    }

    public CharSequence getTitle() {
        return this.mSecurityViewFlipper.getTitle();
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        this.mSecurityViewFlipper = (KeyguardSecurityViewFlipper) findViewById(R$id.view_flipper);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        int max = Integer.max(windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()).bottom, windowInsets.getInsets(WindowInsets.Type.ime()).bottom);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), max);
        return windowInsets.inset(0, 0, 0, max);
    }

    private void showDialog(String str, String str2) {
        Context context = this.mContext;
        if (MotoFeature.getInstance(context).isSupportCli()) {
            CliAlertDialog cliAlertDialog = this.mCliAlertDialog;
            if (cliAlertDialog != null) {
                cliAlertDialog.setTitle((CharSequence) str);
                this.mCliAlertDialog.setMessage((CharSequence) str2);
                this.mCliAlertDialog.show();
            } else {
                CliAlertDialog create = new CliAlertDialog.Builder(this.mContext).setTitle((CharSequence) str).setMessage((CharSequence) str2).setNeutralButton(R$string.f72ok, (DialogInterface.OnClickListener) null).setOnDismissListener((DialogInterface.OnDismissListener) new KeyguardSecurityContainer$$ExternalSyntheticLambda0(this)).create();
                this.mCliAlertDialog = create;
                create.show();
            }
            AlertDialog alertDialog = this.mAlertDialog;
            if (alertDialog == null || !alertDialog.isShowing()) {
                context = this.mContext.getApplicationContext();
            } else {
                this.mAlertDialog.setTitle(str);
                this.mAlertDialog.setMessage(str2);
                return;
            }
        }
        final Vibrator vibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        final MediaPlayer mediaPlayer = new MediaPlayer();
        AlertDialog alertDialog2 = this.mAlertDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
            vibrator.cancel();
            stopAlarm(mediaPlayer);
        }
        AlertDialog create2 = new AlertDialog.Builder(context).setTitle(str).setMessage(str2).setCancelable(false).setNeutralButton(R$string.f72ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                vibrator.cancel();
                KeyguardSecurityContainer.this.stopAlarm(mediaPlayer);
                KeyguardSecurityContainer.this.mHandler.removeCallbacks(KeyguardSecurityContainer.this.mTurnOffScreenRunnable);
                AlertDialog unused = KeyguardSecurityContainer.this.mAlertDialog = null;
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                vibrator.cancel();
                KeyguardSecurityContainer.this.stopAlarm(mediaPlayer);
                KeyguardSecurityContainer.this.mHandler.removeCallbacks(KeyguardSecurityContainer.this.mTurnOffScreenRunnable);
                AlertDialog unused = KeyguardSecurityContainer.this.mAlertDialog = null;
                if (MotoFeature.getInstance(KeyguardSecurityContainer.this.mContext).isSupportCli() && KeyguardSecurityContainer.this.mCliAlertDialog != null) {
                    KeyguardSecurityContainer.this.mCliAlertDialog.lambda$new$0();
                }
            }
        }).create();
        this.mAlertDialog = create2;
        create2.setCanceledOnTouchOutside(false);
        if (!(this.mContext instanceof Activity)) {
            this.mAlertDialog.getWindow().setType(2009);
        }
        this.mAlertDialog.show();
        playAlarm(mediaPlayer);
        VibrationEffect errorVibrationEffect = getErrorVibrationEffect();
        if (errorVibrationEffect != null) {
            vibrator.vibrate(errorVibrationEffect);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$2(DialogInterface dialogInterface) {
        this.mCliAlertDialog = null;
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private VibrationEffect getErrorVibrationEffect() {
        int[] intArray = this.mContext.getResources().getIntArray(17236083);
        if (intArray == null || intArray.length == 0) {
            Log.e("KeyguardSecurityView", "config_fpErrorVibePattern no config");
            return null;
        }
        int length = intArray.length;
        long[] jArr = new long[length];
        for (int i = 0; i < intArray.length; i++) {
            jArr[i] = (long) intArray[i];
        }
        if (length == 1) {
            return VibrationEffect.createOneShot(jArr[0], -1);
        }
        return VibrationEffect.createWaveform(jArr, -1);
    }

    /* access modifiers changed from: package-private */
    public void showTimeoutDialog(int i, int i2, LockPatternUtils lockPatternUtils, KeyguardSecurityModel.SecurityMode securityMode) {
        int i3;
        int i4 = i2 / 1000;
        int i5 = C06097.f52xdc0e830a[securityMode.ordinal()];
        if (i5 == 1) {
            i3 = R$string.kg_too_many_failed_pattern_attempts_dialog_message;
        } else if (i5 == 2) {
            i3 = R$string.kg_too_many_failed_pin_attempts_dialog_message;
        } else if (i5 != 3) {
            i3 = 0;
        } else {
            i3 = R$string.kg_too_many_failed_password_attempts_dialog_message;
        }
        if (i3 != 0) {
            showDialog((String) null, this.mContext.getString(i3, new Object[]{Integer.valueOf(lockPatternUtils.getCurrentFailedPasswordAttempts(i)), Integer.valueOf(i4)}));
        }
    }

    /* renamed from: com.android.keyguard.KeyguardSecurityContainer$7 */
    static /* synthetic */ class C06097 {

        /* renamed from: $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode */
        static final /* synthetic */ int[] f52xdc0e830a;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.keyguard.KeyguardSecurityModel$SecurityMode[] r0 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f52xdc0e830a = r0
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Pattern     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f52xdc0e830a     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.PIN     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f52xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Password     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f52xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Invalid     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = f52xdc0e830a     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.None     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = f52xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.SimPin     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = f52xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.SimPuk     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardSecurityContainer.C06097.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i) / 2, View.MeasureSpec.getMode(i));
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < getChildCount(); i6++) {
            View childAt = getChildAt(i6);
            if (childAt.getVisibility() != 8) {
                if (!this.mOneHandedMode || !isKeyguardSecurityView(childAt)) {
                    measureChildWithMargins(childAt, i, 0, i2, 0);
                } else {
                    measureChildWithMargins(childAt, makeMeasureSpec, 0, i2, 0);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                int max = Math.max(i3, childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
                i4 = Math.max(i4, childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
                i3 = max;
                i5 = FrameLayout.combineMeasuredStates(i5, childAt.getMeasuredState());
            }
        }
        setMeasuredDimension(FrameLayout.resolveSizeAndState(Math.max(i3 + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), i, i5), FrameLayout.resolveSizeAndState(Math.max(i4 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight()), i2, i5 << 16));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateSecurityViewLocation(false);
    }

    /* access modifiers changed from: package-private */
    public void showAlmostAtWipeDialog(int i, int i2, int i3) {
        String str;
        if (i3 == 1) {
            str = this.mContext.getString(R$string.kg_failed_attempts_almost_at_wipe, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        } else if (i3 == 2) {
            str = this.mContext.getString(R$string.kg_failed_attempts_almost_at_erase_profile, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        } else if (i3 != 3) {
            str = null;
        } else {
            str = this.mContext.getString(R$string.kg_failed_attempts_almost_at_erase_user, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        }
        showDialog((String) null, str);
    }

    /* access modifiers changed from: package-private */
    public void showWipeDialog(int i, int i2) {
        String str;
        if (i2 == 1) {
            str = this.mContext.getString(R$string.kg_failed_attempts_now_wiping, new Object[]{Integer.valueOf(i)});
        } else if (i2 == 2) {
            str = this.mContext.getString(R$string.kg_failed_attempts_now_erasing_profile, new Object[]{Integer.valueOf(i)});
        } else if (i2 != 3) {
            str = null;
        } else {
            str = this.mContext.getString(R$string.kg_failed_attempts_now_erasing_user, new Object[]{Integer.valueOf(i)});
        }
        showDialog((String) null, str);
    }

    public void reset() {
        this.mDisappearAnimRunning = false;
    }

    private void playAlarm(MediaPlayer mediaPlayer) {
        Uri defaultUri = RingtoneManager.getDefaultUri(4);
        if (mediaPlayer != null) {
            try {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.setDataSource(this.mContext, defaultUri);
                    mediaPlayer.setAudioStreamType(4);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
            } catch (Exception unused) {
                Log.e("KeyguardSecurityView", "Play error!");
            }
        }
    }

    /* access modifiers changed from: private */
    public void stopAlarm(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            } catch (Exception unused) {
                Log.e("KeyguardSecurityView", "Stop error");
            }
        }
    }

    public void turnOffScreenDelay(long j) {
        this.mHandler.postDelayed(this.mTurnOffScreenRunnable, j);
    }

    public boolean isSimReadyAndLoaded() {
        return this.mSimReadyAndLoaded;
    }
}
