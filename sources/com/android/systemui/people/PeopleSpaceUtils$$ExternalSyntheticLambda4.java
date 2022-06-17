package com.android.systemui.people;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final /* synthetic */ class PeopleSpaceUtils$$ExternalSyntheticLambda4 implements Function {
    public static final /* synthetic */ PeopleSpaceUtils$$ExternalSyntheticLambda4 INSTANCE = new PeopleSpaceUtils$$ExternalSyntheticLambda4();

    private /* synthetic */ PeopleSpaceUtils$$ExternalSyntheticLambda4() {
    }

    public final Object apply(Object obj) {
        return ((Set) ((Map.Entry) obj).getValue()).stream();
    }
}
