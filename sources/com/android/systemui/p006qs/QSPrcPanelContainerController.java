package com.android.systemui.p006qs;

import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.media.MediaHost;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.p006qs.PagedTileLayout;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.QSPanelControllerBase;
import com.android.systemui.p006qs.QSPrcPanel;
import com.android.systemui.p006qs.QSTileRevealController;
import com.android.systemui.p006qs.customize.QSCustomizerController;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.settings.brightness.BrightnessController;
import com.android.systemui.settings.brightness.BrightnessSlider;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.tuner.TunerService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.android.systemui.qs.QSPrcPanelContainerController */
public class QSPrcPanelContainerController extends QSPanelController {
    static final boolean DEBUG = (!Build.IS_USER);
    private final String TAG = "QSPrcPanelContainerController";
    private final BrightnessController mBrightnessController;
    private BrightnessMirrorController mBrightnessMirrorController;
    private final BrightnessMirrorController.BrightnessMirrorListener mBrightnessMirrorListener = new QSPrcPanelContainerController$$ExternalSyntheticLambda1(this);
    private final BrightnessSlider mBrightnessSlider;
    private final BrightnessSlider.Factory mBrightnessSliderFactory;
    private String mCachedSpecs = "";
    private final FalsingManager mFalsingManager;
    private int mFixedTileNum;
    private final QSPrcPanel.OnConfigurationChangedListener mOnConfigurationChangedListener = new QSPrcPanel.OnConfigurationChangedListener() {
        public void onConfigurationChange(Configuration configuration) {
            if (QSPrcPanelContainerController.DEBUG) {
                Log.i("QSPrcPanelContainerController", "OnConfigurationChangedListener");
            }
            ((QSPanel) QSPrcPanelContainerController.this.mView).updateResources();
            QSPrcPanelContainerController.this.mQsSecurityFooter.onConfigurationChanged();
            if (((QSPanel) QSPrcPanelContainerController.this.mView).isListening()) {
                QSPrcPanelContainerController.this.refreshAllTiles();
            }
            QSPrcPanelContainerController.this.updateBrightnessMirror();
        }
    };
    private QSFooterView mQSFooterView;
    private final QSHost.Callback mQSHostCallback = new QSPrcPanelContainerController$$ExternalSyntheticLambda0(this);

    /* access modifiers changed from: protected */
    public QSTileRevealController createTileRevealController() {
        return null;
    }

    public void setContentMargins(int i, int i2) {
    }

    public void setFooterPageIndicator(PageIndicator pageIndicator) {
    }

    public void setPageListener(PagedTileLayout.PageListener pageListener) {
    }

    /* access modifiers changed from: package-private */
    public void setPageMargin(int i) {
    }

    public void setRevealExpansion(float f) {
    }

    public void setUsingHorizontalLayoutChangeListener(Runnable runnable) {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        updateBrightnessMirror();
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    QSPrcPanelContainerController(QSPrcPanelContainer qSPrcPanelContainer, QSSecurityFooter qSSecurityFooter, TunerService tunerService, QSTileHost qSTileHost, QSCustomizerController qSCustomizerController, boolean z, MediaHost mediaHost, QSTileRevealController.Factory factory, DumpManager dumpManager, MetricsLogger metricsLogger, UiEventLogger uiEventLogger, QSLogger qSLogger, BrightnessController.Factory factory2, BrightnessSlider.Factory factory3, FalsingManager falsingManager, FeatureFlags featureFlags, QSFooterView qSFooterView) {
        super(qSPrcPanelContainer, qSSecurityFooter, tunerService, qSTileHost, qSCustomizerController, z, mediaHost, factory, dumpManager, metricsLogger, uiEventLogger, qSLogger, factory2, factory3, falsingManager, featureFlags);
        BrightnessSlider.Factory factory4 = factory3;
        this.mBrightnessSliderFactory = factory4;
        BrightnessSlider create = factory4.create(getContext(), (ViewGroup) this.mView);
        this.mBrightnessSlider = create;
        ((QSPanel) this.mView).setBrightnessView(create.getRootView());
        this.mBrightnessController = factory2.create(create);
        this.mQSFooterView = qSFooterView;
        this.mFalsingManager = falsingManager;
        this.mFixedTileNum = getContext().getResources().getInteger(R$integer.zz_moto_prc_fixed_panel_max_tiles);
    }

    public List<View> getAnimatorView() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mView);
        arrayList.add(this.mQSFooterView);
        return arrayList;
    }

    public void onInit() {
        super.onInit();
        this.mBrightnessSlider.init();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        if (DEBUG) {
            Log.i("QSPrcPanelContainerController", "onViewAttached");
        }
        this.mHost.addCallback(this.mQSHostCallback);
        setTiles();
        ((QSPanel) this.mView).updateResources();
        if (((QSPanel) this.mView).isListening()) {
            refreshAllTiles();
        }
        ((QSPrcPanelContainer) this.mView).addOnConfigurationListener(this.mOnConfigurationChangedListener);
        ((QSPanel) this.mView).setSecurityFooter(this.mQsSecurityFooter.getView());
        switchTileLayout(true);
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.addCallback(this.mBrightnessMirrorListener);
        }
        ((QSPrcPanelContainer) this.mView).setFalsingManager(this.mFalsingManager);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        if (DEBUG) {
            Log.i("QSPrcPanelContainerController", "onViewDetached");
        }
        ((QSPrcPanelContainer) this.mView).removeOnConfigurationListener(this.mOnConfigurationChangedListener);
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.removeCallback(this.mBrightnessMirrorListener);
        }
        this.mHost.removeCallback(this.mQSHostCallback);
        ((QSPanel) this.mView).getTileLayout().setListening(false, (UiEventLogger) null);
        Iterator<QSPanelControllerBase.TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            QSPanelControllerBase.TileRecord next = it.next();
            next.tile.removeCallbacks();
            if (DesktopFeature.isDesktopDisplayContext(((QSPanel) this.mView).mContext)) {
                ((QSPanel) this.mView).removeTile(next);
            }
        }
        this.mRecords.clear();
    }

    public void setBrightnessMirror(BrightnessMirrorController brightnessMirrorController) {
        this.mBrightnessMirrorController = brightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.removeCallback(this.mBrightnessMirrorListener);
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

    public void setHeaderContainer(ViewGroup viewGroup) {
        ((QSPanel) this.mView).setHeaderContainer(viewGroup);
    }

    public void setQSSecurityContainer(ViewGroup viewGroup) {
        ((QSPanel) this.mView).setQSSecurityContainer(viewGroup);
    }

    /* access modifiers changed from: package-private */
    public boolean switchTileLayout(boolean z) {
        if (!z) {
            return false;
        }
        ((QSPrcPanelContainer) this.mView).updateTileLayout(z);
        return true;
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
            this.mBrightnessController.registerCallbacks();
        } else {
            this.mBrightnessController.unregisterCallbacks();
        }
    }

    /* access modifiers changed from: package-private */
    public void setListening(boolean z) {
        ((QSPanel) this.mView).setListening(z);
        if (((QSPanel) this.mView).getTileLayout() != null) {
            ((QSPanel) this.mView).getTileLayout().setListening(z, (UiEventLogger) null);
        }
    }

    public QSTileHost getHost() {
        return this.mHost;
    }

    public void updateResources() {
        ((QSPanel) this.mView).updateResources();
    }

    public void refreshAllTiles() {
        this.mBrightnessController.checkRestrictionAndSetEnabled();
        this.mQsSecurityFooter.refreshState();
        Iterator<QSPanelControllerBase.TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            it.next().tile.refreshState();
        }
    }

    public void setTiles() {
        ArrayList arrayList = new ArrayList();
        this.mHost.getTiles().size();
        int i = 0;
        for (QSTile next : this.mHost.getTiles()) {
            if (i > this.mFixedTileNum - 1) {
                arrayList.add(next);
            }
            i++;
        }
        if (DEBUG) {
            Log.i("QSPrcPanelContainerController", "QSTileLog setTiles() " + arrayList.toString());
        }
        setTiles(arrayList, false);
    }

    public View getBrightnessView() {
        return ((QSPanel) this.mView).getBrightnessView();
    }

    public void setupAsDestopView() {
        super.setupAsDestopView();
        this.mQSFooterView.setVisibility(8);
        View findViewById = ((QSPanel) this.mView).findViewById(R$id.prc_security_footer_container);
        if (findViewById != null) {
            findViewById.setVisibility(8);
        }
    }

    public int getDesktopQsPanelMaxHeight() {
        return ((QSPrcPanelContainer) this.mView).getDesktopQsPanelMaxHeight();
    }
}
