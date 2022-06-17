package com.motorola.gesturetouch;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.navigationbar.gestural.RegionSamplingHelper;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.motorola.gesturetouch.GestureTouchSettingsManager;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import motorola.core_services.misc.MotoExtHwManager;

public class EdgeTouchPillController extends GestureTouchController implements GestureTouchSettingsManager.GestureSettingsListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private final int AM_START = 10;
    private final int AM_STOP = 12;
    private final float DEFAULT_WATERFALL_SCROLL_DISTANCE_MM = 18.0f;
    private final int EDGE_BOTH = 4;
    private final int EDGE_LEFT = 1;
    private final int EDGE_NONE = 0;
    private final int EDGE_RIGHT = 2;
    private final String LAYOUT_PARAM_TITLE_LEFT = "SystemUI Left Pill Vview";
    private final String LAYOUT_PARAM_TITLE_LEFT_DRAG = "SystemUI Left Drag Vview";
    private final String LAYOUT_PARAM_TITLE_RIGHT = "SystemUI Right Pill View";
    private final String LAYOUT_PARAM_TITLE_RIGHT_DRAG = "SystemUI Right Drag View";
    private final int PM1_START = 15;
    private final int PM1_STOP = 17;
    private final int PM2_START = 20;
    private final int PM2_STOP = 22;
    private final float SWIPE_WATERFALL_SCROLL_DISTANCE_MM = 12.0f;
    private final int WHATNEW_ANIMATION_DURATION_MS = 750;
    private final int WHATNEW_ANIMATION_STOP_DELAY = 60000;
    /* access modifiers changed from: private */
    public View mClickAbleArea;
    View.OnTouchListener mClickableAreaTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (EdgeTouchPillController.this.mIsInPositionMode) {
                EdgeTouchPillController.this.mPositionTouchController.onTouchEvent(motionEvent);
                return true;
            }
            EdgeTouchPillController.this.mGestureDetectionController.startDetection(motionEvent);
            return true;
        }
    };
    private CommandQueue mCommandQueue;
    private Context mContext;
    private ImageView mDotBottom;
    private ImageView mDotTop;
    WindowManager.LayoutParams mDragAreaLayoutParams;
    private View mEdgeTouchPillView;
    private GestureTouchSettingsManager mEdgeTouchSettingsManager;
    /* access modifiers changed from: private */
    public GestureDetectionController mGestureDetectionController;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mInTutorialMode;
    private boolean mIsAppAvailable;
    private boolean mIsGlobalActionShow;
    /* access modifiers changed from: private */
    public boolean mIsInPositionMode;
    private boolean mIsKeyguard;
    /* access modifiers changed from: private */
    public boolean mIsLightTheme = true;
    private boolean mIsNavBarShow = true;
    private boolean mIsNeedHidePill;
    private boolean mIsNeedShowWhatNew;
    private boolean mIsPortrait = true;
    /* access modifiers changed from: private */
    public boolean mIsReversed;
    /* access modifiers changed from: private */
    public boolean mIsShownToday;
    WindowManager.LayoutParams mLayoutParams;
    private View mLeftDragAreaView;
    private View mLeftDragView;
    NotificationPanelViewController mNotificationPanelViewController;
    private GestureActionController mPillActionController;
    private PillAnimationListener mPillAnimationListener;
    private boolean mPillGestureEnabled;
    private int mPillHeight;
    private int mPillPadding;
    private boolean mPillSwipeUpDownEnabled;
    private int mPillTouchableWidth;
    private PillVibrationListener mPillVibrationListener;
    private ImageView mPillView;
    /* access modifiers changed from: private */
    public final Rect mPillViewRect = new Rect();
    private int mPillWidth;
    private int mPillXPosition = 1;
    private int mPillYPosition;
    /* access modifiers changed from: private */
    public PositionTouchController mPositionTouchController;
    private RegionSamplingHelper mRegionSamplingHelper;
    private View mRightDragAreaView;
    private View mRightDragView;
    private boolean mShowWhatNewAnimator;
    private Runnable mStopWhatNewAnimatorRunnable = new Runnable() {
        public void run() {
            boolean unused = EdgeTouchPillController.this.mIsShownToday = true;
            EdgeTouchPillController.this.stopWhatNewAnimation();
        }
    };
    private boolean mVibrationEnabled;
    /* access modifiers changed from: private */
    public ValueAnimator mWhatNewAnimator;
    public WindowManager mWindowManager;

    private boolean isInTimeZone(int i, int i2, int i3) {
        return i3 >= i && i3 < i2;
    }

    public void updateSNGState(boolean z) {
    }

    public EdgeTouchPillController(Context context, NotificationPanelViewController notificationPanelViewController) {
        this.mContext = context;
        this.mCommandQueue = (CommandQueue) Dependency.get(CommandQueue.class);
        this.mNotificationPanelViewController = notificationPanelViewController;
        GestureTouchSettingsManager instance = GestureTouchSettingsManager.getInstance(this.mContext);
        this.mEdgeTouchSettingsManager = instance;
        instance.addSettingsListener(this);
        updateSettingState();
        loadDimens();
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        CrearePillView();
        this.mPositionTouchController = new PositionTouchController(context, this.mEdgeTouchPillView, this.mLayoutParams, this, this.mEdgeTouchSettingsManager);
        initAnimationListener();
    }

    public void updateKeyguardState(boolean z) {
        if (this.mIsKeyguard != z) {
            this.mIsKeyguard = z;
            updatePillViewState();
        }
    }

    public void updateOrientation(boolean z) {
        if (this.mIsPortrait != z) {
            this.mIsPortrait = z;
            updatePillViewState();
        }
    }

    public void updatePillViewState() {
        Log.i("GestureTouch", "updatePillViewState mPillGestureEnabled = " + this.mPillGestureEnabled + " mInTutorialMode = " + this.mInTutorialMode + " mIsPortrait = " + this.mIsPortrait + " mIsNavBarShow = " + this.mIsNavBarShow + " mIsAppAvailable = " + this.mIsAppAvailable + " mCommandQueue.panelEnable() " + this.mCommandQueue.panelsEnabled() + " mIsKeyguard = " + this.mIsKeyguard + " mIsNeedHidePill = " + this.mIsNeedHidePill + " mIsGlobalActionShow = " + this.mIsGlobalActionShow);
        if ((this.mPillGestureEnabled || this.mInTutorialMode) && this.mIsPortrait && this.mIsNavBarShow && this.mCommandQueue.panelsEnabled() && this.mIsAppAvailable && !this.mIsKeyguard && !this.mIsNeedHidePill && !this.mIsGlobalActionShow) {
            showPillView();
        } else {
            hidePillView();
        }
    }

    public void showPillView() {
        View view = this.mEdgeTouchPillView;
        if (view != null && view.getVisibility() != 0) {
            if (DEBUG) {
                Log.i("GestureTouch", "showPillView()");
            }
            if (this.mShowWhatNewAnimator) {
                showWhatNewAnimator();
            }
            this.mEdgeTouchPillView.setVisibility(0);
            setGripSuppression(true);
            this.mRegionSamplingHelper.start(this.mPillViewRect);
        }
    }

    public void hidePillView() {
        View view = this.mEdgeTouchPillView;
        if (view != null && view.getVisibility() != 8) {
            if (DEBUG) {
                Log.i("GestureTouch", "hidePillView()");
            }
            stopWhatNewAnimation();
            this.mEdgeTouchPillView.setVisibility(8);
            setGripSuppression(false);
            this.mRegionSamplingHelper.stop();
        }
    }

    private void setGripSuppression(boolean z) {
        MotoExtHwManager instance = MotoExtHwManager.getInstance(this.mContext);
        int i = isActionBarOnLeft() ? 1 : 2;
        int i2 = this.mPillYPosition;
        int i3 = this.mPillHeight + i2;
        if (DEBUG) {
            Log.i("GestureTouch", "setGripSuppression excludePill  " + z + " y1 = " + i2 + " y2 = " + i3 + " edge = " + i);
        }
        if (z) {
            int i4 = -1;
            int i5 = i == 1 ? i2 : -1;
            int i6 = i == 1 ? i3 : -1;
            int i7 = i == 2 ? i2 : -1;
            if (i == 2) {
                i4 = i3;
            }
            instance.disableGripSuppress(i, i5, i6, i7, i4);
        } else {
            instance.disableGripSuppress(0, -1, -1, -1, -1);
        }
        instance.setWaterfallDisplayHoldDistance((!this.mPillGestureEnabled || !this.mPillSwipeUpDownEnabled) ? 18.0f : 12.0f);
    }

    public void initAnimationListener() {
        this.mPillVibrationListener = new PillVibrationListener(this.mContext, this);
        this.mPillAnimationListener = new PillAnimationListener(this.mContext, this.mEdgeTouchPillView, this);
        GestureActionController instance = GestureActionController.getInstance(this.mContext);
        this.mPillActionController = instance;
        instance.setNotificaitonPanelView(this.mNotificationPanelViewController);
        this.mPillActionController.setGestureTouchController(this, false);
        this.mPillActionController.setSettingsManager(this.mEdgeTouchSettingsManager);
    }

    public void loadDimens() {
        Resources resources = this.mContext.getResources();
        this.mPillTouchableWidth = resources.getDimensionPixelSize(R$dimen.zz_moto_pill_touchable_area);
        this.mPillWidth = resources.getDimensionPixelSize(R$dimen.zz_moto_pill_width);
        this.mPillHeight = resources.getDimensionPixelSize(R$dimen.zz_moto_pill_height);
        this.mPillPadding = resources.getDimensionPixelSize(R$dimen.zz_moto_pill_padding);
    }

    public void onNavigationBarVisibilityChanged(boolean z) {
        if (DEBUG) {
            Log.i("GestureTouch", "onNavigationBarVisibilityChanged visibility = " + z);
        }
        if (this.mIsNavBarShow != z) {
            this.mIsNavBarShow = z;
            updatePillViewState();
        }
    }

    public void onGlobalActionsStateChanged(boolean z) {
        if (this.mIsGlobalActionShow != z) {
            this.mIsGlobalActionShow = z;
            updatePillViewState();
        }
    }

    private void CrearePillView() {
        this.mLayoutParams = crearePillViewLayoutParams();
        View inflate = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(R$layout.edge_touch_pill_view, (ViewGroup) null);
        this.mEdgeTouchPillView = inflate;
        WindowManager.LayoutParams layoutParams = this.mLayoutParams;
        layoutParams.gravity = 51;
        this.mWindowManager.addView(inflate, layoutParams);
        this.mEdgeTouchPillView.setVisibility(8);
        this.mClickAbleArea = this.mEdgeTouchPillView.findViewById(R$id.clickable_area);
        this.mPillView = (ImageView) this.mEdgeTouchPillView.findViewById(R$id.pill);
        updateClickableAreaLayout();
        RegionSamplingHelper regionSamplingHelper = new RegionSamplingHelper(this.mPillView, new RegionSamplingHelper.SamplingCallback() {
            public void onRegionDarknessChanged(boolean z) {
                if (EdgeTouchPillController.this.mIsLightTheme != z) {
                    if (EdgeTouchPillController.DEBUG) {
                        Log.i("GestureTouch", "isRegionDark = " + z);
                    }
                    boolean unused = EdgeTouchPillController.this.mIsLightTheme = z;
                    EdgeTouchPillController.this.updateTheme();
                }
            }

            public Rect getSampledRegion(View view) {
                return EdgeTouchPillController.this.mPillViewRect;
            }
        });
        this.mRegionSamplingHelper = regionSamplingHelper;
        regionSamplingHelper.setWindowVisible(true);
        updatePillViewState();
        this.mGestureDetectionController = new GestureDetectionController(this.mContext, this);
        this.mClickAbleArea.setOnTouchListener(this.mClickableAreaTouchListener);
    }

    private void updateDots(View view, int i) {
        View findViewById = view.findViewById(R$id.dots_right);
        View findViewById2 = view.findViewById(R$id.dots_left);
        if (isActionBarOnLeft()) {
            findViewById.setVisibility(8);
            findViewById2.setVisibility(0);
            findViewById2.setPadding(i, 0, 0, 0);
            this.mDotTop = (ImageView) findViewById2.findViewById(R$id.dot_top);
            this.mDotBottom = (ImageView) findViewById2.findViewById(R$id.dot_bottom);
            this.mDotTop.setImageDrawable(getPillDotsDrawable());
            this.mDotBottom.setImageDrawable(getPillDotsDrawable());
            return;
        }
        findViewById2.setVisibility(8);
        findViewById.setVisibility(0);
        findViewById.setPadding(0, 0, i, 0);
        this.mDotTop = (ImageView) findViewById.findViewById(R$id.dot_top);
        this.mDotBottom = (ImageView) findViewById.findViewById(R$id.dot_bottom);
        this.mDotTop.setImageDrawable(getPillDotsDrawable());
        this.mDotBottom.setImageDrawable(getPillDotsDrawable());
    }

    private Drawable getPillDrawable() {
        Drawable drawable;
        Resources resources = this.mContext.getResources();
        if (this.mIsLightTheme) {
            drawable = resources.getDrawable(R$drawable.edge_pill_resting_light);
        } else {
            drawable = resources.getDrawable(R$drawable.edge_pill_resting_dark);
        }
        drawable.mutate();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setSize(this.mPillWidth + (((int) resources.getDimension(R$dimen.pill_stroke_width)) * 2), this.mPillHeight);
        }
        return drawable;
    }

    private Drawable getPillDotsDrawable() {
        if (this.mIsLightTheme) {
            return this.mContext.getResources().getDrawable(R$drawable.edge_pill_dot_resting_light);
        }
        return this.mContext.getResources().getDrawable(R$drawable.edge_pill_dot_resting_dark);
    }

    /* access modifiers changed from: private */
    public void updateTheme() {
        this.mDotTop.setImageDrawable(getPillDotsDrawable());
        this.mDotBottom.setImageDrawable(getPillDotsDrawable());
        this.mPillView.setImageDrawable(getPillDrawable());
    }

    public boolean isActionBarOnLeft() {
        return this.mPillXPosition == 0;
    }

    public boolean isInTutorialMode() {
        return this.mInTutorialMode;
    }

    public boolean isLightTheme() {
        return this.mIsLightTheme;
    }

    public int getPillWidth() {
        return this.mPillWidth;
    }

    public int getPillHeight() {
        return this.mPillHeight;
    }

    public boolean isNeedShowWhatNew() {
        return this.mIsNeedShowWhatNew;
    }

    public boolean isPillSwipeUpDownEnabled() {
        return this.mPillSwipeUpDownEnabled || this.mInTutorialMode;
    }

    private WindowManager.LayoutParams crearePillViewLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(this.mPillTouchableWidth, this.mPillHeight, 2955, 520, -2);
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.privateFlags = layoutParams.privateFlags | 1048576 | 16;
        layoutParams.y = this.mPillYPosition;
        layoutParams.setTitle(isActionBarOnLeft() ? "SystemUI Left Pill Vview" : "SystemUI Right Pill View");
        return layoutParams;
    }

    public void updateSettingState() {
        this.mPillGestureEnabled = this.mEdgeTouchSettingsManager.isGestureEnabled();
        this.mPillSwipeUpDownEnabled = this.mEdgeTouchSettingsManager.isSwipeUpDownEnabled();
        this.mVibrationEnabled = this.mEdgeTouchSettingsManager.isVibrationEnabled();
        this.mInTutorialMode = this.mEdgeTouchSettingsManager.isInTutorialMode();
        this.mIsNeedHidePill = this.mEdgeTouchSettingsManager.isNeedHidePill();
        this.mIsNeedShowWhatNew = this.mEdgeTouchSettingsManager.isNeedShowWhatNew();
        this.mShowWhatNewAnimator = this.mEdgeTouchSettingsManager.isNeedShowWhatNewAnimator();
        this.mIsInPositionMode = this.mEdgeTouchSettingsManager.isPositionMode();
        this.mPillXPosition = this.mEdgeTouchSettingsManager.getPillXPosition();
        this.mPillYPosition = this.mEdgeTouchSettingsManager.getPillYPosition();
        this.mIsAppAvailable = this.mEdgeTouchSettingsManager.isAppAvaliable();
        if (DEBUG) {
            Log.i("GestureTouch", "mPillGestureEnabled = " + this.mPillGestureEnabled + " mPillSwipeUpDownEnabled = " + this.mPillSwipeUpDownEnabled + " mVibrationEnabled = " + this.mVibrationEnabled + " mInTutorialMode = " + this.mInTutorialMode + " mIsNeedHidePill = " + this.mIsNeedHidePill + " mIsNeedShowWhatNew = " + this.mIsNeedShowWhatNew + " mShowWhatNewAnimator = " + this.mShowWhatNewAnimator + " mIsInPositionMode = " + this.mIsInPositionMode + " mPillXPosition = " + this.mPillXPosition + " mPillYPosition = " + this.mPillYPosition + " mIsAppAvailable = " + this.mIsAppAvailable);
        }
    }

    public void updateGestureTouchEnabled(boolean z) {
        this.mPillGestureEnabled = z;
        updatePillViewState();
    }

    public void updateSwipeEnabled(boolean z) {
        this.mPillSwipeUpDownEnabled = z;
    }

    public void updateVibrationEnabled(boolean z) {
        this.mVibrationEnabled = z;
    }

    public void updateTutorialMode(boolean z) {
        this.mInTutorialMode = z;
        updatePillViewState();
        this.mPillActionController.updateTutorialMode(this.mInTutorialMode);
    }

    public void updateShortcutState(boolean z) {
        this.mIsNeedHidePill = z;
        updatePillViewState();
    }

    public void updateWhatNewState(boolean z, boolean z2) {
        this.mIsNeedShowWhatNew = z;
        this.mShowWhatNewAnimator = z2;
        this.mGestureDetectionController.registerSignalTapDetected(z);
        updatePillViewState();
    }

    public void updatePositionModeState(boolean z) {
        this.mIsInPositionMode = z;
        if (DEBUG) {
            Log.i("GestureTouch", "updatePositionModeState mIsInPositionMode = " + this.mIsInPositionMode);
        }
        if (this.mIsInPositionMode) {
            enterPositionMode();
        } else {
            quitPositionMode();
        }
        this.mPositionTouchController.updatePositionMode(this.mIsInPositionMode);
    }

    public void updatePositionX(int i) {
        this.mPillXPosition = i;
        updateClickableAreaLayout();
        setGripSuppression(true);
    }

    public void updatePositionY(int i) {
        this.mPillYPosition = i;
        updateClickableAreaLayout();
        setGripSuppression(true);
        this.mPillAnimationListener.updateSwipeAnimationParams();
    }

    public void updateAppState(boolean z) {
        this.mIsAppAvailable = z;
        updatePillViewState();
    }

    public void onUserSwitch(Context context) {
        if (DEBUG) {
            Log.i("GestureTouch", "onUserSwitch");
        }
        this.mContext = context;
        updateSettingState();
        updateClickableAreaLayout();
        this.mPillVibrationListener.onUserSwitch(this.mContext);
        this.mPillActionController.onUserSwitch(context);
        this.mPillAnimationListener.onUserSwitch(context);
        this.mPositionTouchController.onUserSwitch(context);
        this.mPillAnimationListener.updateSwipeAnimationParams();
        updatePillViewState();
    }

    private void enterPositionMode() {
        if (DEBUG) {
            Log.i("GestureTouch", "enterPositionMode()");
        }
        updateSystemBackGestureRect();
        addDragArea();
        stopWhatNewAnimation();
    }

    public void dateChanged() {
        this.mIsShownToday = false;
    }

    private void quitPositionMode() {
        if (DEBUG) {
            Log.i("GestureTouch", "quitPositionMode()");
        }
        removeDragArea();
        updateSystemBackGestureRect();
        this.mPillXPosition = this.mEdgeTouchSettingsManager.getPillXPosition();
        this.mPillYPosition = this.mEdgeTouchSettingsManager.getPillYPosition();
    }

    private void updateSystemBackGestureRect() {
        List list = Collections.EMPTY_LIST;
        if (this.mIsInPositionMode) {
            Rect rect = new Rect();
            this.mClickAbleArea.getLocalVisibleRect(rect);
            list = Collections.singletonList(rect);
        }
        this.mClickAbleArea.setSystemGestureExclusionRects(list);
    }

    private void addDragArea() {
        if (DEBUG) {
            Log.i("GestureTouch", "addDragArea");
        }
        this.mDragAreaLayoutParams = creareDragAreaLayoutParams();
        LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        this.mLeftDragAreaView = layoutInflater.inflate(R$layout.zz_moto_left_drag_area_view, (ViewGroup) null);
        this.mRightDragAreaView = layoutInflater.inflate(R$layout.zz_moto_right_drag_area_view, (ViewGroup) null);
        WindowManager.LayoutParams layoutParams = this.mDragAreaLayoutParams;
        layoutParams.gravity = 51;
        layoutParams.setTitle("SystemUI Left Drag Vview");
        this.mWindowManager.addView(this.mLeftDragAreaView, this.mDragAreaLayoutParams);
        WindowManager.LayoutParams layoutParams2 = this.mDragAreaLayoutParams;
        layoutParams2.gravity = 53;
        layoutParams2.setTitle("SystemUI Right Drag View");
        this.mWindowManager.addView(this.mRightDragAreaView, this.mDragAreaLayoutParams);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.pill_padding_vertical);
        int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(R$dimen.zz_moto_pill_drag_vertical_margin) - (dimensionPixelSize * 2);
        int i = getDisplayBounds().y - (dimensionPixelSize2 * 2);
        int dimensionPixelSize3 = dimensionPixelSize2 - this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_height);
        View findViewById = this.mLeftDragAreaView.findViewById(R$id.left_area);
        this.mLeftDragView = findViewById;
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
        layoutParams3.height = i;
        layoutParams3.width = this.mPillTouchableWidth;
        layoutParams3.setMargins(0, dimensionPixelSize3, 0, 0);
        this.mLeftDragView.setLayoutParams(layoutParams3);
        this.mLeftDragView.setPadding(0, dimensionPixelSize, 0, dimensionPixelSize);
        View findViewById2 = this.mRightDragAreaView.findViewById(R$id.right_area);
        this.mRightDragView = findViewById2;
        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) findViewById2.getLayoutParams();
        layoutParams4.height = i;
        layoutParams4.width = this.mPillTouchableWidth;
        layoutParams4.setMargins(0, dimensionPixelSize3, 0, 0);
        this.mRightDragView.setLayoutParams(layoutParams4);
        this.mRightDragView.setPadding(0, dimensionPixelSize, 0, dimensionPixelSize);
        this.mPositionTouchController.updateDragView(this.mLeftDragView);
    }

    public Point getDisplayBounds() {
        Display defaultDisplay = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        return point;
    }

    private WindowManager.LayoutParams creareDragAreaLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(this.mPillTouchableWidth, -1, 2038, 520, -2);
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.privateFlags |= 16;
        return layoutParams;
    }

    private void removeDragArea() {
        if (this.mLeftDragAreaView != null) {
            if (DEBUG) {
                Log.i("GestureTouch", "removeDragArea mLeftDragAreaView");
            }
            this.mWindowManager.removeView(this.mLeftDragAreaView);
            this.mLeftDragAreaView = null;
        }
        if (this.mRightDragAreaView != null) {
            if (DEBUG) {
                Log.i("GestureTouch", "removeDragArea mRightDragAreaView");
            }
            this.mWindowManager.removeView(this.mRightDragAreaView);
            this.mRightDragAreaView = null;
        }
    }

    public void updateClickableAreaLayout() {
        int i;
        if (DEBUG) {
            Log.i("GestureTouch", "updateClickableAreaLayout  " + isActionBarOnLeft());
        }
        this.mPillView.setImageDrawable(getPillDrawable());
        ViewGroup.LayoutParams layoutParams = this.mClickAbleArea.getLayoutParams();
        layoutParams.width = this.mPillTouchableWidth;
        layoutParams.height = this.mPillHeight;
        int i2 = this.mPillPadding;
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.pill_padding_vertical);
        updateDots(this.mClickAbleArea, (((this.mPillWidth / 2) + i2) + ((int) this.mContext.getResources().getDimension(R$dimen.pill_stroke_width))) - ((int) this.mContext.getResources().getDimension(R$dimen.zz_moto_pill_dots_size)));
        if (isActionBarOnLeft()) {
            this.mPillView.setScaleType(ImageView.ScaleType.FIT_START);
            this.mPillView.setPadding(i2, dimensionPixelSize, 0, dimensionPixelSize);
        } else {
            this.mPillView.setScaleType(ImageView.ScaleType.FIT_END);
            this.mPillView.setPadding(0, dimensionPixelSize, i2, dimensionPixelSize);
        }
        this.mClickAbleArea.setLayoutParams(layoutParams);
        if (this.mContext.getResources().getConfiguration().orientation == 2) {
            i = getDisplayBounds().y;
        } else {
            i = getDisplayBounds().x;
        }
        this.mLayoutParams.x = isActionBarOnLeft() ? 0 : i - this.mLayoutParams.width;
        WindowManager.LayoutParams layoutParams2 = this.mLayoutParams;
        layoutParams2.y = this.mPillYPosition;
        this.mWindowManager.updateViewLayout(this.mEdgeTouchPillView, layoutParams2);
        int[] iArr = new int[2];
        this.mClickAbleArea.getLocationOnScreen(iArr);
        this.mPillViewRect.set(iArr[0], iArr[1], iArr[0] + this.mPillTouchableWidth, iArr[1] + this.mPillHeight);
    }

    public void handleGestureAction(int i) {
        Log.i("GestureTouch", "handleGestureAction gestureType = " + i);
        stopWhatNewAnimation();
        if (this.mVibrationEnabled) {
            this.mPillVibrationListener.excuteAction(i);
        }
        this.mPillAnimationListener.excuteAction(i);
        this.mPillActionController.excuteAction(i);
    }

    public void showWhatNewAnimator() {
        if (isWhatNewAnimatTimeZone() && !this.mIsShownToday) {
            if (DEBUG) {
                Log.i("GestureTouch", "showWhatNewAnimator()");
            }
            startWhatNewAnimation();
        }
    }

    private boolean isWhatNewAnimatTimeZone() {
        int i = Calendar.getInstance().get(11);
        return isInTimeZone(10, 12, i) || isInTimeZone(15, 17, i) || isInTimeZone(20, 22, i);
    }

    private void startWhatNewAnimation() {
        if (this.mWhatNewAnimator == null) {
            ValueAnimator whatNewAnimator = getWhatNewAnimator();
            this.mWhatNewAnimator = whatNewAnimator;
            whatNewAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    EdgeTouchPillController edgeTouchPillController = EdgeTouchPillController.this;
                    boolean unused = edgeTouchPillController.mIsReversed = !edgeTouchPillController.mIsReversed;
                }

                public void onAnimationEnd(Animator animator) {
                    EdgeTouchPillController.this.mHandler.post(new EdgeTouchPillController$3$$ExternalSyntheticLambda0(this));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onAnimationEnd$0() {
                    if (EdgeTouchPillController.this.mWhatNewAnimator != null) {
                        if (EdgeTouchPillController.this.mIsReversed) {
                            EdgeTouchPillController.this.mWhatNewAnimator.reverse();
                        } else {
                            EdgeTouchPillController.this.mWhatNewAnimator.start();
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    ValueAnimator unused = EdgeTouchPillController.this.mWhatNewAnimator = null;
                    boolean unused2 = EdgeTouchPillController.this.mIsReversed = false;
                    EdgeTouchPillController.this.mClickAbleArea.setScaleY(1.0f);
                    EdgeTouchPillController.this.mClickAbleArea.setScaleX(1.0f);
                }
            });
            this.mWhatNewAnimator.start();
            this.mHandler.postDelayed(this.mStopWhatNewAnimatorRunnable, 60000);
        } else if (DEBUG) {
            Log.i("GestureTouch", "mWhatNewAnimator is running, return");
        }
    }

    public void stopWhatNewAnimation() {
        if (this.mWhatNewAnimator != null) {
            if (DEBUG) {
                Log.i("GestureTouch", "stopWhatNewAnimation()");
            }
            if (this.mHandler.hasCallbacks(this.mStopWhatNewAnimatorRunnable)) {
                this.mHandler.removeCallbacks(this.mStopWhatNewAnimatorRunnable);
            }
            this.mWhatNewAnimator.cancel();
        }
    }

    public ValueAnimator getWhatNewAnimator() {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 1.2f});
        ofFloat.setDuration(750);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float f = (Float) valueAnimator.getAnimatedValue();
                EdgeTouchPillController.this.mClickAbleArea.setScaleX(f.floatValue());
                EdgeTouchPillController.this.mClickAbleArea.setScaleY(f.floatValue());
            }
        });
        return ofFloat;
    }
}
