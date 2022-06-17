package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSectioner;
import com.android.systemui.statusbar.notification.collection.render.NodeController;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ConversationCoordinator.kt */
public final class ConversationCoordinator$sectioner$1 extends NotifSectioner {
    final /* synthetic */ NodeController $peopleHeaderController;
    final /* synthetic */ ConversationCoordinator this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ConversationCoordinator$sectioner$1(ConversationCoordinator conversationCoordinator, NodeController nodeController) {
        super("People");
        this.this$0 = conversationCoordinator;
        this.$peopleHeaderController = nodeController;
    }

    public boolean isInSection(@NotNull ListEntry listEntry) {
        Intrinsics.checkNotNullParameter(listEntry, "entry");
        ConversationCoordinator conversationCoordinator = this.this$0;
        NotificationEntry representativeEntry = listEntry.getRepresentativeEntry();
        Intrinsics.checkNotNull(representativeEntry);
        return conversationCoordinator.isConversation(representativeEntry);
    }

    @NotNull
    public NodeController getHeaderNodeController() {
        return this.$peopleHeaderController;
    }
}
