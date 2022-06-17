package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.internal.util.Preconditions;
import com.android.systemui.CameraAvailabilityListener;
import com.android.systemui.RegionInterceptingFrameLayout;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p006qs.SecureSetting;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.events.PrivacyDotViewController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.concurrency.ThreadFactory;
import com.android.systemui.util.settings.SecureSettings;
import com.motorola.systemui.decorations.CameraDecoration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ScreenDecorations extends SystemUI implements TunerService.Tunable {
    private static final boolean DEBUG_COLOR;
    private static final boolean DEBUG_DISABLE_SCREEN_DECORATIONS = SystemProperties.getBoolean("debug.disable_screen_decorations", false);
    private static final boolean DEBUG_SCREENSHOT_ROUNDED_CORNERS;
    View mBottomLeftDot;
    View mBottomRightDot;
    private final BroadcastDispatcher mBroadcastDispatcher;
    /* access modifiers changed from: private */
    public CameraDecoration mCameraDecoration;
    private CameraAvailabilityListener mCameraListener;
    private CameraAvailabilityListener.CameraTransitionCallback mCameraTransitionCallback = new CameraAvailabilityListener.CameraTransitionCallback() {
        public void onApplyCameraProtection(Path path, Rect rect) {
            if (ScreenDecorations.this.mCameraDecoration != null) {
                ScreenDecorations.this.mCameraDecoration.onFrontCameraStateChange(true);
            }
            if (ScreenDecorations.this.mCutoutViews == null) {
                Log.w("ScreenDecorations", "DisplayCutoutView do not initialized");
                return;
            }
            for (DisplayCutoutView displayCutoutView : ScreenDecorations.this.mCutoutViews) {
                if (displayCutoutView != null) {
                    displayCutoutView.setProtection(path, rect);
                    displayCutoutView.setShowProtection(true);
                }
            }
        }

        public void onHideCameraProtection() {
            if (ScreenDecorations.this.mCameraDecoration != null) {
                ScreenDecorations.this.mCameraDecoration.onFrontCameraStateChange(false);
            }
            if (ScreenDecorations.this.mCutoutViews == null) {
                Log.w("ScreenDecorations", "DisplayCutoutView do not initialized");
                return;
            }
            for (DisplayCutoutView displayCutoutView : ScreenDecorations.this.mCutoutViews) {
                if (displayCutoutView != null) {
                    displayCutoutView.setShowProtection(false);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public SecureSetting mColorInversionSetting;
    private int mCornerDecorOverlayHeight;
    private int mCornerDecorOverlayWidth;
    private View mCornerDecorationOverlay;
    /* access modifiers changed from: private */
    public DisplayCutoutView[] mCutoutViews;
    private float mDensity;
    private DisplayManager.DisplayListener mDisplayListener;
    private DisplayManager mDisplayManager;
    private final PrivacyDotViewController mDotViewController;
    private DelayableExecutor mExecutor;
    private Handler mHandler;
    protected boolean mIsRegistered;
    private boolean mIsRoundedCornerMultipleRadius;
    private final Executor mMainExecutor;
    protected View[] mOverlays;
    /* access modifiers changed from: private */
    public boolean mPendingRotationChange;
    /* access modifiers changed from: private */
    public int mRotation;
    protected Point mRoundedDefault = new Point(0, 0);
    protected Point mRoundedDefaultBottom = new Point(0, 0);
    protected Point mRoundedDefaultTop = new Point(0, 0);
    private final SecureSettings mSecureSettings;
    private int mStatusBarHeightLandscape;
    private int mStatusBarHeightPortrait;
    private final ThreadFactory mThreadFactory;
    View mTopLeftDot;
    View mTopRightDot;
    private final TunerService mTunerService;
    private final BroadcastReceiver mUserSwitchIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ScreenDecorations.this.mColorInversionSetting.setUserId(ActivityManager.getCurrentUser());
            ScreenDecorations screenDecorations = ScreenDecorations.this;
            screenDecorations.updateColorInversion(screenDecorations.mColorInversionSetting.getValue());
        }
    };
    private final UserTracker mUserTracker;
    private WindowManager mWindowManager;

    /* access modifiers changed from: private */
    public static int getBoundPositionFromRotation(int i, int i2) {
        int i3 = i - i2;
        return i3 < 0 ? i3 + 4 : i3;
    }

    static {
        boolean z = SystemProperties.getBoolean("debug.screenshot_rounded_corners", false);
        DEBUG_SCREENSHOT_ROUNDED_CORNERS = z;
        DEBUG_COLOR = z;
    }

    public static Region rectsToRegion(List<Rect> list) {
        Region obtain = Region.obtain();
        if (list != null) {
            for (Rect next : list) {
                if (next != null && !next.isEmpty()) {
                    obtain.op(next, Region.Op.UNION);
                }
            }
        }
        return obtain;
    }

    public ScreenDecorations(Context context, Executor executor, SecureSettings secureSettings, BroadcastDispatcher broadcastDispatcher, TunerService tunerService, UserTracker userTracker, PrivacyDotViewController privacyDotViewController, ThreadFactory threadFactory) {
        super(context);
        this.mMainExecutor = executor;
        this.mSecureSettings = secureSettings;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mTunerService = tunerService;
        this.mUserTracker = userTracker;
        this.mDotViewController = privacyDotViewController;
        this.mThreadFactory = threadFactory;
    }

    public void start() {
        if (DEBUG_DISABLE_SCREEN_DECORATIONS) {
            Log.i("ScreenDecorations", "ScreenDecorations is disabled");
            return;
        }
        Handler buildHandlerOnNewThread = this.mThreadFactory.buildHandlerOnNewThread("ScreenDecorations");
        this.mHandler = buildHandlerOnNewThread;
        DelayableExecutor buildDelayableExecutorOnHandler = this.mThreadFactory.buildDelayableExecutorOnHandler(buildHandlerOnNewThread);
        this.mExecutor = buildDelayableExecutorOnHandler;
        buildDelayableExecutorOnHandler.execute(new ScreenDecorations$$ExternalSyntheticLambda2(this));
        this.mDotViewController.setUiExecutor(this.mExecutor);
    }

    /* access modifiers changed from: private */
    public void startOnScreenDecorationsThread() {
        this.mRotation = this.mContext.getDisplay().getRotation();
        this.mWindowManager = (WindowManager) this.mContext.getSystemService(WindowManager.class);
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mIsRoundedCornerMultipleRadius = this.mContext.getResources().getBoolean(R$bool.config_roundedCornerMultipleRadius);
        updateRoundedCornerRadii();
        this.mCornerDecorOverlayWidth = this.mContext.getResources().getDimensionPixelSize(R$dimen.zz_moto_corner_decor_width);
        this.mCornerDecorOverlayHeight = this.mContext.getResources().getDimensionPixelSize(R$dimen.zz_moto_corner_decor_height);
        setupDecorations();
        setupCameraListener();
        C07952 r0 = new DisplayManager.DisplayListener() {
            public void onDisplayAdded(int i) {
            }

            public void onDisplayRemoved(int i) {
            }

            public void onDisplayChanged(int i) {
                int rotation = ScreenDecorations.this.mContext.getDisplay().getRotation();
                ScreenDecorations screenDecorations = ScreenDecorations.this;
                if (!(screenDecorations.mOverlays == null || screenDecorations.mRotation == rotation)) {
                    boolean unused = ScreenDecorations.this.mPendingRotationChange = true;
                    for (int i2 = 0; i2 < 4; i2++) {
                        View[] viewArr = ScreenDecorations.this.mOverlays;
                        if (viewArr[i2] != null) {
                            ViewTreeObserver viewTreeObserver = viewArr[i2].getViewTreeObserver();
                            ScreenDecorations screenDecorations2 = ScreenDecorations.this;
                            viewTreeObserver.addOnPreDrawListener(new RestartingPreDrawListener(screenDecorations2.mOverlays[i2], i2, rotation));
                        }
                    }
                }
                ScreenDecorations.this.updateOrientation();
            }
        };
        this.mDisplayListener = r0;
        this.mDisplayManager.registerDisplayListener(r0, this.mHandler);
        updateOrientation();
    }

    private void setupDecorations() {
        Rect[] rectArr = null;
        if (hasCornerDecorOverlay() && this.mCornerDecorationOverlay == null) {
            View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.zz_moto_corner_decorations, (ViewGroup) null);
            this.mCornerDecorationOverlay = inflate;
            inflate.setSystemUiVisibility(256);
            this.mCornerDecorationOverlay.setAlpha(1.0f);
            this.mCornerDecorationOverlay.setForceDarkAllowed(false);
            this.mWindowManager.addView(this.mCornerDecorationOverlay, getCornerDecorWindowLayoutParams());
            this.mCornerDecorationOverlay.getViewTreeObserver().addOnPreDrawListener(new ValidatingPreDrawListener(this.mCornerDecorationOverlay));
        }
        if (this.mContext.getResources().getBoolean(17891635) && this.mCameraDecoration == null) {
            this.mCameraDecoration = new CameraDecoration(this.mContext);
        }
        if (hasRoundedCorners() || shouldDrawCutout()) {
            updateStatusBarHeight();
            DisplayCutout cutout = getCutout();
            if (cutout != null) {
                rectArr = cutout.getBoundingRectsAll();
            }
            for (int i = 0; i < 4; i++) {
                int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
                if ((rectArr == null || rectArr[boundPositionFromRotation].isEmpty()) && !shouldShowRoundedCorner(i)) {
                    removeOverlay(i);
                } else {
                    createOverlay(i);
                }
            }
            this.mDotViewController.initialize(this.mTopLeftDot, this.mTopRightDot, this.mBottomLeftDot, this.mBottomRightDot);
        } else {
            removeAllOverlays();
        }
        if (!hasOverlays()) {
            this.mMainExecutor.execute(new ScreenDecorations$$ExternalSyntheticLambda0(this));
            SecureSetting secureSetting = this.mColorInversionSetting;
            if (secureSetting != null) {
                secureSetting.setListening(false);
            }
            this.mBroadcastDispatcher.unregisterReceiver(this.mUserSwitchIntentReceiver);
            this.mIsRegistered = false;
        } else if (!this.mIsRegistered) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            this.mDisplayManager.getDisplay(0).getMetrics(displayMetrics);
            this.mDensity = displayMetrics.density;
            this.mExecutor.execute(new ScreenDecorations$$ExternalSyntheticLambda3(this));
            if (this.mColorInversionSetting == null) {
                this.mColorInversionSetting = new SecureSetting(this.mSecureSettings, this.mHandler, "accessibility_display_inversion_enabled", this.mUserTracker.getUserId()) {
                    /* access modifiers changed from: protected */
                    public void handleValueChanged(int i, boolean z) {
                        ScreenDecorations.this.updateColorInversion(i);
                    }
                };
            }
            this.mColorInversionSetting.setListening(true);
            this.mColorInversionSetting.onChange(false);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            this.mBroadcastDispatcher.registerReceiver(this.mUserSwitchIntentReceiver, intentFilter, this.mExecutor, UserHandle.ALL);
            this.mIsRegistered = true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupDecorations$0() {
        this.mTunerService.addTunable(this, "sysui_rounded_size");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupDecorations$1() {
        this.mTunerService.removeTunable(this);
    }

    /* access modifiers changed from: package-private */
    public DisplayCutout getCutout() {
        return this.mContext.getDisplay().getCutout();
    }

    /* access modifiers changed from: package-private */
    public boolean hasOverlays() {
        if (this.mOverlays == null) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (this.mOverlays[i] != null) {
                return true;
            }
        }
        this.mOverlays = null;
        return false;
    }

    private void removeAllOverlays() {
        if (this.mOverlays != null) {
            for (int i = 0; i < 4; i++) {
                if (this.mOverlays[i] != null) {
                    removeOverlay(i);
                }
            }
            this.mOverlays = null;
        }
    }

    private void removeOverlay(int i) {
        View[] viewArr = this.mOverlays;
        if (viewArr != null && viewArr[i] != null) {
            this.mWindowManager.removeViewImmediate(viewArr[i]);
            this.mOverlays[i] = null;
        }
    }

    private void createOverlay(final int i) {
        if (this.mOverlays == null) {
            this.mOverlays = new View[4];
        }
        if (this.mCutoutViews == null) {
            this.mCutoutViews = new DisplayCutoutView[4];
        }
        View[] viewArr = this.mOverlays;
        if (viewArr[i] == null) {
            viewArr[i] = overlayForPosition(i);
            this.mCutoutViews[i] = new DisplayCutoutView(this.mContext, i, this);
            ((ViewGroup) this.mOverlays[i]).addView(this.mCutoutViews[i]);
            this.mOverlays[i].setSystemUiVisibility(256);
            this.mOverlays[i].setAlpha(0.0f);
            this.mOverlays[i].setForceDarkAllowed(false);
            updateView(i);
            this.mWindowManager.addView(this.mOverlays[i], getWindowLayoutParams(i));
            this.mOverlays[i].addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    ScreenDecorations.this.mOverlays[i].removeOnLayoutChangeListener(this);
                    ScreenDecorations.this.mOverlays[i].animate().alpha(1.0f).setDuration(1000).start();
                }
            });
            this.mOverlays[i].getViewTreeObserver().addOnPreDrawListener(new ValidatingPreDrawListener(this.mOverlays[i]));
        }
    }

    private View overlayForPosition(int i) {
        if (i == 0 || i == 1) {
            View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.rounded_corners_top, (ViewGroup) null);
            this.mTopLeftDot = inflate.findViewById(R$id.privacy_dot_left_container);
            this.mTopRightDot = inflate.findViewById(R$id.privacy_dot_right_container);
            return inflate;
        } else if (i == 2 || i == 3) {
            View inflate2 = LayoutInflater.from(this.mContext).inflate(R$layout.rounded_corners_bottom, (ViewGroup) null);
            this.mBottomLeftDot = inflate2.findViewById(R$id.privacy_dot_left_container);
            this.mBottomRightDot = inflate2.findViewById(R$id.privacy_dot_right_container);
            return inflate2;
        } else {
            throw new IllegalArgumentException("Unknown bounds position");
        }
    }

    private void updateView(int i) {
        View[] viewArr = this.mOverlays;
        if (viewArr != null && viewArr[i] != null) {
            updateRoundedCornerView(i, R$id.left);
            updateRoundedCornerView(i, R$id.right);
            updateRoundedCornerSize(this.mRoundedDefault, this.mRoundedDefaultTop, this.mRoundedDefaultBottom);
            DisplayCutoutView[] displayCutoutViewArr = this.mCutoutViews;
            if (displayCutoutViewArr != null && displayCutoutViewArr[i] != null) {
                displayCutoutViewArr[i].setRotation(this.mRotation);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public WindowManager.LayoutParams getWindowLayoutParams(int i) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(getWidthLayoutParamByPos(i), getHeightLayoutParamByPos(i), 2024, 545259816, -3);
        int i2 = layoutParams.privateFlags | 80;
        layoutParams.privateFlags = i2;
        if (!DEBUG_SCREENSHOT_ROUNDED_CORNERS) {
            layoutParams.privateFlags = i2 | 1048576;
        }
        layoutParams.setTitle(getWindowTitleByPos(i));
        layoutParams.gravity = getOverlayWindowGravity(i);
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.setFitInsetsTypes(0);
        layoutParams.privateFlags |= 16777216;
        return layoutParams;
    }

    private int getWidthLayoutParamByPos(int i) {
        int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
        return (boundPositionFromRotation == 1 || boundPositionFromRotation == 3) ? -1 : -2;
    }

    private int getHeightLayoutParamByPos(int i) {
        int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
        return (boundPositionFromRotation == 1 || boundPositionFromRotation == 3) ? -2 : -1;
    }

    private static String getWindowTitleByPos(int i) {
        if (i == 0) {
            return "ScreenDecorOverlayLeft";
        }
        if (i == 1) {
            return "ScreenDecorOverlay";
        }
        if (i == 2) {
            return "ScreenDecorOverlayRight";
        }
        if (i == 3) {
            return "ScreenDecorOverlayBottom";
        }
        throw new IllegalArgumentException("unknown bound position: " + i);
    }

    private int getOverlayWindowGravity(int i) {
        int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
        if (boundPositionFromRotation == 0) {
            return 3;
        }
        if (boundPositionFromRotation == 1) {
            return 48;
        }
        if (boundPositionFromRotation == 2) {
            return 5;
        }
        if (boundPositionFromRotation == 3) {
            return 80;
        }
        throw new IllegalArgumentException("unknown bound position: " + i);
    }

    private void setupCameraListener() {
        if (this.mContext.getResources().getBoolean(R$bool.config_enableDisplayCutoutProtection)) {
            CameraAvailabilityListener build = CameraAvailabilityListener.Factory.build(this.mContext, this.mExecutor);
            this.mCameraListener = build;
            build.addTransitionCallback(this.mCameraTransitionCallback);
            this.mCameraListener.startListening();
        }
    }

    /* access modifiers changed from: private */
    public void updateColorInversion(int i) {
        int i2 = i != 0 ? -1 : -16777216;
        if (DEBUG_COLOR) {
            i2 = -65536;
        }
        ColorStateList valueOf = ColorStateList.valueOf(i2);
        if (this.mOverlays != null) {
            for (int i3 = 0; i3 < 4; i3++) {
                View[] viewArr = this.mOverlays;
                if (viewArr[i3] != null) {
                    int childCount = ((ViewGroup) viewArr[i3]).getChildCount();
                    for (int i4 = 0; i4 < childCount; i4++) {
                        View childAt = ((ViewGroup) this.mOverlays[i3]).getChildAt(i4);
                        if (!(childAt.getId() == R$id.privacy_dot_left_container || childAt.getId() == R$id.privacy_dot_right_container)) {
                            if (childAt instanceof ImageView) {
                                ((ImageView) childAt).setImageTintList(valueOf);
                            } else if (childAt instanceof DisplayCutoutView) {
                                ((DisplayCutoutView) childAt).setColor(i2);
                            }
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        if (DEBUG_DISABLE_SCREEN_DECORATIONS) {
            Log.i("ScreenDecorations", "ScreenDecorations is disabled");
        } else {
            this.mExecutor.execute(new ScreenDecorations$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onConfigurationChanged$2() {
        this.mPendingRotationChange = false;
        updateOrientation();
        updateRoundedCornerRadii();
        setupDecorations();
        if (this.mOverlays != null) {
            updateLayoutParams();
        }
    }

    /* access modifiers changed from: private */
    public void updateOrientation() {
        boolean z = this.mHandler.getLooper().getThread() == Thread.currentThread();
        Preconditions.checkState(z, "must call on " + this.mHandler.getLooper().getThread() + ", but was " + Thread.currentThread());
        int rotation = this.mContext.getDisplay().getRotation();
        if (this.mRotation != rotation) {
            this.mDotViewController.setNewRotation(rotation);
        }
        CameraDecoration cameraDecoration = this.mCameraDecoration;
        if (cameraDecoration != null) {
            cameraDecoration.updateOrientation(rotation);
        }
        if (!this.mPendingRotationChange && rotation != this.mRotation) {
            this.mRotation = rotation;
            if (this.mCornerDecorationOverlay != null) {
                updateCornerDecorLayoutParams();
                updateCornerDecorOverlaySize();
                updateCornerDecorOrientation();
            }
            if (this.mOverlays != null) {
                updateLayoutParams();
                for (int i = 0; i < 4; i++) {
                    if (this.mOverlays[i] != null) {
                        updateView(i);
                    }
                }
            }
        }
    }

    private void updateStatusBarHeight() {
        this.mStatusBarHeightLandscape = this.mContext.getResources().getDimensionPixelSize(17105534);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(17105535);
        this.mStatusBarHeightPortrait = dimensionPixelSize;
        this.mDotViewController.setStatusBarHeights(dimensionPixelSize, this.mStatusBarHeightLandscape);
    }

    private void updateRoundedCornerRadii() {
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(17105504);
        int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(17105508);
        int dimensionPixelSize3 = this.mContext.getResources().getDimensionPixelSize(17105506);
        Point point = this.mRoundedDefault;
        if ((point.x == dimensionPixelSize && this.mRoundedDefaultTop.x == dimensionPixelSize2 && this.mRoundedDefaultBottom.x == dimensionPixelSize3) ? false : true) {
            if (this.mIsRoundedCornerMultipleRadius) {
                Drawable drawable = this.mContext.getDrawable(R$drawable.rounded);
                this.mRoundedDefault.set(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                Drawable drawable2 = this.mContext.getDrawable(R$drawable.rounded_corner_top);
                this.mRoundedDefaultTop.set(drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
                Drawable drawable3 = this.mContext.getDrawable(R$drawable.rounded_corner_bottom);
                this.mRoundedDefaultBottom.set(drawable3.getIntrinsicWidth(), drawable3.getIntrinsicHeight());
            } else {
                point.set(dimensionPixelSize, dimensionPixelSize);
                this.mRoundedDefaultTop.set(dimensionPixelSize2, dimensionPixelSize2);
                this.mRoundedDefaultBottom.set(dimensionPixelSize3, dimensionPixelSize3);
            }
            onTuningChanged("sysui_rounded_size", (String) null);
        }
    }

    private void updateRoundedCornerView(int i, int i2) {
        View findViewById = this.mOverlays[i].findViewById(i2);
        if (findViewById != null) {
            findViewById.setVisibility(8);
            if (shouldShowRoundedCorner(i)) {
                int roundedCornerGravity = getRoundedCornerGravity(i, i2 == R$id.left);
                ((FrameLayout.LayoutParams) findViewById.getLayoutParams()).gravity = roundedCornerGravity;
                setRoundedCornerOrientation(findViewById, roundedCornerGravity);
                findViewById.setVisibility(0);
            }
        }
    }

    private int getRoundedCornerGravity(int i, boolean z) {
        int boundPositionFromRotation = getBoundPositionFromRotation(i, this.mRotation);
        if (boundPositionFromRotation == 0) {
            return z ? 51 : 83;
        }
        if (boundPositionFromRotation == 1) {
            return z ? 51 : 53;
        }
        if (boundPositionFromRotation == 2) {
            return z ? 53 : 85;
        }
        if (boundPositionFromRotation == 3) {
            return z ? 83 : 85;
        }
        throw new IllegalArgumentException("Incorrect position: " + boundPositionFromRotation);
    }

    private void setRoundedCornerOrientation(View view, int i) {
        view.setRotation(0.0f);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        if (i == 51) {
            return;
        }
        if (i == 53) {
            view.setScaleX(-1.0f);
        } else if (i == 83) {
            view.setScaleY(-1.0f);
        } else if (i == 85) {
            view.setRotation(180.0f);
        } else {
            throw new IllegalArgumentException("Unsupported gravity: " + i);
        }
    }

    private boolean hasRoundedCorners() {
        return this.mRoundedDefault.x > 0 || this.mRoundedDefaultBottom.x > 0 || this.mRoundedDefaultTop.x > 0 || this.mIsRoundedCornerMultipleRadius;
    }

    private boolean shouldShowRoundedCorner(int i) {
        if (!hasRoundedCorners()) {
            return false;
        }
        DisplayCutout cutout = getCutout();
        boolean z = cutout == null || cutout.isBoundsEmpty();
        int boundPositionFromRotation = getBoundPositionFromRotation(1, this.mRotation);
        int boundPositionFromRotation2 = getBoundPositionFromRotation(3, this.mRotation);
        if (z || !cutout.getBoundingRectsAll()[boundPositionFromRotation].isEmpty() || !cutout.getBoundingRectsAll()[boundPositionFromRotation2].isEmpty()) {
            if (i == 1 || i == 3) {
                return true;
            }
            return false;
        } else if (i == 0 || i == 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldDrawCutout() {
        return shouldDrawCutout(this.mContext);
    }

    static boolean shouldDrawCutout(Context context) {
        return context.getResources().getBoolean(17891609);
    }

    private void updateLayoutParams() {
        if (this.mOverlays != null) {
            for (int i = 0; i < 4; i++) {
                View[] viewArr = this.mOverlays;
                if (viewArr[i] != null) {
                    this.mWindowManager.updateViewLayout(viewArr[i], getWindowLayoutParams(i));
                }
            }
        }
    }

    public void onTuningChanged(String str, String str2) {
        if (DEBUG_DISABLE_SCREEN_DECORATIONS) {
            Log.i("ScreenDecorations", "ScreenDecorations is disabled");
        } else {
            this.mExecutor.execute(new ScreenDecorations$$ExternalSyntheticLambda4(this, str, str2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTuningChanged$3(String str, String str2) {
        if (this.mOverlays != null && "sysui_rounded_size".equals(str)) {
            Point point = this.mRoundedDefault;
            Point point2 = this.mRoundedDefaultTop;
            Point point3 = this.mRoundedDefaultBottom;
            if (str2 != null) {
                try {
                    int parseInt = (int) (((float) Integer.parseInt(str2)) * this.mDensity);
                    point = new Point(parseInt, parseInt);
                } catch (Exception unused) {
                }
            }
            updateRoundedCornerSize(point, point2, point3);
        }
    }

    private void updateRoundedCornerSize(Point point, Point point2, Point point3) {
        if (this.mOverlays != null) {
            if (point2.x == 0) {
                point2 = point;
            }
            if (point3.x != 0) {
                point = point3;
            }
            for (int i = 0; i < 4; i++) {
                View[] viewArr = this.mOverlays;
                if (viewArr[i] != null) {
                    if (i == 0 || i == 2) {
                        if (this.mRotation == 3) {
                            setSize(viewArr[i].findViewById(R$id.left), point);
                            setSize(this.mOverlays[i].findViewById(R$id.right), point2);
                        } else {
                            setSize(viewArr[i].findViewById(R$id.left), point2);
                            setSize(this.mOverlays[i].findViewById(R$id.right), point);
                        }
                    } else if (i == 1) {
                        setSize(viewArr[i].findViewById(R$id.left), point2);
                        setSize(this.mOverlays[i].findViewById(R$id.right), point2);
                    } else if (i == 3) {
                        setSize(viewArr[i].findViewById(R$id.left), point);
                        setSize(this.mOverlays[i].findViewById(R$id.right), point);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setSize(View view, Point point) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = point.x;
        layoutParams.height = point.y;
        view.setLayoutParams(layoutParams);
    }

    public static class DisplayCutoutView extends View implements DisplayManager.DisplayListener, RegionInterceptingFrameLayout.RegionInterceptableView {
        private final Path mBoundingPath = new Path();
        private final Rect mBoundingRect = new Rect();
        private final List<Rect> mBounds = new ArrayList();
        /* access modifiers changed from: private */
        public ValueAnimator mCameraProtectionAnimator;
        private float mCameraProtectionProgress = 0.5f;
        private int mColor = -16777216;
        private final ScreenDecorations mDecorations;
        private Display.Mode mDisplayMode = null;
        private final DisplayInfo mInfo = new DisplayInfo();
        private int mInitialPosition;
        private final int[] mLocation = new int[2];
        private final Paint mPaint = new Paint();
        private int mPosition;
        private Path mProtectionPath;
        private Path mProtectionPathOrig;
        private RectF mProtectionRect;
        private RectF mProtectionRectOrig;
        private int mRotation;
        /* access modifiers changed from: private */
        public boolean mShowProtection = false;
        private Rect mTotalBounds = new Rect();

        public void onDisplayAdded(int i) {
        }

        public void onDisplayRemoved(int i) {
        }

        public DisplayCutoutView(Context context, int i, ScreenDecorations screenDecorations) {
            super(context);
            this.mInitialPosition = i;
            this.mDecorations = screenDecorations;
            setId(R$id.display_cutout);
        }

        public void setColor(int i) {
            this.mColor = i;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).registerDisplayListener(this, getHandler());
            update();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).unregisterDisplayListener(this);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            getLocationOnScreen(this.mLocation);
            int[] iArr = this.mLocation;
            canvas.translate((float) (-iArr[0]), (float) (-iArr[1]));
            if (!this.mBoundingPath.isEmpty()) {
                this.mPaint.setColor(this.mColor);
                this.mPaint.setStyle(Paint.Style.FILL);
                this.mPaint.setAntiAlias(true);
                canvas.drawPath(this.mBoundingPath, this.mPaint);
            }
            if (this.mCameraProtectionProgress > 0.5f && !this.mProtectionRect.isEmpty()) {
                float f = this.mCameraProtectionProgress;
                canvas.scale(f, f, this.mProtectionRect.centerX(), this.mProtectionRect.centerY());
                canvas.drawPath(this.mProtectionPath, this.mPaint);
            }
        }

        public void onDisplayChanged(int i) {
            Display.Mode mode = this.mDisplayMode;
            Display.Mode mode2 = getDisplay().getMode();
            this.mDisplayMode = mode2;
            if (modeChanged(mode, mode2) && i == getDisplay().getDisplayId()) {
                update();
            }
        }

        private boolean modeChanged(Display.Mode mode, Display.Mode mode2) {
            boolean z = true;
            if (mode == null) {
                return true;
            }
            boolean z2 = (mode.getPhysicalHeight() != mode2.getPhysicalHeight()) | false;
            if (mode.getPhysicalWidth() == mode2.getPhysicalWidth()) {
                z = false;
            }
            return z | z2;
        }

        public void setRotation(int i) {
            this.mRotation = i;
            update();
        }

        /* access modifiers changed from: package-private */
        public void setProtection(Path path, Rect rect) {
            if (this.mProtectionPathOrig == null) {
                this.mProtectionPathOrig = new Path();
                this.mProtectionPath = new Path();
            }
            this.mProtectionPathOrig.set(path);
            if (this.mProtectionRectOrig == null) {
                this.mProtectionRectOrig = new RectF();
                this.mProtectionRect = new RectF();
            }
            this.mProtectionRectOrig.set(rect);
        }

        /* access modifiers changed from: package-private */
        public void setShowProtection(boolean z) {
            if (this.mShowProtection != z) {
                this.mShowProtection = z;
                updateBoundingPath();
                if (this.mShowProtection) {
                    requestLayout();
                }
                ValueAnimator valueAnimator = this.mCameraProtectionAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.mCameraProtectionProgress;
                fArr[1] = this.mShowProtection ? 1.0f : 0.5f;
                ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(750);
                this.mCameraProtectionAnimator = duration;
                duration.setInterpolator(Interpolators.DECELERATE_QUINT);
                this.mCameraProtectionAnimator.addUpdateListener(new ScreenDecorations$DisplayCutoutView$$ExternalSyntheticLambda0(this));
                this.mCameraProtectionAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ValueAnimator unused = DisplayCutoutView.this.mCameraProtectionAnimator = null;
                        if (!DisplayCutoutView.this.mShowProtection) {
                            DisplayCutoutView.this.requestLayout();
                        }
                    }
                });
                this.mCameraProtectionAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setShowProtection$1(ValueAnimator valueAnimator) {
            this.mCameraProtectionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        private void update() {
            int i;
            if (isAttachedToWindow() && !this.mDecorations.mPendingRotationChange) {
                this.mPosition = ScreenDecorations.getBoundPositionFromRotation(this.mInitialPosition, this.mRotation);
                requestLayout();
                getDisplay().getDisplayInfo(this.mInfo);
                this.mBounds.clear();
                this.mBoundingRect.setEmpty();
                this.mBoundingPath.reset();
                if (!ScreenDecorations.shouldDrawCutout(getContext()) || !hasCutout()) {
                    i = 8;
                } else {
                    this.mBounds.addAll(this.mInfo.displayCutout.getBoundingRects());
                    localBounds(this.mBoundingRect);
                    updateGravity();
                    updateBoundingPath();
                    invalidate();
                    i = 0;
                }
                if (i != getVisibility()) {
                    setVisibility(i);
                }
            }
        }

        private void updateBoundingPath() {
            DisplayInfo displayInfo = this.mInfo;
            int i = displayInfo.logicalWidth;
            int i2 = displayInfo.logicalHeight;
            int i3 = displayInfo.rotation;
            boolean z = true;
            if (!(i3 == 1 || i3 == 3)) {
                z = false;
            }
            int i4 = z ? i2 : i;
            if (!z) {
                i = i2;
            }
            Path pathFromResources = DisplayCutout.pathFromResources(getResources(), i4, i);
            if (pathFromResources != null) {
                this.mBoundingPath.set(pathFromResources);
            } else {
                this.mBoundingPath.reset();
            }
            Matrix matrix = new Matrix();
            transformPhysicalToLogicalCoordinates(this.mInfo.rotation, i4, i, matrix);
            this.mBoundingPath.transform(matrix);
            Path path = this.mProtectionPathOrig;
            if (path != null) {
                this.mProtectionPath.set(path);
                this.mProtectionPath.transform(matrix);
                matrix.mapRect(this.mProtectionRect, this.mProtectionRectOrig);
            }
        }

        private static void transformPhysicalToLogicalCoordinates(int i, int i2, int i3, Matrix matrix) {
            if (i == 0) {
                matrix.reset();
            } else if (i == 1) {
                matrix.setRotate(270.0f);
                matrix.postTranslate(0.0f, (float) i2);
            } else if (i == 2) {
                matrix.setRotate(180.0f);
                matrix.postTranslate((float) i2, (float) i3);
            } else if (i == 3) {
                matrix.setRotate(90.0f);
                matrix.postTranslate((float) i3, 0.0f);
            } else {
                throw new IllegalArgumentException("Unknown rotation: " + i);
            }
        }

        private void updateGravity() {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) layoutParams;
                int gravity = getGravity(this.mInfo.displayCutout);
                if (layoutParams2.gravity != gravity) {
                    layoutParams2.gravity = gravity;
                    setLayoutParams(layoutParams2);
                }
            }
        }

        private boolean hasCutout() {
            DisplayCutout displayCutout = this.mInfo.displayCutout;
            if (displayCutout == null) {
                return false;
            }
            int i = this.mPosition;
            if (i == 0) {
                return !displayCutout.getBoundingRectLeft().isEmpty();
            }
            if (i == 1) {
                return !displayCutout.getBoundingRectTop().isEmpty();
            }
            if (i == 3) {
                return !displayCutout.getBoundingRectBottom().isEmpty();
            }
            if (i == 2) {
                return !displayCutout.getBoundingRectRight().isEmpty();
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            if (this.mBounds.isEmpty()) {
                super.onMeasure(i, i2);
            } else if (this.mShowProtection) {
                this.mTotalBounds.union(this.mBoundingRect);
                Rect rect = this.mTotalBounds;
                RectF rectF = this.mProtectionRect;
                rect.union((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                setMeasuredDimension(View.resolveSizeAndState(this.mTotalBounds.width(), i, 0), View.resolveSizeAndState(this.mTotalBounds.height(), i2, 0));
            } else {
                setMeasuredDimension(View.resolveSizeAndState(this.mBoundingRect.width(), i, 0), View.resolveSizeAndState(this.mBoundingRect.height(), i2, 0));
            }
        }

        public static void boundsFromDirection(DisplayCutout displayCutout, int i, Rect rect) {
            if (i == 3) {
                rect.set(displayCutout.getBoundingRectLeft());
            } else if (i == 5) {
                rect.set(displayCutout.getBoundingRectRight());
            } else if (i == 48) {
                rect.set(displayCutout.getBoundingRectTop());
            } else if (i != 80) {
                rect.setEmpty();
            } else {
                rect.set(displayCutout.getBoundingRectBottom());
            }
        }

        private void localBounds(Rect rect) {
            DisplayCutout displayCutout = this.mInfo.displayCutout;
            boundsFromDirection(displayCutout, getGravity(displayCutout), rect);
        }

        private int getGravity(DisplayCutout displayCutout) {
            int i = this.mPosition;
            if (i == 0) {
                if (!displayCutout.getBoundingRectLeft().isEmpty()) {
                    return 3;
                }
                return 0;
            } else if (i == 1) {
                if (!displayCutout.getBoundingRectTop().isEmpty()) {
                    return 48;
                }
                return 0;
            } else if (i != 3) {
                return (i != 2 || displayCutout.getBoundingRectRight().isEmpty()) ? 0 : 5;
            } else {
                if (!displayCutout.getBoundingRectBottom().isEmpty()) {
                    return 80;
                }
                return 0;
            }
        }

        public boolean shouldInterceptTouch() {
            return this.mInfo.displayCutout != null && getVisibility() == 0;
        }

        public Region getInterceptRegion() {
            if (this.mInfo.displayCutout == null) {
                return null;
            }
            View rootView = getRootView();
            Region rectsToRegion = ScreenDecorations.rectsToRegion(this.mInfo.displayCutout.getBoundingRects());
            rootView.getLocationOnScreen(this.mLocation);
            int[] iArr = this.mLocation;
            rectsToRegion.translate(-iArr[0], -iArr[1]);
            rectsToRegion.op(rootView.getLeft(), rootView.getTop(), rootView.getRight(), rootView.getBottom(), Region.Op.INTERSECT);
            return rectsToRegion;
        }
    }

    private class RestartingPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        private final int mPosition;
        private final int mTargetRotation;
        private final View mView;

        private RestartingPreDrawListener(View view, int i, int i2) {
            this.mView = view;
            this.mTargetRotation = i2;
            this.mPosition = i;
        }

        public boolean onPreDraw() {
            this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
            if (this.mTargetRotation == ScreenDecorations.this.mRotation) {
                return true;
            }
            boolean unused = ScreenDecorations.this.mPendingRotationChange = false;
            ScreenDecorations.this.updateOrientation();
            this.mView.invalidate();
            return false;
        }
    }

    private class ValidatingPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        private final View mView;

        public ValidatingPreDrawListener(View view) {
            this.mView = view;
        }

        public boolean onPreDraw() {
            if (ScreenDecorations.this.mContext.getDisplay().getRotation() == ScreenDecorations.this.mRotation || ScreenDecorations.this.mPendingRotationChange) {
                return true;
            }
            this.mView.invalidate();
            return false;
        }
    }

    private boolean hasCornerDecorOverlay() {
        return this.mCornerDecorOverlayWidth > 0 || this.mCornerDecorOverlayHeight > 0;
    }

    /* access modifiers changed from: package-private */
    public WindowManager.LayoutParams getCornerDecorWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2024, 545259816, -3);
        int i = layoutParams.privateFlags | 80;
        layoutParams.privateFlags = i;
        if (!DEBUG_SCREENSHOT_ROUNDED_CORNERS) {
            layoutParams.privateFlags = i | 1048576;
        }
        layoutParams.setTitle("ScreenCornerDecorOverlay");
        layoutParams.gravity = 51;
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.setFitInsetsTypes(0);
        layoutParams.privateFlags |= 16777216;
        return layoutParams;
    }

    private void updateCornerDecorLayoutParams() {
        this.mWindowManager.updateViewLayout(this.mCornerDecorationOverlay, getCornerDecorWindowLayoutParams());
    }

    private void updateCornerDecorOrientation() {
        Drawable drawable;
        ImageView imageView = (ImageView) this.mCornerDecorationOverlay.findViewById(R$id.corner_decor);
        if (imageView != null) {
            imageView.setVisibility(8);
            int i = this.mRotation;
            if (i == 0) {
                drawable = this.mContext.getResources().getDrawable(R$drawable.zz_moto_corner_decor_none);
            } else if (i == 1) {
                drawable = this.mContext.getResources().getDrawable(R$drawable.zz_moto_corner_decor_land);
            } else if (i == 2) {
                drawable = this.mContext.getResources().getDrawable(R$drawable.zz_moto_corner_decor_updown);
            } else if (i == 3) {
                drawable = this.mContext.getResources().getDrawable(R$drawable.zz_moto_corner_decor_sea);
            } else {
                throw new IllegalArgumentException("unknown screen rotation: " + this.mRotation);
            }
            imageView.setImageDrawable(drawable);
            imageView.setVisibility(0);
        }
    }

    private void updateCornerDecorOverlaySize() {
        setSize(this.mCornerDecorationOverlay.findViewById(R$id.corner_decor), isLandscape() ? this.mCornerDecorOverlayHeight : this.mCornerDecorOverlayWidth, isLandscape() ? this.mCornerDecorOverlayWidth : this.mCornerDecorOverlayHeight);
    }

    private boolean isLandscape() {
        int i = this.mRotation;
        return i == 1 || i == 3;
    }

    private void setSize(View view, int i, int i2) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = i;
        layoutParams.height = i2;
        view.setLayoutParams(layoutParams);
    }
}
