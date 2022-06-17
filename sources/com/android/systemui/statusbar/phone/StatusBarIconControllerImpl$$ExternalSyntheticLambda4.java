package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.phone.StatusBarIconController;
import java.util.function.Consumer;

public final /* synthetic */ class StatusBarIconControllerImpl$$ExternalSyntheticLambda4 implements Consumer {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ StatusBarIconHolder f$1;

    public /* synthetic */ StatusBarIconControllerImpl$$ExternalSyntheticLambda4(int i, StatusBarIconHolder statusBarIconHolder) {
        this.f$0 = i;
        this.f$1 = statusBarIconHolder;
    }

    public final void accept(Object obj) {
        ((StatusBarIconController.IconManager) obj).onSetIconHolder(this.f$0, this.f$1);
    }
}
