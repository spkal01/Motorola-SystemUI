package com.android.systemui.controls.management;

import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsFavoritingActivity.kt */
final class ControlsFavoritingActivity$loadControls$1$2 implements Consumer<Runnable> {
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$loadControls$1$2(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
    }

    public final void accept(@NotNull Runnable runnable) {
        Intrinsics.checkNotNullParameter(runnable, "runnable");
        this.this$0.cancelLoadRunnable = runnable;
    }
}
