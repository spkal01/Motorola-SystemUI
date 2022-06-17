package com.android.systemui.p006qs.tileimpl;

import com.android.systemui.statusbar.policy.ConfigurationController;

/* renamed from: com.android.systemui.qs.tileimpl.QSTileViewPrcFixedImpl$mConfigListener$1 */
/* compiled from: QSTileViewPrcFixedImpl.kt */
public final class QSTileViewPrcFixedImpl$mConfigListener$1 implements ConfigurationController.ConfigurationListener {
    final /* synthetic */ QSTileViewPrcFixedImpl this$0;

    QSTileViewPrcFixedImpl$mConfigListener$1(QSTileViewPrcFixedImpl qSTileViewPrcFixedImpl) {
        this.this$0 = qSTileViewPrcFixedImpl;
    }

    public void onUiModeChanged() {
        this.this$0.updateThemeColor();
    }
}
