package com.android.systemui.p006qs.dagger;

import com.android.systemui.p006qs.DesktopQSFooterViewController;
import com.android.systemui.p006qs.QSAnimator;
import com.android.systemui.p006qs.QSContainerImplController;
import com.android.systemui.p006qs.QSFooter;
import com.android.systemui.p006qs.QSFragment;
import com.android.systemui.p006qs.QSPanelController;
import com.android.systemui.p006qs.QSPrcFixedPanelController;
import com.android.systemui.p006qs.QSPrcPanelContainerController;
import com.android.systemui.p006qs.QuickQSPanelController;
import com.android.systemui.p006qs.customize.QSCustomizerController;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentComponent */
public interface QSFragmentComponent {

    /* renamed from: com.android.systemui.qs.dagger.QSFragmentComponent$Factory */
    public interface Factory {
        QSFragmentComponent create(QSFragment qSFragment);
    }

    DesktopQSFooterViewController getDesktopQSFooter();

    QSAnimator getQSAnimator();

    QSContainerImplController getQSContainerImplController();

    QSCustomizerController getQSCustomizerController();

    QSFooter getQSFooter();

    QSPanelController getQSPanelController();

    QSPrcFixedPanelController getQSPrcFixedPanelController();

    QSPrcPanelContainerController getQSPrcPanelContainerController();

    QuickQSPanelController getQuickQSPanelController();
}
