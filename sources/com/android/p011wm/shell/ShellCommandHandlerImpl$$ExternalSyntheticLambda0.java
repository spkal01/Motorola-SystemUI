package com.android.p011wm.shell;

import com.android.p011wm.shell.apppairs.AppPairsController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((AppPairsController) obj).unpair(this.f$0);
    }
}
