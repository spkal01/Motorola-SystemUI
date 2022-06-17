package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

public final /* synthetic */ class NotificationIconAreaController$$ExternalSyntheticLambda5 implements Function {
    public static final /* synthetic */ NotificationIconAreaController$$ExternalSyntheticLambda5 INSTANCE = new NotificationIconAreaController$$ExternalSyntheticLambda5();

    private /* synthetic */ NotificationIconAreaController$$ExternalSyntheticLambda5() {
    }

    public final Object apply(Object obj) {
        return ((NotificationEntry) obj).getIcons().getShelfIcon();
    }
}
