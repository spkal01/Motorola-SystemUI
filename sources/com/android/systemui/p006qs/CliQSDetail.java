package com.android.systemui.p006qs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusIconContainer;
import java.util.Objects;

/* renamed from: com.android.systemui.qs.CliQSDetail */
public class CliQSDetail extends LinearLayout {
    /* access modifiers changed from: private */
    public boolean mAnimatingOpen;
    private CliQSPanelNew mCliQsPanel;
    private QSDetailClipper mClipper;
    /* access modifiers changed from: private */
    public boolean mClosingDetail;
    private DetailAdapter mDetailAdapter;
    /* access modifiers changed from: private */
    public ViewGroup mDetailContent;
    private final SparseArray<View> mDetailViews = new SparseArray<>();
    private boolean mFullyExpanded;
    private final AnimatorListenerAdapter mHideGridContentWhenDone = new AnimatorListenerAdapter() {
        public void onAnimationCancel(Animator animator) {
            animator.removeListener(this);
            boolean unused = CliQSDetail.this.mAnimatingOpen = false;
            CliQSDetail.this.checkPendingAnimations();
        }

        public void onAnimationEnd(Animator animator) {
            boolean unused = CliQSDetail.this.mAnimatingOpen = false;
            CliQSDetail.this.checkPendingAnimations();
        }
    };
    private int mOpenX;
    private int mOpenY;
    protected ImageView mQsBackButton;
    protected View mQsDetailHeader;
    protected ImageView mQsDetailHeaderProgress;
    private Switch mQsDetailHeaderSwitch;
    protected TextView mQsDetailHeaderTitle;
    protected Callback mQsPanelCallback = new Callback() {
        public void onShowingDetail(final DetailAdapter detailAdapter, final int i, final int i2) {
            CliQSDetail.this.post(new Runnable() {
                public void run() {
                    if (CliQSDetail.this.isAttachedToWindow()) {
                        CliQSDetail.this.handleShowingDetail(detailAdapter, i, i2, false);
                    }
                }
            });
        }

        public void onScanStateChanged(final boolean z) {
            CliQSDetail.this.post(new Runnable() {
                public void run() {
                    CliQSDetail.this.handleScanStateChanged(z);
                }
            });
        }
    };
    private boolean mScanState;
    private boolean mSwitchState;
    private final AnimatorListenerAdapter mTeardownDetailWhenDone = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            CliQSDetail.this.mDetailContent.removeAllViews();
            CliQSDetail.this.setVisibility(4);
            boolean unused = CliQSDetail.this.mClosingDetail = false;
        }
    };
    private StatusBarIconController.TintedIconManager mTintedIconManager;
    private boolean mTriggeredExpand;
    private final UiEventLogger mUiEventLogger = QSEvents.INSTANCE.getQsUiEventsLogger();

    /* renamed from: com.android.systemui.qs.CliQSDetail$Callback */
    public interface Callback {
        void onScanStateChanged(boolean z);

        void onShowingDetail(DetailAdapter detailAdapter, int i, int i2);
    }

    public CliQSDetail(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        for (int i = 0; i < this.mDetailViews.size(); i++) {
            this.mDetailViews.valueAt(i).dispatchConfigurationChanged(configuration);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setBackground(this.mContext.getDrawable(R$drawable.cli_qs_detail_background));
        int colorAttr = getColorAttr(16842800);
        StatusBarIconController.TintedIconManager tintedIconManager = new StatusBarIconController.TintedIconManager((StatusIconContainer) findViewById(R$id.cli_statusIcons), (FeatureFlags) Dependency.get(FeatureFlags.class));
        this.mTintedIconManager = tintedIconManager;
        tintedIconManager.setTint(colorAttr);
        this.mDetailContent = (ViewGroup) findViewById(16908290);
        View findViewById = findViewById(R$id.cli_qs_detail_header);
        this.mQsDetailHeader = findViewById;
        this.mQsDetailHeaderTitle = (TextView) findViewById.findViewById(16908310);
        this.mQsDetailHeaderProgress = (ImageView) findViewById(R$id.cli_qs_detail_header_progress);
        this.mQsBackButton = (ImageView) findViewById(R$id.cli_qs_detail_back);
        this.mClipper = new QSDetailClipper(this);
        this.mQsBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CliQSDetail cliQSDetail = CliQSDetail.this;
                cliQSDetail.announceForAccessibility(cliQSDetail.mContext.getString(R$string.accessibility_desc_quick_settings));
                CliQSDetail.this.closeDetail();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mTintedIconManager != null) {
            ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).addIconGroup(this.mTintedIconManager);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (this.mTintedIconManager != null) {
            ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).removeIconGroup(this.mTintedIconManager);
        }
        super.onDetachedFromWindow();
    }

    public void setQsPanel(CliQSPanelNew cliQSPanelNew, QuickStatusBarHeader quickStatusBarHeader, View view) {
        this.mCliQsPanel = cliQSPanelNew;
        cliQSPanelNew.setCallback(this.mQsPanelCallback);
    }

    public boolean isShowingDetail() {
        return this.mDetailAdapter != null;
    }

    public void handleShowingDetail(DetailAdapter detailAdapter, int i, int i2, boolean z) {
        AnimatorListenerAdapter animatorListenerAdapter;
        boolean z2 = detailAdapter != null;
        setClickable(z2);
        if (z2) {
            Log.d("handleShowingDetail", "showingDetail:  : ");
            setupDetailHeader(detailAdapter);
            if (!z || this.mFullyExpanded) {
                this.mTriggeredExpand = false;
            } else {
                this.mTriggeredExpand = true;
                ((CommandQueue) Dependency.get(CommandQueue.class)).animateExpandSettingsPanel((String) null);
            }
            this.mOpenX = i;
            this.mOpenY = i2;
        } else {
            Log.d("handleShowingDetail", " not showingDetail:  : ");
            i = this.mOpenX;
            i2 = this.mOpenY;
            if (z && this.mTriggeredExpand) {
                ((CommandQueue) Dependency.get(CommandQueue.class)).animateCollapsePanels();
                this.mTriggeredExpand = false;
            }
        }
        DetailAdapter detailAdapter2 = this.mDetailAdapter;
        boolean z3 = (detailAdapter2 != null) != (detailAdapter != null);
        if (z3 || detailAdapter2 != detailAdapter) {
            if (detailAdapter != null) {
                int metricsCategory = detailAdapter.getMetricsCategory();
                View createDetailView = detailAdapter.createDetailView(this.mContext, this.mDetailViews.get(metricsCategory), this.mDetailContent);
                if (createDetailView != null) {
                    this.mDetailContent.removeAllViews();
                    this.mDetailContent.addView(createDetailView);
                    this.mDetailViews.put(metricsCategory, createDetailView);
                    ((MetricsLogger) Dependency.get(MetricsLogger.class)).visible(detailAdapter.getMetricsCategory());
                    this.mUiEventLogger.log(detailAdapter.openDetailEvent());
                    announceForAccessibility(this.mContext.getString(R$string.accessibility_quick_settings_detail, new Object[]{detailAdapter.getTitle()}));
                    this.mDetailAdapter = detailAdapter;
                    animatorListenerAdapter = this.mHideGridContentWhenDone;
                    setVisibility(0);
                } else {
                    throw new IllegalStateException("Must return detail view");
                }
            } else {
                if (detailAdapter2 != null) {
                    ((MetricsLogger) Dependency.get(MetricsLogger.class)).hidden(this.mDetailAdapter.getMetricsCategory());
                    this.mUiEventLogger.log(this.mDetailAdapter.closeDetailEvent());
                }
                this.mClosingDetail = true;
                this.mDetailAdapter = null;
                animatorListenerAdapter = this.mTeardownDetailWhenDone;
                this.mQsPanelCallback.onScanStateChanged(false);
            }
            sendAccessibilityEvent(32);
            animateDetailVisibleDiff(i, i2, z3, animatorListenerAdapter);
        }
    }

    /* access modifiers changed from: protected */
    public void animateDetailVisibleDiff(int i, int i2, boolean z, Animator.AnimatorListener animatorListener) {
        if (z) {
            DetailAdapter detailAdapter = this.mDetailAdapter;
            boolean z2 = true;
            this.mAnimatingOpen = detailAdapter != null;
            if (this.mFullyExpanded || detailAdapter != null) {
                setAlpha(1.0f);
                QSDetailClipper qSDetailClipper = this.mClipper;
                if (this.mDetailAdapter == null) {
                    z2 = false;
                }
                qSDetailClipper.animateCircularClip(i, i2, z2, animatorListener);
                return;
            }
            animate().alpha(0.0f).setDuration(300).setListener(animatorListener).start();
        }
    }

    /* access modifiers changed from: protected */
    public void setupDetailHeader(DetailAdapter detailAdapter) {
        this.mQsDetailHeaderTitle.setText(detailAdapter.getTitle());
    }

    private void handleToggleStateChanged(boolean z, boolean z2) {
        this.mSwitchState = z;
        if (!this.mAnimatingOpen) {
            Switch switchR = this.mQsDetailHeaderSwitch;
            if (switchR != null) {
                switchR.setChecked(z);
            }
            this.mQsDetailHeader.setEnabled(z2);
            Switch switchR2 = this.mQsDetailHeaderSwitch;
            if (switchR2 != null) {
                switchR2.setEnabled(z2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleScanStateChanged(boolean z) {
        if (this.mScanState != z) {
            this.mScanState = z;
            Animatable animatable = (Animatable) this.mQsDetailHeaderProgress.getDrawable();
            if (z) {
                this.mQsDetailHeaderProgress.animate().cancel();
                ViewPropertyAnimator alpha = this.mQsDetailHeaderProgress.animate().alpha(1.0f);
                Objects.requireNonNull(animatable);
                alpha.withEndAction(new CliQSDetail$$ExternalSyntheticLambda0(animatable)).start();
                return;
            }
            this.mQsDetailHeaderProgress.animate().cancel();
            ViewPropertyAnimator alpha2 = this.mQsDetailHeaderProgress.animate().alpha(0.0f);
            Objects.requireNonNull(animatable);
            alpha2.withEndAction(new CliQSDetail$$ExternalSyntheticLambda1(animatable)).start();
        }
    }

    /* access modifiers changed from: private */
    public void checkPendingAnimations() {
        boolean z = this.mSwitchState;
        DetailAdapter detailAdapter = this.mDetailAdapter;
        handleToggleStateChanged(z, detailAdapter != null && detailAdapter.getToggleEnabled());
    }

    public void closeDetail() {
        CliQSPanelNew cliQSPanelNew = this.mCliQsPanel;
        if (cliQSPanelNew != null) {
            cliQSPanelNew.closeDetail();
        }
    }

    private int getColorAttr(int i) {
        return Utils.getColorAttrDefaultColor(this.mContext, i);
    }
}
