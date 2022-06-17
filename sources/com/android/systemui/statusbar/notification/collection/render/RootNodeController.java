package com.android.systemui.statusbar.notification.collection.render;

import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: RootNodeController.kt */
public final class RootNodeController implements NodeController {
    @NotNull
    private final NotificationListContainer listContainer;
    @NotNull
    private final String nodeLabel = "<root>";
    @NotNull
    private final View view;

    public RootNodeController(@NotNull NotificationListContainer notificationListContainer, @NotNull View view2) {
        Intrinsics.checkNotNullParameter(notificationListContainer, "listContainer");
        Intrinsics.checkNotNullParameter(view2, "view");
        this.listContainer = notificationListContainer;
        this.view = view2;
    }

    @NotNull
    public View getView() {
        return this.view;
    }

    @NotNull
    public String getNodeLabel() {
        return this.nodeLabel;
    }

    @Nullable
    public View getChildAt(int i) {
        return this.listContainer.getContainerChildAt(i);
    }

    public int getChildCount() {
        return this.listContainer.getContainerChildCount();
    }

    public void addChildAt(@NotNull NodeController nodeController, int i) {
        Intrinsics.checkNotNullParameter(nodeController, "child");
        this.listContainer.addContainerViewAt(nodeController.getView(), i);
    }

    public void moveChildTo(@NotNull NodeController nodeController, int i) {
        Intrinsics.checkNotNullParameter(nodeController, "child");
        this.listContainer.changeViewPosition((ExpandableView) nodeController.getView(), i);
    }

    public void removeChild(@NotNull NodeController nodeController, boolean z) {
        Intrinsics.checkNotNullParameter(nodeController, "child");
        if (z) {
            this.listContainer.setChildTransferInProgress(true);
        }
        this.listContainer.removeContainerView(nodeController.getView());
        if (z) {
            this.listContainer.setChildTransferInProgress(false);
        }
    }
}
