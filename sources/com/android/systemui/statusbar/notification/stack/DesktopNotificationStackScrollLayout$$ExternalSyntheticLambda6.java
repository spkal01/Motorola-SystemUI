package com.android.systemui.statusbar.notification.stack;

import java.util.ArrayList;

public final /* synthetic */ class DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ DesktopNotificationStackScrollLayout f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ DesktopNotificationStackScrollLayout$$ExternalSyntheticLambda6(DesktopNotificationStackScrollLayout desktopNotificationStackScrollLayout, ArrayList arrayList, int i) {
        this.f$0 = desktopNotificationStackScrollLayout;
        this.f$1 = arrayList;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$clearNotifications$3(this.f$1, this.f$2);
    }
}
