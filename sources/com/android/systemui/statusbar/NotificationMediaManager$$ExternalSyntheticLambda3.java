package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Predicate;

public final /* synthetic */ class NotificationMediaManager$$ExternalSyntheticLambda3 implements Predicate {
    public final /* synthetic */ NotificationMediaManager f$0;

    public /* synthetic */ NotificationMediaManager$$ExternalSyntheticLambda3(NotificationMediaManager notificationMediaManager) {
        this.f$0 = notificationMediaManager;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$getMediaIcon$0((NotificationEntry) obj);
    }
}
