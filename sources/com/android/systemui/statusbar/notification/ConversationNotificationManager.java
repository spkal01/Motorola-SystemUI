package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.widget.ConversationLayout;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ConversationNotifications.kt */
public final class ConversationNotificationManager {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final Context context;
    /* access modifiers changed from: private */
    @NotNull
    public final Handler mainHandler;
    /* access modifiers changed from: private */
    public boolean notifPanelCollapsed = true;
    /* access modifiers changed from: private */
    @NotNull
    public final NotificationEntryManager notificationEntryManager;
    /* access modifiers changed from: private */
    @NotNull
    public final NotificationGroupManagerLegacy notificationGroupManager;
    /* access modifiers changed from: private */
    @NotNull
    public final ConcurrentHashMap<String, ConversationState> states = new ConcurrentHashMap<>();

    public ConversationNotificationManager(@NotNull NotificationEntryManager notificationEntryManager2, @NotNull NotificationGroupManagerLegacy notificationGroupManagerLegacy, @NotNull Context context2, @NotNull Handler handler) {
        Intrinsics.checkNotNullParameter(notificationEntryManager2, "notificationEntryManager");
        Intrinsics.checkNotNullParameter(notificationGroupManagerLegacy, "notificationGroupManager");
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(handler, "mainHandler");
        this.notificationEntryManager = notificationEntryManager2;
        this.notificationGroupManager = notificationGroupManagerLegacy;
        this.context = context2;
        this.mainHandler = handler;
        notificationEntryManager2.addNotificationEntryListener(new NotificationEntryListener(this) {
            final /* synthetic */ ConversationNotificationManager this$0;

            {
                this.this$0 = r1;
            }

            /* access modifiers changed from: private */
            public static final Sequence<View> onNotificationRankingUpdated$getLayouts(NotificationContentView notificationContentView) {
                return SequencesKt__SequencesKt.sequenceOf(notificationContentView.getContractedChild(), notificationContentView.getExpandedChild(), notificationContentView.getHeadsUpChild());
            }

            /* JADX WARNING: Code restructure failed: missing block: B:10:0x005e, code lost:
                r4 = r4.getLayouts();
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onNotificationRankingUpdated(@org.jetbrains.annotations.NotNull android.service.notification.NotificationListenerService.RankingMap r14) {
                /*
                    r13 = this;
                    java.lang.String r0 = "rankingMap"
                    kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r14, r0)
                    android.service.notification.NotificationListenerService$Ranking r0 = new android.service.notification.NotificationListenerService$Ranking
                    r0.<init>()
                    com.android.systemui.statusbar.notification.ConversationNotificationManager r1 = r13.this$0
                    java.util.concurrent.ConcurrentHashMap r1 = r1.states
                    java.util.Set r1 = r1.keySet()
                    java.lang.String r2 = "states.keys"
                    kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r2)
                    kotlin.sequences.Sequence r1 = kotlin.collections.CollectionsKt___CollectionsKt.asSequence(r1)
                    com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$activeConversationEntries$1 r2 = new com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$activeConversationEntries$1
                    com.android.systemui.statusbar.notification.ConversationNotificationManager r3 = r13.this$0
                    r2.<init>(r3)
                    kotlin.sequences.Sequence r1 = kotlin.sequences.SequencesKt___SequencesKt.mapNotNull(r1, r2)
                    java.util.Iterator r1 = r1.iterator()
                L_0x002d:
                    boolean r2 = r1.hasNext()
                    if (r2 == 0) goto L_0x00c9
                    java.lang.Object r2 = r1.next()
                    com.android.systemui.statusbar.notification.collection.NotificationEntry r2 = (com.android.systemui.statusbar.notification.collection.NotificationEntry) r2
                    android.service.notification.StatusBarNotification r3 = r2.getSbn()
                    java.lang.String r3 = r3.getKey()
                    boolean r3 = r14.getRanking(r3, r0)
                    if (r3 == 0) goto L_0x002d
                    boolean r3 = r0.isConversation()
                    if (r3 == 0) goto L_0x002d
                    android.app.NotificationChannel r3 = r0.getChannel()
                    boolean r3 = r3.isImportantConversation()
                    com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r4 = r2.getRow()
                    r5 = 0
                    if (r4 != 0) goto L_0x005e
                L_0x005c:
                    r4 = r5
                    goto L_0x0069
                L_0x005e:
                    com.android.systemui.statusbar.notification.row.NotificationContentView[] r4 = r4.getLayouts()
                    if (r4 != 0) goto L_0x0065
                    goto L_0x005c
                L_0x0065:
                    kotlin.sequences.Sequence r4 = kotlin.collections.ArraysKt___ArraysKt.asSequence(r4)
                L_0x0069:
                    if (r4 != 0) goto L_0x006c
                    goto L_0x007b
                L_0x006c:
                    com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$1 r6 = com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$1.INSTANCE
                    kotlin.sequences.Sequence r4 = kotlin.sequences.SequencesKt___SequencesKt.flatMap(r4, r6)
                    if (r4 != 0) goto L_0x0075
                    goto L_0x007b
                L_0x0075:
                    com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$2 r5 = com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$2.INSTANCE
                    kotlin.sequences.Sequence r5 = kotlin.sequences.SequencesKt___SequencesKt.mapNotNull(r4, r5)
                L_0x007b:
                    r4 = 0
                    if (r5 != 0) goto L_0x007f
                    goto L_0x00bc
                L_0x007f:
                    com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$3 r6 = new com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$3
                    r6.<init>(r3)
                    kotlin.sequences.Sequence r5 = kotlin.sequences.SequencesKt___SequencesKt.filterNot(r5, r6)
                    if (r5 != 0) goto L_0x008b
                    goto L_0x00bc
                L_0x008b:
                    com.android.systemui.statusbar.notification.ConversationNotificationManager r6 = r13.this$0
                    java.util.Iterator r5 = r5.iterator()
                    r7 = r4
                L_0x0092:
                    boolean r8 = r5.hasNext()
                    if (r8 == 0) goto L_0x00bb
                    java.lang.Object r7 = r5.next()
                    com.android.internal.widget.ConversationLayout r7 = (com.android.internal.widget.ConversationLayout) r7
                    r8 = 1
                    if (r3 == 0) goto L_0x00b6
                    boolean r9 = r2.isMarkedForUserTriggeredMovement()
                    if (r9 == 0) goto L_0x00b6
                    android.os.Handler r9 = r6.mainHandler
                    com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$4$1 r10 = new com.android.systemui.statusbar.notification.ConversationNotificationManager$1$onNotificationRankingUpdated$4$1
                    r10.<init>(r7, r3)
                    r11 = 960(0x3c0, double:4.743E-321)
                    r9.postDelayed(r10, r11)
                    goto L_0x00b9
                L_0x00b6:
                    r7.setIsImportantConversation(r3, r4)
                L_0x00b9:
                    r7 = r8
                    goto L_0x0092
                L_0x00bb:
                    r4 = r7
                L_0x00bc:
                    if (r4 == 0) goto L_0x002d
                    com.android.systemui.statusbar.notification.ConversationNotificationManager r3 = r13.this$0
                    com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy r3 = r3.notificationGroupManager
                    r3.updateIsolation(r2)
                    goto L_0x002d
                L_0x00c9:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.ConversationNotificationManager.C15121.onNotificationRankingUpdated(android.service.notification.NotificationListenerService$RankingMap):void");
            }

            public void onEntryInflated(@NotNull NotificationEntry notificationEntry) {
                Intrinsics.checkNotNullParameter(notificationEntry, "entry");
                if (notificationEntry.getRanking().isConversation()) {
                    ExpandableNotificationRow row = notificationEntry.getRow();
                    if (row != null) {
                        row.setOnExpansionChangedListener(new ConversationNotificationManager$1$onEntryInflated$1(notificationEntry, this.this$0));
                    }
                    ConversationNotificationManager conversationNotificationManager = this.this$0;
                    ExpandableNotificationRow row2 = notificationEntry.getRow();
                    onEntryInflated$updateCount(conversationNotificationManager, notificationEntry, Intrinsics.areEqual((Object) row2 == null ? null : Boolean.valueOf(row2.isExpanded()), (Object) Boolean.TRUE));
                }
            }

            /* access modifiers changed from: private */
            public static final void onEntryInflated$updateCount(ConversationNotificationManager conversationNotificationManager, NotificationEntry notificationEntry, boolean z) {
                if (!z) {
                    return;
                }
                if (!conversationNotificationManager.notifPanelCollapsed || notificationEntry.isPinnedAndExpanded()) {
                    String key = notificationEntry.getKey();
                    Intrinsics.checkNotNullExpressionValue(key, "entry.key");
                    conversationNotificationManager.resetCount(key);
                    ExpandableNotificationRow row = notificationEntry.getRow();
                    if (row != null) {
                        conversationNotificationManager.resetBadgeUi(row);
                    }
                }
            }

            public void onEntryReinflated(@NotNull NotificationEntry notificationEntry) {
                Intrinsics.checkNotNullParameter(notificationEntry, "entry");
                onEntryInflated(notificationEntry);
            }

            public void onEntryRemoved(@NotNull NotificationEntry notificationEntry, @Nullable NotificationVisibility notificationVisibility, boolean z, int i) {
                Intrinsics.checkNotNullParameter(notificationEntry, "entry");
                this.this$0.removeTrackedEntry(notificationEntry);
            }
        });
    }

    /* access modifiers changed from: private */
    public final boolean shouldIncrementUnread(ConversationState conversationState, Notification.Builder builder) {
        if ((conversationState.getNotification().flags & 8) != 0) {
            return false;
        }
        return Notification.areStyledNotificationsVisiblyDifferent(Notification.Builder.recoverBuilder(this.context, conversationState.getNotification()), builder);
    }

    public final int getUnreadCount(@NotNull NotificationEntry notificationEntry, @NotNull Notification.Builder builder) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        Intrinsics.checkNotNullParameter(builder, "recoveredBuilder");
        ConversationState compute = this.states.compute(notificationEntry.getKey(), new ConversationNotificationManager$getUnreadCount$1(notificationEntry, this, builder));
        Intrinsics.checkNotNull(compute);
        return compute.getUnreadCount();
    }

    public final void onNotificationPanelExpandStateChanged(boolean z) {
        this.notifPanelCollapsed = z;
        if (!z) {
            Map<K, V> map = MapsKt__MapsKt.toMap(SequencesKt___SequencesKt.mapNotNull(MapsKt___MapsKt.asSequence(this.states), new C1518x7388b338(this)));
            this.states.replaceAll(new C1516x5e24d3bf(map));
            for (R resetBadgeUi : SequencesKt___SequencesKt.mapNotNull(CollectionsKt___CollectionsKt.asSequence(map.values()), C1517x5e24d3c0.INSTANCE)) {
                resetBadgeUi(resetBadgeUi);
            }
        }
    }

    /* access modifiers changed from: private */
    public final void resetCount(String str) {
        this.states.compute(str, ConversationNotificationManager$resetCount$1.INSTANCE);
    }

    /* access modifiers changed from: private */
    public final void removeTrackedEntry(NotificationEntry notificationEntry) {
        this.states.remove(notificationEntry.getKey());
    }

    /* access modifiers changed from: private */
    public final void resetBadgeUi(ExpandableNotificationRow expandableNotificationRow) {
        NotificationContentView[] layouts = expandableNotificationRow.getLayouts();
        Sequence asSequence = layouts == null ? null : ArraysKt___ArraysKt.asSequence(layouts);
        if (asSequence == null) {
            asSequence = SequencesKt__SequencesKt.emptySequence();
        }
        for (ConversationLayout unreadCount : SequencesKt___SequencesKt.mapNotNull(SequencesKt___SequencesKt.flatMap(asSequence, ConversationNotificationManager$resetBadgeUi$1.INSTANCE), ConversationNotificationManager$resetBadgeUi$2.INSTANCE)) {
            unreadCount.setUnreadCount(0);
        }
    }

    /* compiled from: ConversationNotifications.kt */
    private static final class ConversationState {
        @NotNull
        private final Notification notification;
        private final int unreadCount;

        public static /* synthetic */ ConversationState copy$default(ConversationState conversationState, int i, Notification notification2, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                i = conversationState.unreadCount;
            }
            if ((i2 & 2) != 0) {
                notification2 = conversationState.notification;
            }
            return conversationState.copy(i, notification2);
        }

        @NotNull
        public final ConversationState copy(int i, @NotNull Notification notification2) {
            Intrinsics.checkNotNullParameter(notification2, "notification");
            return new ConversationState(i, notification2);
        }

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ConversationState)) {
                return false;
            }
            ConversationState conversationState = (ConversationState) obj;
            return this.unreadCount == conversationState.unreadCount && Intrinsics.areEqual((Object) this.notification, (Object) conversationState.notification);
        }

        public int hashCode() {
            return (Integer.hashCode(this.unreadCount) * 31) + this.notification.hashCode();
        }

        @NotNull
        public String toString() {
            return "ConversationState(unreadCount=" + this.unreadCount + ", notification=" + this.notification + ')';
        }

        public ConversationState(int i, @NotNull Notification notification2) {
            Intrinsics.checkNotNullParameter(notification2, "notification");
            this.unreadCount = i;
            this.notification = notification2;
        }

        @NotNull
        public final Notification getNotification() {
            return this.notification;
        }

        public final int getUnreadCount() {
            return this.unreadCount;
        }
    }

    /* compiled from: ConversationNotifications.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
