package com.android.p011wm.shell;

import com.android.p011wm.shell.splitscreen.SplitScreenController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda1 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda1(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((SplitScreenController) obj).removeFromSideStage(this.f$0);
    }
}
