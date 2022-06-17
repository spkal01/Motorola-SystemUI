package com.android.systemui;

import android.app.PendingIntent;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ PendingIntent f$0;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda1(PendingIntent pendingIntent) {
        this.f$0 = pendingIntent;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).postStartActivityDismissingKeyguard(this.f$0);
    }
}
