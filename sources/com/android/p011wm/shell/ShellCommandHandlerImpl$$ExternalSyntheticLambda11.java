package com.android.p011wm.shell;

import com.android.p011wm.shell.splitscreen.SplitScreenController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellCommandHandlerImpl$$ExternalSyntheticLambda11 */
public final /* synthetic */ class ShellCommandHandlerImpl$$ExternalSyntheticLambda11 implements Consumer {
    public final /* synthetic */ Boolean f$0;

    public /* synthetic */ ShellCommandHandlerImpl$$ExternalSyntheticLambda11(Boolean bool) {
        this.f$0 = bool;
    }

    public final void accept(Object obj) {
        ((SplitScreenController) obj).setSideStageVisibility(this.f$0.booleanValue());
    }
}
