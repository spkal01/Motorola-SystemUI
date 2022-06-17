package com.android.p011wm.shell;

import com.android.p011wm.shell.splitscreen.SplitScreenController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellInitImpl$$ExternalSyntheticLambda3 */
public final /* synthetic */ class ShellInitImpl$$ExternalSyntheticLambda3 implements Consumer {
    public static final /* synthetic */ ShellInitImpl$$ExternalSyntheticLambda3 INSTANCE = new ShellInitImpl$$ExternalSyntheticLambda3();

    private /* synthetic */ ShellInitImpl$$ExternalSyntheticLambda3() {
    }

    public final void accept(Object obj) {
        ((SplitScreenController) obj).onOrganizerRegistered();
    }
}
