package com.android.p011wm.shell.pip.phone;

import com.android.p011wm.shell.pip.phone.PhonePipMenuController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.pip.phone.PhonePipMenuController$$ExternalSyntheticLambda3 */
public final /* synthetic */ class PhonePipMenuController$$ExternalSyntheticLambda3 implements Consumer {
    public static final /* synthetic */ PhonePipMenuController$$ExternalSyntheticLambda3 INSTANCE = new PhonePipMenuController$$ExternalSyntheticLambda3();

    private /* synthetic */ PhonePipMenuController$$ExternalSyntheticLambda3() {
    }

    public final void accept(Object obj) {
        ((PhonePipMenuController.Listener) obj).onPipDismiss();
    }
}
