package com.android.systemui.statusbar.phone;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.customview.widget.ViewDragHelper;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.CliStatusBarWindowController;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.motorola.systemui.cli.media.CliMediaViewPager;

public class CliPanelDragView extends FrameLayout implements StatusBarWindowCallback, CliStatusBarWindowController.ICliChildView, ConfigurationController.ConfigurationListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    AnimationSet mAlphaScaleAnimation;
    private DarkIconDispatcher.DarkReceiver mBattery;
    private boolean mBouncerShowing;
    private ViewDragHelper.Callback mCallback;
    /* access modifiers changed from: private */
    public boolean mCanCapture;
    private boolean mCanInterceptTouch;
    private CliStatusBar mCliBar;
    private View mCliKeyguardBlurBg;
    private DarkIconDispatcher.DarkReceiver mCliLockIcon;
    /* access modifiers changed from: private */
    public CliStatusBarWindowController mCliStatusBarWindowController;
    private StatusBarIconController.DarkIconManager mDarkIconManager;
    private int mDirection;
    private boolean mDozing;
    private float mInitialTouchX;
    private float mInitialTouchY;
    /* access modifiers changed from: private */
    public boolean mIsExpanded;
    private CliMediaViewPager mKeyguardMeidaPanel;
    private boolean mKeyguardOccluded;
    private boolean mKeyguardShowing;
    private View mKeyguardView;
    private int mLockIconPaddingEndForAD;
    private int mLockIconPaddingEndForKeyguard;
    private LockPatternUtils mLockPatternUtils;
    private boolean mMediaPanelDraging;
    private CliMediaViewPager mMediaViewPager;
    private MotoDisplayManager mMotoDisplayManager;
    private View mNotificationView;
    private OnCloseListener mOnCloseListener;
    /* access modifiers changed from: private */
    public OnOpenListener mOnOpenListener;
    /* access modifiers changed from: private */
    public View mPanelView;
    /* access modifiers changed from: private */
    public View mQsView;
    /* access modifiers changed from: private */
    public View mScrim;
    /* access modifiers changed from: private */
    public int mStatus;
    /* access modifiers changed from: private */
    public View mStatusBarView;
    private StatusIconContainer mStatusIconContainer;
    /* access modifiers changed from: private */
    public float mTopForQs;
    private boolean mUnlockCollapsing;
    /* access modifiers changed from: private */
    public ViewDragHelper mViewDragHelper;
    private WallpaperManager mWallpaperManager;

    public interface OnCloseListener {
        void close();
    }

    public interface OnOpenListener {
        void open();
    }

    /* access modifiers changed from: private */
    public String stateToString(int i) {
        if (i == 0) {
            return "STATE_IDLE";
        }
        if (i == 1) {
            return "STATE_DRAGGING";
        }
        if (i == 2) {
            return "STATE_SETTLING";
        }
        switch (i) {
            case 10:
                return "ANIMATION_STATE_IDLE";
            case 11:
                return "ANIMATION_STATE_STARTING";
            case 12:
                return "ANIMATION_STATE_CAPTURING";
            case 13:
                return "ANIMATION_STATE_CAPTURED";
            case 14:
                return "ANIMATION_STATE_NEXT_COLLAPSE";
            case 15:
                return "ANIMATION_STATE_NEXT_EXPAND";
            case 16:
                return "ANIMATION_STATE_COLLAPSING";
            case 17:
                return "ANIMATION_STATE_EXPANDING";
            default:
                return "WRONG STATE";
        }
    }

    public void onStateChanged(boolean z, boolean z2, boolean z3) {
    }

    public CliPanelDragView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliPanelDragView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public CliPanelDragView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsExpanded = false;
        this.mCanCapture = true;
        this.mKeyguardShowing = false;
        this.mBouncerShowing = false;
        this.mKeyguardOccluded = false;
        this.mCanInterceptTouch = true;
        this.mDozing = false;
        this.mTopForQs = 0.0f;
        this.mDirection = 0;
        this.mStatus = 10;
        this.mMediaPanelDraging = false;
        this.mAlphaScaleAnimation = new AnimationSet(true);
        this.mCallback = new ViewDragHelper.Callback() {
            public boolean tryCaptureView(View view, int i) {
                if (CliPanelDragView.DEBUG) {
                    Log.d("Cli_DragDownLayout", "tryCaptureView child=" + view + ";mPanelView=" + CliPanelDragView.this.mPanelView);
                }
                if (CliPanelDragView.this.mPanelView == null) {
                    return false;
                }
                CliPanelDragView.this.mViewDragHelper.captureChildView(CliPanelDragView.this.mPanelView, i);
                if (view != CliPanelDragView.this.mPanelView || !CliPanelDragView.this.mCanCapture) {
                    return false;
                }
                return true;
            }

            public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
                int height = view.getHeight();
                float f = ((float) (i2 + height)) / ((float) height);
                if (view == CliPanelDragView.this.mQsView) {
                    float unused = CliPanelDragView.this.mTopForQs = f;
                } else if (view == CliPanelDragView.this.mScrim) {
                    CliPanelDragView.this.scaleKeyguardView(f);
                    CliPanelDragView.this.alphaKeyguardView(f);
                }
                view.setVisibility(f == 0.0f ? 4 : 0);
            }

            public int clampViewPositionVertical(View view, int i, int i2) {
                return Math.min(i, CliPanelDragView.this.getPaddingTop());
            }

            public int clampViewPositionHorizontal(View view, int i, int i2) {
                return CliPanelDragView.this.getPaddingLeft();
            }

            public int getViewVerticalDragRange(View view) {
                return CliPanelDragView.this.getPaddingTop() + view.getMeasuredWidth();
            }

            public void onViewReleased(View view, float f, float f2) {
                if (view == CliPanelDragView.this.mPanelView) {
                    if (CliPanelDragView.DEBUG) {
                        Log.d("Cli_DragDownLayout", "onViewReleased");
                    }
                    int bottom = CliPanelDragView.this.mPanelView.getBottom() - CliPanelDragView.this.getPaddingTop();
                    if (bottom == CliPanelDragView.this.mPanelView.getHeight()) {
                        if (CliPanelDragView.this.isOpen() && CliPanelDragView.this.mPanelView == CliPanelDragView.this.mQsView && CliPanelDragView.this.mOnOpenListener != null) {
                            CliPanelDragView.this.mOnOpenListener.open();
                        }
                        CliPanelDragView.this.finishCapture();
                        return;
                    }
                    boolean z = true;
                    if (CliPanelDragView.this.mIsExpanded) {
                        if (((float) bottom) >= ((float) CliPanelDragView.this.mPanelView.getHeight()) * 0.8f) {
                            z = false;
                        }
                        if (z) {
                            CliPanelDragView.this.smoothToTop();
                        } else {
                            CliPanelDragView.this.smoothToBottom();
                        }
                    } else {
                        if (((float) bottom) <= ((float) CliPanelDragView.this.mPanelView.getHeight()) * 0.2f) {
                            z = false;
                        }
                        if (z) {
                            CliPanelDragView.this.smoothToBottom();
                        } else {
                            CliPanelDragView.this.smoothToTop();
                        }
                    }
                }
            }

            public void onViewCaptured(View view, int i) {
                if (view == CliPanelDragView.this.mPanelView) {
                    if (CliPanelDragView.DEBUG) {
                        Log.d("Cli_DragDownLayout", "onViewCaptured");
                    }
                    boolean unused = CliPanelDragView.this.mCanCapture = false;
                    if (CliPanelDragView.this.mPanelView == CliPanelDragView.this.mScrim) {
                        boolean unused2 = CliPanelDragView.this.mIsExpanded = true;
                    }
                }
            }

            public void onViewDragStateChanged(int i) {
                if (i == 0) {
                    if (CliPanelDragView.DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("onViewDragStateChanged=");
                        CliPanelDragView cliPanelDragView = CliPanelDragView.this;
                        sb.append(cliPanelDragView.stateToString(cliPanelDragView.mStatus));
                        Log.d("Cli_DragDownLayout", sb.toString());
                    }
                    int access$1500 = CliPanelDragView.this.mStatus;
                    if (access$1500 != 11) {
                        switch (access$1500) {
                            case 14:
                                CliPanelDragView.this.smoothToTop();
                                return;
                            case 15:
                                CliPanelDragView.this.smoothToBottom();
                                return;
                            case 16:
                                CliPanelDragView.this.finishCaptureFromCollapse();
                                return;
                            case 17:
                                CliPanelDragView.this.finishCaptureFromExpand();
                                return;
                            default:
                                return;
                        }
                    } else {
                        int unused = CliPanelDragView.this.mStatus = 12;
                    }
                }
            }
        };
        init(context);
    }

    public void setService(CliStatusBar cliStatusBar) {
        this.mCliBar = cliStatusBar;
    }

    private void init(Context context) {
        this.mViewDragHelper = ViewDragHelper.create(this, this.mCallback);
        CliStatusBarWindowController cliStatusBarWindowController = (CliStatusBarWindowController) Dependency.get(CliStatusBarWindowController.class);
        this.mCliStatusBarWindowController = cliStatusBarWindowController;
        cliStatusBarWindowController.registerCallback(this);
        this.mWallpaperManager = (WallpaperManager) context.getSystemService(WallpaperManager.class);
        this.mMotoDisplayManager = (MotoDisplayManager) Dependency.get(MotoDisplayManager.class);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mLockIconPaddingEndForAD = getResources().getDimensionPixelSize(R$dimen.cli_statusbar_lockicon_padding_end_for_ad);
        this.mLockIconPaddingEndForKeyguard = getResources().getDimensionPixelSize(R$dimen.cli_statusbar_lockicon_padding_end_for_keyguard);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        Class cls = DarkIconDispatcher.class;
        super.onAttachedToWindow();
        ((DarkIconDispatcher) Dependency.get(cls)).addDarkReceiver(this.mBattery);
        ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).addIconGroup(this.mDarkIconManager);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        ((DarkIconDispatcher) Dependency.get(cls)).addDarkReceiver(this.mCliLockIcon);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        Class cls = DarkIconDispatcher.class;
        super.onDetachedFromWindow();
        ((DarkIconDispatcher) Dependency.get(cls)).removeDarkReceiver(this.mBattery);
        ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).removeIconGroup(this.mDarkIconManager);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
        ((DarkIconDispatcher) Dependency.get(cls)).removeDarkReceiver(this.mCliLockIcon);
    }

    public void onOverlayChanged() {
        ((BatteryMeterView) this.mBattery).updatePercentView();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mStatusBarView.layout(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), this.mStatusBarView.getMeasuredHeight() + getPaddingTop());
        this.mKeyguardView.layout(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), this.mKeyguardView.getMeasuredHeight() + getPaddingTop());
        this.mNotificationView.layout(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), this.mNotificationView.getMeasuredHeight() + getPaddingTop());
        int measuredHeight = this.mQsView.getMeasuredHeight();
        int i5 = (-measuredHeight) + ((int) (((float) measuredHeight) * this.mTopForQs));
        this.mQsView.layout(getPaddingLeft(), i5, getWidth() - getPaddingRight(), measuredHeight + i5);
        this.mScrim.layout(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), this.mScrim.getMeasuredHeight() + getPaddingTop());
        this.mCliKeyguardBlurBg.layout(0, 0, getWidth(), getHeight());
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        View.MeasureSpec.getMode(i);
        View.MeasureSpec.getSize(i);
        View.MeasureSpec.getMode(i2);
        View.MeasureSpec.getSize(i2);
        super.onMeasure(i, i2);
    }

    private void readyToCapture() {
        if (DEBUG) {
            Log.d("Cli_DragDownLayout", "readyToCapture status=" + stateToString(this.mStatus));
        }
        this.mStatus = 11;
        this.mIsExpanded = false;
        this.mCliBar.makeExpandedVisible(true);
        this.mCanCapture = false;
        post(new Runnable() {
            public void run() {
                if (CliPanelDragView.this.mViewDragHelper.smoothSlideViewTo(CliPanelDragView.this.mPanelView, CliPanelDragView.this.getPaddingLeft(), (CliPanelDragView.this.getPaddingTop() + CliPanelDragView.this.mStatusBarView.getMeasuredHeight()) - CliPanelDragView.this.mPanelView.getMeasuredHeight())) {
                    boolean unused = CliPanelDragView.this.mCanCapture = true;
                    CliPanelDragView.this.invalidate();
                    return;
                }
                boolean unused2 = CliPanelDragView.this.mCanCapture = true;
                if (CliPanelDragView.DEBUG) {
                    Log.d("Cli_DragDownLayout", "readyToCapture: Panel head is animating");
                }
            }
        });
        this.mScrim.setVisibility(0);
    }

    private void resetMoveState() {
        this.mStatus = 10;
        this.mScrim.setVisibility(8);
        this.mMediaPanelDraging = false;
    }

    /* access modifiers changed from: private */
    public void finishCapture() {
        if (DEBUG) {
            Log.d("Cli_DragDownLayout", "finishCapture");
        }
        resetMoveState();
        if (this.mPanelView == this.mScrim) {
            this.mIsExpanded = false;
        }
    }

    /* access modifiers changed from: private */
    public void finishCaptureFromCollapse() {
        if (DEBUG) {
            Log.d("Cli_DragDownLayout", "finishCaptureFromCollapse");
        }
        resetMoveState();
        this.mIsExpanded = false;
        if (this.mPanelView == this.mScrim) {
            this.mCliBar.dismissKeyguard();
        } else {
            this.mCliBar.makeExpandedVisible(false);
        }
    }

    /* access modifiers changed from: private */
    public void finishCaptureFromExpand() {
        if (DEBUG) {
            Log.d("Cli_DragDownLayout", "finishCaptureFromExpand");
        }
        resetMoveState();
        if (this.mPanelView == this.mScrim) {
            this.mIsExpanded = false;
            return;
        }
        this.mIsExpanded = true;
        if (this.mCliStatusBarWindowController.isDozing()) {
            this.mMotoDisplayManager.requestWakeup();
        }
    }

    private void initCapture() {
        Log.d("Cli_DragDownLayout", "initCapture mIsExpanded=" + this.mIsExpanded);
        this.mCanCapture = true;
        this.mDirection = 0;
        this.mInitialTouchX = 0.0f;
        this.mInitialTouchY = 0.0f;
    }

    private boolean onKeyguardIntercept(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mInitialTouchX = x;
            this.mInitialTouchY = y;
            return false;
        } else if (actionMasked != 2 || this.mDirection != 0) {
            return false;
        } else {
            float abs = Math.abs(y - this.mInitialTouchY);
            float abs2 = Math.abs(x - this.mInitialTouchX);
            Log.d("Cli_DragDownLayout", "onKeyguardIntercept dx=" + abs2 + ";dy=" + abs);
            if (abs == 0.0f && abs2 == 0.0f) {
                return false;
            }
            if (abs < abs2) {
                this.mDirection = 1;
                return false;
            }
            this.mDirection = 2;
            return false;
        }
    }

    private boolean onStatusbarIntercept(MotionEvent motionEvent) {
        return motionEvent.getActionMasked() == 0 && !this.mCliStatusBarWindowController.isPanelVisible() && touchOnKeyguardStatusbar((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    private boolean touchOnKeyguardStatusbar(int i, int i2) {
        return this.mViewDragHelper.isViewUnder(this.mStatusBarView, i, i2);
    }

    private boolean touchOnMediaView(int i, int i2) {
        CliMediaViewPager cliMediaViewPager = this.mMediaViewPager;
        if (cliMediaViewPager == null) {
            if (DEBUG) {
                Log.e("Cli_DragDownLayout", "Media view pager is null. Can't get the location.");
            }
            return false;
        }
        int[] iArr = new int[2];
        cliMediaViewPager.getLocationOnScreen(iArr);
        int width = this.mMediaViewPager.getWidth();
        int height = this.mMediaViewPager.getHeight();
        if (i <= iArr[0] || i >= iArr[0] + width || i2 <= iArr[1] || i2 >= iArr[1] + height) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void setMediaViewPager(CliMediaViewPager cliMediaViewPager) {
        this.mMediaViewPager = cliMediaViewPager;
    }

    /* access modifiers changed from: protected */
    public void setKeyguardMediaPanel(CliMediaViewPager cliMediaViewPager) {
        this.mKeyguardMeidaPanel = cliMediaViewPager;
    }

    private boolean isMediaOutputLayoutVisible() {
        View findViewById = findViewById(R$id.media_output_layout);
        if (findViewById == null || findViewById.getVisibility() != 0) {
            return false;
        }
        return true;
    }

    private boolean shouldHandleTouch(MotionEvent motionEvent) {
        if (!this.mCliBar.panelEnabled()) {
            Log.e("Cli_DragDownLayout", "onTouch: all cli panel disabled!");
            return false;
        } else if (!this.mCanInterceptTouch || dozingAndStowed() || isMediaOutputLayoutVisible() || this.mCliStatusBarWindowController.isDetailVisible() || this.mCliStatusBarWindowController.getNotificationCardShowing()) {
            return false;
        } else {
            if (this.mIsExpanded && this.mPanelView == this.mQsView && this.mMediaViewPager.getTutorialVisibility() == 0) {
                return false;
            }
            if (this.mKeyguardShowing && !this.mKeyguardOccluded && !this.mBouncerShowing && this.mKeyguardMeidaPanel.getTutorialVisibility() == 0) {
                return false;
            }
            if (dozingAndStowed()) {
                Log.e("Cli_DragDownLayout", "onTouch: dozing and stowed!");
                return false;
            } else if (motionEvent.getActionMasked() != 0 && motionEvent.getActionMasked() != 2) {
                return true;
            } else {
                int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                int activePointerId = this.mViewDragHelper.getActivePointerId();
                if (activePointerId == -1 || activePointerId == pointerId) {
                    return true;
                }
                return false;
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (!shouldHandleTouch(motionEvent)) {
            return false;
        }
        boolean isPanelVisible = this.mCliStatusBarWindowController.isPanelVisible();
        if (motionEvent.getAction() == 0 && this.mMediaViewPager.getVisibility() == 0 && touchOnMediaView((int) motionEvent.getX(), (int) motionEvent.getY()) && isPanelVisible) {
            this.mMediaPanelDraging = true;
        }
        if (this.mMediaPanelDraging) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                this.mMediaPanelDraging = false;
            }
            return false;
        }
        if (motionEvent.getActionMasked() == 0) {
            initCapture();
        }
        onKeyguardIntercept(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 2 && this.mDirection == 1) {
            z = false;
        } else {
            z = this.mViewDragHelper.shouldInterceptTouchEvent(motionEvent);
        }
        if (actionMasked == 0) {
            if (this.mStatus != 10) {
                return false;
            }
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (isPanelVisible && this.mViewDragHelper.isViewUnder(this.mQsView, (int) x, (int) y)) {
                this.mPanelView = this.mQsView;
            } else if (!isPanelVisible) {
                if (!this.mCliStatusBarWindowController.isDozing() && (onStatusbarIntercept(motionEvent) || this.mViewDragHelper.isEdgeTouched(4))) {
                    this.mPanelView = this.mQsView;
                    readyToCapture();
                    return true;
                } else if ((this.mKeyguardShowing || this.mDozing) && this.mViewDragHelper.isViewUnder(this.mKeyguardView, (int) x, (int) y)) {
                    this.mPanelView = this.mScrim;
                }
            }
        }
        if (actionMasked == 2 && this.mDirection == 1) {
            finishCapture();
            return false;
        } else if (z || this.mDirection == 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean dozingAndStowed() {
        return this.mCliStatusBarWindowController.isDozing() && this.mCliBar.getStowedState();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!shouldHandleTouch(motionEvent)) {
            return false;
        }
        if (motionEvent.getActionMasked() == 1) {
            int i = this.mStatus;
            if (i == 11) {
                if (DEBUG) {
                    Log.d("Cli_DragDownLayout", "ACTION_UP, panel head animating, not captured yet");
                }
                this.mStatus = 14;
            } else if (i == 12) {
                if (DEBUG) {
                    Log.d("Cli_DragDownLayout", "ACTION_UP, panel head animation finish, not captured yet");
                }
                smoothToTop();
            }
        }
        this.mViewDragHelper.processTouchEvent(motionEvent);
        return true;
    }

    /* access modifiers changed from: private */
    public void smoothToTop() {
        OnCloseListener onCloseListener;
        if (this.mViewDragHelper.smoothSlideViewTo(this.mPanelView, getPaddingLeft(), -this.mPanelView.getHeight())) {
            this.mStatus = 16;
            if (this.mPanelView == this.mQsView && (onCloseListener = this.mOnCloseListener) != null) {
                onCloseListener.close();
            }
            invalidate();
            return;
        }
        if (DEBUG) {
            Log.d("Cli_DragDownLayout", "smoothToTop: Panel head is animating=" + this.mPanelView.getTop());
        }
        if (this.mStatus != 10) {
            this.mStatus = 14;
        }
    }

    /* access modifiers changed from: private */
    public void smoothToBottom() {
        OnOpenListener onOpenListener;
        if (this.mViewDragHelper.smoothSlideViewTo(this.mPanelView, getPaddingLeft(), this.mStatusBarView.getTop())) {
            this.mStatus = 17;
            if (this.mPanelView == this.mQsView && (onOpenListener = this.mOnOpenListener) != null) {
                onOpenListener.open();
            }
            invalidate();
            return;
        }
        if (DEBUG) {
            Log.d("Cli_DragDownLayout", "smoothToBottom: Panel head is animating=" + this.mPanelView.getTop());
        }
        if (this.mStatus != 10) {
            this.mStatus = 14;
        }
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public boolean isOpen() {
        return this.mCliStatusBarWindowController.isPanelVisible();
    }

    public void openContent() {
        if (this.mStatus == 10 && !this.mCliStatusBarWindowController.isPanelVisible()) {
            this.mPanelView = this.mQsView;
            smoothToBottom();
        }
    }

    public void closeContent() {
        if (this.mStatus == 10 && this.mCliStatusBarWindowController.isPanelVisible()) {
            this.mPanelView = this.mQsView;
            smoothToTop();
        }
    }

    private boolean shouldResetKeyguard() {
        return (this.mDozing && !this.mBouncerShowing) || (this.mKeyguardShowing && !this.mKeyguardOccluded && !this.mBouncerShowing);
    }

    public void resetCliKeyguard() {
        if (shouldResetKeyguard()) {
            if (DEBUG) {
                Log.d("Cli_DragDownLayout", "mKeyguardShowing=" + this.mKeyguardShowing + ";mBouncerShowing=" + this.mBouncerShowing + ";mKeyguardOccluded=" + this.mKeyguardOccluded + ";mDozing=" + this.mDozing + ";mStatus=" + this.mStatus);
            }
            resetCliKeyguardInner();
        }
    }

    private void resetCliKeyguardInner() {
        if (this.mCliKeyguardBlurBg.getBackground() == null) {
            Log.e("Cli_DragDownLayout", "Cli keyguard wallpaper is null, reload again.");
            if (!loadWallpaperBitmap()) {
                Log.e("Cli_DragDownLayout", "resetCliKeyguardInner: Can't load CLI wallpaper.");
            }
        }
        scaleKeyguardView(1.0f);
        alphaKeyguardView(1.0f);
        resetMoveState();
        setKeyguardVisibility(0);
        if (hideQs()) {
            requestLayout();
        }
    }

    private void hideKeyguardView() {
        if (DEBUG) {
            Log.i("Cli_DragDownLayout", "hideKeyguardView");
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 1, 0.5f, 1, 0.5f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (CliPanelDragView.DEBUG) {
                    Log.i("Cli_DragDownLayout", "onAnimationEnd");
                }
                CliPanelDragView.this.mCliStatusBarWindowController.setBiometricUnlockCollapsing(false);
            }
        });
        animationSet.setDuration(400);
        this.mKeyguardView.startAnimation(animationSet);
        this.mCliKeyguardBlurBg.startAnimation(animationSet);
        this.mStatusBarView.startAnimation(animationSet);
    }

    private void setKeyguardVisibility(int i) {
        this.mKeyguardView.setVisibility(i);
        this.mStatusBarView.setVisibility(i);
        this.mCliKeyguardBlurBg.setVisibility(i);
    }

    private boolean hideQs() {
        boolean z = this.mTopForQs != 0.0f;
        if (!this.mCliStatusBarWindowController.isPanelVisible() && !z) {
            return false;
        }
        this.mQsView.setVisibility(8);
        this.mTopForQs = 0.0f;
        this.mCliBar.makeExpandedVisible(false);
        this.mIsExpanded = false;
        return z;
    }

    public void onStateChangedForCli(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        if (this.mKeyguardShowing != z || this.mBouncerShowing != z3 || this.mKeyguardOccluded != z2 || this.mDozing != z4 || this.mUnlockCollapsing != z5) {
            if (DEBUG) {
                Log.d("Cli_DragDownLayout", "keyguardShowing=" + z + ";keyguardOccluded=" + z2 + ";bouncerShowing=" + z3 + ";dozing=" + z4 + ";mStatus=" + this.mStatus + ";unlockCollapsing=" + z5);
            }
            resetMoveState();
            this.mKeyguardShowing = z;
            this.mKeyguardOccluded = z2;
            this.mBouncerShowing = z3;
            this.mDozing = z4;
            this.mUnlockCollapsing = z5;
            if (shouldResetKeyguard()) {
                resetCliKeyguardInner();
            } else {
                if (this.mUnlockCollapsing) {
                    hideKeyguardView();
                } else {
                    setKeyguardVisibility(8);
                }
                if (hideQs()) {
                    requestLayout();
                }
            }
            if (this.mCliStatusBarWindowController.isDozing()) {
                this.mStatusIconContainer.setVisibility(8);
                ((ImageView) this.mCliLockIcon).setPaddingRelative(0, 0, this.mLockIconPaddingEndForAD, 0);
                ((BatteryMeterView) this.mBattery).setPercentShowMode(3);
                return;
            }
            this.mStatusIconContainer.setVisibility(0);
            ((ImageView) this.mCliLockIcon).setPaddingRelative(0, 0, this.mLockIconPaddingEndForKeyguard, 0);
            ((BatteryMeterView) this.mBattery).setPercentShowMode(0);
        }
    }

    public void setOnOpenListener(OnOpenListener onOpenListener) {
        this.mOnOpenListener = onOpenListener;
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.mOnCloseListener = onCloseListener;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(R$id.cli_system_icons);
        this.mStatusBarView = findViewById;
        this.mStatusIconContainer = (StatusIconContainer) findViewById.findViewById(R$id.cli_statusIcons);
        this.mQsView = findViewById(R$id.cli_status_bar_panel);
        this.mKeyguardView = findViewById(R$id.cli_keyguard);
        this.mNotificationView = findViewById(R$id.cli_notification);
        this.mKeyguardView.setVisibility(8);
        this.mScrim = findViewById(R$id.scrim_in_front);
        this.mBattery = (DarkIconDispatcher.DarkReceiver) findViewById(R$id.cli_battery);
        this.mDarkIconManager = new StatusBarIconController.DarkIconManager(this.mStatusIconContainer, (FeatureFlags) Dependency.get(FeatureFlags.class));
        this.mCliLockIcon = (DarkIconDispatcher.DarkReceiver) findViewById(R$id.cli_locked);
        this.mCliKeyguardBlurBg = findViewById(R$id.cli_keyguard_blur_bg);
        if (!loadWallpaperBitmap()) {
            Log.e("Cli_DragDownLayout", "onFinishInflate: Can't load CLI wallpaper.");
        }
    }

    public void setInterceptTouch(boolean z) {
        this.mCanInterceptTouch = z;
    }

    /* access modifiers changed from: private */
    public void scaleKeyguardView(float f) {
        if (f >= 0.0f && f <= 1.0f) {
            float f2 = (f + 1.0f) / 2.0f;
            this.mKeyguardView.setScaleX(f2);
            this.mKeyguardView.setScaleY(f2);
        }
    }

    /* access modifiers changed from: private */
    public void alphaKeyguardView(float f) {
        if (f >= 0.0f && f <= 1.0f) {
            this.mKeyguardView.setAlpha(f);
        }
    }

    public boolean loadWallpaperBitmap() {
        Bitmap bitmap;
        WallpaperManager wallpaperManager = this.mWallpaperManager;
        if (wallpaperManager != null) {
            bitmap = wallpaperManager.getBitmap(4);
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postScale(0.2f, 0.2f);
                Bitmap fastBlur = fastBlur(getContext(), Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true), 25.0f);
                new Canvas(fastBlur).drawARGB(153, 0, 0, 0);
                this.mCliKeyguardBlurBg.setBackground(new BitmapDrawable(fastBlur));
            }
        } else {
            bitmap = null;
        }
        if (bitmap != null) {
            return true;
        }
        return false;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: android.renderscript.RenderScript} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: android.renderscript.ScriptIntrinsicBlur} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: android.renderscript.RenderScript} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: android.renderscript.ScriptIntrinsicBlur} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: android.renderscript.Allocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: android.renderscript.RenderScript} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: android.renderscript.ScriptIntrinsicBlur} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: android.renderscript.Allocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: android.renderscript.Allocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: android.renderscript.RenderScript} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: android.renderscript.ScriptIntrinsicBlur} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: android.renderscript.RenderScript} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: android.renderscript.ScriptIntrinsicBlur} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: android.renderscript.RenderScript} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x006b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.Bitmap fastBlur(android.content.Context r5, android.graphics.Bitmap r6, float r7) {
        /*
            r4 = this;
            android.graphics.Bitmap$Config r4 = r6.getConfig()
            android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888
            if (r4 == r0) goto L_0x0011
            r4 = 0
            android.graphics.Bitmap r4 = r6.copy(r0, r4)
            r6.recycle()
            r6 = r4
        L_0x0011:
            r4 = 0
            android.renderscript.RenderScript r5 = android.renderscript.RenderScript.create(r5)     // Catch:{ all -> 0x0056 }
            android.renderscript.Allocation$MipmapControl r0 = android.renderscript.Allocation.MipmapControl.MIPMAP_NONE     // Catch:{ all -> 0x0050 }
            r1 = 1
            android.renderscript.Allocation r0 = android.renderscript.Allocation.createFromBitmap(r5, r6, r0, r1)     // Catch:{ all -> 0x0050 }
            android.renderscript.Type r1 = r0.getType()     // Catch:{ all -> 0x004d }
            android.renderscript.Allocation r1 = android.renderscript.Allocation.createTyped(r5, r1)     // Catch:{ all -> 0x004d }
            android.renderscript.Element r2 = android.renderscript.Element.U8_4(r5)     // Catch:{ all -> 0x0048 }
            android.renderscript.ScriptIntrinsicBlur r4 = android.renderscript.ScriptIntrinsicBlur.create(r5, r2)     // Catch:{ all -> 0x0048 }
            r4.setRadius(r7)     // Catch:{ all -> 0x0048 }
            r4.setInput(r0)     // Catch:{ all -> 0x0048 }
            r4.forEach(r1)     // Catch:{ all -> 0x0048 }
            r1.copyTo(r6)     // Catch:{ all -> 0x0048 }
            if (r5 == 0) goto L_0x003e
            r5.destroy()
        L_0x003e:
            r4.destroy()
            r0.destroy()
            r1.destroy()
            return r6
        L_0x0048:
            r6 = move-exception
            r3 = r5
            r5 = r4
            r4 = r3
            goto L_0x005a
        L_0x004d:
            r6 = move-exception
            r1 = r4
            goto L_0x0053
        L_0x0050:
            r6 = move-exception
            r0 = r4
            r1 = r0
        L_0x0053:
            r4 = r5
            r5 = r1
            goto L_0x005a
        L_0x0056:
            r6 = move-exception
            r5 = r4
            r0 = r5
            r1 = r0
        L_0x005a:
            if (r4 == 0) goto L_0x005f
            r4.destroy()
        L_0x005f:
            if (r5 == 0) goto L_0x0064
            r5.destroy()
        L_0x0064:
            if (r0 == 0) goto L_0x0069
            r0.destroy()
        L_0x0069:
            if (r1 == 0) goto L_0x006e
            r1.destroy()
        L_0x006e:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.CliPanelDragView.fastBlur(android.content.Context, android.graphics.Bitmap, float):android.graphics.Bitmap");
    }

    public void onCliDensityOrFontScaleChanged() {
        updateStatusBarView();
    }

    private void updateStatusBarView() {
        Class cls = StatusBarIconController.class;
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.cli_status_bar_height);
        this.mStatusIconContainer.setPaddingRelative(0, 0, getResources().getDimensionPixelSize(R$dimen.signal_cluster_battery_padding), 0);
        ViewGroup.LayoutParams layoutParams = this.mStatusBarView.getLayoutParams();
        layoutParams.height = dimensionPixelSize;
        this.mStatusBarView.setLayoutParams(layoutParams);
        ((StatusBarIconController) Dependency.get(cls)).removeIconGroup(this.mDarkIconManager);
        this.mDarkIconManager = null;
        this.mDarkIconManager = new StatusBarIconController.DarkIconManager(this.mStatusIconContainer, (FeatureFlags) Dependency.get(FeatureFlags.class));
        ((StatusBarIconController) Dependency.get(cls)).addIconGroup(this.mDarkIconManager);
        ((BatteryMeterView) this.mBattery).onDensityOrFontScaleChanged();
    }
}
