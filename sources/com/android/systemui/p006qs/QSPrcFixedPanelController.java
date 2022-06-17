package com.android.systemui.p006qs;

import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$integer;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.media.MediaHost;
import com.android.systemui.p006qs.QSPanel;
import com.android.systemui.p006qs.customize.QSCustomizerController;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.statusbar.FeatureFlags;
import java.util.ArrayList;

/* renamed from: com.android.systemui.qs.QSPrcFixedPanelController */
public class QSPrcFixedPanelController extends QSPanelControllerBase<QSPrcFixedPanel> {
    private static final boolean DEBUG = (!Build.IS_USER);
    private final QSPanel.OnConfigurationChangedListener mOnConfigurationChangedListener = new QSPrcFixedPanelController$$ExternalSyntheticLambda0(this);

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Configuration configuration) {
        int integer = getResources().getInteger(R$integer.zz_moto_prc_fixed_panel_max_tiles);
        if (integer != ((QSPrcFixedPanel) this.mView).getNumFixedTiles()) {
            setMaxTiles(integer);
        }
    }

    QSPrcFixedPanelController(QSPrcFixedPanel qSPrcFixedPanel, QSTileHost qSTileHost, QSCustomizerController qSCustomizerController, boolean z, MediaHost mediaHost, MetricsLogger metricsLogger, UiEventLogger uiEventLogger, QSLogger qSLogger, DumpManager dumpManager, FeatureFlags featureFlags) {
        super(qSPrcFixedPanel, qSTileHost, qSCustomizerController, z, mediaHost, metricsLogger, uiEventLogger, qSLogger, dumpManager, featureFlags);
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        super.onInit();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        if (DEBUG) {
            Log.i("QSPrcFixedPanelController", "onViewAttached()");
        }
        super.onViewAttached();
        ((QSPrcFixedPanel) this.mView).addOnConfigurationChangedListener(this.mOnConfigurationChangedListener);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        if (DEBUG) {
            Log.i("QSPrcFixedPanelController", "onViewDetached()");
        }
        super.onViewDetached();
        ((QSPrcFixedPanel) this.mView).removeOnConfigurationChangedListener(this.mOnConfigurationChangedListener);
    }

    private void setMaxTiles(int i) {
        ((QSPrcFixedPanel) this.mView).setMaxTiles(i);
        setTiles();
    }

    public void setTiles() {
        ArrayList arrayList = new ArrayList();
        for (QSTile add : this.mHost.getTiles()) {
            arrayList.add(add);
            if (arrayList.size() == ((QSPrcFixedPanel) this.mView).getNumFixedTiles()) {
                break;
            }
        }
        if (DEBUG) {
            Log.i("QSPrcFixedPanelController", "QSTileLog setTiles() " + arrayList.toString());
        }
        super.setTiles(arrayList, true);
    }

    public void setListening(boolean z, boolean z2) {
        setListening(z && z2);
        if (((QSPrcFixedPanel) this.mView).isListening()) {
            refreshAllTiles();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean switchTileLayout(boolean z) {
        if (!z) {
            return false;
        }
        ((QSPrcFixedPanel) this.mView).addTileLayoutToParent();
        return true;
    }
}
