package com.android.systemui;

import android.app.PendingIntent;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda5 implements Consumer {
    public final /* synthetic */ PendingIntent f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ ActivityLaunchAnimator.Controller f$2;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda5(PendingIntent pendingIntent, Runnable runnable, ActivityLaunchAnimator.Controller controller) {
        this.f$0 = pendingIntent;
        this.f$1 = runnable;
        this.f$2 = controller;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).startPendingIntentDismissingKeyguard(this.f$0, this.f$1, this.f$2);
    }
}
