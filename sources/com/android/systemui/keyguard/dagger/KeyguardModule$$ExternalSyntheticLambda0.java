package com.android.systemui.keyguard.dagger;

import android.hardware.face.FaceSensorPropertiesInternal;
import java.util.function.Predicate;

public final /* synthetic */ class KeyguardModule$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ KeyguardModule$$ExternalSyntheticLambda0 INSTANCE = new KeyguardModule$$ExternalSyntheticLambda0();

    private /* synthetic */ KeyguardModule$$ExternalSyntheticLambda0() {
    }

    public final boolean test(Object obj) {
        return KeyguardModule.lambda$provideFaceAuthScreenBrightnessController$0((FaceSensorPropertiesInternal) obj);
    }
}
