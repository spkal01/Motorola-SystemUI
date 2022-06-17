package com.android.systemui.statusbar.notification.collection.notifcollection;

import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotifEvent.kt */
public final class BindEntryEvent extends NotifEvent {
    @NotNull
    private final NotificationEntry entry;
    @NotNull
    private final StatusBarNotification sbn;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BindEntryEvent)) {
            return false;
        }
        BindEntryEvent bindEntryEvent = (BindEntryEvent) obj;
        return Intrinsics.areEqual((Object) this.entry, (Object) bindEntryEvent.entry) && Intrinsics.areEqual((Object) this.sbn, (Object) bindEntryEvent.sbn);
    }

    public int hashCode() {
        return (this.entry.hashCode() * 31) + this.sbn.hashCode();
    }

    @NotNull
    public String toString() {
        return "BindEntryEvent(entry=" + this.entry + ", sbn=" + this.sbn + ')';
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public BindEntryEvent(@NotNull NotificationEntry notificationEntry, @NotNull StatusBarNotification statusBarNotification) {
        super((DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        Intrinsics.checkNotNullParameter(statusBarNotification, "sbn");
        this.entry = notificationEntry;
        this.sbn = statusBarNotification;
    }

    public void dispatchToListener(@NotNull NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkNotNullParameter(notifCollectionListener, "listener");
        notifCollectionListener.onEntryBind(this.entry, this.sbn);
    }
}
