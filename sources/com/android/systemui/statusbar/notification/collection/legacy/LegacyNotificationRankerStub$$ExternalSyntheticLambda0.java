package com.android.systemui.statusbar.notification.collection.legacy;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.ToLongFunction;

public final /* synthetic */ class LegacyNotificationRankerStub$$ExternalSyntheticLambda0 implements ToLongFunction {
    public static final /* synthetic */ LegacyNotificationRankerStub$$ExternalSyntheticLambda0 INSTANCE = new LegacyNotificationRankerStub$$ExternalSyntheticLambda0();

    private /* synthetic */ LegacyNotificationRankerStub$$ExternalSyntheticLambda0() {
    }

    public final long applyAsLong(Object obj) {
        return ((NotificationEntry) obj).getSbn().getNotification().when;
    }
}
