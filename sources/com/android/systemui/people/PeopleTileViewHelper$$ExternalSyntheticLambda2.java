package com.android.systemui.people;

import android.app.people.ConversationStatus;
import java.util.function.Function;

public final /* synthetic */ class PeopleTileViewHelper$$ExternalSyntheticLambda2 implements Function {
    public static final /* synthetic */ PeopleTileViewHelper$$ExternalSyntheticLambda2 INSTANCE = new PeopleTileViewHelper$$ExternalSyntheticLambda2();

    private /* synthetic */ PeopleTileViewHelper$$ExternalSyntheticLambda2() {
    }

    public final Object apply(Object obj) {
        return Long.valueOf(((ConversationStatus) obj).getStartTimeMillis());
    }
}
