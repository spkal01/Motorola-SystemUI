package com.android.p011wm.shell.pip.phone;

import com.android.p011wm.shell.pip.phone.PhonePipMenuController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.pip.phone.PhonePipMenuController$$ExternalSyntheticLambda1 */
public final /* synthetic */ class PhonePipMenuController$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ PhonePipMenuController$$ExternalSyntheticLambda1(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((PhonePipMenuController.Listener) obj).onPipMenuStateChangeFinish(this.f$0);
    }
}
