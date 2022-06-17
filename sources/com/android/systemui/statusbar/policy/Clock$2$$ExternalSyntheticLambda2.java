package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.Clock;
import java.util.Locale;

public final /* synthetic */ class Clock$2$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ Clock.C19922 f$0;
    public final /* synthetic */ Locale f$1;

    public /* synthetic */ Clock$2$$ExternalSyntheticLambda2(Clock.C19922 r1, Locale locale) {
        this.f$0 = r1;
        this.f$1 = locale;
    }

    public final void run() {
        this.f$0.lambda$onReceive$1(this.f$1);
    }
}
