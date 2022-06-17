package com.android.systemui.statusbar.notification;

import android.graphics.drawable.AnimatedImageDrawable;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

/* compiled from: ConversationNotifications.kt */
public final class AnimatedImageNotificationManager {
    @NotNull
    private final HeadsUpManager headsUpManager;
    /* access modifiers changed from: private */
    public boolean isStatusBarExpanded;
    /* access modifiers changed from: private */
    @NotNull
    public final NotificationEntryManager notificationEntryManager;
    @NotNull
    private final StatusBarStateController statusBarStateController;

    public AnimatedImageNotificationManager(@NotNull NotificationEntryManager notificationEntryManager2, @NotNull HeadsUpManager headsUpManager2, @NotNull StatusBarStateController statusBarStateController2) {
        Intrinsics.checkNotNullParameter(notificationEntryManager2, "notificationEntryManager");
        Intrinsics.checkNotNullParameter(headsUpManager2, "headsUpManager");
        Intrinsics.checkNotNullParameter(statusBarStateController2, "statusBarStateController");
        this.notificationEntryManager = notificationEntryManager2;
        this.headsUpManager = headsUpManager2;
        this.statusBarStateController = statusBarStateController2;
    }

    public final void bind() {
        this.headsUpManager.addListener(new AnimatedImageNotificationManager$bind$1(this));
        this.statusBarStateController.addCallback(new AnimatedImageNotificationManager$bind$2(this));
        this.notificationEntryManager.addNotificationEntryListener(new AnimatedImageNotificationManager$bind$3(this));
    }

    /* access modifiers changed from: private */
    public final void updateAnimatedImageDrawables(ExpandableNotificationRow expandableNotificationRow, boolean z) {
        NotificationContentView[] layouts = expandableNotificationRow.getLayouts();
        Sequence asSequence = layouts == null ? null : ArraysKt___ArraysKt.asSequence(layouts);
        if (asSequence == null) {
            asSequence = SequencesKt__SequencesKt.emptySequence();
        }
        for (AnimatedImageDrawable animatedImageDrawable : SequencesKt___SequencesKt.mapNotNull(SequencesKt___SequencesKt.flatMap(SequencesKt___SequencesKt.flatMap(SequencesKt___SequencesKt.flatMap(asSequence, AnimatedImageNotificationManager$updateAnimatedImageDrawables$1.INSTANCE), AnimatedImageNotificationManager$updateAnimatedImageDrawables$2.INSTANCE), AnimatedImageNotificationManager$updateAnimatedImageDrawables$3.INSTANCE), AnimatedImageNotificationManager$updateAnimatedImageDrawables$4.INSTANCE)) {
            if (z) {
                animatedImageDrawable.start();
            } else {
                animatedImageDrawable.stop();
            }
        }
    }
}
