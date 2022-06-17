package com.android.systemui.statusbar.notification.stack;

import java.util.ArrayList;

public final /* synthetic */ class NotificationStackScrollLayout$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ NotificationStackScrollLayout f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ NotificationStackScrollLayout$$ExternalSyntheticLambda8(NotificationStackScrollLayout notificationStackScrollLayout, ArrayList arrayList, int i) {
        this.f$0 = notificationStackScrollLayout;
        this.f$1 = arrayList;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$clearNotifications$5(this.f$1, this.f$2);
    }
}
