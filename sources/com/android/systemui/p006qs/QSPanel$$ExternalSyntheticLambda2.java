package com.android.systemui.p006qs;

import android.content.res.Configuration;
import com.android.systemui.p006qs.QSPanel;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.QSPanel$$ExternalSyntheticLambda2 */
public final /* synthetic */ class QSPanel$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ Configuration f$0;

    public /* synthetic */ QSPanel$$ExternalSyntheticLambda2(Configuration configuration) {
        this.f$0 = configuration;
    }

    public final void accept(Object obj) {
        ((QSPanel.OnConfigurationChangedListener) obj).onConfigurationChange(this.f$0);
    }
}
