package com.android.systemui.statusbar.notification.stack;

import android.view.View;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.DungeonRow;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ForegroundServiceSectionController.kt */
final class ForegroundServiceSectionController$update$1$2$1 implements View.OnClickListener {
    final /* synthetic */ DungeonRow $child;
    final /* synthetic */ NotificationEntry $entry;
    final /* synthetic */ ForegroundServiceSectionController this$0;

    ForegroundServiceSectionController$update$1$2$1(ForegroundServiceSectionController foregroundServiceSectionController, DungeonRow dungeonRow, NotificationEntry notificationEntry) {
        this.this$0 = foregroundServiceSectionController;
        this.$child = dungeonRow;
        this.$entry = notificationEntry;
    }

    public final void onClick(View view) {
        ForegroundServiceSectionController foregroundServiceSectionController = this.this$0;
        NotificationEntry entry = this.$child.getEntry();
        Intrinsics.checkNotNull(entry);
        foregroundServiceSectionController.removeEntry(entry);
        this.this$0.update();
        this.$entry.getRow().unDismiss();
        this.$entry.getRow().resetTranslation();
        this.this$0.getEntryManager().updateNotifications("ForegroundServiceSectionController.onClick");
    }
}
