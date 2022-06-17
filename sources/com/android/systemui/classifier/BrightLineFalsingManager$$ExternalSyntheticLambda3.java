package com.android.systemui.classifier;

import java.util.function.Function;

public final /* synthetic */ class BrightLineFalsingManager$$ExternalSyntheticLambda3 implements Function {
    public final /* synthetic */ BrightLineFalsingManager f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean[] f$2;

    public /* synthetic */ BrightLineFalsingManager$$ExternalSyntheticLambda3(BrightLineFalsingManager brightLineFalsingManager, int i, boolean[] zArr) {
        this.f$0 = brightLineFalsingManager;
        this.f$1 = i;
        this.f$2 = zArr;
    }

    public final Object apply(Object obj) {
        return this.f$0.lambda$isFalseTouch$0(this.f$1, this.f$2, (FalsingClassifier) obj);
    }
}
