package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import com.motorola.systemui.cli.navgesture.ResourceObject;
import java.util.function.Function;

public final /* synthetic */ class MainThreadInitializedObject$$ExternalSyntheticLambda1 implements Function {
    public final /* synthetic */ int f$0;

    public /* synthetic */ MainThreadInitializedObject$$ExternalSyntheticLambda1(int i) {
        this.f$0 = i;
    }

    public final Object apply(Object obj) {
        return ResourceObject.Overrides.getObject((Context) obj, this.f$0);
    }
}
