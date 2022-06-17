package com.motorola.systemui.cli.navgesture.recents;

import java.util.ArrayList;
import java.util.function.Consumer;

public final /* synthetic */ class RecentsModel$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ RecentsModel f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ RecentsModel$$ExternalSyntheticLambda0(RecentsModel recentsModel, int i) {
        this.f$0 = recentsModel;
        this.f$1 = i;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onTaskStackChangedBackground$1(this.f$1, (ArrayList) obj);
    }
}
