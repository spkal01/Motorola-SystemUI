package com.android.p011wm.shell;

import com.android.p011wm.shell.pip.Pip;
import java.io.PrintWriter;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda9 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda9 implements Consumer {
    public final /* synthetic */ PrintWriter f$0;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda9(PrintWriter printWriter) {
        this.f$0 = printWriter;
    }

    public final void accept(Object obj) {
        ((Pip) obj).dump(this.f$0);
    }
}
