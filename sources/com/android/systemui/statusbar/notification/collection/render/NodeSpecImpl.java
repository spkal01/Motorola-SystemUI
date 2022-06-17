package com.android.systemui.statusbar.notification.collection.render;

import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NodeController.kt */
public final class NodeSpecImpl implements NodeSpec {
    @NotNull
    private final List<NodeSpec> children = new ArrayList();
    @NotNull
    private final NodeController controller;
    @Nullable
    private final NodeSpec parent;

    public NodeSpecImpl(@Nullable NodeSpec nodeSpec, @NotNull NodeController nodeController) {
        Intrinsics.checkNotNullParameter(nodeController, "controller");
        this.parent = nodeSpec;
        this.controller = nodeController;
    }

    @Nullable
    public NodeSpec getParent() {
        return this.parent;
    }

    @NotNull
    public NodeController getController() {
        return this.controller;
    }

    @NotNull
    public List<NodeSpec> getChildren() {
        return this.children;
    }
}
