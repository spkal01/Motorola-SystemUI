package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Predicate;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda45 implements Predicate {
    public final /* synthetic */ StatusBar f$0;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda45(StatusBar statusBar) {
        this.f$0 = statusBar;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$maybeEscalateHeadsUp$16((NotificationEntry) obj);
    }
}
