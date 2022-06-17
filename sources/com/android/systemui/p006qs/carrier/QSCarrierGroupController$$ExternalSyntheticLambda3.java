package com.android.systemui.p006qs.carrier;

import com.android.keyguard.CarrierTextManager;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$$ExternalSyntheticLambda3 */
public final /* synthetic */ class QSCarrierGroupController$$ExternalSyntheticLambda3 implements Consumer {
    public final /* synthetic */ QSCarrierGroupController f$0;

    public /* synthetic */ QSCarrierGroupController$$ExternalSyntheticLambda3(QSCarrierGroupController qSCarrierGroupController) {
        this.f$0 = qSCarrierGroupController;
    }

    public final void accept(Object obj) {
        this.f$0.handleUpdateCarrierInfo((CarrierTextManager.CarrierTextCallbackInfo) obj);
    }
}
