package com.android.systemui.statusbar.notification.row;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final /* synthetic */ class NotificationInlineImageCache$$ExternalSyntheticLambda0 implements Predicate {
    public final /* synthetic */ Set f$0;

    public /* synthetic */ NotificationInlineImageCache$$ExternalSyntheticLambda0(Set set) {
        this.f$0 = set;
    }

    public final boolean test(Object obj) {
        return NotificationInlineImageCache.lambda$purge$0(this.f$0, (Map.Entry) obj);
    }
}
