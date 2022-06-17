package com.android.systemui.settings;

import java.util.function.Consumer;

public final /* synthetic */ class CurrentUserTracker$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ CurrentUserTracker f$0;

    public /* synthetic */ CurrentUserTracker$$ExternalSyntheticLambda0(CurrentUserTracker currentUserTracker) {
        this.f$0 = currentUserTracker;
    }

    public final void accept(Object obj) {
        this.f$0.onUserSwitched(((Integer) obj).intValue());
    }
}
