package com.android.systemui.p006qs;

import android.content.res.Configuration;
import com.android.systemui.p006qs.QSPrcPanel;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.QSPrcPanel$$ExternalSyntheticLambda6 */
public final /* synthetic */ class QSPrcPanel$$ExternalSyntheticLambda6 implements Consumer {
    public final /* synthetic */ Configuration f$0;

    public /* synthetic */ QSPrcPanel$$ExternalSyntheticLambda6(Configuration configuration) {
        this.f$0 = configuration;
    }

    public final void accept(Object obj) {
        ((QSPrcPanel.OnConfigurationChangedListener) obj).onConfigurationChange(this.f$0);
    }
}
