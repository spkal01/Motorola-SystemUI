package com.android.p011wm.shell;

import com.android.p011wm.shell.splitscreen.SplitScreenController;
import java.io.PrintWriter;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda10 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda10 implements Consumer {
    public final /* synthetic */ PrintWriter f$0;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda10(PrintWriter printWriter) {
        this.f$0 = printWriter;
    }

    public final void accept(Object obj) {
        ((SplitScreenController) obj).dump(this.f$0, "");
    }
}
