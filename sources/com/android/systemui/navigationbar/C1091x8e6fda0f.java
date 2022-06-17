package com.android.systemui.navigationbar;

import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.util.function.Function;

/* renamed from: com.android.systemui.navigationbar.RotationButtonController$TaskStackListenerImpl$$ExternalSyntheticLambda1 */
public final /* synthetic */ class C1091x8e6fda0f implements Function {
    public static final /* synthetic */ C1091x8e6fda0f INSTANCE = new C1091x8e6fda0f();

    private /* synthetic */ C1091x8e6fda0f() {
    }

    public final Object apply(Object obj) {
        return ((ActivityManagerWrapper) obj).getRunningTask();
    }
}
