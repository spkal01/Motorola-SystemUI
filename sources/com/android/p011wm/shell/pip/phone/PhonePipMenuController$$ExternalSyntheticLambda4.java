package com.android.p011wm.shell.pip.phone;

import com.android.p011wm.shell.pip.phone.PhonePipMenuController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.pip.phone.PhonePipMenuController$$ExternalSyntheticLambda4 */
public final /* synthetic */ class PhonePipMenuController$$ExternalSyntheticLambda4 implements Consumer {
    public static final /* synthetic */ PhonePipMenuController$$ExternalSyntheticLambda4 INSTANCE = new PhonePipMenuController$$ExternalSyntheticLambda4();

    private /* synthetic */ PhonePipMenuController$$ExternalSyntheticLambda4() {
    }

    public final void accept(Object obj) {
        ((PhonePipMenuController.Listener) obj).onPipExpand();
    }
}
