package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.ExtensionControllerImpl;
import java.util.function.ToIntFunction;

/* renamed from: com.android.systemui.statusbar.policy.ExtensionControllerImpl$ExtensionBuilder$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2004xb62966b4 implements ToIntFunction {
    public static final /* synthetic */ C2004xb62966b4 INSTANCE = new C2004xb62966b4();

    private /* synthetic */ C2004xb62966b4() {
    }

    public final int applyAsInt(Object obj) {
        return ((ExtensionControllerImpl.Item) obj).sortOrder();
    }
}
