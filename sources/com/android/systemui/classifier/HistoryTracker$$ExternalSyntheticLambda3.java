package com.android.systemui.classifier;

import com.android.systemui.classifier.HistoryTracker;
import java.util.function.Function;

public final /* synthetic */ class HistoryTracker$$ExternalSyntheticLambda3 implements Function {
    public final /* synthetic */ double f$0;

    public /* synthetic */ HistoryTracker$$ExternalSyntheticLambda3(double d) {
        this.f$0 = d;
    }

    public final Object apply(Object obj) {
        return Double.valueOf(Math.pow(((HistoryTracker.CombinedResult) obj).getScore() - this.f$0, 2.0d));
    }
}
