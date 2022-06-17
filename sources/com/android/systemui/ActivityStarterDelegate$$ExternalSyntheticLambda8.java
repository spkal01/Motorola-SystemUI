package com.android.systemui;

import android.content.Intent;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class ActivityStarterDelegate$$ExternalSyntheticLambda8 implements Consumer {
    public final /* synthetic */ Intent f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ ActivityStarterDelegate$$ExternalSyntheticLambda8(Intent intent, boolean z) {
        this.f$0 = intent;
        this.f$1 = z;
    }

    public final void accept(Object obj) {
        ((StatusBar) ((Lazy) obj).get()).startActivity(this.f$0, this.f$1);
    }
}
