package com.android.systemui.statusbar.events;

import android.content.Context;
import android.view.View;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: StatusEvent.kt */
public interface StatusEvent {

    /* compiled from: StatusEvent.kt */
    public static final class DefaultImpls {
        public static boolean shouldUpdateFromEvent(@NotNull StatusEvent statusEvent, @Nullable StatusEvent statusEvent2) {
            Intrinsics.checkNotNullParameter(statusEvent, "this");
            return false;
        }

        public static void updateFromEvent(@NotNull StatusEvent statusEvent, @Nullable StatusEvent statusEvent2) {
            Intrinsics.checkNotNullParameter(statusEvent, "this");
        }
    }

    @Nullable
    String getContentDescription();

    boolean getForceVisible();

    int getPriority();

    boolean getShowAnimation();

    @NotNull
    Function1<Context, View> getViewCreator();

    boolean shouldUpdateFromEvent(@Nullable StatusEvent statusEvent);

    void updateFromEvent(@Nullable StatusEvent statusEvent);
}
