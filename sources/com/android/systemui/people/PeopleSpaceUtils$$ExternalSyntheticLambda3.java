package com.android.systemui.people;

import android.app.people.PeopleSpaceTile;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import java.util.function.Function;

public final /* synthetic */ class PeopleSpaceUtils$$ExternalSyntheticLambda3 implements Function {
    public final /* synthetic */ LauncherApps f$0;

    public /* synthetic */ PeopleSpaceUtils$$ExternalSyntheticLambda3(LauncherApps launcherApps) {
        this.f$0 = launcherApps;
    }

    public final Object apply(Object obj) {
        return new PeopleSpaceTile.Builder((ShortcutInfo) obj, this.f$0).build();
    }
}
