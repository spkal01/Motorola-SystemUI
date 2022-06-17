package com.android.systemui.statusbar.notification.stack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.DungeonRow;
import com.android.systemui.util.Assert;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ForegroundServiceSectionController.kt */
public final class ForegroundServiceSectionController {
    @NotNull
    private final String TAG = "FgsSectionController";
    /* access modifiers changed from: private */
    @NotNull
    public final Set<NotificationEntry> entries = new LinkedHashSet();
    @Nullable
    private View entriesView;
    @NotNull
    private final NotificationEntryManager entryManager;
    @NotNull
    private final ForegroundServiceDismissalFeatureController featureController;

    public ForegroundServiceSectionController(@NotNull NotificationEntryManager notificationEntryManager, @NotNull ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController) {
        Intrinsics.checkNotNullParameter(notificationEntryManager, "entryManager");
        Intrinsics.checkNotNullParameter(foregroundServiceDismissalFeatureController, "featureController");
        this.entryManager = notificationEntryManager;
        this.featureController = foregroundServiceDismissalFeatureController;
        if (foregroundServiceDismissalFeatureController.isForegroundServiceDismissalEnabled()) {
            notificationEntryManager.addNotificationRemoveInterceptor(new NotificationRemoveInterceptor() {
                public final boolean onNotificationRemoveRequested(@NotNull String str, @Nullable NotificationEntry notificationEntry, int i) {
                    Intrinsics.checkNotNullParameter(str, "p0");
                    return ForegroundServiceSectionController.this.shouldInterceptRemoval(str, notificationEntry, i);
                }
            });
            notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener(this) {
                final /* synthetic */ ForegroundServiceSectionController this$0;

                {
                    this.this$0 = r1;
                }

                public void onPostEntryUpdated(@NotNull NotificationEntry notificationEntry) {
                    Intrinsics.checkNotNullParameter(notificationEntry, "entry");
                    if (this.this$0.entries.contains(notificationEntry)) {
                        this.this$0.removeEntry(notificationEntry);
                        this.this$0.addEntry(notificationEntry);
                        this.this$0.update();
                    }
                }
            });
        }
    }

    @NotNull
    public final NotificationEntryManager getEntryManager() {
        return this.entryManager;
    }

    /* access modifiers changed from: private */
    public final boolean shouldInterceptRemoval(String str, NotificationEntry notificationEntry, int i) {
        Assert.isMainThread();
        boolean z = i == 3;
        boolean z2 = i == 2 || i == 1;
        if (i != 8) {
        }
        boolean z3 = i == 12;
        if (notificationEntry == null) {
            return false;
        }
        if (z2 && !notificationEntry.getSbn().isClearable()) {
            if (!hasEntry(notificationEntry)) {
                addEntry(notificationEntry);
                update();
            }
            this.entryManager.updateNotifications("FgsSectionController.onNotificationRemoveRequested");
            return true;
        } else if ((z || z3) && !notificationEntry.getSbn().isClearable()) {
            return true;
        } else {
            if (hasEntry(notificationEntry)) {
                removeEntry(notificationEntry);
                update();
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public final void removeEntry(NotificationEntry notificationEntry) {
        Assert.isMainThread();
        this.entries.remove(notificationEntry);
    }

    /* access modifiers changed from: private */
    public final void addEntry(NotificationEntry notificationEntry) {
        Assert.isMainThread();
        this.entries.add(notificationEntry);
    }

    public final boolean hasEntry(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        Assert.isMainThread();
        return this.entries.contains(notificationEntry);
    }

    @NotNull
    public final View createView(@NotNull LayoutInflater layoutInflater) {
        Intrinsics.checkNotNullParameter(layoutInflater, "li");
        View inflate = layoutInflater.inflate(R$layout.foreground_service_dungeon, (ViewGroup) null);
        this.entriesView = inflate;
        Intrinsics.checkNotNull(inflate);
        inflate.setVisibility(8);
        View view = this.entriesView;
        Intrinsics.checkNotNull(view);
        return view;
    }

    /* access modifiers changed from: private */
    public final void update() {
        Assert.isMainThread();
        View view = this.entriesView;
        if (view != null) {
            Intrinsics.checkNotNull(view);
            View findViewById = view.findViewById(R$id.entry_list);
            Objects.requireNonNull(findViewById, "null cannot be cast to non-null type android.widget.LinearLayout");
            LinearLayout linearLayout = (LinearLayout) findViewById;
            linearLayout.removeAllViews();
            for (T t : CollectionsKt___CollectionsKt.sortedWith(this.entries, new C1699x9638ed8c())) {
                View inflate = LayoutInflater.from(linearLayout.getContext()).inflate(R$layout.foreground_service_dungeon_row, (ViewGroup) null);
                Objects.requireNonNull(inflate, "null cannot be cast to non-null type com.android.systemui.statusbar.notification.row.DungeonRow");
                DungeonRow dungeonRow = (DungeonRow) inflate;
                dungeonRow.setEntry(t);
                dungeonRow.setOnClickListener(new ForegroundServiceSectionController$update$1$2$1(this, dungeonRow, t));
                linearLayout.addView(dungeonRow);
            }
            if (this.entries.isEmpty()) {
                View view2 = this.entriesView;
                if (view2 != null) {
                    view2.setVisibility(8);
                    return;
                }
                return;
            }
            View view3 = this.entriesView;
            if (view3 != null) {
                view3.setVisibility(0);
                return;
            }
            return;
        }
        throw new IllegalStateException("ForegroundServiceSectionController is trying to show dismissed fgs notifications without having been initialized!");
    }
}
