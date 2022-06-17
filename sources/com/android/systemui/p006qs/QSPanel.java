package com.android.systemui.p006qs;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.widget.RemeasuringLinearLayout;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.R$layout;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.PagedTileLayout;
import com.android.systemui.p006qs.QSDetail;
import com.android.systemui.p006qs.QSPanelControllerBase;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.Utils;
import com.android.systemui.util.animation.UniqueObjectHostView;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.systemui.qs.QSPanel */
public class QSPanel extends LinearLayout implements TunerService.Tunable {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private int mActiveSubsCount;
    private boolean mAirplaneMode;
    protected View mBrightnessView;
    private QSDetail.Callback mCallback;
    private int mContentMarginEnd;
    private int mContentMarginStart;
    protected final Context mContext;
    /* access modifiers changed from: private */
    public Record mDetailRecord;
    protected boolean mExpanded;
    protected View mFooter;
    private PageIndicator mFooterPageIndicator;
    /* access modifiers changed from: private */
    public final C1190H mHandler = new C1190H();
    protected ViewGroup mHeaderContainer;
    protected LinearLayout mHorizontalContentContainer;
    private LinearLayout mHorizontalLinearLayout;
    private final boolean mIsDestkop;
    /* access modifiers changed from: private */
    public boolean mIsPrcCustom;
    protected boolean mListening;
    private final int mMediaTopMargin;
    private int mMediaTotalBottomMargin;
    private int mMovableContentStartIndex;
    private final List<OnConfigurationChangedListener> mOnConfigurationChangedListeners = new ArrayList();
    private int mOrientation;
    protected View mSecurityFooter;
    private boolean mShowMotoQSCarrierGroup;
    protected QSTileLayout mTileLayout;
    private boolean mUsingHorizontalLayout;
    protected boolean mUsingMediaPlayer;

    /* renamed from: com.android.systemui.qs.QSPanel$OnConfigurationChangedListener */
    interface OnConfigurationChangedListener {
        void onConfigurationChange(Configuration configuration);
    }

    /* renamed from: com.android.systemui.qs.QSPanel$QSTileLayout */
    public interface QSTileLayout {
        void addTile(QSPanelControllerBase.TileRecord tileRecord);

        int getNumVisibleTiles();

        int getOffsetTop(QSPanelControllerBase.TileRecord tileRecord);

        void removeTile(QSPanelControllerBase.TileRecord tileRecord);

        void restoreInstanceState(Bundle bundle) {
        }

        void saveInstanceState(Bundle bundle) {
        }

        void setExpansion(float f, float f2) {
        }

        void setListening(boolean z, UiEventLogger uiEventLogger);

        boolean setMaxColumns(int i) {
            return false;
        }

        boolean setMinRows(int i) {
            return false;
        }

        boolean updateResources();
    }

    private boolean needsDynamicRowsAndColumns() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean displayMediaMarginsOnMedia() {
        return true;
    }

    /* access modifiers changed from: protected */
    public String getDumpableTag() {
        return "QSPanel";
    }

    /* access modifiers changed from: protected */
    public boolean mediaNeedsTopMargin() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void setQSSecurityContainer(ViewGroup viewGroup) {
    }

    public QSPanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mUsingMediaPlayer = Utils.useQsMediaPlayer(context);
        this.mMediaTotalBottomMargin = getResources().getDimensionPixelSize(R$dimen.quick_settings_bottom_margin_media);
        if (DesktopFeature.isDesktopDisplayContext(getContext())) {
            this.mMediaTotalBottomMargin = getResources().getDimensionPixelSize(R$dimen.desktop_qs_panel_padding_bottom);
        }
        this.mMediaTopMargin = getResources().getDimensionPixelSize(R$dimen.qs_tile_margin_vertical);
        this.mContext = context;
        setOrientation(1);
        this.mMovableContentStartIndex = getChildCount();
        this.mIsPrcCustom = MotoFeature.getInstance(context).isCustomPanelView();
        this.mIsDestkop = DesktopFeature.isDesktopDisplayContext(context);
    }

    /* access modifiers changed from: package-private */
    public void initialize() {
        this.mTileLayout = getOrCreateTileLayout();
        if (this.mUsingMediaPlayer) {
            RemeasuringLinearLayout remeasuringLinearLayout = new RemeasuringLinearLayout(this.mContext);
            this.mHorizontalLinearLayout = remeasuringLinearLayout;
            remeasuringLinearLayout.setOrientation(0);
            this.mHorizontalLinearLayout.setClipChildren(false);
            this.mHorizontalLinearLayout.setClipToPadding(false);
            RemeasuringLinearLayout remeasuringLinearLayout2 = new RemeasuringLinearLayout(this.mContext);
            this.mHorizontalContentContainer = remeasuringLinearLayout2;
            remeasuringLinearLayout2.setOrientation(1);
            this.mHorizontalContentContainer.setClipChildren(true);
            this.mHorizontalContentContainer.setClipToPadding(false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd((int) this.mContext.getResources().getDimension(R$dimen.qs_media_padding));
            layoutParams.gravity = 16;
            this.mHorizontalLinearLayout.addView(this.mHorizontalContentContainer, layoutParams);
            addView(this.mHorizontalLinearLayout, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        }
    }

    public void setBrightnessView(View view) {
        View view2 = this.mBrightnessView;
        if (view2 != null) {
            removeView(view2);
            this.mMovableContentStartIndex--;
        }
        addView(view, 0);
        this.mBrightnessView = view;
        setBrightnessViewMargin();
        this.mMovableContentStartIndex++;
    }

    private void setBrightnessViewMargin() {
        View view = this.mBrightnessView;
        if (view != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            marginLayoutParams.topMargin = this.mContext.getResources().getDimensionPixelSize(R$dimen.qs_brightness_margin_top);
            marginLayoutParams.bottomMargin = this.mContext.getResources().getDimensionPixelSize(R$dimen.qs_brightness_margin_bottom);
            this.mBrightnessView.setLayoutParams(marginLayoutParams);
        }
    }

    public QSTileLayout getOrCreateTileLayout() {
        if (this.mTileLayout == null) {
            this.mTileLayout = (QSTileLayout) LayoutInflater.from(this.mContext).inflate(R$layout.qs_paged_tile_layout, this, false);
        }
        if (DesktopFeature.isDesktopDisplayContext(this.mContext)) {
            QSTileLayout qSTileLayout = this.mTileLayout;
            if (qSTileLayout instanceof PagedTileLayout) {
                ((PagedTileLayout) qSTileLayout).setPageOffsetRatio(0.2f);
            }
        }
        return this.mTileLayout;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout instanceof PagedTileLayout) {
            PageIndicator pageIndicator = this.mFooterPageIndicator;
            if (pageIndicator != null) {
                pageIndicator.setNumPages(((PagedTileLayout) qSTileLayout).getNumPages());
            }
            if (((View) this.mTileLayout).getParent() == this) {
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(10000, 1073741824);
                ((PagedTileLayout) this.mTileLayout).setExcessHeight(10000 - View.MeasureSpec.getSize(i2));
                i2 = makeMeasureSpec;
            }
        }
        super.onMeasure(i, i2);
        int paddingBottom = getPaddingBottom() + getPaddingTop();
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
                paddingBottom = paddingBottom + childAt.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
            }
        }
        setMeasuredDimension(getMeasuredWidth(), paddingBottom);
    }

    public void onTuningChanged(String str, String str2) {
        View view;
        if ("qs_show_brightness".equals(str) && (view = this.mBrightnessView) != null) {
            updateViewVisibilityForTuningValue(view, str2);
        }
    }

    private void updateViewVisibilityForTuningValue(View view, String str) {
        view.setVisibility(TunerService.parseIntegerSwitch(str, true) ? 0 : 8);
    }

    public void openDetails(QSTile qSTile) {
        if (qSTile != null) {
            showDetailAdapter(true, qSTile.getDetailAdapter(), new int[]{getWidth() / 2, 0});
        }
    }

    /* access modifiers changed from: package-private */
    public View getBrightnessView() {
        return this.mBrightnessView;
    }

    public void setCallback(QSDetail.Callback callback) {
        this.mCallback = callback;
    }

    public void setFooterPageIndicator(PageIndicator pageIndicator) {
        if (this.mTileLayout instanceof PagedTileLayout) {
            this.mFooterPageIndicator = pageIndicator;
            updatePageIndicator();
        }
    }

    private void updatePageIndicator() {
        PageIndicator pageIndicator;
        if ((this.mTileLayout instanceof PagedTileLayout) && (pageIndicator = this.mFooterPageIndicator) != null) {
            pageIndicator.setVisibility(8);
            ((PagedTileLayout) this.mTileLayout).setPageIndicator(this.mFooterPageIndicator);
        }
    }

    public void updateResources() {
        lambda$updateAirplaneMode$3();
        updatePageIndicator();
        setBrightnessViewMargin();
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout != null) {
            qSTileLayout.updateResources();
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: updatePadding */
    public void lambda$updateAirplaneMode$3() {
        Resources resources = this.mContext.getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.qs_panel_padding_top);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.qs_panel_padding_bottom);
        if (this.mShowMotoQSCarrierGroup) {
            dimensionPixelSize += resources.getDimensionPixelSize(R$dimen.qs_panel_extra_carrier_padding_top);
        }
        if (this.mIsDestkop) {
            dimensionPixelSize = 0;
            dimensionPixelSize2 = 0;
        }
        setPaddingRelative(getPaddingStart(), dimensionPixelSize, getPaddingEnd(), dimensionPixelSize2);
    }

    /* access modifiers changed from: package-private */
    public void addOnConfigurationChangedListener(OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mOnConfigurationChangedListeners.add(onConfigurationChangedListener);
    }

    /* access modifiers changed from: package-private */
    public void removeOnConfigurationChangedListener(OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mOnConfigurationChangedListeners.remove(onConfigurationChangedListener);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mOnConfigurationChangedListeners.forEach(new QSPanel$$ExternalSyntheticLambda2(configuration));
        switchSecurityFooter();
        int i = this.mOrientation;
        int i2 = configuration.orientation;
        if (i != i2) {
            this.mOrientation = i2;
            if (needUpdateTopPaddingForCarrierGroup()) {
                lambda$updateAirplaneMode$3();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mFooter = findViewById(R$id.qs_footer);
        this.mOrientation = this.mContext.getResources().getConfiguration().orientation;
    }

    private void updateHorizontalLinearLayoutMargins() {
        if (this.mHorizontalLinearLayout != null && !displayMediaMarginsOnMedia()) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mHorizontalLinearLayout.getLayoutParams();
            layoutParams.bottomMargin = Math.max(this.mMediaTotalBottomMargin - getPaddingBottom(), 0);
            this.mHorizontalLinearLayout.setLayoutParams(layoutParams);
        }
    }

    private void switchAllContentToParent(ViewGroup viewGroup, QSTileLayout qSTileLayout) {
        int i = viewGroup == this ? this.mMovableContentStartIndex : 0;
        switchToParent((View) qSTileLayout, viewGroup, i);
        int i2 = i + 1;
        View view = this.mFooter;
        if (view != null) {
            switchToParent(view, viewGroup, i2);
        }
    }

    /* access modifiers changed from: protected */
    public void switchSecurityFooter() {
        ViewGroup viewGroup;
        if (this.mSecurityFooter == null) {
            return;
        }
        if (this.mContext.getResources().getConfiguration().orientation != 2 || (viewGroup = this.mHeaderContainer) == null) {
            View findViewByPredicate = findViewByPredicate(QSPanel$$ExternalSyntheticLambda3.INSTANCE);
            int indexOfChild = findViewByPredicate != null ? indexOfChild(findViewByPredicate) : -1;
            if (this.mSecurityFooter.getParent() == this && indexOfChild(this.mSecurityFooter) < indexOfChild) {
                indexOfChild--;
            }
            switchToParent(this.mSecurityFooter, this, indexOfChild);
            return;
        }
        switchToParent(this.mSecurityFooter, viewGroup, 0);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$switchSecurityFooter$1(View view) {
        return view instanceof UniqueObjectHostView;
    }

    /* access modifiers changed from: protected */
    public void switchToParent(View view, ViewGroup viewGroup, int i) {
        switchToParent(view, viewGroup, i, getDumpableTag());
    }

    private void reAttachMediaHost(ViewGroup viewGroup, boolean z) {
        int i;
        if (this.mUsingMediaPlayer) {
            LinearLayout linearLayout = z ? this.mHorizontalLinearLayout : this;
            ViewGroup viewGroup2 = (ViewGroup) viewGroup.getParent();
            if (viewGroup2 != linearLayout) {
                if (viewGroup2 != null) {
                    viewGroup2.removeView(viewGroup);
                }
                linearLayout.addView(viewGroup);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewGroup.getLayoutParams();
                layoutParams.height = -2;
                int i2 = 0;
                layoutParams.width = z ? 0 : -1;
                layoutParams.weight = z ? 1.0f : 0.0f;
                if (!z || displayMediaMarginsOnMedia()) {
                    i = Math.max(this.mMediaTotalBottomMargin - getPaddingBottom(), 0);
                } else {
                    i = 0;
                }
                layoutParams.bottomMargin = i;
                if (mediaNeedsTopMargin() && !z) {
                    i2 = this.mMediaTopMargin;
                }
                layoutParams.topMargin = i2;
            }
        }
    }

    public void setExpanded(boolean z) {
        if (this.mExpanded != z) {
            this.mExpanded = z;
            if (!z) {
                QSTileLayout qSTileLayout = this.mTileLayout;
                if (qSTileLayout instanceof PagedTileLayout) {
                    ((PagedTileLayout) qSTileLayout).setCurrentItem(0, false);
                }
            }
        }
    }

    public void setPageListener(PagedTileLayout.PageListener pageListener) {
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout instanceof PagedTileLayout) {
            ((PagedTileLayout) qSTileLayout).setPageListener(pageListener);
        }
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    public void setListening(boolean z) {
        this.mListening = z;
    }

    public void showDetailAdapter(boolean z, DetailAdapter detailAdapter, int[] iArr) {
        int i = iArr[0];
        int i2 = iArr[1];
        ((View) getParent()).getLocationInWindow(iArr);
        Record record = new Record();
        record.detailAdapter = detailAdapter;
        record.f117x = i - iArr[0];
        record.f118y = i2 - iArr[1];
        iArr[0] = i;
        iArr[1] = i2;
        showDetail(z, record);
    }

    /* access modifiers changed from: protected */
    public void showDetail(boolean z, Record record) {
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isLidClosed(this.mContext)) {
            this.mHandler.obtainMessage(1, z ? 1 : 0, 0, record).sendToTarget();
        }
    }

    /* access modifiers changed from: protected */
    public void drawTile(QSPanelControllerBase.TileRecord tileRecord, QSTile.State state) {
        tileRecord.tileView.onStateChanged(state);
    }

    /* access modifiers changed from: protected */
    public QSEvent openPanelEvent() {
        return QSEvent.QS_PANEL_EXPANDED;
    }

    /* access modifiers changed from: protected */
    public QSEvent closePanelEvent() {
        return QSEvent.QS_PANEL_COLLAPSED;
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowDetail() {
        return this.mExpanded;
    }

    /* access modifiers changed from: package-private */
    public void addTile(final QSPanelControllerBase.TileRecord tileRecord) {
        C11891 r0 = new QSTile.Callback() {
            public void onStateChanged(QSTile.State state) {
                if (QSPanel.this.mIsPrcCustom && QSPanel.DEBUG) {
                    Log.i("QSPanel", "QSTileLog Callback onStateChanged = " + tileRecord.tile.getState().spec + " state = " + state.spec + " label = " + state.label + " state = " + state.state);
                }
                QSPanel.this.drawTile(tileRecord, state);
            }

            public void onShowDetail(boolean z) {
                if (QSPanel.this.shouldShowDetail()) {
                    QSPanel.this.showDetail(z, tileRecord);
                }
            }

            public void onToggleStateChanged(boolean z) {
                if (QSPanel.this.mDetailRecord == tileRecord) {
                    QSPanel.this.fireToggleStateChanged(z);
                }
            }

            public void onScanStateChanged(boolean z) {
                tileRecord.scanState = z;
                Record access$300 = QSPanel.this.mDetailRecord;
                QSPanelControllerBase.TileRecord tileRecord = tileRecord;
                if (access$300 == tileRecord) {
                    QSPanel.this.fireScanStateChanged(tileRecord.scanState);
                }
            }

            public void onAnnouncementRequested(CharSequence charSequence) {
                if (charSequence != null) {
                    QSPanel.this.mHandler.obtainMessage(3, charSequence).sendToTarget();
                }
            }
        };
        if (this.mIsPrcCustom && DEBUG) {
            Log.i("QSPanel", "QSTileLog addTile = " + tileRecord.tile.getState().spec);
        }
        tileRecord.tile.addCallback(r0);
        tileRecord.callback = r0;
        tileRecord.tileView.init(tileRecord.tile);
        tileRecord.tile.refreshState();
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout != null) {
            qSTileLayout.addTile(tileRecord);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeTile(QSPanelControllerBase.TileRecord tileRecord) {
        this.mTileLayout.removeTile(tileRecord);
    }

    /* access modifiers changed from: package-private */
    public void closeDetail() {
        showDetail(false, this.mDetailRecord);
    }

    /* access modifiers changed from: protected */
    public void handleShowDetail(Record record, boolean z) {
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
        Record record = this.mDetailRecord;
        if ((record != null) != z || record != tileRecord) {
            if (z) {
                DetailAdapter detailAdapter = tileRecord.tile.getDetailAdapter();
                tileRecord.detailAdapter = detailAdapter;
                if (detailAdapter == null) {
                    return;
                }
            }
            tileRecord.tile.setDetailListening(z);
            handleShowDetailImpl(tileRecord, z, tileRecord.tileView.getLeft() + (tileRecord.tileView.getWidth() / 2), tileRecord.tileView.getDetailY() + this.mTileLayout.getOffsetTop(tileRecord) + getTop());
        }
    }

    private void handleShowDetailImpl(Record record, boolean z, int i, int i2) {
        DetailAdapter detailAdapter = null;
        setDetailRecord(z ? record : null);
        if (z) {
            detailAdapter = record.detailAdapter;
        }
        fireShowingDetail(detailAdapter, i, i2);
    }

    /* access modifiers changed from: protected */
    public void setDetailRecord(Record record) {
        if (record != this.mDetailRecord) {
            this.mDetailRecord = record;
            fireScanStateChanged((record instanceof QSPanelControllerBase.TileRecord) && ((QSPanelControllerBase.TileRecord) record).scanState);
        }
    }

    private void fireShowingDetail(DetailAdapter detailAdapter, int i, int i2) {
        QSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onShowingDetail(detailAdapter, i, i2);
        }
    }

    /* access modifiers changed from: private */
    public void fireToggleStateChanged(boolean z) {
        QSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onToggleStateChanged(z);
        }
    }

    /* access modifiers changed from: private */
    public void fireScanStateChanged(boolean z) {
        QSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onScanStateChanged(z);
        }
    }

    /* access modifiers changed from: package-private */
    public QSTileLayout getTileLayout() {
        return this.mTileLayout;
    }

    public void setContentMargins(int i, int i2, ViewGroup viewGroup) {
        this.mContentMarginStart = i;
        this.mContentMarginEnd = i2;
        updateMediaHostContentMargins(viewGroup);
    }

    /* access modifiers changed from: protected */
    public void updateMediaHostContentMargins(ViewGroup viewGroup) {
        if (this.mUsingMediaPlayer) {
            updateMargins(viewGroup, 0, this.mUsingHorizontalLayout ? this.mContentMarginEnd : 0);
        }
    }

    /* access modifiers changed from: protected */
    public void updateMargins(View view, int i, int i2) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.setMarginStart(i);
            layoutParams.setMarginEnd(i2);
            view.setLayoutParams(layoutParams);
        }
    }

    public void setHeaderContainer(ViewGroup viewGroup) {
        this.mHeaderContainer = viewGroup;
    }

    public boolean isListening() {
        return this.mListening;
    }

    public void setSecurityFooter(View view) {
        this.mSecurityFooter = view;
        switchSecurityFooter();
    }

    /* access modifiers changed from: protected */
    public void setPageMargin(int i) {
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout instanceof PagedTileLayout) {
            ((PagedTileLayout) qSTileLayout).setPageMargin(i);
        }
    }

    /* access modifiers changed from: package-private */
    public void setUsingHorizontalLayout(boolean z, ViewGroup viewGroup, boolean z2) {
        if (z != this.mUsingHorizontalLayout || z2) {
            this.mUsingHorizontalLayout = z;
            switchAllContentToParent(z ? this.mHorizontalContentContainer : this, this.mTileLayout);
            reAttachMediaHost(viewGroup, z);
            if (needsDynamicRowsAndColumns()) {
                int i = 2;
                this.mTileLayout.setMinRows(z ? 2 : 1);
                QSTileLayout qSTileLayout = this.mTileLayout;
                if (!z) {
                    i = 4;
                }
                qSTileLayout.setMaxColumns(i);
            }
            if (DesktopFeature.isDesktopDisplayContext(getContext())) {
                this.mTileLayout.setMaxColumns(getResources().getInteger(R$integer.desktop_qs_max_columns));
            }
            updateMargins(viewGroup);
            if (this.mUsingMediaPlayer) {
                this.mHorizontalLinearLayout.setVisibility(z ? 0 : 8);
            }
        }
    }

    private void updateMargins(ViewGroup viewGroup) {
        updateMediaHostContentMargins(viewGroup);
        updateHorizontalLinearLayoutMargins();
        lambda$updateAirplaneMode$3();
    }

    /* renamed from: com.android.systemui.qs.QSPanel$H */
    private class C1190H extends Handler {
        private C1190H() {
        }

        public void handleMessage(Message message) {
            int i = message.what;
            boolean z = true;
            if (i == 1) {
                QSPanel qSPanel = QSPanel.this;
                Record record = (Record) message.obj;
                if (message.arg1 == 0) {
                    z = false;
                }
                qSPanel.handleShowDetail(record, z);
            } else if (i == 3) {
                QSPanel.this.announceForAccessibility((CharSequence) message.obj);
            }
        }
    }

    /* renamed from: com.android.systemui.qs.QSPanel$Record */
    protected static class Record {
        DetailAdapter detailAdapter;

        /* renamed from: x */
        int f117x;

        /* renamed from: y */
        int f118y;

        protected Record() {
        }
    }

    static void switchToParent(View view, ViewGroup viewGroup, int i, String str) {
        if (viewGroup == null) {
            Log.w(str, "Trying to move view to null parent", new IllegalStateException());
            return;
        }
        ViewGroup viewGroup2 = (ViewGroup) view.getParent();
        if (viewGroup2 != viewGroup) {
            if (viewGroup2 != null) {
                viewGroup2.removeView(view);
            }
            viewGroup.addView(view, i);
        } else if (viewGroup.indexOfChild(view) != i) {
            viewGroup.removeView(view);
            viewGroup.addView(view, i);
        }
    }

    public void updateActiveSubsCount(int i) {
        this.mActiveSubsCount = i;
        if (needUpdateTopPaddingForCarrierGroup()) {
            post(new QSPanel$$ExternalSyntheticLambda0(this));
        }
    }

    public void updateAirplaneMode(boolean z) {
        this.mAirplaneMode = z;
        if (needUpdateTopPaddingForCarrierGroup()) {
            post(new QSPanel$$ExternalSyntheticLambda1(this));
        }
    }

    private boolean needUpdateTopPaddingForCarrierGroup() {
        boolean z = this.mOrientation == 1 && this.mActiveSubsCount == 2 && !this.mIsDestkop && !this.mAirplaneMode;
        if (this.mShowMotoQSCarrierGroup == z) {
            return false;
        }
        this.mShowMotoQSCarrierGroup = z;
        return true;
    }
}
