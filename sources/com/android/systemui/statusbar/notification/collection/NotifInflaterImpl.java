package com.android.systemui.statusbar.notification.collection;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.collection.inflation.NotifInflater;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;

public class NotifInflaterImpl implements NotifInflater {
    private final NotifCollection mNotifCollection;
    /* access modifiers changed from: private */
    public final NotifInflationErrorManager mNotifErrorManager;
    private final NotifPipeline mNotifPipeline;
    private NotificationRowBinderImpl mNotificationRowBinder;
    private final IStatusBarService mStatusBarService;

    public NotifInflaterImpl(IStatusBarService iStatusBarService, NotifCollection notifCollection, NotifInflationErrorManager notifInflationErrorManager, NotifPipeline notifPipeline) {
        this.mStatusBarService = iStatusBarService;
        this.mNotifCollection = notifCollection;
        this.mNotifErrorManager = notifInflationErrorManager;
        this.mNotifPipeline = notifPipeline;
    }

    public void setRowBinder(NotificationRowBinderImpl notificationRowBinderImpl) {
        this.mNotificationRowBinder = notificationRowBinderImpl;
    }

    public void rebindViews(NotificationEntry notificationEntry, NotifInflater.InflationCallback inflationCallback) {
        inflateViews(notificationEntry, inflationCallback);
    }

    public void inflateViews(NotificationEntry notificationEntry, NotifInflater.InflationCallback inflationCallback) {
        try {
            requireBinder().inflateViews(notificationEntry, wrapInflationCallback(inflationCallback));
        } catch (InflationException e) {
            this.mNotifErrorManager.setInflationError(notificationEntry, e);
        }
    }

    private NotificationRowContentBinder.InflationCallback wrapInflationCallback(final NotifInflater.InflationCallback inflationCallback) {
        return new NotificationRowContentBinder.InflationCallback() {
            public void handleInflationException(NotificationEntry notificationEntry, Exception exc) {
                NotifInflaterImpl.this.mNotifErrorManager.setInflationError(notificationEntry, exc);
            }

            public void onAsyncInflationFinished(NotificationEntry notificationEntry) {
                NotifInflaterImpl.this.mNotifErrorManager.clearInflationError(notificationEntry);
                NotifInflater.InflationCallback inflationCallback = inflationCallback;
                if (inflationCallback != null) {
                    inflationCallback.onInflationFinished(notificationEntry);
                }
            }
        };
    }

    private NotificationRowBinderImpl requireBinder() {
        NotificationRowBinderImpl notificationRowBinderImpl = this.mNotificationRowBinder;
        if (notificationRowBinderImpl != null) {
            return notificationRowBinderImpl;
        }
        throw new RuntimeException("NotificationRowBinder must be attached before using NotifInflaterImpl.");
    }
}
