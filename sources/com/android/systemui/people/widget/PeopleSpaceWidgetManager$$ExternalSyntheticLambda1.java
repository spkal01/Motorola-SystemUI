package com.android.systemui.people.widget;

import android.service.notification.StatusBarNotification;
import com.android.systemui.people.PeopleSpaceUtils;

public final /* synthetic */ class PeopleSpaceWidgetManager$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ PeopleSpaceWidgetManager f$0;
    public final /* synthetic */ StatusBarNotification f$1;
    public final /* synthetic */ PeopleSpaceUtils.NotificationAction f$2;

    public /* synthetic */ PeopleSpaceWidgetManager$$ExternalSyntheticLambda1(PeopleSpaceWidgetManager peopleSpaceWidgetManager, StatusBarNotification statusBarNotification, PeopleSpaceUtils.NotificationAction notificationAction) {
        this.f$0 = peopleSpaceWidgetManager;
        this.f$1 = statusBarNotification;
        this.f$2 = notificationAction;
    }

    public final void run() {
        this.f$0.lambda$updateWidgetsWithNotificationChanged$1(this.f$1, this.f$2);
    }
}
