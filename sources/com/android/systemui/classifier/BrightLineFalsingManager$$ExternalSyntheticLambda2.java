package com.android.systemui.classifier;

import com.android.systemui.plugins.FalsingManager;
import java.util.function.Consumer;

public final /* synthetic */ class BrightLineFalsingManager$$ExternalSyntheticLambda2 implements Consumer {
    public static final /* synthetic */ BrightLineFalsingManager$$ExternalSyntheticLambda2 INSTANCE = new BrightLineFalsingManager$$ExternalSyntheticLambda2();

    private /* synthetic */ BrightLineFalsingManager$$ExternalSyntheticLambda2() {
    }

    public final void accept(Object obj) {
        ((FalsingManager.FalsingTapListener) obj).onDoubleTapRequired();
    }
}
