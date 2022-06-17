package com.android.systemui.p006qs;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import com.android.p011wm.shell.animation.Interpolators;
import com.android.systemui.p006qs.PagedTileLayout;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.QSPanel;
import com.android.systemui.p006qs.TouchAnimator;
import com.android.systemui.p006qs.tileimpl.HeightOverrideable;
import com.android.systemui.plugins.p005qs.C1129QS;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.p005qs.QSTileView;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.animation.UniqueObjectHostView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

/* renamed from: com.android.systemui.qs.QSAnimator */
public class QSAnimator implements QSHost.Callback, PagedTileLayout.PageListener, TouchAnimator.Listener, View.OnLayoutChangeListener, View.OnAttachStateChangeListener, TunerService.Tunable {
    private TouchAnimator mAllPagesDelayedAnimator;
    private final ArrayList<View> mAllViews = new ArrayList<>();
    private boolean mAllowFancy;
    private TouchAnimator mBrightnessAnimator;
    private final Executor mExecutor;
    private TouchAnimator mFirstPageAnimator;
    private TouchAnimator mFirstPageDelayedAnimator;
    private boolean mFullRows;
    private final QSTileHost mHost;
    private float mLastPosition;
    private boolean mNeedsAnimatorUpdate = false;
    private final TouchAnimator.Listener mNonFirstPageListener = new TouchAnimator.ListenerAdapter() {
        public void onAnimationAtEnd() {
            QSAnimator.this.mQuickQsPanel.setVisibility(4);
        }

        public void onAnimationStarted() {
            QSAnimator.this.mQuickQsPanel.setVisibility(0);
        }
    };
    private TouchAnimator mNonfirstPageAnimator;
    private TouchAnimator mNonfirstPageDelayedAnimator;
    private int mNumQuickTiles;
    private boolean mOnFirstPage = true;
    private boolean mOnKeyguard;
    private HeightExpansionAnimator mOtherTilesExpandAnimator;
    private PagedTileLayout mPagedLayout;
    private HeightExpansionAnimator mQQSTileHeightAnimator;
    private QSExpansionPathInterpolator mQSExpansionPathInterpolator;
    private final C1129QS mQs;
    private final QSPanelController mQsPanelController;
    private final QuickQSPanelController mQuickQSPanelController;
    /* access modifiers changed from: private */
    public final QuickQSPanel mQuickQsPanel;
    private final ArrayList<View> mQuickQsViews = new ArrayList<>();
    private final QuickStatusBarHeader mQuickStatusBarHeader;
    private final QSSecurityFooter mSecurityFooter;
    private boolean mShowCollapsedOnKeyguard;
    private boolean mToShowing;
    private boolean mTranslateWhileExpanding;
    private TouchAnimator mTranslationXAnimator;
    private TouchAnimator mTranslationYAnimator;
    private final TunerService mTunerService;
    private final Runnable mUpdateAnimators = new QSAnimator$$ExternalSyntheticLambda0(this);

    public QSAnimator(C1129QS qs, QuickQSPanel quickQSPanel, QuickStatusBarHeader quickStatusBarHeader, QSPanelController qSPanelController, QuickQSPanelController quickQSPanelController, QSTileHost qSTileHost, QSSecurityFooter qSSecurityFooter, Executor executor, TunerService tunerService, QSExpansionPathInterpolator qSExpansionPathInterpolator) {
        this.mQs = qs;
        this.mQuickQsPanel = quickQSPanel;
        this.mQsPanelController = qSPanelController;
        this.mQuickQSPanelController = quickQSPanelController;
        this.mQuickStatusBarHeader = quickStatusBarHeader;
        this.mSecurityFooter = qSSecurityFooter;
        this.mHost = qSTileHost;
        this.mExecutor = executor;
        this.mTunerService = tunerService;
        this.mQSExpansionPathInterpolator = qSExpansionPathInterpolator;
        qSTileHost.addCallback(this);
        qSPanelController.addOnAttachStateChangeListener(this);
        qs.getView().addOnLayoutChangeListener(this);
        if (qSPanelController.isAttachedToWindow()) {
            onViewAttachedToWindow((View) null);
        }
        QSPanel.QSTileLayout tileLayout = qSPanelController.getTileLayout();
        if (tileLayout instanceof PagedTileLayout) {
            this.mPagedLayout = (PagedTileLayout) tileLayout;
        } else {
            Log.w("QSAnimator", "QS Not using page layout");
        }
        qSPanelController.setPageListener(this);
    }

    public void onRtlChanged() {
        updateAnimators();
    }

    public void requestAnimatorUpdate() {
        this.mNeedsAnimatorUpdate = true;
    }

    public void setOnKeyguard(boolean z) {
        this.mOnKeyguard = z;
        updateQQSVisibility();
        if (this.mOnKeyguard) {
            clearAnimationState();
        }
    }

    /* access modifiers changed from: package-private */
    public void startAlphaAnimation(boolean z) {
        if (z != this.mToShowing) {
            this.mToShowing = z;
            if (z) {
                CrossFadeHelper.fadeIn(this.mQs.getView(), 200, 0);
            } else {
                CrossFadeHelper.fadeOut(this.mQs.getView(), 50, 0, (Runnable) null);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setShowCollapsedOnKeyguard(boolean z) {
        this.mShowCollapsedOnKeyguard = z;
        updateQQSVisibility();
        setCurrentPosition();
    }

    private void setCurrentPosition() {
        setPosition(this.mLastPosition);
    }

    private void updateQQSVisibility() {
        this.mQuickQsPanel.setVisibility((!this.mOnKeyguard || this.mShowCollapsedOnKeyguard) ? 0 : 4);
    }

    public void onViewAttachedToWindow(View view) {
        this.mTunerService.addTunable(this, "sysui_qs_fancy_anim", "sysui_qs_move_whole_rows");
    }

    public void onViewDetachedFromWindow(View view) {
        this.mHost.removeCallback(this);
        this.mTunerService.removeTunable(this);
    }

    public void onTuningChanged(String str, String str2) {
        if ("sysui_qs_fancy_anim".equals(str)) {
            boolean parseIntegerSwitch = TunerService.parseIntegerSwitch(str2, true);
            this.mAllowFancy = parseIntegerSwitch;
            if (!parseIntegerSwitch) {
                clearAnimationState();
            }
        } else if ("sysui_qs_move_whole_rows".equals(str)) {
            this.mFullRows = TunerService.parseIntegerSwitch(str2, true);
        }
        updateAnimators();
    }

    public void onPageChanged(boolean z) {
        if (this.mOnFirstPage != z) {
            if (!z) {
                clearAnimationState();
            }
            this.mOnFirstPage = z;
        }
    }

    private void translateContent(View view, View view2, View view3, int i, int i2, int[] iArr, TouchAnimator.Builder builder, TouchAnimator.Builder builder2) {
        getRelativePosition(iArr, view, view3);
        int i3 = iArr[0];
        int i4 = iArr[1];
        getRelativePosition(iArr, view2, view3);
        int i5 = iArr[0];
        int i6 = iArr[1];
        int i7 = (i5 - i3) - i;
        builder.addFloat(view, "translationX", 0.0f, (float) i7);
        builder.addFloat(view2, "translationX", (float) (-i7), 0.0f);
        int i8 = (i6 - i4) - i2;
        builder2.addFloat(view, "translationY", 0.0f, (float) i8);
        builder2.addFloat(view2, "translationY", (float) (-i8), 0.0f);
        this.mAllViews.add(view);
        this.mAllViews.add(view2);
    }

    private void updateAnimators() {
        QSPanel.QSTileLayout qSTileLayout;
        UniqueObjectHostView uniqueObjectHostView;
        String str;
        QSPanel.QSTileLayout qSTileLayout2;
        String str2;
        String str3;
        boolean z;
        int i;
        int i2;
        QSAnimator qSAnimator;
        QSAnimator qSAnimator2 = this;
        qSAnimator2.mNeedsAnimatorUpdate = false;
        TouchAnimator.Builder builder = new TouchAnimator.Builder();
        TouchAnimator.Builder builder2 = new TouchAnimator.Builder();
        TouchAnimator.Builder builder3 = new TouchAnimator.Builder();
        Collection<QSTile> tiles = qSAnimator2.mHost.getTiles();
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        clearAnimationState();
        qSAnimator2.mAllViews.clear();
        qSAnimator2.mQuickQsViews.clear();
        qSAnimator2.mQQSTileHeightAnimator = null;
        qSAnimator2.mOtherTilesExpandAnimator = null;
        qSAnimator2.mNumQuickTiles = qSAnimator2.mQuickQsPanel.getNumQuickTiles();
        QSPanel.QSTileLayout tileLayout = qSAnimator2.mQsPanelController.getTileLayout();
        qSAnimator2.mAllViews.add((View) tileLayout);
        int measuredHeight = ((qSAnimator2.mQs.getView() != null ? qSAnimator2.mQs.getView().getMeasuredHeight() : 0) - qSAnimator2.mQs.getHeader().getBottom()) + qSAnimator2.mQs.getHeader().getPaddingBottom();
        if (!qSAnimator2.mTranslateWhileExpanding) {
            measuredHeight = (int) (((float) measuredHeight) * 0.1f);
        }
        int i3 = measuredHeight;
        boolean z2 = true;
        String str4 = "translationY";
        builder.addFloat(tileLayout, str4, (float) i3, 0.0f);
        String str5 = "alpha";
        if (qSAnimator2.mQsPanelController.areThereTiles()) {
            int i4 = 0;
            int i5 = 0;
            for (QSTile next : tiles) {
                QSTileView tileView = qSAnimator2.mQsPanelController.getTileView(next);
                if (tileView == null) {
                    Log.e("QSAnimator", "tileView is null " + next.getTileSpec());
                    str = str5;
                } else {
                    View iconView = tileView.getIcon().getIconView();
                    View view = qSAnimator2.mQs.getView();
                    str = str5;
                    if (i5 >= qSAnimator2.mQuickQSPanelController.getTileLayout().getNumVisibleTiles() || !qSAnimator2.mAllowFancy) {
                        int i6 = i5;
                        String str6 = str4;
                        int i7 = i3;
                        qSTileLayout2 = tileLayout;
                        int[] iArr3 = iArr2;
                        qSAnimator = qSAnimator2;
                        str2 = str;
                        View view2 = view;
                        if (qSAnimator.mFullRows) {
                            i2 = i6;
                            if (qSAnimator.isIconInAnimatedRow(i2)) {
                                i = i7;
                                z = true;
                                str3 = str6;
                                builder.addFloat(tileView, str3, (float) (-i), 0.0f);
                                qSAnimator.mAllViews.add(iconView);
                                iArr2 = iArr3;
                            } else {
                                str3 = str6;
                                i = i7;
                            }
                        } else {
                            str3 = str6;
                            i = i7;
                            i2 = i6;
                        }
                        z = true;
                        SideLabelTileLayout sideLabelTileLayout = (SideLabelTileLayout) qSAnimator.mQuickQsPanel.getTileLayout();
                        View view3 = view2;
                        qSAnimator.getRelativePosition(iArr, sideLabelTileLayout, view3);
                        iArr2 = iArr3;
                        qSAnimator.getRelativePosition(iArr2, tileView, view3);
                        builder2.addFloat(tileView, str3, (float) (-(iArr2[1] - (iArr[1] + sideLabelTileLayout.getPhantomTopPosition(i2)))), 0.0f);
                        if (qSAnimator.mOtherTilesExpandAnimator == null) {
                            qSAnimator.mOtherTilesExpandAnimator = new HeightExpansionAnimator(qSAnimator, i4, tileView.getHeight());
                        }
                        qSAnimator.mOtherTilesExpandAnimator.addView(tileView);
                        tileView.setClipChildren(true);
                        tileView.setClipToPadding(true);
                        builder.addFloat(tileView.getSecondaryLabel(), str2, 0.0f, 1.0f);
                    } else {
                        QSTileView tileView2 = qSAnimator2.mQuickQSPanelController.getTileView(next);
                        if (tileView2 != null) {
                            qSAnimator2.getRelativePosition(iArr, tileView2, view);
                            qSAnimator2.getRelativePosition(iArr2, tileView, view);
                            int i8 = iArr2[1] - iArr[1];
                            int i9 = iArr2[0] - iArr[0];
                            int offsetTranslation = i8 - qSAnimator2.mQuickStatusBarHeader.getOffsetTranslation();
                            int i10 = i5;
                            int i11 = i3;
                            builder2.addFloat(tileView2, str4, 0.0f, (float) offsetTranslation);
                            builder2.addFloat(tileView, str4, (float) (-offsetTranslation), 0.0f);
                            builder3.addFloat(tileView2, "translationX", 0.0f, (float) i9);
                            builder3.addFloat(tileView, "translationX", (float) (-i9), 0.0f);
                            if (qSAnimator2.mQQSTileHeightAnimator == null) {
                                qSAnimator2.mQQSTileHeightAnimator = new HeightExpansionAnimator(qSAnimator2, tileView2.getHeight(), tileView.getHeight());
                                i4 = tileView2.getHeight();
                            }
                            int i12 = i4;
                            qSAnimator2.mQQSTileHeightAnimator.addView(tileView2);
                            str2 = str;
                            String str7 = str4;
                            View view4 = view;
                            View view5 = view;
                            int i13 = i9;
                            int i14 = i8;
                            qSTileLayout2 = tileLayout;
                            int[] iArr4 = iArr;
                            int i15 = i9;
                            TouchAnimator.Builder builder4 = builder3;
                            int[] iArr5 = iArr2;
                            TouchAnimator.Builder builder5 = builder2;
                            translateContent(tileView2.getIcon(), tileView.getIcon(), view4, i13, i14, iArr4, builder4, builder5);
                            View view6 = view5;
                            int i16 = i15;
                            translateContent(tileView2.getLabelContainer(), tileView.getLabelContainer(), view6, i16, i14, iArr4, builder4, builder5);
                            translateContent(tileView2.getSecondaryIcon(), tileView.getSecondaryIcon(), view6, i16, i14, iArr4, builder4, builder5);
                            builder.addFloat(tileView2.getSecondaryLabel(), str2, 0.0f, 1.0f);
                            qSAnimator = this;
                            qSAnimator.mQuickQsViews.add(tileView);
                            qSAnimator.mAllViews.add(tileView2);
                            qSAnimator.mAllViews.add(tileView2.getSecondaryLabel());
                            i4 = i12;
                            str3 = str7;
                            i = i11;
                            i2 = i10;
                            iArr2 = iArr5;
                            z = true;
                        }
                    }
                    qSAnimator.mAllViews.add(tileView);
                    z2 = z;
                    i3 = i;
                    str4 = str3;
                    tileLayout = qSTileLayout2;
                    String str8 = str2;
                    qSAnimator2 = qSAnimator;
                    i5 = i2 + 1;
                    str5 = str8;
                }
                str5 = str;
                z2 = true;
            }
        }
        char c = z2;
        QSPanel.QSTileLayout qSTileLayout3 = tileLayout;
        QSAnimator qSAnimator3 = qSAnimator2;
        String str9 = str5;
        String str10 = str4;
        if (qSAnimator3.mAllowFancy) {
            View brightnessView = qSAnimator3.mQsPanelController.getBrightnessView();
            if (brightnessView != null) {
                float[] fArr = new float[2];
                fArr[0] = ((float) brightnessView.getMeasuredHeight()) * 0.5f;
                fArr[c] = 0.0f;
                builder.addFloat(brightnessView, str10, fArr);
                qSAnimator3.mBrightnessAnimator = new TouchAnimator.Builder().addFloat(brightnessView, str9, 0.0f, 1.0f).addFloat(brightnessView, "sliderScaleY", 0.3f, 1.0f).setInterpolator(Interpolators.ALPHA_IN).setStartDelay(0.3f).build();
                qSAnimator3.mAllViews.add(brightnessView);
            } else {
                qSAnimator3.mBrightnessAnimator = null;
            }
            qSAnimator3.mFirstPageAnimator = builder.setListener(qSAnimator3).build();
            qSTileLayout = qSTileLayout3;
            qSAnimator3.mFirstPageDelayedAnimator = new TouchAnimator.Builder().addFloat(qSTileLayout, str9, 0.0f, 1.0f).build();
            TouchAnimator.Builder startDelay = new TouchAnimator.Builder().setStartDelay(0.86f);
            startDelay.addFloat(qSAnimator3.mSecurityFooter.getView(), str9, 0.0f, 1.0f);
            if (!qSAnimator3.mQsPanelController.shouldUseHorizontalLayout() || (uniqueObjectHostView = qSAnimator3.mQsPanelController.mMediaHost.hostView) == null) {
                qSAnimator3.mQsPanelController.mMediaHost.hostView.setAlpha(1.0f);
            } else {
                startDelay.addFloat(uniqueObjectHostView, str9, 0.0f, 1.0f);
            }
            qSAnimator3.mAllPagesDelayedAnimator = startDelay.build();
            qSAnimator3.mAllViews.add(qSAnimator3.mSecurityFooter.getView());
            builder2.setInterpolator(qSAnimator3.mQSExpansionPathInterpolator.getYInterpolator());
            builder3.setInterpolator(qSAnimator3.mQSExpansionPathInterpolator.getXInterpolator());
            qSAnimator3.mTranslationYAnimator = builder2.build();
            qSAnimator3.mTranslationXAnimator = builder3.build();
            HeightExpansionAnimator heightExpansionAnimator = qSAnimator3.mQQSTileHeightAnimator;
            if (heightExpansionAnimator != null) {
                heightExpansionAnimator.setInterpolator(qSAnimator3.mQSExpansionPathInterpolator.getYInterpolator());
            }
            HeightExpansionAnimator heightExpansionAnimator2 = qSAnimator3.mOtherTilesExpandAnimator;
            if (heightExpansionAnimator2 != null) {
                heightExpansionAnimator2.setInterpolator(qSAnimator3.mQSExpansionPathInterpolator.getYInterpolator());
            }
        } else {
            qSTileLayout = qSTileLayout3;
        }
        qSAnimator3.mNonfirstPageAnimator = new TouchAnimator.Builder().addFloat(qSAnimator3.mQuickQsPanel, str9, 1.0f, 0.0f).setListener(qSAnimator3.mNonFirstPageListener).setEndDelay(0.5f).build();
        qSAnimator3.mNonfirstPageDelayedAnimator = new TouchAnimator.Builder().setStartDelay(0.14f).addFloat(qSTileLayout, str9, 0.0f, 1.0f).build();
    }

    private boolean isIconInAnimatedRow(int i) {
        PagedTileLayout pagedTileLayout = this.mPagedLayout;
        if (pagedTileLayout == null) {
            return false;
        }
        int columnCount = pagedTileLayout.getColumnCount();
        if (i < (((this.mNumQuickTiles + columnCount) - 1) / columnCount) * columnCount) {
            return true;
        }
        return false;
    }

    private void getRelativePosition(int[] iArr, View view, View view2) {
        iArr[0] = (view.getWidth() / 2) + 0;
        iArr[1] = 0;
        getRelativePositionInt(iArr, view, view2);
    }

    private void getRelativePositionInt(int[] iArr, View view, View view2) {
        if (view != view2 && view != null) {
            if (!isAPage(view)) {
                iArr[0] = iArr[0] + view.getLeft();
                iArr[1] = iArr[1] + view.getTop();
            }
            if (!(view instanceof PagedTileLayout)) {
                iArr[0] = iArr[0] - view.getScrollX();
                iArr[1] = iArr[1] - view.getScrollY();
            }
            getRelativePositionInt(iArr, (View) view.getParent(), view2);
        }
    }

    private boolean isAPage(View view) {
        return view.getClass().equals(SideLabelTileLayout.class);
    }

    public void setPosition(float f) {
        if (this.mNeedsAnimatorUpdate) {
            updateAnimators();
        }
        if (this.mFirstPageAnimator != null) {
            if (this.mOnKeyguard) {
                f = this.mShowCollapsedOnKeyguard ? 0.0f : 1.0f;
            }
            this.mLastPosition = f;
            if (!this.mOnFirstPage || !this.mAllowFancy) {
                this.mNonfirstPageAnimator.setPosition(f);
                this.mNonfirstPageDelayedAnimator.setPosition(f);
            } else {
                this.mQuickQsPanel.setAlpha(1.0f);
                this.mFirstPageAnimator.setPosition(f);
                this.mFirstPageDelayedAnimator.setPosition(f);
                this.mTranslationYAnimator.setPosition(f);
                this.mTranslationXAnimator.setPosition(f);
                HeightExpansionAnimator heightExpansionAnimator = this.mQQSTileHeightAnimator;
                if (heightExpansionAnimator != null) {
                    heightExpansionAnimator.setPosition(f);
                }
                HeightExpansionAnimator heightExpansionAnimator2 = this.mOtherTilesExpandAnimator;
                if (heightExpansionAnimator2 != null) {
                    heightExpansionAnimator2.setPosition(f);
                }
            }
            if (this.mAllowFancy) {
                this.mAllPagesDelayedAnimator.setPosition(f);
                TouchAnimator touchAnimator = this.mBrightnessAnimator;
                if (touchAnimator != null) {
                    touchAnimator.setPosition(f);
                }
            }
        }
    }

    public void onAnimationAtStart() {
        this.mQuickQsPanel.setVisibility(0);
    }

    public void onAnimationAtEnd() {
        this.mQuickQsPanel.setVisibility(4);
        int size = this.mQuickQsViews.size();
        for (int i = 0; i < size; i++) {
            this.mQuickQsViews.get(i).setVisibility(0);
        }
    }

    public void onAnimationStarted() {
        updateQQSVisibility();
        if (this.mOnFirstPage) {
            int size = this.mQuickQsViews.size();
            for (int i = 0; i < size; i++) {
                this.mQuickQsViews.get(i).setVisibility(4);
            }
        }
    }

    private void clearAnimationState() {
        int size = this.mAllViews.size();
        this.mQuickQsPanel.setAlpha(0.0f);
        for (int i = 0; i < size; i++) {
            View view = this.mAllViews.get(i);
            view.setAlpha(1.0f);
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
            view.setScaleY(1.0f);
            if (view instanceof SideLabelTileLayout) {
                SideLabelTileLayout sideLabelTileLayout = (SideLabelTileLayout) view;
                sideLabelTileLayout.setClipChildren(false);
                sideLabelTileLayout.setClipToPadding(false);
            }
        }
        HeightExpansionAnimator heightExpansionAnimator = this.mQQSTileHeightAnimator;
        if (heightExpansionAnimator != null) {
            heightExpansionAnimator.resetViewsHeights();
        }
        HeightExpansionAnimator heightExpansionAnimator2 = this.mOtherTilesExpandAnimator;
        if (heightExpansionAnimator2 != null) {
            heightExpansionAnimator2.resetViewsHeights();
        }
        int size2 = this.mQuickQsViews.size();
        for (int i2 = 0; i2 < size2; i2++) {
            this.mQuickQsViews.get(i2).setVisibility(0);
        }
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mExecutor.execute(this.mUpdateAnimators);
    }

    public void onTilesChanged() {
        this.mExecutor.execute(this.mUpdateAnimators);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateAnimators();
        setCurrentPosition();
    }

    public void setTranslateWhileExpanding(boolean z) {
        this.mTranslateWhileExpanding = z;
    }

    /* renamed from: com.android.systemui.qs.QSAnimator$HeightExpansionAnimator */
    static class HeightExpansionAnimator {
        private final ValueAnimator mAnimator;
        /* access modifiers changed from: private */
        public final TouchAnimator.Listener mListener;
        private final ValueAnimator.AnimatorUpdateListener mUpdateListener;
        /* access modifiers changed from: private */
        public final List<View> mViews = new ArrayList();

        HeightExpansionAnimator(TouchAnimator.Listener listener, int i, int i2) {
            C11671 r0 = new ValueAnimator.AnimatorUpdateListener() {
                float mLastT = -1.0f;

                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animatedFraction = valueAnimator.getAnimatedFraction();
                    int size = HeightExpansionAnimator.this.mViews.size();
                    int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                    for (int i = 0; i < size; i++) {
                        View view = (View) HeightExpansionAnimator.this.mViews.get(i);
                        view.setBottom(view.getTop() + intValue);
                        if (view instanceof HeightOverrideable) {
                            ((HeightOverrideable) view).setHeightOverride(intValue);
                        }
                    }
                    if (animatedFraction == 0.0f) {
                        HeightExpansionAnimator.this.mListener.onAnimationAtStart();
                    } else if (animatedFraction == 1.0f) {
                        HeightExpansionAnimator.this.mListener.onAnimationAtEnd();
                    } else {
                        float f = this.mLastT;
                        if (f <= 0.0f || f == 1.0f) {
                            HeightExpansionAnimator.this.mListener.onAnimationStarted();
                        }
                    }
                    this.mLastT = animatedFraction;
                }
            };
            this.mUpdateListener = r0;
            this.mListener = listener;
            ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{i, i2});
            this.mAnimator = ofInt;
            ofInt.setRepeatCount(-1);
            ofInt.setRepeatMode(2);
            ofInt.addUpdateListener(r0);
        }

        /* access modifiers changed from: package-private */
        public void addView(View view) {
            this.mViews.add(view);
        }

        /* access modifiers changed from: package-private */
        public void setInterpolator(TimeInterpolator timeInterpolator) {
            this.mAnimator.setInterpolator(timeInterpolator);
        }

        /* access modifiers changed from: package-private */
        public void setPosition(float f) {
            this.mAnimator.setCurrentFraction(f);
        }

        /* access modifiers changed from: package-private */
        public void resetViewsHeights() {
            int size = this.mViews.size();
            for (int i = 0; i < size; i++) {
                View view = this.mViews.get(i);
                view.setBottom(view.getTop() + view.getMeasuredHeight());
                if (view instanceof HeightOverrideable) {
                    ((HeightOverrideable) view).resetOverride();
                }
            }
        }
    }
}
