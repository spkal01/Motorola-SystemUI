package com.android.systemui.p006qs;

import com.android.systemui.tuner.TunerService;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.QSTileHost$$ExternalSyntheticLambda0 */
public final /* synthetic */ class QSTileHost$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ QSTileHost f$0;
    public final /* synthetic */ TunerService f$1;
    public final /* synthetic */ Provider f$2;

    public /* synthetic */ QSTileHost$$ExternalSyntheticLambda0(QSTileHost qSTileHost, TunerService tunerService, Provider provider) {
        this.f$0 = qSTileHost;
        this.f$1 = tunerService;
        this.f$2 = provider;
    }

    public final void run() {
        this.f$0.lambda$new$0(this.f$1, this.f$2);
    }
}
