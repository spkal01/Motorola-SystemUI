package com.android.systemui.p006qs;

import android.content.res.Configuration;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.Dependency;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.media.MediaHierarchyManager;
import com.android.systemui.media.MediaHost;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.DualSimIconController;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.PagedTileLayout;
import com.android.systemui.p006qs.QSDetail;
import com.android.systemui.p006qs.QSPanel;
import com.android.systemui.p006qs.QSTileRevealController;
import com.android.systemui.p006qs.customize.QSCustomizerController;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.settings.brightness.BrightnessController;
import com.android.systemui.settings.brightness.BrightnessSlider;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.tuner.TunerService;

/* renamed from: com.android.systemui.qs.QSPanelController */
public class QSPanelController extends QSPanelControllerBase<QSPanel> {
    private final BrightnessController mBrightnessController;
    private BrightnessMirrorController mBrightnessMirrorController;
    private final BrightnessMirrorController.BrightnessMirrorListener mBrightnessMirrorListener = new QSPanelController$$ExternalSyntheticLambda0(this);
    private final BrightnessSlider mBrightnessSlider;
    private final BrightnessSlider.Factory mBrightnessSliderFactory;
    private DualSimIconController.Callback mCallback = new DualSimIconController.Callback() {
        public void onActiveSubsCountChanged(int i) {
            ((QSPanel) QSPanelController.this.mView).updateActiveSubsCount(i);
        }

        public void onAirplaneModeChanged(boolean z) {
            ((QSPanel) QSPanelController.this.mView).updateAirplaneMode(z);
        }
    };
    /* access modifiers changed from: private */
    public final FalsingManager mFalsingManager;
    private boolean mGridContentVisible = true;
    public MediaHierarchyManager mMediaHierarchyManager;
    private final QSPanel.OnConfigurationChangedListener mOnConfigurationChangedListener = new QSPanel.OnConfigurationChangedListener() {
        public void onConfigurationChange(Configuration configuration) {
            ((QSPanel) QSPanelController.this.mView).updateResources();
            QSPanelController.this.mQsSecurityFooter.onConfigurationChanged();
            if (((QSPanel) QSPanelController.this.mView).isListening()) {
                QSPanelController.this.refreshAllTiles();
            }
            QSPanelController.this.updateBrightnessMirror();
        }
    };
    private final QSCustomizerController mQsCustomizerController;
    protected final QSSecurityFooter mQsSecurityFooter;
    private final QSTileRevealController.Factory mQsTileRevealControllerFactory;
    private View.OnTouchListener mTileLayoutTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() != 1) {
                return false;
            }
            QSPanelController.this.mFalsingManager.isFalseTouch(15);
            return false;
        }
    };
    private final TunerService mTunerService;

    public void setQSSecurityContainer(ViewGroup viewGroup) {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        updateBrightnessMirror();
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    QSPanelController(QSPanel qSPanel, QSSecurityFooter qSSecurityFooter, TunerService tunerService, QSTileHost qSTileHost, QSCustomizerController qSCustomizerController, boolean z, MediaHost mediaHost, QSTileRevealController.Factory factory, DumpManager dumpManager, MetricsLogger metricsLogger, UiEventLogger uiEventLogger, QSLogger qSLogger, BrightnessController.Factory factory2, BrightnessSlider.Factory factory3, FalsingManager falsingManager, FeatureFlags featureFlags) {
        super(qSPanel, qSTileHost, qSCustomizerController, z, mediaHost, metricsLogger, uiEventLogger, qSLogger, dumpManager, featureFlags);
        QSSecurityFooter qSSecurityFooter2 = qSSecurityFooter;
        BrightnessSlider.Factory factory4 = factory3;
        this.mQsSecurityFooter = qSSecurityFooter2;
        this.mTunerService = tunerService;
        this.mQsCustomizerController = qSCustomizerController;
        this.mQsTileRevealControllerFactory = factory;
        this.mFalsingManager = falsingManager;
        qSSecurityFooter2.setHostEnvironment(qSTileHost);
        if (!MotoFeature.getInstance(getContext()).isCustomPanelView()) {
            this.mBrightnessSliderFactory = factory4;
            BrightnessSlider create = factory4.create(getContext(), (ViewGroup) this.mView);
            this.mBrightnessSlider = create;
            ((QSPanel) this.mView).setBrightnessView(create.getRootView());
            this.mBrightnessController = factory2.create(create);
            return;
        }
        this.mBrightnessSliderFactory = null;
        this.mBrightnessSlider = null;
        this.mBrightnessController = null;
    }

    public void onInit() {
        super.onInit();
        this.mMediaHost.setExpansion(1.0f);
        this.mMediaHost.setShowsOnlyActiveMedia(false);
        this.mMediaHost.init(0);
        this.mQsCustomizerController.init();
        BrightnessSlider brightnessSlider = this.mBrightnessSlider;
        if (brightnessSlider != null) {
            brightnessSlider.init();
        }
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        updateMediaDisappearParameters();
        this.mTunerService.addTunable((TunerService.Tunable) this.mView, "qs_show_brightness");
        ((QSPanel) this.mView).updateResources();
        if (((QSPanel) this.mView).isListening()) {
            refreshAllTiles();
        }
        ((QSPanel) this.mView).addOnConfigurationChangedListener(this.mOnConfigurationChangedListener);
        ((QSPanel) this.mView).setSecurityFooter(this.mQsSecurityFooter.getView());
        switchTileLayout(true);
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.addCallback(this.mBrightnessMirrorListener);
        }
        ((PagedTileLayout) ((QSPanel) this.mView).getOrCreateTileLayout()).setOnTouchListener(this.mTileLayoutTouchListener);
        addActiveSubsCallback();
    }

    /* access modifiers changed from: protected */
    public QSTileRevealController createTileRevealController() {
        return this.mQsTileRevealControllerFactory.create(this, (PagedTileLayout) ((QSPanel) this.mView).getOrCreateTileLayout());
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mTunerService.removeTunable((TunerService.Tunable) this.mView);
        ((QSPanel) this.mView).removeOnConfigurationChangedListener(this.mOnConfigurationChangedListener);
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.removeCallback(this.mBrightnessMirrorListener);
        }
        removeActiveSubsCallback();
        super.onViewDetached();
    }

    public void setHeaderContainer(ViewGroup viewGroup) {
        ((QSPanel) this.mView).setHeaderContainer(viewGroup);
    }

    public void setVisibility(int i) {
        ((QSPanel) this.mView).setVisibility(i);
    }

    public void setListening(boolean z, boolean z2) {
        setListening(z && z2);
        if (((QSPanel) this.mView).isListening()) {
            refreshAllTiles();
        }
        this.mQsSecurityFooter.setListening(z);
        if (z) {
            BrightnessController brightnessController = this.mBrightnessController;
            if (brightnessController != null) {
                brightnessController.registerCallbacks();
                return;
            }
            return;
        }
        BrightnessController brightnessController2 = this.mBrightnessController;
        if (brightnessController2 != null) {
            brightnessController2.unregisterCallbacks();
        }
    }

    public void setBrightnessMirror(BrightnessMirrorController brightnessMirrorController) {
        if (MotoFeature.getInstance(getContext()).isCustomPanelView()) {
            this.mBrightnessMirrorController = null;
        } else {
            this.mBrightnessMirrorController = brightnessMirrorController;
        }
        BrightnessMirrorController brightnessMirrorController2 = this.mBrightnessMirrorController;
        if (brightnessMirrorController2 != null) {
            brightnessMirrorController2.removeCallback(this.mBrightnessMirrorListener);
        }
        this.mBrightnessMirrorController = brightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.addCallback(this.mBrightnessMirrorListener);
        }
        updateBrightnessMirror();
    }

    /* access modifiers changed from: private */
    public void updateBrightnessMirror() {
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            this.mBrightnessSlider.setMirrorControllerAndMirror(brightnessMirrorController);
        }
    }

    public QSTileHost getHost() {
        return this.mHost;
    }

    public void openDetails(String str) {
        QSTile tile = getTile(str);
        if (tile != null) {
            ((QSPanel) this.mView).openDetails(tile);
        }
    }

    public void showDeviceMonitoringDialog() {
        this.mQsSecurityFooter.showDeviceMonitoringDialog();
    }

    public void updateResources() {
        ((QSPanel) this.mView).updateResources();
    }

    public void refreshAllTiles() {
        BrightnessController brightnessController = this.mBrightnessController;
        if (brightnessController != null) {
            brightnessController.checkRestrictionAndSetEnabled();
        }
        super.refreshAllTiles();
        this.mQsSecurityFooter.refreshState();
    }

    public void showEdit(View view) {
        view.post(new QSPanelController$$ExternalSyntheticLambda1(this, view));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showEdit$1(View view) {
        if (!this.mQsCustomizerController.isCustomizing()) {
            int[] locationOnScreen = view.getLocationOnScreen();
            this.mQsCustomizerController.show(locationOnScreen[0] + (view.getWidth() / 2), locationOnScreen[1] + (view.getHeight() / 2), false);
        }
    }

    public void setCallback(QSDetail.Callback callback) {
        ((QSPanel) this.mView).setCallback(callback);
    }

    public void setGridContentVisibility(boolean z) {
        int i = z ? 0 : 4;
        setVisibility(i);
        if (this.mGridContentVisible != z) {
            this.mMetricsLogger.visibility(111, i);
        }
        this.mGridContentVisible = z;
    }

    public View getBrightnessView() {
        return ((QSPanel) this.mView).getBrightnessView();
    }

    public void setPageListener(PagedTileLayout.PageListener pageListener) {
        ((QSPanel) this.mView).setPageListener(pageListener);
    }

    public void setContentMargins(int i, int i2) {
        ((QSPanel) this.mView).setContentMargins(i, i2, this.mMediaHost.getHostView());
    }

    public void showDetailAdapter(DetailAdapter detailAdapter, int i, int i2) {
        ((QSPanel) this.mView).showDetailAdapter(true, detailAdapter, new int[]{i, i2});
    }

    public void setFooterPageIndicator(PageIndicator pageIndicator) {
        ((QSPanel) this.mView).setFooterPageIndicator(pageIndicator);
    }

    public boolean isExpanded() {
        return ((QSPanel) this.mView).isExpanded();
    }

    /* access modifiers changed from: package-private */
    public void setPageMargin(int i) {
        ((QSPanel) this.mView).setPageMargin(i);
    }

    public void setupAsDestopView() {
        getBrightnessView().setVisibility(8);
        this.mQsSecurityFooter.getView().setVisibility(8);
    }

    public void setQsExpansion(float f) {
        MediaHierarchyManager mediaHierarchyManager = this.mMediaHierarchyManager;
        if (mediaHierarchyManager != null) {
            mediaHierarchyManager.setQsExpansion(f);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldUseHorizontalLayout() {
        if (!DesktopFeature.isDesktopSupported() || !DesktopFeature.isDesktopDisplayContext(((QSPanel) this.mView).getContext())) {
            return super.shouldUseHorizontalLayout();
        }
        return false;
    }

    private void addActiveSubsCallback() {
        ((DualSimIconController) Dependency.get(DualSimIconController.class)).addCallback(this.mCallback);
    }

    private void removeActiveSubsCallback() {
        ((DualSimIconController) Dependency.get(DualSimIconController.class)).removeCallback(this.mCallback);
    }
}
