package com.android.systemui.statusbar.notification.row;

import android.app.Notification;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.CancellationSignal;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.media.MediaFeatureFlag;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.InflationTask;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.ConversationNotificationProcessor;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import com.android.systemui.statusbar.policy.InflatedSmartReplyState;
import com.android.systemui.statusbar.policy.InflatedSmartReplyViewHolder;
import com.android.systemui.statusbar.policy.SmartReplyStateInflater;
import com.android.systemui.statusbar.policy.SmartReplyView;
import com.android.systemui.util.Assert;
import java.util.HashMap;
import java.util.concurrent.Executor;

@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
public class NotificationContentInflater implements NotificationRowContentBinder {
    private final Executor mBgExecutor;
    private final ConversationNotificationProcessor mConversationProcessor;
    private boolean mInflateSynchronously = false;
    private final boolean mIsMediaInQS;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final NotifRemoteViewCache mRemoteViewCache;
    private final SmartReplyStateInflater mSmartReplyStateInflater;

    NotificationContentInflater(NotifRemoteViewCache notifRemoteViewCache, NotificationRemoteInputManager notificationRemoteInputManager, ConversationNotificationProcessor conversationNotificationProcessor, MediaFeatureFlag mediaFeatureFlag, Executor executor, SmartReplyStateInflater smartReplyStateInflater) {
        this.mRemoteViewCache = notifRemoteViewCache;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mConversationProcessor = conversationNotificationProcessor;
        this.mIsMediaInQS = mediaFeatureFlag.getEnabled();
        this.mBgExecutor = executor;
        this.mSmartReplyStateInflater = smartReplyStateInflater;
    }

    public void bindContent(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, int i, NotificationRowContentBinder.BindParams bindParams, boolean z, NotificationRowContentBinder.InflationCallback inflationCallback) {
        NotificationRowContentBinder.BindParams bindParams2 = bindParams;
        if (!expandableNotificationRow.isRemoved()) {
            expandableNotificationRow.getImageResolver().preloadImages(notificationEntry.getSbn().getNotification());
            if (z) {
                this.mRemoteViewCache.clearCache(notificationEntry);
            } else {
                NotificationEntry notificationEntry2 = notificationEntry;
            }
            cancelContentViewFrees(expandableNotificationRow, i);
            Executor executor = this.mBgExecutor;
            boolean z2 = this.mInflateSynchronously;
            NotifRemoteViewCache notifRemoteViewCache = this.mRemoteViewCache;
            ConversationNotificationProcessor conversationNotificationProcessor = this.mConversationProcessor;
            boolean z3 = bindParams2.isLowPriority;
            boolean z4 = bindParams2.usesIncreasedHeight;
            boolean z5 = bindParams2.usesIncreasedHeadsUpHeight;
            RemoteViews.InteractionHandler remoteViewsOnClickHandler = this.mRemoteInputManager.getRemoteViewsOnClickHandler();
            boolean z6 = this.mIsMediaInQS;
            boolean z7 = z6;
            AsyncInflationTask asyncInflationTask = r3;
            AsyncInflationTask asyncInflationTask2 = new AsyncInflationTask(executor, z2, i, notifRemoteViewCache, notificationEntry, conversationNotificationProcessor, expandableNotificationRow, z3, z4, z5, inflationCallback, remoteViewsOnClickHandler, z7, this.mSmartReplyStateInflater);
            if (this.mInflateSynchronously) {
                AsyncInflationTask asyncInflationTask3 = asyncInflationTask;
                asyncInflationTask3.onPostExecute(asyncInflationTask3.doInBackground(new Void[0]));
                return;
            }
            asyncInflationTask.executeOnExecutor(this.mBgExecutor, new Void[0]);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public InflationProgress inflateNotificationViews(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, NotificationRowContentBinder.BindParams bindParams, boolean z, int i, Notification.Builder builder, Context context, SmartReplyStateInflater smartReplyStateInflater) {
        NotificationRowContentBinder.BindParams bindParams2 = bindParams;
        int i2 = i;
        NotificationEntry notificationEntry2 = notificationEntry;
        Context context2 = context;
        InflationProgress inflateSmartReplyViews = inflateSmartReplyViews(createRemoteViews(i, builder, bindParams2.isLowPriority, bindParams2.usesIncreasedHeight, bindParams2.usesIncreasedHeadsUpHeight, context, (Context) null), i2, notificationEntry2, expandableNotificationRow.getContext(), context2, expandableNotificationRow.getExistingSmartReplyState(), smartReplyStateInflater);
        apply(this.mBgExecutor, z, inflateSmartReplyViews, i, this.mRemoteViewCache, notificationEntry, expandableNotificationRow, this.mRemoteInputManager.getRemoteViewsOnClickHandler(), (NotificationRowContentBinder.InflationCallback) null);
        return inflateSmartReplyViews;
    }

    public void cancelBind(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow) {
        notificationEntry.abortTask();
    }

    public void unbindContent(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, int i) {
        int i2 = 1;
        while (i != 0) {
            if ((i & i2) != 0) {
                freeNotificationView(notificationEntry, expandableNotificationRow, i2);
            }
            i &= ~i2;
            i2 <<= 1;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$freeNotificationView$0(ExpandableNotificationRow expandableNotificationRow, NotificationEntry notificationEntry) {
        expandableNotificationRow.getPrivateLayout().setContractedChild((View) null);
        this.mRemoteViewCache.removeCachedView(notificationEntry, 1);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$freeNotificationView$1(ExpandableNotificationRow expandableNotificationRow, NotificationEntry notificationEntry) {
        expandableNotificationRow.getPrivateLayout().setExpandedChild((View) null);
        this.mRemoteViewCache.removeCachedView(notificationEntry, 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$freeNotificationView$2(ExpandableNotificationRow expandableNotificationRow, NotificationEntry notificationEntry) {
        expandableNotificationRow.getPrivateLayout().setHeadsUpChild((View) null);
        this.mRemoteViewCache.removeCachedView(notificationEntry, 4);
        expandableNotificationRow.getPrivateLayout().setHeadsUpInflatedSmartReplies((InflatedSmartReplyViewHolder) null);
    }

    private void freeNotificationView(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, int i) {
        if (i == 1) {
            expandableNotificationRow.getPrivateLayout().performWhenContentInactive(0, new NotificationContentInflater$$ExternalSyntheticLambda4(this, expandableNotificationRow, notificationEntry));
        } else if (i == 2) {
            expandableNotificationRow.getPrivateLayout().performWhenContentInactive(1, new NotificationContentInflater$$ExternalSyntheticLambda5(this, expandableNotificationRow, notificationEntry));
        } else if (i == 4) {
            expandableNotificationRow.getPrivateLayout().performWhenContentInactive(2, new NotificationContentInflater$$ExternalSyntheticLambda6(this, expandableNotificationRow, notificationEntry));
        } else if (i == 8) {
            expandableNotificationRow.getPublicLayout().performWhenContentInactive(0, new NotificationContentInflater$$ExternalSyntheticLambda7(this, expandableNotificationRow, notificationEntry));
        }
        if (!MotoFeature.getInstance(expandableNotificationRow.getContext()).isSupportCli()) {
            return;
        }
        if (i == 1) {
            expandableNotificationRow.getCliRow().getPrivateLayout().performWhenContentInactive(0, new NotificationContentInflater$$ExternalSyntheticLambda1(expandableNotificationRow));
        } else if (i == 2) {
            expandableNotificationRow.getCliRow().getPrivateLayout().performWhenContentInactive(1, new NotificationContentInflater$$ExternalSyntheticLambda3(expandableNotificationRow));
        } else if (i == 4) {
            NotificationContentView cliPrivateLayout = expandableNotificationRow.getCliPrivateLayout();
            if (cliPrivateLayout != null) {
                cliPrivateLayout.performWhenContentInactive(2, new NotificationContentInflater$$ExternalSyntheticLambda8(cliPrivateLayout));
            }
        } else if (i == 8) {
            expandableNotificationRow.getCliRow().getPublicLayout().performWhenContentInactive(0, new NotificationContentInflater$$ExternalSyntheticLambda2(expandableNotificationRow));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$freeNotificationView$3(ExpandableNotificationRow expandableNotificationRow, NotificationEntry notificationEntry) {
        expandableNotificationRow.getPublicLayout().setContractedChild((View) null);
        this.mRemoteViewCache.removeCachedView(notificationEntry, 8);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$freeNotificationView$6(NotificationContentView notificationContentView) {
        notificationContentView.setHeadsUpChild((View) null);
        notificationContentView.setHeadsUpInflatedSmartReplies((InflatedSmartReplyViewHolder) null);
    }

    private void cancelContentViewFrees(ExpandableNotificationRow expandableNotificationRow, int i) {
        int i2 = i & 1;
        if (i2 != 0) {
            expandableNotificationRow.getPrivateLayout().removeContentInactiveRunnable(0);
        }
        int i3 = i & 2;
        if (i3 != 0) {
            expandableNotificationRow.getPrivateLayout().removeContentInactiveRunnable(1);
        }
        int i4 = i & 4;
        if (i4 != 0) {
            expandableNotificationRow.getPrivateLayout().removeContentInactiveRunnable(2);
        }
        int i5 = i & 8;
        if (i5 != 0) {
            expandableNotificationRow.getPublicLayout().removeContentInactiveRunnable(0);
        }
        if (MotoFeature.getInstance(expandableNotificationRow.getContext()).isSupportCli()) {
            ExpandableNotificationRow cliRow = expandableNotificationRow.getCliRow();
            if (i2 != 0) {
                cliRow.getPrivateLayout().removeContentInactiveRunnable(0);
            }
            if (i3 != 0) {
                cliRow.getPrivateLayout().removeContentInactiveRunnable(1);
            }
            if (i4 != 0) {
                expandableNotificationRow.getCliPrivateLayout().removeContentInactiveRunnable(2);
            }
            if (i5 != 0) {
                cliRow.getPublicLayout().removeContentInactiveRunnable(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public static InflationProgress inflateSmartReplyViews(InflationProgress inflationProgress, int i, NotificationEntry notificationEntry, Context context, Context context2, InflatedSmartReplyState inflatedSmartReplyState, SmartReplyStateInflater smartReplyStateInflater) {
        SmartReplyView smartReplyView;
        InflationProgress inflationProgress2 = inflationProgress;
        boolean z = false;
        boolean z2 = ((i & 1) == 0 || inflationProgress.newContentView == null) ? false : true;
        boolean z3 = ((i & 2) == 0 || inflationProgress.newExpandedView == null) ? false : true;
        if (!((i & 4) == 0 || inflationProgress.newHeadsUpView == null)) {
            z = true;
        }
        if (z2 || z3 || z) {
            NotificationEntry notificationEntry2 = notificationEntry;
            InflatedSmartReplyState unused = inflationProgress.inflatedSmartReplyState = smartReplyStateInflater.inflateSmartReplyState(notificationEntry);
        } else {
            NotificationEntry notificationEntry3 = notificationEntry;
            SmartReplyStateInflater smartReplyStateInflater2 = smartReplyStateInflater;
        }
        if (z3) {
            InflatedSmartReplyViewHolder unused2 = inflationProgress.expandedInflatedSmartReplies = smartReplyStateInflater.inflateSmartReplyViewHolder(context, context2, notificationEntry, inflatedSmartReplyState, inflationProgress.inflatedSmartReplyState);
            if (MotoFeature.getInstance(context).isSupportCli()) {
                InflatedSmartReplyViewHolder unused3 = inflationProgress.cliExpandedInflatedSmartReplies = smartReplyStateInflater.inflateSmartReplyViewHolder(context, context2, notificationEntry, inflatedSmartReplyState, inflationProgress.inflatedSmartReplyState);
                if (!(inflationProgress.cliExpandedInflatedSmartReplies == null || (smartReplyView = inflationProgress.cliExpandedInflatedSmartReplies.getSmartReplyView()) == null)) {
                    smartReplyView.setIsCliSmartReply(true);
                }
            }
        }
        if (z) {
            InflatedSmartReplyViewHolder unused4 = inflationProgress.headsUpInflatedSmartReplies = smartReplyStateInflater.inflateSmartReplyViewHolder(context, context2, notificationEntry, inflatedSmartReplyState, inflationProgress.inflatedSmartReplyState);
            if (MotoFeature.getInstance(context).isSupportCli() && HeadsUpUtil.isCliHeadsUpNotification(notificationEntry.getSbn())) {
                InflatedSmartReplyViewHolder unused5 = inflationProgress.cliHeadsUpInflatedSmartReplies = smartReplyStateInflater.inflateSmartReplyViewHolder(context, context2, notificationEntry, inflatedSmartReplyState, inflationProgress.inflatedSmartReplyState);
            }
        }
        return inflationProgress2;
    }

    /* access modifiers changed from: private */
    public static InflationProgress createRemoteViews(int i, Notification.Builder builder, boolean z, boolean z2, boolean z3, Context context, Context context2) {
        InflationProgress inflationProgress = new InflationProgress();
        if ((i & 1) != 0) {
            RemoteViews unused = inflationProgress.newContentView = createContentView(builder, z, z2);
        }
        if ((i & 2) != 0) {
            RemoteViews unused2 = inflationProgress.newExpandedView = createExpandedView(builder, z);
        }
        if ((i & 4) != 0) {
            RemoteViews unused3 = inflationProgress.newHeadsUpView = builder.createHeadsUpContentView(z3);
        }
        if ((i & 8) != 0) {
            RemoteViews unused4 = inflationProgress.newPublicView = builder.makePublicContentView(z);
        }
        inflationProgress.cliPackageContext = context2;
        inflationProgress.packageContext = context;
        CharSequence unused5 = inflationProgress.headsUpStatusBarText = builder.getHeadsUpStatusBarText(false);
        CharSequence unused6 = inflationProgress.headsUpStatusBarTextPublic = builder.getHeadsUpStatusBarText(true);
        return inflationProgress;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x01d9  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0218  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.os.CancellationSignal apply(java.util.concurrent.Executor r23, boolean r24, com.android.systemui.statusbar.notification.row.NotificationContentInflater.InflationProgress r25, int r26, com.android.systemui.statusbar.notification.row.NotifRemoteViewCache r27, com.android.systemui.statusbar.notification.collection.NotificationEntry r28, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r29, android.widget.RemoteViews.InteractionHandler r30, com.android.systemui.statusbar.notification.row.NotificationRowContentBinder.InflationCallback r31) {
        /*
            r15 = r25
            r14 = r27
            r13 = r28
            com.android.systemui.statusbar.notification.row.NotificationContentView r12 = r29.getPrivateLayout()
            com.android.systemui.statusbar.notification.row.NotificationContentView r11 = r29.getPublicLayout()
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            r0 = r26 & 1
            r9 = 0
            r8 = 1
            if (r0 == 0) goto L_0x00b1
            android.widget.RemoteViews r0 = r25.newContentView
            android.widget.RemoteViews r1 = r14.getCachedView(r13, r8)
            boolean r0 = canReapplyRemoteView(r0, r1)
            r16 = r0 ^ 1
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$1 r7 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$1
            r7.<init>()
            android.view.View r17 = r12.getContractedChild()
            com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper r18 = r12.getVisibleWrapper(r9)
            r4 = 1
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r26
            r5 = r27
            r6 = r28
            r19 = r7
            r7 = r29
            r8 = r16
            r9 = r30
            r20 = r10
            r10 = r31
            r21 = r11
            r11 = r12
            r22 = r12
            r12 = r17
            r13 = r18
            r14 = r20
            r15 = r19
            applyRemoteView(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            android.content.Context r0 = r29.getContext()
            com.android.systemui.moto.MotoFeature r0 = com.android.systemui.moto.MotoFeature.getInstance(r0)
            boolean r0 = r0.isSupportCli()
            if (r0 == 0) goto L_0x00b7
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$2 r15 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$2
            r14 = r25
            r15.<init>()
            r4 = 9
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = r29.getCliRow()
            com.android.systemui.statusbar.notification.row.NotificationContentView r11 = r0.getPrivateLayout()
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = r29.getCliRow()
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r0.getPrivateLayout()
            android.view.View r12 = r0.getContractedChild()
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = r29.getCliRow()
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r0.getPrivateLayout()
            r13 = 0
            com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper r17 = r0.getVisibleWrapper(r13)
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r26
            r5 = r27
            r6 = r28
            r7 = r29
            r8 = r16
            r9 = r30
            r10 = r31
            r13 = r17
            r14 = r20
            applyRemoteView(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x00b7
        L_0x00b1:
            r20 = r10
            r21 = r11
            r22 = r12
        L_0x00b7:
            r0 = r26 & 2
            r15 = 2
            if (r0 == 0) goto L_0x0160
            android.widget.RemoteViews r0 = r25.newExpandedView
            if (r0 == 0) goto L_0x0160
            android.widget.RemoteViews r0 = r25.newExpandedView
            r14 = r27
            r13 = r28
            android.widget.RemoteViews r1 = r14.getCachedView(r13, r15)
            boolean r0 = canReapplyRemoteView(r0, r1)
            r12 = 1
            r16 = r0 ^ 1
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$3 r11 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$3
            r10 = r25
            r11.<init>()
            android.view.View r17 = r22.getExpandedChild()
            r9 = r22
            com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper r18 = r9.getVisibleWrapper(r12)
            r4 = 2
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r26
            r5 = r27
            r6 = r28
            r7 = r29
            r8 = r16
            r9 = r30
            r10 = r31
            r19 = r11
            r11 = r22
            r12 = r17
            r13 = r18
            r14 = r20
            r15 = r19
            applyRemoteView(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            android.content.Context r0 = r29.getContext()
            com.android.systemui.moto.MotoFeature r0 = com.android.systemui.moto.MotoFeature.getInstance(r0)
            boolean r0 = r0.isSupportCli()
            if (r0 == 0) goto L_0x0160
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$4 r15 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$4
            r14 = r25
            r15.<init>()
            r4 = 10
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = r29.getCliRow()
            com.android.systemui.statusbar.notification.row.NotificationContentView r11 = r0.getPrivateLayout()
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = r29.getCliRow()
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r0.getPrivateLayout()
            android.view.View r12 = r0.getExpandedChild()
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = r29.getCliRow()
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r0.getPrivateLayout()
            r13 = 1
            com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper r17 = r0.getVisibleWrapper(r13)
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r26
            r5 = r27
            r6 = r28
            r7 = r29
            r8 = r16
            r9 = r30
            r10 = r31
            r16 = r13
            r13 = r17
            r14 = r20
            applyRemoteView(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x0162
        L_0x0160:
            r16 = 1
        L_0x0162:
            r0 = r26 & 4
            if (r0 == 0) goto L_0x0214
            android.widget.RemoteViews r0 = r25.newHeadsUpView
            if (r0 == 0) goto L_0x0214
            android.widget.RemoteViews r0 = r25.newHeadsUpView
            r1 = 4
            r15 = r27
            r14 = r28
            android.widget.RemoteViews r1 = r15.getCachedView(r14, r1)
            boolean r0 = canReapplyRemoteView(r0, r1)
            r17 = r0 ^ 1
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$5 r13 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$5
            r12 = r25
            r13.<init>()
            android.view.View r18 = r22.getHeadsUpChild()
            r11 = r22
            r10 = 2
            com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper r19 = r11.getVisibleWrapper(r10)
            r4 = 4
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r26
            r5 = r27
            r6 = r28
            r7 = r29
            r8 = r17
            r9 = r30
            r10 = r31
            r12 = r18
            r18 = r13
            r13 = r19
            r14 = r20
            r15 = r18
            applyRemoteView(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            android.content.Context r0 = r29.getContext()
            com.android.systemui.moto.MotoFeature r0 = com.android.systemui.moto.MotoFeature.getInstance(r0)
            boolean r0 = r0.isSupportCli()
            if (r0 == 0) goto L_0x0214
            com.android.systemui.statusbar.notification.collection.NotificationEntry r0 = r29.getEntry()
            android.service.notification.StatusBarNotification r0 = r0.getSbn()
            boolean r0 = com.android.systemui.statusbar.policy.HeadsUpUtil.isCliHeadsUpNotification(r0)
            if (r0 == 0) goto L_0x0214
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r29.getCliPrivateLayout()
            android.view.View r0 = r0.getHeadsUpChild()
            if (r0 != 0) goto L_0x01dc
            r9 = r16
            goto L_0x01dd
        L_0x01dc:
            r9 = 0
        L_0x01dd:
            r8 = r17 | r9
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$6 r15 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$6
            r14 = r25
            r15.<init>()
            r4 = 11
            com.android.systemui.statusbar.notification.row.NotificationContentView r11 = r29.getCliPrivateLayout()
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r29.getCliPrivateLayout()
            android.view.View r12 = r0.getHeadsUpChild()
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r29.getCliPrivateLayout()
            r1 = 2
            com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper r13 = r0.getVisibleWrapper(r1)
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r26
            r5 = r27
            r6 = r28
            r7 = r29
            r9 = r30
            r10 = r31
            r14 = r20
            applyRemoteView(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
        L_0x0214:
            r0 = r26 & 8
            if (r0 == 0) goto L_0x02b2
            android.widget.RemoteViews r0 = r25.newPublicView
            r1 = 8
            r15 = r27
            r14 = r28
            android.widget.RemoteViews r1 = r15.getCachedView(r14, r1)
            boolean r0 = canReapplyRemoteView(r0, r1)
            r16 = r0 ^ 1
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$7 r13 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$7
            r12 = r25
            r13.<init>()
            android.view.View r17 = r21.getContractedChild()
            r11 = r21
            r10 = 0
            com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper r18 = r11.getVisibleWrapper(r10)
            r4 = 8
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r26
            r5 = r27
            r6 = r28
            r7 = r29
            r8 = r16
            r9 = r30
            r10 = r31
            r12 = r17
            r17 = r13
            r13 = r18
            r14 = r20
            r15 = r17
            applyRemoteView(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            android.content.Context r0 = r29.getContext()
            com.android.systemui.moto.MotoFeature r0 = com.android.systemui.moto.MotoFeature.getInstance(r0)
            boolean r0 = r0.isSupportCli()
            if (r0 == 0) goto L_0x02b2
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$8 r15 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$8
            r14 = r25
            r15.<init>()
            r4 = 12
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = r29.getCliRow()
            com.android.systemui.statusbar.notification.row.NotificationContentView r11 = r0.getPrivateLayout()
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = r29.getCliRow()
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r0.getPublicLayout()
            android.view.View r12 = r0.getContractedChild()
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = r29.getCliRow()
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r0.getPublicLayout()
            r1 = 0
            com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper r13 = r0.getVisibleWrapper(r1)
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r26
            r5 = r27
            r6 = r28
            r7 = r29
            r8 = r16
            r9 = r30
            r10 = r31
            r14 = r20
            applyRemoteView(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
        L_0x02b2:
            r0 = r25
            r1 = r26
            r2 = r27
            r3 = r20
            r4 = r31
            r5 = r28
            r6 = r29
            finishIfDone(r0, r1, r2, r3, r4, r5, r6)
            android.os.CancellationSignal r0 = new android.os.CancellationSignal
            r0.<init>()
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$$ExternalSyntheticLambda0 r1 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$$ExternalSyntheticLambda0
            r2 = r20
            r1.<init>(r2)
            r0.setOnCancelListener(r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.NotificationContentInflater.apply(java.util.concurrent.Executor, boolean, com.android.systemui.statusbar.notification.row.NotificationContentInflater$InflationProgress, int, com.android.systemui.statusbar.notification.row.NotifRemoteViewCache, com.android.systemui.statusbar.notification.collection.NotificationEntry, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow, android.widget.RemoteViews$InteractionHandler, com.android.systemui.statusbar.notification.row.NotificationRowContentBinder$InflationCallback):android.os.CancellationSignal");
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0021  */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void applyRemoteView(java.util.concurrent.Executor r21, boolean r22, com.android.systemui.statusbar.notification.row.NotificationContentInflater.InflationProgress r23, int r24, int r25, com.android.systemui.statusbar.notification.row.NotifRemoteViewCache r26, com.android.systemui.statusbar.notification.collection.NotificationEntry r27, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r28, boolean r29, android.widget.RemoteViews.InteractionHandler r30, com.android.systemui.statusbar.notification.row.NotificationRowContentBinder.InflationCallback r31, com.android.systemui.statusbar.notification.row.NotificationContentView r32, android.view.View r33, com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper r34, java.util.HashMap<java.lang.Integer, android.os.CancellationSignal> r35, com.android.systemui.statusbar.notification.row.NotificationContentInflater.ApplyCallback r36) {
        /*
            r0 = r23
            r15 = r30
            r14 = r35
            android.widget.RemoteViews r13 = r36.getRemoteView()
            r1 = 1
            r2 = 9
            r12 = r25
            if (r12 < r2) goto L_0x001a
            r2 = 12
            r9 = r24
            if (r9 > r2) goto L_0x001c
            r18 = r1
            goto L_0x001f
        L_0x001a:
            r9 = r24
        L_0x001c:
            r2 = 0
            r18 = r2
        L_0x001f:
            if (r22 == 0) goto L_0x0060
            if (r29 == 0) goto L_0x0039
            if (r18 == 0) goto L_0x0028
            android.content.Context r0 = r0.cliPackageContext     // Catch:{ Exception -> 0x0049 }
            goto L_0x002a
        L_0x0028:
            android.content.Context r0 = r0.packageContext     // Catch:{ Exception -> 0x0049 }
        L_0x002a:
            r11 = r32
            android.view.View r0 = r13.apply(r0, r11, r15)     // Catch:{ Exception -> 0x0049 }
            r0.setIsRootNamespace(r1)     // Catch:{ Exception -> 0x0049 }
            r4 = r36
            r4.setResultView(r0)     // Catch:{ Exception -> 0x0049 }
            goto L_0x005f
        L_0x0039:
            if (r18 == 0) goto L_0x003e
            android.content.Context r0 = r0.cliPackageContext     // Catch:{ Exception -> 0x0049 }
            goto L_0x0040
        L_0x003e:
            android.content.Context r0 = r0.packageContext     // Catch:{ Exception -> 0x0049 }
        L_0x0040:
            r10 = r33
            r13.reapply(r0, r10, r15)     // Catch:{ Exception -> 0x0049 }
            r34.onReinflated()     // Catch:{ Exception -> 0x0049 }
            goto L_0x005f
        L_0x0049:
            r0 = move-exception
            com.android.systemui.statusbar.notification.collection.NotificationEntry r1 = r28.getEntry()
            r8 = r31
            handleInflationError(r14, r0, r1, r8)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r25)
            android.os.CancellationSignal r1 = new android.os.CancellationSignal
            r1.<init>()
            r14.put(r0, r1)
        L_0x005f:
            return
        L_0x0060:
            r8 = r31
            r11 = r32
            r10 = r33
            r4 = r36
            com.android.systemui.statusbar.notification.row.NotificationContentInflater$9 r19 = new com.android.systemui.statusbar.notification.row.NotificationContentInflater$9
            r1 = r19
            r2 = r28
            r3 = r29
            r5 = r34
            r6 = r35
            r7 = r25
            r8 = r23
            r9 = r24
            r10 = r26
            r11 = r31
            r12 = r27
            r20 = r13
            r13 = r33
            r14 = r20
            r15 = r18
            r16 = r32
            r17 = r30
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
            if (r29 == 0) goto L_0x00a8
            if (r18 == 0) goto L_0x0096
            android.content.Context r0 = r0.cliPackageContext
            goto L_0x0098
        L_0x0096:
            android.content.Context r0 = r0.packageContext
        L_0x0098:
            r1 = r0
            r0 = r20
            r2 = r32
            r3 = r21
            r4 = r19
            r5 = r30
            android.os.CancellationSignal r0 = r0.applyAsync(r1, r2, r3, r4, r5)
            goto L_0x00be
        L_0x00a8:
            if (r18 == 0) goto L_0x00ad
            android.content.Context r0 = r0.cliPackageContext
            goto L_0x00af
        L_0x00ad:
            android.content.Context r0 = r0.packageContext
        L_0x00af:
            r1 = r0
            r0 = r20
            r2 = r33
            r3 = r21
            r4 = r19
            r5 = r30
            android.os.CancellationSignal r0 = r0.reapplyAsync(r1, r2, r3, r4, r5)
        L_0x00be:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r25)
            r2 = r35
            r2.put(r1, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.NotificationContentInflater.applyRemoteView(java.util.concurrent.Executor, boolean, com.android.systemui.statusbar.notification.row.NotificationContentInflater$InflationProgress, int, int, com.android.systemui.statusbar.notification.row.NotifRemoteViewCache, com.android.systemui.statusbar.notification.collection.NotificationEntry, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow, boolean, android.widget.RemoteViews$InteractionHandler, com.android.systemui.statusbar.notification.row.NotificationRowContentBinder$InflationCallback, com.android.systemui.statusbar.notification.row.NotificationContentView, android.view.View, com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper, java.util.HashMap, com.android.systemui.statusbar.notification.row.NotificationContentInflater$ApplyCallback):void");
    }

    /* access modifiers changed from: private */
    public static void handleInflationError(HashMap<Integer, CancellationSignal> hashMap, Exception exc, NotificationEntry notificationEntry, NotificationRowContentBinder.InflationCallback inflationCallback) {
        Assert.isMainThread();
        hashMap.values().forEach(NotificationContentInflater$$ExternalSyntheticLambda9.INSTANCE);
        if (inflationCallback != null) {
            inflationCallback.handleInflationException(notificationEntry, exc);
        }
    }

    /* access modifiers changed from: private */
    public static boolean finishIfDone(InflationProgress inflationProgress, int i, NotifRemoteViewCache notifRemoteViewCache, HashMap<Integer, CancellationSignal> hashMap, NotificationRowContentBinder.InflationCallback inflationCallback, NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow) {
        Assert.isMainThread();
        NotificationContentView privateLayout = expandableNotificationRow.getPrivateLayout();
        NotificationContentView publicLayout = expandableNotificationRow.getPublicLayout();
        boolean z = false;
        if (!hashMap.isEmpty()) {
            return false;
        }
        if ((i & 1) != 0) {
            if (inflationProgress.inflatedContentView != null) {
                privateLayout.setContractedChild(inflationProgress.inflatedContentView);
                notifRemoteViewCache.putCachedView(notificationEntry, 1, inflationProgress.newContentView);
            } else if (notifRemoteViewCache.hasCachedView(notificationEntry, 1)) {
                notifRemoteViewCache.putCachedView(notificationEntry, 1, inflationProgress.newContentView);
            }
            if (MotoFeature.getInstance(expandableNotificationRow.getContext()).isSupportCli() && inflationProgress.inflatedCliContentView != null) {
                expandableNotificationRow.getCliRow().getPrivateLayout().setContractedChild(inflationProgress.inflatedCliContentView);
            }
        }
        if ((i & 2) != 0) {
            if (inflationProgress.inflatedExpandedView != null) {
                privateLayout.setExpandedChild(inflationProgress.inflatedExpandedView);
                notifRemoteViewCache.putCachedView(notificationEntry, 2, inflationProgress.newExpandedView);
            } else if (inflationProgress.newExpandedView == null) {
                privateLayout.setExpandedChild((View) null);
                notifRemoteViewCache.removeCachedView(notificationEntry, 2);
            } else if (notifRemoteViewCache.hasCachedView(notificationEntry, 2)) {
                notifRemoteViewCache.putCachedView(notificationEntry, 2, inflationProgress.newExpandedView);
            }
            if (inflationProgress.newExpandedView != null) {
                privateLayout.setExpandedInflatedSmartReplies(inflationProgress.expandedInflatedSmartReplies);
            } else {
                privateLayout.setExpandedInflatedSmartReplies((InflatedSmartReplyViewHolder) null);
            }
            expandableNotificationRow.setExpandable(inflationProgress.newExpandedView != null);
            if (MotoFeature.getInstance(expandableNotificationRow.getContext()).isSupportCli()) {
                if (inflationProgress.inflatedCliExpandedView != null) {
                    expandableNotificationRow.getCliRow().getPrivateLayout().setExpandedChild(inflationProgress.inflatedCliExpandedView);
                } else if (inflationProgress.newExpandedView == null) {
                    expandableNotificationRow.getCliRow().getPrivateLayout().setExpandedChild((View) null);
                }
                if (inflationProgress.newExpandedView != null) {
                    expandableNotificationRow.getCliRow().getPrivateLayout().setExpandedInflatedSmartReplies(inflationProgress.cliExpandedInflatedSmartReplies);
                } else {
                    expandableNotificationRow.getCliRow().getPrivateLayout().setExpandedInflatedSmartReplies((InflatedSmartReplyViewHolder) null);
                }
                ExpandableNotificationRow cliRow = expandableNotificationRow.getCliRow();
                if (inflationProgress.newExpandedView != null) {
                    z = true;
                }
                cliRow.setExpandable(z);
            }
        }
        if ((i & 4) != 0) {
            if (inflationProgress.inflatedHeadsUpView != null) {
                privateLayout.setHeadsUpChild(inflationProgress.inflatedHeadsUpView);
                notifRemoteViewCache.putCachedView(notificationEntry, 4, inflationProgress.newHeadsUpView);
            } else if (inflationProgress.newHeadsUpView == null) {
                privateLayout.setHeadsUpChild((View) null);
                notifRemoteViewCache.removeCachedView(notificationEntry, 4);
            } else if (notifRemoteViewCache.hasCachedView(notificationEntry, 4)) {
                notifRemoteViewCache.putCachedView(notificationEntry, 4, inflationProgress.newHeadsUpView);
            }
            if (inflationProgress.newHeadsUpView != null) {
                privateLayout.setHeadsUpInflatedSmartReplies(inflationProgress.headsUpInflatedSmartReplies);
            } else {
                privateLayout.setHeadsUpInflatedSmartReplies((InflatedSmartReplyViewHolder) null);
            }
            if (MotoFeature.getInstance(expandableNotificationRow.getContext()).isSupportCli()) {
                if (inflationProgress.inflatedCliHeadsUpView != null) {
                    expandableNotificationRow.getCliPrivateLayout().setHeadsUpChild(inflationProgress.inflatedCliHeadsUpView);
                    expandableNotificationRow.getCliPrivateLayout().setHeadsUp(true);
                } else if (inflationProgress.newHeadsUpView == null) {
                    expandableNotificationRow.getCliPrivateLayout().setHeadsUpChild((View) null);
                }
                if (inflationProgress.newHeadsUpView != null) {
                    expandableNotificationRow.getCliPrivateLayout().setHeadsUpInflatedSmartReplies(inflationProgress.cliHeadsUpInflatedSmartReplies);
                } else {
                    expandableNotificationRow.getCliPrivateLayout().setHeadsUpInflatedSmartReplies((InflatedSmartReplyViewHolder) null);
                }
            }
        }
        privateLayout.setInflatedSmartReplyState(inflationProgress.inflatedSmartReplyState);
        if ((i & 8) != 0) {
            if (inflationProgress.inflatedPublicView != null) {
                publicLayout.setContractedChild(inflationProgress.inflatedPublicView);
                notifRemoteViewCache.putCachedView(notificationEntry, 8, inflationProgress.newPublicView);
            } else if (notifRemoteViewCache.hasCachedView(notificationEntry, 8)) {
                notifRemoteViewCache.putCachedView(notificationEntry, 8, inflationProgress.newPublicView);
            }
            if (MotoFeature.getInstance(expandableNotificationRow.getContext()).isSupportCli() && inflationProgress.inflatedCliPublicView != null) {
                expandableNotificationRow.getCliRow().getPublicLayout().setContractedChild(inflationProgress.inflatedCliPublicView);
            }
        }
        notificationEntry.headsUpStatusBarText = inflationProgress.headsUpStatusBarText;
        notificationEntry.headsUpStatusBarTextPublic = inflationProgress.headsUpStatusBarTextPublic;
        if (inflationCallback != null) {
            inflationCallback.onAsyncInflationFinished(notificationEntry);
        }
        return true;
    }

    private static RemoteViews createExpandedView(Notification.Builder builder, boolean z) {
        RemoteViews createBigContentView = builder.createBigContentView();
        if (createBigContentView != null) {
            return createBigContentView;
        }
        if (!z) {
            return null;
        }
        RemoteViews createContentView = builder.createContentView();
        Notification.Builder.makeHeaderExpanded(createContentView);
        return createContentView;
    }

    private static RemoteViews createContentView(Notification.Builder builder, boolean z, boolean z2) {
        if (z) {
            return builder.makeLowPriorityContentView(false);
        }
        return builder.createContentView(z2);
    }

    @VisibleForTesting
    static boolean canReapplyRemoteView(RemoteViews remoteViews, RemoteViews remoteViews2) {
        if (remoteViews == null && remoteViews2 == null) {
            return true;
        }
        if (remoteViews == null || remoteViews2 == null || remoteViews2.getPackage() == null || remoteViews.getPackage() == null || !remoteViews.getPackage().equals(remoteViews2.getPackage()) || remoteViews.getLayoutId() != remoteViews2.getLayoutId() || remoteViews2.hasFlags(1)) {
            return false;
        }
        return true;
    }

    @VisibleForTesting
    public void setInflateSynchronously(boolean z) {
        this.mInflateSynchronously = z;
    }

    public static class AsyncInflationTask extends AsyncTask<Void, Void, InflationProgress> implements NotificationRowContentBinder.InflationCallback, InflationTask {
        private final Executor mBgExecutor;
        private final NotificationRowContentBinder.InflationCallback mCallback;
        private CancellationSignal mCancellationSignal;
        private final Context mContext;
        private final ConversationNotificationProcessor mConversationProcessor;
        private final NotificationEntry mEntry;
        private Exception mError;
        private final boolean mInflateSynchronously;
        private final boolean mIsLowPriority;
        private final boolean mIsMediaInQS;
        private final int mReInflateFlags;
        private final NotifRemoteViewCache mRemoteViewCache;
        private RemoteViews.InteractionHandler mRemoteViewClickHandler;
        private ExpandableNotificationRow mRow;
        private final SmartReplyStateInflater mSmartRepliesInflater;
        private final boolean mUsesIncreasedHeadsUpHeight;
        private final boolean mUsesIncreasedHeight;

        private AsyncInflationTask(Executor executor, boolean z, int i, NotifRemoteViewCache notifRemoteViewCache, NotificationEntry notificationEntry, ConversationNotificationProcessor conversationNotificationProcessor, ExpandableNotificationRow expandableNotificationRow, boolean z2, boolean z3, boolean z4, NotificationRowContentBinder.InflationCallback inflationCallback, RemoteViews.InteractionHandler interactionHandler, boolean z5, SmartReplyStateInflater smartReplyStateInflater) {
            this.mEntry = notificationEntry;
            this.mRow = expandableNotificationRow;
            this.mBgExecutor = executor;
            this.mInflateSynchronously = z;
            this.mReInflateFlags = i;
            this.mRemoteViewCache = notifRemoteViewCache;
            this.mSmartRepliesInflater = smartReplyStateInflater;
            this.mContext = expandableNotificationRow.getContext();
            this.mIsLowPriority = z2;
            this.mUsesIncreasedHeight = z3;
            this.mUsesIncreasedHeadsUpHeight = z4;
            this.mRemoteViewClickHandler = interactionHandler;
            this.mCallback = inflationCallback;
            this.mConversationProcessor = conversationNotificationProcessor;
            this.mIsMediaInQS = z5;
            notificationEntry.setInflationTask(this);
        }

        @VisibleForTesting
        public int getReInflateFlags() {
            return this.mReInflateFlags;
        }

        /* access modifiers changed from: package-private */
        public void updateApplicationInfo(StatusBarNotification statusBarNotification) {
            try {
                Notification.addFieldsFromContext(this.mContext.getPackageManager().getApplicationInfoAsUser(statusBarNotification.getPackageName(), 8192, UserHandle.getUserId(statusBarNotification.getUid())), statusBarNotification.getNotification());
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }

        /* access modifiers changed from: protected */
        public InflationProgress doInBackground(Void... voidArr) {
            Context context;
            RtlEnabledContext rtlEnabledContext;
            try {
                StatusBarNotification sbn = this.mEntry.getSbn();
                updateApplicationInfo(sbn);
                Notification.Builder recoverBuilder = Notification.Builder.recoverBuilder(this.mContext, sbn.getNotification());
                Context packageContext = sbn.getPackageContext(this.mContext);
                if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
                    MotoFeature.getInstance(this.mContext);
                    context = MotoFeature.getCliContext(packageContext);
                } else {
                    context = null;
                }
                if (recoverBuilder.usesTemplate()) {
                    RtlEnabledContext rtlEnabledContext2 = new RtlEnabledContext(packageContext);
                    rtlEnabledContext = MotoFeature.getInstance(this.mContext).isSupportCli() ? new RtlEnabledContext(context) : context;
                    packageContext = rtlEnabledContext2;
                } else {
                    rtlEnabledContext = context;
                }
                if (this.mEntry.getRanking().isConversation()) {
                    this.mConversationProcessor.processNotification(this.mEntry, recoverBuilder);
                }
                return NotificationContentInflater.inflateSmartReplyViews(NotificationContentInflater.createRemoteViews(this.mReInflateFlags, recoverBuilder, this.mIsLowPriority, this.mUsesIncreasedHeight, this.mUsesIncreasedHeadsUpHeight, packageContext, rtlEnabledContext), this.mReInflateFlags, this.mEntry, this.mContext, packageContext, this.mRow.getExistingSmartReplyState(), this.mSmartRepliesInflater);
            } catch (Exception e) {
                this.mError = e;
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(InflationProgress inflationProgress) {
            Exception exc = this.mError;
            if (exc == null) {
                this.mCancellationSignal = NotificationContentInflater.apply(this.mBgExecutor, this.mInflateSynchronously, inflationProgress, this.mReInflateFlags, this.mRemoteViewCache, this.mEntry, this.mRow, this.mRemoteViewClickHandler, this);
                return;
            }
            handleError(exc);
        }

        private void handleError(Exception exc) {
            this.mEntry.onInflationTaskFinished();
            StatusBarNotification sbn = this.mEntry.getSbn();
            Log.e("StatusBar", "couldn't inflate view for notification " + (sbn.getPackageName() + "/0x" + Integer.toHexString(sbn.getId())), exc);
            NotificationRowContentBinder.InflationCallback inflationCallback = this.mCallback;
            if (inflationCallback != null) {
                inflationCallback.handleInflationException(this.mRow.getEntry(), new InflationException("Couldn't inflate contentViews" + exc));
            }
        }

        public void abort() {
            cancel(true);
            CancellationSignal cancellationSignal = this.mCancellationSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
        }

        public void handleInflationException(NotificationEntry notificationEntry, Exception exc) {
            handleError(exc);
        }

        public void onAsyncInflationFinished(NotificationEntry notificationEntry) {
            this.mEntry.onInflationTaskFinished();
            this.mRow.onNotificationUpdated();
            NotificationRowContentBinder.InflationCallback inflationCallback = this.mCallback;
            if (inflationCallback != null) {
                inflationCallback.onAsyncInflationFinished(this.mEntry);
            }
            this.mRow.getImageResolver().purgeCache();
        }

        private class RtlEnabledContext extends ContextWrapper {
            private RtlEnabledContext(Context context) {
                super(context);
            }

            public ApplicationInfo getApplicationInfo() {
                ApplicationInfo applicationInfo = super.getApplicationInfo();
                applicationInfo.flags |= 4194304;
                return applicationInfo;
            }
        }
    }

    @VisibleForTesting
    static class InflationProgress {
        /* access modifiers changed from: private */
        public InflatedSmartReplyViewHolder cliExpandedInflatedSmartReplies;
        /* access modifiers changed from: private */
        public InflatedSmartReplyViewHolder cliHeadsUpInflatedSmartReplies;
        Context cliPackageContext;
        /* access modifiers changed from: private */
        public InflatedSmartReplyViewHolder expandedInflatedSmartReplies;
        /* access modifiers changed from: private */
        public InflatedSmartReplyViewHolder headsUpInflatedSmartReplies;
        /* access modifiers changed from: private */
        public CharSequence headsUpStatusBarText;
        /* access modifiers changed from: private */
        public CharSequence headsUpStatusBarTextPublic;
        /* access modifiers changed from: private */
        public View inflatedCliContentView;
        /* access modifiers changed from: private */
        public View inflatedCliExpandedView;
        /* access modifiers changed from: private */
        public View inflatedCliHeadsUpView;
        /* access modifiers changed from: private */
        public View inflatedCliPublicView;
        /* access modifiers changed from: private */
        public View inflatedContentView;
        /* access modifiers changed from: private */
        public View inflatedExpandedView;
        /* access modifiers changed from: private */
        public View inflatedHeadsUpView;
        /* access modifiers changed from: private */
        public View inflatedPublicView;
        /* access modifiers changed from: private */
        public InflatedSmartReplyState inflatedSmartReplyState;
        /* access modifiers changed from: private */
        public RemoteViews newContentView;
        /* access modifiers changed from: private */
        public RemoteViews newExpandedView;
        /* access modifiers changed from: private */
        public RemoteViews newHeadsUpView;
        /* access modifiers changed from: private */
        public RemoteViews newPublicView;
        @VisibleForTesting
        Context packageContext;

        InflationProgress() {
        }
    }

    @VisibleForTesting
    static abstract class ApplyCallback {
        public abstract RemoteViews getRemoteView();

        public abstract void setResultView(View view);

        ApplyCallback() {
        }
    }
}
