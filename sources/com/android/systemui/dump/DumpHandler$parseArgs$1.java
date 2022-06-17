package com.android.systemui.dump;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: DumpHandler.kt */
final class DumpHandler$parseArgs$1 extends Lambda implements Function1<String, String> {
    public static final DumpHandler$parseArgs$1 INSTANCE = new DumpHandler$parseArgs$1();

    DumpHandler$parseArgs$1() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "it");
        if (ArraysKt___ArraysKt.contains((T[]) DumpHandlerKt.PRIORITY_OPTIONS, str)) {
            return str;
        }
        throw new IllegalArgumentException();
    }
}
