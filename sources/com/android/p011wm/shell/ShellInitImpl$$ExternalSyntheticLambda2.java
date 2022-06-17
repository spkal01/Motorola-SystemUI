package com.android.p011wm.shell;

import com.android.p011wm.shell.pip.phone.PipTouchHandler;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellInitImpl$$ExternalSyntheticLambda2 */
public final /* synthetic */ class ShellInitImpl$$ExternalSyntheticLambda2 implements Consumer {
    public static final /* synthetic */ ShellInitImpl$$ExternalSyntheticLambda2 INSTANCE = new ShellInitImpl$$ExternalSyntheticLambda2();

    private /* synthetic */ ShellInitImpl$$ExternalSyntheticLambda2() {
    }

    public final void accept(Object obj) {
        ((PipTouchHandler) obj).init();
    }
}
