package com.android.systemui.statusbar.notification.stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MathUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.ColorUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R$bool;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.moto.CarrierLabelUpdateMonitor;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.EmptyShadeView;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.ExpandAnimationParameters;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import com.android.systemui.statusbar.notification.collection.render.GroupMembershipManager;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.FooterView;
import com.android.systemui.statusbar.notification.row.ForegroundServiceDungeonView;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;
import com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayoutController;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import com.android.systemui.util.Assert;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

public class DesktopNotificationStackScrollLayout extends ViewGroup implements Dumpable {
    private boolean mActivateNeedsAnimation;
    private int mActivePointerId;
    private ArrayList<View> mAddedHeadsUpChildren;
    /* access modifiers changed from: private */
    public final AmbientState mAmbientState;
    private boolean mAnimateBottomOnLayout;
    private boolean mAnimateNextBackgroundBottom;
    private boolean mAnimateNextBackgroundTop;
    private boolean mAnimateNextSectionBoundsChange;
    private ArrayList<NotificationStackScrollLayout.AnimationEvent> mAnimationEvents;
    private HashSet<Runnable> mAnimationFinishedRunnables;
    private boolean mAnimationRunning;
    private boolean mAnimationsEnabled;
    /* access modifiers changed from: private */
    public final Rect mBackgroundAnimationRect;
    private final Paint mBackgroundPaint;
    private ViewTreeObserver.OnPreDrawListener mBackgroundUpdater;
    /* access modifiers changed from: private */
    public float mBackgroundXFactor;
    private int mBgColor;
    private int mBottomInset;
    private int mBottomMargin;
    private int mCachedBackgroundColor;
    private boolean mChangePositionInProgress;
    private boolean mChildTransferInProgress;
    private ArrayList<ExpandableView> mChildrenChangingPositions;
    private HashSet<ExpandableView> mChildrenToAddAnimated;
    private ArrayList<ExpandableView> mChildrenToRemoveAnimated;
    /* access modifiers changed from: private */
    public boolean mChildrenUpdateRequested;
    private ViewTreeObserver.OnPreDrawListener mChildrenUpdater;
    protected boolean mClearAllEnabled;
    private HashSet<ExpandableView> mClearTransientViewsWhenFinished;
    private final Rect mClipRect;
    private int mCollapsedSize;
    private int mContentHeight;
    private boolean mContinuousBackgroundUpdate;
    private boolean mContinuousShadowUpdate;
    private DesktopNotificationStackScrollLayoutController mController;
    /* access modifiers changed from: private */
    public int mCornerRadius;
    private int mCurrentStackHeight = Integer.MAX_VALUE;
    private float mDimAmount;
    /* access modifiers changed from: private */
    public ValueAnimator mDimAnimator;
    private final Animator.AnimatorListener mDimEndListener;
    private ValueAnimator.AnimatorUpdateListener mDimUpdateListener;
    private boolean mDimmedNeedsAnimation;
    private boolean mDisallowDismissInThisMotion;
    private boolean mDisallowScrollingInThisMotion;
    private DismissAllAnimationListener mDismissAllAnimationListener;
    private boolean mDismissAllInProgress;
    private DismissListener mDismissListener;
    private boolean mDismissRtl;
    private boolean mDismissUsingRowTranslationX;
    private final DisplayMetrics mDisplayMetrics;
    protected EmptyShadeView mEmptyShadeView;
    private boolean mEverythingNeedsAnimation;
    private ExpandableView mExpandedGroupView;
    private ArrayList<BiConsumer<Float, Float>> mExpandedHeightListeners;
    private boolean mExpandingNotification;
    private final FeatureFlags mFeatureFlags;
    private ForegroundServiceDungeonView mFgsSectionView;
    private FooterDismissListener mFooterDismissListener;
    protected FooterView mFooterView;
    private boolean mForceNoOverlappingRendering;
    private View mForcedScroll;
    private HashSet<View> mFromMoreCardAdditions;
    private int mGapHeight;
    private boolean mGenerateChildOrderChangedEvent;
    private long mGoToFullShadeDelay;
    private boolean mGoToFullShadeNeedsAnimation;
    private GroupExpansionManager mGroupExpansionManager;
    private GroupMembershipManager mGroupMembershipManager;
    private boolean mHeadsUpAnimatingAway;
    private HashSet<Pair<ExpandableNotificationRow, Boolean>> mHeadsUpChangeAnimations;
    private boolean mHeadsUpGoingAwayAnimationsAllowed;
    private int mHeadsUpInset;
    private boolean mHideSensitiveNeedsAnimation;
    /* access modifiers changed from: private */
    public Interpolator mHideXInterpolator;
    private boolean mHighPriorityBeforeSpeedBump;
    private boolean mInHeadsUpPinnedMode;
    private float mInterpolatedHideAmount;
    private int mIntrinsicContentHeight;
    private int mIntrinsicPadding;
    private boolean mIsClipped;
    private boolean mIsExpanded;
    /* access modifiers changed from: private */
    public float mLinearHideAmount;
    private int mMaxDisplayedNotifications;
    private int mMaxLayoutHeight;
    private int mMaxTopPadding;
    private int mMinInteractionHeight;
    private float mMinTopOverScrollToEscape;
    private boolean mNeedViewResizeAnimation;
    private boolean mNeedsAnimation;
    private NotificationActivityStarter mNotificationActivityStarter;
    private final ExpandableView.OnHeightChangedListener mOnChildHeightChangedListener;
    private ExpandableView.OnHeightChangedListener mOnHeightChangedListener;
    private final ViewOutlineProvider mOutlineProvider;
    private int mPaddingBetweenElements;
    private boolean mPulsing;
    private NotificationRemoteInputManager mRemoteInputManager;
    private Rect mRequestedClipBounds;
    private ViewTreeObserver.OnPreDrawListener mRunningAnimationUpdater;
    private NotificationSection[] mSections;
    private final NotificationSectionsManager mSectionsManager;
    private ViewTreeObserver.OnPreDrawListener mShadowUpdater;
    private NotificationShelf mShelf;
    private final boolean mShouldDrawNotificationBackground;
    private boolean mShouldShowShelfOnly;
    private boolean mShouldUseSplitNotificationShade;
    private int mSidePaddings;
    private int mSpeedBumpIndex;
    private boolean mSpeedBumpIndexDirty;
    private final StackScrollAlgorithm mStackScrollAlgorithm;
    private float mStackTranslation;
    private final StackStateAnimator mStateAnimator;
    private StatusBar mStatusBar;
    private int mStatusBarHeight;
    private NotificationSwipeHelper mSwipeHelper;
    private ArrayList<View> mSwipedOutViews;
    private int[] mTempInt2;
    private final ArrayList<Pair<ExpandableNotificationRow, Boolean>> mTmpList;
    private final Rect mTmpRect;
    private ArrayList<ExpandableView> mTmpSortedChildren;
    private int mTopPadding;
    private boolean mTopPaddingNeedsAnimation;
    private DesktopNotificationStackScrollLayoutController.TouchHandler mTouchHandler;
    private Comparator<ExpandableView> mViewPositionComparator;
    private boolean mWillExpand;

    interface DismissAllAnimationListener {
        void onAnimationEnd(List<ExpandableNotificationRow> list, int i);
    }

    interface DismissListener {
        void onDismiss(int i);
    }

    interface FooterDismissListener {
        void onDismiss();
    }

    public ViewGroup getViewParentForNotification(NotificationEntry notificationEntry) {
        return this;
    }

    public boolean isFullySwipedOut(ExpandableView expandableView) {
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean onKeyguard() {
        return false;
    }

    public boolean shouldDelayChildPressedState() {
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0() {
        updateViewShadows();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1() {
        updateBackground();
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$new$2(ExpandableView expandableView, ExpandableView expandableView2) {
        float translationY = expandableView.getTranslationY() + ((float) expandableView.getActualHeight());
        float translationY2 = expandableView2.getTranslationY() + ((float) expandableView2.getActualHeight());
        if (translationY < translationY2) {
            return -1;
        }
        return translationY > translationY2 ? 1 : 0;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DesktopNotificationStackScrollLayout(Context context, AttributeSet attributeSet, NotificationSectionsManager notificationSectionsManager, GroupMembershipManager groupMembershipManager, GroupExpansionManager groupExpansionManager, AmbientState ambientState, FeatureFlags featureFlags) {
        super(context, attributeSet, 0, 0);
        boolean z = false;
        Paint paint = new Paint();
        this.mBackgroundPaint = paint;
        this.mActivePointerId = -1;
        this.mBottomInset = 0;
        this.mChildrenToAddAnimated = new HashSet<>();
        this.mAddedHeadsUpChildren = new ArrayList<>();
        this.mChildrenToRemoveAnimated = new ArrayList<>();
        this.mChildrenChangingPositions = new ArrayList<>();
        this.mFromMoreCardAdditions = new HashSet<>();
        this.mAnimationEvents = new ArrayList<>();
        this.mSwipedOutViews = new ArrayList<>();
        this.mStateAnimator = new StackStateAnimator(this);
        this.mSpeedBumpIndex = -1;
        this.mSpeedBumpIndexDirty = true;
        this.mIsExpanded = true;
        this.mChildrenUpdater = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                DesktopNotificationStackScrollLayout.this.updateChildren();
                boolean unused = DesktopNotificationStackScrollLayout.this.mChildrenUpdateRequested = false;
                DesktopNotificationStackScrollLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        };
        this.mTempInt2 = new int[2];
        this.mAnimationFinishedRunnables = new HashSet<>();
        this.mClearTransientViewsWhenFinished = new HashSet<>();
        this.mHeadsUpChangeAnimations = new HashSet<>();
        this.mTmpList = new ArrayList<>();
        this.mRunningAnimationUpdater = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                DesktopNotificationStackScrollLayout.this.onPreDrawDuringAnimation();
                return true;
            }
        };
        this.mTmpSortedChildren = new ArrayList<>();
        this.mDimEndListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ValueAnimator unused = DesktopNotificationStackScrollLayout.this.mDimAnimator = null;
            }
        };
        this.mDimUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                DesktopNotificationStackScrollLayout.this.setDimAmount(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        };
        this.mShadowUpdater = new DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda4(this);
        this.mBackgroundUpdater = new DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda3(this);
        this.mViewPositionComparator = DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda7.INSTANCE;
        C16695 r3 = new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                if (DesktopNotificationStackScrollLayout.this.mAmbientState.isHiddenAtAll()) {
                    outline.setRoundRect(DesktopNotificationStackScrollLayout.this.mBackgroundAnimationRect, MathUtils.lerp(((float) DesktopNotificationStackScrollLayout.this.mCornerRadius) / 2.0f, (float) DesktopNotificationStackScrollLayout.this.mCornerRadius, DesktopNotificationStackScrollLayout.this.mHideXInterpolator.getInterpolation((1.0f - DesktopNotificationStackScrollLayout.this.mLinearHideAmount) * DesktopNotificationStackScrollLayout.this.mBackgroundXFactor)));
                    outline.setAlpha(1.0f - DesktopNotificationStackScrollLayout.this.mAmbientState.getHideAmount());
                    return;
                }
                ViewOutlineProvider.BACKGROUND.getOutline(view, outline);
            }
        };
        this.mOutlineProvider = r3;
        this.mInterpolatedHideAmount = 0.0f;
        this.mLinearHideAmount = 0.0f;
        this.mBackgroundXFactor = 1.0f;
        this.mMaxDisplayedNotifications = -1;
        this.mClipRect = new Rect();
        this.mHeadsUpGoingAwayAnimationsAllowed = true;
        this.mBackgroundAnimationRect = new Rect();
        this.mExpandedHeightListeners = new ArrayList<>();
        this.mTmpRect = new Rect();
        this.mDisplayMetrics = (DisplayMetrics) Dependency.get(DisplayMetrics.class);
        this.mHideXInterpolator = Interpolators.FAST_OUT_SLOW_IN;
        this.mDismissUsingRowTranslationX = true;
        this.mOnChildHeightChangedListener = new ExpandableView.OnHeightChangedListener() {
            public void onHeightChanged(ExpandableView expandableView, boolean z) {
                DesktopNotificationStackScrollLayout.this.onChildHeightChanged(expandableView, z);
            }

            public void onReset(ExpandableView expandableView) {
                DesktopNotificationStackScrollLayout.this.onChildHeightReset(expandableView);
            }
        };
        Resources resources = getResources();
        this.mSectionsManager = notificationSectionsManager;
        this.mFeatureFlags = featureFlags;
        this.mShouldUseSplitNotificationShade = Utils.shouldUseSplitNotificationShade(featureFlags, resources);
        notificationSectionsManager.initialize(this, LayoutInflater.from(context));
        if (!MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            notificationSectionsManager.setHeaderForegroundColor(this.mContext.getColor(R$color.desktop_qs_notification_sections_head_text));
        }
        this.mSections = notificationSectionsManager.createSectionsForBuckets();
        this.mAmbientState = ambientState;
        this.mBgColor = com.android.settingslib.Utils.getColorAttr(this.mContext, 16844002).getDefaultColor();
        this.mStackScrollAlgorithm = createStackScrollAlgorithm(context);
        boolean z2 = resources.getBoolean(R$bool.config_drawNotificationBackground);
        this.mShouldDrawNotificationBackground = z2;
        setOutlineProvider(r3);
        setWillNotDraw(!(z2 ? true : z));
        paint.setAntiAlias(true);
        this.mClearAllEnabled = resources.getBoolean(R$bool.config_enableNotificationsClearAll);
        this.mGroupMembershipManager = groupMembershipManager;
        this.mGroupExpansionManager = groupExpansionManager;
    }

    /* access modifiers changed from: package-private */
    public void initializeForegroundServiceSection(ForegroundServiceDungeonView foregroundServiceDungeonView) {
        if (this.mFgsSectionView == null) {
            this.mFgsSectionView = foregroundServiceDungeonView;
            addView(foregroundServiceDungeonView, -1);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        inflateEmptyShadeView();
        inflateFooterView();
    }

    /* access modifiers changed from: package-private */
    public void reinflateViews() {
        inflateFooterView();
        inflateEmptyShadeView();
        updateFooter();
        this.mSectionsManager.reinflateViews(LayoutInflater.from(this.mContext));
        if (!MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            this.mSectionsManager.setHeaderForegroundColor(this.mContext.getColor(R$color.desktop_qs_notification_sections_head_text));
        }
    }

    @VisibleForTesting
    public void updateFooter() {
        if (this.mFooterView != null) {
            boolean z = true;
            boolean z2 = this.mClearAllEnabled && this.mController.hasActiveClearableNotifications(0);
            RemoteInputController controller = this.mRemoteInputManager.getController();
            boolean z3 = (z2 || this.mController.hasActiveNotifications()) && (controller == null || !controller.isRemoteInputActive());
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_history_enabled", 0, -2) != 1) {
                z = false;
            }
            updateFooterView(z3, z2, z);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasActiveClearableNotifications(int i) {
        return this.mController.hasActiveClearableNotifications(i);
    }

    /* access modifiers changed from: package-private */
    public void updateBgColor() {
        this.mBgColor = com.android.settingslib.Utils.getColorAttr(this.mContext, 16844002).getDefaultColor();
        updateBackgroundDimming();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActivatableNotificationView) {
                ((ActivatableNotificationView) childAt).updateBackgroundColors();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mShouldDrawNotificationBackground) {
            int i = this.mSections[0].getCurrentBounds().top;
            NotificationSection[] notificationSectionArr = this.mSections;
            if (i < notificationSectionArr[notificationSectionArr.length - 1].getCurrentBounds().bottom || this.mAmbientState.isDozing()) {
                drawBackground(canvas);
                return;
            }
        }
        if (this.mInHeadsUpPinnedMode || this.mHeadsUpAnimatingAway) {
            drawHeadsUpBackground(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        boolean z;
        int i = this.mSidePaddings;
        int width = getWidth() - this.mSidePaddings;
        boolean z2 = false;
        int i2 = this.mSections[0].getCurrentBounds().top;
        NotificationSection[] notificationSectionArr = this.mSections;
        int i3 = notificationSectionArr[notificationSectionArr.length - 1].getCurrentBounds().bottom;
        int i4 = this.mTopPadding;
        float f = 1.0f - this.mInterpolatedHideAmount;
        float interpolation = this.mHideXInterpolator.getInterpolation((1.0f - this.mLinearHideAmount) * this.mBackgroundXFactor);
        float width2 = (float) (getWidth() / 2);
        int lerp = (int) MathUtils.lerp(width2, (float) i, interpolation);
        int lerp2 = (int) MathUtils.lerp(width2, (float) width, interpolation);
        float f2 = (float) i4;
        int lerp3 = (int) MathUtils.lerp(f2, (float) i2, f);
        this.mBackgroundAnimationRect.set(lerp, lerp3, lerp2, (int) MathUtils.lerp(f2, (float) i3, f));
        int i5 = lerp3 - i2;
        NotificationSection[] notificationSectionArr2 = this.mSections;
        int length = notificationSectionArr2.length;
        int i6 = 0;
        while (true) {
            if (i6 >= length) {
                z = false;
                break;
            } else if (notificationSectionArr2[i6].needsBackground()) {
                z = true;
                break;
            } else {
                i6++;
            }
        }
        if (!this.mAmbientState.isDozing() || z) {
            z2 = true;
        }
        if (z2) {
            drawBackgroundRects(canvas, lerp, lerp2, lerp3, i5);
        }
        updateClipping();
    }

    private void drawBackgroundRects(Canvas canvas, int i, int i2, int i3, int i4) {
        int i5 = i2;
        NotificationSection[] notificationSectionArr = this.mSections;
        int length = notificationSectionArr.length;
        int i6 = 1;
        int i7 = i;
        int i8 = i5;
        int i9 = this.mSections[0].getCurrentBounds().bottom + i4;
        int i10 = 0;
        boolean z = true;
        int i11 = i3;
        while (i10 < length) {
            NotificationSection notificationSection = notificationSectionArr[i10];
            if (!notificationSection.needsBackground()) {
                int i12 = i;
            } else {
                int i13 = notificationSection.getCurrentBounds().top + i4;
                int min = Math.min(Math.max(i, notificationSection.getCurrentBounds().left), i5);
                int max = Math.max(Math.min(i5, notificationSection.getCurrentBounds().right), min);
                if (i13 - i9 > i6 || (!(i7 == min && i8 == max) && !z)) {
                    float f = (float) i7;
                    float f2 = (float) i8;
                    int i14 = this.mCornerRadius;
                    canvas.drawRoundRect(f, (float) i11, f2, (float) i9, (float) i14, (float) i14, this.mBackgroundPaint);
                    i11 = i13;
                }
                i9 = notificationSection.getCurrentBounds().bottom + i4;
                i8 = max;
                i7 = min;
                z = false;
            }
            i10++;
            i5 = i2;
            i6 = 1;
        }
        int i15 = this.mCornerRadius;
        canvas.drawRoundRect((float) i7, (float) i11, (float) i8, (float) i9, (float) i15, (float) i15, this.mBackgroundPaint);
    }

    private void drawHeadsUpBackground(Canvas canvas) {
        int i = this.mSidePaddings;
        int width = getWidth() - this.mSidePaddings;
        int childCount = getChildCount();
        float height = (float) getHeight();
        float f = 0.0f;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() != 8 && (childAt instanceof ExpandableNotificationRow)) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) childAt;
                if ((expandableNotificationRow.isPinned() || expandableNotificationRow.isHeadsUpAnimatingAway()) && expandableNotificationRow.getTranslation() < 0.0f && expandableNotificationRow.getProvider().shouldShowGutsOnSnapOpen()) {
                    float min = Math.min(height, expandableNotificationRow.getTranslationY());
                    f = Math.max(f, expandableNotificationRow.getTranslationY() + ((float) expandableNotificationRow.getActualHeight()));
                    height = min;
                }
            }
        }
        if (height < f) {
            int i3 = this.mCornerRadius;
            canvas.drawRoundRect((float) i, height, (float) width, f, (float) i3, (float) i3, this.mBackgroundPaint);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateBackgroundDimming() {
        int blendARGB;
        if (this.mShouldDrawNotificationBackground && this.mCachedBackgroundColor != (blendARGB = ColorUtils.blendARGB(this.mBgColor, -1, MathUtils.smoothStep(0.4f, 1.0f, this.mLinearHideAmount)))) {
            this.mCachedBackgroundColor = blendARGB;
            this.mBackgroundPaint.setColor(blendARGB);
            invalidate();
        }
    }

    private void reinitView() {
        initView(getContext(), this.mSwipeHelper);
    }

    /* access modifiers changed from: package-private */
    public void initView(Context context, NotificationSwipeHelper notificationSwipeHelper) {
        this.mSwipeHelper = notificationSwipeHelper;
        setDescendantFocusability(262144);
        setClipChildren(false);
        ViewConfiguration.get(context);
        Resources resources = context.getResources();
        this.mCollapsedSize = resources.getDimensionPixelSize(R$dimen.notification_min_height);
        this.mGapHeight = resources.getDimensionPixelSize(R$dimen.notification_section_divider_height);
        this.mStackScrollAlgorithm.initView(context);
        this.mAmbientState.reload(context);
        this.mPaddingBetweenElements = Math.max(1, resources.getDimensionPixelSize(R$dimen.notification_divider_height));
        this.mMinTopOverScrollToEscape = (float) resources.getDimensionPixelSize(R$dimen.min_top_overscroll_to_qs);
        this.mStatusBarHeight = resources.getDimensionPixelSize(R$dimen.status_bar_height);
        this.mBottomMargin = resources.getDimensionPixelSize(R$dimen.notification_panel_margin_bottom);
        this.mSidePaddings = resources.getDimensionPixelSize(R$dimen.notification_side_paddings);
        this.mMinInteractionHeight = resources.getDimensionPixelSize(R$dimen.notification_min_interaction_height);
        this.mCornerRadius = resources.getDimensionPixelSize(R$dimen.notification_corner_radius);
        this.mHeadsUpInset = this.mStatusBarHeight + resources.getDimensionPixelSize(R$dimen.heads_up_status_bar_padding);
        setIsExpanded(true);
    }

    /* access modifiers changed from: package-private */
    public void updateCornerRadius() {
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.notification_corner_radius);
        if (this.mCornerRadius != dimensionPixelSize) {
            this.mCornerRadius = dimensionPixelSize;
            invalidate();
        }
    }

    private void notifyHeightChangeListener(ExpandableView expandableView, boolean z) {
        ExpandableView.OnHeightChangedListener onHeightChangedListener = this.mOnHeightChangedListener;
        if (onHeightChangedListener != null) {
            onHeightChangedListener.onHeightChanged(expandableView, z);
        }
    }

    public int getSpeedBumpIndex() {
        if (this.mSpeedBumpIndexDirty) {
            this.mSpeedBumpIndexDirty = false;
            int childCount = getChildCount();
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (childAt.getVisibility() != 8 && (childAt instanceof ExpandableNotificationRow)) {
                    ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) childAt;
                    i2++;
                    boolean z = true;
                    if (!this.mHighPriorityBeforeSpeedBump) {
                        z = true ^ expandableNotificationRow.getEntry().isAmbient();
                    } else if (expandableNotificationRow.getEntry().getBucket() >= 6) {
                        z = false;
                    }
                    if (z) {
                        i = i2;
                    }
                }
            }
            this.mSpeedBumpIndex = i;
        }
        return this.mSpeedBumpIndex;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i) - (this.mSidePaddings * 2), View.MeasureSpec.getMode(i));
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 0);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            measureChild(getChildAt(i3), makeMeasureSpec, makeMeasureSpec2);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float width = ((float) getWidth()) / 2.0f;
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            float measuredWidth = ((float) childAt.getMeasuredWidth()) / 2.0f;
            childAt.layout((int) (width - measuredWidth), 0, (int) (measuredWidth + width), (int) ((float) childAt.getMeasuredHeight()));
        }
        setMaxLayoutHeight(getHeight());
        updateContentHeight();
        requestChildrenUpdate();
        updateFirstAndLastBackgroundViews();
        updateAlgorithmLayoutMinHeight();
        updateOwnTranslationZ();
    }

    private void requestAnimationOnViewResize(ExpandableNotificationRow expandableNotificationRow) {
        if (!this.mAnimationsEnabled) {
            return;
        }
        if (this.mIsExpanded || (expandableNotificationRow != null && expandableNotificationRow.isPinned())) {
            this.mNeedViewResizeAnimation = true;
            this.mNeedsAnimation = true;
        }
    }

    private void setMaxLayoutHeight(int i) {
        this.mMaxLayoutHeight = i;
        updateAlgorithmHeightAndPadding();
    }

    private void updateAlgorithmHeightAndPadding() {
        this.mAmbientState.setLayoutHeight(getLayoutHeight());
        updateAlgorithmLayoutMinHeight();
        this.mAmbientState.setTopPadding(this.mTopPadding);
    }

    private void updateAlgorithmLayoutMinHeight() {
        this.mAmbientState.setLayoutMinHeight(0);
    }

    /* access modifiers changed from: private */
    public void updateChildren() {
        this.mAmbientState.setCurrentScrollVelocity(0.0f);
        this.mStackScrollAlgorithm.resetViewStates(this.mAmbientState, getSpeedBumpIndex());
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int stackScrollHeightExtent = ((int) this.mStackScrollAlgorithm.getStackScrollHeightExtent()) + getContext().getResources().getDimensionPixelSize(R$dimen.desktop_notification_panel_padding_bottom);
        if (layoutParams.height != stackScrollHeightExtent) {
            layoutParams.height = stackScrollHeightExtent;
            setLayoutParams(layoutParams);
            setMinimumHeight(stackScrollHeightExtent);
        }
        if (isCurrentlyAnimating() || this.mNeedsAnimation) {
            startAnimationToState();
        } else {
            applyCurrentState();
        }
    }

    /* access modifiers changed from: private */
    public void onPreDrawDuringAnimation() {
        NotificationShelf notificationShelf = this.mShelf;
        if (notificationShelf != null) {
            notificationShelf.updateAppearance();
        }
        if (!this.mNeedsAnimation && !this.mChildrenUpdateRequested) {
            updateBackground();
        }
    }

    /* access modifiers changed from: package-private */
    public void requestChildrenUpdate() {
        if (!this.mChildrenUpdateRequested) {
            getViewTreeObserver().addOnPreDrawListener(this.mChildrenUpdater);
            this.mChildrenUpdateRequested = true;
            invalidate();
        }
    }

    public int getVisibleNotificationCount() {
        int i = 0;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() != 8 && (childAt instanceof ExpandableNotificationRow)) {
                i++;
            }
        }
        return i;
    }

    private boolean isCurrentlyAnimating() {
        return this.mStateAnimator.isRunning();
    }

    public void updateClipping() {
        boolean z = this.mRequestedClipBounds != null && !this.mInHeadsUpPinnedMode && !this.mHeadsUpAnimatingAway;
        if (this.mIsClipped != z) {
            this.mIsClipped = z;
        }
        if (this.mAmbientState.isHiddenAtAll()) {
            invalidateOutline();
            if (isFullyHidden()) {
                setClipBounds((Rect) null);
            }
        } else if (z) {
            setClipBounds(this.mRequestedClipBounds);
        } else {
            setClipBounds((Rect) null);
        }
        setClipToOutline(false);
    }

    private int getLayoutHeight() {
        return Math.min(this.mMaxLayoutHeight, this.mCurrentStackHeight);
    }

    public static boolean isPinnedHeadsUp(View view) {
        if (!(view instanceof ExpandableNotificationRow)) {
            return false;
        }
        ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
        if (!expandableNotificationRow.isHeadsUp() || !expandableNotificationRow.isPinned()) {
            return false;
        }
        return true;
    }

    private boolean isHeadsUp(View view) {
        if (view instanceof ExpandableNotificationRow) {
            return ((ExpandableNotificationRow) view).isHeadsUp();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public ExpandableView getChildAtPosition(float f, float f2, boolean z, boolean z2) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ExpandableView expandableView = (ExpandableView) getChildAt(i);
            if (expandableView.getVisibility() == 0 && (!z2 || !(expandableView instanceof StackScrollerDecorView))) {
                float translationY = expandableView.getTranslationY();
                float clipTopAmount = ((float) expandableView.getClipTopAmount()) + translationY;
                float actualHeight = (((float) expandableView.getActualHeight()) + translationY) - ((float) expandableView.getClipBottomAmount());
                int width = getWidth();
                if ((actualHeight - clipTopAmount >= ((float) this.mMinInteractionHeight) || !z) && f2 >= clipTopAmount && f2 <= actualHeight && f >= ((float) 0) && f <= ((float) width)) {
                    if (!(expandableView instanceof ExpandableNotificationRow)) {
                        return expandableView;
                    }
                    ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) expandableView;
                    expandableNotificationRow.getEntry();
                    return expandableNotificationRow.getViewAtPosition(f2 - translationY);
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Resources resources = getResources();
        this.mShouldUseSplitNotificationShade = Utils.shouldUseSplitNotificationShade(this.mFeatureFlags, resources);
        this.mStatusBarHeight = resources.getDimensionPixelOffset(R$dimen.status_bar_height);
        this.mSwipeHelper.setDensityScale(resources.getDisplayMetrics().density);
        this.mSwipeHelper.setPagingTouchSlop((float) ViewConfiguration.get(getContext()).getScaledPagingTouchSlop());
        reinitView();
    }

    public void dismissViewAnimated(View view, Runnable runnable, int i, long j) {
        this.mSwipeHelper.dismissChild(view, 0.0f, runnable, (long) i, true, j, true);
    }

    private void snapViewIfNeeded(NotificationEntry notificationEntry) {
        ExpandableNotificationRow row = notificationEntry.getRow();
        boolean z = this.mIsExpanded || isPinnedHeadsUp(row);
        if (row.getProvider() != null) {
            this.mSwipeHelper.snapChildIfNeeded(row, z, row.getProvider().isMenuVisible() ? row.getTranslation() : 0.0f);
        }
    }

    private View getFirstChildBelowTranlsationY(float f, boolean z) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() != 8) {
                float translationY = childAt.getTranslationY();
                if (translationY >= f) {
                    return childAt;
                }
                if (!z && (childAt instanceof ExpandableNotificationRow)) {
                    ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) childAt;
                    if (expandableNotificationRow.isSummaryWithChildren() && expandableNotificationRow.areChildrenExpanded()) {
                        List<ExpandableNotificationRow> attachedChildren = expandableNotificationRow.getAttachedChildren();
                        for (int i2 = 0; i2 < attachedChildren.size(); i2++) {
                            ExpandableNotificationRow expandableNotificationRow2 = attachedChildren.get(i2);
                            if (expandableNotificationRow2.getTranslationY() + translationY >= f) {
                                return expandableNotificationRow2;
                            }
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public ExpandableView getLastChildNotGone() {
        NotificationShelf notificationShelf;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            if (childAt.getVisibility() != 8 && ((notificationShelf = this.mShelf) == null || childAt != notificationShelf)) {
                return (ExpandableView) childAt;
            }
        }
        return null;
    }

    private void updateContentHeight() {
        float f;
        int i = this.mMaxDisplayedNotifications;
        ExpandableView expandableView = null;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        boolean z = false;
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            ExpandableView expandableView2 = (ExpandableView) getChildAt(i5);
            boolean z2 = true;
            boolean z3 = expandableView2 == this.mFooterView && onKeyguard();
            if (expandableView2.getVisibility() != 8 && !expandableView2.hasNoContentHeight() && !z3) {
                if (i != -1 && i2 >= i) {
                    f = (float) this.mShelf.getIntrinsicHeight();
                } else {
                    z2 = z;
                    f = (float) expandableView2.getIntrinsicHeight();
                }
                if (i3 != 0) {
                    i3 += this.mPaddingBetweenElements;
                }
                i3 = (int) (((float) ((int) (((float) i3) + calculateGapHeight(expandableView, expandableView2, i4)))) + f);
                i4++;
                if (!(expandableView2 instanceof MediaHeaderView)) {
                    i2++;
                }
                if (z2) {
                    break;
                }
                expandableView = expandableView2;
                z = z2;
            }
        }
        this.mIntrinsicContentHeight = i3;
        int max = i3 + Math.max(this.mIntrinsicPadding, this.mTopPadding) + this.mBottomMargin;
        this.mContentHeight = max;
        this.mAmbientState.setContentHeight(max);
        this.mAmbientState.setStackEndHeight((float) this.mContentHeight);
        this.mAmbientState.setStackHeight((float) this.mContentHeight);
    }

    public float calculateGapHeight(ExpandableView expandableView, ExpandableView expandableView2, int i) {
        return this.mStackScrollAlgorithm.getGapHeightForChild(this.mSectionsManager, i, expandableView2, expandableView);
    }

    public boolean hasPulsingNotifications() {
        return this.mPulsing;
    }

    private void updateBackground() {
        if (this.mShouldDrawNotificationBackground) {
            updateBackgroundBounds();
            if (didSectionBoundsChange()) {
                boolean z = this.mAnimateNextSectionBoundsChange || this.mAnimateNextBackgroundTop || this.mAnimateNextBackgroundBottom || areSectionBoundsAnimating();
                if (!isExpanded()) {
                    abortBackgroundAnimators();
                    z = false;
                }
                if (z) {
                    startBackgroundAnimation();
                } else {
                    for (NotificationSection resetCurrentBounds : this.mSections) {
                        resetCurrentBounds.resetCurrentBounds();
                    }
                    invalidate();
                }
            } else {
                abortBackgroundAnimators();
            }
            this.mAnimateNextBackgroundTop = false;
            this.mAnimateNextBackgroundBottom = false;
            this.mAnimateNextSectionBoundsChange = false;
        }
    }

    private void abortBackgroundAnimators() {
        for (NotificationSection cancelAnimators : this.mSections) {
            cancelAnimators.cancelAnimators();
        }
    }

    private boolean didSectionBoundsChange() {
        for (NotificationSection didBoundsChange : this.mSections) {
            if (didBoundsChange.didBoundsChange()) {
                return true;
            }
        }
        return false;
    }

    private boolean areSectionBoundsAnimating() {
        for (NotificationSection areBoundsAnimating : this.mSections) {
            if (areBoundsAnimating.areBoundsAnimating()) {
                return true;
            }
        }
        return false;
    }

    private void startBackgroundAnimation() {
        boolean z;
        boolean z2;
        NotificationSection firstVisibleSection = getFirstVisibleSection();
        NotificationSection lastVisibleSection = getLastVisibleSection();
        for (NotificationSection notificationSection : this.mSections) {
            if (notificationSection == firstVisibleSection) {
                z = this.mAnimateNextBackgroundTop;
            } else {
                z = this.mAnimateNextSectionBoundsChange;
            }
            if (notificationSection == lastVisibleSection) {
                z2 = this.mAnimateNextBackgroundBottom;
            } else {
                z2 = this.mAnimateNextSectionBoundsChange;
            }
            notificationSection.startBackgroundAnimation(z, z2);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0052, code lost:
        r7 = r9.mShelf;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateBackgroundBounds() {
        /*
            r9 = this;
            int r0 = r9.mSidePaddings
            int r1 = r9.getWidth()
            int r2 = r9.mSidePaddings
            int r1 = r1 - r2
            com.android.systemui.statusbar.notification.stack.NotificationSection[] r2 = r9.mSections
            int r3 = r2.length
            r4 = 0
            r5 = r4
        L_0x000e:
            if (r5 >= r3) goto L_0x0021
            r6 = r2[r5]
            android.graphics.Rect r7 = r6.getBounds()
            r7.left = r0
            android.graphics.Rect r6 = r6.getBounds()
            r6.right = r1
            int r5 = r5 + 1
            goto L_0x000e
        L_0x0021:
            boolean r0 = r9.mIsExpanded
            if (r0 != 0) goto L_0x003d
            com.android.systemui.statusbar.notification.stack.NotificationSection[] r9 = r9.mSections
            int r0 = r9.length
            r1 = r4
        L_0x0029:
            if (r1 >= r0) goto L_0x003c
            r2 = r9[r1]
            android.graphics.Rect r3 = r2.getBounds()
            r3.top = r4
            android.graphics.Rect r2 = r2.getBounds()
            r2.bottom = r4
            int r1 = r1 + 1
            goto L_0x0029
        L_0x003c:
            return
        L_0x003d:
            com.android.systemui.statusbar.notification.stack.NotificationSection r0 = r9.getLastVisibleSection()
            int r1 = r9.mTopPadding
            float r1 = (float) r1
            float r2 = r9.mStackTranslation
            float r1 = r1 + r2
            int r1 = (int) r1
            com.android.systemui.statusbar.notification.stack.NotificationSection[] r2 = r9.mSections
            int r3 = r2.length
            r5 = r4
        L_0x004c:
            if (r5 >= r3) goto L_0x006c
            r6 = r2[r5]
            if (r6 != r0) goto L_0x0064
            com.android.systemui.statusbar.NotificationShelf r7 = r9.mShelf
            if (r7 == 0) goto L_0x0064
            float r7 = com.android.systemui.statusbar.notification.stack.ViewState.getFinalTranslationY(r7)
            com.android.systemui.statusbar.NotificationShelf r8 = r9.mShelf
            int r8 = r8.getIntrinsicHeight()
            float r8 = (float) r8
            float r7 = r7 + r8
            int r7 = (int) r7
            goto L_0x0065
        L_0x0064:
            r7 = r1
        L_0x0065:
            int r1 = r6.updateBounds(r1, r7, r4)
            int r5 = r5 + 1
            goto L_0x004c
        L_0x006c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout.updateBackgroundBounds():void");
    }

    private NotificationSection getFirstVisibleSection() {
        for (NotificationSection notificationSection : this.mSections) {
            if (notificationSection.getFirstVisibleChild() != null) {
                return notificationSection;
            }
        }
        return null;
    }

    private NotificationSection getLastVisibleSection() {
        for (int length = this.mSections.length - 1; length >= 0; length--) {
            NotificationSection notificationSection = this.mSections[length];
            if (notificationSection.getLastVisibleChild() != null) {
                return notificationSection;
            }
        }
        return null;
    }

    private ExpandableView getLastChildWithBackground() {
        NotificationShelf notificationShelf;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            ExpandableView expandableView = (ExpandableView) getChildAt(childCount);
            if (expandableView.getVisibility() != 8 && !(expandableView instanceof StackScrollerDecorView) && ((notificationShelf = this.mShelf) == null || expandableView != notificationShelf)) {
                return expandableView;
            }
        }
        return null;
    }

    private ExpandableView getFirstChildWithBackground() {
        NotificationShelf notificationShelf;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ExpandableView expandableView = (ExpandableView) getChildAt(i);
            if (expandableView.getVisibility() != 8 && !(expandableView instanceof StackScrollerDecorView) && ((notificationShelf = this.mShelf) == null || expandableView != notificationShelf)) {
                return expandableView;
            }
        }
        return null;
    }

    private List<ExpandableView> getChildrenWithBackground() {
        NotificationShelf notificationShelf;
        ArrayList arrayList = new ArrayList();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ExpandableView expandableView = (ExpandableView) getChildAt(i);
            if (expandableView.getVisibility() != 8 && !(expandableView instanceof StackScrollerDecorView) && ((notificationShelf = this.mShelf) == null || expandableView != notificationShelf)) {
                arrayList.add(expandableView);
            }
        }
        return arrayList;
    }

    public void setChildTransferInProgress(boolean z) {
        Assert.isMainThread();
        this.mChildTransferInProgress = z;
    }

    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        if (!this.mChildTransferInProgress) {
            onViewRemovedInternal((ExpandableView) view, this);
        }
    }

    public void cleanUpViewStateForEntry(NotificationEntry notificationEntry) {
        if (notificationEntry.getRow() == this.mSwipeHelper.getTranslatingParentView()) {
            this.mSwipeHelper.clearTranslatingParentView();
        }
    }

    private void onViewRemovedInternal(ExpandableView expandableView, ViewGroup viewGroup) {
        if (!this.mChangePositionInProgress) {
            expandableView.setOnHeightChangedListener((ExpandableView.OnHeightChangedListener) null);
            if (!generateRemoveAnimation(expandableView)) {
                this.mSwipedOutViews.remove(expandableView);
            } else if (!this.mSwipedOutViews.contains(expandableView) || Math.abs(expandableView.getTranslation()) != ((float) expandableView.getWidth())) {
                viewGroup.addTransientView(expandableView, 0);
                expandableView.setTransientContainer(viewGroup);
            }
            updateAnimationState(false, expandableView);
            focusNextViewIfFocused(expandableView);
        }
    }

    private void focusNextViewIfFocused(View view) {
        float f;
        if (view instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
            if (expandableNotificationRow.shouldRefocusOnDismiss()) {
                View childAfterViewWhenDismissed = expandableNotificationRow.getChildAfterViewWhenDismissed();
                if (childAfterViewWhenDismissed == null) {
                    View groupParentWhenDismissed = expandableNotificationRow.getGroupParentWhenDismissed();
                    if (groupParentWhenDismissed != null) {
                        f = groupParentWhenDismissed.getTranslationY();
                    } else {
                        f = view.getTranslationY();
                    }
                    childAfterViewWhenDismissed = getFirstChildBelowTranlsationY(f, true);
                }
                if (childAfterViewWhenDismissed != null) {
                    childAfterViewWhenDismissed.requestAccessibilityFocus();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean generateRemoveAnimation(ExpandableView expandableView) {
        if (removeRemovedChildFromHeadsUpChangeAnimations(expandableView)) {
            this.mAddedHeadsUpChildren.remove(expandableView);
            return false;
        } else if (isClickedHeadsUp(expandableView)) {
            this.mClearTransientViewsWhenFinished.add(expandableView);
            return true;
        } else {
            if (this.mIsExpanded && this.mAnimationsEnabled && !isChildInInvisibleGroup(expandableView)) {
                if (!this.mChildrenToAddAnimated.contains(expandableView)) {
                    this.mChildrenToRemoveAnimated.add(expandableView);
                    this.mNeedsAnimation = true;
                    return true;
                }
                this.mChildrenToAddAnimated.remove(expandableView);
                this.mFromMoreCardAdditions.remove(expandableView);
            }
            return false;
        }
    }

    private boolean isClickedHeadsUp(View view) {
        return HeadsUpUtil.isClickedHeadsUpNotification(view);
    }

    private boolean removeRemovedChildFromHeadsUpChangeAnimations(View view) {
        Iterator<Pair<ExpandableNotificationRow, Boolean>> it = this.mHeadsUpChangeAnimations.iterator();
        boolean z = false;
        while (it.hasNext()) {
            Pair next = it.next();
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) next.first;
            boolean booleanValue = ((Boolean) next.second).booleanValue();
            if (view == expandableNotificationRow) {
                this.mTmpList.add(next);
                z |= booleanValue;
            }
        }
        if (z) {
            this.mHeadsUpChangeAnimations.removeAll(this.mTmpList);
            ((ExpandableNotificationRow) view).setHeadsUpAnimatingAway(false);
        }
        this.mTmpList.clear();
        return z;
    }

    private boolean isChildInInvisibleGroup(View view) {
        if (!(view instanceof ExpandableNotificationRow)) {
            return false;
        }
        ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
        NotificationEntry groupSummary = this.mGroupMembershipManager.getGroupSummary(expandableNotificationRow.getEntry());
        if (groupSummary == null || groupSummary.getRow() == expandableNotificationRow || expandableNotificationRow.getVisibility() != 4) {
            return false;
        }
        return true;
    }

    public void onViewAdded(View view) {
        super.onViewAdded(view);
        if (view instanceof ExpandableView) {
            onViewAddedInternal((ExpandableView) view);
        }
    }

    private void updateFirstAndLastBackgroundViews() {
        ExpandableView expandableView;
        NotificationSection firstVisibleSection = getFirstVisibleSection();
        NotificationSection lastVisibleSection = getLastVisibleSection();
        ExpandableView expandableView2 = null;
        if (firstVisibleSection == null) {
            expandableView = null;
        } else {
            expandableView = firstVisibleSection.getFirstVisibleChild();
        }
        if (lastVisibleSection != null) {
            expandableView2 = lastVisibleSection.getLastVisibleChild();
        }
        ExpandableView firstChildWithBackground = getFirstChildWithBackground();
        ExpandableView lastChildWithBackground = getLastChildWithBackground();
        boolean updateFirstAndLastViewsForAllSections = this.mSectionsManager.updateFirstAndLastViewsForAllSections(this.mSections, getChildrenWithBackground());
        if (!this.mAnimationsEnabled || !this.mIsExpanded) {
            this.mAnimateNextBackgroundTop = false;
            this.mAnimateNextBackgroundBottom = false;
            this.mAnimateNextSectionBoundsChange = false;
        } else {
            boolean z = true;
            this.mAnimateNextBackgroundTop = firstChildWithBackground != expandableView;
            if (lastChildWithBackground == expandableView2 && !this.mAnimateBottomOnLayout) {
                z = false;
            }
            this.mAnimateNextBackgroundBottom = z;
            this.mAnimateNextSectionBoundsChange = updateFirstAndLastViewsForAllSections;
        }
        this.mAmbientState.setLastVisibleBackgroundChild(lastChildWithBackground);
        this.mController.getNoticationRoundessManager().updateRoundedChildren(this.mSections);
        this.mAnimateBottomOnLayout = false;
        invalidate();
    }

    private void onViewAddedInternal(ExpandableView expandableView) {
        updateHideSensitiveForChild(expandableView);
        expandableView.setOnHeightChangedListener(this.mOnChildHeightChangedListener);
        generateAddAnimation(expandableView, false);
        updateAnimationState(expandableView);
        updateChronometerForChild(expandableView);
        if (expandableView instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow) expandableView).setDismissRtl(this.mDismissRtl);
        }
    }

    private void updateHideSensitiveForChild(ExpandableView expandableView) {
        expandableView.setHideSensitiveForIntrinsicHeight(this.mAmbientState.isHideSensitive());
    }

    public void notifyGroupChildRemoved(ExpandableView expandableView, ViewGroup viewGroup) {
        onViewRemovedInternal(expandableView, viewGroup);
    }

    public void notifyGroupChildAdded(ExpandableView expandableView) {
        onViewAddedInternal(expandableView);
    }

    private void updateNotificationAnimationStates() {
        boolean z = this.mAnimationsEnabled || hasPulsingNotifications();
        NotificationShelf notificationShelf = this.mShelf;
        if (notificationShelf != null) {
            notificationShelf.setAnimationsEnabled(z);
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            z &= this.mIsExpanded || isPinnedHeadsUp(childAt);
            updateAnimationState(z, childAt);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateAnimationState(View view) {
        updateAnimationState((this.mAnimationsEnabled || hasPulsingNotifications()) && (this.mIsExpanded || isPinnedHeadsUp(view)), view);
    }

    /* access modifiers changed from: package-private */
    public void setExpandingNotification(ExpandableNotificationRow expandableNotificationRow) {
        requestChildrenUpdate();
    }

    public boolean containsView(View view) {
        return view.getParent() == this;
    }

    public void applyExpandAnimationParams(ExpandAnimationParameters expandAnimationParameters) {
        requestChildrenUpdate();
    }

    private void updateAnimationState(boolean z, View view) {
        if (view instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow) view).setIconAnimationRunning(z);
        }
    }

    public void generateAddAnimation(ExpandableView expandableView, boolean z) {
        if (this.mIsExpanded && this.mAnimationsEnabled && !this.mChangePositionInProgress && !isFullyHidden()) {
            this.mChildrenToAddAnimated.add(expandableView);
            if (z) {
                this.mFromMoreCardAdditions.add(expandableView);
            }
            this.mNeedsAnimation = true;
        }
        if (isHeadsUp(expandableView) && this.mAnimationsEnabled && !this.mChangePositionInProgress && !isFullyHidden()) {
            this.mAddedHeadsUpChildren.add(expandableView);
            this.mChildrenToAddAnimated.remove(expandableView);
        }
    }

    public void changeViewPosition(ExpandableView expandableView, int i) {
        Assert.isMainThread();
        if (!this.mChangePositionInProgress) {
            int indexOfChild = indexOfChild(expandableView);
            boolean z = false;
            if (indexOfChild == -1) {
                if ((expandableView instanceof ExpandableNotificationRow) && expandableView.getTransientContainer() != null) {
                    z = true;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Attempting to re-position ");
                sb.append(z ? "transient" : "");
                sb.append(" view {");
                sb.append(expandableView);
                sb.append("}");
                Log.e("DekstopStackScroller", sb.toString());
            } else if (expandableView != null && expandableView.getParent() == this && indexOfChild != i) {
                this.mChangePositionInProgress = true;
                expandableView.setChangingPosition(true);
                removeView(expandableView);
                if (expandableView.getParent() != null) {
                    Log.wtf("DekstopStackScroller", "Trying to readd a notification child that already has a parent:" + expandableView.getParent().getClass() + ", child: " + expandableView);
                } else {
                    addView(expandableView, i);
                }
                expandableView.setChangingPosition(false);
                this.mChangePositionInProgress = false;
                if (this.mIsExpanded && this.mAnimationsEnabled && expandableView.getVisibility() != 8) {
                    this.mChildrenChangingPositions.add(expandableView);
                    this.mNeedsAnimation = true;
                }
            }
        } else {
            throw new IllegalStateException("Reentrant call to changeViewPosition");
        }
    }

    private void startAnimationToState() {
        if (this.mNeedsAnimation) {
            generateAllAnimationEvents();
            this.mNeedsAnimation = false;
        }
        if (!this.mAnimationEvents.isEmpty() || isCurrentlyAnimating()) {
            setAnimationRunning(true);
            this.mStateAnimator.startAnimationForEvents(this.mAnimationEvents, this.mGoToFullShadeDelay);
            this.mAnimationEvents.clear();
            updateBackground();
            updateViewShadows();
        } else {
            applyCurrentState();
        }
        this.mGoToFullShadeDelay = 0;
    }

    private void generateAllAnimationEvents() {
        generateChildRemovalEvents();
        generateChildAdditionEvents();
        generatePositionChangeEvents();
        generateTopPaddingEvent();
        generateActivateEvent();
        generateDimmedEvent();
        generateHideSensitiveEvent();
        generateGoToFullShadeEvent();
        generateViewResizeEvent();
        generateGroupExpansionEvent();
        generateAnimateEverythingEvent();
    }

    private void generateGroupExpansionEvent() {
        if (this.mExpandedGroupView != null) {
            this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent(this.mExpandedGroupView, 10));
            this.mExpandedGroupView = null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0023 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:5:0x0011  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void generateViewResizeEvent() {
        /*
            r5 = this;
            boolean r0 = r5.mNeedViewResizeAnimation
            r1 = 0
            if (r0 == 0) goto L_0x0033
            java.util.ArrayList<com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent> r0 = r5.mAnimationEvents
            java.util.Iterator r0 = r0.iterator()
        L_0x000b:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x0023
            java.lang.Object r2 = r0.next()
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent r2 = (com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.AnimationEvent) r2
            int r2 = r2.animationType
            r3 = 13
            if (r2 == r3) goto L_0x0021
            r3 = 12
            if (r2 != r3) goto L_0x000b
        L_0x0021:
            r0 = 1
            goto L_0x0024
        L_0x0023:
            r0 = r1
        L_0x0024:
            if (r0 != 0) goto L_0x0033
            java.util.ArrayList<com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent> r0 = r5.mAnimationEvents
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent r2 = new com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent
            r3 = 0
            r4 = 9
            r2.<init>(r3, r4)
            r0.add(r2)
        L_0x0033:
            r5.mNeedViewResizeAnimation = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout.generateViewResizeEvent():void");
    }

    private void generateChildRemovalEvents() {
        boolean z;
        ViewGroup transientContainer;
        Iterator<ExpandableView> it = this.mChildrenToRemoveAnimated.iterator();
        while (it.hasNext()) {
            ExpandableView next = it.next();
            boolean contains = this.mSwipedOutViews.contains(next);
            float translationY = next.getTranslationY();
            boolean z2 = false;
            int i = 1;
            if (next instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) next;
                if (!expandableNotificationRow.isRemoved() || !expandableNotificationRow.wasChildInGroupWhenRemoved()) {
                    z = true;
                } else {
                    translationY = expandableNotificationRow.getTranslationWhenRemoved();
                    z = false;
                }
                contains |= Math.abs(expandableNotificationRow.getTranslation()) == ((float) expandableNotificationRow.getWidth());
            } else if (next instanceof MediaHeaderView) {
                contains = true;
                z = true;
            } else {
                z = true;
            }
            if (!contains) {
                Rect clipBounds = next.getClipBounds();
                if (clipBounds != null && clipBounds.height() == 0) {
                    z2 = true;
                }
                if (z2 && (transientContainer = next.getTransientContainer()) != null) {
                    transientContainer.removeTransientView(next);
                }
                contains = z2;
            }
            if (contains) {
                i = 2;
            }
            NotificationStackScrollLayout.AnimationEvent animationEvent = new NotificationStackScrollLayout.AnimationEvent(next, i);
            animationEvent.viewAfterChangingView = getFirstChildBelowTranlsationY(translationY, z);
            this.mAnimationEvents.add(animationEvent);
            this.mSwipedOutViews.remove(next);
        }
        this.mChildrenToRemoveAnimated.clear();
    }

    private void generatePositionChangeEvents() {
        NotificationStackScrollLayout.AnimationEvent animationEvent;
        Iterator<ExpandableView> it = this.mChildrenChangingPositions.iterator();
        while (true) {
            Integer num = null;
            if (!it.hasNext()) {
                break;
            }
            ExpandableView next = it.next();
            if (next instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) next;
                if (expandableNotificationRow.getEntry().isMarkedForUserTriggeredMovement()) {
                    num = 500;
                    expandableNotificationRow.getEntry().markForUserTriggeredMovement(false);
                }
            }
            if (num == null) {
                animationEvent = new NotificationStackScrollLayout.AnimationEvent(next, 6);
            } else {
                animationEvent = new NotificationStackScrollLayout.AnimationEvent(next, 6, (long) num.intValue());
            }
            this.mAnimationEvents.add(animationEvent);
        }
        this.mChildrenChangingPositions.clear();
        if (this.mGenerateChildOrderChangedEvent) {
            this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent((ExpandableView) null, 6));
            this.mGenerateChildOrderChangedEvent = false;
        }
    }

    private void generateChildAdditionEvents() {
        Iterator<ExpandableView> it = this.mChildrenToAddAnimated.iterator();
        while (it.hasNext()) {
            ExpandableView next = it.next();
            if (this.mFromMoreCardAdditions.contains(next)) {
                this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent(next, 0, 360));
            } else {
                this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent(next, 0));
            }
        }
        this.mChildrenToAddAnimated.clear();
        this.mFromMoreCardAdditions.clear();
    }

    private void generateTopPaddingEvent() {
        NotificationStackScrollLayout.AnimationEvent animationEvent;
        if (this.mTopPaddingNeedsAnimation) {
            if (this.mAmbientState.isDozing()) {
                animationEvent = new NotificationStackScrollLayout.AnimationEvent((ExpandableView) null, 3, 550);
            } else {
                animationEvent = new NotificationStackScrollLayout.AnimationEvent((ExpandableView) null, 3);
            }
            this.mAnimationEvents.add(animationEvent);
        }
        this.mTopPaddingNeedsAnimation = false;
    }

    private void generateActivateEvent() {
        if (this.mActivateNeedsAnimation) {
            this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent((ExpandableView) null, 4));
        }
        this.mActivateNeedsAnimation = false;
    }

    private void generateAnimateEverythingEvent() {
        if (this.mEverythingNeedsAnimation) {
            this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent((ExpandableView) null, 15));
        }
        this.mEverythingNeedsAnimation = false;
    }

    private void generateDimmedEvent() {
        if (this.mDimmedNeedsAnimation) {
            this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent((ExpandableView) null, 5));
        }
        this.mDimmedNeedsAnimation = false;
    }

    private void generateHideSensitiveEvent() {
        if (this.mHideSensitiveNeedsAnimation) {
            this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent((ExpandableView) null, 8));
        }
        this.mHideSensitiveNeedsAnimation = false;
    }

    private void generateGoToFullShadeEvent() {
        if (this.mGoToFullShadeNeedsAnimation) {
            this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent((ExpandableView) null, 7));
        }
        this.mGoToFullShadeNeedsAnimation = false;
    }

    /* access modifiers changed from: protected */
    public StackScrollAlgorithm createStackScrollAlgorithm(Context context) {
        return new StackScrollAlgorithm(context, this);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        DesktopNotificationStackScrollLayoutController.TouchHandler touchHandler = this.mTouchHandler;
        if (touchHandler == null || !touchHandler.onTouchEvent(motionEvent)) {
            return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if (!this.mIsExpanded || this.mSwipeHelper.isSwiping() || this.mExpandingNotification || this.mDisallowScrollingInThisMotion) {
            return false;
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        DesktopNotificationStackScrollLayoutController.TouchHandler touchHandler = this.mTouchHandler;
        if (touchHandler == null || !touchHandler.onInterceptTouchEvent(motionEvent)) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return true;
    }

    public void requestDisallowLongPress() {
        cancelLongPress();
    }

    public void requestDisallowDismiss() {
        this.mDisallowDismissInThisMotion = true;
    }

    public void cancelLongPress() {
        this.mSwipeHelper.cancelLongPress();
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!z) {
            cancelLongPress();
        }
    }

    public void clearChildFocus(View view) {
        super.clearChildFocus(view);
        if (this.mForcedScroll == view) {
            this.mForcedScroll = null;
        }
    }

    private void setIsExpanded(boolean z) {
        boolean z2 = z != this.mIsExpanded;
        this.mIsExpanded = z;
        this.mStackScrollAlgorithm.setIsExpanded(z);
        this.mAmbientState.setShadeExpanded(z);
        this.mStateAnimator.setShadeExpanded(z);
        this.mSwipeHelper.setIsExpanded(z);
        if (z2) {
            this.mWillExpand = false;
            if (!this.mIsExpanded) {
                this.mGroupExpansionManager.collapseGroups();
            }
            updateNotificationAnimationStates();
            updateChronometers();
            requestChildrenUpdate();
        }
    }

    private void updateChronometers() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            updateChronometerForChild(getChildAt(i));
        }
    }

    /* access modifiers changed from: package-private */
    public void updateChronometerForChild(View view) {
        if (view instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow) view).setChronometerRunning(this.mIsExpanded);
        }
    }

    /* access modifiers changed from: package-private */
    public void onChildHeightChanged(ExpandableView expandableView, boolean z) {
        updateContentHeight();
        notifyHeightChangeListener(expandableView, z);
        ExpandableView expandableView2 = null;
        ExpandableNotificationRow expandableNotificationRow = expandableView instanceof ExpandableNotificationRow ? (ExpandableNotificationRow) expandableView : null;
        NotificationSection firstVisibleSection = getFirstVisibleSection();
        if (firstVisibleSection != null) {
            expandableView2 = firstVisibleSection.getFirstVisibleChild();
        }
        if (expandableNotificationRow != null && (expandableNotificationRow == expandableView2 || expandableNotificationRow.getNotificationParent() == expandableView2)) {
            updateAlgorithmLayoutMinHeight();
        }
        if (z) {
            requestAnimationOnViewResize(expandableNotificationRow);
        }
        requestChildrenUpdate();
    }

    /* access modifiers changed from: package-private */
    public void onChildHeightReset(ExpandableView expandableView) {
        updateAnimationState(expandableView);
        updateChronometerForChild(expandableView);
    }

    /* access modifiers changed from: package-private */
    public void onChildAnimationFinished() {
        setAnimationRunning(false);
        requestChildrenUpdate();
        runAnimationFinishedRunnables();
        clearTransient();
        clearHeadsUpDisappearRunning();
    }

    private void clearHeadsUpDisappearRunning() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) childAt;
                expandableNotificationRow.setHeadsUpAnimatingAway(false);
                if (expandableNotificationRow.isSummaryWithChildren()) {
                    for (ExpandableNotificationRow headsUpAnimatingAway : expandableNotificationRow.getAttachedChildren()) {
                        headsUpAnimatingAway.setHeadsUpAnimatingAway(false);
                    }
                }
            }
        }
    }

    private void clearTransient() {
        Iterator<ExpandableView> it = this.mClearTransientViewsWhenFinished.iterator();
        while (it.hasNext()) {
            StackStateAnimator.removeTransientView(it.next());
        }
        this.mClearTransientViewsWhenFinished.clear();
    }

    private void runAnimationFinishedRunnables() {
        Iterator<Runnable> it = this.mAnimationFinishedRunnables.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mAnimationFinishedRunnables.clear();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isDimmed() {
        return this.mAmbientState.isDimmed();
    }

    /* access modifiers changed from: private */
    public void setDimAmount(float f) {
        this.mDimAmount = f;
        updateBackgroundDimming();
    }

    /* access modifiers changed from: package-private */
    public void updateSensitiveness(boolean z, boolean z2) {
        if (z2 != this.mAmbientState.isHideSensitive()) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                ((ExpandableView) getChildAt(i)).setHideSensitiveForIntrinsicHeight(z2);
            }
            this.mAmbientState.setHideSensitive(z2);
            if (z && this.mAnimationsEnabled) {
                this.mHideSensitiveNeedsAnimation = true;
                this.mNeedsAnimation = true;
            }
            updateContentHeight();
            requestChildrenUpdate();
        }
    }

    private void applyCurrentState() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((ExpandableView) getChildAt(i)).applyViewState();
        }
        runAnimationFinishedRunnables();
        setAnimationRunning(false);
        updateBackground();
        updateViewShadows();
    }

    private void updateViewShadows() {
        float f;
        for (int i = 0; i < getChildCount(); i++) {
            ExpandableView expandableView = (ExpandableView) getChildAt(i);
            if (expandableView.getVisibility() != 8) {
                this.mTmpSortedChildren.add(expandableView);
            }
        }
        Collections.sort(this.mTmpSortedChildren, this.mViewPositionComparator);
        ExpandableView expandableView2 = null;
        int i2 = 0;
        while (i2 < this.mTmpSortedChildren.size()) {
            ExpandableView expandableView3 = this.mTmpSortedChildren.get(i2);
            float translationZ = expandableView3.getTranslationZ();
            if (expandableView2 == null) {
                f = translationZ;
            } else {
                f = expandableView2.getTranslationZ();
            }
            float f2 = f - translationZ;
            if (f2 <= 0.0f || f2 >= 0.1f) {
                expandableView3.setFakeShadowIntensity(0.0f, 0.0f, 0, 0);
            } else {
                expandableView3.setFakeShadowIntensity(f2 / 0.1f, expandableView2.getOutlineAlpha(), (int) (((expandableView2.getTranslationY() + ((float) expandableView2.getActualHeight())) - expandableView3.getTranslationY()) - ((float) expandableView2.getExtraBottomPadding())), expandableView2.getOutlineTranslation());
            }
            i2++;
            expandableView2 = expandableView3;
        }
        this.mTmpSortedChildren.clear();
    }

    /* access modifiers changed from: package-private */
    public void updateDecorViews() {
        int colorAttrDefaultColor = com.android.settingslib.Utils.getColorAttrDefaultColor(this.mContext, 16842806);
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            this.mSectionsManager.setHeaderForegroundColor(colorAttrDefaultColor);
        } else {
            this.mSectionsManager.setHeaderForegroundColor(this.mContext.getColor(R$color.desktop_qs_notification_sections_head_text));
        }
        this.mFooterView.updateColors();
        this.mEmptyShadeView.setTextColor(colorAttrDefaultColor);
        CarrierLabelUpdateMonitor.getInstance().updateTextColor(colorAttrDefaultColor);
    }

    /* access modifiers changed from: package-private */
    public void setIntrinsicPadding(int i) {
        this.mIntrinsicPadding = i;
        this.mAmbientState.setIntrinsicPadding(i);
    }

    private void updateOwnTranslationZ() {
        setTranslationZ(0.0f);
    }

    /* access modifiers changed from: package-private */
    public void setFooterView(FooterView footerView) {
        int i;
        FooterView footerView2 = this.mFooterView;
        if (footerView2 != null) {
            i = indexOfChild(footerView2);
            removeView(this.mFooterView);
        } else {
            i = -1;
        }
        this.mFooterView = footerView;
        addView(footerView, i);
    }

    /* access modifiers changed from: package-private */
    public void setEmptyShadeView(EmptyShadeView emptyShadeView) {
        int i;
        EmptyShadeView emptyShadeView2 = this.mEmptyShadeView;
        if (emptyShadeView2 != null) {
            i = indexOfChild(emptyShadeView2);
            removeView(this.mEmptyShadeView);
        } else {
            i = -1;
        }
        this.mEmptyShadeView = emptyShadeView;
        addView(emptyShadeView, i);
    }

    /* access modifiers changed from: package-private */
    public void updateEmptyShadeView(boolean z, boolean z2) {
        this.mEmptyShadeView.setVisible(z, this.mIsExpanded && this.mAnimationsEnabled);
        int textResource = this.mEmptyShadeView.getTextResource();
        int i = z2 ? R$string.dnd_suppressing_shade_text : R$string.empty_shade_text;
        if (textResource != i) {
            this.mEmptyShadeView.setText(i);
        }
    }

    public void updateFooterView(boolean z, boolean z2, boolean z3) {
        FooterView footerView = this.mFooterView;
        if (footerView != null) {
            boolean z4 = this.mIsExpanded && this.mAnimationsEnabled;
            footerView.setVisible(z, z4);
            this.mFooterView.setSecondaryVisible(z2, z4);
            this.mFooterView.showHistory(z3);
            this.mFooterView.disableManageButton();
        }
    }

    public void setDismissAllInProgress(boolean z) {
        this.mDismissAllInProgress = z;
        this.mAmbientState.setDismissAllInProgress(z);
        handleDismissAllClipping();
    }

    /* access modifiers changed from: package-private */
    public boolean getDismissAllInProgress() {
        return this.mDismissAllInProgress;
    }

    private void handleDismissAllClipping() {
        int childCount = getChildCount();
        boolean z = false;
        for (int i = 0; i < childCount; i++) {
            ExpandableView expandableView = (ExpandableView) getChildAt(i);
            if (expandableView.getVisibility() != 8) {
                if (!this.mDismissAllInProgress || !z) {
                    expandableView.setMinClipTopAmount(0);
                } else {
                    expandableView.setMinClipTopAmount(expandableView.getClipTopAmount());
                }
                z = canChildBeDismissed(expandableView);
            }
        }
    }

    public void setStatusBar(StatusBar statusBar) {
        this.mStatusBar = statusBar;
    }

    public void generateChildOrderChangedEvent() {
        if (this.mIsExpanded && this.mAnimationsEnabled) {
            this.mGenerateChildOrderChangedEvent = true;
            this.mNeedsAnimation = true;
            requestChildrenUpdate();
        }
    }

    public int getContainerChildCount() {
        return getChildCount();
    }

    public View getContainerChildAt(int i) {
        return getChildAt(i);
    }

    public void removeContainerView(View view) {
        Assert.isMainThread();
        removeView(view);
        if ((view instanceof ExpandableNotificationRow) && !this.mController.isShowingEmptyShadeView()) {
            this.mController.updateShowEmptyShadeView();
            updateFooter();
        }
        updateSpeedBumpIndex();
    }

    public void addContainerView(View view) {
        Assert.isMainThread();
        addView(view);
        if ((view instanceof ExpandableNotificationRow) && this.mController.isShowingEmptyShadeView()) {
            this.mController.updateShowEmptyShadeView();
            updateFooter();
        }
        updateSpeedBumpIndex();
    }

    public void addContainerViewAt(View view, int i) {
        Assert.isMainThread();
        addView(view, i);
        if ((view instanceof ExpandableNotificationRow) && this.mController.isShowingEmptyShadeView()) {
            this.mController.updateShowEmptyShadeView();
            updateFooter();
        }
        updateSpeedBumpIndex();
    }

    public void runAfterAnimationFinished(Runnable runnable) {
        this.mAnimationFinishedRunnables.add(runnable);
    }

    public boolean hasOverlappingRendering() {
        return !this.mForceNoOverlappingRendering && super.hasOverlappingRendering();
    }

    public void setAnimationRunning(boolean z) {
        if (z != this.mAnimationRunning) {
            if (z) {
                getViewTreeObserver().addOnPreDrawListener(this.mRunningAnimationUpdater);
            } else {
                getViewTreeObserver().removeOnPreDrawListener(this.mRunningAnimationUpdater);
            }
            this.mAnimationRunning = z;
            updateContinuousShadowDrawing();
        }
    }

    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    public void setInHeadsUpPinnedMode(boolean z) {
        this.mInHeadsUpPinnedMode = z;
        updateClipping();
    }

    public void setHeadsUpGoingAwayAnimationsAllowed(boolean z) {
        this.mHeadsUpGoingAwayAnimationsAllowed = z;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        String str2;
        Object[] objArr = new Object[8];
        objArr[0] = getClass().getSimpleName();
        String str3 = "T";
        objArr[1] = this.mPulsing ? str3 : "f";
        if (this.mAmbientState.isQsCustomizerShowing()) {
            str = str3;
        } else {
            str = "f";
        }
        objArr[2] = str;
        if (getVisibility() == 0) {
            str2 = "visible";
        } else {
            str2 = getVisibility() == 8 ? "gone" : "invisible";
        }
        objArr[3] = str2;
        objArr[4] = Float.valueOf(getAlpha());
        objArr[5] = Integer.valueOf(this.mAmbientState.getScrollY());
        objArr[6] = Integer.valueOf(this.mMaxTopPadding);
        if (!this.mShouldShowShelfOnly) {
            str3 = "f";
        }
        objArr[7] = str3;
        printWriter.println(String.format("[%s: pulsing=%s qsCustomizerShowing=%s visibility=%s alpha:%f scrollY:%d maxTopPadding:%d showShelfOnly=%s", objArr));
        int childCount = getChildCount();
        printWriter.println("  Number of children: " + childCount);
        printWriter.println();
        for (int i = 0; i < childCount; i++) {
            ExpandableView expandableView = (ExpandableView) getChildAt(i);
            expandableView.dump(fileDescriptor, printWriter, strArr);
            if (!(expandableView instanceof ExpandableNotificationRow)) {
                printWriter.println("  " + expandableView.getClass().getSimpleName());
                ExpandableViewState viewState = expandableView.getViewState();
                if (viewState == null) {
                    printWriter.println("    no viewState!!!");
                } else {
                    printWriter.print("    ");
                    viewState.dump(fileDescriptor, printWriter, strArr);
                    printWriter.println();
                    printWriter.println();
                }
            }
        }
        int transientViewCount = getTransientViewCount();
        printWriter.println("  Transient Views: " + transientViewCount);
        for (int i2 = 0; i2 < transientViewCount; i2++) {
            ((ExpandableView) getTransientView(i2)).dump(fileDescriptor, printWriter, strArr);
        }
        View swipedView = this.mSwipeHelper.getSwipedView();
        printWriter.println("  Swiped view: " + swipedView);
        if (swipedView instanceof ExpandableView) {
            ((ExpandableView) swipedView).dump(fileDescriptor, printWriter, strArr);
        }
    }

    public boolean isFullyHidden() {
        return this.mAmbientState.isFullyHidden();
    }

    public void addOnExpandedHeightChangedListener(BiConsumer<Float, Float> biConsumer) {
        this.mExpandedHeightListeners.add(biConsumer);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004f, code lost:
        if (r11.mTmpRect.height() > 0) goto L_0x0053;
     */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0059  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0094 A[SYNTHETIC] */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void clearNotifications(int r12, boolean r13) {
        /*
            r11 = this;
            int r0 = r11.getChildCount()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>(r0)
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>(r0)
            r3 = 0
            r4 = r3
        L_0x0010:
            if (r4 >= r0) goto L_0x0098
            android.view.View r5 = r11.getChildAt(r4)
            boolean r6 = r5 instanceof com.android.systemui.statusbar.notification.row.ExpandableNotificationRow
            if (r6 == 0) goto L_0x0094
            r6 = r5
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r6 = (com.android.systemui.statusbar.notification.row.ExpandableNotificationRow) r6
            android.graphics.Rect r7 = r11.mTmpRect
            boolean r7 = r5.getClipBounds(r7)
            boolean r8 = r11.includeChildInDismissAll(r6, r12)
            r9 = 1
            if (r8 == 0) goto L_0x0041
            r2.add(r6)
            int r8 = r5.getVisibility()
            if (r8 != 0) goto L_0x0052
            if (r7 == 0) goto L_0x003d
            android.graphics.Rect r7 = r11.mTmpRect
            int r7 = r7.height()
            if (r7 <= 0) goto L_0x0052
        L_0x003d:
            r1.add(r5)
            goto L_0x0053
        L_0x0041:
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x0052
            if (r7 == 0) goto L_0x0053
            android.graphics.Rect r5 = r11.mTmpRect
            int r5 = r5.height()
            if (r5 <= 0) goto L_0x0052
            goto L_0x0053
        L_0x0052:
            r9 = r3
        L_0x0053:
            java.util.List r5 = r6.getAttachedChildren()
            if (r5 == 0) goto L_0x0094
            java.util.Iterator r5 = r5.iterator()
        L_0x005d:
            boolean r7 = r5.hasNext()
            if (r7 == 0) goto L_0x0094
            java.lang.Object r7 = r5.next()
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r7 = (com.android.systemui.statusbar.notification.row.ExpandableNotificationRow) r7
            boolean r8 = r11.includeChildInDismissAll(r6, r12)
            if (r8 == 0) goto L_0x005d
            r2.add(r7)
            if (r9 == 0) goto L_0x005d
            boolean r8 = r6.areChildrenExpanded()
            if (r8 == 0) goto L_0x005d
            android.graphics.Rect r8 = r11.mTmpRect
            boolean r8 = r7.getClipBounds(r8)
            int r10 = r7.getVisibility()
            if (r10 != 0) goto L_0x005d
            if (r8 == 0) goto L_0x0090
            android.graphics.Rect r8 = r11.mTmpRect
            int r8 = r8.height()
            if (r8 <= 0) goto L_0x005d
        L_0x0090:
            r1.add(r7)
            goto L_0x005d
        L_0x0094:
            int r4 = r4 + 1
            goto L_0x0010
        L_0x0098:
            com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout$DismissListener r0 = r11.mDismissListener
            if (r0 == 0) goto L_0x009f
            r0.onDismiss(r12)
        L_0x009f:
            boolean r0 = r2.isEmpty()
            if (r0 == 0) goto L_0x00a6
            return
        L_0x00a6:
            com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda6 r0 = new com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda6
            r0.<init>(r11, r2, r12)
            r11.performDismissAllAnimations(r1, r13, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout.clearNotifications(int, boolean):void");
    }

    private boolean includeChildInDismissAll(ExpandableNotificationRow expandableNotificationRow, int i) {
        return canChildBeDismissed(expandableNotificationRow) && matchesSelection(expandableNotificationRow, i);
    }

    private void performDismissAllAnimations(ArrayList<View> arrayList, boolean z, Runnable runnable) {
        DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda5 desktopNotificationStackScrollLayout$$ExternalSyntheticLambda5 = new DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda5(this, runnable);
        if (arrayList.isEmpty()) {
            desktopNotificationStackScrollLayout$$ExternalSyntheticLambda5.run();
            return;
        }
        setDismissAllInProgress(true);
        int i = 140;
        int i2 = 180;
        int size = arrayList.size() - 1;
        while (size >= 0) {
            dismissViewAnimated(arrayList.get(size), size == 0 ? desktopNotificationStackScrollLayout$$ExternalSyntheticLambda5 : null, i2, 260);
            i = Math.max(50, i - 10);
            i2 += i;
            size--;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performDismissAllAnimations$4(Runnable runnable) {
        setDismissAllInProgress(false);
        runnable.run();
    }

    public void setNotificationActivityStarter(NotificationActivityStarter notificationActivityStarter) {
        this.mNotificationActivityStarter = notificationActivityStarter;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void inflateFooterView() {
        FooterView footerView = (FooterView) LayoutInflater.from(this.mContext).inflate(R$layout.status_bar_notification_footer, this, false);
        footerView.setDismissButtonClickListener(new DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda0(this));
        footerView.setManageButtonClickListener(new DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda1(this));
        setFooterView(footerView);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$inflateFooterView$5(View view) {
        FooterDismissListener footerDismissListener = this.mFooterDismissListener;
        if (footerDismissListener != null) {
            footerDismissListener.onDismiss();
        }
        clearNotifications(0, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$inflateFooterView$6(View view) {
        this.mNotificationActivityStarter.startHistoryIntent(view, this.mFooterView.isHistoryShown());
    }

    private void inflateEmptyShadeView() {
        EmptyShadeView emptyShadeView = (EmptyShadeView) LayoutInflater.from(this.mContext).inflate(R$layout.status_bar_no_notifications, this, false);
        emptyShadeView.setText(R$string.empty_shade_text);
        emptyShadeView.setOnClickListener(new DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda2(this));
        setEmptyShadeView(emptyShadeView);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$inflateEmptyShadeView$7(View view) {
        Intent intent;
        boolean z = false;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_history_enabled", 0, -2) == 1) {
            z = true;
        }
        if (z) {
            intent = new Intent("android.settings.NOTIFICATION_HISTORY");
        } else {
            intent = new Intent("android.settings.NOTIFICATION_SETTINGS");
        }
        this.mStatusBar.startActivity(intent, true, true, 536870912);
    }

    public void onUpdateRowStates() {
        ForegroundServiceDungeonView foregroundServiceDungeonView = this.mFgsSectionView;
        int i = 1;
        if (foregroundServiceDungeonView != null) {
            changeViewPosition(foregroundServiceDungeonView, getChildCount() - 1);
            i = 2;
        }
        int i2 = i + 1;
        changeViewPosition(this.mFooterView, getChildCount() - i);
        int i3 = i2 + 1;
        changeViewPosition(this.mEmptyShadeView, getChildCount() - i2);
        NotificationShelf notificationShelf = this.mShelf;
        if (notificationShelf != null) {
            changeViewPosition(notificationShelf, getChildCount() - i3);
        }
    }

    public boolean isFullyAwake() {
        return this.mAmbientState.isFullyAwake();
    }

    /* access modifiers changed from: package-private */
    public void setAnimateBottomOnLayout(boolean z) {
        this.mAnimateBottomOnLayout = z;
    }

    public void setController(DesktopNotificationStackScrollLayoutController desktopNotificationStackScrollLayoutController) {
        this.mController = desktopNotificationStackScrollLayoutController;
        desktopNotificationStackScrollLayoutController.getNoticationRoundessManager().setAnimatedChildren(this.mChildrenToAddAnimated);
    }

    /* access modifiers changed from: package-private */
    public void addSwipedOutView(View view) {
        this.mSwipedOutViews.add(view);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0026, code lost:
        if (r6.mSectionsManager.beginsSection(r7, r2) != false) goto L_0x0028;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSwipeBegin(android.view.View r7) {
        /*
            r6 = this;
            boolean r0 = r7 instanceof com.android.systemui.statusbar.notification.row.ExpandableNotificationRow
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            int r0 = r6.indexOfChild(r7)
            if (r0 >= 0) goto L_0x000c
            return
        L_0x000c:
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager r1 = r6.mSectionsManager
            com.android.systemui.statusbar.notification.stack.NotificationSection[] r2 = r6.mSections
            java.util.List r3 = r6.getChildrenWithBackground()
            r1.updateFirstAndLastViewsForAllSections(r2, r3)
            r1 = 0
            if (r0 <= 0) goto L_0x0028
            int r2 = r0 + -1
            android.view.View r2 = r6.getChildAt(r2)
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager r3 = r6.mSectionsManager
            boolean r3 = r3.beginsSection(r7, r2)
            if (r3 == 0) goto L_0x0029
        L_0x0028:
            r2 = r1
        L_0x0029:
            int r3 = r6.getChildCount()
            r4 = 1
            if (r0 >= r3) goto L_0x003f
            int r0 = r0 + r4
            android.view.View r0 = r6.getChildAt(r0)
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager r3 = r6.mSectionsManager
            boolean r3 = r3.beginsSection(r0, r7)
            if (r3 == 0) goto L_0x003e
            goto L_0x003f
        L_0x003e:
            r1 = r0
        L_0x003f:
            com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayoutController r0 = r6.mController
            com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager r0 = r0.getNoticationRoundessManager()
            com.android.systemui.statusbar.notification.row.ExpandableView r2 = (com.android.systemui.statusbar.notification.row.ExpandableView) r2
            com.android.systemui.statusbar.notification.row.ExpandableView r7 = (com.android.systemui.statusbar.notification.row.ExpandableView) r7
            com.android.systemui.statusbar.notification.row.ExpandableView r1 = (com.android.systemui.statusbar.notification.row.ExpandableView) r1
            android.content.res.Resources r3 = r6.getResources()
            int r5 = com.android.systemui.R$bool.flag_notif_updates
            boolean r3 = r3.getBoolean(r5)
            r0.setViewsAffectedBySwipe(r2, r7, r1, r3)
            r6.updateFirstAndLastBackgroundViews()
            r6.requestDisallowInterceptTouchEvent(r4)
            r6.updateContinuousShadowDrawing()
            r6.updateContinuousBackgroundDrawing()
            r6.requestChildrenUpdate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout.onSwipeBegin(android.view.View):void");
    }

    /* access modifiers changed from: package-private */
    public void onSwipeEnd() {
        updateFirstAndLastBackgroundViews();
        this.mController.getNoticationRoundessManager().setViewsAffectedBySwipe((ExpandableView) null, (ExpandableView) null, (ExpandableView) null, getResources().getBoolean(R$bool.flag_notif_updates));
    }

    /* access modifiers changed from: package-private */
    public void setTouchHandler(DesktopNotificationStackScrollLayoutController.TouchHandler touchHandler) {
        this.mTouchHandler = touchHandler;
    }

    /* access modifiers changed from: package-private */
    public void setDismissAllAnimationListener(DismissAllAnimationListener dismissAllAnimationListener) {
        this.mDismissAllAnimationListener = dismissAllAnimationListener;
    }

    public void setRemoteInputManager(NotificationRemoteInputManager notificationRemoteInputManager) {
        this.mRemoteInputManager = notificationRemoteInputManager;
    }

    public float getTotalTranslationLength(View view) {
        if (!this.mDismissUsingRowTranslationX) {
            return (float) view.getMeasuredWidth();
        }
        float measuredWidth = (float) getMeasuredWidth();
        return measuredWidth - ((measuredWidth - ((float) view.getMeasuredWidth())) / 2.0f);
    }

    private void updateSpeedBumpIndex() {
        this.mSpeedBumpIndexDirty = true;
    }

    public void updateSectionBoundaries(String str) {
        this.mSectionsManager.updateSectionBoundaries(str);
    }

    /* access modifiers changed from: package-private */
    public void updateContinuousBackgroundDrawing() {
        boolean z = !this.mAmbientState.isFullyAwake() && this.mSwipeHelper.isSwiping();
        if (z != this.mContinuousBackgroundUpdate) {
            this.mContinuousBackgroundUpdate = z;
            if (z) {
                getViewTreeObserver().addOnPreDrawListener(this.mBackgroundUpdater);
            } else {
                getViewTreeObserver().removeOnPreDrawListener(this.mBackgroundUpdater);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateContinuousShadowDrawing() {
        boolean z = this.mAnimationRunning || this.mSwipeHelper.isSwiping();
        if (z != this.mContinuousShadowUpdate) {
            if (z) {
                getViewTreeObserver().addOnPreDrawListener(this.mShadowUpdater);
            } else {
                getViewTreeObserver().removeOnPreDrawListener(this.mShadowUpdater);
            }
            this.mContinuousShadowUpdate = z;
        }
    }

    static boolean matchesSelection(ExpandableNotificationRow expandableNotificationRow, int i) {
        if (i == 0) {
            return true;
        }
        if (i != 1) {
            if (i == 2) {
                return expandableNotificationRow.getEntry().getBucket() == 6;
            }
            throw new IllegalArgumentException("Unknown selection: " + i);
        } else if (expandableNotificationRow.getEntry().getBucket() < 6) {
            return true;
        } else {
            return false;
        }
    }

    static boolean canChildBeDismissed(View view) {
        if (view instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
            if (expandableNotificationRow.isBlockingHelperShowingAndTranslationFinished()) {
                return true;
            }
            if (expandableNotificationRow.areGutsExposed() || !expandableNotificationRow.getEntry().hasFinishedInitialization()) {
                return false;
            }
            return expandableNotificationRow.canViewBeDismissed();
        } else if (view instanceof PeopleHubView) {
            return ((PeopleHubView) view).getCanSwipe();
        } else {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void onEntryUpdated(NotificationEntry notificationEntry) {
        if (notificationEntry.rowExists() && !notificationEntry.getSbn().isClearable()) {
            snapViewIfNeeded(notificationEntry);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onDismissAllAnimationsEnd */
    public void lambda$clearNotifications$3(List<ExpandableNotificationRow> list, int i) {
        DismissAllAnimationListener dismissAllAnimationListener = this.mDismissAllAnimationListener;
        if (dismissAllAnimationListener != null) {
            dismissAllAnimationListener.onAnimationEnd(list, i);
        }
    }

    /* access modifiers changed from: package-private */
    public void onGroupExpandChanged(final ExpandableNotificationRow expandableNotificationRow, boolean z) {
        boolean z2 = this.mAnimationsEnabled && (this.mIsExpanded || expandableNotificationRow.isPinned());
        if (z2) {
            this.mExpandedGroupView = expandableNotificationRow;
            this.mNeedsAnimation = true;
        }
        expandableNotificationRow.setChildrenExpanded(z, z2);
        onChildHeightChanged(expandableNotificationRow, false);
        runAfterAnimationFinished(new Runnable() {
            public void run() {
                expandableNotificationRow.onFinishedExpansionChange();
            }
        });
    }
}
