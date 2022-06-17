package com.android.p011wm.shell.sizecompatui;

import java.util.List;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.sizecompatui.SizeCompatUIController$$ExternalSyntheticLambda1 */
public final /* synthetic */ class SizeCompatUIController$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ List f$0;

    public /* synthetic */ SizeCompatUIController$$ExternalSyntheticLambda1(List list) {
        this.f$0 = list;
    }

    public final void accept(Object obj) {
        this.f$0.add(Integer.valueOf(((SizeCompatUILayout) obj).getTaskId()));
    }
}
