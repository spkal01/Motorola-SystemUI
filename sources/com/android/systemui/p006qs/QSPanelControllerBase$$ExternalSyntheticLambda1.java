package com.android.systemui.p006qs;

import com.android.systemui.p006qs.QSPanelControllerBase;
import java.util.function.Function;

/* renamed from: com.android.systemui.qs.QSPanelControllerBase$$ExternalSyntheticLambda1 */
public final /* synthetic */ class QSPanelControllerBase$$ExternalSyntheticLambda1 implements Function {
    public static final /* synthetic */ QSPanelControllerBase$$ExternalSyntheticLambda1 INSTANCE = new QSPanelControllerBase$$ExternalSyntheticLambda1();

    private /* synthetic */ QSPanelControllerBase$$ExternalSyntheticLambda1() {
    }

    public final Object apply(Object obj) {
        return ((QSPanelControllerBase.TileRecord) obj).tile.getTileSpec();
    }
}
