package com.android.systemui;

import android.content.Intent;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda7 implements Consumer {
    public final /* synthetic */ Intent f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ActivityLaunchAnimator.Controller f$2;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda7(Intent intent, int i, ActivityLaunchAnimator.Controller controller) {
        this.f$0 = intent;
        this.f$1 = i;
        this.f$2 = controller;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).postStartActivityDismissingKeyguard(this.f$0, this.f$1, this.f$2);
    }
}
