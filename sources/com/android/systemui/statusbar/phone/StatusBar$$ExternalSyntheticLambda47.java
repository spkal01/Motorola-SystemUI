package com.android.systemui.statusbar.phone;

import android.content.Intent;
import android.view.RemoteAnimationAdapter;
import kotlin.jvm.functions.Function1;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda47 implements Function1 {
    public final /* synthetic */ StatusBar f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Intent f$2;
    public final /* synthetic */ int[] f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda47(StatusBar statusBar, boolean z, Intent intent, int[] iArr, int i) {
        this.f$0 = statusBar;
        this.f$1 = z;
        this.f$2 = intent;
        this.f$3 = iArr;
        this.f$4 = i;
    }

    public final Object invoke(Object obj) {
        return this.f$0.lambda$startActivityDismissingKeyguard$20(this.f$1, this.f$2, this.f$3, this.f$4, (RemoteAnimationAdapter) obj);
    }
}
