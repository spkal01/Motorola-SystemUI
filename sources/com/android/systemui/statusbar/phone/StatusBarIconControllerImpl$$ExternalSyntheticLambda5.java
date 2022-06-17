package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.phone.StatusBarIconController;
import java.util.function.Consumer;

public final /* synthetic */ class StatusBarIconControllerImpl$$ExternalSyntheticLambda5 implements Consumer {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ StatusBarIconHolder f$3;

    public /* synthetic */ StatusBarIconControllerImpl$$ExternalSyntheticLambda5(int i, String str, boolean z, StatusBarIconHolder statusBarIconHolder) {
        this.f$0 = i;
        this.f$1 = str;
        this.f$2 = z;
        this.f$3 = statusBarIconHolder;
    }

    public final void accept(Object obj) {
        ((StatusBarIconController.IconManager) obj).onIconAdded(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
