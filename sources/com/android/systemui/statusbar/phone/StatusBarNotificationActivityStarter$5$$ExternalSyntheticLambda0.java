package com.android.systemui.statusbar.phone;

import android.view.View;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter;

public final /* synthetic */ class StatusBarNotificationActivityStarter$5$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ StatusBarNotificationActivityStarter.C19525 f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ View f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ StatusBarNotificationActivityStarter$5$$ExternalSyntheticLambda0(StatusBarNotificationActivityStarter.C19525 r1, boolean z, View view, boolean z2) {
        this.f$0 = r1;
        this.f$1 = z;
        this.f$2 = view;
        this.f$3 = z2;
    }

    public final void run() {
        this.f$0.lambda$onDismiss$1(this.f$1, this.f$2, this.f$3);
    }
}
