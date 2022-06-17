package com.android.systemui.classifier;

import com.android.systemui.classifier.FalsingDataProvider;
import java.util.function.Consumer;

public final /* synthetic */ class FalsingDataProvider$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ FalsingDataProvider f$0;

    public /* synthetic */ FalsingDataProvider$$ExternalSyntheticLambda1(FalsingDataProvider falsingDataProvider) {
        this.f$0 = falsingDataProvider;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$completePriorGesture$1((FalsingDataProvider.GestureFinalizedListener) obj);
    }
}
