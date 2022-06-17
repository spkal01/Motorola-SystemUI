package com.android.systemui;

import android.app.PendingIntent;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ PendingIntent f$0;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda0(PendingIntent pendingIntent) {
        this.f$0 = pendingIntent;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).startPendingIntentDismissingKeyguard(this.f$0);
    }
}
