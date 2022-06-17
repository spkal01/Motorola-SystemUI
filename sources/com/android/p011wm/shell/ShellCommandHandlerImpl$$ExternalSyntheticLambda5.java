package com.android.p011wm.shell;

import com.android.p011wm.shell.apppairs.AppPairsController;
import java.io.PrintWriter;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda5 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda5 implements Consumer {
    public final /* synthetic */ PrintWriter f$0;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda5(PrintWriter printWriter) {
        this.f$0 = printWriter;
    }

    public final void accept(Object obj) {
        ((AppPairsController) obj).dump(this.f$0, "");
    }
}
