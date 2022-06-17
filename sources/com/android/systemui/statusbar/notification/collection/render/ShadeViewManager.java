package com.android.systemui.statusbar.notification.collection.render;

import android.content.Context;
import android.view.View;
import com.android.systemui.statusbar.notification.collection.GroupEntry;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder;
import com.android.systemui.statusbar.notification.collection.listbuilder.NotifSection;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import java.util.List;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeViewManager.kt */
public final class ShadeViewManager {
    @NotNull
    private final NotificationIconAreaController notificationIconAreaController;
    @NotNull
    private final RootNodeController rootController;
    @NotNull
    private final NotifViewBarn viewBarn;
    @NotNull
    private final ShadeViewDiffer viewDiffer;

    public ShadeViewManager(@NotNull Context context, @NotNull NotificationListContainer notificationListContainer, @NotNull ShadeViewDifferLogger shadeViewDifferLogger, @NotNull NotifViewBarn notifViewBarn, @NotNull NotificationIconAreaController notificationIconAreaController2) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(notificationListContainer, "listContainer");
        Intrinsics.checkNotNullParameter(shadeViewDifferLogger, "logger");
        Intrinsics.checkNotNullParameter(notifViewBarn, "viewBarn");
        Intrinsics.checkNotNullParameter(notificationIconAreaController2, "notificationIconAreaController");
        this.viewBarn = notifViewBarn;
        this.notificationIconAreaController = notificationIconAreaController2;
        RootNodeController rootNodeController = new RootNodeController(notificationListContainer, new View(context));
        this.rootController = rootNodeController;
        this.viewDiffer = new ShadeViewDiffer(rootNodeController, shadeViewDifferLogger);
    }

    public final void attach(@NotNull ShadeListBuilder shadeListBuilder) {
        Intrinsics.checkNotNullParameter(shadeListBuilder, "listBuilder");
        shadeListBuilder.setOnRenderListListener(new ShadeViewManager$attach$1(this));
    }

    /* access modifiers changed from: private */
    public final void onNewNotifTree(List<? extends ListEntry> list) {
        this.viewDiffer.applySpec(buildTree(list));
    }

    private final NodeSpec buildTree(List<? extends ListEntry> list) {
        NodeController headerController;
        NodeController headerController2;
        NodeSpecImpl nodeSpecImpl = new NodeSpecImpl((NodeSpec) null, this.rootController);
        ListEntry listEntry = (ListEntry) CollectionsKt___CollectionsKt.firstOrNull(list);
        NotifSection section = listEntry == null ? null : listEntry.getSection();
        if (!(section == null || (headerController2 = section.getHeaderController()) == null)) {
            nodeSpecImpl.getChildren().add(new NodeSpecImpl(nodeSpecImpl, headerController2));
        }
        for (Pair next : SequencesKt___SequencesKt.zipWithNext(CollectionsKt___CollectionsKt.asSequence(list))) {
            ListEntry listEntry2 = (ListEntry) next.component2();
            NotifSection section2 = listEntry2.getSection();
            if (!(!Intrinsics.areEqual((Object) section2, (Object) ((ListEntry) next.component1()).getSection()))) {
                section2 = null;
            }
            if (!(section2 == null || (headerController = section2.getHeaderController()) == null)) {
                nodeSpecImpl.getChildren().add(new NodeSpecImpl(nodeSpecImpl, headerController));
            }
            nodeSpecImpl.getChildren().add(buildNotifNode(listEntry2, nodeSpecImpl));
        }
        this.notificationIconAreaController.updateNotificationIcons(list);
        return nodeSpecImpl;
    }

    private final NodeSpec buildNotifNode(ListEntry listEntry, NodeSpec nodeSpec) {
        if (listEntry instanceof NotificationEntry) {
            return new NodeSpecImpl(nodeSpec, this.viewBarn.requireView(listEntry));
        }
        if (listEntry instanceof GroupEntry) {
            NotifViewBarn notifViewBarn = this.viewBarn;
            GroupEntry groupEntry = (GroupEntry) listEntry;
            NotificationEntry summary = groupEntry.getSummary();
            if (summary != null) {
                NodeSpecImpl nodeSpecImpl = new NodeSpecImpl(nodeSpec, notifViewBarn.requireView(summary));
                List<NotificationEntry> children = groupEntry.getChildren();
                Intrinsics.checkNotNullExpressionValue(children, "entry.children");
                for (NotificationEntry notificationEntry : children) {
                    List<NodeSpec> children2 = nodeSpecImpl.getChildren();
                    Intrinsics.checkNotNullExpressionValue(notificationEntry, "it");
                    children2.add(buildNotifNode(notificationEntry, nodeSpecImpl));
                }
                return nodeSpecImpl;
            }
            throw new IllegalStateException("Required value was null.".toString());
        }
        throw new RuntimeException(Intrinsics.stringPlus("Unexpected entry: ", listEntry));
    }
}
