package com.android.systemui.people.widget;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

public final /* synthetic */ class PeopleSpaceWidgetManager$$ExternalSyntheticLambda10 implements Function {
    public static final /* synthetic */ PeopleSpaceWidgetManager$$ExternalSyntheticLambda10 INSTANCE = new PeopleSpaceWidgetManager$$ExternalSyntheticLambda10();

    private /* synthetic */ PeopleSpaceWidgetManager$$ExternalSyntheticLambda10() {
    }

    public final Object apply(Object obj) {
        return new PeopleTileKey((NotificationEntry) obj);
    }
}
