package com.motorola.systemui.cli.navgesture.view;

import com.android.systemui.shared.recents.model.Task;
import java.util.function.Predicate;

public final /* synthetic */ class RecentsViewContainer$$ExternalSyntheticLambda8 implements Predicate {
    public final /* synthetic */ RecentsViewContainer f$0;

    public /* synthetic */ RecentsViewContainer$$ExternalSyntheticLambda8(RecentsViewContainer recentsViewContainer) {
        this.f$0 = recentsViewContainer;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$applyLoadPlan$1((Task) obj);
    }
}
