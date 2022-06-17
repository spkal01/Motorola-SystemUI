package com.android.systemui.p006qs;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$id;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.p006qs.QSPanel;
import com.android.systemui.p006qs.QSPrcPanel;
import com.android.systemui.plugins.FalsingManager;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.systemui.qs.QSPrcPanelContainer */
public class QSPrcPanelContainer extends QSPanel {
    static final boolean DEBUG = (!Build.IS_USER);
    private final String TAG = "QSPrcPanelContainer";
    protected final Context mContext;
    private final List<QSPanel.OnConfigurationChangedListener> mOnConfigurationChangedListeners = new ArrayList();
    private QSPrcPanel mQSPrcPanel;
    protected ViewGroup mQSSecurityContainer;

    public QSPrcPanelContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        setOrientation(1);
    }

    /* access modifiers changed from: package-private */
    public void initialize() {
        this.mQSPrcPanel.initialize();
        this.mTileLayout = getOrCreateTileLayout();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        Log.i("QSPrcPanelContainer", "onFinishInflate");
        super.onFinishInflate();
        this.mQSPrcPanel = (QSPrcPanel) findViewById(R$id.qs_prc_panel);
    }

    public QSPanel.QSTileLayout getOrCreateTileLayout() {
        if (this.mTileLayout == null) {
            this.mTileLayout = this.mQSPrcPanel.getUnfixedTileLayout();
        }
        return this.mTileLayout;
    }

    public void setFalsingManager(FalsingManager falsingManager) {
        this.mQSPrcPanel.setFalsingManager(falsingManager);
    }

    public void setQSFooterView(View view) {
        this.mQSPrcPanel.setQSFooterView(view);
    }

    public void removeQSFooter(View view) {
        this.mQSPrcPanel.removeQSFooter(view);
    }

    public void setBrightnessView(View view) {
        this.mQSPrcPanel.setBrightnessView(view);
    }

    /* access modifiers changed from: package-private */
    public View getBrightnessView() {
        return this.mQSPrcPanel.getBrightnessView();
    }

    /* access modifiers changed from: package-private */
    public void addOnConfigurationListener(QSPrcPanel.OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mQSPrcPanel.addOnConfigurationChangedListener(onConfigurationChangedListener);
    }

    /* access modifiers changed from: package-private */
    public void removeOnConfigurationListener(QSPrcPanel.OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mQSPrcPanel.removeOnConfigurationChangedListener(onConfigurationChangedListener);
    }

    public void setQSSecurityContainer(ViewGroup viewGroup) {
        this.mQSSecurityContainer = viewGroup;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        switchSecurityFooter();
    }

    /* access modifiers changed from: protected */
    public void switchSecurityFooter() {
        ViewGroup viewGroup;
        if (this.mSecurityFooter == null) {
            return;
        }
        if (this.mContext.getResources().getConfiguration().orientation != 2 || (viewGroup = this.mHeaderContainer) == null) {
            ViewGroup viewGroup2 = this.mQSSecurityContainer;
            if (viewGroup2 != null) {
                switchToParent(this.mSecurityFooter, viewGroup2, 0);
                return;
            }
            return;
        }
        switchToParent(this.mSecurityFooter, viewGroup, 0);
    }

    public void updateResources() {
        Log.i("QSPrcPanelContainer", "updateResources");
        this.mQSPrcPanel.updateResources();
        QSPanel.QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout != null) {
            qSTileLayout.updateResources();
        }
    }

    /* access modifiers changed from: package-private */
    public QSPanel.QSTileLayout getTileLayout() {
        return this.mTileLayout;
    }

    public void updateTileLayout(boolean z) {
        this.mTileLayout.setMinRows(1);
        if (DesktopFeature.isDesktopDisplayContext(this.mContext)) {
            this.mTileLayout.setMaxColumns(4);
        } else {
            this.mTileLayout.setMaxColumns(100);
        }
    }

    public void setExpanded(boolean z) {
        if (this.mExpanded != z) {
            this.mExpanded = z;
            if (!z) {
                this.mQSPrcPanel.scrollToStart();
            }
        }
    }

    public int getDesktopQsPanelMaxHeight() {
        QSPrcPanel qSPrcPanel = this.mQSPrcPanel;
        if (qSPrcPanel != null) {
            return qSPrcPanel.getDesktopQsPanelMaxHeight();
        }
        return 0;
    }
}
