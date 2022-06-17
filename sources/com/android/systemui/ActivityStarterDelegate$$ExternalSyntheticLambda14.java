package com.android.systemui;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda14 implements Consumer {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda14(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).postQSRunnableDismissingKeyguard(this.f$0);
    }
}
