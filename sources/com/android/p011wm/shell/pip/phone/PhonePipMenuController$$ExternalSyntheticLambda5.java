package com.android.p011wm.shell.pip.phone;

import com.android.p011wm.shell.pip.phone.PhonePipMenuController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.pip.phone.PhonePipMenuController$$ExternalSyntheticLambda5 */
public final /* synthetic */ class PhonePipMenuController$$ExternalSyntheticLambda5 implements Consumer {
    public static final /* synthetic */ PhonePipMenuController$$ExternalSyntheticLambda5 INSTANCE = new PhonePipMenuController$$ExternalSyntheticLambda5();

    private /* synthetic */ PhonePipMenuController$$ExternalSyntheticLambda5() {
    }

    public final void accept(Object obj) {
        ((PhonePipMenuController.Listener) obj).onPipShowMenu();
    }
}
