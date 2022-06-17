package com.android.keyguard;

import android.content.Context;
import android.graphics.Rect;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.internal.widget.LockPatternView;
import com.android.settingslib.animation.AppearAnimationCreator;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.android.systemui.R$id;
import com.android.systemui.moto.MotoFeature;

public class KeyguardPatternView extends KeyguardInputView implements AppearAnimationCreator<LockPatternView.CellState> {
    private static final boolean DEBUG = KeyguardConstants.DEBUG;
    private final AppearAnimationUtils mAppearAnimationUtils;
    private ViewGroup mContainer;
    private final DisappearAnimationUtils mDisappearAnimationUtils;
    private final DisappearAnimationUtils mDisappearAnimationUtilsLocked;
    private View mEcaView;
    private long mLastPokeTime;
    private final Rect mLockPatternScreenBounds;
    private LockPatternView mLockPatternView;
    KeyguardMessageArea mSecurityMessageDisplay;
    private final Rect mTempRect;
    private final int[] mTmpPosition;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean needsEmergencyAreaAnimation() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean needsTranslationAnimation() {
        return true;
    }

    public KeyguardPatternView(Context context) {
        this(context, (AttributeSet) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public KeyguardPatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTmpPosition = new int[2];
        this.mTempRect = new Rect();
        this.mLockPatternScreenBounds = new Rect();
        this.mLastPokeTime = -7000;
        this.mAppearAnimationUtils = new AppearAnimationUtils(context, 220, 1.5f, 2.0f, AnimationUtils.loadInterpolator(this.mContext, 17563662));
        this.mDisappearAnimationUtils = new DisappearAnimationUtils(context, 125, 1.2f, 0.6f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearAnimationUtilsLocked = new DisappearAnimationUtils(context, 187, 1.2f, 0.6f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLockPatternView = findViewById(R$id.lockPatternView);
        this.mEcaView = findViewById(R$id.keyguard_selector_fade_container);
        this.mContainer = (ViewGroup) findViewById(R$id.pattern_container);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSecurityMessageDisplay = KeyguardMessageArea.findSecurityMessageDisplay(this);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        long elapsedRealtime = SystemClock.elapsedRealtime() - this.mLastPokeTime;
        if (onTouchEvent && elapsedRealtime > 6900) {
            this.mLastPokeTime = SystemClock.elapsedRealtime();
        }
        boolean z = false;
        this.mTempRect.set(0, 0, 0, 0);
        offsetRectIntoDescendantCoords(this.mLockPatternView, this.mTempRect);
        Rect rect = this.mTempRect;
        motionEvent.offsetLocation((float) rect.left, (float) rect.top);
        if (this.mLockPatternView.dispatchTouchEvent(motionEvent) || onTouchEvent) {
            z = true;
        }
        Rect rect2 = this.mTempRect;
        motionEvent.offsetLocation((float) (-rect2.left), (float) (-rect2.top));
        return z;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (!z) {
            Log.i("SecurityPatternView", "The position of mLockPatternView is not changed. return.");
            return;
        }
        this.mLockPatternView.getLocationOnScreen(this.mTmpPosition);
        if (DEBUG) {
            Log.d("SecurityPatternView", "onLayout: changed=" + z + "  left=" + i + "  top=" + i2 + "  right=" + i3 + "  bottom=" + i4 + "\nmTmpPosition[0]=" + this.mTmpPosition[0] + "  mTmpPosition[1]=" + this.mTmpPosition[1]);
        }
        Rect rect = this.mLockPatternScreenBounds;
        int[] iArr = this.mTmpPosition;
        rect.set(iArr[0] - 40, iArr[1] - 40, iArr[0] + this.mLockPatternView.getWidth() + 40, this.mTmpPosition[1] + this.mLockPatternView.getHeight() + 40);
    }

    /* access modifiers changed from: package-private */
    public boolean disallowInterceptTouch(MotionEvent motionEvent) {
        return !this.mLockPatternView.isEmpty() || this.mLockPatternScreenBounds.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
    }

    public void startAppearAnimation() {
        enableClipping(false);
        setAlpha(1.0f);
        if (needsTranslationAnimation()) {
            setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
            AppearAnimationUtils.startTranslationYAnimation(this, 0, 500, 0.0f, this.mAppearAnimationUtils.getInterpolator(), getAnimationListener(18));
        }
        this.mAppearAnimationUtils.startAnimation2d(this.mLockPatternView.getCellStates(), new KeyguardPatternView$$ExternalSyntheticLambda0(this), this);
        if (!TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
            AppearAnimationUtils appearAnimationUtils = this.mAppearAnimationUtils;
            appearAnimationUtils.createAnimation((View) this.mSecurityMessageDisplay, 0, 220, appearAnimationUtils.getStartTranslation(), true, this.mAppearAnimationUtils.getInterpolator(), (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startAppearAnimation$0() {
        enableClipping(true);
    }

    public boolean startDisappearAnimation(boolean z, Runnable runnable) {
        float f = z ? 1.5f : 1.0f;
        this.mLockPatternView.clearPattern();
        enableClipping(false);
        if (needsTranslationAnimation()) {
            setTranslationY(0.0f);
            AppearAnimationUtils.startTranslationYAnimation(this, 0, (long) (300.0f * f), -this.mDisappearAnimationUtils.getStartTranslation(), this.mDisappearAnimationUtils.getInterpolator(), getAnimationListener(21));
        }
        if (needDisappearAnim()) {
            (z ? this.mDisappearAnimationUtilsLocked : this.mDisappearAnimationUtils).startAnimation2d(this.mLockPatternView.getCellStates(), new KeyguardPatternView$$ExternalSyntheticLambda1(this, runnable), this);
        }
        if (TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
            return true;
        }
        DisappearAnimationUtils disappearAnimationUtils = this.mDisappearAnimationUtils;
        disappearAnimationUtils.createAnimation((View) this.mSecurityMessageDisplay, 0, (long) (f * 200.0f), (-disappearAnimationUtils.getStartTranslation()) * 3.0f, false, this.mDisappearAnimationUtils.getInterpolator(), (Runnable) null);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startDisappearAnimation$1(Runnable runnable) {
        enableClipping(true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public boolean needDisappearAnim() {
        return !MotoFeature.isLidClosed(this.mContext);
    }

    private void enableClipping(boolean z) {
        setClipChildren(z);
        this.mContainer.setClipToPadding(z);
        this.mContainer.setClipChildren(z);
    }

    public void createAnimation(LockPatternView.CellState cellState, long j, long j2, float f, boolean z, Interpolator interpolator, Runnable runnable) {
        this.mLockPatternView.startCellStateAnimation(cellState, 1.0f, z ? 1.0f : 0.0f, z ? f : 0.0f, z ? 0.0f : f, z ? 0.0f : 1.0f, 1.0f, j, j2, interpolator, runnable);
        if (runnable != null && needsEmergencyAreaAnimation()) {
            this.mAppearAnimationUtils.createAnimation(this.mEcaView, j, j2, f, z, interpolator, (Runnable) null);
        }
    }

    public CharSequence getTitle() {
        return getResources().getString(17040470);
    }

    /* access modifiers changed from: protected */
    public AppearAnimationUtils getAppearAnimationUtils() {
        return this.mAppearAnimationUtils;
    }

    /* access modifiers changed from: protected */
    public DisappearAnimationUtils getDisappearAnimationUtils() {
        return this.mDisappearAnimationUtils;
    }
}
