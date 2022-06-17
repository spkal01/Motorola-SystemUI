package com.android.systemui.people;

import android.app.people.PeopleSpaceTile;
import android.view.View;
import com.android.systemui.people.widget.PeopleTileKey;

public final /* synthetic */ class PeopleSpaceActivity$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ PeopleSpaceActivity f$0;
    public final /* synthetic */ PeopleSpaceTile f$1;
    public final /* synthetic */ PeopleTileKey f$2;

    public /* synthetic */ PeopleSpaceActivity$$ExternalSyntheticLambda0(PeopleSpaceActivity peopleSpaceActivity, PeopleSpaceTile peopleSpaceTile, PeopleTileKey peopleTileKey) {
        this.f$0 = peopleSpaceActivity;
        this.f$1 = peopleSpaceTile;
        this.f$2 = peopleTileKey;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setTileView$0(this.f$1, this.f$2, view);
    }
}
