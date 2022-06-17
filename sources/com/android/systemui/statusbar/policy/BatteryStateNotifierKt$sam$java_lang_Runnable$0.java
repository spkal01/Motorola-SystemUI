package com.android.systemui.statusbar.policy;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

/* compiled from: BatteryStateNotifier.kt */
final class BatteryStateNotifierKt$sam$java_lang_Runnable$0 implements Runnable {
    private final /* synthetic */ Function0 function;

    BatteryStateNotifierKt$sam$java_lang_Runnable$0(@NotNull Function0 function0) {
        this.function = function0;
    }

    public final /* synthetic */ void run() {
        this.function.invoke();
    }
}
