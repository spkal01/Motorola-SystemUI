package com.android.systemui.people.widget;

import java.util.Map;
import java.util.function.Function;

public final /* synthetic */ class PeopleSpaceWidgetManager$$ExternalSyntheticLambda4 implements Function {
    public final /* synthetic */ PeopleSpaceWidgetManager f$0;
    public final /* synthetic */ Map f$1;

    public /* synthetic */ PeopleSpaceWidgetManager$$ExternalSyntheticLambda4(PeopleSpaceWidgetManager peopleSpaceWidgetManager, Map map) {
        this.f$0 = peopleSpaceWidgetManager;
        this.f$1 = map;
    }

    public final Object apply(Object obj) {
        return this.f$0.lambda$updateWidgetIdsBasedOnNotifications$2(this.f$1, (Integer) obj);
    }
}
