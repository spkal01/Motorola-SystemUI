package com.android.systemui.statusbar.notification.people;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifierImpl$getPeopleTypeOfSummary$childTypes$1 */
/* compiled from: PeopleNotificationIdentifier.kt */
final class C1598x4801521d extends Lambda implements Function1<NotificationEntry, Integer> {
    final /* synthetic */ PeopleNotificationIdentifierImpl this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C1598x4801521d(PeopleNotificationIdentifierImpl peopleNotificationIdentifierImpl) {
        super(1);
        this.this$0 = peopleNotificationIdentifierImpl;
    }

    public final int invoke(NotificationEntry notificationEntry) {
        PeopleNotificationIdentifierImpl peopleNotificationIdentifierImpl = this.this$0;
        Intrinsics.checkNotNullExpressionValue(notificationEntry, "it");
        return peopleNotificationIdentifierImpl.getPeopleNotificationType(notificationEntry);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Integer.valueOf(invoke((NotificationEntry) obj));
    }
}
