package com.android.systemui.util.leak;

import java.util.Collection;
import java.util.function.Predicate;

public final /* synthetic */ class LeakDetector$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ LeakDetector$$ExternalSyntheticLambda0 INSTANCE = new LeakDetector$$ExternalSyntheticLambda0();

    private /* synthetic */ LeakDetector$$ExternalSyntheticLambda0() {
    }

    public final boolean test(Object obj) {
        return LeakDetector.lambda$dump$0((Collection) obj);
    }
}
