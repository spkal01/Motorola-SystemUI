package com.android.p011wm.shell;

import com.android.p011wm.shell.onehanded.OneHandedController;
import java.io.PrintWriter;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda8 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda8 implements Consumer {
    public final /* synthetic */ PrintWriter f$0;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda8(PrintWriter printWriter) {
        this.f$0 = printWriter;
    }

    public final void accept(Object obj) {
        ((OneHandedController) obj).dump(this.f$0);
    }
}
