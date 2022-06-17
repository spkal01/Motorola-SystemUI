package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotifEvent.kt */
public final class EntryRemovedEvent extends NotifEvent {
    @NotNull
    private final NotificationEntry entry;
    private final int reason;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntryRemovedEvent)) {
            return false;
        }
        EntryRemovedEvent entryRemovedEvent = (EntryRemovedEvent) obj;
        return Intrinsics.areEqual((Object) this.entry, (Object) entryRemovedEvent.entry) && this.reason == entryRemovedEvent.reason;
    }

    public int hashCode() {
        return (this.entry.hashCode() * 31) + Integer.hashCode(this.reason);
    }

    @NotNull
    public String toString() {
        return "EntryRemovedEvent(entry=" + this.entry + ", reason=" + this.reason + ')';
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public EntryRemovedEvent(@NotNull NotificationEntry notificationEntry, int i) {
        super((DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        this.entry = notificationEntry;
        this.reason = i;
    }

    public void dispatchToListener(@NotNull NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkNotNullParameter(notifCollectionListener, "listener");
        notifCollectionListener.onEntryRemoved(this.entry, this.reason);
    }
}
