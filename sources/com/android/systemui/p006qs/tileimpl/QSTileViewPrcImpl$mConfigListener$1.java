package com.android.systemui.p006qs.tileimpl;

import com.android.systemui.statusbar.policy.ConfigurationController;

/* renamed from: com.android.systemui.qs.tileimpl.QSTileViewPrcImpl$mConfigListener$1 */
/* compiled from: QSTileViewPrcImpl.kt */
public final class QSTileViewPrcImpl$mConfigListener$1 implements ConfigurationController.ConfigurationListener {
    final /* synthetic */ QSTileViewPrcImpl this$0;

    QSTileViewPrcImpl$mConfigListener$1(QSTileViewPrcImpl qSTileViewPrcImpl) {
        this.this$0 = qSTileViewPrcImpl;
    }

    public void onUiModeChanged() {
        this.this$0.updateThemeColor();
    }
}
