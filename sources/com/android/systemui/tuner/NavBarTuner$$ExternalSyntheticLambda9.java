package com.android.systemui.tuner;

import com.android.systemui.Dependency;
import com.android.systemui.tuner.TunerService;
import java.util.function.Consumer;

public final /* synthetic */ class NavBarTuner$$ExternalSyntheticLambda9 implements Consumer {
    public static final /* synthetic */ NavBarTuner$$ExternalSyntheticLambda9 INSTANCE = new NavBarTuner$$ExternalSyntheticLambda9();

    private /* synthetic */ NavBarTuner$$ExternalSyntheticLambda9() {
    }

    public final void accept(Object obj) {
        ((TunerService) Dependency.get(TunerService.class)).removeTunable((TunerService.Tunable) obj);
    }
}
