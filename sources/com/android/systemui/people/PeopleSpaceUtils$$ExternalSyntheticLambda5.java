package com.android.systemui.people;

import android.content.pm.PackageManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Predicate;

public final /* synthetic */ class PeopleSpaceUtils$$ExternalSyntheticLambda5 implements Predicate {
    public final /* synthetic */ PackageManager f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ PeopleSpaceUtils$$ExternalSyntheticLambda5(PackageManager packageManager, String str) {
        this.f$0 = packageManager;
        this.f$1 = str;
    }

    public final boolean test(Object obj) {
        return PeopleSpaceUtils.lambda$getNotificationsByUri$1(this.f$0, this.f$1, (NotificationEntry) obj);
    }
}
