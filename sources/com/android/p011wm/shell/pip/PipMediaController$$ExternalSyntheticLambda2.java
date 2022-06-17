package com.android.p011wm.shell.pip;

import com.android.p011wm.shell.pip.PipMediaController;
import java.util.List;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.pip.PipMediaController$$ExternalSyntheticLambda2 */
public final /* synthetic */ class PipMediaController$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ List f$0;

    public /* synthetic */ PipMediaController$$ExternalSyntheticLambda2(List list) {
        this.f$0 = list;
    }

    public final void accept(Object obj) {
        ((PipMediaController.ActionListener) obj).onMediaActionsChanged(this.f$0);
    }
}
