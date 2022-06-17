package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.phone.StatusBarIconController;
import java.util.function.Consumer;

public final /* synthetic */ class StatusBarIconControllerImpl$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ StatusBarIconControllerImpl$$ExternalSyntheticLambda2(int i, int i2) {
        this.f$0 = i;
        this.f$1 = i2;
    }

    public final void accept(Object obj) {
        ((StatusBarIconController.IconManager) obj).onIconExternal(this.f$0, this.f$1);
    }
}
