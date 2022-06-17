package com.android.systemui.p006qs;

import com.android.systemui.plugins.p005qs.DetailAdapter;

/* renamed from: com.android.systemui.qs.QSDetailDisplayer */
public class QSDetailDisplayer {
    private QSPanelController mQsPanelController;

    public void setQsPanelController(QSPanelController qSPanelController) {
        this.mQsPanelController = qSPanelController;
    }

    public void showDetailAdapter(DetailAdapter detailAdapter, int i, int i2) {
        QSPanelController qSPanelController = this.mQsPanelController;
        if (qSPanelController != null) {
            qSPanelController.showDetailAdapter(detailAdapter, i, i2);
        }
    }
}
