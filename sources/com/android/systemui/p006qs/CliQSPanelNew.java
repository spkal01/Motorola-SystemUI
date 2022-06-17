package com.android.systemui.p006qs;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.logging.UiEventLogger;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.CliQSDetail;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.QSPanel;
import com.android.systemui.p006qs.QSPanelControllerBase;
import com.android.systemui.p006qs.external.CustomTile;
import com.android.systemui.p006qs.tileimpl.QSTileViewImpl;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.p005qs.QSTileView;
import com.android.systemui.settings.brightness.BrightnessController;
import com.android.systemui.settings.brightness.BrightnessSlider;
import com.android.systemui.settings.brightness.BrightnessSliderView;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.CliStatusBarWindowController;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusIconContainer;
import com.android.systemui.statusbar.policy.Clock;
import com.motorola.systemui.cli.media.CliMediaVisibleListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import motorola.core_services.cli.CLIManager;

/* renamed from: com.android.systemui.qs.CliQSPanelNew */
public class CliQSPanelNew extends LinearLayout implements QSHost.Callback {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private final ArrayList<String> mAdvancedSettinglistTiles;
    private BrightnessController mBrightnessController;
    private int mBrightnessPaddingTopDefault;
    private int mBrightnessPaddingTopWithMV;
    protected BrightnessSlider mBrightnessSlider;
    /* access modifiers changed from: private */
    public BrightnessSliderView mBrightnessSliderView;
    private BroadcastDispatcher mBroadcastDispatcher;
    private CliQSDetail.Callback mCallback;
    /* access modifiers changed from: private */
    public LinearLayout mCarrierAndClock;
    private CLIManager mCliManager;
    private CliMediaVisibleListener mCliMediaVisibleListener;
    /* access modifiers changed from: private */
    public LinearLayout mCliQsContainer;
    /* access modifiers changed from: private */
    public int mCliQsPaddingHorizontalDefault;
    /* access modifiers changed from: private */
    public int mCliQsPaddingHorizontalWithMV;
    private CliStatusBarWindowController mCliStatusBarWindowController;
    private CommandQueue mCommandQueue;
    private List<String> mCurrentCliAccessPackages;
    private QSPanel.Record mDetailRecord;
    private final ArrayList<String> mDisallowlistTiles;
    private FalsingManager mFalsingManager;
    private final C1160H mHandler;
    private QSTileHost mHost;
    private boolean mListening;
    /* access modifiers changed from: private */
    public boolean mMVVisibleInited;
    private int mMaxTileCount;
    /* access modifiers changed from: private */
    public boolean mMediaViewVisible;
    /* access modifiers changed from: private */
    public PageIndicator mPageIndicator;
    /* access modifiers changed from: private */
    public int mPageIndicatorMarginTopDefault;
    /* access modifiers changed from: private */
    public int mPageIndicatorMarginTopWithMV;
    /* access modifiers changed from: private */
    public PagedTileLayout mPagedTileLayout;
    /* access modifiers changed from: private */
    public int mPagedTileLayoutMarginTopDefault;
    /* access modifiers changed from: private */
    public int mPagedTileLayoutMarginTopWithMV;
    protected final ArrayList<QSPanelControllerBase.TileRecord> mRecords;
    private StatusBarIconController.TintedIconManager mTintedIconManager;

    public CliMediaVisibleListener getCliMediaVisibleListener() {
        return this.mCliMediaVisibleListener;
    }

    public CliQSPanelNew(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHandler = new C1160H();
        this.mCurrentCliAccessPackages = new ArrayList();
        this.mRecords = new ArrayList<>();
        this.mDisallowlistTiles = new ArrayList<>();
        this.mAdvancedSettinglistTiles = new ArrayList<>();
        this.mMVVisibleInited = false;
        this.mCliMediaVisibleListener = new CliMediaVisibleListener() {
            public void setMediaExpansion(float f) {
                float f2 = 1.0f - f;
                CliQSPanelNew.this.mCliQsContainer.setAlpha(f2);
                CliQSPanelNew.this.mBrightnessSliderView.setAlpha(f2);
            }

            public void visibilityChanged(boolean z) {
                if (CliQSPanelNew.DEBUG) {
                    Log.d("CLI-QSMV-CliQSPanelNew", "Cli MediaView visible change to: " + z);
                }
                if (CliQSPanelNew.this.mMediaViewVisible != z || !CliQSPanelNew.this.mMVVisibleInited) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) CliQSPanelNew.this.mPagedTileLayout.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) CliQSPanelNew.this.mPageIndicator.getLayoutParams();
                    if (z) {
                        CliQSPanelNew.this.mPagedTileLayout.setMaxColumns(CliQSPanelNew.this.getResources().getInteger(R$integer.cli_qs_columns_for_small));
                        marginLayoutParams.topMargin = CliQSPanelNew.this.mPagedTileLayoutMarginTopWithMV;
                        CliQSPanelNew.this.mCarrierAndClock.setVisibility(8);
                        CliQSPanelNew.this.mCliQsContainer.setPaddingRelative(CliQSPanelNew.this.mCliQsPaddingHorizontalWithMV, 0, CliQSPanelNew.this.mCliQsPaddingHorizontalWithMV, 0);
                        marginLayoutParams2.topMargin = CliQSPanelNew.this.mPageIndicatorMarginTopWithMV;
                    } else {
                        CliQSPanelNew.this.mPagedTileLayout.setMaxColumns(CliQSPanelNew.this.getResources().getInteger(R$integer.cli_qs_columns_for_large));
                        marginLayoutParams.topMargin = CliQSPanelNew.this.mPagedTileLayoutMarginTopDefault;
                        CliQSPanelNew.this.mCarrierAndClock.setVisibility(0);
                        CliQSPanelNew.this.mCliQsContainer.setPaddingRelative(CliQSPanelNew.this.mCliQsPaddingHorizontalDefault, 0, CliQSPanelNew.this.mCliQsPaddingHorizontalDefault, 0);
                        marginLayoutParams2.topMargin = CliQSPanelNew.this.mPageIndicatorMarginTopDefault;
                        CliQSPanelNew.this.mCliQsContainer.setAlpha(1.0f);
                        CliQSPanelNew.this.mBrightnessSliderView.setAlpha(1.0f);
                    }
                    CliQSPanelNew.this.mPagedTileLayout.setLayoutParams(marginLayoutParams);
                    CliQSPanelNew.this.mPageIndicator.setLayoutParams(marginLayoutParams2);
                    CliQSPanelNew.this.mPagedTileLayout.updateCliMediaViewVisibility(z);
                    CliQSPanelNew.this.mPagedTileLayout.requestLayout();
                }
                boolean unused = CliQSPanelNew.this.mMediaViewVisible = z;
                if (!CliQSPanelNew.this.mMVVisibleInited) {
                    boolean unused2 = CliQSPanelNew.this.mMVVisibleInited = true;
                }
            }
        };
        setOrientation(1);
    }

    public CliQSPanelNew(Context context, AttributeSet attributeSet, QSTileHost qSTileHost, CommandQueue commandQueue, FalsingManager falsingManager, BroadcastDispatcher broadcastDispatcher) {
        super(context, attributeSet);
        this.mHandler = new C1160H();
        this.mCurrentCliAccessPackages = new ArrayList();
        this.mRecords = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        this.mDisallowlistTiles = arrayList;
        ArrayList<String> arrayList2 = new ArrayList<>();
        this.mAdvancedSettinglistTiles = arrayList2;
        this.mMVVisibleInited = false;
        this.mCliMediaVisibleListener = new CliMediaVisibleListener() {
            public void setMediaExpansion(float f) {
                float f2 = 1.0f - f;
                CliQSPanelNew.this.mCliQsContainer.setAlpha(f2);
                CliQSPanelNew.this.mBrightnessSliderView.setAlpha(f2);
            }

            public void visibilityChanged(boolean z) {
                if (CliQSPanelNew.DEBUG) {
                    Log.d("CLI-QSMV-CliQSPanelNew", "Cli MediaView visible change to: " + z);
                }
                if (CliQSPanelNew.this.mMediaViewVisible != z || !CliQSPanelNew.this.mMVVisibleInited) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) CliQSPanelNew.this.mPagedTileLayout.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) CliQSPanelNew.this.mPageIndicator.getLayoutParams();
                    if (z) {
                        CliQSPanelNew.this.mPagedTileLayout.setMaxColumns(CliQSPanelNew.this.getResources().getInteger(R$integer.cli_qs_columns_for_small));
                        marginLayoutParams.topMargin = CliQSPanelNew.this.mPagedTileLayoutMarginTopWithMV;
                        CliQSPanelNew.this.mCarrierAndClock.setVisibility(8);
                        CliQSPanelNew.this.mCliQsContainer.setPaddingRelative(CliQSPanelNew.this.mCliQsPaddingHorizontalWithMV, 0, CliQSPanelNew.this.mCliQsPaddingHorizontalWithMV, 0);
                        marginLayoutParams2.topMargin = CliQSPanelNew.this.mPageIndicatorMarginTopWithMV;
                    } else {
                        CliQSPanelNew.this.mPagedTileLayout.setMaxColumns(CliQSPanelNew.this.getResources().getInteger(R$integer.cli_qs_columns_for_large));
                        marginLayoutParams.topMargin = CliQSPanelNew.this.mPagedTileLayoutMarginTopDefault;
                        CliQSPanelNew.this.mCarrierAndClock.setVisibility(0);
                        CliQSPanelNew.this.mCliQsContainer.setPaddingRelative(CliQSPanelNew.this.mCliQsPaddingHorizontalDefault, 0, CliQSPanelNew.this.mCliQsPaddingHorizontalDefault, 0);
                        marginLayoutParams2.topMargin = CliQSPanelNew.this.mPageIndicatorMarginTopDefault;
                        CliQSPanelNew.this.mCliQsContainer.setAlpha(1.0f);
                        CliQSPanelNew.this.mBrightnessSliderView.setAlpha(1.0f);
                    }
                    CliQSPanelNew.this.mPagedTileLayout.setLayoutParams(marginLayoutParams);
                    CliQSPanelNew.this.mPageIndicator.setLayoutParams(marginLayoutParams2);
                    CliQSPanelNew.this.mPagedTileLayout.updateCliMediaViewVisibility(z);
                    CliQSPanelNew.this.mPagedTileLayout.requestLayout();
                }
                boolean unused = CliQSPanelNew.this.mMediaViewVisible = z;
                if (!CliQSPanelNew.this.mMVVisibleInited) {
                    boolean unused2 = CliQSPanelNew.this.mMVVisibleInited = true;
                }
            }
        };
        this.mHost = qSTileHost;
        this.mCommandQueue = commandQueue;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mFalsingManager = falsingManager;
        setOrientation(1);
        Resources resources = getResources();
        this.mMaxTileCount = resources.getInteger(R$integer.config_cli_qs_max_tile_count);
        arrayList.addAll(Arrays.asList(resources.getString(R$string.config_cli_qs_tiles_disallowed_list).split(",")));
        arrayList2.addAll(Arrays.asList(resources.getString(R$string.config_cli_qs_tiles_support_advance).split(",")));
        this.mCliStatusBarWindowController = (CliStatusBarWindowController) Dependency.get(CliStatusBarWindowController.class);
        this.mBrightnessPaddingTopDefault = resources.getDimensionPixelSize(R$dimen.cli_qs_brightness_container_paddingTop_default);
        this.mBrightnessPaddingTopWithMV = resources.getDimensionPixelSize(R$dimen.cli_qs_brightness_container_paddingTop_with_mv);
        this.mPagedTileLayoutMarginTopDefault = resources.getDimensionPixelSize(R$dimen.cli_qs_pagedTileLayout_marginTop_default);
        this.mPagedTileLayoutMarginTopWithMV = resources.getDimensionPixelSize(R$dimen.cli_qs_pagedTileLayout_marginTop_with_mv);
        this.mCliQsPaddingHorizontalDefault = resources.getDimensionPixelSize(R$dimen.cli_qs_container_paddingHorizontal_default);
        this.mCliQsPaddingHorizontalWithMV = resources.getDimensionPixelSize(R$dimen.cli_qs_container_paddingHorizontal_with_mv);
        this.mPageIndicatorMarginTopDefault = resources.getDimensionPixelSize(R$dimen.cli_qs_page_indicator_marginTop_default);
        this.mPageIndicatorMarginTopWithMV = resources.getDimensionPixelSize(R$dimen.cli_qs_page_indicator_marginTop_with_mv);
        try {
            this.mCliManager = CLIManager.getInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        int colorAttr = getColorAttr(16842806);
        int colorAttr2 = getColorAttr(16842800);
        StatusBarIconController.TintedIconManager tintedIconManager = new StatusBarIconController.TintedIconManager((StatusIconContainer) findViewById(R$id.cli_statusIcons), (FeatureFlags) Dependency.get(FeatureFlags.class));
        this.mTintedIconManager = tintedIconManager;
        tintedIconManager.setTint(colorAttr2);
        BrightnessSliderView brightnessSliderView = (BrightnessSliderView) findViewById(R$id.cli_brightness_slider);
        this.mBrightnessSliderView = brightnessSliderView;
        this.mBrightnessSlider = new BrightnessSlider(brightnessSliderView, this.mFalsingManager);
        this.mBrightnessController = new BrightnessController(getContext(), this.mBrightnessSlider, this.mBroadcastDispatcher);
        this.mBrightnessSlider.init();
        this.mPagedTileLayout = (PagedTileLayout) findViewById(R$id.cli_qs_pager);
        PageIndicator pageIndicator = (PageIndicator) findViewById(R$id.cli_qs_page_indicator);
        this.mPageIndicator = pageIndicator;
        this.mPagedTileLayout.setPageIndicator(pageIndicator);
        this.mCliQsContainer = (LinearLayout) findViewById(R$id.cli_qs_container);
        this.mCarrierAndClock = (LinearLayout) findViewById(R$id.carrier_and_clock);
        final TextView textView = (TextView) findViewById(R$id.cli_divider_symbol);
        textView.setTextColor(colorAttr);
        TextView textView2 = (TextView) findViewById(R$id.cli_carrier_text);
        textView2.setTextColor(colorAttr);
        textView2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                textView.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
            }
        });
        ((Clock) findViewById(R$id.cli_clock)).setTextColor(colorAttr);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mTintedIconManager != null) {
            ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).addIconGroup(this.mTintedIconManager);
        }
        setTiles();
        this.mHost.addCallback(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (this.mTintedIconManager != null) {
            ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).removeIconGroup(this.mTintedIconManager);
        }
        this.mHost.removeCallback(this);
        Iterator<QSPanelControllerBase.TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            QSPanelControllerBase.TileRecord next = it.next();
            this.mPagedTileLayout.removeTile(next);
            next.tile.removeCallback(next.callback);
            next.callback = null;
        }
        this.mRecords.clear();
        super.onDetachedFromWindow();
    }

    public void setListening(boolean z) {
        if (z && isCliAccessPackagesChanged()) {
            setTiles();
        }
        if (this.mListening != z) {
            this.mListening = z;
            this.mPagedTileLayout.setListening(z, (UiEventLogger) null);
            if (this.mListening) {
                this.mBrightnessController.registerCallbacks();
                return;
            }
            this.mBrightnessController.unregisterCallbacks();
            this.mCliQsContainer.setAlpha(1.0f);
            this.mBrightnessSliderView.setAlpha(1.0f);
        }
    }

    private int getColorAttr(int i) {
        return Utils.getColorAttrDefaultColor(this.mContext, i);
    }

    public void onTilesChanged() {
        setTiles();
    }

    private void setTiles() {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliQSPanelNew", "setTiles() mHost:" + this.mHost);
        }
        Iterator<QSPanelControllerBase.TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            QSPanelControllerBase.TileRecord next = it.next();
            this.mPagedTileLayout.removeTile(next);
            next.tile.removeCallback(next.callback);
            next.callback = null;
        }
        this.mRecords.clear();
        QSTileHost qSTileHost = this.mHost;
        if (qSTileHost != null) {
            for (QSTile next2 : qSTileHost.getTiles()) {
                if (isEligibleTile(next2.getTileSpec(), next2)) {
                    if (DEBUG) {
                        Log.d("CLI-QSMV-CliQSPanelNew", "Add tile: " + next2.getMetricsSpec());
                    }
                    final QSPanelControllerBase.TileRecord tileRecord = new QSPanelControllerBase.TileRecord();
                    tileRecord.tile = next2;
                    tileRecord.tileView = createTileView(next2);
                    C11593 r2 = new QSTile.Callback() {
                        public void onAnnouncementRequested(CharSequence charSequence) {
                        }

                        public void onScanStateChanged(boolean z) {
                        }

                        public void onToggleStateChanged(boolean z) {
                        }

                        public void onStateChanged(QSTile.State state) {
                            tileRecord.tileView.onStateChanged(state);
                        }

                        public void onShowDetail(boolean z) {
                            CliQSPanelNew.this.showDetail(z, tileRecord);
                        }
                    };
                    tileRecord.tile.addCallback(r2);
                    tileRecord.callback = r2;
                    tileRecord.tileView.init(tileRecord.tile);
                    tileRecord.tile.refreshState();
                    this.mRecords.add(tileRecord);
                    this.mPagedTileLayout.addTile(tileRecord);
                    this.mPagedTileLayout.setListening(this.mListening, (UiEventLogger) null);
                    ((QSTileViewImpl) tileRecord.tileView).init(tileRecord.tile);
                }
            }
        }
    }

    private QSTileView createTileView(QSTile qSTile) {
        return new QSTileViewImpl(this.mContext, qSTile.createTileView(this.mContext), true);
    }

    private boolean isEligibleTile(String str, QSTile qSTile) {
        boolean z = false;
        if (str == null || this.mDisallowlistTiles.contains(str)) {
            return false;
        }
        if (!str.startsWith("custom(") || !(qSTile instanceof CustomTile)) {
            return true;
        }
        String packageName = ((CustomTile) qSTile).getComponent().getPackageName();
        CLIManager cLIManager = this.mCliManager;
        if (cLIManager != null) {
            z = cLIManager.queryCliAccess(packageName);
        }
        if (DEBUG) {
            Log.d("CLI-QSMV-CliQSPanelNew", "CustomTile: spec = " + str + " - pkg = " + packageName + " - CliAccess=" + z);
        }
        return z;
    }

    /* renamed from: com.android.systemui.qs.CliQSPanelNew$H */
    private class C1160H extends Handler {
        private C1160H() {
        }

        public void handleMessage(Message message) {
            boolean z = true;
            if (message.what == 1) {
                CliQSPanelNew cliQSPanelNew = CliQSPanelNew.this;
                QSPanel.Record record = (QSPanel.Record) message.obj;
                if (message.arg1 == 0) {
                    z = false;
                }
                cliQSPanelNew.handleShowDetail(record, z);
            }
        }
    }

    public void setCallback(CliQSDetail.Callback callback) {
        this.mCallback = callback;
    }

    public void closeDetail() {
        showDetail(false, this.mDetailRecord);
    }

    /* access modifiers changed from: protected */
    public void showDetail(boolean z, QSPanel.Record record) {
        if (!z || MotoFeature.isLidClosed(this.mContext)) {
            this.mHandler.obtainMessage(1, z ? 1 : 0, 0, record).sendToTarget();
        }
    }

    /* access modifiers changed from: protected */
    public void handleShowDetail(QSPanel.Record record, boolean z) {
        int i;
        if (record instanceof QSPanelControllerBase.TileRecord) {
            handleShowDetailTile((QSPanelControllerBase.TileRecord) record, z);
            return;
        }
        int i2 = 0;
        if (record != null) {
            i2 = record.f117x;
            i = record.f118y;
        } else {
            i = 0;
        }
        handleShowDetailImpl(record, z, i2, i);
    }

    private void handleShowDetailTile(QSPanelControllerBase.TileRecord tileRecord, boolean z) {
        QSPanel.Record record = this.mDetailRecord;
        if ((record != null) != z || record != tileRecord) {
            if (z) {
                DetailAdapter detailAdapter = tileRecord.tile.getDetailAdapter();
                tileRecord.detailAdapter = detailAdapter;
                if (detailAdapter == null) {
                    return;
                }
            }
            tileRecord.tile.setDetailListening(z);
            handleShowDetailImpl(tileRecord, z, tileRecord.tileView.getLeft() + (tileRecord.tileView.getWidth() / 2), tileRecord.tileView.getDetailY() + getTop());
        }
    }

    private void handleShowDetailImpl(QSPanel.Record record, boolean z, int i, int i2) {
        DetailAdapter detailAdapter = null;
        setDetailRecord(z ? record : null);
        if (z) {
            detailAdapter = record.detailAdapter;
        }
        fireShowingDetail(detailAdapter, i, i2);
        this.mCliStatusBarWindowController.setDetailVisible(z);
    }

    /* access modifiers changed from: protected */
    public void setDetailRecord(QSPanel.Record record) {
        if (record != this.mDetailRecord) {
            this.mDetailRecord = record;
            fireScanStateChanged((record instanceof QSPanelControllerBase.TileRecord) && ((QSPanelControllerBase.TileRecord) record).scanState);
        }
    }

    private void fireShowingDetail(DetailAdapter detailAdapter, int i, int i2) {
        CliQSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onShowingDetail(detailAdapter, i, i2);
        }
    }

    private void fireScanStateChanged(boolean z) {
        CliQSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onScanStateChanged(z);
        }
    }

    private boolean isCliAccessPackagesChanged() {
        CLIManager cLIManager = this.mCliManager;
        if (cLIManager == null) {
            return false;
        }
        List<String> cliAccessPackages = cLIManager.getCliAccessPackages(5);
        boolean z = !Arrays.equals(cliAccessPackages.toArray(new String[0]), this.mCurrentCliAccessPackages.toArray(new String[0]));
        this.mCurrentCliAccessPackages = cliAccessPackages;
        if (DEBUG) {
            Log.d("CLI-QSMV-CliQSPanelNew", "Cli access packages changed: " + z);
        }
        return z;
    }
}
