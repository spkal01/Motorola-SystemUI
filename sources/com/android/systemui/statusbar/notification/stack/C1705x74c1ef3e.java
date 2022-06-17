package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.row.ExpandableView;
import java.util.Iterator;
import kotlin.collections.Grouping;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$updateFirstAndLastViewsForAllSections$$inlined$groupingBy$1 */
/* compiled from: _Sequences.kt */
public final class C1705x74c1ef3e implements Grouping<ExpandableView, Integer> {
    final /* synthetic */ Sequence $this_groupingBy;
    final /* synthetic */ NotificationSectionsManager this$0;

    public C1705x74c1ef3e(Sequence sequence, NotificationSectionsManager notificationSectionsManager) {
        this.$this_groupingBy = sequence;
        this.this$0 = notificationSectionsManager;
    }

    @NotNull
    public Iterator<ExpandableView> sourceIterator() {
        return this.$this_groupingBy.iterator();
    }

    public Integer keyOf(ExpandableView expandableView) {
        Integer access$getBucket = this.this$0.getBucket(expandableView);
        if (access$getBucket != null) {
            return Integer.valueOf(access$getBucket.intValue());
        }
        throw new IllegalArgumentException("Cannot find section bucket for view");
    }
}
