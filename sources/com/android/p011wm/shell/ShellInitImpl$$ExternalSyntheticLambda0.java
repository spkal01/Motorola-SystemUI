package com.android.p011wm.shell;

import com.android.p011wm.shell.apppairs.AppPairsController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellInitImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class ShellInitImpl$$ExternalSyntheticLambda0 implements Consumer {
    public static final /* synthetic */ ShellInitImpl$$ExternalSyntheticLambda0 INSTANCE = new ShellInitImpl$$ExternalSyntheticLambda0();

    private /* synthetic */ ShellInitImpl$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj) {
        ((AppPairsController) obj).onOrganizerRegistered();
    }
}
