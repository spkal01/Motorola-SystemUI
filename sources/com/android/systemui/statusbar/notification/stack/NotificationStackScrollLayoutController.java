package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.MathUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.ExpandHelper;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.media.KeyguardMediaController;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.LockscreenShadeTransitionController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShelfController;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.ExpandAnimationParameters;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import com.android.systemui.statusbar.notification.collection.render.SectionHeaderController;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.ForegroundServiceDungeonView;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.row.NotificationSnooze;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper;
import com.android.systemui.statusbar.phone.HeadsUpAppearanceController;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.HeadsUpTouchHelper;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.tuner.TunerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import kotlin.Unit;

public class NotificationStackScrollLayoutController {
    /* access modifiers changed from: private */
    public final boolean mAllowLongPress;
    /* access modifiers changed from: private */
    public int mBarState;
    /* access modifiers changed from: private */
    public final ConfigurationController mConfigurationController;
    @VisibleForTesting
    final ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onDensityOrFontScaleChanged() {
            NotificationStackScrollLayoutController.this.updateShowEmptyShadeView();
            NotificationStackScrollLayoutController.this.mView.reinflateViews();
        }

        public void onOverlayChanged() {
            NotificationStackScrollLayoutController.this.updateShowEmptyShadeView();
            NotificationStackScrollLayoutController.this.mView.updateCornerRadius();
            NotificationStackScrollLayoutController.this.mView.updateBgColor();
            NotificationStackScrollLayoutController.this.mView.updateDecorViews();
            NotificationStackScrollLayoutController.this.mView.reinflateViews();
        }

        public void onUiModeChanged() {
            NotificationStackScrollLayoutController.this.mView.updateBgColor();
            NotificationStackScrollLayoutController.this.mView.updateDecorViews();
        }

        public void onThemeChanged() {
            NotificationStackScrollLayoutController.this.updateFooter();
        }

        public void onConfigChanged(Configuration configuration) {
            NotificationStackScrollLayoutController.this.updateResources();
        }
    };
    private final DynamicPrivacyController mDynamicPrivacyController;
    private final DynamicPrivacyController.Listener mDynamicPrivacyControllerListener = new C1730xbae1b0c0(this);
    /* access modifiers changed from: private */
    public boolean mFadeNotificationsOnDismiss;
    /* access modifiers changed from: private */
    public final FalsingCollector mFalsingCollector;
    /* access modifiers changed from: private */
    public final FalsingManager mFalsingManager;
    private final FeatureFlags mFeatureFlags;
    private final ForegroundServiceDismissalFeatureController mFgFeatureController;
    private final ForegroundServiceSectionController mFgServicesSectionController;
    /* access modifiers changed from: private */
    public HeadsUpAppearanceController mHeadsUpAppearanceController;
    /* access modifiers changed from: private */
    public final HeadsUpManagerPhone mHeadsUpManager;
    private final IStatusBarService mIStatusBarService;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardMediaController mKeyguardMediaController;
    private final LayoutInflater mLayoutInflater;
    private final LockscreenShadeTransitionController mLockscreenShadeTransitionController;
    private final NotificationLockscreenUserManager.UserChangedListener mLockscreenUserChangeListener = new NotificationLockscreenUserManager.UserChangedListener() {
        public void onUserChanged(int i) {
            NotificationStackScrollLayoutController.this.mView.updateSensitiveness(false, NotificationStackScrollLayoutController.this.mLockscreenUserManager.isAnyProfilePublicMode());
        }
    };
    /* access modifiers changed from: private */
    public final NotificationLockscreenUserManager mLockscreenUserManager;
    private final NotificationMenuRowPlugin.OnMenuEventListener mMenuEventListener = new NotificationMenuRowPlugin.OnMenuEventListener() {
        public void onMenuClicked(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem) {
            if (NotificationStackScrollLayoutController.this.mAllowLongPress) {
                if (view instanceof ExpandableNotificationRow) {
                    NotificationStackScrollLayoutController.this.mMetricsLogger.write(((ExpandableNotificationRow) view).getEntry().getSbn().getLogMaker().setCategory(333).setType(4));
                }
                NotificationStackScrollLayoutController.this.mNotificationGutsManager.openGuts(view, i, i2, menuItem);
            }
        }

        public void onMenuReset(View view) {
            View translatingParentView = NotificationStackScrollLayoutController.this.mSwipeHelper.getTranslatingParentView();
            if (translatingParentView != null && view == translatingParentView) {
                NotificationStackScrollLayoutController.this.mSwipeHelper.clearExposedMenuView();
                NotificationStackScrollLayoutController.this.mSwipeHelper.clearTranslatingParentView();
                if (view instanceof ExpandableNotificationRow) {
                    NotificationStackScrollLayoutController.this.mHeadsUpManager.setMenuShown(((ExpandableNotificationRow) view).getEntry(), false);
                }
            }
        }

        public void onMenuShown(View view) {
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                NotificationStackScrollLayoutController.this.mMetricsLogger.write(expandableNotificationRow.getEntry().getSbn().getLogMaker().setCategory(332).setType(4));
                NotificationStackScrollLayoutController.this.mHeadsUpManager.setMenuShown(expandableNotificationRow.getEntry(), true);
                NotificationStackScrollLayoutController.this.mSwipeHelper.onMenuShown(view);
                NotificationStackScrollLayoutController.this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
                NotificationMenuRowPlugin provider = expandableNotificationRow.getProvider();
                if (provider.shouldShowGutsOnSnapOpen()) {
                    NotificationMenuRowPlugin.MenuItem menuItemToExposeOnSnap = provider.menuItemToExposeOnSnap();
                    if (menuItemToExposeOnSnap != null) {
                        Point revealAnimationOrigin = provider.getRevealAnimationOrigin();
                        NotificationStackScrollLayoutController.this.mNotificationGutsManager.openGuts(view, revealAnimationOrigin.x, revealAnimationOrigin.y, menuItemToExposeOnSnap);
                    } else {
                        Log.e("StackScrollerController", "Provider has shouldShowGutsOnSnapOpen, but provided no menu item in menuItemtoExposeOnSnap. Skipping.");
                    }
                    NotificationStackScrollLayoutController.this.mSwipeHelper.resetExposedMenuView(false, true);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final MetricsLogger mMetricsLogger;
    private final NotifCollection mNotifCollection;
    private final NotifPipeline mNotifPipeline;
    private final NotificationSwipeHelper.NotificationCallback mNotificationCallback = new NotificationSwipeHelper.NotificationCallback() {
        public void onDismiss() {
            NotificationStackScrollLayoutController.this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
        }

        public float getTotalTranslationLength(View view) {
            return NotificationStackScrollLayoutController.this.mView.getTotalTranslationLength(view);
        }

        public void onSnooze(StatusBarNotification statusBarNotification, NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
            NotificationStackScrollLayoutController.this.mStatusBar.setNotificationSnoozed(statusBarNotification, snoozeOption);
        }

        public boolean shouldDismissQuickly() {
            return NotificationStackScrollLayoutController.this.mView.isExpanded() && NotificationStackScrollLayoutController.this.mView.isFullyAwake();
        }

        public void onDragCancelled(View view) {
            NotificationStackScrollLayoutController.this.mFalsingCollector.onNotificationStopDismissing();
        }

        public void onChildDismissed(View view) {
            if (view instanceof ActivatableNotificationView) {
                ActivatableNotificationView activatableNotificationView = (ActivatableNotificationView) view;
                if (!activatableNotificationView.isDismissed()) {
                    handleChildViewDismissed(view);
                }
                ViewGroup transientContainer = activatableNotificationView.getTransientContainer();
                if (transientContainer != null) {
                    transientContainer.removeTransientView(view);
                }
            }
        }

        public void handleChildViewDismissed(View view) {
            if (!NotificationStackScrollLayoutController.this.mView.getDismissAllInProgress()) {
                NotificationStackScrollLayoutController.this.mView.onSwipeEnd();
                if (view instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                    if (expandableNotificationRow.isHeadsUp()) {
                        NotificationStackScrollLayoutController.this.mHeadsUpManager.addSwipedOutNotification(expandableNotificationRow.getEntry().getSbn().getKey());
                    }
                    expandableNotificationRow.performDismiss(false);
                }
                NotificationStackScrollLayoutController.this.mView.addSwipedOutView(view);
                NotificationStackScrollLayoutController.this.mFalsingCollector.onNotificationDismissed();
                if (NotificationStackScrollLayoutController.this.mFalsingCollector.shouldEnforceBouncer()) {
                    NotificationStackScrollLayoutController.this.mStatusBar.executeRunnableDismissingKeyguard((Runnable) null, (Runnable) null, false, true, false);
                }
            }
        }

        public boolean isAntiFalsingNeeded() {
            return NotificationStackScrollLayoutController.this.mView.onKeyguard();
        }

        public View getChildAtPosition(MotionEvent motionEvent) {
            ExpandableNotificationRow notificationParent;
            ExpandableView childAtPosition = NotificationStackScrollLayoutController.this.mView.getChildAtPosition(motionEvent.getX(), motionEvent.getY(), true, false);
            if (!(childAtPosition instanceof ExpandableNotificationRow) || (notificationParent = ((ExpandableNotificationRow) childAtPosition).getNotificationParent()) == null || !notificationParent.areChildrenExpanded()) {
                return childAtPosition;
            }
            return (notificationParent.areGutsExposed() || NotificationStackScrollLayoutController.this.mSwipeHelper.getExposedMenuView() == notificationParent || (notificationParent.getAttachedChildren().size() == 1 && notificationParent.getEntry().isClearable())) ? notificationParent : childAtPosition;
        }

        public void onBeginDrag(View view) {
            NotificationStackScrollLayoutController.this.mFalsingCollector.onNotificationStartDismissing();
            NotificationStackScrollLayoutController.this.mView.onSwipeBegin(view);
        }

        public void onChildSnappedBack(View view, float f) {
            NotificationStackScrollLayoutController.this.mView.onSwipeEnd();
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                if (expandableNotificationRow.isPinned() && !canChildBeDismissed(expandableNotificationRow) && expandableNotificationRow.getEntry().getSbn().getNotification().fullScreenIntent == null) {
                    NotificationStackScrollLayoutController.this.mHeadsUpManager.removeNotification(expandableNotificationRow.getEntry().getSbn().getKey(), true);
                }
            }
        }

        public boolean updateSwipeProgress(View view, boolean z, float f) {
            return !NotificationStackScrollLayoutController.this.mFadeNotificationsOnDismiss;
        }

        public float getFalsingThresholdFactor() {
            return NotificationStackScrollLayoutController.this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f;
        }

        public int getConstrainSwipeStartPosition() {
            NotificationMenuRowPlugin currentMenuRow = NotificationStackScrollLayoutController.this.mSwipeHelper.getCurrentMenuRow();
            if (currentMenuRow != null) {
                return Math.abs(currentMenuRow.getMenuSnapTarget());
            }
            return 0;
        }

        public boolean canChildBeDismissed(View view) {
            return NotificationStackScrollLayout.canChildBeDismissed(view);
        }

        public boolean canChildBeDismissedInDirection(View view, boolean z) {
            return canChildBeDismissed(view);
        }

        public boolean canChildBeDragged(View view) {
            return !(view instanceof MediaHeaderView);
        }
    };
    private int mNotificationDragDownMovement;
    /* access modifiers changed from: private */
    public final NotificationEntryManager mNotificationEntryManager;
    /* access modifiers changed from: private */
    public final NotificationGutsManager mNotificationGutsManager;
    private final NotificationListContainerImpl mNotificationListContainer = new NotificationListContainerImpl();
    /* access modifiers changed from: private */
    public final NotificationRoundnessManager mNotificationRoundnessManager;
    private final NotificationSwipeHelper.Builder mNotificationSwipeHelperBuilder;
    @VisibleForTesting
    final View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        public void onViewAttachedToWindow(View view) {
            NotificationStackScrollLayoutController.this.mConfigurationController.addCallback(NotificationStackScrollLayoutController.this.mConfigurationListener);
            NotificationStackScrollLayoutController.this.mZenModeController.addCallback(NotificationStackScrollLayoutController.this.mZenModeControllerCallback);
            NotificationStackScrollLayoutController notificationStackScrollLayoutController = NotificationStackScrollLayoutController.this;
            int unused = notificationStackScrollLayoutController.mBarState = notificationStackScrollLayoutController.mStatusBarStateController.getState();
            NotificationStackScrollLayoutController.this.mStatusBarStateController.addCallback(NotificationStackScrollLayoutController.this.mStateListener, 2);
            NotificationStackScrollLayoutController.this.updateFooter();
        }

        public void onViewDetachedFromWindow(View view) {
            NotificationStackScrollLayoutController.this.mConfigurationController.removeCallback(NotificationStackScrollLayoutController.this.mConfigurationListener);
            NotificationStackScrollLayoutController.this.mZenModeController.removeCallback(NotificationStackScrollLayoutController.this.mZenModeControllerCallback);
            NotificationStackScrollLayoutController.this.mStatusBarStateController.removeCallback(NotificationStackScrollLayoutController.this.mStateListener);
        }
    };
    private final OnHeadsUpChangedListener mOnHeadsUpChangedListener = new OnHeadsUpChangedListener() {
        public void onHeadsUpPinnedModeChanged(boolean z) {
            NotificationStackScrollLayoutController.this.mView.setInHeadsUpPinnedMode(z);
        }

        public void onHeadsUpPinned(NotificationEntry notificationEntry) {
            NotificationStackScrollLayoutController.this.mNotificationRoundnessManager.updateView(notificationEntry.getRow(), false);
        }

        public void onHeadsUpUnPinned(NotificationEntry notificationEntry) {
            ExpandableNotificationRow row = notificationEntry.getRow();
            row.post(new C1743x383f1792(this, row));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onHeadsUpUnPinned$0(ExpandableNotificationRow expandableNotificationRow) {
            NotificationStackScrollLayoutController.this.mNotificationRoundnessManager.updateView(expandableNotificationRow, true);
        }

        public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
            long count = NotificationStackScrollLayoutController.this.mHeadsUpManager.getAllEntries().count();
            NotificationEntry topEntry = NotificationStackScrollLayoutController.this.mHeadsUpManager.getTopEntry();
            NotificationStackScrollLayoutController.this.mView.setNumHeadsUp(count);
            NotificationStackScrollLayoutController.this.mView.setTopHeadsUpEntry(topEntry);
            NotificationStackScrollLayoutController.this.generateHeadsUpAnimation(notificationEntry, z);
            NotificationStackScrollLayoutController.this.mNotificationRoundnessManager.updateView(notificationEntry.getRow(), true);
        }
    };
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final Resources mResources;
    private final ScrimController mScrimController;
    private final ShadeController mShadeController;
    private boolean mShowEmptyShadeView;
    private final SectionHeaderController mSilentHeaderController;
    /* access modifiers changed from: private */
    public final StatusBarStateController.StateListener mStateListener = new StatusBarStateController.StateListener() {
        public void onStatePreChange(int i, int i2) {
            if (i == 2 && i2 == 1) {
                NotificationStackScrollLayoutController.this.mView.requestAnimateEverything();
            }
        }

        public void onStateChanged(int i) {
            int unused = NotificationStackScrollLayoutController.this.mBarState = i;
            NotificationStackScrollLayoutController.this.mView.setStatusBarState(NotificationStackScrollLayoutController.this.mBarState);
        }

        public void onStatePostChange() {
            NotificationStackScrollLayoutController.this.mView.updateSensitiveness(NotificationStackScrollLayoutController.this.mStatusBarStateController.goingToFullShade(), NotificationStackScrollLayoutController.this.mLockscreenUserManager.isAnyProfilePublicMode());
            NotificationStackScrollLayoutController.this.mView.onStatePostChange(NotificationStackScrollLayoutController.this.mStatusBarStateController.fromShadeLocked());
            NotificationStackScrollLayoutController.this.mNotificationEntryManager.updateNotifications("StatusBar state changed");
        }
    };
    /* access modifiers changed from: private */
    public final StatusBar mStatusBar;
    /* access modifiers changed from: private */
    public final SysuiStatusBarStateController mStatusBarStateController;
    /* access modifiers changed from: private */
    public NotificationSwipeHelper mSwipeHelper;
    private int mTotalDistanceForFullShadeTransition;
    private final TunerService mTunerService;
    private final UiEventLogger mUiEventLogger;
    /* access modifiers changed from: private */
    public NotificationStackScrollLayout mView;
    private final VisualStabilityManager mVisualStabilityManager;
    /* access modifiers changed from: private */
    public final ZenModeController mZenModeController;
    /* access modifiers changed from: private */
    public final ZenModeController.Callback mZenModeControllerCallback = new ZenModeController.Callback() {
        public void onZenChanged(int i) {
            NotificationStackScrollLayoutController.this.updateShowEmptyShadeView();
        }
    };

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        if (this.mView.isExpanded()) {
            this.mView.setAnimateBottomOnLayout(true);
        }
        this.mView.post(new C1733xa1546772(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateFooter();
        updateSectionBoundaries("dynamic privacy changed");
    }

    /* access modifiers changed from: private */
    public void updateResources() {
        this.mNotificationDragDownMovement = this.mResources.getDimensionPixelSize(R$dimen.lockscreen_shade_notification_movement);
        this.mTotalDistanceForFullShadeTransition = this.mResources.getDimensionPixelSize(R$dimen.lockscreen_shade_qs_transition_distance);
    }

    public void setOverExpansion(float f) {
        this.mView.setOverExpansion(f);
    }

    public NotificationStackScrollLayoutController(boolean z, NotificationGutsManager notificationGutsManager, HeadsUpManagerPhone headsUpManagerPhone, NotificationRoundnessManager notificationRoundnessManager, TunerService tunerService, DynamicPrivacyController dynamicPrivacyController, ConfigurationController configurationController, SysuiStatusBarStateController sysuiStatusBarStateController, KeyguardMediaController keyguardMediaController, KeyguardBypassController keyguardBypassController, ZenModeController zenModeController, SysuiColorExtractor sysuiColorExtractor, NotificationLockscreenUserManager notificationLockscreenUserManager, MetricsLogger metricsLogger, FalsingCollector falsingCollector, FalsingManager falsingManager, Resources resources, NotificationSwipeHelper.Builder builder, StatusBar statusBar, ScrimController scrimController, NotificationGroupManagerLegacy notificationGroupManagerLegacy, GroupExpansionManager groupExpansionManager, SectionHeaderController sectionHeaderController, FeatureFlags featureFlags, NotifPipeline notifPipeline, NotifCollection notifCollection, NotificationEntryManager notificationEntryManager, LockscreenShadeTransitionController lockscreenShadeTransitionController, IStatusBarService iStatusBarService, UiEventLogger uiEventLogger, ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController, ForegroundServiceSectionController foregroundServiceSectionController, LayoutInflater layoutInflater, NotificationRemoteInputManager notificationRemoteInputManager, VisualStabilityManager visualStabilityManager, ShadeController shadeController) {
        this.mAllowLongPress = z;
        this.mNotificationGutsManager = notificationGutsManager;
        this.mHeadsUpManager = headsUpManagerPhone;
        this.mNotificationRoundnessManager = notificationRoundnessManager;
        this.mTunerService = tunerService;
        this.mDynamicPrivacyController = dynamicPrivacyController;
        this.mConfigurationController = configurationController;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mKeyguardMediaController = keyguardMediaController;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mZenModeController = zenModeController;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mMetricsLogger = metricsLogger;
        this.mLockscreenShadeTransitionController = lockscreenShadeTransitionController;
        this.mFalsingCollector = falsingCollector;
        this.mFalsingManager = falsingManager;
        this.mResources = resources;
        this.mNotificationSwipeHelperBuilder = builder;
        this.mStatusBar = statusBar;
        this.mScrimController = scrimController;
        groupExpansionManager.registerGroupExpansionChangeListener(new C1736xbae1b0c2(this));
        notificationGroupManagerLegacy.registerGroupChangeListener(new NotificationGroupManagerLegacy.OnGroupChangeListener() {
            public void onGroupCreatedFromChildren(NotificationGroupManagerLegacy.NotificationGroup notificationGroup) {
                NotificationStackScrollLayoutController.this.mStatusBar.requestNotificationUpdate("onGroupCreatedFromChildren");
            }

            public void onGroupsChanged() {
                NotificationStackScrollLayoutController.this.mStatusBar.requestNotificationUpdate("onGroupsChanged");
            }
        });
        this.mSilentHeaderController = sectionHeaderController;
        this.mFeatureFlags = featureFlags;
        this.mNotifPipeline = notifPipeline;
        this.mNotifCollection = notifCollection;
        this.mNotificationEntryManager = notificationEntryManager;
        this.mIStatusBarService = iStatusBarService;
        this.mUiEventLogger = uiEventLogger;
        this.mFgFeatureController = foregroundServiceDismissalFeatureController;
        this.mFgServicesSectionController = foregroundServiceSectionController;
        this.mLayoutInflater = layoutInflater;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mShadeController = shadeController;
        updateResources();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(ExpandableNotificationRow expandableNotificationRow, boolean z) {
        this.mView.onGroupExpandChanged(expandableNotificationRow, z);
    }

    public void attach(NotificationStackScrollLayout notificationStackScrollLayout) {
        this.mView = notificationStackScrollLayout;
        notificationStackScrollLayout.setController(this);
        this.mView.setTouchHandler(new TouchHandler());
        this.mView.setStatusBar(this.mStatusBar);
        this.mView.setDismissAllAnimationListener(new C1737xbae1b0c3(this));
        this.mView.setDismissListener(new C1738xbae1b0c4(this));
        this.mView.setFooterDismissListener(new C1739xbae1b0c5(this));
        this.mView.setRemoteInputManager(this.mRemoteInputManager);
        this.mView.setShadeController(this.mShadeController);
        if (this.mFgFeatureController.isForegroundServiceDismissalEnabled()) {
            this.mView.initializeForegroundServiceSection((ForegroundServiceDungeonView) this.mFgServicesSectionController.createView(this.mLayoutInflater));
        }
        this.mSwipeHelper = this.mNotificationSwipeHelperBuilder.setSwipeDirection(0).setNotificationCallback(this.mNotificationCallback).setOnMenuEventListener(this.mMenuEventListener).build();
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mNotifPipeline.addCollectionListener(new NotifCollectionListener() {
                public void onEntryUpdated(NotificationEntry notificationEntry) {
                    NotificationStackScrollLayoutController.this.mView.onEntryUpdated(notificationEntry);
                }
            });
        } else {
            this.mNotificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
                public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                    NotificationStackScrollLayoutController.this.mView.onEntryUpdated(notificationEntry);
                }
            });
        }
        NotificationStackScrollLayout notificationStackScrollLayout2 = this.mView;
        Context context = notificationStackScrollLayout2.getContext();
        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
        Objects.requireNonNull(keyguardBypassController);
        notificationStackScrollLayout2.initView(context, new C1740xbae1b0c6(keyguardBypassController), this.mSwipeHelper);
        this.mHeadsUpManager.addListener(this.mOnHeadsUpChangedListener);
        HeadsUpManagerPhone headsUpManagerPhone = this.mHeadsUpManager;
        NotificationStackScrollLayout notificationStackScrollLayout3 = this.mView;
        Objects.requireNonNull(notificationStackScrollLayout3);
        headsUpManagerPhone.setAnimationStateHandler(new C1741xbae1b0c7(notificationStackScrollLayout3));
        this.mDynamicPrivacyController.addListener(this.mDynamicPrivacyControllerListener);
        ScrimController scrimController = this.mScrimController;
        NotificationStackScrollLayout notificationStackScrollLayout4 = this.mView;
        Objects.requireNonNull(notificationStackScrollLayout4);
        scrimController.setScrimBehindChangeRunnable(new C1732xa1546771(notificationStackScrollLayout4));
        this.mLockscreenShadeTransitionController.setStackScroller(this);
        this.mLockscreenUserManager.addUserChangedListener(this.mLockscreenUserChangeListener);
        this.mFadeNotificationsOnDismiss = this.mResources.getBoolean(R$bool.config_fadeNotificationsOnDismiss);
        NotificationRoundnessManager notificationRoundnessManager = this.mNotificationRoundnessManager;
        NotificationStackScrollLayout notificationStackScrollLayout5 = this.mView;
        Objects.requireNonNull(notificationStackScrollLayout5);
        notificationRoundnessManager.setOnRoundingChangedCallback(new C1731xa1546770(notificationStackScrollLayout5));
        NotificationStackScrollLayout notificationStackScrollLayout6 = this.mView;
        NotificationRoundnessManager notificationRoundnessManager2 = this.mNotificationRoundnessManager;
        Objects.requireNonNull(notificationRoundnessManager2);
        notificationStackScrollLayout6.addOnExpandedHeightChangedListener(new C1691x60684b(notificationRoundnessManager2));
        this.mVisualStabilityManager.setVisibilityLocationProvider(new C1735xbae1b0c1(this));
        this.mTunerService.addTunable(new C1742xbae1b0c8(this), "high_priority", "notification_dismiss_rtl", "notification_history_enabled");
        this.mKeyguardMediaController.setVisibilityChangedListener(new C1734xa1546773(this));
        if (this.mView.isAttachedToWindow()) {
            this.mOnAttachStateChangeListener.onViewAttachedToWindow(this.mView);
        }
        this.mView.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        this.mSilentHeaderController.setOnClearAllClickListener(new C1729xbae1b0bf(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$attach$3(int i) {
        this.mUiEventLogger.log(NotificationPanelEvent.fromSelection(i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$attach$4() {
        this.mMetricsLogger.action(148);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$attach$5(String str, String str2) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -748542495:
                if (str.equals("notification_dismiss_rtl")) {
                    c = 0;
                    break;
                }
                break;
            case -220265567:
                if (str.equals("high_priority")) {
                    c = 1;
                    break;
                }
                break;
            case 1304816450:
                if (str.equals("notification_history_enabled")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.mView.updateDismissRtlSetting("1".equals(str2));
                return;
            case 1:
                this.mView.setHighPriorityBeforeSpeedBump("1".equals(str2));
                return;
            case 2:
                updateFooter();
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Unit lambda$attach$6(Boolean bool) {
        if (bool.booleanValue()) {
            this.mView.generateAddAnimation(this.mKeyguardMediaController.getSinglePaneContainer(), false);
        } else {
            this.mView.generateRemoveAnimation(this.mKeyguardMediaController.getSinglePaneContainer());
        }
        this.mView.requestChildrenUpdate();
        return Unit.INSTANCE;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$attach$7(View view) {
        clearSilentNotifications();
    }

    /* access modifiers changed from: private */
    public boolean isInVisibleLocation(NotificationEntry notificationEntry) {
        ExpandableNotificationRow row = notificationEntry.getRow();
        ExpandableViewState viewState = row.getViewState();
        if (viewState == null || (viewState.location & 5) == 0 || row.getVisibility() != 0) {
            return false;
        }
        return true;
    }

    public boolean isViewAffectedBySwipe(ExpandableView expandableView) {
        return this.mNotificationRoundnessManager.isViewAffectedBySwipe(expandableView);
    }

    public void addOnExpandedHeightChangedListener(BiConsumer<Float, Float> biConsumer) {
        this.mView.addOnExpandedHeightChangedListener(biConsumer);
    }

    public void removeOnExpandedHeightChangedListener(BiConsumer<Float, Float> biConsumer) {
        this.mView.removeOnExpandedHeightChangedListener(biConsumer);
    }

    public void setHeadsUpAppearanceController(HeadsUpAppearanceController headsUpAppearanceController) {
        this.mHeadsUpAppearanceController = headsUpAppearanceController;
        this.mView.setHeadsUpAppearanceController(headsUpAppearanceController);
    }

    public void requestLayout() {
        this.mView.requestLayout();
    }

    public int getRight() {
        return this.mView.getRight();
    }

    public int getLeft() {
        return this.mView.getLeft();
    }

    public int getTop() {
        return this.mView.getTop();
    }

    public int indexOfChild(View view) {
        return this.mView.indexOfChild(view);
    }

    public void setOnHeightChangedListener(ExpandableView.OnHeightChangedListener onHeightChangedListener) {
        this.mView.setOnHeightChangedListener(onHeightChangedListener);
    }

    public void setOverscrollTopChangedListener(NotificationStackScrollLayout.OnOverscrollTopChangedListener onOverscrollTopChangedListener) {
        this.mView.setOverscrollTopChangedListener(onOverscrollTopChangedListener);
    }

    public void setOnEmptySpaceClickListener(NotificationStackScrollLayout.OnEmptySpaceClickListener onEmptySpaceClickListener) {
        this.mView.setOnEmptySpaceClickListener(onEmptySpaceClickListener);
    }

    public void setTrackingHeadsUp(ExpandableNotificationRow expandableNotificationRow) {
        this.mView.setTrackingHeadsUp(expandableNotificationRow);
        this.mNotificationRoundnessManager.setTrackingHeadsUp(expandableNotificationRow);
    }

    public void wakeUpFromPulse() {
        this.mView.wakeUpFromPulse();
    }

    public boolean isPulseExpanding() {
        return this.mView.isPulseExpanding();
    }

    public void setOnPulseHeightChangedListener(Runnable runnable) {
        this.mView.setOnPulseHeightChangedListener(runnable);
    }

    public void setDozeAmount(float f) {
        this.mView.setDozeAmount(f);
    }

    public int getSpeedBumpIndex() {
        return this.mView.getSpeedBumpIndex();
    }

    public void setHideAmount(float f, float f2) {
        this.mView.setHideAmount(f, f2);
    }

    public void notifyHideAnimationStart(boolean z) {
        this.mView.notifyHideAnimationStart(z);
    }

    public float setPulseHeight(float f) {
        return this.mView.setPulseHeight(f);
    }

    public void getLocationOnScreen(int[] iArr) {
        this.mView.getLocationOnScreen(iArr);
    }

    public ExpandableView getChildAtRawPosition(float f, float f2) {
        return this.mView.getChildAtRawPosition(f, f2);
    }

    public void setIsFullWidth(boolean z) {
        this.mView.setIsFullWidth(z);
    }

    public boolean isAddOrRemoveAnimationPending() {
        return this.mView.isAddOrRemoveAnimationPending();
    }

    public int getVisibleNotificationCount() {
        return this.mView.getVisibleNotificationCount();
    }

    public int getIntrinsicContentHeight() {
        return this.mView.getIntrinsicContentHeight();
    }

    public void setIntrinsicPadding(int i) {
        this.mView.setIntrinsicPadding(i);
    }

    public int getHeight() {
        return this.mView.getHeight();
    }

    public int getChildCount() {
        return this.mView.getChildCount();
    }

    public ExpandableView getChildAt(int i) {
        return (ExpandableView) this.mView.getChildAt(i);
    }

    public void goToFullShade(long j) {
        this.mView.goToFullShade(j);
    }

    public void setOverScrollAmount(float f, boolean z, boolean z2, boolean z3) {
        this.mView.setOverScrollAmount(f, z, z2, z3);
    }

    public void setOverScrollAmount(float f, boolean z, boolean z2) {
        this.mView.setOverScrollAmount(f, z, z2);
    }

    public void resetScrollPosition() {
        this.mView.resetScrollPosition();
    }

    public void setShouldShowShelfOnly(boolean z) {
        this.mView.setShouldShowShelfOnly(z);
    }

    public void cancelLongPress() {
        this.mView.cancelLongPress();
    }

    public float getX() {
        return this.mView.getX();
    }

    public boolean isBelowLastNotification(float f, float f2) {
        return this.mView.isBelowLastNotification(f, f2);
    }

    public float getWidth() {
        return (float) this.mView.getWidth();
    }

    public float getOpeningHeight() {
        return this.mView.getOpeningHeight();
    }

    public float getBottomMostNotificationBottom() {
        return this.mView.getBottomMostNotificationBottom();
    }

    public void checkSnoozeLeavebehind() {
        if (this.mView.getCheckSnoozeLeaveBehind()) {
            this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
            this.mView.setCheckForLeaveBehind(false);
        }
    }

    public void setQsExpanded(boolean z) {
        this.mView.setQsExpanded(z);
        updateShowEmptyShadeView();
    }

    public void setScrollingEnabled(boolean z) {
        this.mView.setScrollingEnabled(z);
    }

    public void setQsExpansionFraction(float f) {
        this.mView.setQsExpansionFraction(f);
    }

    public void setOnStackYChanged(Consumer<Boolean> consumer) {
        this.mView.setOnStackYChanged(consumer);
    }

    public float calculateAppearFractionBypass() {
        return this.mView.calculateAppearFractionBypass();
    }

    public void updateTopPadding(float f, boolean z) {
        this.mView.updateTopPadding(f, z);
    }

    public boolean isScrolledToBottom() {
        return this.mView.isScrolledToBottom();
    }

    public int getNotGoneChildCount() {
        return this.mView.getNotGoneChildCount();
    }

    public float getIntrinsicPadding() {
        return (float) this.mView.getIntrinsicPadding();
    }

    public float getLayoutMinHeight() {
        return (float) this.mView.getLayoutMinHeight();
    }

    public int getEmptyBottomMargin() {
        return this.mView.getEmptyBottomMargin();
    }

    public float getTopPaddingOverflow() {
        return this.mView.getTopPaddingOverflow();
    }

    public int getTopPadding() {
        return this.mView.getTopPadding();
    }

    public float getEmptyShadeViewHeight() {
        return (float) this.mView.getEmptyShadeViewHeight();
    }

    public void setAlpha(float f) {
        this.mView.setAlpha(f);
    }

    public float calculateAppearFraction(float f) {
        return this.mView.calculateAppearFraction(f);
    }

    public void onExpansionStarted() {
        this.mView.onExpansionStarted();
        checkSnoozeLeavebehind();
    }

    public void onExpansionStopped() {
        this.mView.setCheckForLeaveBehind(false);
        this.mView.onExpansionStopped();
    }

    public void onPanelTrackingStarted() {
        this.mView.onPanelTrackingStarted();
    }

    public void onPanelTrackingStopped() {
        this.mView.onPanelTrackingStopped();
    }

    public void setHeadsUpBoundaries(int i, int i2) {
        this.mView.setHeadsUpBoundaries(i, i2);
    }

    public void setUnlockHintRunning(boolean z) {
        this.mView.setUnlockHintRunning(z);
    }

    public void updateShowEmptyShadeView() {
        boolean z = true;
        if (this.mBarState == 1 || this.mView.isQsExpanded() || this.mView.getVisibleNotificationCount() != 0) {
            z = false;
        }
        this.mShowEmptyShadeView = z;
        this.mView.updateEmptyShadeView(z, this.mZenModeController.areNotificationsHiddenInShade());
    }

    public boolean isShowingEmptyShadeView() {
        return this.mShowEmptyShadeView;
    }

    public void setHeadsUpAnimatingAway(boolean z) {
        this.mView.setHeadsUpAnimatingAway(z);
    }

    public HeadsUpTouchHelper.Callback getHeadsUpCallback() {
        return this.mView.getHeadsUpCallback();
    }

    public void forceNoOverlappingRendering(boolean z) {
        this.mView.forceNoOverlappingRendering(z);
    }

    public void setTranslationX(float f) {
        this.mView.setTranslationX(f);
    }

    public void setExpandingVelocity(float f) {
        this.mView.setExpandingVelocity(f);
    }

    public void setExpandedHeight(float f) {
        this.mView.setExpandedHeight(f);
    }

    public void setQsContainer(ViewGroup viewGroup) {
        this.mView.setQsContainer(viewGroup);
    }

    public void setAnimationsEnabled(boolean z) {
        this.mView.setAnimationsEnabled(z);
    }

    public void setDozing(boolean z, boolean z2, PointF pointF) {
        this.mView.setDozing(z, z2, pointF);
    }

    public void setPulsing(boolean z, boolean z2) {
        this.mView.setPulsing(z, z2);
    }

    public boolean hasActiveClearableNotifications(int i) {
        if (this.mDynamicPrivacyController.isInLockedDownShade()) {
            return false;
        }
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            ExpandableView childAt = getChildAt(i2);
            if (childAt instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) childAt;
                if (expandableNotificationRow.canViewBeDismissed() && NotificationStackScrollLayout.matchesSelection(expandableNotificationRow, i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setMaxDisplayedNotifications(int i) {
        this.mNotificationListContainer.setMaxDisplayedNotifications(i);
    }

    public RemoteInputController.Delegate createDelegate() {
        return new RemoteInputController.Delegate() {
            public void setRemoteInputActive(NotificationEntry notificationEntry, boolean z) {
                NotificationStackScrollLayoutController.this.mHeadsUpManager.setRemoteInputActive(notificationEntry, z);
                notificationEntry.notifyHeightChanged(true);
                NotificationStackScrollLayoutController.this.updateFooter();
            }

            public void lockScrollTo(NotificationEntry notificationEntry) {
                NotificationStackScrollLayoutController.this.mView.lockScrollTo(notificationEntry.getRow());
            }

            public void requestDisallowLongPressAndDismiss() {
                NotificationStackScrollLayoutController.this.mView.requestDisallowLongPress();
                NotificationStackScrollLayoutController.this.mView.requestDisallowDismiss();
            }
        };
    }

    public void updateSectionBoundaries(String str) {
        this.mView.updateSectionBoundaries(str);
    }

    public void updateFooter() {
        this.mView.updateFooter();
    }

    public void onUpdateRowStates() {
        this.mView.onUpdateRowStates();
    }

    public ActivatableNotificationView getActivatedChild() {
        return this.mView.getActivatedChild();
    }

    public void setActivatedChild(ActivatableNotificationView activatableNotificationView) {
        this.mView.setActivatedChild(activatableNotificationView);
    }

    public void runAfterAnimationFinished(Runnable runnable) {
        this.mView.runAfterAnimationFinished(runnable);
    }

    public void setShelfController(NotificationShelfController notificationShelfController) {
        this.mView.setShelfController(notificationShelfController);
    }

    public ExpandableView getFirstChildNotGone() {
        return this.mView.getFirstChildNotGone();
    }

    /* access modifiers changed from: private */
    public void generateHeadsUpAnimation(NotificationEntry notificationEntry, boolean z) {
        this.mView.generateHeadsUpAnimation(notificationEntry, z);
    }

    public void generateHeadsUpAnimation(ExpandableNotificationRow expandableNotificationRow, boolean z) {
        this.mView.generateHeadsUpAnimation(expandableNotificationRow, z);
    }

    public void setMaxTopPadding(int i) {
        this.mView.setMaxTopPadding(i);
    }

    public int getTransientViewCount() {
        return this.mView.getTransientViewCount();
    }

    public View getTransientView(int i) {
        return this.mView.getTransientView(i);
    }

    public NotificationStackScrollLayout getView() {
        return this.mView;
    }

    public float calculateGapHeight(ExpandableView expandableView, ExpandableView expandableView2, int i) {
        return this.mView.calculateGapHeight(expandableView, expandableView2, i);
    }

    /* access modifiers changed from: package-private */
    public NotificationRoundnessManager getNoticationRoundessManager() {
        return this.mNotificationRoundnessManager;
    }

    public NotificationListContainer getNotificationListContainer() {
        return this.mNotificationListContainer;
    }

    public void resetCheckSnoozeLeavebehind() {
        this.mView.resetCheckSnoozeLeavebehind();
    }

    private DismissedByUserStats getDismissedByUserStats(NotificationEntry notificationEntry, int i) {
        return new DismissedByUserStats(3, 1, NotificationVisibility.obtain(notificationEntry.getKey(), notificationEntry.getRanking().getRank(), i, true, NotificationLogger.getNotificationLocation(notificationEntry)));
    }

    /* JADX WARNING: type inference failed for: r2v1, types: [android.view.View] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void closeControlsIfOutsideTouch(android.view.MotionEvent r8) {
        /*
            r7 = this;
            com.android.systemui.statusbar.notification.row.NotificationGutsManager r0 = r7.mNotificationGutsManager
            com.android.systemui.statusbar.notification.row.NotificationGuts r0 = r0.getExposedGuts()
            com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper r1 = r7.mSwipeHelper
            com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin r1 = r1.getCurrentMenuRow()
            com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper r2 = r7.mSwipeHelper
            android.view.View r2 = r2.getTranslatingParentView()
            if (r0 == 0) goto L_0x001f
            com.android.systemui.statusbar.notification.row.NotificationGuts$GutsContent r3 = r0.getGutsContent()
            boolean r3 = r3.isLeavebehind()
            if (r3 != 0) goto L_0x001f
            goto L_0x002c
        L_0x001f:
            if (r1 == 0) goto L_0x002b
            boolean r0 = r1.isMenuVisible()
            if (r0 == 0) goto L_0x002b
            if (r2 == 0) goto L_0x002b
            r0 = r2
            goto L_0x002c
        L_0x002b:
            r0 = 0
        L_0x002c:
            if (r0 == 0) goto L_0x0045
            boolean r8 = com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper.isTouchInView(r8, r0)
            if (r8 != 0) goto L_0x0045
            com.android.systemui.statusbar.notification.row.NotificationGutsManager r0 = r7.mNotificationGutsManager
            r1 = 0
            r2 = 0
            r3 = 1
            r4 = -1
            r5 = -1
            r6 = 0
            r0.closeAndSaveGuts(r1, r2, r3, r4, r5, r6)
            com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper r7 = r7.mSwipeHelper
            r8 = 1
            r7.resetExposedMenuView(r8, r8)
        L_0x0045:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController.closeControlsIfOutsideTouch(android.view.MotionEvent):void");
    }

    public void clearSilentNotifications() {
        this.mView.clearNotifications(2, true ^ hasActiveClearableNotifications(1));
    }

    /* access modifiers changed from: private */
    public void onAnimationEnd(List<ExpandableNotificationRow> list, int i) {
        if (!this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            for (ExpandableNotificationRow next : list) {
                if (NotificationStackScrollLayout.canChildBeDismissed(next)) {
                    this.mNotificationEntryManager.performRemoveNotification(next.getEntry().getSbn(), getDismissedByUserStats(next.getEntry(), this.mNotificationEntryManager.getActiveNotificationsCount()), 3);
                } else {
                    next.resetTranslation();
                }
            }
            if (i == 0) {
                try {
                    this.mIStatusBarService.onClearAllNotifications(this.mLockscreenUserManager.getCurrentUserId());
                } catch (Exception unused) {
                }
            }
        } else if (i == 0) {
            this.mNotifCollection.dismissAllNotifications(this.mLockscreenUserManager.getCurrentUserId());
        } else {
            ArrayList arrayList = new ArrayList();
            int shadeListCount = this.mNotifPipeline.getShadeListCount();
            for (ExpandableNotificationRow entry : list) {
                NotificationEntry entry2 = entry.getEntry();
                arrayList.add(new Pair(entry2, getDismissedByUserStats(entry2, shadeListCount)));
            }
            this.mNotifCollection.dismissNotifications(arrayList);
        }
    }

    public ExpandHelper.Callback getExpandHelperCallback() {
        return this.mView.getExpandHelperCallback();
    }

    public boolean isInLockedDownShade() {
        return this.mDynamicPrivacyController.isInLockedDownShade();
    }

    public void setDimmed(boolean z, boolean z2) {
        this.mView.setDimmed(z, z2);
    }

    public int getFullShadeTransitionInset() {
        MediaHeaderView singlePaneContainer = this.mKeyguardMediaController.getSinglePaneContainer();
        if (singlePaneContainer == null || singlePaneContainer.getHeight() == 0 || this.mStatusBarStateController.getState() != 1) {
            return 0;
        }
        return singlePaneContainer.getHeight() + this.mView.getPaddingAfterMedia();
    }

    public void setTransitionToFullShadeAmount(float f) {
        this.mView.setExtraTopInsetForFullShadeTransition(this.mStatusBarStateController.getState() == 1 ? Interpolators.getOvershootInterpolation(MathUtils.saturate(f / ((float) this.mView.getHeight())), 0.6f, ((float) this.mTotalDistanceForFullShadeTransition) / ((float) this.mView.getHeight())) * ((float) this.mNotificationDragDownMovement) : 0.0f);
    }

    public void setOnScrollListener(Consumer<Integer> consumer) {
        this.mView.setOnScrollListener(consumer);
    }

    public void setRoundedClippingBounds(int i, int i2, int i3, int i4, int i5, int i6) {
        this.mView.setRoundedClippingBounds(i, i2, i3, i4, i5, i6);
    }

    public void animateNextTopPaddingChange() {
        this.mView.animateNextTopPaddingChange();
    }

    enum NotificationPanelEvent implements UiEventLogger.UiEventEnum {
        INVALID(0),
        DISMISS_ALL_NOTIFICATIONS_PANEL(312),
        DISMISS_SILENT_NOTIFICATIONS_PANEL(314);
        
        private final int mId;

        private NotificationPanelEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }

        public static UiEventLogger.UiEventEnum fromSelection(int i) {
            if (i == 0) {
                return DISMISS_ALL_NOTIFICATIONS_PANEL;
            }
            if (i == 2) {
                return DISMISS_SILENT_NOTIFICATIONS_PANEL;
            }
            return INVALID;
        }
    }

    private class NotificationListContainerImpl implements NotificationListContainer {
        private NotificationListContainerImpl() {
        }

        public void setChildTransferInProgress(boolean z) {
            NotificationStackScrollLayoutController.this.mView.setChildTransferInProgress(z);
        }

        public void changeViewPosition(ExpandableView expandableView, int i) {
            NotificationStackScrollLayoutController.this.mView.changeViewPosition(expandableView, i);
        }

        public void notifyGroupChildAdded(ExpandableView expandableView) {
            NotificationStackScrollLayoutController.this.mView.notifyGroupChildAdded(expandableView);
        }

        public void notifyGroupChildRemoved(ExpandableView expandableView, ViewGroup viewGroup) {
            NotificationStackScrollLayoutController.this.mView.notifyGroupChildRemoved(expandableView, viewGroup);
        }

        public void generateAddAnimation(ExpandableView expandableView, boolean z) {
            NotificationStackScrollLayoutController.this.mView.generateAddAnimation(expandableView, z);
        }

        public void generateChildOrderChangedEvent() {
            NotificationStackScrollLayoutController.this.mView.generateChildOrderChangedEvent();
        }

        public int getContainerChildCount() {
            return NotificationStackScrollLayoutController.this.mView.getContainerChildCount();
        }

        public void setNotificationActivityStarter(NotificationActivityStarter notificationActivityStarter) {
            NotificationStackScrollLayoutController.this.mView.setNotificationActivityStarter(notificationActivityStarter);
        }

        public int getTopClippingStartLocation() {
            return NotificationStackScrollLayoutController.this.mView.getTopClippingStartLocation();
        }

        public View getContainerChildAt(int i) {
            return NotificationStackScrollLayoutController.this.mView.getContainerChildAt(i);
        }

        public void removeContainerView(View view) {
            NotificationStackScrollLayoutController.this.mView.removeContainerView(view);
        }

        public void addContainerView(View view) {
            NotificationStackScrollLayoutController.this.mView.addContainerView(view);
        }

        public void addContainerViewAt(View view, int i) {
            NotificationStackScrollLayoutController.this.mView.addContainerViewAt(view, i);
        }

        public void setMaxDisplayedNotifications(int i) {
            NotificationStackScrollLayoutController.this.mView.setMaxDisplayedNotifications(i);
        }

        public ViewGroup getViewParentForNotification(NotificationEntry notificationEntry) {
            return NotificationStackScrollLayoutController.this.mView.getViewParentForNotification(notificationEntry);
        }

        public void resetExposedMenuView(boolean z, boolean z2) {
            NotificationStackScrollLayoutController.this.mSwipeHelper.resetExposedMenuView(z, z2);
        }

        public NotificationSwipeActionHelper getSwipeActionHelper() {
            return NotificationStackScrollLayoutController.this.mSwipeHelper;
        }

        public void cleanUpViewStateForEntry(NotificationEntry notificationEntry) {
            NotificationStackScrollLayoutController.this.mView.cleanUpViewStateForEntry(notificationEntry);
        }

        public void setChildLocationsChangedListener(NotificationLogger.OnChildLocationsChangedListener onChildLocationsChangedListener) {
            NotificationStackScrollLayoutController.this.mView.setChildLocationsChangedListener(onChildLocationsChangedListener);
        }

        public boolean hasPulsingNotifications() {
            return NotificationStackScrollLayoutController.this.mView.hasPulsingNotifications();
        }

        public boolean isInVisibleLocation(NotificationEntry notificationEntry) {
            return NotificationStackScrollLayoutController.this.isInVisibleLocation(notificationEntry);
        }

        public void onHeightChanged(ExpandableView expandableView, boolean z) {
            NotificationStackScrollLayoutController.this.mView.onChildHeightChanged(expandableView, z);
        }

        public void onReset(ExpandableView expandableView) {
            NotificationStackScrollLayoutController.this.mView.onChildHeightReset(expandableView);
        }

        public void bindRow(ExpandableNotificationRow expandableNotificationRow) {
            expandableNotificationRow.setHeadsUpAnimatingAwayListener(new C1744xbe435293(this, expandableNotificationRow));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$bindRow$0(ExpandableNotificationRow expandableNotificationRow, Boolean bool) {
            NotificationStackScrollLayoutController.this.mNotificationRoundnessManager.updateView(expandableNotificationRow, false);
            NotificationStackScrollLayoutController.this.mHeadsUpAppearanceController.lambda$updateHeadsUpHeaders$3(expandableNotificationRow.getEntry());
        }

        public void applyExpandAnimationParams(ExpandAnimationParameters expandAnimationParameters) {
            NotificationStackScrollLayoutController.this.mView.applyExpandAnimationParams(expandAnimationParameters);
        }

        public void setExpandingNotification(ExpandableNotificationRow expandableNotificationRow) {
            NotificationStackScrollLayoutController.this.mView.setExpandingNotification(expandableNotificationRow);
        }

        public boolean containsView(View view) {
            return NotificationStackScrollLayoutController.this.mView.containsView(view);
        }

        public void setCliNotificationListContainer(NotificationListContainer notificationListContainer) {
            NotificationStackScrollLayoutController.this.mView.setCliNotificationListContainer(notificationListContainer, NotificationStackScrollLayoutController.this.mNotificationEntryManager);
        }
    }

    class TouchHandler implements Gefingerpoken {
        TouchHandler() {
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            NotificationStackScrollLayoutController.this.mView.initDownStates(motionEvent);
            NotificationStackScrollLayoutController.this.mView.handleEmptySpaceClick(motionEvent);
            NotificationGuts exposedGuts = NotificationStackScrollLayoutController.this.mNotificationGutsManager.getExposedGuts();
            boolean onInterceptTouchEvent = (NotificationStackScrollLayoutController.this.mSwipeHelper.isSwiping() || NotificationStackScrollLayoutController.this.mView.getOnlyScrollingInThisMotion() || exposedGuts != null) ? false : NotificationStackScrollLayoutController.this.mView.getExpandHelper().onInterceptTouchEvent(motionEvent);
            boolean onInterceptTouchEventScroll = (NotificationStackScrollLayoutController.this.mSwipeHelper.isSwiping() || NotificationStackScrollLayoutController.this.mView.isExpandingNotification()) ? false : NotificationStackScrollLayoutController.this.mView.onInterceptTouchEventScroll(motionEvent);
            boolean onInterceptTouchEvent2 = (NotificationStackScrollLayoutController.this.mView.isBeingDragged() || NotificationStackScrollLayoutController.this.mView.isExpandingNotification() || NotificationStackScrollLayoutController.this.mView.getExpandedInThisMotion() || NotificationStackScrollLayoutController.this.mView.getOnlyScrollingInThisMotion() || NotificationStackScrollLayoutController.this.mView.getDisallowDismissInThisMotion()) ? false : NotificationStackScrollLayoutController.this.mSwipeHelper.onInterceptTouchEvent(motionEvent);
            boolean z = motionEvent.getActionMasked() == 1;
            if (!NotificationSwipeHelper.isTouchInView(motionEvent, exposedGuts) && z && !onInterceptTouchEvent2 && !onInterceptTouchEvent && !onInterceptTouchEventScroll) {
                NotificationStackScrollLayoutController.this.mView.setCheckForLeaveBehind(false);
                NotificationStackScrollLayoutController.this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
            }
            if (motionEvent.getActionMasked() == 1) {
                NotificationStackScrollLayoutController.this.mView.setCheckForLeaveBehind(true);
            }
            if (onInterceptTouchEventScroll && motionEvent.getActionMasked() != 0) {
                InteractionJankMonitor.getInstance().begin(NotificationStackScrollLayoutController.this.mView, 2);
            }
            if (onInterceptTouchEvent2 || onInterceptTouchEventScroll || onInterceptTouchEvent) {
                return true;
            }
            return false;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean z;
            NotificationGuts exposedGuts = NotificationStackScrollLayoutController.this.mNotificationGutsManager.getExposedGuts();
            boolean z2 = motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1;
            NotificationStackScrollLayoutController.this.mView.handleEmptySpaceClick(motionEvent);
            boolean onlyScrollingInThisMotion = NotificationStackScrollLayoutController.this.mView.getOnlyScrollingInThisMotion();
            boolean isExpandingNotification = NotificationStackScrollLayoutController.this.mView.isExpandingNotification();
            if (!NotificationStackScrollLayoutController.this.mView.getIsExpanded() || NotificationStackScrollLayoutController.this.mSwipeHelper.isSwiping() || onlyScrollingInThisMotion || exposedGuts != null) {
                z = false;
            } else {
                ExpandHelper expandHelper = NotificationStackScrollLayoutController.this.mView.getExpandHelper();
                if (z2) {
                    expandHelper.onlyObserveMovements(false);
                }
                z = expandHelper.onTouchEvent(motionEvent);
                boolean isExpandingNotification2 = NotificationStackScrollLayoutController.this.mView.isExpandingNotification();
                if (NotificationStackScrollLayoutController.this.mView.getExpandedInThisMotion() && !isExpandingNotification2 && isExpandingNotification && !NotificationStackScrollLayoutController.this.mView.getDisallowScrollingInThisMotion()) {
                    NotificationStackScrollLayoutController.this.mView.dispatchDownEventToScroller(motionEvent);
                }
                isExpandingNotification = isExpandingNotification2;
            }
            boolean onScrollTouch = (!NotificationStackScrollLayoutController.this.mView.isExpanded() || NotificationStackScrollLayoutController.this.mSwipeHelper.isSwiping() || isExpandingNotification || NotificationStackScrollLayoutController.this.mView.getDisallowScrollingInThisMotion()) ? false : NotificationStackScrollLayoutController.this.mView.onScrollTouch(motionEvent);
            boolean onTouchEvent = (NotificationStackScrollLayoutController.this.mView.isBeingDragged() || isExpandingNotification || NotificationStackScrollLayoutController.this.mView.getExpandedInThisMotion() || onlyScrollingInThisMotion || NotificationStackScrollLayoutController.this.mView.getDisallowDismissInThisMotion()) ? false : NotificationStackScrollLayoutController.this.mSwipeHelper.onTouchEvent(motionEvent);
            if (exposedGuts != null && !NotificationSwipeHelper.isTouchInView(motionEvent, exposedGuts) && (exposedGuts.getGutsContent() instanceof NotificationSnooze) && ((((NotificationSnooze) exposedGuts.getGutsContent()).isExpanded() && z2) || (!onTouchEvent && onScrollTouch))) {
                NotificationStackScrollLayoutController.this.checkSnoozeLeavebehind();
            }
            if (motionEvent.getActionMasked() == 1) {
                NotificationStackScrollLayoutController.this.mFalsingManager.isFalseTouch(11);
                NotificationStackScrollLayoutController.this.mView.setCheckForLeaveBehind(true);
            }
            traceJankOnTouchEvent(motionEvent.getActionMasked(), onScrollTouch);
            if (onTouchEvent || onScrollTouch || z) {
                return true;
            }
            return false;
        }

        private void traceJankOnTouchEvent(int i, boolean z) {
            if (i != 0) {
                if (i != 1) {
                    if (i == 3 && z) {
                        InteractionJankMonitor.getInstance().cancel(2);
                    }
                } else if (z && !NotificationStackScrollLayoutController.this.mView.isFlingAfterUpEvent()) {
                    InteractionJankMonitor.getInstance().end(2);
                }
            } else if (z) {
                InteractionJankMonitor.getInstance().begin(NotificationStackScrollLayoutController.this.mView, 2);
            }
        }
    }
}
