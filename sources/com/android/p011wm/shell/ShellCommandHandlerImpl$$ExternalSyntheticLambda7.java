package com.android.p011wm.shell;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import java.io.PrintWriter;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda7 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda7 implements Consumer {
    public final /* synthetic */ PrintWriter f$0;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda7(PrintWriter printWriter) {
        this.f$0 = printWriter;
    }

    public final void accept(Object obj) {
        ((LegacySplitScreenController) obj).dump(this.f$0);
    }
}
