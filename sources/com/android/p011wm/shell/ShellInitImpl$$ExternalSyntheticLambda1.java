package com.android.p011wm.shell;

import com.android.p011wm.shell.bubbles.BubbleController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.ShellInitImpl$$ExternalSyntheticLambda1 */
public final /* synthetic */ class ShellInitImpl$$ExternalSyntheticLambda1 implements Consumer {
    public static final /* synthetic */ ShellInitImpl$$ExternalSyntheticLambda1 INSTANCE = new ShellInitImpl$$ExternalSyntheticLambda1();

    private /* synthetic */ ShellInitImpl$$ExternalSyntheticLambda1() {
    }

    public final void accept(Object obj) {
        ((BubbleController) obj).initialize();
    }
}
