package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

public final /* synthetic */ class NotificationIconAreaController$$ExternalSyntheticLambda4 implements Function {
    public static final /* synthetic */ NotificationIconAreaController$$ExternalSyntheticLambda4 INSTANCE = new NotificationIconAreaController$$ExternalSyntheticLambda4();

    private /* synthetic */ NotificationIconAreaController$$ExternalSyntheticLambda4() {
    }

    public final Object apply(Object obj) {
        return ((NotificationEntry) obj).getIcons().getAodIcon();
    }
}
