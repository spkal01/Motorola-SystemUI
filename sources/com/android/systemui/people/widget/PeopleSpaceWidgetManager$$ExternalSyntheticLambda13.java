package com.android.systemui.people.widget;

import android.service.notification.ConversationChannelWrapper;
import java.util.function.Predicate;

public final /* synthetic */ class PeopleSpaceWidgetManager$$ExternalSyntheticLambda13 implements Predicate {
    public static final /* synthetic */ PeopleSpaceWidgetManager$$ExternalSyntheticLambda13 INSTANCE = new PeopleSpaceWidgetManager$$ExternalSyntheticLambda13();

    private /* synthetic */ PeopleSpaceWidgetManager$$ExternalSyntheticLambda13() {
    }

    public final boolean test(Object obj) {
        return PeopleSpaceWidgetManager.lambda$getRecentTiles$8((ConversationChannelWrapper) obj);
    }
}
