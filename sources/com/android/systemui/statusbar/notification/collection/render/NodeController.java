package com.android.systemui.statusbar.notification.collection.render;

import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NodeController.kt */
public interface NodeController {
    void addChildAt(@NotNull NodeController nodeController, int i);

    @Nullable
    View getChildAt(int i);

    int getChildCount();

    @NotNull
    String getNodeLabel();

    @NotNull
    View getView();

    void moveChildTo(@NotNull NodeController nodeController, int i);

    void removeChild(@NotNull NodeController nodeController, boolean z);

    /* compiled from: NodeController.kt */
    public static final class DefaultImpls {
        public static int getChildCount(@NotNull NodeController nodeController) {
            Intrinsics.checkNotNullParameter(nodeController, "this");
            return 0;
        }

        @Nullable
        public static View getChildAt(@NotNull NodeController nodeController, int i) {
            Intrinsics.checkNotNullParameter(nodeController, "this");
            throw new RuntimeException("Not supported");
        }

        public static void addChildAt(@NotNull NodeController nodeController, @NotNull NodeController nodeController2, int i) {
            Intrinsics.checkNotNullParameter(nodeController, "this");
            Intrinsics.checkNotNullParameter(nodeController2, "child");
            throw new RuntimeException("Not supported");
        }

        public static void moveChildTo(@NotNull NodeController nodeController, @NotNull NodeController nodeController2, int i) {
            Intrinsics.checkNotNullParameter(nodeController, "this");
            Intrinsics.checkNotNullParameter(nodeController2, "child");
            throw new RuntimeException("Not supported");
        }

        public static void removeChild(@NotNull NodeController nodeController, @NotNull NodeController nodeController2, boolean z) {
            Intrinsics.checkNotNullParameter(nodeController, "this");
            Intrinsics.checkNotNullParameter(nodeController2, "child");
            throw new RuntimeException("Not supported");
        }
    }
}
