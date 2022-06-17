package com.android.systemui.statusbar.notification.collection.coalescer;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: CoalescedEvent.kt */
public final class CoalescedEvent {
    @Nullable
    private EventBatch batch;
    @NotNull
    private final String key;
    private int position;
    @NotNull
    private NotificationListenerService.Ranking ranking;
    @NotNull
    private StatusBarNotification sbn;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CoalescedEvent)) {
            return false;
        }
        CoalescedEvent coalescedEvent = (CoalescedEvent) obj;
        return Intrinsics.areEqual((Object) this.key, (Object) coalescedEvent.key) && this.position == coalescedEvent.position && Intrinsics.areEqual((Object) this.sbn, (Object) coalescedEvent.sbn) && Intrinsics.areEqual((Object) this.ranking, (Object) coalescedEvent.ranking) && Intrinsics.areEqual((Object) this.batch, (Object) coalescedEvent.batch);
    }

    public int hashCode() {
        int hashCode = ((((((this.key.hashCode() * 31) + Integer.hashCode(this.position)) * 31) + this.sbn.hashCode()) * 31) + this.ranking.hashCode()) * 31;
        EventBatch eventBatch = this.batch;
        return hashCode + (eventBatch == null ? 0 : eventBatch.hashCode());
    }

    public CoalescedEvent(@NotNull String str, int i, @NotNull StatusBarNotification statusBarNotification, @NotNull NotificationListenerService.Ranking ranking2, @Nullable EventBatch eventBatch) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(statusBarNotification, "sbn");
        Intrinsics.checkNotNullParameter(ranking2, "ranking");
        this.key = str;
        this.position = i;
        this.sbn = statusBarNotification;
        this.ranking = ranking2;
        this.batch = eventBatch;
    }

    @NotNull
    public final String getKey() {
        return this.key;
    }

    public final int getPosition() {
        return this.position;
    }

    @NotNull
    public final StatusBarNotification getSbn() {
        return this.sbn;
    }

    @NotNull
    public final NotificationListenerService.Ranking getRanking() {
        return this.ranking;
    }

    public final void setRanking(@NotNull NotificationListenerService.Ranking ranking2) {
        Intrinsics.checkNotNullParameter(ranking2, "<set-?>");
        this.ranking = ranking2;
    }

    @Nullable
    public final EventBatch getBatch() {
        return this.batch;
    }

    public final void setBatch(@Nullable EventBatch eventBatch) {
        this.batch = eventBatch;
    }

    @NotNull
    public String toString() {
        return "CoalescedEvent(key=" + this.key + ')';
    }
}
