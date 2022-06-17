package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

public final /* synthetic */ class NotificationIconAreaController$$ExternalSyntheticLambda6 implements Function {
    public static final /* synthetic */ NotificationIconAreaController$$ExternalSyntheticLambda6 INSTANCE = new NotificationIconAreaController$$ExternalSyntheticLambda6();

    private /* synthetic */ NotificationIconAreaController$$ExternalSyntheticLambda6() {
    }

    public final Object apply(Object obj) {
        return ((NotificationEntry) obj).getIcons().getStatusBarIcon();
    }
}
