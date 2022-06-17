package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Objects;
import java.util.function.Predicate;

public final /* synthetic */ class NotificationMediaManager$4$$ExternalSyntheticLambda1 implements Predicate {
    public final /* synthetic */ String f$0;

    public /* synthetic */ NotificationMediaManager$4$$ExternalSyntheticLambda1(String str) {
        this.f$0 = str;
    }

    public final boolean test(Object obj) {
        return Objects.equals(((NotificationEntry) obj).getKey(), this.f$0);
    }
}
