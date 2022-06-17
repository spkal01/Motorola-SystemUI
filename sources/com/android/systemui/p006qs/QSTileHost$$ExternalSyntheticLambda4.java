package com.android.systemui.p006qs;

import com.android.systemui.statusbar.phone.StatusBar;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.QSTileHost$$ExternalSyntheticLambda4 */
public final /* synthetic */ class QSTileHost$$ExternalSyntheticLambda4 implements Consumer {
    public static final /* synthetic */ QSTileHost$$ExternalSyntheticLambda4 INSTANCE = new QSTileHost$$ExternalSyntheticLambda4();

    private /* synthetic */ QSTileHost$$ExternalSyntheticLambda4() {
    }

    public final void accept(Object obj) {
        ((StatusBar) obj).postAnimateForceCollapsePanels();
    }
}
