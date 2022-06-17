package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

public final /* synthetic */ class NotificationMediaManager$$ExternalSyntheticLambda2 implements Function {
    public static final /* synthetic */ NotificationMediaManager$$ExternalSyntheticLambda2 INSTANCE = new NotificationMediaManager$$ExternalSyntheticLambda2();

    private /* synthetic */ NotificationMediaManager$$ExternalSyntheticLambda2() {
    }

    public final Object apply(Object obj) {
        return ((NotificationEntry) obj).getIcons().getShelfIcon();
    }
}
