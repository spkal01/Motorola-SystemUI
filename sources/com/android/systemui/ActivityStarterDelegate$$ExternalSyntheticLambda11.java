package com.android.systemui;

import android.content.Intent;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda11 implements Consumer {
    public final /* synthetic */ Intent f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda11(Intent intent, boolean z, boolean z2) {
        this.f$0 = intent;
        this.f$1 = z;
        this.f$2 = z2;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).startActivity(this.f$0, this.f$1, this.f$2);
    }
}
