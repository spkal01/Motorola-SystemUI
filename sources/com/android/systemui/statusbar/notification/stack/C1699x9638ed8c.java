package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Comparator;

/* renamed from: com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController$update$lambda-2$$inlined$sortedBy$1 */
/* compiled from: Comparisons.kt */
public final class C1699x9638ed8c<T> implements Comparator<T> {
    public final int compare(T t, T t2) {
        return ComparisonsKt__ComparisonsKt.compareValues(Integer.valueOf(((NotificationEntry) t).getRanking().getRank()), Integer.valueOf(((NotificationEntry) t2).getRanking().getRank()));
    }
}
