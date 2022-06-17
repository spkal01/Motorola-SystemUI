package com.android.systemui.controls.management;

import android.content.ComponentName;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsProviderSelectorActivity.kt */
/* synthetic */ class ControlsProviderSelectorActivity$onStart$1 extends FunctionReferenceImpl implements Function1<ComponentName, Unit> {
    ControlsProviderSelectorActivity$onStart$1(ControlsProviderSelectorActivity controlsProviderSelectorActivity) {
        super(1, controlsProviderSelectorActivity, ControlsProviderSelectorActivity.class, "launchFavoritingActivity", "launchFavoritingActivity(Landroid/content/ComponentName;)V", 0);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((ComponentName) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(@Nullable ComponentName componentName) {
        ((ControlsProviderSelectorActivity) this.receiver).launchFavoritingActivity(componentName);
    }
}
