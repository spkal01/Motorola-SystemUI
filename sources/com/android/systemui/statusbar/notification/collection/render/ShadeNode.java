package com.android.systemui.statusbar.notification.collection.render;

import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ShadeViewDiffer.kt */
final class ShadeNode {
    @NotNull
    private final NodeController controller;
    @Nullable
    private ShadeNode parent;
    @NotNull
    private final View view;

    public ShadeNode(@NotNull NodeController nodeController) {
        Intrinsics.checkNotNullParameter(nodeController, "controller");
        this.controller = nodeController;
        this.view = nodeController.getView();
    }

    @NotNull
    public final NodeController getController() {
        return this.controller;
    }

    @NotNull
    public final View getView() {
        return this.view;
    }

    @Nullable
    public final ShadeNode getParent() {
        return this.parent;
    }

    public final void setParent(@Nullable ShadeNode shadeNode) {
        this.parent = shadeNode;
    }

    @NotNull
    public final String getLabel() {
        return this.controller.getNodeLabel();
    }

    @Nullable
    public final View getChildAt(int i) {
        return this.controller.getChildAt(i);
    }

    public final int getChildCount() {
        return this.controller.getChildCount();
    }

    public final void addChildAt(@NotNull ShadeNode shadeNode, int i) {
        Intrinsics.checkNotNullParameter(shadeNode, "child");
        this.controller.addChildAt(shadeNode.controller, i);
    }

    public final void moveChildTo(@NotNull ShadeNode shadeNode, int i) {
        Intrinsics.checkNotNullParameter(shadeNode, "child");
        this.controller.moveChildTo(shadeNode.controller, i);
    }

    public final void removeChild(@NotNull ShadeNode shadeNode, boolean z) {
        Intrinsics.checkNotNullParameter(shadeNode, "child");
        this.controller.removeChild(shadeNode.controller, z);
    }
}
