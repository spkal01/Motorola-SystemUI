package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.view.View;
import android.view.ViewParent;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.ActionClickLogger;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.motorola.systemui.desktop.util.DesktopDisplayContext;
import java.util.Objects;
import java.util.concurrent.Executor;

public class StatusBarRemoteInputCallback implements NotificationRemoteInputManager.Callback, CommandQueue.Callbacks, StatusBarStateController.StateListener {
    private final ActionClickLogger mActionClickLogger;
    private final ActivityIntentHelper mActivityIntentHelper;
    private final ActivityStarter mActivityStarter;
    protected BroadcastReceiver mChallengeReceiver;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private int mDisabled2;
    private Executor mExecutor;
    private final GroupExpansionManager mGroupExpansionManager;
    private KeyguardManager mKeyguardManager;
    private final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public final NotificationLockscreenUserManager mLockscreenUserManager;
    private View mPendingRemoteInputView;
    private View mPendingWorkRemoteInputView;
    private final ShadeController mShadeController;
    private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final SysuiStatusBarStateController mStatusBarStateController;

    public StatusBarRemoteInputCallback(Context context, GroupExpansionManager groupExpansionManager, NotificationLockscreenUserManager notificationLockscreenUserManager, KeyguardStateController keyguardStateController, StatusBarStateController statusBarStateController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, ActivityStarter activityStarter, ShadeController shadeController, CommandQueue commandQueue, ActionClickLogger actionClickLogger, Executor executor) {
        CommandQueue commandQueue2 = commandQueue;
        ChallengeReceiver challengeReceiver = new ChallengeReceiver();
        this.mChallengeReceiver = challengeReceiver;
        this.mContext = context;
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
        this.mShadeController = shadeController;
        this.mExecutor = executor;
        context.registerReceiverAsUser(challengeReceiver, UserHandle.ALL, new IntentFilter("android.intent.action.DEVICE_LOCKED_CHANGED"), (String) null, (Handler) null);
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mKeyguardStateController = keyguardStateController;
        SysuiStatusBarStateController sysuiStatusBarStateController = (SysuiStatusBarStateController) statusBarStateController;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mActivityStarter = activityStarter;
        sysuiStatusBarStateController.addCallback(this);
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mCommandQueue = commandQueue2;
        commandQueue2.addCallback((CommandQueue.Callbacks) this);
        this.mActionClickLogger = actionClickLogger;
        this.mActivityIntentHelper = new ActivityIntentHelper(context);
        this.mGroupExpansionManager = groupExpansionManager;
    }

    public void onStateChanged(int i) {
        boolean z = this.mPendingRemoteInputView != null;
        if (i != 0) {
            return;
        }
        if ((this.mStatusBarStateController.leaveOpenOnKeyguardHide() || z) && !this.mStatusBarStateController.isKeyguardRequested() && this.mKeyguardStateController.isUnlocked()) {
            if (z) {
                Executor executor = this.mExecutor;
                View view = this.mPendingRemoteInputView;
                Objects.requireNonNull(view);
                executor.execute(new StatusBarRemoteInputCallback$$ExternalSyntheticLambda1(view));
            }
            this.mPendingRemoteInputView = null;
        }
    }

    public void onLockedRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view) {
        if (!expandableNotificationRow.isPinned()) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        }
        this.mStatusBarKeyguardViewManager.showGenericBouncer(true);
        this.mPendingRemoteInputView = view;
    }

    /* access modifiers changed from: protected */
    public void onWorkChallengeChanged() {
        this.mLockscreenUserManager.updatePublicMode();
        if (this.mPendingWorkRemoteInputView != null && !this.mLockscreenUserManager.isAnyProfilePublicMode()) {
            this.mShadeController.postOnShadeExpanded(new StatusBarRemoteInputCallback$$ExternalSyntheticLambda2(this));
            this.mShadeController.instantExpandNotificationsPanel();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onWorkChallengeChanged$2() {
        View view = this.mPendingWorkRemoteInputView;
        if (view != null) {
            ViewParent parent = view.getParent();
            while (!(parent instanceof ExpandableNotificationRow)) {
                if (parent != null) {
                    parent = parent.getParent();
                } else {
                    return;
                }
            }
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) parent;
            ViewParent parent2 = expandableNotificationRow.getParent();
            if (parent2 instanceof NotificationStackScrollLayout) {
                expandableNotificationRow.makeActionsVisibile();
                expandableNotificationRow.post(new StatusBarRemoteInputCallback$$ExternalSyntheticLambda4(this, (NotificationStackScrollLayout) parent2, expandableNotificationRow));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onWorkChallengeChanged$1(NotificationStackScrollLayout notificationStackScrollLayout, ExpandableNotificationRow expandableNotificationRow) {
        StatusBarRemoteInputCallback$$ExternalSyntheticLambda3 statusBarRemoteInputCallback$$ExternalSyntheticLambda3 = new StatusBarRemoteInputCallback$$ExternalSyntheticLambda3(this, notificationStackScrollLayout);
        if (notificationStackScrollLayout.scrollTo(expandableNotificationRow)) {
            notificationStackScrollLayout.setFinishScrollingCallback(statusBarRemoteInputCallback$$ExternalSyntheticLambda3);
        } else {
            statusBarRemoteInputCallback$$ExternalSyntheticLambda3.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onWorkChallengeChanged$0(NotificationStackScrollLayout notificationStackScrollLayout) {
        this.mPendingWorkRemoteInputView.callOnClick();
        this.mPendingWorkRemoteInputView = null;
        notificationStackScrollLayout.setFinishScrollingCallback((Runnable) null);
    }

    public void onMakeExpandedVisibleForRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view, boolean z, Runnable runnable) {
        if (z || !this.mKeyguardStateController.isShowing()) {
            if (expandableNotificationRow.isChildInGroup() && !expandableNotificationRow.areChildrenExpanded()) {
                this.mGroupExpansionManager.toggleGroupExpansion(expandableNotificationRow.getEntry());
            }
            expandableNotificationRow.setUserExpanded(true);
            expandableNotificationRow.getPrivateLayout().setOnExpandedVisibleListener(runnable);
            return;
        }
        onLockedRemoteInput(expandableNotificationRow, view);
    }

    public void onLockedWorkRemoteInput(int i, ExpandableNotificationRow expandableNotificationRow, View view) {
        this.mCommandQueue.animateCollapsePanels();
        startWorkChallengeIfNecessary(i, (IntentSender) null, (String) null);
        this.mPendingWorkRemoteInputView = view;
    }

    /* access modifiers changed from: package-private */
    public boolean startWorkChallengeIfNecessary(int i, IntentSender intentSender, String str) {
        this.mPendingWorkRemoteInputView = null;
        Intent createConfirmDeviceCredentialIntent = this.mKeyguardManager.createConfirmDeviceCredentialIntent((CharSequence) null, (CharSequence) null, i);
        if (createConfirmDeviceCredentialIntent == null) {
            return false;
        }
        Intent intent = new Intent("com.android.systemui.statusbar.work_challenge_unlocked_notification_action");
        intent.putExtra("android.intent.extra.INTENT", intentSender);
        intent.putExtra("android.intent.extra.INDEX", str);
        intent.setPackage(this.mContext.getPackageName());
        createConfirmDeviceCredentialIntent.putExtra("android.intent.extra.INTENT", PendingIntent.getBroadcast(this.mContext, 0, intent, 1409286144).getIntentSender());
        try {
            ActivityManager.getService().startConfirmDeviceCredentialIntent(createConfirmDeviceCredentialIntent, (Bundle) null);
            return true;
        } catch (RemoteException unused) {
            return true;
        }
    }

    public boolean shouldHandleRemoteInput(View view, PendingIntent pendingIntent) {
        return (this.mDisabled2 & 4) != 0;
    }

    public boolean handleRemoteViewClick(View view, PendingIntent pendingIntent, boolean z, NotificationRemoteInputManager.ClickHandler clickHandler) {
        if (!pendingIntent.isActivity() && !z) {
            return clickHandler.handleClick();
        }
        this.mActionClickLogger.logWaitingToCloseKeyguard(pendingIntent);
        this.mActivityStarter.dismissKeyguardThenExecute(new StatusBarRemoteInputCallback$$ExternalSyntheticLambda0(this, pendingIntent, clickHandler), (Runnable) null, this.mActivityIntentHelper.wouldLaunchResolverActivity(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId()));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$handleRemoteViewClick$3(PendingIntent pendingIntent, NotificationRemoteInputManager.ClickHandler clickHandler) {
        this.mActionClickLogger.logKeyguardGone(pendingIntent);
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException unused) {
        }
        boolean handleClick = clickHandler.handleClick();
        if (this.mContext instanceof DesktopDisplayContext) {
            return handleClick;
        }
        return handleClick && this.mShadeController.closeShadeIfOpen();
    }

    public void disable(int i, int i2, int i3, boolean z) {
        if (i == this.mContext.getDisplayId()) {
            this.mDisabled2 = i3;
        }
    }

    protected class ChallengeReceiver extends BroadcastReceiver {
        protected ChallengeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
            if ("android.intent.action.DEVICE_LOCKED_CHANGED".equals(action) && intExtra != StatusBarRemoteInputCallback.this.mLockscreenUserManager.getCurrentUserId() && StatusBarRemoteInputCallback.this.mLockscreenUserManager.isCurrentProfile(intExtra)) {
                StatusBarRemoteInputCallback.this.onWorkChallengeChanged();
            }
        }
    }
}
