package com.android.systemui.tuner;

import com.android.systemui.fragments.FragmentService;
import java.util.function.Consumer;

public final /* synthetic */ class TunerActivity$$ExternalSyntheticLambda0 implements Consumer {
    public static final /* synthetic */ TunerActivity$$ExternalSyntheticLambda0 INSTANCE = new TunerActivity$$ExternalSyntheticLambda0();

    private /* synthetic */ TunerActivity$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj) {
        ((FragmentService) obj).destroyAll();
    }
}
