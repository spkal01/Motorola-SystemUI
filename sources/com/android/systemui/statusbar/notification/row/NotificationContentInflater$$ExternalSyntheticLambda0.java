package com.android.systemui.statusbar.notification.row;

import android.os.CancellationSignal;
import java.util.HashMap;

public final /* synthetic */ class NotificationContentInflater$$ExternalSyntheticLambda0 implements CancellationSignal.OnCancelListener {
    public final /* synthetic */ HashMap f$0;

    public /* synthetic */ NotificationContentInflater$$ExternalSyntheticLambda0(HashMap hashMap) {
        this.f$0 = hashMap;
    }

    public final void onCancel() {
        this.f$0.values().forEach(NotificationContentInflater$$ExternalSyntheticLambda9.INSTANCE);
    }
}
