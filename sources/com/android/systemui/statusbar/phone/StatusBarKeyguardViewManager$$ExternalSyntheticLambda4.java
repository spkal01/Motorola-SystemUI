package com.android.systemui.statusbar.phone;

import android.view.ViewGroup;
import com.android.systemui.keyguard.FaceAuthScreenBrightnessController;
import java.util.function.Consumer;

public final /* synthetic */ class StatusBarKeyguardViewManager$$ExternalSyntheticLambda4 implements Consumer {
    public final /* synthetic */ StatusBarKeyguardViewManager f$0;
    public final /* synthetic */ ViewGroup f$1;

    public /* synthetic */ StatusBarKeyguardViewManager$$ExternalSyntheticLambda4(StatusBarKeyguardViewManager statusBarKeyguardViewManager, ViewGroup viewGroup) {
        this.f$0 = statusBarKeyguardViewManager;
        this.f$1 = viewGroup;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$registerStatusBar$0(this.f$1, (FaceAuthScreenBrightnessController) obj);
    }
}
