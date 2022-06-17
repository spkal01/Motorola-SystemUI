package com.android.systemui;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda13 implements Consumer {
    public final /* synthetic */ ActivityStarter.OnDismissAction f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda13(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable, boolean z) {
        this.f$0 = onDismissAction;
        this.f$1 = runnable;
        this.f$2 = z;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).dismissKeyguardThenExecute(this.f$0, this.f$1, this.f$2);
    }
}
