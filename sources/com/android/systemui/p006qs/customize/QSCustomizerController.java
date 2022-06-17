package com.android.systemui.p006qs.customize;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSEditEvent;
import com.android.systemui.p006qs.QSFragment;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.p006qs.customize.TileQueryHelper;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.ViewController;
import com.motorola.systemui.p014qs.DynamicTileManager;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.systemui.qs.customize.QSCustomizerController */
public class QSCustomizerController extends ViewController<QSCustomizer> {
    private final ConfigurationController mConfigurationController;
    private final ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onConfigChanged(Configuration configuration) {
            boolean z = (configuration.uiMode & 48) == 32;
            if (QSCustomizerController.this.mIsPrcCustom && QSCustomizerController.this.mIsNightMode != z) {
                boolean unused = QSCustomizerController.this.mIsNightMode = z;
                QSCustomizerController.this.updateToolBarUIMode();
            }
            ((QSCustomizer) QSCustomizerController.this.mView).updateNavBackDrop(configuration, QSCustomizerController.this.mLightBarController);
            ((QSCustomizer) QSCustomizerController.this.mView).updateResources();
            if (QSCustomizerController.this.mTileAdapter.updateNumColumns()) {
                RecyclerView.LayoutManager layoutManager = ((QSCustomizer) QSCustomizerController.this.mView).getRecyclerView().getLayoutManager();
                if (layoutManager instanceof GridLayoutManager) {
                    ((GridLayoutManager) layoutManager).setSpanCount(QSCustomizerController.this.mTileAdapter.getNumColumns());
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsNightMode;
    /* access modifiers changed from: private */
    public boolean mIsPrcCustom = MotoFeature.getInstance(getContext()).isCustomPanelView();
    private final KeyguardStateController.Callback mKeyguardCallback = new KeyguardStateController.Callback() {
        public void onKeyguardShowingChanged() {
            if (((QSCustomizer) QSCustomizerController.this.mView).isAttachedToWindow() && QSCustomizerController.this.mKeyguardStateController.isShowing() && !((QSCustomizer) QSCustomizerController.this.mView).isOpening()) {
                QSCustomizerController.this.hide();
            }
        }
    };
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public final LightBarController mLightBarController;
    private final Toolbar.OnMenuItemClickListener mOnMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (menuItem.getItemId() != 1) {
                return false;
            }
            QSCustomizerController.this.mUiEventLogger.log(QSEditEvent.QS_EDIT_RESET);
            QSCustomizerController.this.reset();
            return false;
        }
    };
    private final QSTileHost mQsTileHost;
    private final ScreenLifecycle mScreenLifecycle;
    /* access modifiers changed from: private */
    public final TileAdapter mTileAdapter;
    private final TileQueryHelper mTileQueryHelper;
    private Toolbar mToolbar;
    /* access modifiers changed from: private */
    public final UiEventLogger mUiEventLogger;

    /* access modifiers changed from: private */
    public void updateToolBarUIMode() {
        Context context = getContext();
        getContext().getResources();
        ViewGroup viewGroup = (ViewGroup) ((QSCustomizer) this.mView).findViewById(R$id.customize_container);
        viewGroup.removeView(this.mToolbar);
        Toolbar toolbar = (Toolbar) ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R$layout.prc_qs_customize_toolbar, viewGroup, false);
        this.mToolbar = toolbar;
        viewGroup.addView(toolbar, 0);
        this.mToolbar.setOnMenuItemClickListener(this.mOnMenuItemClickListener);
        this.mToolbar.setNavigationOnClickListener(new QSCustomizerController$$ExternalSyntheticLambda0(this));
        ((QSCustomizer) this.mView).updateToolBar();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateToolBarUIMode$0(View view) {
        hide();
    }

    protected QSCustomizerController(QSCustomizer qSCustomizer, TileQueryHelper tileQueryHelper, QSTileHost qSTileHost, TileAdapter tileAdapter, ScreenLifecycle screenLifecycle, KeyguardStateController keyguardStateController, LightBarController lightBarController, ConfigurationController configurationController, UiEventLogger uiEventLogger) {
        super(qSCustomizer);
        this.mTileQueryHelper = tileQueryHelper;
        this.mQsTileHost = qSTileHost;
        this.mTileAdapter = tileAdapter;
        this.mScreenLifecycle = screenLifecycle;
        this.mKeyguardStateController = keyguardStateController;
        this.mLightBarController = lightBarController;
        this.mConfigurationController = configurationController;
        this.mUiEventLogger = uiEventLogger;
        this.mToolbar = (Toolbar) ((QSCustomizer) this.mView).findViewById(16908710);
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        ((QSCustomizer) this.mView).updateNavBackDrop(getResources().getConfiguration(), this.mLightBarController);
        this.mConfigurationController.addCallback(this.mConfigurationListener);
        this.mTileQueryHelper.setListener(this.mTileAdapter);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.qs_tile_margin_horizontal) / 2;
        if (this.mIsPrcCustom) {
            dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_tile_margin_horizontal) / 2;
            updateToolBarUIMode();
        }
        this.mTileAdapter.changeHalfMargin(dimensionPixelSize);
        final RecyclerView recyclerView = ((QSCustomizer) this.mView).getRecyclerView();
        recyclerView.setAdapter(this.mTileAdapter);
        this.mTileAdapter.getItemTouchHelper().attachToRecyclerView(recyclerView);
        C12204 r1 = new GridLayoutManager(getContext(), this.mTileAdapter.getNumColumns()) {
            public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            }

            public void calculateItemDecorationsForChild(View view, Rect rect) {
                if (!(view instanceof TextView)) {
                    rect.setEmpty();
                    QSCustomizerController.this.mTileAdapter.getMarginItemDecoration().getItemOffsets(rect, view, recyclerView, new RecyclerView.State());
                    ((GridLayoutManager.LayoutParams) view.getLayoutParams()).leftMargin = rect.left;
                    ((GridLayoutManager.LayoutParams) view.getLayoutParams()).rightMargin = rect.right;
                }
            }
        };
        r1.setSpanSizeLookup(this.mTileAdapter.getSizeLookup());
        recyclerView.setLayoutManager(r1);
        recyclerView.addItemDecoration(this.mTileAdapter.getItemDecoration());
        recyclerView.addItemDecoration(this.mTileAdapter.getMarginItemDecoration());
        if (this.mIsPrcCustom) {
            ((QSCustomizer) this.mView).updateRecyclerViewLayout(getResources().getConfiguration().orientation);
        }
        this.mToolbar.setOnMenuItemClickListener(this.mOnMenuItemClickListener);
        this.mToolbar.setNavigationOnClickListener(new QSCustomizerController$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$1(View view) {
        hide();
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mTileQueryHelper.setListener((TileQueryHelper.TileStateListener) null);
        this.mToolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) null);
        this.mConfigurationController.removeCallback(this.mConfigurationListener);
    }

    /* access modifiers changed from: private */
    public void reset() {
        List<String> motoSpecs = QSTileHost.getMotoSpecs(getContext());
        resetAutoTiles(motoSpecs);
        DynamicTileManager.getInstance(this.mQsTileHost).reloadTiles(motoSpecs);
        this.mTileAdapter.resetTileSpecs(motoSpecs);
    }

    private void resetAutoTiles(List<String> list) {
        this.mQsTileHost.resetAutoTiles(list);
    }

    public boolean isCustomizing() {
        return ((QSCustomizer) this.mView).isCustomizing();
    }

    public void show(int i, int i2, boolean z) {
        if (!((QSCustomizer) this.mView).isShown()) {
            setTileSpecs();
            if (z) {
                ((QSCustomizer) this.mView).showImmediately();
            } else {
                ((QSCustomizer) this.mView).show(i, i2, this.mTileAdapter);
                this.mUiEventLogger.log(QSEditEvent.QS_EDIT_OPEN);
            }
            this.mTileQueryHelper.queryTiles(this.mQsTileHost);
            this.mKeyguardStateController.addCallback(this.mKeyguardCallback);
            ((QSCustomizer) this.mView).updateNavColors(this.mLightBarController);
        }
    }

    public void setQs(QSFragment qSFragment) {
        ((QSCustomizer) this.mView).setQs(qSFragment);
    }

    public void restoreInstanceState(Bundle bundle) {
        if (bundle.getBoolean("qs_customizing")) {
            ((QSCustomizer) this.mView).setVisibility(0);
            ((QSCustomizer) this.mView).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    ((QSCustomizer) QSCustomizerController.this.mView).removeOnLayoutChangeListener(this);
                    QSCustomizerController.this.show(0, 0, true);
                }
            });
        }
    }

    public void saveInstanceState(Bundle bundle) {
        if (((QSCustomizer) this.mView).isShown()) {
            this.mKeyguardStateController.removeCallback(this.mKeyguardCallback);
        }
        bundle.putBoolean("qs_customizing", ((QSCustomizer) this.mView).isCustomizing());
    }

    public void setEditLocation(int i, int i2) {
        ((QSCustomizer) this.mView).setEditLocation(i, i2);
    }

    public void setContainer(NotificationsQuickSettingsContainer notificationsQuickSettingsContainer) {
        ((QSCustomizer) this.mView).setContainer(notificationsQuickSettingsContainer);
    }

    public boolean isShown() {
        return ((QSCustomizer) this.mView).isShown();
    }

    public void hide() {
        boolean z = this.mScreenLifecycle.getScreenState() != 0;
        if (((QSCustomizer) this.mView).isShown()) {
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_CLOSED);
            this.mToolbar.dismissPopupMenus();
            ((QSCustomizer) this.mView).setCustomizing(false);
            save();
            ((QSCustomizer) this.mView).hide(z);
            ((QSCustomizer) this.mView).updateNavColors(this.mLightBarController);
            this.mKeyguardStateController.removeCallback(this.mKeyguardCallback);
        }
    }

    private void save() {
        if (this.mTileQueryHelper.isFinished()) {
            this.mTileAdapter.saveSpecs(this.mQsTileHost);
        }
    }

    private void setTileSpecs() {
        ArrayList arrayList = new ArrayList();
        for (QSTile tileSpec : this.mQsTileHost.getTiles()) {
            arrayList.add(tileSpec.getTileSpec());
        }
        ((QSCustomizer) this.mView).getRecyclerView().setItemViewCacheSize(arrayList.size());
        this.mTileAdapter.setTileSpecs(arrayList);
    }
}
