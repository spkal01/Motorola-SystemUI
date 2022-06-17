package com.android.systemui.statusbar.notification.collection.notifcollection;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotifEvent.kt */
public abstract class NotifEvent {
    public /* synthetic */ NotifEvent(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    public abstract void dispatchToListener(@NotNull NotifCollectionListener notifCollectionListener);

    private NotifEvent() {
    }

    public final void dispatchTo(@NotNull List<? extends NotifCollectionListener> list) {
        Intrinsics.checkNotNullParameter(list, "listeners");
        int size = list.size() - 1;
        if (size >= 0) {
            int i = 0;
            while (true) {
                int i2 = i + 1;
                dispatchToListener((NotifCollectionListener) list.get(i));
                if (i2 <= size) {
                    i = i2;
                } else {
                    return;
                }
            }
        }
    }
}
