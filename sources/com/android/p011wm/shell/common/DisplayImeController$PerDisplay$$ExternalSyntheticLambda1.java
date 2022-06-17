package com.android.p011wm.shell.common;

import android.view.SurfaceControl;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.common.DisplayImeController$PerDisplay$$ExternalSyntheticLambda1 */
public final /* synthetic */ class DisplayImeController$PerDisplay$$ExternalSyntheticLambda1 implements Consumer {
    public static final /* synthetic */ DisplayImeController$PerDisplay$$ExternalSyntheticLambda1 INSTANCE = new DisplayImeController$PerDisplay$$ExternalSyntheticLambda1();

    private /* synthetic */ DisplayImeController$PerDisplay$$ExternalSyntheticLambda1() {
    }

    public final void accept(Object obj) {
        ((SurfaceControl) obj).release();
    }
}
