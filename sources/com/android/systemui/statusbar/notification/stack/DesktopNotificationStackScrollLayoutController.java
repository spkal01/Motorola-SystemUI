package com.android.systemui.statusbar.notification.stack;

import android.content.res.Resources;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.R$bool;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.RemoteInputController;
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
import com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper;
import com.android.systemui.statusbar.phone.HeadsUpAppearanceController;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.motorola.systemui.desktop.overwrites.statusbar.notification.DesktopHeadsUpController;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DesktopNotificationStackScrollLayoutController {
    /* access modifiers changed from: private */
    public final boolean mAllowLongPress;
    /* access modifiers changed from: private */
    public final ConfigurationController mConfigurationController;
    @VisibleForTesting
    final ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onDensityOrFontScaleChanged() {
            DesktopNotificationStackScrollLayoutController.this.updateShowEmptyShadeView();
            DesktopNotificationStackScrollLayoutController.this.mView.reinflateViews();
        }

        public void onOverlayChanged() {
            DesktopNotificationStackScrollLayoutController.this.updateShowEmptyShadeView();
            DesktopNotificationStackScrollLayoutController.this.mView.updateCornerRadius();
            DesktopNotificationStackScrollLayoutController.this.mView.updateBgColor();
            DesktopNotificationStackScrollLayoutController.this.mView.updateDecorViews();
            DesktopNotificationStackScrollLayoutController.this.mView.reinflateViews();
        }

        public void onUiModeChanged() {
            DesktopNotificationStackScrollLayoutController.this.mView.updateBgColor();
            DesktopNotificationStackScrollLayoutController.this.mView.updateDecorViews();
        }

        public void onThemeChanged() {
            DesktopNotificationStackScrollLayoutController.this.updateFooter();
        }
    };
    /* access modifiers changed from: private */
    public DesktopHeadsUpController mDesktopHeadsUpController;
    private final DynamicPrivacyController mDynamicPrivacyController;
    private final DynamicPrivacyController.Listener mDynamicPrivacyControllerListener = new C1684x606844(this);
    /* access modifiers changed from: private */
    public boolean mFadeNotificationsOnDismiss;
    private final FeatureFlags mFeatureFlags;
    private final ForegroundServiceDismissalFeatureController mFgFeatureController;
    private final ForegroundServiceSectionController mFgServicesSectionController;
    /* access modifiers changed from: private */
    public HeadsUpAppearanceController mHeadsUpAppearanceController;
    /* access modifiers changed from: private */
    public final HeadsUpManagerPhone mHeadsUpManager;
    private final IStatusBarService mIStatusBarService;
    private final LayoutInflater mLayoutInflater;
    private final NotificationLockscreenUserManager.UserChangedListener mLockscreenUserChangeListener = new NotificationLockscreenUserManager.UserChangedListener() {
        public void onUserChanged(int i) {
            DesktopNotificationStackScrollLayoutController.this.mView.updateSensitiveness(false, DesktopNotificationStackScrollLayoutController.this.mLockscreenUserManager.isAnyProfilePublicMode());
        }
    };
    /* access modifiers changed from: private */
    public final NotificationLockscreenUserManager mLockscreenUserManager;
    private final NotificationMenuRowPlugin.OnMenuEventListener mMenuEventListener = new NotificationMenuRowPlugin.OnMenuEventListener() {
        public void onMenuClicked(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem) {
            boolean unused = DesktopNotificationStackScrollLayoutController.this.mAllowLongPress;
        }

        public void onMenuReset(View view) {
            View translatingParentView = DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.getTranslatingParentView();
            if (translatingParentView != null && view == translatingParentView) {
                DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.clearExposedMenuView();
                DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.clearTranslatingParentView();
                if (view instanceof ExpandableNotificationRow) {
                    DesktopNotificationStackScrollLayoutController.this.mHeadsUpManager.setMenuShown(((ExpandableNotificationRow) view).getEntry(), false);
                }
            }
        }

        public void onMenuShown(View view) {
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                DesktopNotificationStackScrollLayoutController.this.mHeadsUpManager.setMenuShown(expandableNotificationRow.getEntry(), true);
                DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.onMenuShown(view);
                NotificationMenuRowPlugin provider = expandableNotificationRow.getProvider();
                if (provider.shouldShowGutsOnSnapOpen()) {
                    if (provider.menuItemToExposeOnSnap() != null) {
                        provider.getRevealAnimationOrigin();
                    } else {
                        Log.e("DesktopStackScrollerController", "Provider has shouldShowGutsOnSnapOpen, but provided no menu item in menuItemtoExposeOnSnap. Skipping.");
                    }
                    DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.resetExposedMenuView(false, true);
                }
            }
        }
    };
    private final NotifCollection mNotifCollection;
    private final NotifPipeline mNotifPipeline;
    private final NotificationSwipeHelper.NotificationCallback mNotificationCallback = new NotificationSwipeHelper.NotificationCallback() {
        public float getFalsingThresholdFactor() {
            return 1.0f;
        }

        public void onDismiss() {
        }

        public void onDragCancelled(View view) {
        }

        public void onSnooze(StatusBarNotification statusBarNotification, NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        }

        public float getTotalTranslationLength(View view) {
            return DesktopNotificationStackScrollLayoutController.this.mView.getTotalTranslationLength(view);
        }

        public boolean shouldDismissQuickly() {
            return DesktopNotificationStackScrollLayoutController.this.mView.isExpanded() && DesktopNotificationStackScrollLayoutController.this.mView.isFullyAwake();
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
            if (!DesktopNotificationStackScrollLayoutController.this.mView.getDismissAllInProgress()) {
                DesktopNotificationStackScrollLayoutController.this.mView.onSwipeEnd();
                if (view instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                    if (expandableNotificationRow.isHeadsUp()) {
                        DesktopNotificationStackScrollLayoutController.this.mHeadsUpManager.addSwipedOutNotification(expandableNotificationRow.getEntry().getSbn().getKey());
                    }
                    expandableNotificationRow.performDismiss(false);
                }
                DesktopNotificationStackScrollLayoutController.this.mView.addSwipedOutView(view);
            }
        }

        public boolean isAntiFalsingNeeded() {
            return DesktopNotificationStackScrollLayoutController.this.mView.onKeyguard();
        }

        public View getChildAtPosition(MotionEvent motionEvent) {
            ExpandableNotificationRow notificationParent;
            ExpandableView childAtPosition = DesktopNotificationStackScrollLayoutController.this.mView.getChildAtPosition(motionEvent.getX(), motionEvent.getY(), true, false);
            if (!(childAtPosition instanceof ExpandableNotificationRow) || (notificationParent = ((ExpandableNotificationRow) childAtPosition).getNotificationParent()) == null || !notificationParent.areChildrenExpanded()) {
                return childAtPosition;
            }
            return (notificationParent.areGutsExposed() || DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.getExposedMenuView() == notificationParent || (notificationParent.getAttachedChildren().size() == 1 && notificationParent.getEntry().isClearable())) ? notificationParent : childAtPosition;
        }

        public void onBeginDrag(View view) {
            DesktopNotificationStackScrollLayoutController.this.mView.onSwipeBegin(view);
        }

        public void onChildSnappedBack(View view, float f) {
            DesktopNotificationStackScrollLayoutController.this.mView.onSwipeEnd();
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                if (expandableNotificationRow.isPinned() && !canChildBeDismissed(expandableNotificationRow) && expandableNotificationRow.getEntry().getSbn().getNotification().fullScreenIntent == null) {
                    DesktopNotificationStackScrollLayoutController.this.mHeadsUpManager.removeNotification(expandableNotificationRow.getEntry().getSbn().getKey(), true);
                }
            }
        }

        public boolean updateSwipeProgress(View view, boolean z, float f) {
            return !DesktopNotificationStackScrollLayoutController.this.mFadeNotificationsOnDismiss;
        }

        public int getConstrainSwipeStartPosition() {
            NotificationMenuRowPlugin currentMenuRow = DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.getCurrentMenuRow();
            if (currentMenuRow != null) {
                return Math.abs(currentMenuRow.getMenuSnapTarget());
            }
            return 0;
        }

        public boolean canChildBeDismissed(View view) {
            return DesktopNotificationStackScrollLayout.canChildBeDismissed(view);
        }

        public boolean canChildBeDismissedInDirection(View view, boolean z) {
            return canChildBeDismissed(view);
        }
    };
    /* access modifiers changed from: private */
    public final NotificationEntryManager mNotificationEntryManager;
    private final NotificationListContainerImpl mNotificationListContainer = new NotificationListContainerImpl();
    /* access modifiers changed from: private */
    public final NotificationRoundnessManager mNotificationRoundnessManager;
    private final NotificationSwipeHelper.Builder mNotificationSwipeHelperBuilder;
    @VisibleForTesting
    final View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        public void onViewAttachedToWindow(View view) {
            DesktopNotificationStackScrollLayoutController.this.mConfigurationController.addCallback(DesktopNotificationStackScrollLayoutController.this.mConfigurationListener);
            DesktopNotificationStackScrollLayoutController.this.mZenModeController.addCallback(DesktopNotificationStackScrollLayoutController.this.mZenModeControllerCallback);
            DesktopNotificationStackScrollLayoutController.this.updateFooter();
        }

        public void onViewDetachedFromWindow(View view) {
            DesktopNotificationStackScrollLayoutController.this.mConfigurationController.removeCallback(DesktopNotificationStackScrollLayoutController.this.mConfigurationListener);
            DesktopNotificationStackScrollLayoutController.this.mZenModeController.removeCallback(DesktopNotificationStackScrollLayoutController.this.mZenModeControllerCallback);
        }
    };
    private final OnHeadsUpChangedListener mOnHeadsUpChangedListener = new OnHeadsUpChangedListener() {
        public void onHeadsUpPinnedModeChanged(boolean z) {
            DesktopNotificationStackScrollLayoutController.this.mView.setInHeadsUpPinnedMode(z);
        }

        public void onHeadsUpPinned(NotificationEntry notificationEntry) {
            DesktopNotificationStackScrollLayoutController.this.mNotificationRoundnessManager.updateView(notificationEntry.getRow(), false);
        }

        public void onHeadsUpUnPinned(NotificationEntry notificationEntry) {
            DesktopNotificationStackScrollLayoutController.this.mNotificationRoundnessManager.updateView(notificationEntry.getRow(), true);
        }

        public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
            DesktopNotificationStackScrollLayoutController.this.mHeadsUpManager.getAllEntries().count();
            DesktopNotificationStackScrollLayoutController.this.mHeadsUpManager.getTopEntry();
            DesktopNotificationStackScrollLayoutController.this.mNotificationRoundnessManager.updateView(notificationEntry.getRow(), false);
        }
    };
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final Resources mResources;
    private boolean mShowEmptyShadeView;
    private final SectionHeaderController mSilentHeaderController;
    private final StatusBar mStatusBar;
    /* access modifiers changed from: private */
    public NotificationSwipeHelper mSwipeHelper;
    /* access modifiers changed from: private */
    public DesktopNotificationStackScrollLayout mView;
    private final VisualStabilityManager mVisualStabilityManager;
    /* access modifiers changed from: private */
    public final ZenModeController mZenModeController;
    /* access modifiers changed from: private */
    public final ZenModeController.Callback mZenModeControllerCallback = new ZenModeController.Callback() {
        public void onZenChanged(int i) {
            DesktopNotificationStackScrollLayoutController.this.updateShowEmptyShadeView();
        }
    };

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        if (this.mView.isExpanded()) {
            this.mView.setAnimateBottomOnLayout(true);
        }
        this.mView.post(new C1690x60684a(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateFooter();
        updateSectionBoundaries("dynamic privacy changed");
    }

    public DesktopNotificationStackScrollLayoutController(boolean z, HeadsUpManagerPhone headsUpManagerPhone, NotificationRoundnessManager notificationRoundnessManager, DynamicPrivacyController dynamicPrivacyController, ConfigurationController configurationController, ZenModeController zenModeController, NotificationLockscreenUserManager notificationLockscreenUserManager, Resources resources, NotificationSwipeHelper.Builder builder, StatusBar statusBar, NotificationGroupManagerLegacy notificationGroupManagerLegacy, GroupExpansionManager groupExpansionManager, SectionHeaderController sectionHeaderController, FeatureFlags featureFlags, NotifPipeline notifPipeline, NotifCollection notifCollection, NotificationEntryManager notificationEntryManager, IStatusBarService iStatusBarService, ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController, ForegroundServiceSectionController foregroundServiceSectionController, LayoutInflater layoutInflater, NotificationRemoteInputManager notificationRemoteInputManager, DesktopHeadsUpController desktopHeadsUpController, VisualStabilityManager visualStabilityManager) {
        this.mAllowLongPress = z;
        this.mHeadsUpManager = headsUpManagerPhone;
        this.mNotificationRoundnessManager = notificationRoundnessManager;
        this.mDynamicPrivacyController = dynamicPrivacyController;
        this.mConfigurationController = configurationController;
        this.mZenModeController = zenModeController;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mResources = resources;
        this.mNotificationSwipeHelperBuilder = builder;
        this.mStatusBar = statusBar;
        GroupExpansionManager groupExpansionManager2 = groupExpansionManager;
        groupExpansionManager.registerGroupExpansionChangeListener(new C1686x606846(this));
        NotificationGroupManagerLegacy notificationGroupManagerLegacy2 = notificationGroupManagerLegacy;
        notificationGroupManagerLegacy.registerGroupChangeListener(new NotificationGroupManagerLegacy.OnGroupChangeListener() {
            public void onGroupCreatedFromChildren(NotificationGroupManagerLegacy.NotificationGroup notificationGroup) {
                DesktopNotificationStackScrollLayoutController.this.mNotificationEntryManager.updateNotifications("onGroupCreatedFromChildren");
            }

            public void onGroupsChanged() {
                DesktopNotificationStackScrollLayoutController.this.mNotificationEntryManager.updateNotifications("onGroupsChanged");
            }
        });
        this.mSilentHeaderController = sectionHeaderController;
        this.mFeatureFlags = featureFlags;
        this.mNotifPipeline = notifPipeline;
        this.mNotifCollection = notifCollection;
        this.mNotificationEntryManager = notificationEntryManager;
        this.mIStatusBarService = iStatusBarService;
        this.mFgFeatureController = foregroundServiceDismissalFeatureController;
        this.mFgServicesSectionController = foregroundServiceSectionController;
        this.mLayoutInflater = layoutInflater;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mDesktopHeadsUpController = desktopHeadsUpController;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(ExpandableNotificationRow expandableNotificationRow, boolean z) {
        this.mView.onGroupExpandChanged(expandableNotificationRow, z);
    }

    public void attach(DesktopNotificationStackScrollLayout desktopNotificationStackScrollLayout) {
        this.mView = desktopNotificationStackScrollLayout;
        desktopNotificationStackScrollLayout.setController(this);
        this.mView.setTouchHandler(new TouchHandler());
        this.mView.setStatusBar(this.mStatusBar);
        this.mView.setDismissAllAnimationListener(new C1687x606847(this));
        this.mView.setRemoteInputManager(this.mRemoteInputManager);
        if (this.mFgFeatureController.isForegroundServiceDismissalEnabled()) {
            this.mView.initializeForegroundServiceSection((ForegroundServiceDungeonView) this.mFgServicesSectionController.createView(this.mLayoutInflater));
        }
        this.mSwipeHelper = this.mNotificationSwipeHelperBuilder.setSwipeDirection(0).setNotificationCallback(this.mNotificationCallback).setOnMenuEventListener(this.mMenuEventListener).build();
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mNotifPipeline.addCollectionListener(new NotifCollectionListener() {
                public void onEntryUpdated(NotificationEntry notificationEntry) {
                    DesktopNotificationStackScrollLayoutController.this.mView.onEntryUpdated(notificationEntry);
                }
            });
        } else {
            this.mNotificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
                public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                    DesktopNotificationStackScrollLayoutController.this.mView.onEntryUpdated(notificationEntry);
                }
            });
        }
        DesktopNotificationStackScrollLayout desktopNotificationStackScrollLayout2 = this.mView;
        desktopNotificationStackScrollLayout2.initView(desktopNotificationStackScrollLayout2.getContext(), this.mSwipeHelper);
        this.mHeadsUpManager.addListener(this.mOnHeadsUpChangedListener);
        HeadsUpManagerPhone headsUpManagerPhone = this.mHeadsUpManager;
        DesktopNotificationStackScrollLayout desktopNotificationStackScrollLayout3 = this.mView;
        Objects.requireNonNull(desktopNotificationStackScrollLayout3);
        headsUpManagerPhone.setAnimationStateHandler(new C1688x606848(desktopNotificationStackScrollLayout3));
        this.mDynamicPrivacyController.addListener(this.mDynamicPrivacyControllerListener);
        this.mLockscreenUserManager.addUserChangedListener(this.mLockscreenUserChangeListener);
        this.mFadeNotificationsOnDismiss = this.mResources.getBoolean(R$bool.config_fadeNotificationsOnDismiss);
        NotificationRoundnessManager notificationRoundnessManager = this.mNotificationRoundnessManager;
        DesktopNotificationStackScrollLayout desktopNotificationStackScrollLayout4 = this.mView;
        Objects.requireNonNull(desktopNotificationStackScrollLayout4);
        notificationRoundnessManager.setOnRoundingChangedCallback(new C1689x606849(desktopNotificationStackScrollLayout4));
        DesktopNotificationStackScrollLayout desktopNotificationStackScrollLayout5 = this.mView;
        NotificationRoundnessManager notificationRoundnessManager2 = this.mNotificationRoundnessManager;
        Objects.requireNonNull(notificationRoundnessManager2);
        desktopNotificationStackScrollLayout5.addOnExpandedHeightChangedListener(new C1691x60684b(notificationRoundnessManager2));
        this.mVisualStabilityManager.setVisibilityLocationProvider(new C1685x606845(this));
        if (this.mView.isAttachedToWindow()) {
            this.mOnAttachStateChangeListener.onViewAttachedToWindow(this.mView);
        }
        this.mView.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        this.mSilentHeaderController.setOnClearAllClickListener(new C1683x606843(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$attach$3(View view) {
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

    public void setIntrinsicPadding(int i) {
        this.mView.setIntrinsicPadding(i);
    }

    public int getChildCount() {
        return this.mView.getChildCount();
    }

    public ExpandableView getChildAt(int i) {
        return (ExpandableView) this.mView.getChildAt(i);
    }

    public void updateShowEmptyShadeView() {
        boolean z = this.mView.getVisibleNotificationCount() == 0;
        this.mShowEmptyShadeView = z;
        this.mView.updateEmptyShadeView(z, this.mZenModeController.areNotificationsHiddenInShade());
    }

    public boolean isShowingEmptyShadeView() {
        return this.mShowEmptyShadeView;
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
                if (expandableNotificationRow.canViewBeDismissed() && DesktopNotificationStackScrollLayout.matchesSelection(expandableNotificationRow, i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public RemoteInputController.Delegate createDelegate() {
        return new RemoteInputController.Delegate() {
            public void lockScrollTo(NotificationEntry notificationEntry) {
            }

            public void setRemoteInputActive(NotificationEntry notificationEntry, boolean z) {
                DesktopNotificationStackScrollLayoutController.this.mHeadsUpManager.setRemoteInputActive(notificationEntry, z);
                DesktopNotificationStackScrollLayoutController.this.mDesktopHeadsUpController.setRemoteInputActive(notificationEntry, z);
                notificationEntry.notifyHeightChanged(true);
                DesktopNotificationStackScrollLayoutController.this.updateFooter();
            }

            public void requestDisallowLongPressAndDismiss() {
                DesktopNotificationStackScrollLayoutController.this.mView.requestDisallowLongPress();
                DesktopNotificationStackScrollLayoutController.this.mView.requestDisallowDismiss();
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

    public DesktopNotificationStackScrollLayout getView() {
        return this.mView;
    }

    /* access modifiers changed from: package-private */
    public NotificationRoundnessManager getNoticationRoundessManager() {
        return this.mNotificationRoundnessManager;
    }

    public NotificationListContainer getNotificationListContainer() {
        return this.mNotificationListContainer;
    }

    private DismissedByUserStats getDismissedByUserStats(NotificationEntry notificationEntry, int i) {
        return new DismissedByUserStats(3, 1, NotificationVisibility.obtain(notificationEntry.getKey(), notificationEntry.getRanking().getRank(), i, true, NotificationLogger.getNotificationLocation(notificationEntry)));
    }

    /* access modifiers changed from: package-private */
    public boolean hasActiveNotifications() {
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            return !this.mNotifPipeline.getShadeList().isEmpty();
        }
        return this.mNotificationEntryManager.hasActiveNotifications();
    }

    public void clearSilentNotifications() {
        this.mView.clearNotifications(2, true ^ hasActiveClearableNotifications(1));
    }

    /* access modifiers changed from: private */
    public void onAnimationEnd(List<ExpandableNotificationRow> list, int i) {
        if (!this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            for (ExpandableNotificationRow next : list) {
                if (DesktopNotificationStackScrollLayout.canChildBeDismissed(next)) {
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

    private class NotificationListContainerImpl implements NotificationListContainer {
        public boolean isInVisibleLocation(NotificationEntry notificationEntry) {
            return true;
        }

        public void setChildLocationsChangedListener(NotificationLogger.OnChildLocationsChangedListener onChildLocationsChangedListener) {
        }

        private NotificationListContainerImpl() {
        }

        public void setChildTransferInProgress(boolean z) {
            DesktopNotificationStackScrollLayoutController.this.mView.setChildTransferInProgress(z);
        }

        public void changeViewPosition(ExpandableView expandableView, int i) {
            DesktopNotificationStackScrollLayoutController.this.mView.changeViewPosition(expandableView, i);
        }

        public void notifyGroupChildAdded(ExpandableView expandableView) {
            DesktopNotificationStackScrollLayoutController.this.mView.notifyGroupChildAdded(expandableView);
        }

        public void notifyGroupChildRemoved(ExpandableView expandableView, ViewGroup viewGroup) {
            DesktopNotificationStackScrollLayoutController.this.mView.notifyGroupChildRemoved(expandableView, viewGroup);
        }

        public void generateAddAnimation(ExpandableView expandableView, boolean z) {
            DesktopNotificationStackScrollLayoutController.this.mView.generateAddAnimation(expandableView, z);
        }

        public void generateChildOrderChangedEvent() {
            DesktopNotificationStackScrollLayoutController.this.mView.generateChildOrderChangedEvent();
        }

        public int getContainerChildCount() {
            return DesktopNotificationStackScrollLayoutController.this.mView.getContainerChildCount();
        }

        public void setNotificationActivityStarter(NotificationActivityStarter notificationActivityStarter) {
            DesktopNotificationStackScrollLayoutController.this.mView.setNotificationActivityStarter(notificationActivityStarter);
        }

        public View getContainerChildAt(int i) {
            return DesktopNotificationStackScrollLayoutController.this.mView.getContainerChildAt(i);
        }

        public void removeContainerView(View view) {
            DesktopNotificationStackScrollLayoutController.this.mView.removeContainerView(view);
        }

        public void addContainerView(View view) {
            DesktopNotificationStackScrollLayoutController.this.mView.addContainerView(view);
        }

        public void addContainerViewAt(View view, int i) {
            DesktopNotificationStackScrollLayoutController.this.mView.addContainerViewAt(view, i);
        }

        public ViewGroup getViewParentForNotification(NotificationEntry notificationEntry) {
            return DesktopNotificationStackScrollLayoutController.this.mView.getViewParentForNotification(notificationEntry);
        }

        public void resetExposedMenuView(boolean z, boolean z2) {
            DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.resetExposedMenuView(z, z2);
        }

        public NotificationSwipeActionHelper getSwipeActionHelper() {
            return DesktopNotificationStackScrollLayoutController.this.mSwipeHelper;
        }

        public void cleanUpViewStateForEntry(NotificationEntry notificationEntry) {
            DesktopNotificationStackScrollLayoutController.this.mView.cleanUpViewStateForEntry(notificationEntry);
        }

        public boolean hasPulsingNotifications() {
            return DesktopNotificationStackScrollLayoutController.this.mView.hasPulsingNotifications();
        }

        public void onHeightChanged(ExpandableView expandableView, boolean z) {
            DesktopNotificationStackScrollLayoutController.this.mView.onChildHeightChanged(expandableView, z);
        }

        public void onReset(ExpandableView expandableView) {
            DesktopNotificationStackScrollLayoutController.this.mView.onChildHeightReset(expandableView);
        }

        public void bindRow(ExpandableNotificationRow expandableNotificationRow) {
            expandableNotificationRow.setHeadsUpAnimatingAwayListener(new C1692x26f0b17(this, expandableNotificationRow));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$bindRow$0(ExpandableNotificationRow expandableNotificationRow, Boolean bool) {
            DesktopNotificationStackScrollLayoutController.this.mNotificationRoundnessManager.updateView(expandableNotificationRow, false);
            DesktopNotificationStackScrollLayoutController.this.mHeadsUpAppearanceController.lambda$updateHeadsUpHeaders$3(expandableNotificationRow.getEntry());
        }

        public void applyExpandAnimationParams(ExpandAnimationParameters expandAnimationParameters) {
            DesktopNotificationStackScrollLayoutController.this.mView.applyExpandAnimationParams(expandAnimationParameters);
        }

        public void setExpandingNotification(ExpandableNotificationRow expandableNotificationRow) {
            DesktopNotificationStackScrollLayoutController.this.mView.setExpandingNotification(expandableNotificationRow);
        }

        public boolean containsView(View view) {
            return DesktopNotificationStackScrollLayoutController.this.mView.containsView(view);
        }
    }

    class TouchHandler implements Gefingerpoken {
        TouchHandler() {
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.onInterceptTouchEvent(motionEvent);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return DesktopNotificationStackScrollLayoutController.this.mSwipeHelper.onTouchEvent(motionEvent);
        }
    }
}
