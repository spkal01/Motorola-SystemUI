package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSectioner;
import com.android.systemui.statusbar.notification.collection.render.NodeController;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ConversationCoordinator.kt */
public final class ConversationCoordinator implements Coordinator {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final ConversationCoordinator$notificationPromoter$1 notificationPromoter = new ConversationCoordinator$notificationPromoter$1();
    @NotNull
    private final PeopleNotificationIdentifier peopleNotificationIdentifier;
    @NotNull
    private final NotifSectioner sectioner;

    public ConversationCoordinator(@NotNull PeopleNotificationIdentifier peopleNotificationIdentifier2, @NotNull NodeController nodeController) {
        Intrinsics.checkNotNullParameter(peopleNotificationIdentifier2, "peopleNotificationIdentifier");
        Intrinsics.checkNotNullParameter(nodeController, "peopleHeaderController");
        this.peopleNotificationIdentifier = peopleNotificationIdentifier2;
        this.sectioner = new ConversationCoordinator$sectioner$1(this, nodeController);
    }

    @NotNull
    public final NotifSectioner getSectioner() {
        return this.sectioner;
    }

    public void attach(@NotNull NotifPipeline notifPipeline) {
        Intrinsics.checkNotNullParameter(notifPipeline, "pipeline");
        notifPipeline.addPromoter(this.notificationPromoter);
    }

    /* access modifiers changed from: private */
    public final boolean isConversation(NotificationEntry notificationEntry) {
        return this.peopleNotificationIdentifier.getPeopleNotificationType(notificationEntry) != 0;
    }

    /* compiled from: ConversationCoordinator.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
