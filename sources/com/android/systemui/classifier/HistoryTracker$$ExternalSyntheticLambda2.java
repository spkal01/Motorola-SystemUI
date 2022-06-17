package com.android.systemui.classifier;

import com.android.systemui.classifier.HistoryTracker;
import java.util.function.Consumer;

public final /* synthetic */ class HistoryTracker$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ HistoryTracker f$0;

    public /* synthetic */ HistoryTracker$$ExternalSyntheticLambda2(HistoryTracker historyTracker) {
        this.f$0 = historyTracker;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$addResults$3((HistoryTracker.BeliefListener) obj);
    }
}
