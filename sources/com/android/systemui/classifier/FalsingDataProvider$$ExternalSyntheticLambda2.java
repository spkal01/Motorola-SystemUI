package com.android.systemui.classifier;

import com.android.systemui.classifier.FalsingDataProvider;
import java.util.function.Consumer;

public final /* synthetic */ class FalsingDataProvider$$ExternalSyntheticLambda2 implements Consumer {
    public static final /* synthetic */ FalsingDataProvider$$ExternalSyntheticLambda2 INSTANCE = new FalsingDataProvider$$ExternalSyntheticLambda2();

    private /* synthetic */ FalsingDataProvider$$ExternalSyntheticLambda2() {
    }

    public final void accept(Object obj) {
        ((FalsingDataProvider.SessionListener) obj).onSessionEnded();
    }
}
