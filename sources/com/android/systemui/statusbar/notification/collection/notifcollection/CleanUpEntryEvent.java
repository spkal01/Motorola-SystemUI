package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotifEvent.kt */
public final class CleanUpEntryEvent extends NotifEvent {
    @NotNull
    private final NotificationEntry entry;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof CleanUpEntryEvent) && Intrinsics.areEqual((Object) this.entry, (Object) ((CleanUpEntryEvent) obj).entry);
    }

    public int hashCode() {
        return this.entry.hashCode();
    }

    @NotNull
    public String toString() {
        return "CleanUpEntryEvent(entry=" + this.entry + ')';
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CleanUpEntryEvent(@NotNull NotificationEntry notificationEntry) {
        super((DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        this.entry = notificationEntry;
    }

    public void dispatchToListener(@NotNull NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkNotNullParameter(notifCollectionListener, "listener");
        notifCollectionListener.onEntryCleanUp(this.entry);
    }
}
