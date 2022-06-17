package com.android.systemui.util;

import android.view.View;
import android.view.ViewGroup;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

/* compiled from: ConvenienceExtensions.kt */
public final class ConvenienceExtensionsKt {
    @NotNull
    public static final Sequence<View> getChildren(@NotNull ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "<this>");
        return SequencesKt__SequenceBuilderKt.sequence(new ConvenienceExtensionsKt$children$1(viewGroup, (Continuation<? super ConvenienceExtensionsKt$children$1>) null));
    }

    @NotNull
    public static final <T> Sequence<T> takeUntil(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, Boolean> function1) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(function1, "pred");
        return SequencesKt__SequenceBuilderKt.sequence(new ConvenienceExtensionsKt$takeUntil$1(sequence, function1, (Continuation<? super ConvenienceExtensionsKt$takeUntil$1>) null));
    }
}
