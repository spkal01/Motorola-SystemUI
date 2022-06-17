package com.android.systemui.classifier;

import java.util.function.Consumer;

public final /* synthetic */ class BrightLineFalsingManager$1$$ExternalSyntheticLambda1 implements Consumer {
    public static final /* synthetic */ BrightLineFalsingManager$1$$ExternalSyntheticLambda1 INSTANCE = new BrightLineFalsingManager$1$$ExternalSyntheticLambda1();

    private /* synthetic */ BrightLineFalsingManager$1$$ExternalSyntheticLambda1() {
    }

    public final void accept(Object obj) {
        ((FalsingClassifier) obj).onSessionStarted();
    }
}
