package com.android.systemui.people;

import android.content.Context;
import com.android.systemui.people.widget.PeopleSpaceWidgetManager;
import java.util.Map;

public final /* synthetic */ class PeopleSpaceUtils$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Context f$0;
    public final /* synthetic */ PeopleSpaceWidgetManager f$1;
    public final /* synthetic */ Map f$2;
    public final /* synthetic */ int[] f$3;

    public /* synthetic */ PeopleSpaceUtils$$ExternalSyntheticLambda0(Context context, PeopleSpaceWidgetManager peopleSpaceWidgetManager, Map map, int[] iArr) {
        this.f$0 = context;
        this.f$1 = peopleSpaceWidgetManager;
        this.f$2 = map;
        this.f$3 = iArr;
    }

    public final void run() {
        PeopleSpaceUtils.getDataFromContacts(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
