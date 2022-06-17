package com.android.systemui.people.widget;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Predicate;

public final /* synthetic */ class PeopleSpaceWidgetManager$$ExternalSyntheticLambda11 implements Predicate {
    public final /* synthetic */ PeopleSpaceWidgetManager f$0;

    public /* synthetic */ PeopleSpaceWidgetManager$$ExternalSyntheticLambda11(PeopleSpaceWidgetManager peopleSpaceWidgetManager) {
        this.f$0 = peopleSpaceWidgetManager;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$getGroupedConversationNotifications$4((NotificationEntry) obj);
    }
}
