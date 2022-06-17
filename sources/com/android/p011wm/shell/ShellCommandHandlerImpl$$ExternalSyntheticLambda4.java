package com.android.p011wm.shell;

import com.android.p011wm.shell.splitscreen.SplitScreenController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda4 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda4 implements Consumer {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda4(int i, int i2) {
        this.f$0 = i;
        this.f$1 = i2;
    }

    public final void accept(Object obj) {
        ((SplitScreenController) obj).moveToSideStage(this.f$0, this.f$1);
    }
}
