package com.android.systemui;

import android.app.PendingIntent;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda3 implements Consumer {
    public final /* synthetic */ PendingIntent f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda3(PendingIntent pendingIntent, Runnable runnable) {
        this.f$0 = pendingIntent;
        this.f$1 = runnable;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).startPendingIntentDismissingKeyguard(this.f$0, this.f$1);
    }
}
