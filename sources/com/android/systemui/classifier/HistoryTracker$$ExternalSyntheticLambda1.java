package com.android.systemui.classifier;

import java.util.function.BinaryOperator;

public final /* synthetic */ class HistoryTracker$$ExternalSyntheticLambda1 implements BinaryOperator {
    public static final /* synthetic */ HistoryTracker$$ExternalSyntheticLambda1 INSTANCE = new HistoryTracker$$ExternalSyntheticLambda1();

    private /* synthetic */ HistoryTracker$$ExternalSyntheticLambda1() {
    }

    public final Object apply(Object obj, Object obj2) {
        return Double.valueOf(Double.sum(((Double) obj).doubleValue(), ((Double) obj2).doubleValue()));
    }
}
