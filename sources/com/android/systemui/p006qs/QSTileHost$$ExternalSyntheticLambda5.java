package com.android.systemui.p006qs;

import com.android.systemui.statusbar.phone.StatusBar;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.QSTileHost$$ExternalSyntheticLambda5 */
public final /* synthetic */ class QSTileHost$$ExternalSyntheticLambda5 implements Consumer {
    public static final /* synthetic */ QSTileHost$$ExternalSyntheticLambda5 INSTANCE = new QSTileHost$$ExternalSyntheticLambda5();

    private /* synthetic */ QSTileHost$$ExternalSyntheticLambda5() {
    }

    public final void accept(Object obj) {
        ((StatusBar) obj).postAnimateOpenPanels();
    }
}
