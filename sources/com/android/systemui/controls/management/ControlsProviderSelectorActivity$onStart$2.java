package com.android.systemui.controls.management;

import android.content.ComponentName;
import com.android.systemui.controls.controller.ControlsController;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsProviderSelectorActivity.kt */
/* synthetic */ class ControlsProviderSelectorActivity$onStart$2 extends FunctionReferenceImpl implements Function1<ComponentName, Integer> {
    ControlsProviderSelectorActivity$onStart$2(ControlsController controlsController) {
        super(1, controlsController, ControlsController.class, "countFavoritesForComponent", "countFavoritesForComponent(Landroid/content/ComponentName;)I", 0);
    }

    public final int invoke(@NotNull ComponentName componentName) {
        Intrinsics.checkNotNullParameter(componentName, "p0");
        return ((ControlsController) this.receiver).countFavoritesForComponent(componentName);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Integer.valueOf(invoke((ComponentName) obj));
    }
}
