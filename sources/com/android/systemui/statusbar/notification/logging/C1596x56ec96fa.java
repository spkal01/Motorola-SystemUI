package com.android.systemui.statusbar.notification.logging;

import com.android.systemui.statusbar.notification.logging.NotificationLogger;

/* renamed from: com.android.systemui.statusbar.notification.logging.NotificationLogger$ExpansionStateLogger$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1596x56ec96fa implements Runnable {
    public final /* synthetic */ NotificationLogger.ExpansionStateLogger f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ NotificationLogger.ExpansionStateLogger.State f$2;

    public /* synthetic */ C1596x56ec96fa(NotificationLogger.ExpansionStateLogger expansionStateLogger, String str, NotificationLogger.ExpansionStateLogger.State state) {
        this.f$0 = expansionStateLogger;
        this.f$1 = str;
        this.f$2 = state;
    }

    public final void run() {
        this.f$0.lambda$maybeNotifyOnNotificationExpansionChanged$0(this.f$1, this.f$2);
    }
}
