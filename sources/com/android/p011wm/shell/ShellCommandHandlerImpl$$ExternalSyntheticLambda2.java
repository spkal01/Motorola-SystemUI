package com.android.p011wm.shell;

import com.android.p011wm.shell.splitscreen.SplitScreenController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda2 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda2(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((SplitScreenController) obj).setSideStagePosition(this.f$0);
    }
}
