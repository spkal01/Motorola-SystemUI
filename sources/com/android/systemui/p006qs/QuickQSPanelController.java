package com.android.systemui.p006qs;

import android.content.res.Configuration;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$integer;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.media.MediaHost;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSPanel;
import com.android.systemui.p006qs.customize.QSCustomizerController;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.statusbar.FeatureFlags;
import java.util.ArrayList;

/* renamed from: com.android.systemui.qs.QuickQSPanelController */
public class QuickQSPanelController extends QSPanelControllerBase<QuickQSPanel> {
    private final QSPanel.OnConfigurationChangedListener mOnConfigurationChangedListener = new QuickQSPanelController$$ExternalSyntheticLambda0(this);

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Configuration configuration) {
        int integer = getResources().getInteger(R$integer.quick_qs_panel_max_columns);
        if (integer != ((QuickQSPanel) this.mView).getNumQuickTiles()) {
            setMaxTiles(integer);
        }
    }

    QuickQSPanelController(QuickQSPanel quickQSPanel, QSTileHost qSTileHost, QSCustomizerController qSCustomizerController, boolean z, MediaHost mediaHost, MetricsLogger metricsLogger, UiEventLogger uiEventLogger, QSLogger qSLogger, DumpManager dumpManager, FeatureFlags featureFlags) {
        super(quickQSPanel, qSTileHost, qSCustomizerController, z, mediaHost, metricsLogger, uiEventLogger, qSLogger, dumpManager, featureFlags);
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        super.onInit();
        this.mMediaHost.setExpansion(0.0f);
        this.mMediaHost.setShowsOnlyActiveMedia(true);
        this.mMediaHost.init(1);
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        ((QuickQSPanel) this.mView).addOnConfigurationChangedListener(this.mOnConfigurationChangedListener);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        super.onViewDetached();
        ((QuickQSPanel) this.mView).removeOnConfigurationChangedListener(this.mOnConfigurationChangedListener);
    }

    public boolean isListening() {
        return ((QuickQSPanel) this.mView).isListening();
    }

    private void setMaxTiles(int i) {
        ((QuickQSPanel) this.mView).setMaxTiles(i);
        setTiles();
    }

    public void setTiles() {
        if (!MotoFeature.getInstance(getContext()).isCustomPanelView()) {
            ArrayList arrayList = new ArrayList();
            for (QSTile add : this.mHost.getTiles()) {
                arrayList.add(add);
                if (arrayList.size() == ((QuickQSPanel) this.mView).getNumQuickTiles()) {
                    break;
                }
            }
            super.setTiles(arrayList, true);
        }
    }

    public void setVisibility(int i) {
        ((QuickQSPanel) this.mView).setVisibility(i);
    }

    public void setContentMargins(int i, int i2) {
        ((QuickQSPanel) this.mView).setContentMargins(i, i2, this.mMediaHost.getHostView());
    }

    public int getNumQuickTiles() {
        return ((QuickQSPanel) this.mView).getNumQuickTiles();
    }
}
