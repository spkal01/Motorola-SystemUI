package com.motorola.systemui.desktop.overwrites.statusbar.phone;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationClickNotifier;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.OnUserInteractionCallback;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger;
import com.motorola.systemui.desktop.util.FakeDesktopDisplayContext;

public class DesktopNotificationActivityStarter implements NotificationActivityStarter {
    private final NotificationClickNotifier mClickNotifier;
    private final Context mContext;
    private final NotificationEntryManager mEntryManager;
    private final FeatureFlags mFeatureFlags;
    private final KeyguardManager mKeyguardManager;
    private final LockPatternUtils mLockPatternUtils;
    private final StatusBarNotificationActivityStarterLogger mLogger;
    private final Handler mMainThreadHandler;
    private final NotifPipeline mNotifPipeline;
    private final OnUserInteractionCallback mOnUserInteractionCallback;
    private final NotificationRemoteInputManager mRemoteInputManager;

    public boolean isCollapsingToShowActivityOverLockscreen() {
        return false;
    }

    public void startNotificationGutsIntent(Intent intent, int i, ExpandableNotificationRow expandableNotificationRow) {
    }

    public DesktopNotificationActivityStarter(Context context, Handler handler, FeatureFlags featureFlags, LockPatternUtils lockPatternUtils, NotificationEntryManager notificationEntryManager, NotificationClickNotifier notificationClickNotifier, KeyguardManager keyguardManager, NotifPipeline notifPipeline, NotificationRemoteInputManager notificationRemoteInputManager, OnUserInteractionCallback onUserInteractionCallback, StatusBarNotificationActivityStarterLogger statusBarNotificationActivityStarterLogger) {
        this.mContext = context;
        this.mMainThreadHandler = handler;
        this.mFeatureFlags = featureFlags;
        this.mLockPatternUtils = lockPatternUtils;
        this.mEntryManager = notificationEntryManager;
        this.mClickNotifier = notificationClickNotifier;
        this.mKeyguardManager = keyguardManager;
        this.mNotifPipeline = notifPipeline;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mLogger = statusBarNotificationActivityStarterLogger;
        this.mOnUserInteractionCallback = onUserInteractionCallback;
    }

    public void onNotificationClicked(StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow) {
        PendingIntent pendingIntent;
        this.mLogger.logStartingActivityFromClick(statusBarNotification.getKey());
        NotificationEntry entry = expandableNotificationRow.getEntry();
        RemoteInputController controller = this.mRemoteInputManager.getController();
        if (!controller.isRemoteInputActive(entry) || TextUtils.isEmpty(expandableNotificationRow.getActiveRemoteInputText())) {
            Notification notification = statusBarNotification.getNotification();
            PendingIntent pendingIntent2 = notification.contentIntent;
            if (pendingIntent2 != null) {
                pendingIntent = pendingIntent2;
            } else {
                pendingIntent = notification.fullScreenIntent;
            }
            boolean isBubble = entry.isBubble();
            if (pendingIntent != null || isBubble) {
                new DesktopNotificationActivityStarter$$ExternalSyntheticLambda0(this, entry, expandableNotificationRow, controller, pendingIntent, pendingIntent != null && pendingIntent.isActivity() && !isBubble, false).onDismiss();
            } else {
                this.mLogger.logNonClickableNotification(statusBarNotification.getKey());
            }
        } else {
            controller.closeRemoteInputs();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onNotificationClicked$0(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, RemoteInputController remoteInputController, PendingIntent pendingIntent, boolean z, boolean z2) {
        return handleNotificationClickAfterKeyguardDismissed(notificationEntry, expandableNotificationRow, remoteInputController, pendingIntent, z, false, z2);
    }

    public void startHistoryIntent(View view, boolean z) {
        Intent intent;
        if (z) {
            intent = new Intent("android.settings.NOTIFICATION_HISTORY");
        } else {
            intent = new Intent("android.settings.NOTIFICATION_SETTINGS");
        }
        TaskStackBuilder addNextIntent = TaskStackBuilder.create(this.mContext).addNextIntent(new Intent("android.settings.NOTIFICATION_SETTINGS"));
        if (z) {
            addNextIntent.addNextIntent(intent);
        }
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        Context context = this.mContext;
        if (!(context instanceof FakeDesktopDisplayContext)) {
            makeBasic.setLaunchDisplayId(context.getDisplayId());
        }
        addNextIntent.startActivities(makeBasic.toBundle(), UserHandle.CURRENT);
    }

    private boolean handleNotificationClickAfterKeyguardDismissed(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, RemoteInputController remoteInputController, PendingIntent pendingIntent, boolean z, boolean z2, boolean z3) {
        this.mLogger.logHandleClickAfterKeyguardDismissed(notificationEntry.getKey());
        new DesktopNotificationActivityStarter$$ExternalSyntheticLambda3(this, notificationEntry, expandableNotificationRow, remoteInputController, pendingIntent, z, z2).run();
        return true;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /* renamed from: handleNotificationClickAfterPanelCollapsed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$handleNotificationClickAfterKeyguardDismissed$1(com.android.systemui.statusbar.notification.collection.NotificationEntry r12, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r13, com.android.systemui.statusbar.RemoteInputController r14, android.app.PendingIntent r15, boolean r16, boolean r17) {
        /*
            r11 = this;
            r7 = r11
            r8 = r12
            java.lang.String r9 = r12.getKey()
            com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger r0 = r7.mLogger
            r0.logHandleClickAfterPanelCollapsed(r9)
            android.app.IActivityManager r0 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x0012 }
            r0.resumeAppSwitches()     // Catch:{ RemoteException -> 0x0012 }
        L_0x0012:
            if (r16 == 0) goto L_0x0037
            android.os.UserHandle r0 = r15.getCreatorUserHandle()
            int r0 = r0.getIdentifier()
            com.android.internal.widget.LockPatternUtils r1 = r7.mLockPatternUtils
            boolean r1 = r1.isSeparateProfileChallengeEnabled(r0)
            if (r1 == 0) goto L_0x0037
            android.app.KeyguardManager r1 = r7.mKeyguardManager
            boolean r1 = r1.isDeviceLocked(r0)
            if (r1 == 0) goto L_0x0037
            android.content.IntentSender r1 = r15.getIntentSender()
            boolean r0 = r11.startWorkChallengeIfNecessary(r0, r1, r9)
            if (r0 == 0) goto L_0x0037
            return
        L_0x0037:
            java.lang.CharSequence r0 = r8.remoteInputText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r10 = 0
            if (r0 != 0) goto L_0x0043
            java.lang.CharSequence r0 = r8.remoteInputText
            goto L_0x0044
        L_0x0043:
            r0 = r10
        L_0x0044:
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x0062
            r1 = r14
            boolean r1 = r14.isSpinning(r9)
            if (r1 != 0) goto L_0x0062
            android.content.Intent r1 = new android.content.Intent
            r1.<init>()
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "android.remoteInputDraft"
            android.content.Intent r0 = r1.putExtra(r2, r0)
            r2 = r0
            goto L_0x0063
        L_0x0062:
            r2 = r10
        L_0x0063:
            r0 = r11
            r1 = r15
            r3 = r12
            r4 = r13
            r5 = r17
            r6 = r16
            r0.startNotificationIntent(r1, r2, r3, r4, r5, r6)
            com.android.internal.statusbar.NotificationVisibility$NotificationLocation r0 = com.android.systemui.statusbar.notification.logging.NotificationLogger.getNotificationLocation(r12)
            java.lang.String r1 = r12.getKey()
            android.service.notification.NotificationListenerService$Ranking r2 = r12.getRanking()
            int r2 = r2.getRank()
            int r3 = r11.getVisibleNotificationsCount()
            r4 = 1
            com.android.internal.statusbar.NotificationVisibility r0 = com.android.internal.statusbar.NotificationVisibility.obtain(r1, r2, r3, r4, r0)
            android.service.notification.StatusBarNotification r1 = r12.getSbn()
            boolean r1 = shouldAutoCancel(r1)
            if (r1 == 0) goto L_0x0097
            com.android.systemui.statusbar.notification.row.OnUserInteractionCallback r2 = r7.mOnUserInteractionCallback
            com.android.systemui.statusbar.notification.collection.NotificationEntry r10 = r2.getGroupSummaryToDismiss(r12)
        L_0x0097:
            com.android.systemui.statusbar.NotificationClickNotifier r2 = r7.mClickNotifier
            r2.onNotificationClick(r9, r0)
            if (r1 != 0) goto L_0x00a6
            com.android.systemui.statusbar.NotificationRemoteInputManager r0 = r7.mRemoteInputManager
            boolean r0 = r0.isNotificationKeptForRemoteInputHistory(r9)
            if (r0 == 0) goto L_0x00b0
        L_0x00a6:
            android.os.Handler r0 = r7.mMainThreadHandler
            com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopNotificationActivityStarter$$ExternalSyntheticLambda2 r1 = new com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopNotificationActivityStarter$$ExternalSyntheticLambda2
            r1.<init>(r11, r12, r10)
            r0.post(r1)
        L_0x00b0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopNotificationActivityStarter.lambda$handleNotificationClickAfterKeyguardDismissed$1(com.android.systemui.statusbar.notification.collection.NotificationEntry, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow, com.android.systemui.statusbar.RemoteInputController, android.app.PendingIntent, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleNotificationClickAfterPanelCollapsed$3(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        new DesktopNotificationActivityStarter$$ExternalSyntheticLambda1(this, notificationEntry, notificationEntry2).run();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleNotificationClickAfterPanelCollapsed$2(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        this.mOnUserInteractionCallback.onDismiss(notificationEntry, 1, notificationEntry2);
    }

    private void startNotificationIntent(PendingIntent pendingIntent, Intent intent, NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, boolean z, boolean z2) {
        try {
            ActivityOptions makeBasic = ActivityOptions.makeBasic();
            Context context = this.mContext;
            if (!(context instanceof FakeDesktopDisplayContext)) {
                makeBasic.setLaunchDisplayId(context.getDisplayId());
            }
            int sendAndReturnResult = pendingIntent.sendAndReturnResult(this.mContext, 0, intent, (PendingIntent.OnFinished) null, (Handler) null, (String) null, makeBasic.toBundle());
            Log.d("DesktopNotificationActivityStarter", "startNotificationIntent launchResult = " + sendAndReturnResult);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private static boolean shouldAutoCancel(StatusBarNotification statusBarNotification) {
        int i = statusBarNotification.getNotification().flags;
        return (i & 16) == 16 && (i & 64) == 0;
    }

    /* access modifiers changed from: package-private */
    public boolean startWorkChallengeIfNecessary(int i, IntentSender intentSender, String str) {
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

    private int getVisibleNotificationsCount() {
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            return this.mNotifPipeline.getShadeListCount();
        }
        return this.mEntryManager.getActiveNotificationsCount();
    }
}
