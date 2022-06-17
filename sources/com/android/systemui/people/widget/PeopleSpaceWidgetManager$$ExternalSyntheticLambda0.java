package com.android.systemui.people.widget;

import android.app.people.PeopleSpaceTile;

public final /* synthetic */ class PeopleSpaceWidgetManager$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ PeopleSpaceWidgetManager f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ PeopleSpaceTile f$2;

    public /* synthetic */ PeopleSpaceWidgetManager$$ExternalSyntheticLambda0(PeopleSpaceWidgetManager peopleSpaceWidgetManager, int i, PeopleSpaceTile peopleSpaceTile) {
        this.f$0 = peopleSpaceWidgetManager;
        this.f$1 = i;
        this.f$2 = peopleSpaceTile;
    }

    public final void run() {
        this.f$0.lambda$addNewWidget$5(this.f$1, this.f$2);
    }
}
