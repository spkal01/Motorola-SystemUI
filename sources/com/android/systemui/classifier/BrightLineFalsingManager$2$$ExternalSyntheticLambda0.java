package com.android.systemui.classifier;

import com.android.systemui.plugins.FalsingManager;
import java.util.function.Consumer;

public final /* synthetic */ class BrightLineFalsingManager$2$$ExternalSyntheticLambda0 implements Consumer {
    public static final /* synthetic */ BrightLineFalsingManager$2$$ExternalSyntheticLambda0 INSTANCE = new BrightLineFalsingManager$2$$ExternalSyntheticLambda0();

    private /* synthetic */ BrightLineFalsingManager$2$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj) {
        ((FalsingManager.FalsingBeliefListener) obj).onFalse();
    }
}
