package com.android.systemui.statusbar.notification.collection.render;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NodeController.kt */
public interface NodeSpec {
    @NotNull
    List<NodeSpec> getChildren();

    @NotNull
    NodeController getController();

    @Nullable
    NodeSpec getParent();
}
