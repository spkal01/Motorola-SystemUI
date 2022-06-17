package com.android.systemui.assist;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Function;

public final /* synthetic */ class PhoneStateMonitor$$ExternalSyntheticLambda1 implements Function {
    public static final /* synthetic */ PhoneStateMonitor$$ExternalSyntheticLambda1 INSTANCE = new PhoneStateMonitor$$ExternalSyntheticLambda1();

    private /* synthetic */ PhoneStateMonitor$$ExternalSyntheticLambda1() {
    }

    public final Object apply(Object obj) {
        return Boolean.valueOf(((StatusBar) ((Lazy) obj).get()).isBouncerShowing());
    }
}
