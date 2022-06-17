package com.android.systemui.statusbar.notification.row;

import android.os.CancellationSignal;
import java.util.function.Consumer;

public final /* synthetic */ class NotificationContentInflater$$ExternalSyntheticLambda9 implements Consumer {
    public static final /* synthetic */ NotificationContentInflater$$ExternalSyntheticLambda9 INSTANCE = new NotificationContentInflater$$ExternalSyntheticLambda9();

    private /* synthetic */ NotificationContentInflater$$ExternalSyntheticLambda9() {
    }

    public final void accept(Object obj) {
        ((CancellationSignal) obj).cancel();
    }
}
