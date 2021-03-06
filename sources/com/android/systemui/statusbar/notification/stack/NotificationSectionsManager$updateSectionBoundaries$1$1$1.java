package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsManager;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationSectionsManager.kt */
final class NotificationSectionsManager$updateSectionBoundaries$1$1$1 extends Lambda implements Function1<NotificationSectionsManager.SectionUpdateState<? extends ExpandableView>, Boolean> {
    final /* synthetic */ NotificationSectionsManager.SectionUpdateState<ExpandableView> $state;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    NotificationSectionsManager$updateSectionBoundaries$1$1$1(NotificationSectionsManager.SectionUpdateState<? extends ExpandableView> sectionUpdateState) {
        super(1);
        this.$state = sectionUpdateState;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Boolean.valueOf(invoke((NotificationSectionsManager.SectionUpdateState<? extends ExpandableView>) (NotificationSectionsManager.SectionUpdateState) obj));
    }

    public final boolean invoke(@NotNull NotificationSectionsManager.SectionUpdateState<? extends ExpandableView> sectionUpdateState) {
        Intrinsics.checkNotNullParameter(sectionUpdateState, "it");
        return sectionUpdateState == this.$state;
    }
}
