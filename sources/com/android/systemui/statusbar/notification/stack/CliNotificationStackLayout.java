package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.ServiceManager;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.Dependency;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.SwipeHelper;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.CliNotificationFilter;
import com.android.systemui.statusbar.notification.CliNotificationSettings;
import com.android.systemui.statusbar.notification.ExpandAnimationParameters;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.CliNotificationStackClient;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm;
import com.android.systemui.statusbar.phone.CliStatusBar;
import com.android.systemui.statusbar.phone.CliStatusBarWindowController;
import com.android.systemui.statusbar.phone.NotificationIconContainer;
import com.android.systemui.util.Assert;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class CliNotificationStackLayout extends ViewGroup implements NotificationListContainer, CliNotificationStackClient.StackProxyListener, CliStatusBarWindowController.ICliChildView {
    private AmbientState mAmbientState;
    private ArrayList<NotificationStackScrollLayout.AnimationEvent> mAnimationEvents;
    private HashSet<Runnable> mAnimationFinishedRunnables;
    private int mAnimationType;
    private CliNotificationAnimationUtils mAnimationUtils;
    /* access modifiers changed from: private */
    public final IStatusBarService mBarService;
    /* access modifiers changed from: private */
    public boolean mCardExpand;
    private final int mCardTranlateY;
    private final int mCarouselIconSize;
    private boolean mChangePositionInProgress;
    private ArrayList<ExpandableView> mChildrenToRemoveAnimated;
    /* access modifiers changed from: private */
    public boolean mChildrenUpdateRequested;
    private ViewTreeObserver.OnPreDrawListener mChildrenUpdater;
    private Button mCleanButton;
    /* access modifiers changed from: private */
    public CliEducationView mCliEducationView;
    private View mCliKeyguardBlurBg;
    /* access modifiers changed from: private */
    public CliStatusBar mCliStatusBar;
    /* access modifiers changed from: private */
    public CliStatusBarWindowController mCliStatusBarWindowController;
    private final View.OnClickListener mClickListener;
    private StatusBarIconView mEducationIcon;
    /* access modifiers changed from: private */
    public NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public ExpandableView mExpandedGroupView;
    /* access modifiers changed from: private */
    public NotificationGroupManagerLegacy mGroupManager;
    private Handler mHandler;
    private HighPriorityProvider mHighPriorityProvider;
    /* access modifiers changed from: private */
    public NotificationIconContainer mIconContainer;
    private int mIntrinsicContentHeight;
    private View mKeyguardClockView;
    /* access modifiers changed from: private */
    public long mLastClickTime;
    private MotionEvent mLauncherDownEvent;
    /* access modifiers changed from: private */
    public NotificationLockscreenUserManager mLockscreenUserManager;
    private boolean mNeedViewResizeAnimation;
    /* access modifiers changed from: private */
    public boolean mNeedsAnimation;
    /* access modifiers changed from: private */
    public NotificationActivityStarter mNotificationAcitvityStarter;
    private StatusBarIconView mOldSelectedIconView;
    private ExpandableNotificationRow mOldSelectedView;
    private CliStatusBarWindowController.OnCliViewRequestListener mOnCliViewRequestListener;
    private final ExpandableNotificationRow.OnExpandClickListener mOnExpandClickListener;
    private final NotificationGroupManagerLegacy.OnGroupChangeListener mOnGroupChangeListener;
    private final GroupExpansionManager.OnGroupExpansionChangeListener mOnGroupExpansionChangeListener;
    private final int mPaddingTop;
    private View mParentView;
    private final Comparator<NotificationEntry> mRankingComparator;
    private final int mRowSpace;
    private StatusBarIconView mSelectedIconView;
    /* access modifiers changed from: private */
    public ExpandableNotificationRow mSelectedView;
    private final CliStackStateAnimator mStateAnimator;
    private final StatusBarStateController.StateListener mStateListener;
    /* access modifiers changed from: private */
    public int mStatusBarState;
    private final SwipeHelper.Callback mSwipeCallback;
    private SwipeHelper mSwipeHelper;
    private ArrayList<View> mSwipedOutViews;
    private View mTopPadingView;
    /* access modifiers changed from: private */
    public WaitForClick mWaitForClick;

    public void applyExpandAnimationParams(ExpandAnimationParameters expandAnimationParameters) {
    }

    public void bindRow(ExpandableNotificationRow expandableNotificationRow) {
    }

    public void cleanUpViewStateForEntry(NotificationEntry notificationEntry) {
    }

    public void generateAddAnimation(ExpandableView expandableView, boolean z) {
    }

    public void generateChildOrderChangedEvent() {
    }

    public int getIndexRow() {
        return 2;
    }

    public int getPulseTime() {
        return 30000;
    }

    public NotificationSwipeActionHelper getSwipeActionHelper() {
        return null;
    }

    public ViewGroup getViewParentForNotification(NotificationEntry notificationEntry) {
        return this;
    }

    public boolean hasPulsingNotifications() {
        return false;
    }

    public void onNotificationViewUpdateFinished() {
    }

    public void onReset(ExpandableView expandableView) {
    }

    public void resetExposedMenuView(boolean z, boolean z2) {
    }

    public void setChildLocationsChangedListener(NotificationLogger.OnChildLocationsChangedListener onChildLocationsChangedListener) {
    }

    public void setChildTransferInProgress(boolean z) {
    }

    public void setExpandingNotification(ExpandableNotificationRow expandableNotificationRow) {
    }

    public void setNotificationActivityStarter(NotificationActivityStarter notificationActivityStarter) {
    }

    public CliNotificationStackLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliNotificationStackLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public CliNotificationStackLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, -1);
    }

    public CliNotificationStackLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mChangePositionInProgress = false;
        this.mHandler = new Handler();
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mWaitForClick = new WaitForClick();
        this.mStateAnimator = new CliStackStateAnimator(this);
        this.mChildrenToRemoveAnimated = new ArrayList<>();
        this.mSwipedOutViews = new ArrayList<>();
        this.mAnimationEvents = new ArrayList<>();
        this.mAnimationFinishedRunnables = new HashSet<>();
        this.mClickListener = new View.OnClickListener() {
            public void onClick(View view) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                Log.d("Cli_NotificationStackScrollLayout", "onClick sbn=" + expandableNotificationRow.getEntry().getSbn() + ";expandable=" + expandableNotificationRow.isExpandable());
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - CliNotificationStackLayout.this.mLastClickTime < 200) {
                    CliNotificationStackLayout cliNotificationStackLayout = CliNotificationStackLayout.this;
                    cliNotificationStackLayout.removeCallbacks(cliNotificationStackLayout.mWaitForClick);
                    CliNotificationStackLayout.this.mWaitForClick.doubleClick();
                    long unused = CliNotificationStackLayout.this.mLastClickTime = 0;
                    return;
                }
                long unused2 = CliNotificationStackLayout.this.mLastClickTime = currentTimeMillis;
                CliNotificationStackLayout.this.mWaitForClick.setRow(expandableNotificationRow);
                if (expandableNotificationRow.isExpandable()) {
                    CliNotificationStackLayout cliNotificationStackLayout2 = CliNotificationStackLayout.this;
                    cliNotificationStackLayout2.postDelayed(cliNotificationStackLayout2.mWaitForClick, 200);
                }
            }
        };
        this.mRankingComparator = new Comparator<NotificationEntry>() {
            public int compare(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
                return Long.compare(notificationEntry2.getSbn().getNotification().when, notificationEntry.getSbn().getNotification().when);
            }
        };
        this.mChildrenUpdater = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                CliNotificationStackLayout.this.updateChildren();
                boolean unused = CliNotificationStackLayout.this.mChildrenUpdateRequested = false;
                CliNotificationStackLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        };
        this.mStateListener = new StatusBarStateController.StateListener() {
            public void onStatePreChange(int i, int i2) {
            }

            public void onStateChanged(int i) {
                Log.d("Cli_NotificationStackScrollLayout", "onStateChanged=" + i);
                if (i != 0) {
                    i = 1;
                }
                if (CliNotificationStackLayout.this.mStatusBarState != i) {
                    int unused = CliNotificationStackLayout.this.mStatusBarState = i;
                    CliNotificationStackLayout.this.updateVisibility();
                }
            }

            public void onStatePostChange() {
                CliNotificationStackLayout.this.onStatePostChange();
            }
        };
        this.mSwipeCallback = new SwipeHelper.Callback() {
            public boolean canChildBeDragged(View view) {
                return true;
            }

            public int getConstrainSwipeStartPosition() {
                return 0;
            }

            public float getFalsingThresholdFactor() {
                return 0.0f;
            }

            public boolean isAntiFalsingNeeded() {
                return false;
            }

            public void onBeginDrag(View view) {
            }

            public void onChildSnappedBack(View view, float f) {
            }

            public void onDragCancelled(View view) {
            }

            public boolean updateSwipeProgress(View view, boolean z, float f) {
                return true;
            }

            public View getChildAtPosition(MotionEvent motionEvent) {
                if (!CliNotificationStackLayout.this.handleEducationCard()) {
                    return CliNotificationStackLayout.this.getChildAtPosition(motionEvent.getX(), motionEvent.getY());
                }
                if (CliNotificationStackLayout.this.mCliEducationView.touchInView(CliNotificationStackLayout.this.mCliEducationView.getCardContainer(), motionEvent)) {
                    return CliNotificationStackLayout.this.mCliEducationView.getCardContainer();
                }
                return null;
            }

            public boolean canChildBeDismissed(View view) {
                if (CliNotificationStackLayout.this.handleEducationCard()) {
                    return true;
                }
                return ((ExpandableNotificationRow) view).canViewBeDismissed();
            }

            public boolean canChildBeDismissedInDirection(View view, boolean z) {
                if (CliNotificationStackLayout.this.handleEducationCard()) {
                    return true;
                }
                return canChildBeDismissed(view);
            }

            public void onChildDismissed(View view) {
                if (CliNotificationStackLayout.this.handleEducationCard()) {
                    CliNotificationStackLayout.this.hideEducation();
                    return;
                }
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                if (!expandableNotificationRow.isDismissed()) {
                    CliNotificationStackLayout.this.handleChildViewDismissed(expandableNotificationRow);
                }
            }
        };
        this.mAnimationUtils = new CliNotificationAnimationUtils();
        this.mCardExpand = false;
        this.mOnGroupExpansionChangeListener = new GroupExpansionManager.OnGroupExpansionChangeListener() {
            public void onGroupExpansionChange(ExpandableNotificationRow expandableNotificationRow, boolean z) {
                final ExpandableNotificationRow cliRow = expandableNotificationRow.getCliRow();
                if (cliRow != null) {
                    ExpandableView unused = CliNotificationStackLayout.this.mExpandedGroupView = cliRow;
                    CliNotificationStackLayout cliNotificationStackLayout = CliNotificationStackLayout.this;
                    boolean unused2 = cliNotificationStackLayout.mNeedsAnimation = cliNotificationStackLayout.mCliStatusBarWindowController.getNotificationCardShowing();
                    cliRow.setChildrenExpanded(z, true);
                    CliNotificationStackLayout.this.onHeightChanged(cliRow, false);
                    CliNotificationStackLayout.this.runAfterAnimationFinished(new Runnable() {
                        public void run() {
                            cliRow.onFinishedExpansionChange();
                        }
                    });
                }
            }
        };
        this.mOnGroupChangeListener = new NotificationGroupManagerLegacy.OnGroupChangeListener() {
            public void onGroupCreatedFromChildren(NotificationGroupManagerLegacy.NotificationGroup notificationGroup) {
            }

            public void onGroupsChanged() {
            }

            /* JADX WARNING: Removed duplicated region for block: B:24:0x009b A[LOOP:0: B:1:0x0021->B:24:0x009b, LOOP_END] */
            /* JADX WARNING: Removed duplicated region for block: B:26:0x006f A[SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onGroupSuppressionChanged(com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy.NotificationGroup r9, boolean r10) {
                /*
                    r8 = this;
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    java.lang.String r1 = "onGroupSuppressionChanged="
                    r0.append(r1)
                    r0.append(r9)
                    java.lang.String r1 = ";suppressed="
                    r0.append(r1)
                    r0.append(r10)
                    java.lang.String r0 = r0.toString()
                    java.lang.String r1 = "Cli_NotificationStackScrollLayout"
                    android.util.Log.d(r1, r0)
                    r0 = 0
                    r1 = 2
                    r2 = r0
                L_0x0021:
                    com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout r3 = com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.this
                    int r3 = r3.getChildCount()
                    if (r1 >= r3) goto L_0x009f
                    com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout r3 = com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.this
                    android.view.View r3 = r3.getChildAt(r1)
                    com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r3 = (com.android.systemui.statusbar.notification.row.ExpandableNotificationRow) r3
                    com.android.systemui.statusbar.notification.collection.NotificationEntry r4 = r3.getEntry()
                    android.service.notification.StatusBarNotification r4 = r4.getSbn()
                    com.android.systemui.statusbar.notification.collection.NotificationEntry r5 = r3.getEntry()
                    com.android.systemui.statusbar.notification.icon.IconPack r5 = r5.getIcons()
                    com.android.systemui.statusbar.StatusBarIconView r5 = r5.getCarouselIcon()
                    if (r10 != 0) goto L_0x0065
                    r6 = r0
                L_0x0048:
                    com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout r7 = com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.this
                    com.android.systemui.statusbar.phone.NotificationIconContainer r7 = r7.mIconContainer
                    int r7 = r7.getChildCount()
                    if (r6 >= r7) goto L_0x0065
                    com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout r7 = com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.this
                    com.android.systemui.statusbar.phone.NotificationIconContainer r7 = r7.mIconContainer
                    android.view.View r7 = r7.getChildAt(r6)
                    if (r7 != r5) goto L_0x0062
                    r2 = 1
                    goto L_0x0067
                L_0x0062:
                    int r6 = r6 + 1
                    goto L_0x0048
                L_0x0065:
                    r6 = r2
                    r2 = r0
                L_0x0067:
                    com.android.systemui.statusbar.notification.collection.NotificationEntry r5 = r3.getEntry()
                    com.android.systemui.statusbar.notification.collection.NotificationEntry r7 = r9.summary
                    if (r5 != r7) goto L_0x009b
                    com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout r9 = com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.this
                    com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy r9 = r9.mGroupManager
                    boolean r9 = r9.isSummaryOfSuppressedGroup(r4)
                    if (r9 == 0) goto L_0x0086
                    r9 = 8
                    r3.setVisibility(r9)
                    com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout r8 = com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.this
                    r8.removeCarouselView(r3)
                    goto L_0x009f
                L_0x0086:
                    if (r10 != 0) goto L_0x009f
                    com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout r9 = com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.this
                    int r9 = r9.mStatusBarState
                    if (r9 != 0) goto L_0x0093
                    r3.setVisibility(r0)
                L_0x0093:
                    if (r2 != 0) goto L_0x009f
                    com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout r8 = com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.this
                    r8.addCarouselView(r3, r6)
                    goto L_0x009f
                L_0x009b:
                    int r1 = r1 + 1
                    r2 = r6
                    goto L_0x0021
                L_0x009f:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.C165111.onGroupSuppressionChanged(com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy$NotificationGroup, boolean):void");
            }
        };
        this.mOnExpandClickListener = new ExpandableNotificationRow.OnExpandClickListener() {
            public void onExpandClicked(NotificationEntry notificationEntry, View view, boolean z) {
                if (CliNotificationStackLayout.this.mCardExpand != z && !notificationEntry.isSensitive() && CliNotificationStackLayout.this.mStatusBarState != 0) {
                    ExpandableNotificationRow row = notificationEntry.getRow();
                    if (row == null || !row.isChildInGroup()) {
                        CliNotificationStackLayout.this.doCarrouselAnimation(z);
                    }
                }
            }
        };
        this.mRowSpace = getResources().getDimensionPixelSize(R$dimen.cli_stack_row_space);
        this.mPaddingTop = getResources().getDimensionPixelSize(R$dimen.cli_stack_margin_top);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.cli_status_bar_icon_size);
        this.mCarouselIconSize = dimensionPixelSize;
        this.mCardTranlateY = (getResources().getDimensionPixelSize(R$dimen.cli_carousel_height) - dimensionPixelSize) / 2;
    }

    private FrameLayout.LayoutParams generateIconLayoutParamsForCarousel() {
        int i = this.mCarouselIconSize;
        return new FrameLayout.LayoutParams(i, i);
    }

    public int getStackPaddingTop() {
        return this.mPaddingTop;
    }

    public boolean isStatusBarShade() {
        return this.mStatusBarState == 0;
    }

    public int getChildIntrinsicHeight(ExpandableNotificationRow expandableNotificationRow) {
        return Math.max(expandableNotificationRow.getIntrinsicHeight(), expandableNotificationRow.getCliMinHeight());
    }

    private boolean isCurrentlyAnimating() {
        return this.mStateAnimator.isRunning();
    }

    /* access modifiers changed from: private */
    public void updateChildren() {
        resetViewStates();
        if (isCurrentlyAnimating() || this.mNeedsAnimation) {
            updateContentHeight(false);
            startAnimationToState();
            return;
        }
        updateContentHeight(true);
        applyCurrentState();
    }

    private void updateContentHeight(boolean z) {
        int childCount = getChildCount();
        int i = 0;
        if (this.mTopPadingView.getVisibility() != 8) {
            i = 0 + this.mTopPadingView.getMeasuredHeight();
        }
        int i2 = 2;
        while (true) {
            if (i2 >= childCount) {
                break;
            }
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) getChildAt(i2);
            if (expandableNotificationRow.getVisibility() != 8 && !expandableNotificationRow.hasNoContentHeight()) {
                if (z) {
                    expandableNotificationRow.setTranslationY((float) i);
                }
                float f = (float) i;
                expandableNotificationRow.getViewState().yTranslation = f;
                float childIntrinsicHeight = (float) getChildIntrinsicHeight(expandableNotificationRow);
                if (this.mStatusBarState != 0) {
                    i = (int) (f + childIntrinsicHeight);
                    break;
                }
                i = (int) (f + childIntrinsicHeight + ((float) this.mRowSpace));
            }
            i2++;
        }
        if (this.mCleanButton.getVisibility() != 8) {
            this.mCleanButton.setTranslationY((float) i);
            i += this.mCleanButton.getMeasuredHeight();
        }
        Log.d("Cli_NotificationStackScrollLayout", "updateContentHeight force=" + z + ";mIntrinsicContentHeight=" + this.mIntrinsicContentHeight + ";height=" + i);
        int i3 = this.mIntrinsicContentHeight;
        if ((i3 != i && z) || i3 < i) {
            this.mIntrinsicContentHeight = i;
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = this.mIntrinsicContentHeight;
            setLayoutParams(layoutParams);
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mIntrinsicContentHeight, 1073741824);
        super.onMeasure(i, makeMeasureSpec);
        int size = View.MeasureSpec.getSize(i);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.getMode(i));
        int makeMeasureSpec3 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(makeMeasureSpec), 0);
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            measureChild(getChildAt(i3), makeMeasureSpec2, makeMeasureSpec3);
        }
        setMeasuredDimension(size, this.mIntrinsicContentHeight);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float width = ((float) getWidth()) / 2.0f;
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            float measuredWidth = ((float) childAt.getMeasuredWidth()) / 2.0f;
            childAt.layout((int) (width - measuredWidth), 0, (int) (measuredWidth + width), (int) ((float) childAt.getMeasuredHeight()));
        }
        requestChildrenUpdate();
    }

    private void requestChildrenUpdate() {
        if (!this.mChildrenUpdateRequested) {
            getViewTreeObserver().addOnPreDrawListener(this.mChildrenUpdater);
            this.mChildrenUpdateRequested = true;
            invalidate();
        }
    }

    private void onViewAddedInternal(ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.setOnHeightChangedListener(this);
        expandableNotificationRow.setOnClickListener(this.mClickListener);
        updateHideSensitiveForChild(expandableNotificationRow);
    }

    private void onViewRemovedInternal(ExpandableNotificationRow expandableNotificationRow) {
        generateRemoveAnimation(expandableNotificationRow);
        expandableNotificationRow.setOnHeightChangedListener((ExpandableView.OnHeightChangedListener) null);
        expandableNotificationRow.setOnClickListener((View.OnClickListener) null);
    }

    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        if (view instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
            onViewRemovedInternal(expandableNotificationRow);
            Log.d("Cli_NotificationStackScrollLayout", "onViewRemoved sbn=" + expandableNotificationRow.getEntry().getSbn() + ";row=" + expandableNotificationRow);
            removeCarouselView(expandableNotificationRow);
        }
    }

    public void onViewAdded(View view) {
        super.onViewAdded(view);
        if (view instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
            onViewAddedInternal(expandableNotificationRow);
            Log.d("Cli_NotificationStackScrollLayout", "onViewAdded sbn=" + expandableNotificationRow.getEntry().getSbn() + ";row=" + expandableNotificationRow);
            if (!this.mGroupManager.isSummaryOfSuppressedGroup(expandableNotificationRow.getEntry().getSbn())) {
                addCarouselView(expandableNotificationRow);
            }
        }
    }

    private void addCarouselView(ExpandableNotificationRow expandableNotificationRow) {
        if (!CliNotificationFilter.isNotificationFiltered(this.mContext, expandableNotificationRow.getEntry().getSbn())) {
            int i = 0;
            for (int i2 = 2; i2 < getChildCount(); i2++) {
                ExpandableNotificationRow expandableNotificationRow2 = (ExpandableNotificationRow) getChildAt(i2);
                if (this.mRankingComparator.compare(expandableNotificationRow.getEntry(), expandableNotificationRow2.getEntry()) < 0) {
                    break;
                }
                StatusBarIconView carouselIcon = expandableNotificationRow2.getEntry().getIcons().getCarouselIcon();
                int i3 = 0;
                while (true) {
                    if (i3 >= this.mIconContainer.getChildCount()) {
                        break;
                    } else if (this.mIconContainer.getChildAt(i3) == carouselIcon) {
                        i++;
                        break;
                    } else {
                        i3++;
                    }
                }
            }
            addCarouselView(expandableNotificationRow, i);
        }
    }

    /* access modifiers changed from: private */
    public void addCarouselView(ExpandableNotificationRow expandableNotificationRow, int i) {
        StatusBarIconView carouselIcon = expandableNotificationRow.getEntry().getIcons().getCarouselIcon();
        this.mIconContainer.removeTransientView(carouselIcon);
        if (this.mCliEducationView.shouldEducationCard() && this.mEducationIcon != null) {
            i++;
        }
        this.mIconContainer.addView(carouselIcon, i, generateIconLayoutParamsForCarousel());
        if (this.mCliEducationView.shouldEducationCard()) {
            post(new CliNotificationStackLayout$$ExternalSyntheticLambda4(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addCarouselView$0() {
        this.mCliEducationView.updateEducationNotePadding();
    }

    /* access modifiers changed from: private */
    public void removeCarouselView(ExpandableNotificationRow expandableNotificationRow) {
        this.mIconContainer.removeView(expandableNotificationRow.getEntry().getIcons().getCarouselIcon());
        if (this.mCliEducationView.shouldEducationCard()) {
            post(new CliNotificationStackLayout$$ExternalSyntheticLambda3(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeCarouselView$1() {
        this.mCliEducationView.updateEducationNotePadding();
    }

    public void changeViewPosition(ExpandableView expandableView, int i) {
        Assert.isMainThread();
        if (!this.mChangePositionInProgress) {
            int indexOfChild = indexOfChild(expandableView);
            boolean z = true;
            if (indexOfChild == -1) {
                if (!(expandableView instanceof ExpandableNotificationRow) || expandableView.getTransientContainer() == null) {
                    z = false;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Attempting to re-position ");
                sb.append(z ? "transient" : "");
                sb.append(" view {");
                sb.append(expandableView);
                sb.append("}");
                Log.e("Cli_NotificationStackScrollLayout", sb.toString());
            } else if (expandableView != null && expandableView.getParent() == this && indexOfChild != i) {
                this.mChangePositionInProgress = true;
                expandableView.setChangingPosition(true);
                removeView(expandableView);
                if (expandableView.getParent() != null) {
                    Log.wtf("Cli_NotificationStackScrollLayout", "Trying to readd a notification child that already has a parent:" + expandableView.getParent().getClass() + ", child: " + expandableView);
                } else {
                    addView(expandableView, i);
                }
                expandableView.setChangingPosition(false);
                this.mChangePositionInProgress = false;
            }
        } else {
            throw new IllegalStateException("Reentrant call to changeViewPosition");
        }
    }

    public void changeViewPosition(NotificationEntry notificationEntry) {
        boolean z;
        Assert.isMainThread();
        if (notificationEntry != null) {
            Log.d("Cli_NotificationStackScrollLayout", "changeViewPosition sbn=" + notificationEntry.getSbn());
            ExpandableNotificationRow expandableNotificationRow = null;
            for (int i = 2; i < getChildCount(); i++) {
                ExpandableNotificationRow expandableNotificationRow2 = (ExpandableNotificationRow) getChildAt(i);
                if (notificationEntry == expandableNotificationRow2.getEntry()) {
                    expandableNotificationRow = expandableNotificationRow2;
                }
            }
            if (expandableNotificationRow != null) {
                StatusBarIconView carouselIcon = notificationEntry.getIcons().getCarouselIcon();
                int i2 = 0;
                while (true) {
                    if (i2 >= this.mIconContainer.getChildCount()) {
                        z = false;
                        break;
                    } else if (this.mIconContainer.getChildAt(i2) == carouselIcon) {
                        z = true;
                        break;
                    } else {
                        i2++;
                    }
                }
                int i3 = 0;
                int i4 = -1;
                int i5 = 2;
                for (int i6 = 2; i6 < getChildCount(); i6++) {
                    ExpandableNotificationRow expandableNotificationRow3 = (ExpandableNotificationRow) getChildAt(i6);
                    StatusBarIconView carouselIcon2 = expandableNotificationRow3.getEntry().getIcons().getCarouselIcon();
                    if (expandableNotificationRow == expandableNotificationRow3) {
                        i4 = i6;
                    } else if (this.mRankingComparator.compare(notificationEntry, expandableNotificationRow3.getEntry()) < 0) {
                        break;
                    } else {
                        i5++;
                        if (z) {
                            int i7 = 0;
                            while (true) {
                                if (i7 >= this.mIconContainer.getChildCount()) {
                                    break;
                                } else if (this.mIconContainer.getChildAt(i7) == carouselIcon2) {
                                    i3++;
                                    break;
                                } else {
                                    i7++;
                                }
                            }
                        }
                    }
                }
                if (i4 != i5) {
                    removeView(expandableNotificationRow);
                    addView(expandableNotificationRow, i5);
                    if (z) {
                        removeCarouselView(expandableNotificationRow);
                        addCarouselView(expandableNotificationRow, i3);
                    }
                }
            }
        }
    }

    public void notifyGroupChildAdded(ExpandableView expandableView) {
        ExpandableNotificationRow cliView = getCliView(expandableView);
        if (cliView != null) {
            cliView.setVisibility(0);
            cliView.setExpandClickListen(this.mOnExpandClickListener);
            onViewAddedInternal(cliView);
            Log.d("Cli_NotificationStackScrollLayout", "groupChildAdded=" + cliView.getEntry().getSbn() + ";row=" + cliView);
        }
    }

    public void notifyGroupChildRemoved(ExpandableView expandableView, ViewGroup viewGroup) {
        ExpandableNotificationRow cliView = getCliView(expandableView);
        if (cliView != null) {
            onViewRemovedInternal(cliView);
            Log.d("Cli_NotificationStackScrollLayout", "groupChildRemoved=" + cliView.getEntry().getSbn() + ";row=" + cliView);
        }
    }

    public int getContainerChildCount() {
        return getChildCount();
    }

    public View getContainerChildAt(int i) {
        return getChildAt(i);
    }

    public void removeContainerView(View view) {
        ExpandableNotificationRow cliView = getCliView(view);
        if (cliView != null) {
            Log.d("Cli_NotificationStackScrollLayout", "removeContainerView sbn=" + cliView.getEntry().getSbn() + ";row=" + view);
            removeView(cliView);
            cliView.setVisibility(4);
            cliView.setTopRoundness(0.0f, false);
            cliView.setBottomRoundness(0.0f, false);
            if (cliView == this.mSelectedView) {
                updateChildrenVisual((StatusBarIconView) null);
                setKeyguardClockVisibility(true);
                if (this.mCardExpand) {
                    doCarrouselAnimation(false);
                }
            }
            if (getChildCount() <= 2 && this.mStatusBarState == 0) {
                setCardVisibility(false, false);
            }
        }
    }

    public void addContainerView(View view) {
        ExpandableNotificationRow cliView = getCliView(view);
        if (cliView != null && !CliNotificationFilter.isNotificationCardFiltered(this.mHighPriorityProvider, cliView.getEntry())) {
            StatusBarNotification sbn = cliView.getEntry().getSbn();
            Log.d("Cli_NotificationStackScrollLayout", "addContainerView entry=" + sbn + ";row=" + view);
            if (cliView.getParent() != null) {
                Log.e("Cli_NotificationStackScrollLayout", "cliView.getParent()=" + cliView.getParent() + ";this=" + this);
            }
            if (this.mStatusBarState != 0) {
                cliView.setVisibility(8);
            } else if (this.mGroupManager.isSummaryOfSuppressedGroup(sbn)) {
                Log.d("Cli_NotificationStackScrollLayout", "Row is suppressed summary");
                cliView.setVisibility(8);
            } else {
                cliView.setVisibility(0);
            }
            int i = 2;
            int i2 = 2;
            while (i < getChildCount() && this.mRankingComparator.compare(cliView.getEntry(), ((ExpandableNotificationRow) getChildAt(i)).getEntry()) >= 0) {
                i2++;
                i++;
            }
            addView(cliView, i2);
            cliView.setExpandClickListen(this.mOnExpandClickListener);
            cliView.setTopRoundness(1.0f, false);
            cliView.setBottomRoundness(1.0f, false);
        }
    }

    public void addContainerViewAt(View view, int i) {
        Assert.isMainThread();
    }

    public boolean containsView(View view) {
        return view.getParent() == this;
    }

    public void onHeightChanged(ExpandableView expandableView, boolean z) {
        Log.d("Cli_NotificationStackScrollLayout", "onHeightChanged needsAnimation=" + z);
        if (z) {
            requestAnimationOnViewResize((ExpandableNotificationRow) expandableView);
        }
        requestChildrenUpdate();
    }

    public void runAfterAnimationFinished(Runnable runnable) {
        this.mAnimationFinishedRunnables.add(runnable);
    }

    private void runAnimationFinishedRunnables() {
        Iterator<Runnable> it = this.mAnimationFinishedRunnables.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mAnimationFinishedRunnables.clear();
    }

    public void onChildAnimationFinished() {
        Log.d("Cli_NotificationStackScrollLayout", "onChildAnimationFinished");
        requestChildrenUpdate();
        runAnimationFinishedRunnables();
    }

    private void startAnimationToState() {
        if (this.mNeedsAnimation) {
            generateAllAnimationEvents();
            this.mNeedsAnimation = false;
        }
        if (!this.mAnimationEvents.isEmpty() || isCurrentlyAnimating()) {
            this.mStateAnimator.startAnimationForEvents(this.mAnimationEvents, 0);
            this.mAnimationEvents.clear();
            return;
        }
        applyCurrentState();
    }

    private void applyCurrentState() {
        for (int i = 2; i < getChildCount(); i++) {
            ((ExpandableNotificationRow) getChildAt(i)).applyViewState();
        }
        runAnimationFinishedRunnables();
    }

    public void resetViewStates() {
        resetChildViewStates();
        getNotificationChildrenStates();
        updatedHideSensitive();
    }

    private void getNotificationChildrenStates() {
        for (int i = 2; i < getChildCount(); i++) {
            ((ExpandableNotificationRow) getChildAt(i)).updateChildrenStates((AmbientState) null);
        }
    }

    private void updatedHideSensitive() {
        boolean isHideSensitive = this.mAmbientState.isHideSensitive();
        for (int i = 2; i < getChildCount(); i++) {
            ((ExpandableNotificationRow) getChildAt(i)).getViewState().hideSensitive = isHideSensitive;
        }
    }

    private void resetChildViewStates() {
        for (int i = 2; i < getChildCount(); i++) {
            ((ExpandableNotificationRow) getChildAt(i)).resetViewState();
        }
    }

    private void requestAnimationOnViewResize(ExpandableNotificationRow expandableNotificationRow) {
        this.mNeedViewResizeAnimation = true;
        this.mNeedsAnimation = true;
    }

    private void generateAllAnimationEvents() {
        generateViewResizeEvent();
        generateGroupExpansionEvent();
        generateChildRemovalEvents();
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout.generateViewResizeEvent():void");
    }

    private void generateChildRemovalEvents() {
        Iterator<ExpandableView> it = this.mChildrenToRemoveAnimated.iterator();
        while (it.hasNext()) {
            ExpandableView next = it.next();
            boolean contains = this.mSwipedOutViews.contains(next);
            if (contains) {
                this.mAnimationEvents.add(new NotificationStackScrollLayout.AnimationEvent(next, contains ? 2 : 1));
                this.mSwipedOutViews.remove(next);
            }
        }
        this.mChildrenToRemoveAnimated.clear();
    }

    private boolean generateRemoveAnimation(ExpandableView expandableView) {
        if (!this.mSwipedOutViews.contains(expandableView)) {
            return true;
        }
        this.mChildrenToRemoveAnimated.add(expandableView);
        return true;
    }

    public void handleChildViewDismissed(ExpandableNotificationRow expandableNotificationRow) {
        Log.d("Cli_NotificationStackScrollLayout", "handleChildViewDismissed sbn=" + expandableNotificationRow.getEntry().getSbn());
        if (this.mStatusBarState == 0 || expandableNotificationRow.isChildInGroup()) {
            this.mSwipedOutViews.add(expandableNotificationRow);
            this.mNeedsAnimation = true;
        }
        expandableNotificationRow.performDismiss(false);
    }

    public boolean isInVisibleLocation(NotificationEntry notificationEntry) {
        return notificationEntry.getRow().getVisibility() == 0;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Button button = this.mCleanButton;
        if (button != null) {
            button.setTextSize(0, (float) this.mContext.getResources().getDimensionPixelSize(17105562));
            this.mCleanButton.setText(getResources().getString(R$string.clear_all_notifications_text));
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        Button button = (Button) findViewById(R$id.cli_cleanAll);
        this.mCleanButton = button;
        button.setOnClickListener(new CliNotificationStackLayout$$ExternalSyntheticLambda0(this));
        this.mTopPadingView = findViewById(R$id.cli_topPadding);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$2(View view) {
        this.mAnimationUtils.cancelAnimation();
        this.mAnimationUtils.reset();
        this.mAnimationUtils.CreateStackAnimation(this, false, 0.0f, getHeight()).setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                ArrayList arrayList = new ArrayList(CliNotificationStackLayout.this.getChildCount());
                for (int i = 2; i < CliNotificationStackLayout.this.getChildCount(); i++) {
                    ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) CliNotificationStackLayout.this.getChildAt(i);
                    if (expandableNotificationRow.canViewBeDismissed() && !expandableNotificationRow.isDismissed()) {
                        arrayList.add(expandableNotificationRow);
                    }
                }
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    CliNotificationStackLayout.this.mEntryManager.performRemoveNotification(((ExpandableNotificationRow) it.next()).getEntry().getSbn(), (DismissedByUserStats) null, 3);
                }
                try {
                    CliNotificationStackLayout.this.mBarService.onClearAllNotifications(CliNotificationStackLayout.this.mLockscreenUserManager.getCurrentUserId());
                } catch (Exception unused) {
                }
                CliNotificationStackLayout.this.setCardVisibility(false, false);
            }
        });
        this.mAnimationUtils.startAnimation();
    }

    public void startTransitionYFromParent(float f, final boolean z) {
        this.mLauncherDownEvent = null;
        this.mAnimationUtils.cancelAnimation();
        this.mAnimationUtils.reset();
        this.mAnimationUtils.CreateStackAnimation(this, z, f, this.mParentView.getHeight()).setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                CliNotificationStackLayout.this.setTranslationY(0.0f);
            }

            public void onAnimationEnd(Animation animation) {
                if (!z) {
                    if (CliNotificationStackLayout.this.mStatusBarState == 0) {
                        CliNotificationStackLayout.this.setCardVisibility(false, false);
                    } else {
                        CliNotificationStackLayout.this.reset();
                    }
                }
                CliNotificationStackLayout.this.setTranslationY(0.0f);
                CliNotificationStackLayout.this.updateStackBackground(z);
            }
        });
        View view = this.mCliKeyguardBlurBg;
        if (view != null && this.mStatusBarState == 0) {
            this.mAnimationUtils.createBackgroundAnimation(view, z, getStatckBackgroundAlpha());
        }
        this.mAnimationUtils.startAnimation();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this.mStateListener, 2);
        ((CliNotificationStackClient) Dependency.get(CliNotificationStackClient.class)).addCallback((CliNotificationStackClient.StackProxyListener) this);
        this.mParentView = (View) getParent();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).removeCallback(this.mStateListener);
        ((CliNotificationStackClient) Dependency.get(CliNotificationStackClient.class)).removeCallback((CliNotificationStackClient.StackProxyListener) this);
    }

    /* access modifiers changed from: private */
    public void updateVisibility() {
        int i = 2;
        while (true) {
            int i2 = 8;
            if (i >= getChildCount()) {
                break;
            }
            getChildAt(i);
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) getChildAt(i);
            if (!this.mGroupManager.isSummaryOfSuppressedGroup(expandableNotificationRow.getEntry().getSbn())) {
                if (this.mStatusBarState == 0) {
                    i2 = 0;
                }
                expandableNotificationRow.setVisibility(i2);
            }
            i++;
        }
        if (this.mStatusBarState == 0) {
            this.mParentView.setPadding(0, 0, 0, 0);
            this.mTopPadingView.setVisibility(0);
            this.mCleanButton.setVisibility(0);
            if (!this.mCliStatusBarWindowController.getNotificationCardShowing() || this.mCliStatusBarWindowController.isKeyguardShowingAndOccluded()) {
                setCardVisibility(false, false);
            }
            if (handleEducationCard()) {
                hideEducation();
                return;
            }
            return;
        }
        this.mParentView.setPadding(0, this.mPaddingTop, 0, 0);
        this.mTopPadingView.setVisibility(8);
        this.mCleanButton.setVisibility(8);
        reset();
        if (this.mCliEducationView.shouldEducationCard()) {
            showEducation();
        }
    }

    /* access modifiers changed from: private */
    public void onStatePostChange() {
        updateSensitiveness();
    }

    /* access modifiers changed from: private */
    public View getChildAtPosition(float f, float f2) {
        for (int i = 2; i < getChildCount(); i++) {
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) getChildAt(i);
            if (expandableNotificationRow.getVisibility() == 0) {
                float translationY = expandableNotificationRow.getTranslationY();
                float actualHeight = (((float) expandableNotificationRow.getActualHeight()) + translationY) - ((float) expandableNotificationRow.getClipBottomAmount());
                int width = getWidth();
                if (f2 >= ((float) expandableNotificationRow.getClipTopAmount()) + translationY && f2 <= actualHeight && f >= ((float) 0) && f <= ((float) width)) {
                    return expandableNotificationRow.isSummaryWithChildren() ? expandableNotificationRow.getViewAtPosition(f2 - translationY) : expandableNotificationRow;
                }
            }
        }
        return null;
    }

    public boolean isMoving() {
        return this.mSwipeHelper.isMoving();
    }

    public void setCliViewRequestListener(CliStatusBarWindowController.OnCliViewRequestListener onCliViewRequestListener) {
        this.mOnCliViewRequestListener = onCliViewRequestListener;
    }

    public void clearSelectedView() {
        this.mSelectedView = null;
    }

    public int getCurrentCardScrollY() {
        if (this.mSelectedView != null && this.mCliStatusBarWindowController.getNotificationCardShowing() && this.mStatusBarState == 0) {
            int i = 2;
            int i2 = 0;
            while (true) {
                if (i >= getChildCount()) {
                    break;
                }
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) getChildAt(i);
                if (expandableNotificationRow.getVisibility() != 8 && !expandableNotificationRow.hasNoContentHeight()) {
                    float childIntrinsicHeight = (float) getChildIntrinsicHeight(expandableNotificationRow);
                    if (expandableNotificationRow != this.mSelectedView) {
                        i2 = (int) (((float) i2) + childIntrinsicHeight + ((float) this.mRowSpace));
                    } else if (i != 2) {
                        return i2;
                    }
                }
                i++;
            }
        }
        return 0;
    }

    public void onPanelVisibilityChanged(boolean z) {
        if (this.mParentView != null) {
            Log.d("Cli_NotificationStackScrollLayout", "onPanelVisibilityChanged=" + z);
            if (!z) {
                setCardVisibility(false, false);
                ((ScrollView) this.mParentView).setScrollY(0);
            }
        }
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            if (getChildCount() <= 2) {
                Log.d("Cli_NotificationStackScrollLayout", "Not child to swipe up!");
                return;
            }
            this.mLauncherDownEvent = motionEvent;
            ((ScrollView) this.mParentView).setScrollY(0);
            setTranslationY((float) this.mParentView.getHeight());
            setCardVisibility(true, true);
        } else if (motionEvent.getActionMasked() == 1) {
            this.mLauncherDownEvent = null;
            setCardVisibility(false, false);
        }
    }

    public float getLauncherDownY() {
        MotionEvent motionEvent = this.mLauncherDownEvent;
        if (motionEvent != null) {
            return motionEvent.getRawY();
        }
        return 0.0f;
    }

    public void init(CliStatusBar cliStatusBar, NotificationIconContainer notificationIconContainer, NotificationGroupManagerLegacy notificationGroupManagerLegacy, CliStatusBarWindowController cliStatusBarWindowController, FalsingManager falsingManager, NotificationActivityStarter notificationActivityStarter, NotificationLockscreenUserManager notificationLockscreenUserManager, HighPriorityProvider highPriorityProvider) {
        this.mCliStatusBar = cliStatusBar;
        this.mGroupManager = notificationGroupManagerLegacy;
        notificationGroupManagerLegacy.registerGroupChangeListener(this.mOnGroupChangeListener);
        this.mGroupManager.registerGroupExpansionChangeListener(this.mOnGroupExpansionChangeListener);
        this.mIconContainer = notificationIconContainer;
        notificationIconContainer.setOnChildClickListener(new CliNotificationStackLayout$$ExternalSyntheticLambda1(this));
        this.mCliStatusBarWindowController = cliStatusBarWindowController;
        this.mSwipeHelper = new SwipeHelper(0, this.mSwipeCallback, this.mContext.getResources(), ViewConfiguration.get(this.mContext), falsingManager);
        this.mNotificationAcitvityStarter = notificationActivityStarter;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mAmbientState = new AmbientState(this.mContext, (StackScrollAlgorithm.SectionProvider) null, (StackScrollAlgorithm.BypassController) null);
        this.mKeyguardClockView = getRootView().findViewById(R$id.cli_keyguard_clock_container);
        this.mCliKeyguardBlurBg = getRootView().findViewById(R$id.cli_keyguard_blur_bg);
        this.mHighPriorityProvider = highPriorityProvider;
        CliEducationView cliEducationView = (CliEducationView) getRootView().findViewById(R$id.education_container);
        this.mCliEducationView = cliEducationView;
        if (cliEducationView.shouldEducationCard()) {
            showEducation();
        } else {
            this.mCliEducationView.startEducationCardIfNeed();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$3(View view) {
        if (CliNotificationSettings.getInstance(this.mContext).isHapticsEnabled()) {
            performHapticFeedback(1, 2);
        }
        updateChildrenVisual((StatusBarIconView) view);
    }

    public void initToCli(NotificationEntryManager notificationEntryManager) {
        this.mEntryManager = notificationEntryManager;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                CliNotificationStackLayout.this.onEntryUpdated(notificationEntry);
            }
        });
    }

    /* access modifiers changed from: private */
    public void onEntryUpdated(NotificationEntry notificationEntry) {
        Log.d("Cli_NotificationStackScrollLayout", "onEntryUpdated sbn=" + notificationEntry.getSbn());
        for (int i = 2; i < getChildCount(); i++) {
            if (notificationEntry == ((ExpandableNotificationRow) getChildAt(i)).getEntry()) {
                changeViewPosition(notificationEntry);
            }
        }
    }

    private ExpandableNotificationRow getCliView(View view) {
        if (view instanceof ExpandableNotificationRow) {
            return ((ExpandableNotificationRow) view).getCliRow();
        }
        return null;
    }

    private void updateSensitiveText(ExpandableNotificationRow expandableNotificationRow) {
        if (expandableNotificationRow != null) {
            expandableNotificationRow.updateSensitiveText();
        }
    }

    private void updateHideSensitiveForChild(ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.setHideSensitiveForIntrinsicHeight(this.mAmbientState.isHideSensitive());
        updateSensitiveText(expandableNotificationRow);
    }

    private void updateSensitiveness() {
        boolean isAnyProfilePublicMode = this.mLockscreenUserManager.isAnyProfilePublicMode();
        if (isAnyProfilePublicMode != this.mAmbientState.isHideSensitive()) {
            for (int i = 2; i < getChildCount(); i++) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) getChildAt(i);
                expandableNotificationRow.setHideSensitiveForIntrinsicHeight(isAnyProfilePublicMode);
                updateSensitiveText(expandableNotificationRow);
            }
            this.mAmbientState.setHideSensitive(isAnyProfilePublicMode);
            requestChildrenUpdate();
        }
    }

    private void updateChildrenVisual(StatusBarIconView statusBarIconView) {
        StatusBarNotification notification = statusBarIconView == null ? null : statusBarIconView.getNotification();
        Log.d("Cli_NotificationStackScrollLayout", "updateChildrenVisual sbn=" + notification);
        this.mOldSelectedIconView = this.mSelectedIconView;
        this.mOldSelectedView = this.mSelectedView;
        for (int i = 2; i < getChildCount(); i++) {
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) getChildAt(i);
            if (notification == expandableNotificationRow.getEntry().getSbn()) {
                this.mSelectedIconView = statusBarIconView;
                this.mSelectedView = expandableNotificationRow;
            }
        }
        boolean z = false;
        if (notification != null) {
            if (!this.mCliStatusBarWindowController.getNotificationCardShowing()) {
                this.mAnimationType = 2;
            } else if (this.mSelectedIconView == this.mOldSelectedIconView) {
                this.mAnimationType = 1;
            } else {
                this.mAnimationType = 3;
            }
            this.mHandler.post(new CliNotificationStackLayout$$ExternalSyntheticLambda5(this));
        } else {
            if (this.mSelectedView != null) {
                this.mAnimationUtils.cancelAnimation();
                this.mSelectedView.setVisibility(8);
            }
            this.mAnimationType = 0;
        }
        int i2 = this.mAnimationType;
        if (i2 == 3 || i2 == 2) {
            z = true;
        }
        setCardVisibility(z, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateChildrenVisual$4() {
        doCardAnimation(this.mAnimationType);
    }

    public void setCardVisibility(boolean z, boolean z2) {
        this.mCliStatusBarWindowController.setNotificationCardShowing(z);
        int i = 0;
        this.mOnCliViewRequestListener.onVisibleChanged(this, z ? 0 : 8, z);
        View view = this.mParentView;
        if (view != null) {
            if (!z2) {
                i = 8;
            }
            view.setVisibility(i);
        }
        if (!z2) {
            clearSelectedView();
        }
    }

    private float getStatckBackgroundAlpha() {
        float height = (float) this.mParentView.getHeight();
        return (height - getTranslationY()) / height;
    }

    public void updateStackBackground(boolean z) {
        View view = this.mCliKeyguardBlurBg;
        if (view != null && this.mStatusBarState == 0) {
            view.setVisibility(z ? 0 : 8);
            this.mCliKeyguardBlurBg.setAlpha(getStatckBackgroundAlpha());
        }
    }

    /* access modifiers changed from: private */
    public void setKeyguardClockVisibility(boolean z) {
        View view = this.mKeyguardClockView;
        if (view != null) {
            view.setVisibility(z ? 0 : 4);
        }
    }

    /* access modifiers changed from: private */
    public void doCardAnimation(final int i) {
        final boolean z;
        final ExpandableNotificationRow expandableNotificationRow;
        View view;
        if (i == 2 || i == 4) {
            expandableNotificationRow = this.mSelectedView;
            z = true;
        } else {
            expandableNotificationRow = this.mOldSelectedView;
            z = false;
        }
        if (expandableNotificationRow == null) {
            Log.d("Cli_NotificationStackScrollLayout", "doCardAnimation target has been remove! =" + z);
            return;
        }
        if (i == 3) {
            this.mAnimationUtils.setTwice(true);
        } else {
            this.mAnimationUtils.setTwice(false);
        }
        this.mAnimationUtils.cancelAnimation();
        this.mAnimationUtils.reset();
        Log.d("Cli_NotificationStackScrollLayout", "doCardAnimation x=" + expandableNotificationRow.getTranslationX() + ";type" + i + ";sbn=" + expandableNotificationRow.getEntry().getSbn());
        this.mAnimationUtils.createCardAnimation(this, (float) this.mCardTranlateY, z).setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                expandableNotificationRow.setVisibility(0);
            }

            public void onAnimationEnd(Animation animation) {
                if (!z) {
                    expandableNotificationRow.setVisibility(8);
                }
                int i = i;
                if (i == 2) {
                    CliNotificationStackLayout.this.setKeyguardClockVisibility(false);
                    CliNotificationStackLayout.this.showEducationAction(true);
                } else if (i == 1) {
                    CliNotificationStackLayout.this.setKeyguardClockVisibility(true);
                } else if (i == 3 && CliNotificationStackLayout.this.mSelectedView != null) {
                    CliNotificationStackLayout.this.doCardAnimation(4);
                }
            }
        });
        if ((i == 2 || i == 1) && (view = this.mKeyguardClockView) != null) {
            this.mAnimationUtils.createKeyguardAnimation(view, !z);
        }
        this.mAnimationUtils.startAnimation();
    }

    private void resetCarousel() {
        this.mIconContainer.updateSelectedIcon((StatusBarIconView) null);
        this.mIconContainer.setTranslationY(0.0f);
        this.mIconContainer.setAlpha(1.0f);
        this.mIconContainer.setVisibility(0);
    }

    private void resetUserExpanderStates() {
        int i = 2;
        while (true) {
            if (i < getChildCount()) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) getChildAt(i);
                if (expandableNotificationRow.isSummaryWithChildren()) {
                    List<ExpandableNotificationRow> attachedChildren = expandableNotificationRow.getAttachedChildren();
                    for (int i2 = 0; i2 < attachedChildren.size(); i2++) {
                        attachedChildren.get(i2).resetUserExpansion();
                    }
                }
                expandableNotificationRow.resetUserExpansion();
                i++;
            } else {
                this.mGroupManager.collapseGroups();
                this.mCardExpand = false;
                return;
            }
        }
    }

    public void reset() {
        updateChildrenVisual((StatusBarIconView) null);
        resetUserExpanderStates();
        resetCarousel();
        setKeyguardClockVisibility(true);
        showEducationAction(false);
    }

    /* access modifiers changed from: private */
    public void doCarrouselAnimation(boolean z) {
        doCarrouselAnimation(z, false);
    }

    private void doCarrouselAnimation(final boolean z, final boolean z2) {
        this.mCardExpand = z;
        this.mAnimationUtils.cancelAnimation();
        this.mAnimationUtils.reset();
        this.mAnimationUtils.createCarouselAnimation(this.mIconContainer, !z).setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                CliNotificationStackLayout.this.mIconContainer.setVisibility(0);
            }

            public void onAnimationEnd(Animation animation) {
                if (z) {
                    CliNotificationStackLayout.this.mIconContainer.setVisibility(8);
                }
                if (z2) {
                    CliNotificationStackLayout.this.mCliEducationView.setExpanded(z);
                }
            }
        });
        this.mAnimationUtils.startAnimation();
    }

    private final class WaitForClick implements Runnable {
        private ExpandableNotificationRow mClickRow;

        private WaitForClick() {
        }

        public void setRow(ExpandableNotificationRow expandableNotificationRow) {
            this.mClickRow = expandableNotificationRow;
        }

        public void run() {
            ExpandableNotificationRow expandableNotificationRow = this.mClickRow;
            if (expandableNotificationRow != null) {
                expandableNotificationRow.expandCliNotification();
            }
        }

        public void doubleClick() {
            if (this.mClickRow != null) {
                if (CliNotificationStackLayout.this.mCliStatusBarWindowController.isDozing()) {
                    CliNotificationStackLayout.this.mCliStatusBar.wakeupAndResetScreen(true, false);
                }
                CliNotificationStackLayout.this.mNotificationAcitvityStarter.onNotificationClicked(this.mClickRow.getEntry().getSbn(), this.mClickRow);
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeHelper.onInterceptTouchEvent(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeHelper.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }

    private void showEducation() {
        if (this.mCliEducationView.getVisibility() != 0) {
            this.mCliEducationView.setOnNextListener(new CliNotificationStackLayout$$ExternalSyntheticLambda2(this));
            this.mCliEducationView.setSwipeHelper(this.mSwipeHelper);
            StatusBarIconView statusBarIconView = new StatusBarIconView(this.mContext, "education_slot", (StatusBarNotification) null);
            this.mEducationIcon = statusBarIconView;
            statusBarIconView.setImageDrawable(this.mContext.getDrawable(R$drawable.zz_cli_batwing));
            this.mEducationIcon.setIsCarousel(true);
            StatusBarIconView statusBarIconView2 = this.mEducationIcon;
            Context context = this.mContext;
            int i = R$color.cli_education_color;
            statusBarIconView2.setCarouselIconSelectedColor(context.getColor(i));
            this.mEducationIcon.updateColorForced(this.mContext.getColor(i), this.mContext.getColor(R$color.zz_moto_carousel_icon));
            this.mCliEducationView.setEducationIcon(this.mEducationIcon);
            this.mIconContainer.addView(this.mEducationIcon, 0, generateIconLayoutParamsForCarousel());
            this.mCliEducationView.setStep(0);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showEducation$5(boolean z) {
        doCarrouselAnimation(z, true);
    }

    /* access modifiers changed from: private */
    public void hideEducation() {
        this.mCliEducationView.setStep(5);
        this.mIconContainer.removeView(this.mEducationIcon);
        this.mEducationIcon = null;
    }

    /* access modifiers changed from: private */
    public boolean handleEducationCard() {
        return this.mCliEducationView.shouldEducationCard() && this.mCliEducationView.getVisibility() == 0;
    }

    /* access modifiers changed from: private */
    public void showEducationAction(boolean z) {
        if (z) {
            if (this.mCliEducationView.shouldEducationSwipe()) {
                this.mCliEducationView.setStep(6);
            } else if (this.mCliEducationView.shouldEducationDoubleTap()) {
                this.mCliEducationView.setStep(7);
            }
        } else if (!this.mCliEducationView.shouldEducationCard() && this.mCliEducationView.getVisibility() == 0) {
            this.mCliEducationView.setStep(8);
        }
    }
}
