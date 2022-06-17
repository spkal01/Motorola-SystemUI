package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Function;

public final /* synthetic */ class NotificationIconAreaController$$ExternalSyntheticLambda7 implements Function {
    public static final /* synthetic */ NotificationIconAreaController$$ExternalSyntheticLambda7 INSTANCE = new NotificationIconAreaController$$ExternalSyntheticLambda7();

    private /* synthetic */ NotificationIconAreaController$$ExternalSyntheticLambda7() {
    }

    public final Object apply(Object obj) {
        return ((NotificationEntry) obj).getIcons().getCenteredIcon();
    }
}
