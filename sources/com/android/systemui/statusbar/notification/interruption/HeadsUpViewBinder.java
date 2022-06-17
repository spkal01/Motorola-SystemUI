package com.android.systemui.statusbar.notification.interruption;

import android.util.ArrayMap;
import androidx.core.p002os.CancellationSignal;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import java.util.Map;

public class HeadsUpViewBinder {
    private final NotificationMessagingUtil mNotificationMessagingUtil;
    private NotificationPresenter mNotificationPresenter;
    private final Map<NotificationEntry, CancellationSignal> mOngoingBindCallbacks = new ArrayMap();
    private final RowContentBindStage mStage;

    HeadsUpViewBinder(NotificationMessagingUtil notificationMessagingUtil, RowContentBindStage rowContentBindStage) {
        this.mNotificationMessagingUtil = notificationMessagingUtil;
        this.mStage = rowContentBindStage;
    }

    public void setPresenter(NotificationPresenter notificationPresenter) {
        this.mNotificationPresenter = notificationPresenter;
    }

    public void bindHeadsUpView(NotificationEntry notificationEntry, NotifBindPipeline.BindCallback bindCallback) {
        RowContentBindParams rowContentBindParams = (RowContentBindParams) this.mStage.getStageParams(notificationEntry);
        rowContentBindParams.setUseIncreasedHeadsUpHeight(this.mNotificationMessagingUtil.isImportantMessaging(notificationEntry.getSbn(), notificationEntry.getImportance()) && !this.mNotificationPresenter.isPresenterFullyCollapsed());
        rowContentBindParams.requireContentViews(4);
        CancellationSignal requestRebind = this.mStage.requestRebind(notificationEntry, new HeadsUpViewBinder$$ExternalSyntheticLambda0(rowContentBindParams, bindCallback));
        abortBindCallback(notificationEntry);
        this.mOngoingBindCallbacks.put(notificationEntry, requestRebind);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$bindHeadsUpView$0(RowContentBindParams rowContentBindParams, NotifBindPipeline.BindCallback bindCallback, NotificationEntry notificationEntry) {
        notificationEntry.getRow().setUsesIncreasedHeadsUpHeight(rowContentBindParams.useIncreasedHeadsUpHeight());
        if (bindCallback != null) {
            bindCallback.onBindFinished(notificationEntry);
        }
    }

    public void abortBindCallback(NotificationEntry notificationEntry) {
        CancellationSignal remove = this.mOngoingBindCallbacks.remove(notificationEntry);
        if (remove != null) {
            remove.cancel();
        }
    }

    public void unbindHeadsUpView(NotificationEntry notificationEntry) {
        abortBindCallback(notificationEntry);
        ((RowContentBindParams) this.mStage.getStageParams(notificationEntry)).markContentViewsFreeable(4);
        this.mStage.requestRebind(notificationEntry, (NotifBindPipeline.BindCallback) null);
    }
}
