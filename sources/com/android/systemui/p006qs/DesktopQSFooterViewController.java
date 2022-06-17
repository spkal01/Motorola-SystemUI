package com.android.systemui.p006qs;

import android.os.UserManager;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$id;
import com.android.systemui.globalactions.GlobalActionsDialogLite;
import com.android.systemui.p006qs.QSPanel;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.phone.MultiUserSwitchController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.tuner.TunerService;
import com.motorola.systemui.desktop.widget.DesktopQSPanelArrowLayout;

/* renamed from: com.android.systemui.qs.DesktopQSFooterViewController */
public class DesktopQSFooterViewController extends QSFooterViewController {
    private final DesktopQSPanelArrowLayout mPageArrow = ((DesktopQSPanelArrowLayout) ((QSFooterView) this.mView).findViewById(R$id.qs_footer_page_arrow));
    private final QSPanelController mQsPanelController;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DesktopQSFooterViewController(QSFooterView qSFooterView, UserManager userManager, UserInfoController userInfoController, ActivityStarter activityStarter, DeviceProvisionedController deviceProvisionedController, UserTracker userTracker, QSPanelController qSPanelController, MultiUserSwitchController multiUserSwitchController, QuickQSPanelController quickQSPanelController, TunerService tunerService, MetricsLogger metricsLogger, FalsingManager falsingManager, UiEventLogger uiEventLogger) {
        super(qSFooterView, userManager, userInfoController, activityStarter, deviceProvisionedController, userTracker, qSPanelController, multiUserSwitchController, quickQSPanelController, tunerService, metricsLogger, falsingManager, false, (GlobalActionsDialogLite) null, uiEventLogger);
        this.mQsPanelController = qSPanelController;
        init();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        QSPanel.QSTileLayout tileLayout = this.mQsPanelController.getTileLayout();
        if (tileLayout instanceof PagedTileLayout) {
            PagedTileLayout pagedTileLayout = (PagedTileLayout) tileLayout;
            pagedTileLayout.setDesktopFooterPageArrow(this.mPageArrow);
            this.mPageArrow.setPageTileLayout(pagedTileLayout);
        }
    }
}
