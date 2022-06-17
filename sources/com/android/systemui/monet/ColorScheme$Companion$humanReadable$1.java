package com.android.systemui.monet;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ColorScheme.kt */
final class ColorScheme$Companion$humanReadable$1 extends Lambda implements Function1<Integer, CharSequence> {
    public static final ColorScheme$Companion$humanReadable$1 INSTANCE = new ColorScheme$Companion$humanReadable$1();

    ColorScheme$Companion$humanReadable$1() {
        super(1);
    }

    @NotNull
    public final CharSequence invoke(int i) {
        return Intrinsics.stringPlus("#", Integer.toHexString(i));
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return invoke(((Number) obj).intValue());
    }
}
