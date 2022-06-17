package com.android.systemui.classifier;

import java.util.function.Consumer;

public final /* synthetic */ class BrightLineFalsingManager$$ExternalSyntheticLambda1 implements Consumer {
    public static final /* synthetic */ BrightLineFalsingManager$$ExternalSyntheticLambda1 INSTANCE = new BrightLineFalsingManager$$ExternalSyntheticLambda1();

    private /* synthetic */ BrightLineFalsingManager$$ExternalSyntheticLambda1() {
    }

    public final void accept(Object obj) {
        ((FalsingClassifier) obj).cleanup();
    }
}
