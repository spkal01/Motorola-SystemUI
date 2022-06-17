package com.android.p011wm.shell.sizecompatui;

import com.android.p011wm.shell.common.DisplayLayout;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.sizecompatui.SizeCompatUIController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class SizeCompatUIController$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ DisplayLayout f$0;

    public /* synthetic */ SizeCompatUIController$$ExternalSyntheticLambda0(DisplayLayout displayLayout) {
        this.f$0 = displayLayout;
    }

    public final void accept(Object obj) {
        ((SizeCompatUILayout) obj).updateDisplayLayout(this.f$0);
    }
}
