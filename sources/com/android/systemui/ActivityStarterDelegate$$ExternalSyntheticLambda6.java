package com.android.systemui;

import android.content.Intent;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda6 implements Consumer {
    public final /* synthetic */ Intent f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda6(Intent intent, int i) {
        this.f$0 = intent;
        this.f$1 = i;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).postStartActivityDismissingKeyguard(this.f$0, this.f$1);
    }
}
