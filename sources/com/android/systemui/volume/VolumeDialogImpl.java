package com.android.systemui.volume;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.media.AudioSystem;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.android.internal.graphics.drawable.BackgroundBlurDrawable;
import com.android.settingslib.Utils;
import com.android.settingslib.volume.Util;
import com.android.systemui.CliToast;
import com.android.systemui.Dependency;
import com.android.systemui.Prefs;
import com.android.systemui.R$bool;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.R$style;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.media.dialog.MediaOutputDialogFactory;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.VolumeDialog;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.util.AlphaTintDrawableWrapper;
import com.android.systemui.util.RoundedCornerProgressDrawable;
import com.motorola.multivolume.AppVolumeState;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VolumeDialogImpl implements VolumeDialog, ConfigurationController.ConfigurationListener, ViewTreeObserver.OnComputeInternalInsetsListener {
    public static boolean DEBUG_MULTISOUND = Log.isLoggable("volume_multisound", 3);
    /* access modifiers changed from: private */
    public static final String TAG = Util.logTag(VolumeDialogImpl.class);
    private int APP_ICON_PADDING = 10;
    Comparator<AppVolumeRow> comparator_ltr = new Comparator<AppVolumeRow>() {
        public int compare(AppVolumeRow appVolumeRow, AppVolumeRow appVolumeRow2) {
            AppVolumeState appVolumeState = appVolumeRow.appVolumeState;
            boolean z = appVolumeState.foreground;
            if (z && !appVolumeRow2.appVolumeState.foreground) {
                return 1;
            }
            if (!z && appVolumeRow2.appVolumeState.foreground) {
                return -1;
            }
            boolean z2 = appVolumeState.playing;
            if (z2 && !appVolumeRow2.appVolumeState.playing) {
                return 1;
            }
            if (!z2 && appVolumeRow2.appVolumeState.playing) {
                return -1;
            }
            long j = appVolumeState.timeInMills;
            long j2 = appVolumeRow2.appVolumeState.timeInMills;
            if (j == j2) {
                return 0;
            }
            if (j > j2) {
                return 1;
            }
            return -1;
        }
    };
    Comparator<AppVolumeRow> comparator_rtl = new Comparator<AppVolumeRow>() {
        public int compare(AppVolumeRow appVolumeRow, AppVolumeRow appVolumeRow2) {
            AppVolumeState appVolumeState = appVolumeRow.appVolumeState;
            boolean z = appVolumeState.foreground;
            if (z && !appVolumeRow2.appVolumeState.foreground) {
                return -1;
            }
            if (!z && appVolumeRow2.appVolumeState.foreground) {
                return 1;
            }
            boolean z2 = appVolumeState.playing;
            if (z2 && !appVolumeRow2.appVolumeState.playing) {
                return -1;
            }
            if (!z2 && appVolumeRow2.appVolumeState.playing) {
                return 1;
            }
            long j = appVolumeState.timeInMills;
            long j2 = appVolumeRow2.appVolumeState.timeInMills;
            if (j == j2) {
                return 0;
            }
            if (j > j2) {
                return -1;
            }
            return 1;
        }
    };
    private final Accessibility mAccessibility = new Accessibility();
    private final AccessibilityManagerWrapper mAccessibilityMgr;
    private int mActiveStream;
    private final ActivityManager mActivityManager;
    private final ValueAnimator mAnimateUpBackgroundToMatchDrawer = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
    private ImageButton mAppVolumeIcon;
    /* access modifiers changed from: private */
    public List<AppVolumeRow> mAppVolumeRows = new ArrayList();
    private boolean mAutomute = true;
    private final boolean mChangeVolumeRowTintWhenInactive;
    /* access modifiers changed from: private */
    public boolean mConfigChanged = false;
    private ConfigurableTexts mConfigurableTexts;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final VolumeDialogController mController;
    private final VolumeDialogController.Callbacks mControllerCallbackH = new VolumeDialogController.Callbacks() {
        public void onShowRequested(int i) {
            VolumeDialogImpl.this.showH(i);
        }

        public void onDismissRequested(int i) {
            VolumeDialogImpl.this.dismissH(i);
        }

        public void onScreenOff() {
            VolumeDialogImpl.this.dismissH(4);
        }

        public void onStateChanged(VolumeDialogController.State state) {
            VolumeDialogImpl.this.onStateChangedH(state);
        }

        public void onLayoutDirectionChanged(int i) {
            VolumeDialogImpl.this.mDialogView.setLayoutDirection(i);
        }

        public void onConfigurationChanged() {
            VolumeDialogImpl.this.mDialog.dismiss();
            boolean unused = VolumeDialogImpl.this.mConfigChanged = true;
        }

        public void onShowVibrateHint() {
            if (VolumeDialogImpl.this.mSilentMode) {
                VolumeDialogImpl.this.mController.setRingerMode(0, false);
            }
        }

        public void onShowSilentHint() {
            if (VolumeDialogImpl.this.mSilentMode) {
                VolumeDialogImpl.this.mController.setRingerMode(2, false);
            }
        }

        public void onShowSafetyWarning(int i) {
            VolumeDialogImpl.this.showSafetyWarningH(i);
        }

        public void onAccessibilityModeChanged(Boolean bool) {
            boolean unused = VolumeDialogImpl.this.mShowA11yStream = bool == null ? false : bool.booleanValue();
            VolumeRow access$3300 = VolumeDialogImpl.this.getActiveRow();
            if (VolumeDialogImpl.this.mShowA11yStream || 10 != access$3300.stream) {
                VolumeDialogImpl.this.updateRowsH(access$3300);
            } else {
                VolumeDialogImpl.this.dismissH(7);
            }
        }

        public void onIncreaseVolume(int i) {
            AppVolumeRow access$3500;
            if (i == 3 && (access$3500 = VolumeDialogImpl.this.getOnlyOneVisibleAndNotSameWithMusicRow()) != null && !VolumeDialogImpl.this.mController.isAppAutoMute(access$3500.uid)) {
                int i2 = (access$3500.appVolumeState.appLevel + 1) * 100;
                if (C2129D.BUG) {
                    Log.d(VolumeDialogImpl.TAG + ".dv", "onIncreaseVolume cause to increase volume, packages: " + access$3500.packageName + ", newProgress: " + i2 + ", appLevel: " + access$3500.appVolumeState.appLevel + ", lastSetLevel: " + access$3500.appVolumeState.lastSetLevel + ", requestedLevel: " + access$3500.requestedLevel);
                }
                VolumeDialogImpl.this.mController.changeAppRow(access$3500.packageName, access$3500.uid, i2, -1.0d, TouchType.START_TOUCH.ordinal());
            }
        }

        public void onMusicRowChanged(int i, double d) {
            VolumeRow access$3700 = VolumeDialogImpl.this.findRow(3);
            access$3700.slider.setProgress(i);
            access$3700.userAttempt = SystemClock.uptimeMillis();
            if (C2129D.BUG) {
                Log.d(VolumeDialogImpl.TAG + ".dv", "onMusicRowChanged, " + i + " percentage=" + d + " userAttempt= " + access$3700.userAttempt);
            }
        }

        public void onAppRowChanged(String str, int i, int i2, double d) {
            AppVolumeRow access$3800 = VolumeDialogImpl.this.findAppRow(i);
            if (access$3800 != null) {
                access$3800.slider.setProgress(i2);
                access$3800.userAttempt = SystemClock.uptimeMillis();
                if (C2129D.BUG) {
                    Log.d(VolumeDialogImpl.TAG + ".dv", "onAppRowChanged, package= " + str + " uid= " + i + " progress= " + i2 + " percentage=" + d + " userAttempt= " + access$3800.userAttempt);
                }
            }
        }

        public void onAppRowsChanged(List<AppVolumeState> list) {
            for (AppVolumeState next : list) {
                AppVolumeRow access$3800 = VolumeDialogImpl.this.findAppRow(next.packageUid);
                if (access$3800 != null) {
                    access$3800.slider.setProgress(next.progress);
                    access$3800.userAttempt = SystemClock.uptimeMillis();
                    if (C2129D.BUG) {
                        Log.d(VolumeDialogImpl.TAG + ".dv", "onAppRowsChanged, package= " + access$3800.packageName + " uid= " + access$3800.uid + " progress= " + next.progress + " percentage=" + next.percentage + " userAttempt=" + access$3800.userAttempt);
                    }
                }
            }
        }

        public void onMultiVolumeRowsChanged(int i, double d, List<AppVolumeState> list) {
            onMusicRowChanged(i, d);
            onAppRowsChanged(list);
        }

        public void onMultiVolumeStateChanged(VolumeDialogController.State state) {
            if (C2129D.BUG) {
                Log.d(VolumeDialogImpl.TAG + ".dv", "onMultiVolumeStateChanged() appVolumeStates: " + state.dumpMultiVolumeString(0));
            }
            VolumeDialogController.State unused = VolumeDialogImpl.this.mState = state;
            boolean access$4000 = VolumeDialogImpl.this.updateAppRowsH();
            VolumeDialogImpl.this.updateAppVolumeIconH();
            if (access$4000) {
                VolumeDialogImpl volumeDialogImpl = VolumeDialogImpl.this;
                volumeDialogImpl.updateRowsH(volumeDialogImpl.getActiveRow());
                if (VolumeDialogImpl.this.mShowing) {
                    VolumeDialogImpl.this.rescheduleTimeoutH();
                }
            }
            for (AppVolumeRow access$4400 : VolumeDialogImpl.this.mAppVolumeRows) {
                VolumeDialogImpl.this.updateAppVolumeRowH(access$4400);
            }
            if (C2129D.BUG) {
                VolumeDialogImpl volumeDialogImpl2 = VolumeDialogImpl.this;
                volumeDialogImpl2.dumpAppVolumeRows(volumeDialogImpl2.mAppVolumeRows);
            }
        }

        public void onCaptionComponentStateChanged(Boolean bool, Boolean bool2) {
            VolumeDialogImpl.this.updateODICaptionsH(bool.booleanValue(), bool2.booleanValue());
        }
    };
    /* access modifiers changed from: private */
    public Consumer<Boolean> mCrossWindowBlurEnabledListener;
    private final DeviceProvisionedController mDeviceProvisionedController;
    /* access modifiers changed from: private */
    public CustomDialog mDialog;
    /* access modifiers changed from: private */
    public int mDialogCornerRadius;
    private final int mDialogHideAnimationDurationMs;
    /* access modifiers changed from: private */
    public ViewGroup mDialogMain;
    /* access modifiers changed from: private */
    public ViewGroup mDialogRowsView;
    /* access modifiers changed from: private */
    public BackgroundBlurDrawable mDialogRowsViewBackground;
    private ViewGroup mDialogRowsViewContainer;
    private final int mDialogShowAnimationDurationMs;
    /* access modifiers changed from: private */
    public ViewGroup mDialogView;
    private int mDialogWidth;
    private final SparseBooleanArray mDynamic = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public final C2173H mHandler = new C2173H();
    private boolean mHasSeenODICaptionsTooltip;
    private boolean mHovering = false;
    private boolean mIsAnimatingDismiss = false;
    /* access modifiers changed from: private */
    public boolean mIsRingerDrawerOpen = false;
    private final KeyguardManager mKeyguard;
    private CaptionsToggleImageButton mODICaptionsIcon;
    private View mODICaptionsTooltipView = null;
    private ViewStub mODICaptionsTooltipViewStub;
    private ViewGroup mODICaptionsView;
    private int mPrevActiveStream;
    /* access modifiers changed from: private */
    public ViewGroup mRinger;
    private View mRingerAndDrawerContainer;
    private Drawable mRingerAndDrawerContainerBackground;
    private int mRingerCount;
    private float mRingerDrawerClosedAmount = 1.0f;
    private ViewGroup mRingerDrawerContainer;
    /* access modifiers changed from: private */
    public ImageView mRingerDrawerIconAnimatingDeselected;
    /* access modifiers changed from: private */
    public ImageView mRingerDrawerIconAnimatingSelected;
    /* access modifiers changed from: private */
    public final ValueAnimator mRingerDrawerIconColorAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
    private int mRingerDrawerItemSize;
    private ViewGroup mRingerDrawerMute;
    private ImageView mRingerDrawerMuteIcon;
    /* access modifiers changed from: private */
    public ViewGroup mRingerDrawerNewSelectionBg;
    private ViewGroup mRingerDrawerNormal;
    private ImageView mRingerDrawerNormalIcon;
    private ViewGroup mRingerDrawerVibrate;
    private ImageView mRingerDrawerVibrateIcon;
    private ImageButton mRingerIcon;
    private int mRingerRowsPadding;
    /* access modifiers changed from: private */
    public final List<VolumeRow> mRows = new ArrayList();
    /* access modifiers changed from: private */
    public SafetyWarningDialog mSafetyWarning;
    /* access modifiers changed from: private */
    public final Object mSafetyWarningLock = new Object();
    /* access modifiers changed from: private */
    public ViewGroup mSelectedRingerContainer;
    private ImageView mSelectedRingerIcon;
    private ImageButton mSettingsIcon;
    private View mSettingsView;
    /* access modifiers changed from: private */
    public boolean mShowA11yStream;
    private boolean mShowActiveStreamOnly;
    private final boolean mShowLowMediaVolumeIcon;
    private boolean mShowVibrate;
    /* access modifiers changed from: private */
    public boolean mShowing;
    /* access modifiers changed from: private */
    public boolean mSilentMode = true;
    /* access modifiers changed from: private */
    public VolumeDialogController.State mState;
    private View mTopContainer;
    private final Region mTouchableRegion = new Region();
    private final boolean mUseBackgroundBlur;
    /* access modifiers changed from: private */
    public Window mWindow;
    private FrameLayout mZenIcon;

    enum TouchType {
        START_TOUCH,
        ON_TOUCH,
        END_TOUCH,
        NO_TOUCH
    }

    public VolumeDialogImpl(Context context) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, R$style.volume_dialog_theme);
        this.mContext = contextThemeWrapper;
        this.mController = (VolumeDialogController) Dependency.get(VolumeDialogController.class);
        this.mKeyguard = (KeyguardManager) contextThemeWrapper.getSystemService("keyguard");
        this.mActivityManager = (ActivityManager) contextThemeWrapper.getSystemService("activity");
        this.mAccessibilityMgr = (AccessibilityManagerWrapper) Dependency.get(AccessibilityManagerWrapper.class);
        this.mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
        this.mShowActiveStreamOnly = showActiveStreamOnly();
        this.mHasSeenODICaptionsTooltip = Prefs.getBoolean(context, "HasSeenODICaptionsTooltip", false);
        this.mShowLowMediaVolumeIcon = contextThemeWrapper.getResources().getBoolean(R$bool.config_showLowMediaVolumeIcon);
        this.mChangeVolumeRowTintWhenInactive = contextThemeWrapper.getResources().getBoolean(R$bool.config_changeVolumeRowTintWhenInactive);
        this.mDialogShowAnimationDurationMs = contextThemeWrapper.getResources().getInteger(R$integer.config_dialogShowAnimationDurationMs);
        this.mDialogHideAnimationDurationMs = contextThemeWrapper.getResources().getInteger(R$integer.config_dialogHideAnimationDurationMs);
        boolean z = contextThemeWrapper.getResources().getBoolean(R$bool.config_volumeDialogUseBackgroundBlur);
        this.mUseBackgroundBlur = z;
        if (z) {
            this.mCrossWindowBlurEnabledListener = new VolumeDialogImpl$$ExternalSyntheticLambda26(this, contextThemeWrapper.getColor(R$color.volume_dialog_background_color_above_blur), contextThemeWrapper.getColor(R$color.volume_dialog_background_color));
        }
        initDimens();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, Boolean bool) {
        BackgroundBlurDrawable backgroundBlurDrawable = this.mDialogRowsViewBackground;
        if (!bool.booleanValue()) {
            i = i2;
        }
        backgroundBlurDrawable.setColor(i);
        this.mDialogRowsView.invalidate();
    }

    public void onUiModeChanged() {
        this.mContext.getTheme().applyStyle(this.mContext.getThemeResId(), true);
    }

    public void init(int i, VolumeDialog.Callback callback) {
        initDialog();
        this.mAccessibility.init();
        this.mController.addCallback(this.mControllerCallbackH, this.mHandler);
        this.mController.getState();
        if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume()) {
            this.mController.setUIHandlerCallbacks(this.mControllerCallbackH, this.mHandler);
        }
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
    }

    public void destroy() {
        this.mController.removeCallback(this.mControllerCallbackH);
        if (this.mDialog != null) {
            if (C2129D.BUG) {
                String str = TAG;
                Log.d(str, "destroy dialog: showing status= " + this.mDialog.isShowing());
            }
            if (this.mDialog.isShowing()) {
                this.mDialog.dismiss();
            }
        }
        this.mHandler.removeCallbacksAndMessages((Object) null);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
    }

    public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
        internalInsetsInfo.setTouchableInsets(3);
        this.mTouchableRegion.setEmpty();
        for (int i = 0; i < this.mDialogView.getChildCount(); i++) {
            unionViewBoundstoTouchableRegion(this.mDialogView.getChildAt(i));
        }
        View view = this.mODICaptionsTooltipView;
        if (view != null && view.getVisibility() == 0) {
            unionViewBoundstoTouchableRegion(this.mODICaptionsTooltipView);
        }
        internalInsetsInfo.touchableRegion.set(this.mTouchableRegion);
    }

    private Boolean isVolumeRowsExpanded() {
        int i = 0;
        for (int i2 = 0; i2 < this.mDialogRowsView.getChildCount(); i2++) {
            if (this.mDialogRowsView.getChildAt(i2).getVisibility() == 0) {
                i++;
            }
            if (i > 1) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private void unionViewBoundstoTouchableRegion(View view) {
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        float f = (float) iArr[0];
        float f2 = (float) iArr[1];
        if (view == this.mTopContainer && !this.mIsRingerDrawerOpen) {
            if (!isLandscape()) {
                f2 += (float) getRingerDrawerOpenExtraSize();
            } else if (!isVolumeRowsExpanded().booleanValue()) {
                f += (float) getRingerDrawerOpenExtraSize();
            }
        }
        this.mTouchableRegion.op((int) f, (int) f2, iArr[0] + view.getWidth(), iArr[1] + view.getHeight(), Region.Op.UNION);
    }

    private void initDialog() {
        this.mDialog = new CustomDialog(this.mContext);
        initDimens();
        this.mConfigurableTexts = new ConfigurableTexts(this.mContext);
        this.mHovering = false;
        this.mShowing = false;
        if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume()) {
            resetAppVolumeRows(this.mAppVolumeRows);
        }
        Window window = this.mDialog.getWindow();
        this.mWindow = window;
        window.requestFeature(1);
        this.mWindow.setBackgroundDrawable(new ColorDrawable(0));
        this.mWindow.clearFlags(65538);
        this.mWindow.addFlags(17563688);
        this.mWindow.addPrivateFlags(536870912);
        this.mWindow.setType(2020);
        this.mWindow.setWindowAnimations(16973828);
        WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
        attributes.format = -3;
        attributes.setTitle(VolumeDialogImpl.class.getSimpleName());
        attributes.windowAnimations = -1;
        attributes.gravity = this.mContext.getResources().getInteger(R$integer.volume_dialog_gravity);
        this.mWindow.setAttributes(attributes);
        this.mWindow.setLayout(-2, -2);
        this.mDialog.setContentView(R$layout.volume_dialog);
        ViewGroup viewGroup = (ViewGroup) this.mDialog.findViewById(R$id.volume_dialog);
        this.mDialogView = viewGroup;
        viewGroup.setAlpha(0.0f);
        this.mDialog.setCanceledOnTouchOutside(true);
        this.mDialog.setOnShowListener(new VolumeDialogImpl$$ExternalSyntheticLambda3(this));
        this.mDialog.setOnDismissListener(new VolumeDialogImpl$$ExternalSyntheticLambda2(this));
        this.mDialogView.setOnHoverListener(new VolumeDialogImpl$$ExternalSyntheticLambda11(this));
        this.mDialogRowsView = (ViewGroup) this.mDialog.findViewById(R$id.volume_dialog_rows);
        if (this.mUseBackgroundBlur) {
            this.mDialogView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                public void onViewAttachedToWindow(View view) {
                    VolumeDialogImpl.this.mWindow.getWindowManager().addCrossWindowBlurEnabledListener(VolumeDialogImpl.this.mCrossWindowBlurEnabledListener);
                    BackgroundBlurDrawable unused = VolumeDialogImpl.this.mDialogRowsViewBackground = view.getViewRootImpl().createBackgroundBlurDrawable();
                    Resources resources = VolumeDialogImpl.this.mContext.getResources();
                    VolumeDialogImpl.this.mDialogRowsViewBackground.setCornerRadius((float) VolumeDialogImpl.this.mContext.getResources().getDimensionPixelSize(Utils.getThemeAttr(VolumeDialogImpl.this.mContext, 16844145)));
                    VolumeDialogImpl.this.mDialogRowsViewBackground.setBlurRadius(resources.getDimensionPixelSize(R$dimen.volume_dialog_background_blur_radius));
                    VolumeDialogImpl.this.mDialogRowsView.setBackground(VolumeDialogImpl.this.mDialogRowsViewBackground);
                }

                public void onViewDetachedFromWindow(View view) {
                    VolumeDialogImpl.this.mWindow.getWindowManager().removeCrossWindowBlurEnabledListener(VolumeDialogImpl.this.mCrossWindowBlurEnabledListener);
                }
            });
        }
        this.mDialogRowsViewContainer = (ViewGroup) this.mDialogView.findViewById(R$id.volume_dialog_rows_container);
        this.mTopContainer = this.mDialogView.findViewById(R$id.volume_dialog_top_container);
        View findViewById = this.mDialogView.findViewById(R$id.volume_ringer_and_drawer_container);
        this.mRingerAndDrawerContainer = findViewById;
        if (findViewById != null) {
            if (isLandscape()) {
                View view = this.mRingerAndDrawerContainer;
                view.setPadding(view.getPaddingLeft(), this.mRingerAndDrawerContainer.getPaddingTop(), this.mRingerAndDrawerContainer.getPaddingRight(), this.mRingerRowsPadding);
                this.mRingerAndDrawerContainer.setBackgroundDrawable(this.mContext.getDrawable(R$drawable.volume_background_top_rounded));
            }
            this.mRingerAndDrawerContainer.post(new VolumeDialogImpl$$ExternalSyntheticLambda15(this));
        }
        ViewGroup viewGroup2 = (ViewGroup) this.mDialog.findViewById(R$id.ringer);
        this.mRinger = viewGroup2;
        if (viewGroup2 != null) {
            this.mRingerIcon = (ImageButton) viewGroup2.findViewById(R$id.ringer_icon);
            this.mZenIcon = (FrameLayout) this.mRinger.findViewById(R$id.dnd_icon);
        }
        this.mSelectedRingerIcon = (ImageView) this.mDialog.findViewById(R$id.volume_new_ringer_active_icon);
        this.mSelectedRingerContainer = (ViewGroup) this.mDialog.findViewById(R$id.volume_new_ringer_active_icon_container);
        this.mRingerDrawerMute = (ViewGroup) this.mDialog.findViewById(R$id.volume_drawer_mute);
        this.mRingerDrawerNormal = (ViewGroup) this.mDialog.findViewById(R$id.volume_drawer_normal);
        this.mRingerDrawerVibrate = (ViewGroup) this.mDialog.findViewById(R$id.volume_drawer_vibrate);
        this.mRingerDrawerMuteIcon = (ImageView) this.mDialog.findViewById(R$id.volume_drawer_mute_icon);
        this.mRingerDrawerVibrateIcon = (ImageView) this.mDialog.findViewById(R$id.volume_drawer_vibrate_icon);
        this.mRingerDrawerNormalIcon = (ImageView) this.mDialog.findViewById(R$id.volume_drawer_normal_icon);
        this.mRingerDrawerNewSelectionBg = (ViewGroup) this.mDialog.findViewById(R$id.volume_drawer_selection_background);
        setupRingerDrawer();
        ViewGroup viewGroup3 = (ViewGroup) this.mDialog.findViewById(R$id.odi_captions);
        this.mODICaptionsView = viewGroup3;
        if (viewGroup3 != null) {
            this.mODICaptionsIcon = (CaptionsToggleImageButton) viewGroup3.findViewById(R$id.odi_captions_icon);
        }
        ViewStub viewStub = (ViewStub) this.mDialog.findViewById(R$id.odi_captions_tooltip_stub);
        this.mODICaptionsTooltipViewStub = viewStub;
        if (this.mHasSeenODICaptionsTooltip && viewStub != null) {
            this.mDialogView.removeView(viewStub);
            this.mODICaptionsTooltipViewStub = null;
        }
        this.mSettingsView = this.mDialog.findViewById(R$id.settings_container);
        this.mSettingsIcon = (ImageButton) this.mDialog.findViewById(R$id.settings);
        this.mDialogMain = (ViewGroup) this.mDialog.findViewById(R$id.main);
        if (this.mRows.isEmpty()) {
            if (!AudioSystem.isSingleVolume(this.mContext)) {
                int i = R$drawable.ic_volume_accessibility;
                addRow(10, i, i, true, false);
            }
            addRow(3, R$drawable.ic_volume_media, R$drawable.ic_volume_media_mute, true, true);
            if (!AudioSystem.isSingleVolume(this.mContext)) {
                addRow(2, R$drawable.ic_volume_ringer, R$drawable.ic_volume_ringer_mute, true, false);
                addRow(4, R$drawable.ic_alarm, R$drawable.ic_volume_alarm_mute, true, false);
                addRow(0, 17303683, 17303683, false, false);
                int i2 = R$drawable.ic_volume_bt_sco;
                addRow(6, i2, i2, false, false);
                addRow(1, R$drawable.ic_volume_system, R$drawable.ic_volume_system_mute, false, false);
            }
        } else {
            addExistingRows();
        }
        if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume()) {
            initAppVolumeRows();
            updateAppRowsH();
            updateAppVolumeIconH();
            for (AppVolumeRow updateAppVolumeRowH : this.mAppVolumeRows) {
                updateAppVolumeRowH(updateAppVolumeRowH);
            }
            if (C2129D.BUG) {
                dumpAppVolumeRows(this.mAppVolumeRows);
            }
        }
        updateRowsH(getActiveRow());
        initRingerH();
        initSettingsH();
        initODICaptionsH();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initDialog$2(DialogInterface dialogInterface) {
        this.mDialogView.getViewTreeObserver().addOnComputeInternalInsetsListener(this);
        if (!isLandscape()) {
            ViewGroup viewGroup = this.mDialogView;
            viewGroup.setTranslationX(((float) viewGroup.getWidth()) / 2.0f);
        }
        this.mDialogView.setAlpha(0.0f);
        this.mDialogView.animate().alpha(1.0f).translationX(0.0f).setDuration((long) this.mDialogShowAnimationDurationMs).setInterpolator(new SystemUIInterpolators$LogDecelerateInterpolator()).withEndAction(new VolumeDialogImpl$$ExternalSyntheticLambda18(this)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initDialog$1() {
        ImageButton imageButton;
        if (!Prefs.getBoolean(this.mContext, "TouchedRingerToggle", false) && (imageButton = this.mRingerIcon) != null) {
            imageButton.postOnAnimationDelayed(getSinglePressFor(imageButton), 1500);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initDialog$3(DialogInterface dialogInterface) {
        this.mDialogView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initDialog$4(View view, MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        this.mHovering = actionMasked == 9 || actionMasked == 7;
        rescheduleTimeoutH();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initDialog$5() {
        LayerDrawable layerDrawable = (LayerDrawable) this.mRingerAndDrawerContainer.getBackground();
        if (layerDrawable != null && layerDrawable.getNumberOfLayers() > 0) {
            this.mRingerAndDrawerContainerBackground = layerDrawable.getDrawable(0);
            updateBackgroundForDrawerClosedAmount();
            setTopContainerBackgroundDrawable();
        }
    }

    private void initDimens() {
        this.mDialogWidth = this.mContext.getResources().getDimensionPixelSize(R$dimen.volume_dialog_panel_width);
        this.mDialogCornerRadius = this.mContext.getResources().getDimensionPixelSize(R$dimen.volume_dialog_panel_width_half);
        this.mRingerDrawerItemSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.volume_ringer_drawer_item_size);
        this.mRingerRowsPadding = this.mContext.getResources().getDimensionPixelSize(R$dimen.volume_dialog_ringer_rows_padding);
        boolean hasVibrator = this.mController.hasVibrator();
        this.mShowVibrate = hasVibrator;
        this.mRingerCount = hasVibrator ? 3 : 2;
    }

    private void updateCliVolumeDialogScale() {
        if (MotoFeature.getInstance(this.mContext).isSupportCli() && MotoFeature.isCliContext(this.mContext)) {
            float f = (float) this.mContext.getResources().getDisplayMetrics().densityDpi;
            if (f > 360.0f) {
                final float f2 = 360.0f / f;
                this.mDialogView.post(new Runnable() {
                    public void run() {
                        VolumeDialogImpl.this.mRinger.setPivotX((float) VolumeDialogImpl.this.mRinger.getWidth());
                        VolumeDialogImpl.this.mRinger.setPivotY((float) VolumeDialogImpl.this.mRinger.getHeight());
                        VolumeDialogImpl.this.mRinger.setScaleX(f2);
                        VolumeDialogImpl.this.mRinger.setScaleY(f2);
                        VolumeDialogImpl.this.mDialogMain.setPivotX((float) VolumeDialogImpl.this.mDialogMain.getWidth());
                        VolumeDialogImpl.this.mDialogMain.setScaleX(f2);
                        VolumeDialogImpl.this.mDialogRowsView.setScaleY(f2);
                        int size = VolumeDialogImpl.this.mRows.size();
                        for (int i = 0; i < size; i++) {
                            VolumeRow volumeRow = (VolumeRow) VolumeDialogImpl.this.mRows.get(i);
                            ViewGroup.LayoutParams layoutParams = volumeRow.slider.getLayoutParams();
                            ViewGroup.LayoutParams layoutParams2 = volumeRow.sliderFrame.getLayoutParams();
                            int dimension = (int) VolumeDialogImpl.this.mContext.getResources().getDimension(R$dimen.volume_dialog_slider_height_cli);
                            layoutParams.width = dimension;
                            layoutParams2.height = dimension;
                        }
                        float f = f2;
                        int dimension2 = (int) (((float) ((int) VolumeDialogImpl.this.mContext.getResources().getDimension(R$dimen.volume_dialog_panel_transparent_padding))) * f);
                        VolumeDialogImpl.this.mDialogView.setPadding(dimension2, dimension2 / 2, (int) (((float) ((int) VolumeDialogImpl.this.mContext.getResources().getDimension(R$dimen.volume_dialog_panel_transparent_padding_right))) * f), 0);
                    }
                });
            }
        }
    }

    private int getAlphaAttr(int i) {
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{i});
        float f = obtainStyledAttributes.getFloat(0, 0.0f);
        obtainStyledAttributes.recycle();
        return (int) (f * 255.0f);
    }

    /* access modifiers changed from: private */
    public boolean isLandscape() {
        return this.mContext.getResources().getConfiguration().orientation == 2;
    }

    private boolean isRtl() {
        return this.mContext.getResources().getConfiguration().getLayoutDirection() == 1;
    }

    public void setStreamImportant(int i, boolean z) {
        this.mHandler.obtainMessage(5, i, z ? 1 : 0).sendToTarget();
    }

    public void setAutomute(boolean z) {
        if (this.mAutomute != z) {
            this.mAutomute = z;
            this.mHandler.sendEmptyMessage(4);
        }
    }

    public void setSilentMode(boolean z) {
        if (this.mSilentMode != z) {
            this.mSilentMode = z;
            this.mHandler.sendEmptyMessage(4);
        }
    }

    private void addRow(int i, int i2, int i3, boolean z, boolean z2) {
        addRow(i, i2, i3, z, z2, false);
    }

    private void addRow(int i, int i2, int i3, boolean z, boolean z2, boolean z3) {
        if (C2129D.BUG) {
            String str = TAG;
            Slog.d(str, "Adding row for stream " + i);
        }
        VolumeRow volumeRow = new VolumeRow();
        initRow(volumeRow, i, i2, i3, z, z2);
        this.mDialogRowsView.addView(volumeRow.view);
        this.mRows.add(volumeRow);
    }

    private void addExistingRows() {
        int size = this.mRows.size();
        for (int i = 0; i < size; i++) {
            VolumeRow volumeRow = this.mRows.get(i);
            initRow(volumeRow, volumeRow.stream, volumeRow.iconRes, volumeRow.iconMuteRes, volumeRow.important, volumeRow.defaultStream);
            this.mDialogRowsView.addView(volumeRow.view);
            updateVolumeRowH(volumeRow);
        }
    }

    /* access modifiers changed from: private */
    public VolumeRow getActiveRow() {
        for (VolumeRow next : this.mRows) {
            if (next.stream == this.mActiveStream) {
                return next;
            }
        }
        for (VolumeRow next2 : this.mRows) {
            if (next2.stream == 3) {
                return next2;
            }
        }
        return this.mRows.get(0);
    }

    /* access modifiers changed from: private */
    public VolumeRow findRow(int i) {
        for (VolumeRow next : this.mRows) {
            if (next.stream == i) {
                return next;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static int getImpliedLevel(SeekBar seekBar, int i) {
        int max = seekBar.getMax();
        int i2 = max / 100;
        int i3 = i2 - 1;
        if (i == 0) {
            return 0;
        }
        return i == max ? i2 : ((int) ((((float) i) / ((float) max)) * ((float) i3))) + 1;
    }

    @SuppressLint({"InflateParams"})
    private void initRow(VolumeRow volumeRow, int i, int i2, int i3, boolean z, boolean z2) {
        volumeRow.stream = i;
        int unused = volumeRow.iconRes = i2;
        int unused2 = volumeRow.iconMuteRes = i3;
        boolean unused3 = volumeRow.important = z;
        boolean unused4 = volumeRow.defaultStream = z2;
        AlphaTintDrawableWrapper alphaTintDrawableWrapper = null;
        View inflate = this.mDialog.getLayoutInflater().inflate(R$layout.volume_dialog_row, (ViewGroup) null);
        volumeRow.view = inflate;
        inflate.setId(volumeRow.stream);
        volumeRow.view.setTag(volumeRow);
        TextView textView = (TextView) volumeRow.view.findViewById(R$id.volume_row_header);
        volumeRow.header = textView;
        textView.setId(volumeRow.stream * 20);
        if (i == 10) {
            volumeRow.header.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        }
        volumeRow.detailView = (TextView) volumeRow.view.findViewById(R$id.volume_detail);
        FrameLayout unused5 = volumeRow.dndIcon = (FrameLayout) volumeRow.view.findViewById(R$id.dnd_icon);
        volumeRow.sliderFrame = volumeRow.view.findViewById(R$id.volume_row_slider_frame);
        SeekBar seekBar = (SeekBar) volumeRow.view.findViewById(R$id.volume_row_slider);
        volumeRow.slider = seekBar;
        seekBar.setOnSeekBarChangeListener(new VolumeSeekBarChangeListener(volumeRow));
        volumeRow.number = (TextView) volumeRow.view.findViewById(R$id.volume_number);
        volumeRow.anim = null;
        LayerDrawable layerDrawable = (LayerDrawable) this.mContext.getDrawable(R$drawable.volume_row_seekbar);
        LayerDrawable layerDrawable2 = (LayerDrawable) ((RoundedCornerProgressDrawable) layerDrawable.findDrawableByLayerId(16908301)).getDrawable();
        Drawable unused6 = volumeRow.sliderProgressSolid = layerDrawable2.findDrawableByLayerId(R$id.volume_seekbar_progress_solid);
        Drawable findDrawableByLayerId = layerDrawable2.findDrawableByLayerId(R$id.volume_seekbar_progress_icon);
        if (findDrawableByLayerId != null) {
            alphaTintDrawableWrapper = (AlphaTintDrawableWrapper) ((RotateDrawable) findDrawableByLayerId).getDrawable();
        }
        volumeRow.sliderProgressIcon = alphaTintDrawableWrapper;
        volumeRow.slider.setProgressDrawable(layerDrawable);
        volumeRow.icon = (ImageButton) volumeRow.view.findViewById(R$id.volume_row_icon);
        volumeRow.setIcon(i2, this.mContext.getTheme());
        ImageButton imageButton = volumeRow.icon;
        if (imageButton == null) {
            return;
        }
        if (volumeRow.stream != 10) {
            imageButton.setOnClickListener(new VolumeDialogImpl$$ExternalSyntheticLambda10(this, volumeRow, i));
        } else {
            imageButton.setImportantForAccessibility(2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initRow$6(VolumeRow volumeRow, int i, View view) {
        int i2 = 0;
        boolean z = true;
        Events.writeEvent(7, Integer.valueOf(volumeRow.stream), Integer.valueOf(volumeRow.iconState));
        this.mController.setActiveStream(volumeRow.stream);
        if (volumeRow.stream == 2) {
            boolean hasVibrator = this.mController.hasVibrator();
            if (this.mState.ringerModeInternal != 2) {
                this.mController.setRingerMode(2, false);
                if (volumeRow.f144ss.level == 0) {
                    this.mController.setStreamVolume(i, 1);
                }
            } else if (hasVibrator) {
                this.mController.setRingerMode(1, false);
            } else {
                if (volumeRow.f144ss.level != 0) {
                    z = false;
                }
                VolumeDialogController volumeDialogController = this.mController;
                if (z) {
                    i2 = volumeRow.lastAudibleLevel;
                }
                volumeDialogController.setStreamVolume(i, i2);
            }
        } else {
            VolumeDialogController.StreamState streamState = volumeRow.f144ss;
            int i3 = streamState.level;
            int i4 = streamState.levelMin;
            if (i3 == i4) {
                i2 = 1;
            }
            VolumeDialogController volumeDialogController2 = this.mController;
            if (i2 != 0) {
                i4 = volumeRow.lastAudibleLevel;
            }
            volumeDialogController2.setStreamVolume(i, i4);
        }
        volumeRow.userAttempt = 0;
    }

    /* access modifiers changed from: private */
    public void setRingerMode(int i) {
        Events.writeEvent(18, Integer.valueOf(i));
        incrementManualToggleCount();
        updateRingerH();
        provideTouchFeedbackH(i);
        this.mController.setRingerMode(i, false);
        maybeShowToastH(i);
    }

    private void setupRingerDrawer() {
        ViewGroup viewGroup = (ViewGroup) this.mDialog.findViewById(R$id.volume_drawer_container);
        this.mRingerDrawerContainer = viewGroup;
        if (viewGroup != null) {
            if (!this.mShowVibrate) {
                this.mRingerDrawerVibrate.setVisibility(8);
            }
            if (!isLandscape()) {
                ViewGroup viewGroup2 = this.mDialogView;
                viewGroup2.setPadding(viewGroup2.getPaddingLeft(), this.mDialogView.getPaddingTop(), this.mDialogView.getPaddingRight(), this.mDialogView.getPaddingBottom() + getRingerDrawerOpenExtraSize());
            } else {
                ViewGroup viewGroup3 = this.mDialogView;
                viewGroup3.setPadding(viewGroup3.getPaddingLeft() + getRingerDrawerOpenExtraSize(), this.mDialogView.getPaddingTop(), this.mDialogView.getPaddingRight(), this.mDialogView.getPaddingBottom());
            }
            ((LinearLayout) this.mRingerDrawerContainer.findViewById(R$id.volume_drawer_options)).setOrientation(isLandscape() ^ true ? 1 : 0);
            this.mSelectedRingerContainer.setOnClickListener(new VolumeDialogImpl$$ExternalSyntheticLambda8(this));
            this.mRingerDrawerVibrate.setOnClickListener(new RingerDrawerItemClickListener(1));
            this.mRingerDrawerMute.setOnClickListener(new RingerDrawerItemClickListener(0));
            this.mRingerDrawerNormal.setOnClickListener(new RingerDrawerItemClickListener(2));
            int colorAccentDefaultColor = Utils.getColorAccentDefaultColor(this.mContext);
            this.mRingerDrawerIconColorAnimator.addUpdateListener(new VolumeDialogImpl$$ExternalSyntheticLambda1(this, Utils.getColorAttrDefaultColor(this.mContext, 16844002), colorAccentDefaultColor));
            this.mRingerDrawerIconColorAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VolumeDialogImpl.this.mRingerDrawerIconAnimatingDeselected.clearColorFilter();
                    VolumeDialogImpl.this.mRingerDrawerIconAnimatingSelected.clearColorFilter();
                }
            });
            this.mRingerDrawerIconColorAnimator.setDuration(175);
            this.mAnimateUpBackgroundToMatchDrawer.addUpdateListener(new VolumeDialogImpl$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupRingerDrawer$7(View view) {
        if (this.mIsRingerDrawerOpen) {
            hideRingerDrawer();
        } else {
            showRingerDrawer();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupRingerDrawer$8(int i, int i2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        int intValue = ((Integer) ArgbEvaluator.getInstance().evaluate(floatValue, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
        int intValue2 = ((Integer) ArgbEvaluator.getInstance().evaluate(floatValue, Integer.valueOf(i2), Integer.valueOf(i))).intValue();
        this.mRingerDrawerIconAnimatingDeselected.setColorFilter(intValue);
        this.mRingerDrawerIconAnimatingSelected.setColorFilter(intValue2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupRingerDrawer$9(ValueAnimator valueAnimator) {
        this.mRingerDrawerClosedAmount = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateBackgroundForDrawerClosedAmount();
    }

    /* access modifiers changed from: private */
    public ImageView getDrawerIconViewForMode(int i) {
        if (i == 1) {
            return this.mRingerDrawerVibrateIcon;
        }
        if (i == 0) {
            return this.mRingerDrawerMuteIcon;
        }
        return this.mRingerDrawerNormalIcon;
    }

    /* access modifiers changed from: private */
    public float getTranslationInDrawerForRingerMode(int i) {
        int i2;
        if (i == 1) {
            i2 = (-this.mRingerDrawerItemSize) * 2;
        } else if (i != 0) {
            return 0.0f;
        } else {
            i2 = -this.mRingerDrawerItemSize;
        }
        return (float) i2;
    }

    private void showRingerDrawer() {
        if (!this.mIsRingerDrawerOpen) {
            int i = 4;
            this.mRingerDrawerVibrateIcon.setVisibility(this.mState.ringerModeInternal == 1 ? 4 : 0);
            this.mRingerDrawerMuteIcon.setVisibility(this.mState.ringerModeInternal == 0 ? 4 : 0);
            ImageView imageView = this.mRingerDrawerNormalIcon;
            if (this.mState.ringerModeInternal != 2) {
                i = 0;
            }
            imageView.setVisibility(i);
            this.mRingerDrawerNewSelectionBg.setAlpha(0.0f);
            if (!isLandscape()) {
                this.mRingerDrawerNewSelectionBg.setTranslationY(getTranslationInDrawerForRingerMode(this.mState.ringerModeInternal));
            } else {
                this.mRingerDrawerNewSelectionBg.setTranslationX(getTranslationInDrawerForRingerMode(this.mState.ringerModeInternal));
            }
            if (!isLandscape()) {
                this.mRingerDrawerContainer.setTranslationY((float) (this.mRingerDrawerItemSize * (this.mRingerCount - 1)));
            } else {
                this.mRingerDrawerContainer.setTranslationX((float) (this.mRingerDrawerItemSize * (this.mRingerCount - 1)));
            }
            this.mRingerDrawerContainer.setAlpha(0.0f);
            this.mRingerDrawerContainer.setVisibility(0);
            int i2 = this.mState.ringerModeInternal == 1 ? 175 : 250;
            ViewPropertyAnimator animate = this.mRingerDrawerContainer.animate();
            Interpolator interpolator = Interpolators.FAST_OUT_SLOW_IN;
            long j = (long) i2;
            animate.setInterpolator(interpolator).setDuration(j).setStartDelay(this.mState.ringerModeInternal == 1 ? 75 : 0).alpha(1.0f).translationX(0.0f).translationY(0.0f).start();
            this.mSelectedRingerContainer.animate().setInterpolator(interpolator).setDuration(250).withEndAction(new VolumeDialogImpl$$ExternalSyntheticLambda14(this));
            this.mAnimateUpBackgroundToMatchDrawer.setDuration(j);
            this.mAnimateUpBackgroundToMatchDrawer.setInterpolator(interpolator);
            this.mAnimateUpBackgroundToMatchDrawer.start();
            if (!isLandscape()) {
                this.mSelectedRingerContainer.animate().translationY(getTranslationInDrawerForRingerMode(this.mState.ringerModeInternal)).start();
            } else {
                this.mSelectedRingerContainer.animate().translationX(getTranslationInDrawerForRingerMode(this.mState.ringerModeInternal)).start();
            }
            this.mSelectedRingerContainer.setContentDescription(this.mContext.getString(getStringDescriptionResourceForRingerMode(this.mState.ringerModeInternal)));
            this.mIsRingerDrawerOpen = true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showRingerDrawer$10() {
        getDrawerIconViewForMode(this.mState.ringerModeInternal).setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void hideRingerDrawer() {
        if (this.mRingerDrawerContainer != null && this.mIsRingerDrawerOpen) {
            getDrawerIconViewForMode(this.mState.ringerModeInternal).setVisibility(4);
            this.mRingerDrawerContainer.animate().alpha(0.0f).setDuration(250).setStartDelay(0).withEndAction(new VolumeDialogImpl$$ExternalSyntheticLambda20(this));
            if (!isLandscape()) {
                this.mRingerDrawerContainer.animate().translationY((float) (this.mRingerDrawerItemSize * 2)).start();
            } else {
                this.mRingerDrawerContainer.animate().translationX((float) (this.mRingerDrawerItemSize * 2)).start();
            }
            this.mAnimateUpBackgroundToMatchDrawer.setDuration(250);
            this.mAnimateUpBackgroundToMatchDrawer.setInterpolator(Interpolators.FAST_OUT_SLOW_IN_REVERSE);
            this.mAnimateUpBackgroundToMatchDrawer.reverse();
            this.mSelectedRingerContainer.animate().translationX(0.0f).translationY(0.0f).start();
            this.mSelectedRingerContainer.setContentDescription(this.mContext.getString(R$string.volume_ringer_change));
            this.mIsRingerDrawerOpen = false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideRingerDrawer$11() {
        this.mRingerDrawerContainer.setVisibility(4);
    }

    public void initSettingsH() {
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext)) {
            ImageButton imageButton = this.mSettingsIcon;
            if (imageButton != null) {
                imageButton.setOnClickListener(new VolumeDialogImpl$$ExternalSyntheticLambda6(this));
                this.mSettingsIcon.setVisibility((!this.mDeviceProvisionedController.isCurrentUserSetup() || this.mActivityManager.getLockTaskModeState() != 0) ? 4 : 0);
                return;
            }
            return;
        }
        this.mSettingsView.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initSettingsH$12(View view) {
        Events.writeEvent(8, new Object[0]);
        Intent intent = new Intent("android.settings.panel.action.VOLUME");
        dismissH(5);
        ((MediaOutputDialogFactory) Dependency.get(MediaOutputDialogFactory.class)).dismiss();
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).startActivity(intent, true);
    }

    public void initRingerH() {
        ImageButton imageButton = this.mRingerIcon;
        if (imageButton != null) {
            imageButton.setAccessibilityLiveRegion(1);
            this.mRingerIcon.setOnClickListener(new VolumeDialogImpl$$ExternalSyntheticLambda5(this));
        }
        updateRingerH();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0023, code lost:
        if (r2 != false) goto L_0x0034;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$initRingerH$13(android.view.View r6) {
        /*
            r5 = this;
            android.content.Context r6 = r5.mContext
            java.lang.String r0 = "TouchedRingerToggle"
            r1 = 1
            com.android.systemui.Prefs.putBoolean(r6, r0, r1)
            com.android.systemui.plugins.VolumeDialogController$State r6 = r5.mState
            android.util.SparseArray<com.android.systemui.plugins.VolumeDialogController$StreamState> r6 = r6.states
            r0 = 2
            java.lang.Object r6 = r6.get(r0)
            com.android.systemui.plugins.VolumeDialogController$StreamState r6 = (com.android.systemui.plugins.VolumeDialogController.StreamState) r6
            if (r6 != 0) goto L_0x0016
            return
        L_0x0016:
            com.android.systemui.plugins.VolumeDialogController r2 = r5.mController
            boolean r2 = r2.hasVibrator()
            com.android.systemui.plugins.VolumeDialogController$State r3 = r5.mState
            int r3 = r3.ringerModeInternal
            r4 = 0
            if (r3 != r0) goto L_0x0026
            if (r2 == 0) goto L_0x0028
            goto L_0x0034
        L_0x0026:
            if (r3 != r1) goto L_0x002a
        L_0x0028:
            r1 = r4
            goto L_0x0034
        L_0x002a:
            int r6 = r6.level
            if (r6 != 0) goto L_0x0033
            com.android.systemui.plugins.VolumeDialogController r6 = r5.mController
            r6.setStreamVolume(r0, r1)
        L_0x0033:
            r1 = r0
        L_0x0034:
            r5.setRingerMode(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.volume.VolumeDialogImpl.lambda$initRingerH$13(android.view.View):void");
    }

    private void initODICaptionsH() {
        CaptionsToggleImageButton captionsToggleImageButton = this.mODICaptionsIcon;
        if (captionsToggleImageButton != null) {
            captionsToggleImageButton.setOnConfirmedTapListener(new VolumeDialogImpl$$ExternalSyntheticLambda12(this), this.mHandler);
        }
        this.mController.getCaptionsComponentState(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initODICaptionsH$14() {
        onCaptionIconClicked();
        Events.writeEvent(21, new Object[0]);
    }

    private void checkODICaptionsTooltip(boolean z) {
        boolean z2 = this.mHasSeenODICaptionsTooltip;
        if (!z2 && !z && this.mODICaptionsTooltipViewStub != null) {
            this.mController.getCaptionsComponentState(true);
        } else if (z2 && z && this.mODICaptionsTooltipView != null) {
            hideCaptionsTooltip();
        }
    }

    /* access modifiers changed from: protected */
    public void showCaptionsTooltip() {
        ViewStub viewStub;
        if (!this.mHasSeenODICaptionsTooltip && (viewStub = this.mODICaptionsTooltipViewStub) != null) {
            View inflate = viewStub.inflate();
            this.mODICaptionsTooltipView = inflate;
            inflate.findViewById(R$id.dismiss).setOnClickListener(new VolumeDialogImpl$$ExternalSyntheticLambda4(this));
            this.mODICaptionsTooltipViewStub = null;
            rescheduleTimeoutH();
        }
        View view = this.mODICaptionsTooltipView;
        if (view != null) {
            view.setAlpha(0.0f);
            this.mHandler.post(new VolumeDialogImpl$$ExternalSyntheticLambda19(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showCaptionsTooltip$15(View view) {
        hideCaptionsTooltip();
        Events.writeEvent(22, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showCaptionsTooltip$17() {
        int[] locationOnScreen = this.mODICaptionsTooltipView.getLocationOnScreen();
        int[] locationOnScreen2 = this.mODICaptionsIcon.getLocationOnScreen();
        this.mODICaptionsTooltipView.setTranslationY(((float) (locationOnScreen2[1] - locationOnScreen[1])) - (((float) (this.mODICaptionsTooltipView.getHeight() - this.mODICaptionsIcon.getHeight())) / 2.0f));
        this.mODICaptionsTooltipView.animate().alpha(1.0f).setStartDelay((long) this.mDialogShowAnimationDurationMs).withEndAction(new VolumeDialogImpl$$ExternalSyntheticLambda16(this)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showCaptionsTooltip$16() {
        if (C2129D.BUG) {
            Log.d(TAG, "tool:checkODICaptionsTooltip() putBoolean true");
        }
        Prefs.putBoolean(this.mContext, "HasSeenODICaptionsTooltip", true);
        this.mHasSeenODICaptionsTooltip = true;
        CaptionsToggleImageButton captionsToggleImageButton = this.mODICaptionsIcon;
        if (captionsToggleImageButton != null) {
            captionsToggleImageButton.postOnAnimation(getSinglePressFor(captionsToggleImageButton));
        }
    }

    private void hideCaptionsTooltip() {
        View view = this.mODICaptionsTooltipView;
        if (view != null && view.getVisibility() == 0) {
            this.mODICaptionsTooltipView.animate().cancel();
            this.mODICaptionsTooltipView.setAlpha(1.0f);
            this.mODICaptionsTooltipView.animate().alpha(0.0f).setStartDelay(0).setDuration((long) this.mDialogHideAnimationDurationMs).withEndAction(new VolumeDialogImpl$$ExternalSyntheticLambda17(this)).start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideCaptionsTooltip$18() {
        View view = this.mODICaptionsTooltipView;
        if (view != null) {
            view.setVisibility(4);
        }
    }

    /* access modifiers changed from: protected */
    public void tryToRemoveCaptionsTooltip() {
        if (this.mHasSeenODICaptionsTooltip && this.mODICaptionsTooltipView != null) {
            ((ViewGroup) this.mDialog.findViewById(R$id.volume_dialog_container)).removeView(this.mODICaptionsTooltipView);
            this.mODICaptionsTooltipView = null;
        }
    }

    /* access modifiers changed from: private */
    public void updateODICaptionsH(boolean z, boolean z2) {
        boolean z3 = true;
        int i = 0;
        boolean z4 = MotoFeature.getInstance(this.mContext).isSupportCli() && MotoFeature.isCliContext(this.mContext);
        if (!z || z4) {
            z3 = false;
        }
        ViewGroup viewGroup = this.mODICaptionsView;
        if (viewGroup != null) {
            if (!z3) {
                i = 8;
            }
            viewGroup.setVisibility(i);
        }
        if (z3) {
            updateCaptionsIcon();
            if (z2) {
                showCaptionsTooltip();
            }
        }
    }

    private void updateCaptionsIcon() {
        boolean areCaptionsEnabled = this.mController.areCaptionsEnabled();
        if (this.mODICaptionsIcon.getCaptionsEnabled() != areCaptionsEnabled) {
            this.mHandler.post(this.mODICaptionsIcon.setCaptionsEnabled(areCaptionsEnabled));
        }
        boolean isCaptionStreamOptedOut = this.mController.isCaptionStreamOptedOut();
        if (this.mODICaptionsIcon.getOptedOut() != isCaptionStreamOptedOut) {
            this.mHandler.post(new VolumeDialogImpl$$ExternalSyntheticLambda24(this, isCaptionStreamOptedOut));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateCaptionsIcon$19(boolean z) {
        this.mODICaptionsIcon.setOptedOut(z);
    }

    private void onCaptionIconClicked() {
        this.mController.setCaptionsEnabled(!this.mController.areCaptionsEnabled());
        updateCaptionsIcon();
    }

    private void incrementManualToggleCount() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Settings.Secure.putInt(contentResolver, "manual_ringer_toggle_count", Settings.Secure.getInt(contentResolver, "manual_ringer_toggle_count", 0) + 1);
    }

    private void provideTouchFeedbackH(int i) {
        VibrationEffect vibrationEffect;
        if (i == 0) {
            vibrationEffect = VibrationEffect.get(0);
        } else if (i != 2) {
            vibrationEffect = VibrationEffect.get(1);
        } else {
            this.mController.scheduleTouchFeedback();
            vibrationEffect = null;
        }
        if (vibrationEffect != null) {
            this.mController.vibrate(vibrationEffect);
        }
    }

    private void maybeShowToastH(int i) {
        int i2 = Prefs.getInt(this.mContext, "RingerGuidanceCount", 0);
        if (i2 <= 12) {
            String str = null;
            if (i == 0) {
                str = this.mContext.getString(17041635);
            } else if (i != 2) {
                str = this.mContext.getString(17041636);
            } else {
                VolumeDialogController.StreamState streamState = this.mState.states.get(2);
                if (streamState != null) {
                    str = this.mContext.getString(R$string.volume_dialog_ringer_guidance_ring, new Object[]{Utils.formatPercentage((long) streamState.level, (long) streamState.levelMax)});
                }
            }
            if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext)) {
                Toast.makeText(this.mContext, str, 0).show();
            } else {
                CliToast.getInstance(this.mContext).makeText(str, 0).show();
            }
            Prefs.putInt(this.mContext, "RingerGuidanceCount", i2 + 1);
        }
    }

    public void show(int i) {
        this.mHandler.obtainMessage(1, i, 0).sendToTarget();
    }

    public void dismiss(int i) {
        this.mHandler.obtainMessage(2, i, 0).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void showH(int i) {
        if (C2129D.BUG) {
            String str = TAG;
            Log.d(str, "showH r=" + Events.SHOW_REASONS[i]);
        }
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext) || MotoFeature.isLidClosed(this.mContext)) {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            rescheduleTimeoutH();
            if (this.mConfigChanged) {
                initDialog();
                this.mConfigurableTexts.update();
                this.mConfigChanged = false;
            }
            initSettingsH();
            this.mShowing = true;
            this.mIsAnimatingDismiss = false;
            this.mDialog.show();
            Events.writeEvent(0, Integer.valueOf(i), Boolean.valueOf(this.mKeyguard.isKeyguardLocked()));
            this.mController.notifyVisible(true);
            this.mController.getCaptionsComponentState(false);
            checkODICaptionsTooltip(false);
            updateBackgroundForDrawerClosedAmount();
        }
    }

    /* access modifiers changed from: protected */
    public void rescheduleTimeoutH() {
        this.mHandler.removeMessages(2);
        int computeTimeoutH = computeTimeoutH();
        C2173H h = this.mHandler;
        h.sendMessageDelayed(h.obtainMessage(2, 3, 0), (long) computeTimeoutH);
        if (C2129D.BUG) {
            String str = TAG;
            Log.d(str, "rescheduleTimeout " + computeTimeoutH + " " + Debug.getCaller());
        }
        this.mController.userActivity();
    }

    private int computeTimeoutH() {
        if (this.mHovering) {
            return this.mAccessibilityMgr.getRecommendedTimeoutMillis(16000, 4);
        }
        if (this.mSafetyWarning != null) {
            return this.mAccessibilityMgr.getRecommendedTimeoutMillis(5000, 6);
        }
        if (this.mHasSeenODICaptionsTooltip || this.mODICaptionsTooltipView == null) {
            return this.mAccessibilityMgr.getRecommendedTimeoutMillis(3000, 4);
        }
        return this.mAccessibilityMgr.getRecommendedTimeoutMillis(5000, 6);
    }

    /* access modifiers changed from: protected */
    public void dismissH(int i) {
        if (C2129D.BUG) {
            String str = TAG;
            Log.d(str, "mDialog.dismiss() reason: " + Events.DISMISS_REASONS[i] + " from: " + Debug.getCaller());
        }
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(1);
        if (!this.mIsAnimatingDismiss) {
            this.mIsAnimatingDismiss = true;
            this.mDialogView.animate().cancel();
            if (this.mShowing) {
                this.mShowing = false;
                Events.writeEvent(1, Integer.valueOf(i));
                if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume()) {
                    resetAppVolumeRows(this.mAppVolumeRows);
                }
            }
            this.mDialogView.setTranslationX(0.0f);
            this.mDialogView.setAlpha(1.0f);
            ViewPropertyAnimator withEndAction = this.mDialogView.animate().alpha(0.0f).setDuration((long) this.mDialogHideAnimationDurationMs).setInterpolator(new SystemUIInterpolators$LogAccelerateInterpolator()).withEndAction(new VolumeDialogImpl$$ExternalSyntheticLambda22(this, i));
            if (!isLandscape()) {
                withEndAction.translationX(((float) this.mDialogView.getWidth()) / 2.0f);
            }
            withEndAction.start();
            checkODICaptionsTooltip(true);
            this.mController.notifyVisible(false);
            synchronized (this.mSafetyWarningLock) {
                if (this.mSafetyWarning != null) {
                    if (C2129D.BUG) {
                        Log.d(TAG, "SafetyWarning dismissed");
                    }
                    this.mSafetyWarning.dismiss();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismissH$21(int i) {
        this.mHandler.postDelayed(new VolumeDialogImpl$$ExternalSyntheticLambda21(this, i), 50);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismissH$20(int i) {
        CustomDialog customDialog = this.mDialog;
        if (customDialog != null && customDialog.isShowing()) {
            String str = TAG;
            Log.i(str, "dismissH reason = " + Events.DISMISS_REASONS[i]);
        }
        this.mDialog.dismiss();
        tryToRemoveCaptionsTooltip();
        this.mIsAnimatingDismiss = false;
        hideRingerDrawer();
    }

    private boolean showActiveStreamOnly() {
        return this.mContext.getPackageManager().hasSystemFeature("android.software.leanback") || this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.television");
    }

    private boolean shouldBeVisibleH(VolumeRow volumeRow, VolumeRow volumeRow2) {
        int i = volumeRow.stream;
        int i2 = volumeRow2.stream;
        if (i == i2) {
            return true;
        }
        if (this.mShowActiveStreamOnly) {
            return false;
        }
        if (i == 10) {
            return this.mShowA11yStream;
        }
        if (i2 == 10 && i == this.mPrevActiveStream) {
            return true;
        }
        if (this.mPrevActiveStream == 0 && i == 0 && i2 == 3) {
            return true;
        }
        if (!volumeRow.defaultStream) {
            return false;
        }
        int i3 = volumeRow2.stream;
        if (i3 == 2 || i3 == 4 || i3 == 0 || i3 == 10 || this.mDynamic.get(i3)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updateRowsH(VolumeRow volumeRow) {
        boolean z;
        String str;
        int min;
        if (C2129D.BUG) {
            Log.d(TAG, "updateRowsH");
        }
        if (!this.mShowing) {
            trimObsoleteH();
        }
        int i = !isRtl() ? -1 : 32767;
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "updateRowsH, activeRow.stream: " + AudioSystem.streamToString(volumeRow.stream) + ", mShowActiveStreamOnly: " + this.mShowActiveStreamOnly + ", mShowing: " + this.mShowing + ", rightmostVisibleRowIndex: " + i + ", isRtl(): " + isRtl() + ", mRingerAndDrawerContainerBackground: " + this.mRingerAndDrawerContainerBackground);
        }
        List arrayList = new ArrayList();
        if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume()) {
            arrayList.addAll(this.mRows);
            arrayList.addAll(this.mAppVolumeRows);
        } else {
            arrayList = this.mRows;
        }
        Iterator it = arrayList.iterator();
        while (true) {
            boolean z2 = false;
            if (!it.hasNext()) {
                break;
            }
            VolumeRow volumeRow2 = (VolumeRow) it.next();
            if (volumeRow2 instanceof AppVolumeRow) {
                if (volumeRow.stream == 3) {
                    z2 = true;
                }
                z = shouldAppVolumeRowBeVisibleH((AppVolumeRow) volumeRow2);
            } else {
                if (volumeRow2 == volumeRow) {
                    z2 = true;
                }
                z = shouldBeVisibleH(volumeRow2, volumeRow);
            }
            Util.setVisOrGone(volumeRow2.view, z);
            if (z && this.mRingerAndDrawerContainerBackground != null) {
                if (!isRtl()) {
                    min = Math.max(i, this.mDialogRowsView.indexOfChild(volumeRow2.view));
                } else {
                    min = Math.min(i, this.mDialogRowsView.indexOfChild(volumeRow2.view));
                }
                ViewGroup.LayoutParams layoutParams = volumeRow2.view.getLayoutParams();
                if (layoutParams instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) layoutParams;
                    if (!isRtl()) {
                        layoutParams2.setMarginEnd(this.mRingerRowsPadding);
                    } else {
                        layoutParams2.setMarginStart(this.mRingerRowsPadding);
                    }
                }
                volumeRow2.view.setBackgroundDrawable(this.mContext.getDrawable(R$drawable.volume_row_rounded_background));
            }
            if (volumeRow2.view.isShown()) {
                updateVolumeRowTintH(volumeRow2, z2);
            }
            if (C2129D.BUG) {
                String str2 = TAG + ".dv";
                StringBuilder sb = new StringBuilder();
                sb.append("updateRowsH, ");
                if (volumeRow2 instanceof AppVolumeRow) {
                    str = "row.packageName: " + ((AppVolumeRow) volumeRow2).packageName;
                } else {
                    str = "row.stream: " + AudioSystem.streamToString(volumeRow2.stream);
                }
                sb.append(str);
                sb.append(", indexOfChild: ");
                sb.append(this.mDialogRowsView.indexOfChild(volumeRow2.view));
                sb.append(", isActive: ");
                sb.append(z2);
                sb.append(", shouldBeVisible: ");
                sb.append(z);
                sb.append(", rightmostVisibleRowIndex: ");
                sb.append(i);
                Log.d(str2, sb.toString());
            }
        }
        if (i > -1 && i < 32767) {
            View childAt = this.mDialogRowsView.getChildAt(i);
            ViewGroup.LayoutParams layoutParams3 = childAt.getLayoutParams();
            if (layoutParams3 instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) layoutParams3;
                layoutParams4.setMarginStart(0);
                layoutParams4.setMarginEnd(0);
                childAt.setBackgroundColor(0);
            }
        }
        updateBackgroundForDrawerClosedAmount();
        updateCliVolumeDialogScale();
    }

    /* access modifiers changed from: protected */
    public void updateRingerH() {
        VolumeDialogController.State state;
        VolumeDialogController.StreamState streamState;
        if (this.mRinger != null && (state = this.mState) != null && (streamState = state.states.get(2)) != null) {
            VolumeDialogController.State state2 = this.mState;
            int i = state2.zenMode;
            boolean z = false;
            boolean z2 = i == 3 || i == 2 || (i == 1 && state2.disallowRinger);
            enableRingerViewsH(!z2);
            int i2 = this.mState.ringerModeInternal;
            if (i2 == 0) {
                ImageButton imageButton = this.mRingerIcon;
                int i3 = R$drawable.ic_volume_ringer_mute;
                imageButton.setImageResource(i3);
                this.mSelectedRingerIcon.setImageResource(i3);
                this.mRingerIcon.setTag(2);
                addAccessibilityDescription(this.mRingerIcon, 0, this.mContext.getString(R$string.volume_ringer_hint_unmute));
            } else if (i2 != 1) {
                if ((this.mAutomute && streamState.level == 0) || streamState.muted) {
                    z = true;
                }
                if (z2 || !z) {
                    ImageButton imageButton2 = this.mRingerIcon;
                    int i4 = R$drawable.ic_volume_ringer;
                    imageButton2.setImageResource(i4);
                    this.mSelectedRingerIcon.setImageResource(i4);
                    if (this.mController.hasVibrator()) {
                        addAccessibilityDescription(this.mRingerIcon, 2, this.mContext.getString(R$string.volume_ringer_hint_vibrate));
                    } else {
                        addAccessibilityDescription(this.mRingerIcon, 2, this.mContext.getString(R$string.volume_ringer_hint_mute));
                    }
                    this.mRingerIcon.setTag(1);
                    return;
                }
                ImageButton imageButton3 = this.mRingerIcon;
                int i5 = R$drawable.ic_volume_ringer_mute;
                imageButton3.setImageResource(i5);
                this.mSelectedRingerIcon.setImageResource(i5);
                addAccessibilityDescription(this.mRingerIcon, 2, this.mContext.getString(R$string.volume_ringer_hint_unmute));
                this.mRingerIcon.setTag(2);
            } else {
                ImageButton imageButton4 = this.mRingerIcon;
                int i6 = R$drawable.ic_volume_ringer_vibrate;
                imageButton4.setImageResource(i6);
                this.mSelectedRingerIcon.setImageResource(i6);
                addAccessibilityDescription(this.mRingerIcon, 1, this.mContext.getString(R$string.volume_ringer_hint_mute));
                this.mRingerIcon.setTag(3);
            }
        }
    }

    private void addAccessibilityDescription(View view, int i, final String str) {
        view.setContentDescription(this.mContext.getString(getStringDescriptionResourceForRingerMode(i)));
        view.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, str));
            }
        });
    }

    private int getStringDescriptionResourceForRingerMode(int i) {
        if (i == 0) {
            return R$string.volume_ringer_status_silent;
        }
        if (i != 1) {
            return R$string.volume_ringer_status_normal;
        }
        return R$string.volume_ringer_status_vibrate;
    }

    private void enableVolumeRowViewsH(VolumeRow volumeRow, boolean z) {
        volumeRow.dndIcon.setVisibility(z ^ true ? 0 : 8);
    }

    private void enableRingerViewsH(boolean z) {
        ImageButton imageButton = this.mRingerIcon;
        if (imageButton != null) {
            imageButton.setEnabled(z);
        }
        FrameLayout frameLayout = this.mZenIcon;
        if (frameLayout != null) {
            frameLayout.setVisibility(z ? 8 : 0);
        }
    }

    private void trimObsoleteH() {
        if (C2129D.BUG) {
            Log.d(TAG, "trimObsoleteH");
        }
        for (int size = this.mRows.size() - 1; size >= 0; size--) {
            VolumeRow volumeRow = this.mRows.get(size);
            VolumeDialogController.StreamState streamState = volumeRow.f144ss;
            if (streamState != null && streamState.dynamic && !this.mDynamic.get(volumeRow.stream)) {
                this.mRows.remove(size);
                this.mDialogRowsView.removeView(volumeRow.view);
                this.mConfigurableTexts.remove(volumeRow.header);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStateChangedH(VolumeDialogController.State state) {
        int i;
        int i2;
        if (C2129D.BUG || !Build.IS_USER) {
            Log.d(TAG, "onStateChangedH() state: " + state.toString());
        }
        VolumeDialogController.State state2 = this.mState;
        if (!(state2 == null || state == null || (i = state2.ringerModeInternal) == -1 || i == (i2 = state.ringerModeInternal) || i2 != 1)) {
            this.mController.vibrate(VibrationEffect.get(5));
        }
        this.mState = state;
        this.mDynamic.clear();
        boolean z = false;
        for (int i3 = 0; i3 < state.states.size(); i3++) {
            int keyAt = state.states.keyAt(i3);
            if (state.states.valueAt(i3).dynamic) {
                this.mDynamic.put(keyAt, true);
                if (findRow(keyAt) == null) {
                    addRow(keyAt, R$drawable.ic_volume_remote, R$drawable.ic_volume_remote_mute, true, false, true);
                }
            }
        }
        if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume()) {
            z = updateAppRowsH();
        }
        int i4 = this.mActiveStream;
        int i5 = state.activeStream;
        if (i4 != i5 || z) {
            this.mPrevActiveStream = i4;
            this.mActiveStream = i5;
            updateRowsH(getActiveRow());
            if (this.mShowing) {
                rescheduleTimeoutH();
            }
        }
        for (VolumeRow updateVolumeRowH : this.mRows) {
            updateVolumeRowH(updateVolumeRowH);
        }
        if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume()) {
            updateAppVolumeIconH();
            for (AppVolumeRow updateAppVolumeRowH : this.mAppVolumeRows) {
                updateAppVolumeRowH(updateAppVolumeRowH);
            }
            if (C2129D.BUG) {
                dumpAppVolumeRows(this.mAppVolumeRows);
            }
        }
        updateRingerH();
        this.mWindow.setTitle(composeWindowTitle());
    }

    /* access modifiers changed from: package-private */
    public CharSequence composeWindowTitle() {
        return this.mContext.getString(R$string.volume_dialog_title, new Object[]{getStreamLabelH(getActiveRow().f144ss)});
    }

    private void updateVolumeRowH(VolumeRow volumeRow) {
        VolumeDialogController.StreamState streamState;
        boolean z;
        int i;
        int i2;
        int i3;
        int i4;
        VolumeRow volumeRow2 = volumeRow;
        if (C2129D.BUG) {
            Log.i(TAG, "updateVolumeRowH s=" + volumeRow2.stream);
        }
        VolumeDialogController.State state = this.mState;
        if (state != null && (streamState = state.states.get(volumeRow2.stream)) != null) {
            volumeRow2.f144ss = streamState;
            int i5 = streamState.level;
            if (i5 > 0) {
                volumeRow2.lastAudibleLevel = i5;
            }
            if (i5 == volumeRow2.requestedLevel) {
                volumeRow2.requestedLevel = -1;
            }
            int i6 = volumeRow2.stream;
            int i7 = 0;
            boolean z2 = i6 == 10;
            int i8 = 2;
            boolean z3 = i6 == 2;
            boolean z4 = i6 == 1;
            boolean z5 = i6 == 4;
            boolean z6 = i6 == 3;
            boolean z7 = z3 && this.mState.ringerModeInternal == 1;
            boolean z8 = z3 && this.mState.ringerModeInternal == 0;
            VolumeDialogController.State state2 = this.mState;
            int i9 = state2.zenMode;
            boolean z9 = i9 == 1;
            boolean z10 = i9 == 3;
            boolean z11 = i9 == 2;
            if (!z10 ? !z11 ? !z9 || ((!z5 || !state2.disallowAlarms) && ((!z6 || !state2.disallowMedia) && ((!z3 || !state2.disallowRinger) && (!z4 || !state2.disallowSystem)))) : !z3 && !z4 && !z5 && !z6 : !z3 && !z4) {
                z = false;
            } else {
                z = true;
            }
            int i10 = streamState.levelMax * 100;
            if (i10 != volumeRow2.slider.getMax()) {
                volumeRow2.slider.setMax(i10);
            }
            int i11 = streamState.levelMin * 100;
            if (i11 != volumeRow2.slider.getMin()) {
                volumeRow2.slider.setMin(i11);
            }
            Util.setText(volumeRow2.header, getStreamLabelH(streamState));
            volumeRow2.slider.setContentDescription(volumeRow2.header.getText());
            this.mConfigurableTexts.add(volumeRow2.header, streamState.name);
            boolean z12 = (this.mAutomute || streamState.muteSupported) && !z;
            if (z7) {
                i = R$drawable.ic_volume_ringer_vibrate;
            } else if (z8 || z) {
                i = volumeRow.iconMuteRes;
            } else if (streamState.routedToBluetooth) {
                i = isStreamMuted(streamState) ? R$drawable.ic_volume_media_bt_mute : R$drawable.ic_volume_media_bt;
            } else {
                i = isStreamMuted(streamState) ? streamState.muted ? R$drawable.ic_volume_media_off : volumeRow.iconMuteRes : (!this.mShowLowMediaVolumeIcon || streamState.level * 2 >= streamState.levelMax + streamState.levelMin) ? volumeRow.iconRes : R$drawable.ic_volume_media_low;
            }
            volumeRow2.setIcon(i, this.mContext.getTheme());
            if (i == R$drawable.ic_volume_ringer_vibrate) {
                i8 = 3;
            } else if (!(i == R$drawable.ic_volume_media_bt_mute || i == volumeRow.iconMuteRes)) {
                i8 = (i == R$drawable.ic_volume_media_bt || i == volumeRow.iconRes || i == R$drawable.ic_volume_media_low) ? 1 : 0;
            }
            int unused = volumeRow2.iconState = i8;
            ImageButton imageButton = volumeRow2.icon;
            if (imageButton != null) {
                if (!z12) {
                    imageButton.setContentDescription(getStreamLabelH(streamState));
                } else if (z3) {
                    if (z7) {
                        imageButton.setContentDescription(this.mContext.getString(R$string.volume_stream_content_description_unmute, new Object[]{getStreamLabelH(streamState)}));
                    } else if (this.mController.hasVibrator()) {
                        ImageButton imageButton2 = volumeRow2.icon;
                        Context context = this.mContext;
                        if (this.mShowA11yStream) {
                            i4 = R$string.volume_stream_content_description_vibrate_a11y;
                        } else {
                            i4 = R$string.volume_stream_content_description_vibrate;
                        }
                        imageButton2.setContentDescription(context.getString(i4, new Object[]{getStreamLabelH(streamState)}));
                    } else {
                        ImageButton imageButton3 = volumeRow2.icon;
                        Context context2 = this.mContext;
                        if (this.mShowA11yStream) {
                            i3 = R$string.volume_stream_content_description_mute_a11y;
                        } else {
                            i3 = R$string.volume_stream_content_description_mute;
                        }
                        imageButton3.setContentDescription(context2.getString(i3, new Object[]{getStreamLabelH(streamState)}));
                    }
                } else if (z2) {
                    imageButton.setContentDescription(getStreamLabelH(streamState));
                } else if (streamState.muted || (this.mAutomute && streamState.level == 0)) {
                    imageButton.setContentDescription(this.mContext.getString(R$string.volume_stream_content_description_unmute, new Object[]{getStreamLabelH(streamState)}));
                } else {
                    Context context3 = this.mContext;
                    if (this.mShowA11yStream) {
                        i2 = R$string.volume_stream_content_description_mute_a11y;
                    } else {
                        i2 = R$string.volume_stream_content_description_mute;
                    }
                    imageButton.setContentDescription(context3.getString(i2, new Object[]{getStreamLabelH(streamState)}));
                }
            }
            if (z) {
                volumeRow2.tracking = false;
            }
            enableVolumeRowViewsH(volumeRow2, !z);
            boolean z13 = !z;
            VolumeDialogController.StreamState streamState2 = volumeRow2.f144ss;
            if (!streamState2.muted || z3 || z) {
                i7 = streamState2.level;
            }
            updateVolumeRowSliderH(volumeRow2, z13, i7);
            TextView textView = volumeRow2.number;
            if (textView != null) {
                textView.setText(Integer.toString(i7));
            }
        }
    }

    private boolean isStreamMuted(VolumeDialogController.StreamState streamState) {
        return (this.mAutomute && streamState.level == 0) || streamState.muted;
    }

    private void updateVolumeRowTintH(VolumeRow volumeRow, boolean z) {
        ColorStateList colorStateList;
        int i;
        if (z) {
            volumeRow.slider.requestFocus();
        }
        boolean z2 = z && volumeRow.slider.isEnabled();
        if (z2 || this.mChangeVolumeRowTintWhenInactive) {
            if (z2) {
                colorStateList = Utils.getColorAccent(this.mContext);
            } else {
                colorStateList = Utils.getColorAttr(this.mContext, 17956903);
            }
            if (z2) {
                i = Color.alpha(colorStateList.getDefaultColor());
            } else {
                i = getAlphaAttr(16844115);
            }
            ColorStateList colorAttr = Utils.getColorAttr(this.mContext, 16844002);
            ColorStateList colorAttr2 = Utils.getColorAttr(this.mContext, 17957104);
            volumeRow.sliderProgressSolid.setTintList(colorStateList);
            AlphaTintDrawableWrapper alphaTintDrawableWrapper = volumeRow.sliderBgIcon;
            if (alphaTintDrawableWrapper != null) {
                alphaTintDrawableWrapper.setTintList(colorStateList);
            }
            if (volumeRow.sliderBgSolid != null) {
                volumeRow.sliderBgSolid.setTintList(colorAttr);
            }
            AlphaTintDrawableWrapper alphaTintDrawableWrapper2 = volumeRow.sliderProgressIcon;
            if (alphaTintDrawableWrapper2 != null) {
                alphaTintDrawableWrapper2.setTintList(colorAttr);
            }
            ImageButton imageButton = volumeRow.icon;
            if (imageButton != null) {
                imageButton.setImageTintList(colorAttr2);
                volumeRow.icon.setImageAlpha(i);
            }
            TextView textView = volumeRow.number;
            if (textView != null) {
                textView.setTextColor(colorStateList);
                volumeRow.number.setAlpha((float) i);
            }
        }
    }

    private void updateVolumeRowSliderH(VolumeRow volumeRow, boolean z, int i) {
        volumeRow.slider.setEnabled(z);
        updateVolumeRowTintH(volumeRow, volumeRow.stream == this.mActiveStream);
        if (!volumeRow.tracking) {
            int progress = volumeRow.slider.getProgress();
            int impliedLevel = getImpliedLevel(volumeRow.slider, progress);
            boolean z2 = volumeRow.view.getVisibility() == 0;
            boolean z3 = SystemClock.uptimeMillis() - volumeRow.userAttempt < 1000;
            this.mHandler.removeMessages(3, volumeRow);
            boolean z4 = this.mShowing;
            if (z4 && z2 && z3) {
                if (C2129D.BUG) {
                    String str = TAG;
                    Log.d(str, "updateVolumeRowSliderH s=" + volumeRow.stream + ", inGracePeriod, ignore....");
                }
                C2173H h = this.mHandler;
                h.sendMessageAtTime(h.obtainMessage(3, volumeRow), volumeRow.userAttempt + 1000);
            } else if (i != impliedLevel || !z4 || !z2) {
                int i2 = i * 100;
                if (!DEBUG_MULTISOUND) {
                    TextView textView = volumeRow.detailView;
                    if (textView != null) {
                        textView.setVisibility(4);
                    }
                } else if (volumeRow.detailView != null) {
                    volumeRow.detailView.setText(NumberFormat.getNumberInstance().format((long) progress) + "/" + getImpliedLevel(volumeRow.slider, progress));
                    volumeRow.detailView.setVisibility(0);
                }
                if (progress == i2) {
                    return;
                }
                if (!this.mShowing || !z2) {
                    ObjectAnimator objectAnimator = volumeRow.anim;
                    if (objectAnimator != null) {
                        objectAnimator.cancel();
                    }
                    if (C2129D.BUG) {
                        String str2 = TAG;
                        Log.i(str2, "updateVolumeRowSliderH s=" + volumeRow.stream + ", setProgress.....");
                    }
                    volumeRow.slider.setProgress(i2, true);
                    return;
                }
                if (C2129D.BUG) {
                    String str3 = TAG;
                    Log.i(str3, "updateVolumeRowSliderH s=" + volumeRow.stream + ", animate.....");
                }
                ObjectAnimator objectAnimator2 = volumeRow.anim;
                if (objectAnimator2 == null || !objectAnimator2.isRunning() || volumeRow.animTargetProgress != i2) {
                    ObjectAnimator objectAnimator3 = volumeRow.anim;
                    if (objectAnimator3 == null) {
                        ObjectAnimator ofInt = ObjectAnimator.ofInt(volumeRow.slider, "progress", new int[]{progress, i2});
                        volumeRow.anim = ofInt;
                        ofInt.setInterpolator(new DecelerateInterpolator());
                    } else {
                        objectAnimator3.cancel();
                        if (Math.abs(i2 - progress) >= 200) {
                            int i3 = i2 > progress ? i2 - 100 : i2 + 100;
                            volumeRow.slider.setProgress(i3, false);
                            volumeRow.anim.setIntValues(new int[]{i3, i2});
                        } else {
                            volumeRow.anim.setIntValues(new int[]{progress, i2});
                        }
                    }
                    volumeRow.animTargetProgress = i2;
                    volumeRow.anim.setDuration(80);
                    volumeRow.anim.start();
                }
            } else if (C2129D.BUG) {
                String str4 = TAG;
                Log.d(str4, "updateVolumeRowSliderH, don't clamp if visible, s=" + volumeRow.stream);
            }
        } else if (C2129D.BUG) {
            String str5 = TAG;
            Log.i(str5, "updateVolumeRowSliderH s=" + volumeRow.stream + ", ignore.....");
        }
    }

    /* access modifiers changed from: private */
    public void recheckH(VolumeRow volumeRow) {
        if (volumeRow == null) {
            if (C2129D.BUG) {
                Log.d(TAG, "recheckH ALL");
            }
            trimObsoleteH();
            for (VolumeRow updateVolumeRowH : this.mRows) {
                updateVolumeRowH(updateVolumeRowH);
            }
            if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume()) {
                for (AppVolumeRow updateAppVolumeRowH : this.mAppVolumeRows) {
                    updateAppVolumeRowH(updateAppVolumeRowH);
                }
                if (C2129D.BUG) {
                    dumpAppVolumeRows(this.mAppVolumeRows);
                    return;
                }
                return;
            }
            return;
        }
        if (C2129D.BUG) {
            String str = TAG;
            Log.d(str, "recheckH " + volumeRow.stream);
        }
        updateVolumeRowH(volumeRow);
    }

    /* access modifiers changed from: private */
    public void setStreamImportantH(int i, boolean z) {
        for (VolumeRow next : this.mRows) {
            if (next.stream == i) {
                boolean unused = next.important = z;
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0036, code lost:
        recheckH((com.android.systemui.volume.VolumeDialogImpl.VolumeRow) null);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showSafetyWarningH(int r4) {
        /*
            r3 = this;
            r4 = r4 & 1025(0x401, float:1.436E-42)
            if (r4 != 0) goto L_0x0008
            boolean r4 = r3.mShowing
            if (r4 == 0) goto L_0x003a
        L_0x0008:
            java.lang.Object r4 = r3.mSafetyWarningLock
            monitor-enter(r4)
            com.android.systemui.volume.SafetyWarningDialog r0 = r3.mSafetyWarning     // Catch:{ all -> 0x003e }
            if (r0 == 0) goto L_0x0011
            monitor-exit(r4)     // Catch:{ all -> 0x003e }
            return
        L_0x0011:
            com.android.systemui.volume.VolumeDialogImpl$5 r0 = new com.android.systemui.volume.VolumeDialogImpl$5     // Catch:{ all -> 0x003e }
            android.content.Context r1 = r3.mContext     // Catch:{ all -> 0x003e }
            com.android.systemui.plugins.VolumeDialogController r2 = r3.mController     // Catch:{ all -> 0x003e }
            android.media.AudioManager r2 = r2.getAudioManager()     // Catch:{ all -> 0x003e }
            r0.<init>(r1, r2)     // Catch:{ all -> 0x003e }
            r3.mSafetyWarning = r0     // Catch:{ all -> 0x003e }
            r0.show()     // Catch:{ all -> 0x003e }
            android.content.Context r0 = r3.mContext     // Catch:{ all -> 0x003e }
            com.android.systemui.moto.MotoFeature r0 = com.android.systemui.moto.MotoFeature.getInstance(r0)     // Catch:{ all -> 0x003e }
            boolean r0 = r0.isSupportRelativeVolume()     // Catch:{ all -> 0x003e }
            if (r0 == 0) goto L_0x0035
            com.android.systemui.plugins.VolumeDialogController r0 = r3.mController     // Catch:{ all -> 0x003e }
            r1 = 1
            r0.handleSafeMediaVolume(r1)     // Catch:{ all -> 0x003e }
        L_0x0035:
            monitor-exit(r4)     // Catch:{ all -> 0x003e }
            r4 = 0
            r3.recheckH(r4)
        L_0x003a:
            r3.rescheduleTimeoutH()
            return
        L_0x003e:
            r3 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x003e }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.volume.VolumeDialogImpl.showSafetyWarningH(int):void");
    }

    private String getStreamLabelH(VolumeDialogController.StreamState streamState) {
        if (streamState == null) {
            return "";
        }
        String str = streamState.remoteLabel;
        if (str != null) {
            return str;
        }
        try {
            return this.mContext.getResources().getString(streamState.name);
        } catch (Resources.NotFoundException unused) {
            String str2 = TAG;
            Slog.e(str2, "Can't find translation for stream " + streamState);
            return "";
        }
    }

    private Runnable getSinglePressFor(ImageButton imageButton) {
        return new VolumeDialogImpl$$ExternalSyntheticLambda23(this, imageButton);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getSinglePressFor$22(ImageButton imageButton) {
        if (imageButton != null) {
            imageButton.setPressed(true);
            imageButton.postOnAnimationDelayed(getSingleUnpressFor(imageButton), 200);
        }
    }

    private Runnable getSingleUnpressFor(ImageButton imageButton) {
        return new VolumeDialogImpl$$ExternalSyntheticLambda13(imageButton);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$getSingleUnpressFor$23(ImageButton imageButton) {
        if (imageButton != null) {
            imageButton.setPressed(false);
        }
    }

    private int getRingerDrawerOpenExtraSize() {
        return (this.mRingerCount - 1) * this.mRingerDrawerItemSize;
    }

    private void updateBackgroundForDrawerClosedAmount() {
        Drawable drawable = this.mRingerAndDrawerContainerBackground;
        if (drawable != null) {
            Rect copyBounds = drawable.copyBounds();
            if (!isLandscape()) {
                copyBounds.top = (int) (this.mRingerDrawerClosedAmount * ((float) getRingerDrawerOpenExtraSize()));
            } else {
                copyBounds.left = (int) (this.mRingerDrawerClosedAmount * ((float) getRingerDrawerOpenExtraSize()));
            }
            this.mRingerAndDrawerContainerBackground.setBounds(copyBounds);
        }
    }

    private void setTopContainerBackgroundDrawable() {
        int i;
        int i2;
        if (this.mTopContainer != null) {
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{new ColorDrawable(Utils.getColorAttrDefaultColor(this.mContext, 17956910))});
            int i3 = this.mDialogWidth;
            if (!isLandscape()) {
                i = this.mDialogRowsView.getHeight();
            } else {
                i = this.mDialogRowsView.getHeight() + this.mDialogCornerRadius;
            }
            layerDrawable.setLayerSize(0, i3, i);
            if (!isLandscape()) {
                i2 = this.mDialogRowsViewContainer.getTop();
            } else {
                i2 = this.mDialogRowsViewContainer.getTop() - this.mDialogCornerRadius;
            }
            layerDrawable.setLayerInsetTop(0, i2);
            layerDrawable.setLayerGravity(0, 53);
            if (isLandscape()) {
                this.mRingerAndDrawerContainer.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) VolumeDialogImpl.this.mDialogCornerRadius);
                    }
                });
                this.mRingerAndDrawerContainer.setClipToOutline(true);
            }
            this.mTopContainer.setBackground(layerDrawable);
        }
    }

    /* renamed from: com.android.systemui.volume.VolumeDialogImpl$H */
    private final class C2173H extends Handler {
        public C2173H() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    VolumeDialogImpl.this.showH(message.arg1);
                    return;
                case 2:
                    VolumeDialogImpl.this.dismissH(message.arg1);
                    return;
                case 3:
                    VolumeDialogImpl.this.recheckH((VolumeRow) message.obj);
                    return;
                case 4:
                    VolumeDialogImpl.this.recheckH((VolumeRow) null);
                    return;
                case 5:
                    VolumeDialogImpl.this.setStreamImportantH(message.arg1, message.arg2 != 0);
                    return;
                case 6:
                    VolumeDialogImpl.this.rescheduleTimeoutH();
                    return;
                case 7:
                    VolumeDialogImpl volumeDialogImpl = VolumeDialogImpl.this;
                    volumeDialogImpl.onStateChangedH(volumeDialogImpl.mState);
                    return;
                case 8:
                    VolumeDialogImpl.this.recheckAppVolumeH((AppVolumeRow) message.obj);
                    return;
                default:
                    return;
            }
        }
    }

    private final class CustomDialog extends Dialog {
        public CustomDialog(Context context) {
            super(context, R$style.volume_dialog_theme);
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            VolumeDialogImpl.this.rescheduleTimeoutH();
            return super.dispatchTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            super.setCanceledOnTouchOutside(true);
            super.onStart();
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            super.onStop();
            VolumeDialogImpl.this.mHandler.sendEmptyMessage(4);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!VolumeDialogImpl.this.mShowing || motionEvent.getAction() != 4) {
                return false;
            }
            VolumeDialogImpl.this.dismissH(1);
            return true;
        }
    }

    private final class VolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private final VolumeRow mRow;

        private VolumeSeekBarChangeListener(VolumeRow volumeRow) {
            this.mRow = volumeRow;
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            int i2;
            if (this.mRow.f144ss != null) {
                if (C2129D.BUG) {
                    String access$3600 = VolumeDialogImpl.TAG;
                    Log.d(access$3600, AudioSystem.streamToString(this.mRow.stream) + " onProgressChanged " + i + " fromUser=" + z);
                }
                if (!VolumeDialogImpl.DEBUG_MULTISOUND) {
                    TextView textView = this.mRow.detailView;
                    if (textView != null) {
                        textView.setVisibility(4);
                    }
                } else if (this.mRow.detailView != null) {
                    this.mRow.detailView.setText(NumberFormat.getNumberInstance().format((long) i) + "/" + VolumeDialogImpl.getImpliedLevel(seekBar, i));
                    this.mRow.detailView.setVisibility(0);
                }
                if (z) {
                    int i3 = this.mRow.f144ss.levelMin;
                    if (i3 > 0 && i < (i2 = i3 * 100)) {
                        seekBar.setProgress(i2);
                        i = i2;
                    }
                    int access$5000 = VolumeDialogImpl.getImpliedLevel(seekBar, i);
                    if (C2129D.BUG) {
                        String access$36002 = VolumeDialogImpl.TAG;
                        Log.d(access$36002, AudioSystem.streamToString(this.mRow.stream) + " userLevel= " + access$5000 + " mRow.ss.level= " + this.mRow.f144ss.level + " mRow.requestedLevel= " + this.mRow.requestedLevel);
                    }
                    VolumeRow volumeRow = this.mRow;
                    VolumeDialogController.StreamState streamState = volumeRow.f144ss;
                    if (streamState.level != access$5000 || (streamState.muted && access$5000 > 0)) {
                        volumeRow.userAttempt = SystemClock.uptimeMillis();
                        if (this.mRow.requestedLevel != access$5000) {
                            VolumeDialogImpl.this.mController.setActiveStream(this.mRow.stream);
                            VolumeDialogImpl.this.mController.setStreamVolume(this.mRow.stream, access$5000);
                            VolumeRow volumeRow2 = this.mRow;
                            volumeRow2.requestedLevel = access$5000;
                            Events.writeEvent(9, Integer.valueOf(volumeRow2.stream), Integer.valueOf(access$5000));
                        }
                    }
                    if (MotoFeature.getInstance(VolumeDialogImpl.this.mContext).isSupportRelativeVolume() && this.mRow.stream == 3) {
                        VolumeDialogImpl.this.mController.changeMusicRow(i, -1.0d);
                    }
                }
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            if (C2129D.BUG) {
                String access$3600 = VolumeDialogImpl.TAG;
                Log.d(access$3600, "onStartTrackingTouch " + this.mRow.stream);
            }
            VolumeDialogImpl.this.mController.setActiveStream(this.mRow.stream);
            this.mRow.tracking = true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            if (C2129D.BUG) {
                String access$3600 = VolumeDialogImpl.TAG;
                Log.d(access$3600, "onStopTrackingTouch " + this.mRow.stream);
            }
            VolumeRow volumeRow = this.mRow;
            volumeRow.tracking = false;
            volumeRow.userAttempt = SystemClock.uptimeMillis();
            int access$5000 = VolumeDialogImpl.getImpliedLevel(seekBar, seekBar.getProgress());
            Events.writeEvent(16, Integer.valueOf(this.mRow.stream), Integer.valueOf(access$5000));
            if (this.mRow.f144ss.level != access$5000) {
                VolumeDialogImpl.this.mHandler.sendMessageDelayed(VolumeDialogImpl.this.mHandler.obtainMessage(3, this.mRow), 1000);
            }
        }
    }

    private final class Accessibility extends View.AccessibilityDelegate {
        private Accessibility() {
        }

        public void init() {
            VolumeDialogImpl.this.mDialogView.setAccessibilityDelegate(this);
        }

        public boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.getText().add(VolumeDialogImpl.this.composeWindowTitle());
            return true;
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
            VolumeDialogImpl.this.rescheduleTimeoutH();
            return super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }
    }

    private static class VolumeRow {
        ObjectAnimator anim;
        int animTargetProgress;
        /* access modifiers changed from: private */
        public boolean defaultStream;
        TextView detailView;
        /* access modifiers changed from: private */
        public FrameLayout dndIcon;
        TextView header;
        ImageButton icon;
        /* access modifiers changed from: private */
        public int iconMuteRes;
        /* access modifiers changed from: private */
        public int iconRes;
        /* access modifiers changed from: private */
        public int iconState;
        /* access modifiers changed from: private */
        public boolean important;
        int lastAudibleLevel;
        TextView number;
        int requestedLevel;
        SeekBar slider;
        AlphaTintDrawableWrapper sliderBgIcon;
        /* access modifiers changed from: private */
        public Drawable sliderBgSolid;
        protected View sliderFrame;
        AlphaTintDrawableWrapper sliderProgressIcon;
        /* access modifiers changed from: private */
        public Drawable sliderProgressSolid;

        /* renamed from: ss */
        VolumeDialogController.StreamState f144ss;
        int stream;
        boolean tracking;
        long userAttempt;
        View view;

        private VolumeRow() {
            this.requestedLevel = -1;
            this.lastAudibleLevel = 1;
        }

        /* access modifiers changed from: package-private */
        public void setIcon(int i, Resources.Theme theme) {
            try {
                this.view.getResources().getResourceName(i);
                ImageButton imageButton = this.icon;
                if (imageButton != null) {
                    imageButton.setImageResource(i);
                }
                AlphaTintDrawableWrapper alphaTintDrawableWrapper = this.sliderProgressIcon;
                if (alphaTintDrawableWrapper != null) {
                    alphaTintDrawableWrapper.setDrawable(this.view.getResources().getDrawable(i, theme));
                }
                AlphaTintDrawableWrapper alphaTintDrawableWrapper2 = this.sliderBgIcon;
                if (alphaTintDrawableWrapper2 != null) {
                    alphaTintDrawableWrapper2.setDrawable(this.view.getResources().getDrawable(i, theme));
                }
            } catch (Resources.NotFoundException unused) {
                if (C2129D.BUG) {
                    Log.d(VolumeDialogImpl.TAG + ".dv", "iconRes: " + i + ", ignore invalid iconRes");
                }
            }
        }
    }

    private class RingerDrawerItemClickListener implements View.OnClickListener {
        private final int mClickedRingerMode;

        RingerDrawerItemClickListener(int i) {
            this.mClickedRingerMode = i;
        }

        public void onClick(View view) {
            if (VolumeDialogImpl.this.mIsRingerDrawerOpen) {
                VolumeDialogImpl.this.setRingerMode(this.mClickedRingerMode);
                VolumeDialogImpl volumeDialogImpl = VolumeDialogImpl.this;
                ImageView unused = volumeDialogImpl.mRingerDrawerIconAnimatingSelected = volumeDialogImpl.getDrawerIconViewForMode(this.mClickedRingerMode);
                VolumeDialogImpl volumeDialogImpl2 = VolumeDialogImpl.this;
                ImageView unused2 = volumeDialogImpl2.mRingerDrawerIconAnimatingDeselected = volumeDialogImpl2.getDrawerIconViewForMode(volumeDialogImpl2.mState.ringerModeInternal);
                VolumeDialogImpl.this.mRingerDrawerIconColorAnimator.start();
                VolumeDialogImpl.this.mSelectedRingerContainer.setVisibility(4);
                VolumeDialogImpl.this.mRingerDrawerNewSelectionBg.setAlpha(1.0f);
                VolumeDialogImpl.this.mRingerDrawerNewSelectionBg.animate().setInterpolator(Interpolators.ACCELERATE_DECELERATE).setDuration(175).withEndAction(new C2174xfb02e228(this));
                if (!VolumeDialogImpl.this.isLandscape()) {
                    VolumeDialogImpl.this.mRingerDrawerNewSelectionBg.animate().translationY(VolumeDialogImpl.this.getTranslationInDrawerForRingerMode(this.mClickedRingerMode)).start();
                } else {
                    VolumeDialogImpl.this.mRingerDrawerNewSelectionBg.animate().translationX(VolumeDialogImpl.this.getTranslationInDrawerForRingerMode(this.mClickedRingerMode)).start();
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$0() {
            VolumeDialogImpl.this.mRingerDrawerNewSelectionBg.setAlpha(0.0f);
            if (!VolumeDialogImpl.this.isLandscape()) {
                VolumeDialogImpl.this.mSelectedRingerContainer.setTranslationY(VolumeDialogImpl.this.getTranslationInDrawerForRingerMode(this.mClickedRingerMode));
            } else {
                VolumeDialogImpl.this.mSelectedRingerContainer.setTranslationX(VolumeDialogImpl.this.getTranslationInDrawerForRingerMode(this.mClickedRingerMode));
            }
            VolumeDialogImpl.this.mSelectedRingerContainer.setVisibility(0);
            VolumeDialogImpl.this.hideRingerDrawer();
        }
    }

    public static class AppVolumeRow extends VolumeRow {
        public AppVolumeState appVolumeState;
        public boolean forceToShow;
        public Drawable iconDrawable;
        public String label;
        public String packageName;
        public Drawable settingIconDrawable;
        public int uid;

        public AppVolumeRow() {
            super();
        }

        public String getSummary() {
            return "AppVolumeRow label: " + this.appVolumeState.label + ", uid: " + this.uid + ", packageName: " + this.packageName + ", playing: " + this.appVolumeState.playing + ", foreground: " + this.appVolumeState.foreground + ", foregroundSettings: " + this.appVolumeState.foregroundSettings + ", active: " + this.appVolumeState.active + ", shouldBeVisible: " + this.appVolumeState.shouldBeVisible + ", forceToShow: " + this.forceToShow + ", appVolumeState.forceToShow: " + this.appVolumeState.forceToShow + ", progress: " + this.appVolumeState.progress + ", appLevel: " + this.appVolumeState.appLevel + ", storedPercentage: " + this.appVolumeState.storedPercentage + ", requestedLevel: " + this.requestedLevel + ", timeInMills: " + this.appVolumeState.timeInMills;
        }

        /* access modifiers changed from: package-private */
        public void setIcon(Drawable drawable) {
            if (drawable != null) {
                ImageButton imageButton = this.icon;
                if (imageButton != null) {
                    imageButton.setImageDrawable(drawable);
                }
                AlphaTintDrawableWrapper alphaTintDrawableWrapper = this.sliderProgressIcon;
                if (alphaTintDrawableWrapper != null) {
                    alphaTintDrawableWrapper.setDrawable(drawable);
                }
                AlphaTintDrawableWrapper alphaTintDrawableWrapper2 = this.sliderBgIcon;
                if (alphaTintDrawableWrapper2 != null) {
                    alphaTintDrawableWrapper2.setDrawable(drawable);
                }
            } else if (C2129D.BUG) {
                Log.d(VolumeDialogImpl.TAG + ".dv", "Drawable is null, ignore invalid Drawable");
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean updateAppRowsH() {
        boolean z;
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "updateAppRowsH");
        }
        boolean z2 = false;
        if (this.mState == null) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "mState is null");
            }
            return false;
        }
        boolean z3 = false;
        boolean z4 = false;
        for (int i = 0; i < this.mState.appVolumeStates.size(); i++) {
            AppVolumeRow findAppRow = findAppRow(this.mState.appVolumeStates.keyAt(i));
            if (findAppRow == null) {
                AppVolumeRow appVolumeRow = new AppVolumeRow();
                initAppVolumeRow(appVolumeRow, this.mState.appVolumeStates.valueAt(i));
                this.mAppVolumeRows.add(appVolumeRow);
                z3 = true;
            } else if (this.mDialogRowsView.findViewById(findAppRow.view.getId()) == null) {
                if (C2129D.BUG) {
                    Log.d(TAG + ".dv", "updateAppRowsH, row: " + findAppRow.packageName + "'s view is not in DialogRowsView");
                }
                z4 = true;
            }
        }
        ArrayList arrayList = new ArrayList();
        for (AppVolumeRow add : this.mAppVolumeRows) {
            arrayList.add(add);
        }
        if (isRtl()) {
            Collections.sort(this.mAppVolumeRows, this.comparator_rtl);
        } else {
            Collections.sort(this.mAppVolumeRows, this.comparator_ltr);
        }
        int size = this.mAppVolumeRows.size();
        int i2 = 0;
        while (true) {
            if (i2 >= size) {
                z = false;
                break;
            } else if (this.mAppVolumeRows.get(i2).view.getId() != ((AppVolumeRow) arrayList.get(i2)).view.getId()) {
                if (C2129D.BUG) {
                    Log.d(TAG + ".dv", "App Volume order is changed");
                }
                z = true;
            } else {
                i2++;
            }
        }
        if (isRtl()) {
            for (AppVolumeRow next : this.mAppVolumeRows) {
                if (z && this.mDialogRowsView.findViewById(next.view.getId()) != null) {
                    this.mDialogRowsView.removeView(next.view);
                    z2 = true;
                }
                if (this.mDialogRowsView.findViewById(next.view.getId()) == null) {
                    this.mDialogRowsView.addView(next.view);
                    z2 = true;
                }
            }
        } else if (z3 || z || z4) {
            for (AppVolumeRow next2 : this.mAppVolumeRows) {
                if (this.mDialogRowsView.findViewById(next2.view.getId()) != null) {
                    this.mDialogRowsView.removeView(next2.view);
                }
                if (this.mDialogRowsView.findViewById(next2.view.getId()) == null) {
                    this.mDialogRowsView.addView(next2.view);
                }
            }
            for (VolumeRow next3 : this.mRows) {
                if (this.mDialogRowsView.findViewById(next3.view.getId()) != null) {
                    this.mDialogRowsView.removeView(next3.view);
                }
                if (this.mDialogRowsView.findViewById(next3.view.getId()) == null) {
                    this.mDialogRowsView.addView(next3.view);
                }
            }
            z2 = true;
        }
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "updateAppRowsH, isRtl(): " + isRtl() + ", dialogRowsViewChanged: " + z2 + ", orderChange: " + z + ", itemAdd: " + z3 + ", viewMissing: " + z4);
        }
        return z2;
    }

    /* access modifiers changed from: private */
    public AppVolumeRow findAppRow(int i) {
        for (AppVolumeRow next : this.mAppVolumeRows) {
            if (next.uid == i) {
                return next;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void dumpAppVolumeRows(List<AppVolumeRow> list) {
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "App Volume Rows: [");
        }
        for (AppVolumeRow next : list) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", next.getSummary());
            }
        }
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "]");
        }
    }

    private void resetAppVolumeRows(List<AppVolumeRow> list) {
        for (AppVolumeRow appVolumeRow : list) {
            appVolumeRow.forceToShow = false;
        }
    }

    private void initAppVolumeRows() {
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "initAppVolumeRows");
        }
        if (!this.mAppVolumeRows.isEmpty()) {
            for (AppVolumeRow next : this.mAppVolumeRows) {
                initAppVolumeRow(next, next.appVolumeState);
            }
        } else if (C2129D.BUG) {
            Log.d(TAG + ".dv", "mAppVolumeRows is empty");
        }
        ImageButton imageButton = (ImageButton) this.mDialog.findViewById(R$id.app_volume);
        this.mAppVolumeIcon = imageButton;
        if (imageButton != null) {
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.mAppVolumeIcon.setImageTintMode(PorterDuff.Mode.DST);
            int dip2px = dip2px(this.mContext, (float) this.APP_ICON_PADDING);
            this.mAppVolumeIcon.setPadding(dip2px, dip2px, dip2px, dip2px);
            this.mAppVolumeIcon.setOnClickListener(new VolumeDialogImpl$$ExternalSyntheticLambda7(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initAppVolumeRows$24(View view) {
        AppVolumeRow onlyOneVisibleAndSameWithMusicRow = getOnlyOneVisibleAndSameWithMusicRow();
        if (C2129D.BUG) {
            String str = TAG + ".dv";
            StringBuilder sb = new StringBuilder();
            sb.append("updateAppVolumeIconH , onClick, row: ");
            sb.append(onlyOneVisibleAndSameWithMusicRow != null ? onlyOneVisibleAndSameWithMusicRow.packageName : "null");
            sb.append(", mAppVolumeIcon: ");
            sb.append(this.mAppVolumeIcon.getId());
            Log.d(str, sb.toString());
        }
        if (onlyOneVisibleAndSameWithMusicRow != null) {
            onlyOneVisibleAndSameWithMusicRow.forceToShow = true;
        }
        updateAppVolumeIconH();
        updateAppVolumeRowH(onlyOneVisibleAndSameWithMusicRow);
    }

    private int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private void initAppVolumeRow(AppVolumeRow appVolumeRow, AppVolumeState appVolumeState) {
        appVolumeRow.uid = appVolumeState.packageUid;
        appVolumeRow.packageName = appVolumeState.packageName;
        PackageManager packageManager = this.mContext.getPackageManager();
        ApplicationInfo applicationForUid = getApplicationForUid(packageManager, appVolumeRow.uid);
        appVolumeRow.iconDrawable = getIcon(this.mContext, applicationForUid, packageManager, appVolumeRow.uid);
        appVolumeRow.settingIconDrawable = getIcon(this.mContext, applicationForUid, packageManager, appVolumeRow.uid);
        appVolumeRow.label = appVolumeState.label;
        appVolumeRow.appVolumeState = appVolumeState;
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "initAppVolumeRow, row.packageName: " + appVolumeRow.packageName + ", row.appLevel:" + appVolumeRow.appVolumeState.appLevel);
        }
        appVolumeRow.stream = 3;
        initRow(appVolumeRow, 3, -1, -1, true, true);
        VolumeDialogController.State state = this.mState;
        if (state != null) {
            appVolumeRow.f144ss = state.states.get(appVolumeRow.stream);
        }
        appVolumeRow.view.setId(appVolumeRow.uid);
        appVolumeRow.view.setTag(appVolumeRow);
        Util.setText(appVolumeRow.header, appVolumeRow.label);
        appVolumeRow.slider.setOnSeekBarChangeListener(new AppVolumeSeekBarChangeListener(appVolumeRow));
        appVolumeRow.slider.setContentDescription(appVolumeRow.header.getText());
        appVolumeRow.setIcon(appVolumeRow.iconDrawable);
        appVolumeRow.icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        appVolumeRow.icon.setImageTintMode(PorterDuff.Mode.DST);
        int dip2px = dip2px(this.mContext, (float) this.APP_ICON_PADDING);
        appVolumeRow.icon.setPadding(dip2px, dip2px, dip2px, dip2px);
        appVolumeRow.icon.setOnClickListener(new VolumeDialogImpl$$ExternalSyntheticLambda9(this, appVolumeRow));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initAppVolumeRow$25(AppVolumeRow appVolumeRow, View view) {
        AppVolumeRow appVolumeRow2 = appVolumeRow;
        this.mController.setActiveStream(appVolumeRow2.stream);
        appVolumeRow2.userAttempt = 0;
        int i = appVolumeRow2.appVolumeState.appLevel;
        int i2 = appVolumeRow2.f144ss.levelMin;
        if (i == i2) {
            int progress = findRow(3).slider.getProgress();
            int i3 = appVolumeRow2.lastAudibleLevel;
            if (i3 * 100 < progress) {
                progress = i3 * 100;
            }
            this.mController.changeAppRow(appVolumeRow2.packageName, appVolumeRow2.uid, progress, -1.0d, TouchType.START_TOUCH.ordinal());
            return;
        }
        this.mController.changeAppRow(appVolumeRow2.packageName, appVolumeRow2.uid, i2 * 100, -1.0d, TouchType.START_TOUCH.ordinal());
    }

    private final class AppVolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private final AppVolumeRow mRow;
        private TouchType touchType;

        private AppVolumeSeekBarChangeListener(AppVolumeRow appVolumeRow) {
            this.mRow = appVolumeRow;
            this.touchType = TouchType.END_TOUCH;
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            int i2;
            int i3;
            if (this.mRow.f144ss != null) {
                if (C2129D.BUG) {
                    Log.d(VolumeDialogImpl.TAG + ".dv", "package: " + this.mRow.packageName + " onProgressChanged " + i + " fromUser=" + z);
                }
                if (!VolumeDialogImpl.DEBUG_MULTISOUND) {
                    TextView textView = this.mRow.detailView;
                    if (textView != null) {
                        textView.setVisibility(4);
                    }
                } else if (this.mRow.detailView != null) {
                    this.mRow.detailView.setText(NumberFormat.getNumberInstance().format((long) i) + "/" + VolumeDialogImpl.getImpliedLevel(seekBar, i));
                    this.mRow.detailView.setVisibility(0);
                }
                if (z) {
                    int i4 = this.mRow.f144ss.levelMin;
                    if (i4 <= 0 || i >= (i3 = i4 * 100)) {
                        i2 = i;
                    } else {
                        seekBar.setProgress(i3);
                        i2 = i3;
                    }
                    int access$5000 = VolumeDialogImpl.getImpliedLevel(seekBar, i2);
                    if (C2129D.BUG) {
                        Log.d(VolumeDialogImpl.TAG + ".dv", "package: " + this.mRow.packageName + " userLevel= " + access$5000 + " mRow.appLevel= " + this.mRow.appVolumeState.appLevel + " mRow.requestedLevel= " + this.mRow.requestedLevel);
                    }
                    AppVolumeRow appVolumeRow = this.mRow;
                    if (!(appVolumeRow.appVolumeState.appLevel == access$5000 || appVolumeRow.requestedLevel == access$5000)) {
                        appVolumeRow.userAttempt = SystemClock.uptimeMillis();
                        VolumeDialogImpl.this.mController.setActiveStream(this.mRow.stream);
                        AppVolumeRow appVolumeRow2 = this.mRow;
                        appVolumeRow2.requestedLevel = access$5000;
                        Events.writeEvent(23, appVolumeRow2.packageName, Integer.valueOf(access$5000));
                    }
                    VolumeDialogController access$2400 = VolumeDialogImpl.this.mController;
                    AppVolumeRow appVolumeRow3 = this.mRow;
                    access$2400.changeAppRow(appVolumeRow3.packageName, appVolumeRow3.uid, i2, -1.0d, this.touchType.ordinal());
                    if (this.touchType == TouchType.START_TOUCH) {
                        this.touchType = TouchType.ON_TOUCH;
                    }
                }
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            if (C2129D.BUG) {
                Log.d(VolumeDialogImpl.TAG + ".dv", "onStartTrackingTouch, package: " + this.mRow.packageName);
            }
            VolumeDialogImpl.this.mController.setActiveStream(this.mRow.stream);
            this.mRow.tracking = true;
            this.touchType = TouchType.START_TOUCH;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            if (C2129D.BUG) {
                Log.d(VolumeDialogImpl.TAG + ".dv", "onStopTrackingTouch, package: " + this.mRow.packageName);
            }
            AppVolumeRow appVolumeRow = this.mRow;
            appVolumeRow.tracking = false;
            this.touchType = TouchType.END_TOUCH;
            appVolumeRow.userAttempt = SystemClock.uptimeMillis();
            int access$5000 = VolumeDialogImpl.getImpliedLevel(seekBar, seekBar.getProgress());
            Events.writeEvent(24, this.mRow.packageName, Integer.valueOf(access$5000));
            if (this.mRow.appVolumeState.appLevel != access$5000) {
                VolumeDialogImpl.this.mHandler.sendMessageDelayed(VolumeDialogImpl.this.mHandler.obtainMessage(8, this.mRow), 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    public AppVolumeRow getOnlyOneVisibleAndNotSameWithMusicRow() {
        int i = 0;
        int i2 = 0;
        AppVolumeRow appVolumeRow = null;
        for (AppVolumeRow next : this.mAppVolumeRows) {
            AppVolumeState appVolumeState = next.appVolumeState;
            if (appVolumeState.active && (appVolumeState.foreground || appVolumeState.playing)) {
                i++;
                if (appVolumeState.playing && appVolumeState.ratio != 1.0d) {
                    i2++;
                    appVolumeRow = next;
                }
            }
        }
        if (i == 1 && i2 == 1) {
            return appVolumeRow;
        }
        return null;
    }

    private AppVolumeRow getOnlyOneVisibleAndSameWithMusicRow() {
        int i = 0;
        int i2 = 0;
        AppVolumeRow appVolumeRow = null;
        for (AppVolumeRow next : this.mAppVolumeRows) {
            AppVolumeState appVolumeState = next.appVolumeState;
            if (appVolumeState.active && (appVolumeState.foreground || appVolumeState.playing)) {
                i++;
                if (appVolumeState.ratio == 1.0d) {
                    i2++;
                    appVolumeRow = next;
                }
            }
        }
        if (i == 1 && i2 == 1) {
            return appVolumeRow;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldAppVolumeRowBeVisibleH(AppVolumeRow appVolumeRow) {
        boolean z = (this.mShowing && appVolumeRow.appVolumeState.forceToShow) | appVolumeRow.forceToShow;
        appVolumeRow.forceToShow = z;
        if (appVolumeRow.appVolumeState.shouldBeVisible || z) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00d4  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:69:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateAppVolumeRowH(com.android.systemui.volume.VolumeDialogImpl.AppVolumeRow r9) {
        /*
            r8 = this;
            com.android.systemui.plugins.VolumeDialogController$State r0 = r8.mState
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            android.util.SparseArray<com.android.systemui.plugins.VolumeDialogController$StreamState> r0 = r0.states
            int r1 = r9.stream
            java.lang.Object r0 = r0.get(r1)
            com.android.systemui.plugins.VolumeDialogController$StreamState r0 = (com.android.systemui.plugins.VolumeDialogController.StreamState) r0
            com.android.systemui.plugins.VolumeDialogController$State r1 = r8.mState
            android.util.SparseArray<com.motorola.multivolume.AppVolumeState> r1 = r1.appVolumeStates
            int r2 = r9.uid
            java.lang.Object r1 = r1.get(r2)
            com.motorola.multivolume.AppVolumeState r1 = (com.motorola.multivolume.AppVolumeState) r1
            if (r0 == 0) goto L_0x00f8
            if (r1 != 0) goto L_0x0021
            goto L_0x00f8
        L_0x0021:
            r9.f144ss = r0
            r9.appVolumeState = r1
            boolean r1 = r8.shouldAppVolumeRowBeVisibleH(r9)
            android.view.View r2 = r9.view
            com.android.systemui.volume.Util.setVisOrGone(r2, r1)
            com.motorola.multivolume.AppVolumeState r1 = r9.appVolumeState
            int r1 = r1.appLevel
            if (r1 <= 0) goto L_0x0036
            r9.lastAudibleLevel = r1
        L_0x0036:
            int r2 = r9.requestedLevel
            if (r1 != r2) goto L_0x003d
            r1 = -1
            r9.requestedLevel = r1
        L_0x003d:
            com.android.systemui.plugins.VolumeDialogController$State r1 = r8.mState
            int r2 = r1.zenMode
            r3 = 0
            r4 = 1
            if (r2 != r4) goto L_0x0047
            r5 = r4
            goto L_0x0048
        L_0x0047:
            r5 = r3
        L_0x0048:
            r6 = 3
            if (r2 != r6) goto L_0x004d
            r6 = r4
            goto L_0x004e
        L_0x004d:
            r6 = r3
        L_0x004e:
            r7 = 2
            if (r2 != r7) goto L_0x0053
            r2 = r4
            goto L_0x0054
        L_0x0053:
            r2 = r3
        L_0x0054:
            if (r6 == 0) goto L_0x0058
        L_0x0056:
            r1 = r3
            goto L_0x0060
        L_0x0058:
            if (r2 == 0) goto L_0x005c
            r1 = r4
            goto L_0x0060
        L_0x005c:
            if (r5 == 0) goto L_0x0056
            boolean r1 = r1.disallowMedia
        L_0x0060:
            int r2 = r0.levelMax
            int r2 = r2 * 100
            android.widget.SeekBar r5 = r9.slider
            int r5 = r5.getMax()
            if (r2 == r5) goto L_0x0071
            android.widget.SeekBar r5 = r9.slider
            r5.setMax(r2)
        L_0x0071:
            int r2 = r0.levelMin
            int r2 = r2 * 100
            android.widget.SeekBar r5 = r9.slider
            int r5 = r5.getMin()
            if (r2 == r5) goto L_0x0082
            android.widget.SeekBar r5 = r9.slider
            r5.setMin(r2)
        L_0x0082:
            boolean r2 = r8.mAutomute
            if (r2 != 0) goto L_0x008a
            boolean r5 = r0.muteSupported
            if (r5 == 0) goto L_0x008e
        L_0x008a:
            if (r1 != 0) goto L_0x008e
            r5 = r4
            goto L_0x008f
        L_0x008e:
            r5 = r3
        L_0x008f:
            android.widget.ImageButton r6 = r9.icon
            if (r6 == 0) goto L_0x00d2
            if (r5 == 0) goto L_0x00cd
            boolean r0 = r0.muted
            if (r0 != 0) goto L_0x00bb
            if (r2 == 0) goto L_0x00a2
            com.motorola.multivolume.AppVolumeState r0 = r9.appVolumeState
            int r0 = r0.appLevel
            if (r0 != 0) goto L_0x00a2
            goto L_0x00bb
        L_0x00a2:
            android.content.Context r0 = r8.mContext
            boolean r2 = r8.mShowA11yStream
            if (r2 == 0) goto L_0x00ab
            int r2 = com.android.systemui.R$string.volume_stream_content_description_mute_a11y
            goto L_0x00ad
        L_0x00ab:
            int r2 = com.android.systemui.R$string.volume_stream_content_description_mute
        L_0x00ad:
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r5 = r9.label
            r4[r3] = r5
            java.lang.String r0 = r0.getString(r2, r4)
            r6.setContentDescription(r0)
            goto L_0x00d2
        L_0x00bb:
            android.content.Context r0 = r8.mContext
            int r2 = com.android.systemui.R$string.volume_stream_content_description_unmute
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r5 = r9.label
            r4[r3] = r5
            java.lang.String r0 = r0.getString(r2, r4)
            r6.setContentDescription(r0)
            goto L_0x00d2
        L_0x00cd:
            java.lang.String r0 = r9.label
            r6.setContentDescription(r0)
        L_0x00d2:
            if (r1 == 0) goto L_0x00d6
            r9.tracking = r3
        L_0x00d6:
            r0 = r1 ^ 1
            r8.enableVolumeRowViewsH(r9, r0)
            r0 = r1 ^ 1
            com.android.systemui.plugins.VolumeDialogController$StreamState r2 = r9.f144ss
            boolean r2 = r2.muted
            if (r2 == 0) goto L_0x00e6
            if (r1 != 0) goto L_0x00e6
            goto L_0x00ea
        L_0x00e6:
            com.motorola.multivolume.AppVolumeState r1 = r9.appVolumeState
            int r3 = r1.appLevel
        L_0x00ea:
            r8.updateAppVolumeRowSliderH(r9, r0, r3)
            android.widget.TextView r8 = r9.number
            if (r8 == 0) goto L_0x00f8
            java.lang.String r9 = java.lang.Integer.toString(r3)
            r8.setText(r9)
        L_0x00f8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.volume.VolumeDialogImpl.updateAppVolumeRowH(com.android.systemui.volume.VolumeDialogImpl$AppVolumeRow):void");
    }

    private void updateAppVolumeRowSliderH(AppVolumeRow appVolumeRow, boolean z, int i) {
        if (C2129D.BUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateAppVolumeRowSliderH, package: ");
            sb.append(appVolumeRow.packageName);
            sb.append(", isPlaying: ");
            sb.append(appVolumeRow.appVolumeState.playing);
            sb.append(", foreground: ");
            sb.append(appVolumeRow.appVolumeState.foreground);
            sb.append(", active: ");
            sb.append(appVolumeRow.appVolumeState.active);
            sb.append(", shouldBeVisible: ");
            sb.append(appVolumeRow.appVolumeState.shouldBeVisible);
            sb.append(", forceToShow: ");
            sb.append(appVolumeRow.forceToShow);
            sb.append(", appVolumeState.forceToShow: ");
            sb.append(appVolumeRow.appVolumeState.forceToShow);
            sb.append(", enable: ");
            sb.append(z);
            sb.append(", progress= ");
            sb.append(appVolumeRow.slider.getProgress());
            sb.append(", newProgress= ");
            sb.append(i * 100);
            sb.append(", ss.level: ");
            sb.append(appVolumeRow.f144ss.level);
            sb.append(", getImpliedLevel= ");
            SeekBar seekBar = appVolumeRow.slider;
            sb.append(getImpliedLevel(seekBar, seekBar.getProgress()));
            sb.append(", new level= ");
            sb.append(i);
            sb.append(", appLevel: ");
            sb.append(appVolumeRow.appVolumeState.appLevel);
            sb.append(", requestedLevel: ");
            sb.append(appVolumeRow.requestedLevel);
            sb.append(", userAttempt: ");
            sb.append(appVolumeRow.userAttempt);
            Log.d(TAG + ".dv", sb.toString());
        }
        appVolumeRow.slider.setEnabled(z);
        updateVolumeRowTintH(appVolumeRow, appVolumeRow.stream == this.mActiveStream);
        if (!appVolumeRow.tracking) {
            int progress = appVolumeRow.slider.getProgress();
            int impliedLevel = getImpliedLevel(appVolumeRow.slider, progress);
            boolean z2 = appVolumeRow.view.getVisibility() == 0;
            boolean z3 = SystemClock.uptimeMillis() - appVolumeRow.userAttempt < 1000;
            this.mHandler.removeMessages(8, appVolumeRow);
            boolean z4 = this.mShowing;
            if (z4 && z2 && z3) {
                if (C2129D.BUG) {
                    Log.d(TAG + ".dv", "updateAppVolumeRowSliderH, inGracePeriod, ignore...., package= " + appVolumeRow.packageName);
                }
                C2173H h = this.mHandler;
                h.sendMessageAtTime(h.obtainMessage(8, appVolumeRow), appVolumeRow.userAttempt + 1000);
            } else if (i != impliedLevel || !z4 || !z2) {
                int i2 = i * 100;
                if (!DEBUG_MULTISOUND) {
                    TextView textView = appVolumeRow.detailView;
                    if (textView != null) {
                        textView.setVisibility(4);
                    }
                } else if (appVolumeRow.detailView != null) {
                    appVolumeRow.detailView.setText(NumberFormat.getNumberInstance().format((long) progress) + "/" + getImpliedLevel(appVolumeRow.slider, progress));
                    appVolumeRow.detailView.setVisibility(0);
                }
                if (progress != i2) {
                    if (!this.mShowing || !z2) {
                        ObjectAnimator objectAnimator = appVolumeRow.anim;
                        if (objectAnimator != null) {
                            objectAnimator.cancel();
                        }
                        if (C2129D.BUG) {
                            Log.d(TAG + ".dv", "updateAppVolumeRowSliderH, setProgress...., package= " + appVolumeRow.packageName);
                        }
                        appVolumeRow.slider.setProgress(i2, true);
                        return;
                    }
                    if (C2129D.BUG) {
                        Log.d(TAG + ".dv", "updateAppVolumeRowSliderH, animate...., package= " + appVolumeRow.packageName);
                    }
                    ObjectAnimator objectAnimator2 = appVolumeRow.anim;
                    if (objectAnimator2 == null || !objectAnimator2.isRunning() || appVolumeRow.animTargetProgress != i2) {
                        ObjectAnimator objectAnimator3 = appVolumeRow.anim;
                        if (objectAnimator3 == null) {
                            ObjectAnimator ofInt = ObjectAnimator.ofInt(appVolumeRow.slider, "progress", new int[]{progress, i2});
                            appVolumeRow.anim = ofInt;
                            ofInt.setInterpolator(new DecelerateInterpolator());
                        } else {
                            objectAnimator3.cancel();
                            appVolumeRow.anim.setIntValues(new int[]{progress, i2});
                        }
                        appVolumeRow.animTargetProgress = i2;
                        appVolumeRow.anim.setDuration(80);
                        appVolumeRow.anim.start();
                    }
                } else if (C2129D.BUG) {
                    Log.d(TAG + ".dv", "updateAppVolumeRowSliderH, remain unchanged...., package= " + appVolumeRow.packageName);
                }
            } else if (C2129D.BUG) {
                Log.d(TAG + ".dv", "updateAppVolumeRowSliderH, don't clamp if visible, package= " + appVolumeRow.packageName);
            }
        } else if (C2129D.BUG) {
            Log.d(TAG + ".dv", "updateAppVolumeRowSliderH, in tracking, ignore...., package= " + appVolumeRow.packageName);
        }
    }

    /* access modifiers changed from: private */
    public void recheckAppVolumeH(AppVolumeRow appVolumeRow) {
        if (appVolumeRow == null) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "recheckAppVolumeH ALL");
            }
            for (AppVolumeRow updateAppVolumeRowH : this.mAppVolumeRows) {
                updateAppVolumeRowH(updateAppVolumeRowH);
            }
            if (C2129D.BUG) {
                dumpAppVolumeRows(this.mAppVolumeRows);
                return;
            }
            return;
        }
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "recheckAppVolumeH " + appVolumeRow.packageName);
        }
        updateAppVolumeRowH(appVolumeRow);
    }

    /* access modifiers changed from: private */
    public void updateAppVolumeIconH() {
        if (this.mAppVolumeIcon != null) {
            if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume()) {
                AppVolumeRow onlyOneVisibleAndSameWithMusicRow = getOnlyOneVisibleAndSameWithMusicRow();
                if (C2129D.BUG) {
                    String str = TAG + ".dv";
                    StringBuilder sb = new StringBuilder();
                    sb.append("updateAppVolumeIconH, row: ");
                    sb.append(onlyOneVisibleAndSameWithMusicRow != null ? onlyOneVisibleAndSameWithMusicRow.packageName : "null");
                    sb.append(", mAppVolumeIcon: ");
                    sb.append(this.mAppVolumeIcon.getId());
                    Log.d(str, sb.toString());
                }
                if (onlyOneVisibleAndSameWithMusicRow == null || onlyOneVisibleAndSameWithMusicRow.forceToShow) {
                    if (C2129D.BUG) {
                        Log.d(TAG + ".dv", "updateAppVolumeIconH, no app row, mAppVolumeIcon set GONE");
                    }
                    this.mAppVolumeIcon.setVisibility(8);
                    return;
                }
                if (onlyOneVisibleAndSameWithMusicRow.settingIconDrawable == null) {
                    if (C2129D.BUG) {
                        Log.d(TAG + ".dv", "updateAppVolumeIconH, can not find app icon, mAppVolumeIcon set default icon");
                    }
                    this.mAppVolumeIcon.setImageResource(R$drawable.ic_volume_media);
                    this.mAppVolumeIcon.setId(0);
                } else if (this.mAppVolumeIcon.getId() != onlyOneVisibleAndSameWithMusicRow.uid) {
                    if (C2129D.BUG) {
                        Log.d(TAG + ".dv", "updateAppVolumeIconH, new app row, mAppVolumeIcon set app icon");
                    }
                    this.mAppVolumeIcon.setImageDrawable(onlyOneVisibleAndSameWithMusicRow.settingIconDrawable);
                    this.mAppVolumeIcon.setId(onlyOneVisibleAndSameWithMusicRow.uid);
                } else if (C2129D.BUG) {
                    Log.d(TAG + ".dv", "updateAppVolumeIconH, same app row, mAppVolumeIcon do nothing");
                }
                this.mAppVolumeIcon.setVisibility(0);
                return;
            }
            this.mAppVolumeIcon.setVisibility(8);
        }
    }

    private static Drawable getIcon(Context context, ApplicationInfo applicationInfo, PackageManager packageManager, int i) {
        Drawable drawable;
        if (applicationInfo == null) {
            return null;
        }
        try {
            Drawable loadIcon = applicationInfo.loadIcon(packageManager);
            if (loadIcon == null) {
                loadIcon = ContextCompat.getDrawable(context, applicationInfo.icon);
            }
            UserHandle userHandleForUid = UserHandle.getUserHandleForUid(i);
            if (loadIcon != null) {
                drawable = packageManager.getUserBadgedIcon(loadIcon, userHandleForUid);
            } else {
                drawable = context.getDrawable(R$drawable.ic_volume_media);
            }
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ApplicationInfo getApplicationForUid(PackageManager packageManager, int i) {
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "getApplicationForUid packageUid= " + i);
        }
        List installedApplicationsAsUser = packageManager.getInstalledApplicationsAsUser(0, ActivityManager.getCurrentUser());
        if (installedApplicationsAsUser == null) {
            return null;
        }
        List list = (List) installedApplicationsAsUser.stream().filter(new VolumeDialogImpl$$ExternalSyntheticLambda27(i)).collect(Collectors.toList());
        if (list.size() == 0) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "getApplicationForUid failed packageUid= " + i);
            }
            return null;
        } else if (list.size() > 1) {
            return (ApplicationInfo) list.stream().min(VolumeDialogImpl$$ExternalSyntheticLambda25.INSTANCE).get();
        } else {
            return (ApplicationInfo) list.get(0);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getApplicationForUid$26(int i, ApplicationInfo applicationInfo) {
        return applicationInfo.uid == i;
    }

    public void setTaskBarFlag() {
        WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
        attributes.gravity = 85;
        this.mWindow.setAttributes(attributes);
    }

    public void setMargin(Rect rect) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mContext.getDisplay().getRealMetrics(displayMetrics);
        WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
        attributes.x = displayMetrics.widthPixels - rect.right;
        attributes.y = displayMetrics.heightPixels - rect.top;
        this.mWindow.setAttributes(attributes);
    }

    public void switchVolumeDialog(Rect rect) {
        CustomDialog customDialog = this.mDialog;
        if (customDialog == null || !customDialog.isShowing()) {
            setMargin(rect);
            show(4);
            return;
        }
        dismiss(10);
    }
}
