package com.android.systemui.people;

import android.app.people.PeopleSpaceTile;
import java.util.Comparator;

public final /* synthetic */ class PeopleSpaceUtils$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ PeopleSpaceUtils$$ExternalSyntheticLambda1 INSTANCE = new PeopleSpaceUtils$$ExternalSyntheticLambda1();

    private /* synthetic */ PeopleSpaceUtils$$ExternalSyntheticLambda1() {
    }

    public final int compare(Object obj, Object obj2) {
        return new Long(((PeopleSpaceTile) obj2).getLastInteractionTimestamp()).compareTo(new Long(((PeopleSpaceTile) obj).getLastInteractionTimestamp()));
    }
}
