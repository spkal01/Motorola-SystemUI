package com.android.systemui.wmshell;

import com.android.p011wm.shell.ShellCommandHandler;
import java.io.PrintWriter;
import java.util.function.Consumer;

public final /* synthetic */ class WMShell$$ExternalSyntheticLambda6 implements Consumer {
    public final /* synthetic */ PrintWriter f$0;

    public /* synthetic */ WMShell$$ExternalSyntheticLambda6(PrintWriter printWriter) {
        this.f$0 = printWriter;
    }

    public final void accept(Object obj) {
        ((ShellCommandHandler) obj).dump(this.f$0);
    }
}
