package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.EventLog;
import android.view.RemoteAnimationAdapter;
import android.view.View;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationClickNotifier;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationLaunchAnimatorControllerProvider;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.render.GroupMembershipManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.OnUserInteractionCallback;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.wmshell.BubblesManager;
import com.motorola.internal.app.MotoDesktopManager;
import dagger.Lazy;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;

public class StatusBarNotificationActivityStarter implements NotificationActivityStarter {
    private final ActivityIntentHelper mActivityIntentHelper;
    /* access modifiers changed from: private */
    public final ActivityLaunchAnimator mActivityLaunchAnimator;
    private final ActivityStarter mActivityStarter;
    private final Lazy<AssistManager> mAssistManagerLazy;
    private final Optional<BubblesManager> mBubblesManagerOptional;
    private final NotificationClickNotifier mClickNotifier;
    private final CommandQueue mCommandQueue;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final IDreamManager mDreamManager;
    private final NotificationEntryManager mEntryManager;
    private final FeatureFlags mFeatureFlags;
    private final GroupMembershipManager mGroupMembershipManager;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private boolean mIsCollapsingToShowActivityOverLockscreen;
    private final KeyguardManager mKeyguardManager;
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    private final LockPatternUtils mLockPatternUtils;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final StatusBarNotificationActivityStarterLogger mLogger;
    private final Handler mMainThreadHandler;
    private final MetricsLogger mMetricsLogger;
    private final NotifPipeline mNotifPipeline;
    /* access modifiers changed from: private */
    public final NotificationLaunchAnimatorControllerProvider mNotificationAnimationProvider;
    private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final NotificationPanelViewController mNotificationPanel;
    private final OnUserInteractionCallback mOnUserInteractionCallback;
    private final NotificationPresenter mPresenter;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final ShadeController mShadeController;
    /* access modifiers changed from: private */
    public final StatusBar mStatusBar;
    private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final StatusBarRemoteInputCallback mStatusBarRemoteInputCallback;
    private final StatusBarStateController mStatusBarStateController;
    private final Executor mUiBgExecutor;

    private StatusBarNotificationActivityStarter(Context context, CommandQueue commandQueue, Handler handler, Executor executor, NotificationEntryManager notificationEntryManager, NotifPipeline notifPipeline, HeadsUpManagerPhone headsUpManagerPhone, ActivityStarter activityStarter, NotificationClickNotifier notificationClickNotifier, StatusBarStateController statusBarStateController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, KeyguardManager keyguardManager, IDreamManager iDreamManager, Optional<BubblesManager> optional, Lazy<AssistManager> lazy, NotificationRemoteInputManager notificationRemoteInputManager, GroupMembershipManager groupMembershipManager, NotificationLockscreenUserManager notificationLockscreenUserManager, ShadeController shadeController, KeyguardStateController keyguardStateController, NotificationInterruptStateProvider notificationInterruptStateProvider, LockPatternUtils lockPatternUtils, StatusBarRemoteInputCallback statusBarRemoteInputCallback, ActivityIntentHelper activityIntentHelper, FeatureFlags featureFlags, MetricsLogger metricsLogger, StatusBarNotificationActivityStarterLogger statusBarNotificationActivityStarterLogger, OnUserInteractionCallback onUserInteractionCallback, StatusBar statusBar, NotificationPresenter notificationPresenter, NotificationPanelViewController notificationPanelViewController, ActivityLaunchAnimator activityLaunchAnimator, NotificationLaunchAnimatorControllerProvider notificationLaunchAnimatorControllerProvider) {
        this.mContext = context;
        this.mCommandQueue = commandQueue;
        this.mMainThreadHandler = handler;
        this.mUiBgExecutor = executor;
        this.mEntryManager = notificationEntryManager;
        this.mNotifPipeline = notifPipeline;
        this.mHeadsUpManager = headsUpManagerPhone;
        this.mActivityStarter = activityStarter;
        this.mClickNotifier = notificationClickNotifier;
        this.mStatusBarStateController = statusBarStateController;
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
        this.mKeyguardManager = keyguardManager;
        this.mDreamManager = iDreamManager;
        this.mBubblesManagerOptional = optional;
        this.mAssistManagerLazy = lazy;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mGroupMembershipManager = groupMembershipManager;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mShadeController = shadeController;
        this.mKeyguardStateController = keyguardStateController;
        this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
        this.mLockPatternUtils = lockPatternUtils;
        this.mStatusBarRemoteInputCallback = statusBarRemoteInputCallback;
        this.mActivityIntentHelper = activityIntentHelper;
        this.mFeatureFlags = featureFlags;
        this.mMetricsLogger = metricsLogger;
        this.mLogger = statusBarNotificationActivityStarterLogger;
        this.mOnUserInteractionCallback = onUserInteractionCallback;
        this.mStatusBar = statusBar;
        this.mPresenter = notificationPresenter;
        this.mNotificationPanel = notificationPanelViewController;
        this.mActivityLaunchAnimator = activityLaunchAnimator;
        this.mNotificationAnimationProvider = notificationLaunchAnimatorControllerProvider;
        if (!featureFlags.isNewNotifPipelineRenderingEnabled()) {
            notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
                public void onPendingEntryAdded(NotificationEntry notificationEntry) {
                    StatusBarNotificationActivityStarter.this.handleFullScreenIntent(notificationEntry);
                }

                public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                    if (StatusBarNotificationActivityStarter.this.mKeyguardStateController.isShowing() && !StatusBarNotificationActivityStarter.this.mKeyguardStateController.isOccluded()) {
                        StatusBarNotificationActivityStarter.this.handleFullScreenIntent(notificationEntry);
                    }
                }
            });
        } else {
            notifPipeline.addCollectionListener(new NotifCollectionListener() {
                public void onEntryAdded(NotificationEntry notificationEntry) {
                    StatusBarNotificationActivityStarter.this.handleFullScreenIntent(notificationEntry);
                }
            });
        }
    }

    public void onNotificationClicked(StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow) {
        final PendingIntent pendingIntent;
        this.mLogger.logStartingActivityFromClick(statusBarNotification.getKey());
        final NotificationEntry entry = expandableNotificationRow.getEntry();
        final RemoteInputController controller = this.mRemoteInputManager.getController();
        if (!controller.isRemoteInputActive(entry) || TextUtils.isEmpty(expandableNotificationRow.getActiveRemoteInputText())) {
            Notification notification = statusBarNotification.getNotification();
            PendingIntent pendingIntent2 = notification.contentIntent;
            if (pendingIntent2 != null) {
                pendingIntent = pendingIntent2;
            } else {
                pendingIntent = notification.fullScreenIntent;
            }
            boolean z = false;
            boolean z2 = entry.isBubble() && !expandableNotificationRow.isCliRow();
            if (pendingIntent != null || z2) {
                final boolean z3 = pendingIntent != null && pendingIntent.isActivity() && !z2;
                boolean z4 = z3 && this.mActivityIntentHelper.wouldLaunchResolverActivity(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId());
                final boolean z5 = !z4 && this.mStatusBar.shouldAnimateLaunch(z3);
                if (this.mKeyguardStateController.isShowing() && pendingIntent != null && this.mActivityIntentHelper.wouldShowOverLockscreen(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId())) {
                    z = true;
                }
                final ExpandableNotificationRow expandableNotificationRow2 = expandableNotificationRow;
                final boolean z6 = z;
                C19503 r2 = new ActivityStarter.OnDismissAction() {
                    public boolean onDismiss() {
                        return StatusBarNotificationActivityStarter.this.handleNotificationClickAfterKeyguardDismissed(entry, expandableNotificationRow2, controller, pendingIntent, z3, z5, z6);
                    }

                    public boolean willRunAnimationOnKeyguard() {
                        return z5;
                    }
                };
                if (z) {
                    this.mIsCollapsingToShowActivityOverLockscreen = true;
                    r2.onDismiss();
                    return;
                }
                this.mActivityStarter.dismissKeyguardThenExecute(r2, (Runnable) null, z4);
                return;
            }
            this.mLogger.logNonClickableNotification(statusBarNotification.getKey());
            return;
        }
        controller.closeRemoteInputs();
    }

    /* access modifiers changed from: private */
    public boolean handleNotificationClickAfterKeyguardDismissed(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, RemoteInputController remoteInputController, PendingIntent pendingIntent, boolean z, boolean z2, boolean z3) {
        this.mLogger.logHandleClickAfterKeyguardDismissed(notificationEntry.getKey());
        StatusBarNotificationActivityStarter$$ExternalSyntheticLambda5 statusBarNotificationActivityStarter$$ExternalSyntheticLambda5 = new StatusBarNotificationActivityStarter$$ExternalSyntheticLambda5(this, notificationEntry, expandableNotificationRow, remoteInputController, pendingIntent, z, z2);
        if (z3) {
            this.mShadeController.addPostCollapseAction(statusBarNotificationActivityStarter$$ExternalSyntheticLambda5);
            this.mShadeController.collapsePanel(true);
        } else if (!this.mKeyguardStateController.isShowing() || !this.mStatusBar.isOccluded()) {
            statusBarNotificationActivityStarter$$ExternalSyntheticLambda5.run();
        } else {
            this.mStatusBarKeyguardViewManager.addAfterKeyguardGoneRunnable(statusBarNotificationActivityStarter$$ExternalSyntheticLambda5);
            this.mShadeController.collapsePanel();
        }
        this.mShadeController.collapseCliStack();
        if (z2 || !this.mNotificationPanel.isFullyCollapsed()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: handleNotificationClickAfterPanelCollapsed */
    public void lambda$handleNotificationClickAfterKeyguardDismissed$0(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, RemoteInputController remoteInputController, PendingIntent pendingIntent, boolean z, boolean z2) {
        NotificationEntry notificationEntry2 = notificationEntry;
        ExpandableNotificationRow expandableNotificationRow2 = expandableNotificationRow;
        String key = notificationEntry.getKey();
        this.mLogger.logHandleClickAfterPanelCollapsed(key);
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException unused) {
        }
        if (z) {
            int identifier = pendingIntent.getCreatorUserHandle().getIdentifier();
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(identifier) && this.mKeyguardManager.isDeviceLocked(identifier) && this.mStatusBarRemoteInputCallback.startWorkChallengeIfNecessary(identifier, pendingIntent.getIntentSender(), key)) {
                removeHunAfterClick(expandableNotificationRow2);
                collapseOnMainThread();
                return;
            }
        }
        NotificationEntry notificationEntry3 = null;
        CharSequence charSequence = !TextUtils.isEmpty(notificationEntry2.remoteInputText) ? notificationEntry2.remoteInputText : null;
        Intent putExtra = (TextUtils.isEmpty(charSequence) || remoteInputController.isSpinning(key)) ? null : new Intent().putExtra("android.remoteInputDraft", charSequence.toString());
        boolean z3 = notificationEntry.canBubble() && !expandableNotificationRow.isCliRow();
        if (z3) {
            this.mLogger.logExpandingBubble(key);
            removeHunAfterClick(expandableNotificationRow2);
            expandBubbleStackOnMainThread(notificationEntry);
        } else {
            startNotificationIntent(pendingIntent, putExtra, notificationEntry, expandableNotificationRow, z2, z);
        }
        if (z || z3) {
            this.mAssistManagerLazy.get().hideAssist();
        }
        NotificationVisibility obtain = NotificationVisibility.obtain(notificationEntry.getKey(), notificationEntry.getRanking().getRank(), getVisibleNotificationsCount(), true, NotificationLogger.getNotificationLocation(notificationEntry));
        boolean shouldAutoCancel = shouldAutoCancel(notificationEntry.getSbn());
        if (shouldAutoCancel) {
            notificationEntry3 = this.mOnUserInteractionCallback.getGroupSummaryToDismiss(notificationEntry);
        }
        this.mClickNotifier.onNotificationClick(key, obtain);
        if (!z3 && (shouldAutoCancel || this.mRemoteInputManager.isNotificationKeptForRemoteInputHistory(key))) {
            this.mMainThreadHandler.post(new StatusBarNotificationActivityStarter$$ExternalSyntheticLambda4(this, notificationEntry, notificationEntry3));
        }
        this.mIsCollapsingToShowActivityOverLockscreen = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleNotificationClickAfterPanelCollapsed$2(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        StatusBarNotificationActivityStarter$$ExternalSyntheticLambda3 statusBarNotificationActivityStarter$$ExternalSyntheticLambda3 = new StatusBarNotificationActivityStarter$$ExternalSyntheticLambda3(this, notificationEntry, notificationEntry2);
        if (this.mPresenter.isCollapsing()) {
            this.mShadeController.addPostCollapseAction(statusBarNotificationActivityStarter$$ExternalSyntheticLambda3);
        } else {
            statusBarNotificationActivityStarter$$ExternalSyntheticLambda3.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleNotificationClickAfterPanelCollapsed$1(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        this.mOnUserInteractionCallback.onDismiss(notificationEntry, 1, notificationEntry2);
    }

    private void expandBubbleStackOnMainThread(NotificationEntry notificationEntry) {
        if (this.mBubblesManagerOptional.isPresent()) {
            if (Looper.getMainLooper().isCurrentThread()) {
                lambda$expandBubbleStackOnMainThread$3(notificationEntry);
            } else {
                this.mMainThreadHandler.post(new StatusBarNotificationActivityStarter$$ExternalSyntheticLambda2(this, notificationEntry));
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: expandBubbleStack */
    public void lambda$expandBubbleStackOnMainThread$3(NotificationEntry notificationEntry) {
        this.mBubblesManagerOptional.get().expandStackAndSelectBubble(notificationEntry);
        this.mShadeController.collapsePanel();
    }

    private void startNotificationIntent(PendingIntent pendingIntent, Intent intent, NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, boolean z, boolean z2) {
        this.mLogger.logStartNotificationIntent(notificationEntry.getKey(), pendingIntent);
        try {
            this.mActivityLaunchAnimator.startPendingIntentWithAnimation(new StatusBarLaunchAnimatorController(this.mNotificationAnimationProvider.getAnimatorController(expandableNotificationRow), this.mStatusBar, z2), z, pendingIntent.getCreatorPackage(), new StatusBarNotificationActivityStarter$$ExternalSyntheticLambda0(this, expandableNotificationRow, pendingIntent, intent));
        } catch (PendingIntent.CanceledException e) {
            this.mLogger.logSendingIntentFailed(e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ int lambda$startNotificationIntent$4(ExpandableNotificationRow expandableNotificationRow, PendingIntent pendingIntent, Intent intent, RemoteAnimationAdapter remoteAnimationAdapter) throws PendingIntent.CanceledException {
        Bundle bundle;
        long andResetLastActionUpTime = expandableNotificationRow.getAndResetLastActionUpTime();
        if (andResetLastActionUpTime > 0) {
            bundle = StatusBar.getActivityOptions(this.mStatusBar.getDisplayId(), remoteAnimationAdapter, this.mKeyguardStateController.isShowing(), andResetLastActionUpTime);
        } else {
            bundle = StatusBar.getActivityOptions(this.mStatusBar.getDisplayId(), remoteAnimationAdapter);
        }
        return pendingIntent.sendAndReturnResult(this.mContext, 0, intent, (PendingIntent.OnFinished) null, (Handler) null, (String) null, bundle);
    }

    public void startNotificationGutsIntent(Intent intent, int i, ExpandableNotificationRow expandableNotificationRow) {
        final boolean shouldAnimateLaunch = this.mStatusBar.shouldAnimateLaunch(true);
        final ExpandableNotificationRow expandableNotificationRow2 = expandableNotificationRow;
        final Intent intent2 = intent;
        final int i2 = i;
        this.mActivityStarter.dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() {
            public boolean onDismiss() {
                AsyncTask.execute(new StatusBarNotificationActivityStarter$4$$ExternalSyntheticLambda0(this, expandableNotificationRow2, shouldAnimateLaunch, intent2, i2));
                return true;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onDismiss$1(ExpandableNotificationRow expandableNotificationRow, boolean z, Intent intent, int i) {
                StatusBarNotificationActivityStarter.this.mActivityLaunchAnimator.startIntentWithAnimation(new StatusBarLaunchAnimatorController(StatusBarNotificationActivityStarter.this.mNotificationAnimationProvider.getAnimatorController(expandableNotificationRow), StatusBarNotificationActivityStarter.this.mStatusBar, true), z, intent.getPackage(), new StatusBarNotificationActivityStarter$4$$ExternalSyntheticLambda1(this, intent, i));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ Integer lambda$onDismiss$0(Intent intent, int i, RemoteAnimationAdapter remoteAnimationAdapter) {
                return Integer.valueOf(TaskStackBuilder.create(StatusBarNotificationActivityStarter.this.mContext).addNextIntentWithParentStack(intent).startActivities(StatusBar.getActivityOptions(StatusBarNotificationActivityStarter.this.mStatusBar.getDisplayId(), remoteAnimationAdapter), new UserHandle(UserHandle.getUserId(i))));
            }

            public boolean willRunAnimationOnKeyguard() {
                return shouldAnimateLaunch;
            }
        }, (Runnable) null, false);
    }

    public void startHistoryIntent(final View view, final boolean z) {
        final boolean shouldAnimateLaunch = this.mStatusBar.shouldAnimateLaunch(true);
        this.mActivityStarter.dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() {
            public boolean onDismiss() {
                AsyncTask.execute(new StatusBarNotificationActivityStarter$5$$ExternalSyntheticLambda0(this, z, view, shouldAnimateLaunch));
                return true;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onDismiss$1(boolean z, View view, boolean z2) {
                Intent intent;
                StatusBarLaunchAnimatorController statusBarLaunchAnimatorController;
                if (z) {
                    intent = new Intent("android.settings.NOTIFICATION_HISTORY");
                } else {
                    intent = new Intent("android.settings.NOTIFICATION_SETTINGS");
                }
                TaskStackBuilder addNextIntent = TaskStackBuilder.create(StatusBarNotificationActivityStarter.this.mContext).addNextIntent(new Intent("android.settings.NOTIFICATION_SETTINGS"));
                if (z) {
                    addNextIntent.addNextIntent(intent);
                }
                ActivityLaunchAnimator.Controller fromView = ActivityLaunchAnimator.Controller.fromView(view, 30);
                if (fromView == null) {
                    statusBarLaunchAnimatorController = null;
                } else {
                    statusBarLaunchAnimatorController = new StatusBarLaunchAnimatorController(fromView, StatusBarNotificationActivityStarter.this.mStatusBar, true);
                }
                StatusBarNotificationActivityStarter.this.mActivityLaunchAnimator.startIntentWithAnimation(statusBarLaunchAnimatorController, z2, intent.getPackage(), new StatusBarNotificationActivityStarter$5$$ExternalSyntheticLambda1(this, addNextIntent));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ Integer lambda$onDismiss$0(TaskStackBuilder taskStackBuilder, RemoteAnimationAdapter remoteAnimationAdapter) {
                return Integer.valueOf(taskStackBuilder.startActivities(StatusBar.getActivityOptions(StatusBarNotificationActivityStarter.this.mStatusBar.getDisplayId(), remoteAnimationAdapter), UserHandle.CURRENT));
            }

            public boolean willRunAnimationOnKeyguard() {
                return shouldAnimateLaunch;
            }
        }, (Runnable) null, false);
    }

    private void removeHunAfterClick(ExpandableNotificationRow expandableNotificationRow) {
        String key = expandableNotificationRow.getEntry().getSbn().getKey();
        HeadsUpManagerPhone headsUpManagerPhone = this.mHeadsUpManager;
        if (headsUpManagerPhone != null && headsUpManagerPhone.isAlerting(key)) {
            if (this.mPresenter.isPresenterFullyCollapsed()) {
                HeadsUpUtil.setNeedsHeadsUpDisappearAnimationAfterClick(expandableNotificationRow, true);
            }
            this.mHeadsUpManager.removeNotification(key, true);
        }
    }

    /* access modifiers changed from: private */
    public void handleFullScreenIntent(NotificationEntry notificationEntry) {
        if (!this.mNotificationInterruptStateProvider.shouldLaunchFullScreenIntentWhenAdded(notificationEntry)) {
            return;
        }
        if (shouldSuppressFullScreenIntent(notificationEntry)) {
            this.mLogger.logFullScreenIntentSuppressedByDnD(notificationEntry.getKey());
        } else if (notificationEntry.getImportance() < 4) {
            this.mLogger.logFullScreenIntentNotImportantEnough(notificationEntry.getKey());
        } else {
            this.mUiBgExecutor.execute(new StatusBarNotificationActivityStarter$$ExternalSyntheticLambda1(this));
            PendingIntent pendingIntent = notificationEntry.getSbn().getNotification().fullScreenIntent;
            this.mLogger.logSendingFullScreenIntent(notificationEntry.getKey(), pendingIntent);
            try {
                EventLog.writeEvent(36002, notificationEntry.getKey());
                if (MotoDesktopManager.isDesktopSupported()) {
                    ActivityOptions makeBasic = ActivityOptions.makeBasic();
                    makeBasic.setFullscreenIntentFromStatusBar(this.mContext, true);
                    pendingIntent.send((Context) null, 0, (Intent) null, (PendingIntent.OnFinished) null, (Handler) null, (String) null, makeBasic.toBundle());
                } else {
                    pendingIntent.send();
                }
                notificationEntry.notifyFullScreenIntentLaunched();
                this.mMetricsLogger.count("note_fullscreen", 1);
            } catch (PendingIntent.CanceledException unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleFullScreenIntent$5() {
        try {
            this.mDreamManager.awaken();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isCollapsingToShowActivityOverLockscreen() {
        return this.mIsCollapsingToShowActivityOverLockscreen;
    }

    private static boolean shouldAutoCancel(StatusBarNotification statusBarNotification) {
        int i = statusBarNotification.getNotification().flags;
        return (i & 16) == 16 && (i & 64) == 0;
    }

    private void collapseOnMainThread() {
        if (Looper.getMainLooper().isCurrentThread()) {
            this.mShadeController.collapsePanel();
            return;
        }
        Handler handler = this.mMainThreadHandler;
        ShadeController shadeController = this.mShadeController;
        Objects.requireNonNull(shadeController);
        handler.post(new StatusBar$$ExternalSyntheticLambda14(shadeController));
    }

    private boolean shouldSuppressFullScreenIntent(NotificationEntry notificationEntry) {
        if (this.mPresenter.isDeviceInVrMode()) {
            return true;
        }
        return notificationEntry.shouldSuppressFullScreenIntent();
    }

    private int getVisibleNotificationsCount() {
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            return this.mNotifPipeline.getShadeListCount();
        }
        return this.mEntryManager.getActiveNotificationsCount();
    }

    public static class Builder {
        private final ActivityIntentHelper mActivityIntentHelper;
        private ActivityLaunchAnimator mActivityLaunchAnimator;
        private final ActivityStarter mActivityStarter;
        private final Lazy<AssistManager> mAssistManagerLazy;
        private final Optional<BubblesManager> mBubblesManagerOptional;
        private final NotificationClickNotifier mClickNotifier;
        private final CommandQueue mCommandQueue;
        private final Context mContext;
        private final IDreamManager mDreamManager;
        private final NotificationEntryManager mEntryManager;
        private final FeatureFlags mFeatureFlags;
        private final GroupMembershipManager mGroupMembershipManager;
        private final HeadsUpManagerPhone mHeadsUpManager;
        private final KeyguardManager mKeyguardManager;
        private final KeyguardStateController mKeyguardStateController;
        private final LockPatternUtils mLockPatternUtils;
        private final NotificationLockscreenUserManager mLockscreenUserManager;
        private final StatusBarNotificationActivityStarterLogger mLogger;
        private final Handler mMainThreadHandler;
        private final MetricsLogger mMetricsLogger;
        private final NotifPipeline mNotifPipeline;
        private NotificationLaunchAnimatorControllerProvider mNotificationAnimationProvider;
        private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
        private NotificationPanelViewController mNotificationPanelViewController;
        private NotificationPresenter mNotificationPresenter;
        private final OnUserInteractionCallback mOnUserInteractionCallback;
        private final StatusBarRemoteInputCallback mRemoteInputCallback;
        private final NotificationRemoteInputManager mRemoteInputManager;
        private final ShadeController mShadeController;
        private StatusBar mStatusBar;
        private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
        private final StatusBarStateController mStatusBarStateController;
        private final Executor mUiBgExecutor;

        public Builder(Context context, CommandQueue commandQueue, Handler handler, Executor executor, NotificationEntryManager notificationEntryManager, NotifPipeline notifPipeline, HeadsUpManagerPhone headsUpManagerPhone, ActivityStarter activityStarter, NotificationClickNotifier notificationClickNotifier, StatusBarStateController statusBarStateController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, KeyguardManager keyguardManager, IDreamManager iDreamManager, Optional<BubblesManager> optional, Lazy<AssistManager> lazy, NotificationRemoteInputManager notificationRemoteInputManager, GroupMembershipManager groupMembershipManager, NotificationLockscreenUserManager notificationLockscreenUserManager, ShadeController shadeController, KeyguardStateController keyguardStateController, NotificationInterruptStateProvider notificationInterruptStateProvider, LockPatternUtils lockPatternUtils, StatusBarRemoteInputCallback statusBarRemoteInputCallback, ActivityIntentHelper activityIntentHelper, FeatureFlags featureFlags, MetricsLogger metricsLogger, StatusBarNotificationActivityStarterLogger statusBarNotificationActivityStarterLogger, OnUserInteractionCallback onUserInteractionCallback) {
            this.mContext = context;
            this.mCommandQueue = commandQueue;
            this.mMainThreadHandler = handler;
            this.mUiBgExecutor = executor;
            this.mEntryManager = notificationEntryManager;
            this.mNotifPipeline = notifPipeline;
            this.mHeadsUpManager = headsUpManagerPhone;
            this.mActivityStarter = activityStarter;
            this.mClickNotifier = notificationClickNotifier;
            this.mStatusBarStateController = statusBarStateController;
            this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
            this.mKeyguardManager = keyguardManager;
            this.mDreamManager = iDreamManager;
            this.mBubblesManagerOptional = optional;
            this.mAssistManagerLazy = lazy;
            this.mRemoteInputManager = notificationRemoteInputManager;
            this.mGroupMembershipManager = groupMembershipManager;
            this.mLockscreenUserManager = notificationLockscreenUserManager;
            this.mShadeController = shadeController;
            this.mKeyguardStateController = keyguardStateController;
            this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
            this.mLockPatternUtils = lockPatternUtils;
            this.mRemoteInputCallback = statusBarRemoteInputCallback;
            this.mActivityIntentHelper = activityIntentHelper;
            this.mFeatureFlags = featureFlags;
            this.mMetricsLogger = metricsLogger;
            this.mLogger = statusBarNotificationActivityStarterLogger;
            this.mOnUserInteractionCallback = onUserInteractionCallback;
        }

        public Builder setStatusBar(StatusBar statusBar) {
            this.mStatusBar = statusBar;
            return this;
        }

        public Builder setNotificationPresenter(NotificationPresenter notificationPresenter) {
            this.mNotificationPresenter = notificationPresenter;
            return this;
        }

        public Builder setActivityLaunchAnimator(ActivityLaunchAnimator activityLaunchAnimator) {
            this.mActivityLaunchAnimator = activityLaunchAnimator;
            return this;
        }

        public Builder setNotificationAnimatorControllerProvider(NotificationLaunchAnimatorControllerProvider notificationLaunchAnimatorControllerProvider) {
            this.mNotificationAnimationProvider = notificationLaunchAnimatorControllerProvider;
            return this;
        }

        public Builder setNotificationPanelViewController(NotificationPanelViewController notificationPanelViewController) {
            this.mNotificationPanelViewController = notificationPanelViewController;
            return this;
        }

        public StatusBarNotificationActivityStarter build() {
            return new StatusBarNotificationActivityStarter(this.mContext, this.mCommandQueue, this.mMainThreadHandler, this.mUiBgExecutor, this.mEntryManager, this.mNotifPipeline, this.mHeadsUpManager, this.mActivityStarter, this.mClickNotifier, this.mStatusBarStateController, this.mStatusBarKeyguardViewManager, this.mKeyguardManager, this.mDreamManager, this.mBubblesManagerOptional, this.mAssistManagerLazy, this.mRemoteInputManager, this.mGroupMembershipManager, this.mLockscreenUserManager, this.mShadeController, this.mKeyguardStateController, this.mNotificationInterruptStateProvider, this.mLockPatternUtils, this.mRemoteInputCallback, this.mActivityIntentHelper, this.mFeatureFlags, this.mMetricsLogger, this.mLogger, this.mOnUserInteractionCallback, this.mStatusBar, this.mNotificationPresenter, this.mNotificationPanelViewController, this.mActivityLaunchAnimator, this.mNotificationAnimationProvider);
        }
    }
}
