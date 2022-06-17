package com.android.systemui.classifier;

import java.util.function.BinaryOperator;

public final /* synthetic */ class HistoryTracker$$ExternalSyntheticLambda0 implements BinaryOperator {
    public static final /* synthetic */ HistoryTracker$$ExternalSyntheticLambda0 INSTANCE = new HistoryTracker$$ExternalSyntheticLambda0();

    private /* synthetic */ HistoryTracker$$ExternalSyntheticLambda0() {
    }

    public final Object apply(Object obj, Object obj2) {
        return Double.valueOf((((Double) obj).doubleValue() * ((Double) obj2).doubleValue()) / ((((Double) obj).doubleValue() * ((Double) obj2).doubleValue()) + ((1.0d - ((Double) obj).doubleValue()) * (1.0d - ((Double) obj2).doubleValue()))));
    }
}
