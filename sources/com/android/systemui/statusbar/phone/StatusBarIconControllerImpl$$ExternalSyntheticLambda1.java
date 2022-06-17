package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.phone.StatusBarIconController;
import java.util.function.Consumer;

public final /* synthetic */ class StatusBarIconControllerImpl$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ StatusBarIconControllerImpl$$ExternalSyntheticLambda1(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((StatusBarIconController.IconManager) obj).onRemoveIcon(this.f$0);
    }
}
