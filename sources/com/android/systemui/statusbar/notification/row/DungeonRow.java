package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: DungeonRow.kt */
public final class DungeonRow extends LinearLayout {
    @Nullable
    private NotificationEntry entry;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DungeonRow(@NotNull Context context, @NotNull AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(attributeSet, "attrs");
    }

    @Nullable
    public final NotificationEntry getEntry() {
        return this.entry;
    }

    public final void setEntry(@Nullable NotificationEntry notificationEntry) {
        this.entry = notificationEntry;
        update();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0016, code lost:
        r1 = r1.getRow();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void update() {
        /*
            r3 = this;
            int r0 = com.android.systemui.R$id.app_name
            android.view.View r0 = r3.findViewById(r0)
            java.lang.String r1 = "null cannot be cast to non-null type android.widget.TextView"
            java.util.Objects.requireNonNull(r0, r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            com.android.systemui.statusbar.notification.collection.NotificationEntry r1 = r3.getEntry()
            r2 = 0
            if (r1 != 0) goto L_0x0016
        L_0x0014:
            r1 = r2
            goto L_0x0021
        L_0x0016:
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r1 = r1.getRow()
            if (r1 != 0) goto L_0x001d
            goto L_0x0014
        L_0x001d:
            java.lang.String r1 = r1.getAppName()
        L_0x0021:
            r0.setText(r1)
            int r0 = com.android.systemui.R$id.icon
            android.view.View r0 = r3.findViewById(r0)
            java.lang.String r1 = "null cannot be cast to non-null type com.android.systemui.statusbar.StatusBarIconView"
            java.util.Objects.requireNonNull(r0, r1)
            com.android.systemui.statusbar.StatusBarIconView r0 = (com.android.systemui.statusbar.StatusBarIconView) r0
            com.android.systemui.statusbar.notification.collection.NotificationEntry r3 = r3.getEntry()
            if (r3 != 0) goto L_0x0039
            r3 = r2
            goto L_0x003d
        L_0x0039:
            com.android.systemui.statusbar.notification.icon.IconPack r3 = r3.getIcons()
        L_0x003d:
            if (r3 != 0) goto L_0x0040
            goto L_0x004b
        L_0x0040:
            com.android.systemui.statusbar.StatusBarIconView r3 = r3.getStatusBarIcon()
            if (r3 != 0) goto L_0x0047
            goto L_0x004b
        L_0x0047:
            com.android.internal.statusbar.StatusBarIcon r2 = r3.getStatusBarIcon()
        L_0x004b:
            r0.set(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.DungeonRow.update():void");
    }
}
