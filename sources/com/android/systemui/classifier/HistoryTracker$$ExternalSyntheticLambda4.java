package com.android.systemui.classifier;

import com.android.systemui.classifier.HistoryTracker;
import java.util.function.Function;

public final /* synthetic */ class HistoryTracker$$ExternalSyntheticLambda4 implements Function {
    public final /* synthetic */ long f$0;

    public /* synthetic */ HistoryTracker$$ExternalSyntheticLambda4(long j) {
        this.f$0 = j;
    }

    public final Object apply(Object obj) {
        return Double.valueOf(((HistoryTracker.CombinedResult) obj).getDecayedScore(this.f$0));
    }
}
