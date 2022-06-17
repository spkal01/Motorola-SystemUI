package com.android.p011wm.shell.sizecompatui;

import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.sizecompatui.SizeCompatUIController$$ExternalSyntheticLambda2 */
public final /* synthetic */ class SizeCompatUIController$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ boolean f$0;

    public /* synthetic */ SizeCompatUIController$$ExternalSyntheticLambda2(boolean z) {
        this.f$0 = z;
    }

    public final void accept(Object obj) {
        ((SizeCompatUILayout) obj).updateImeVisibility(this.f$0);
    }
}
