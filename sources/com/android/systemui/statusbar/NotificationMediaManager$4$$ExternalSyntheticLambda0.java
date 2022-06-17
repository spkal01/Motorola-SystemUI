package com.android.systemui.statusbar;

import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Consumer;

public final /* synthetic */ class NotificationMediaManager$4$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ NotificationMediaManager.C14664 f$0;

    public /* synthetic */ NotificationMediaManager$4$$ExternalSyntheticLambda0(NotificationMediaManager.C14664 r1) {
        this.f$0 = r1;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onMediaDataRemoved$1((NotificationEntry) obj);
    }
}
