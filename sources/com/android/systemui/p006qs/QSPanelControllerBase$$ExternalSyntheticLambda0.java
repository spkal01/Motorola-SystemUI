package com.android.systemui.p006qs;

import com.android.systemui.p006qs.QSHost;

/* renamed from: com.android.systemui.qs.QSPanelControllerBase$$ExternalSyntheticLambda0 */
public final /* synthetic */ class QSPanelControllerBase$$ExternalSyntheticLambda0 implements QSHost.Callback {
    public final /* synthetic */ QSPanelControllerBase f$0;

    public /* synthetic */ QSPanelControllerBase$$ExternalSyntheticLambda0(QSPanelControllerBase qSPanelControllerBase) {
        this.f$0 = qSPanelControllerBase;
    }

    public final void onTilesChanged() {
        this.f$0.setTiles();
    }
}
