package com.android.systemui.p006qs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$layout;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSPanel;
import com.android.systemui.p006qs.QSPanelControllerBase;
import com.android.systemui.plugins.p005qs.QSTile;
import com.motorola.systemui.desktop.widget.DesktopQSPanelArrowLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/* renamed from: com.android.systemui.qs.PagedTileLayout */
public class PagedTileLayout extends ViewPager implements QSPanel.QSTileLayout {
    private static final Interpolator SCROLL_CUBIC = PagedTileLayout$$ExternalSyntheticLambda0.INSTANCE;
    /* access modifiers changed from: private */
    public static boolean mCliMediaViewVisibility;
    private int mActivePointerId = -1;
    private final PagerAdapter mAdapter;
    /* access modifiers changed from: private */
    public AnimatorSet mBounceAnimatorSet;
    CliMediaPanelVisibleState mCliMediaPanelVisibleState;
    /* access modifiers changed from: private */
    public int mCurPosition;
    private DesktopQSPanelArrowLayout mDesktopQSPanelArrowLayout;
    private boolean mDistributeTiles = false;
    private int mExcessHeight;
    private float mInitialMotionX;
    private int mLastExcessHeight;
    private float mLastExpansion;
    private int mLastMaxHeight = -1;
    private int mLastPosition;
    private int mLayoutDirection;
    private int mLayoutOrientation;
    private boolean mListening;
    private int mMaxColumns = 100;
    private int mMinRows = 1;
    private final ViewPager.OnPageChangeListener mOnPageChangeListener;
    /* access modifiers changed from: private */
    public PageIndicator mPageIndicator;
    /* access modifiers changed from: private */
    public float mPageIndicatorPosition;
    /* access modifiers changed from: private */
    public PageListener mPageListener;
    private float mPageOffsetRatio;
    private int mPageToRestore = -1;
    /* access modifiers changed from: private */
    public final ArrayList<TileLayout> mPages = new ArrayList<>();
    private Scroller mScroller;
    private final ArrayList<QSPanelControllerBase.TileRecord> mTiles = new ArrayList<>();
    private final UiEventLogger mUiEventLogger = QSEvents.INSTANCE.getQsUiEventsLogger();

    /* renamed from: com.android.systemui.qs.PagedTileLayout$CliMediaPanelVisibleState */
    interface CliMediaPanelVisibleState {
        boolean isVisible();
    }

    /* renamed from: com.android.systemui.qs.PagedTileLayout$PageListener */
    public interface PageListener {
        void onPageChanged(boolean z);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2) + 1.0f;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public PagedTileLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        C11632 r0 = new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int i) {
                int unused = PagedTileLayout.this.mCurPosition = i;
                PagedTileLayout.this.updateSelected();
                if (PagedTileLayout.this.mPageIndicator != null && PagedTileLayout.this.mPageListener != null) {
                    PageListener access$400 = PagedTileLayout.this.mPageListener;
                    boolean z = false;
                    if (!PagedTileLayout.this.isLayoutRtl() ? i == 0 : i == PagedTileLayout.this.mPages.size() - 1) {
                        z = true;
                    }
                    access$400.onPageChanged(z);
                }
            }

            public void onPageScrolled(int i, float f, int i2) {
                if (PagedTileLayout.this.mPageIndicator != null) {
                    float unused = PagedTileLayout.this.mPageIndicatorPosition = ((float) i) + f;
                    PagedTileLayout.this.mPageIndicator.setLocation(PagedTileLayout.this.mPageIndicatorPosition);
                    if (PagedTileLayout.this.mPageListener != null) {
                        PageListener access$400 = PagedTileLayout.this.mPageListener;
                        boolean z = true;
                        if (i2 != 0 || (!PagedTileLayout.this.isLayoutRtl() ? i != 0 : i != PagedTileLayout.this.mPages.size() - 1)) {
                            z = false;
                        }
                        access$400.onPageChanged(z);
                    }
                }
            }
        };
        this.mOnPageChangeListener = r0;
        C11643 r1 = new PagerAdapter() {
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                viewGroup.removeView((View) obj);
                PagedTileLayout.this.updateListening();
            }

            public Object instantiateItem(ViewGroup viewGroup, int i) {
                if (PagedTileLayout.this.isLayoutRtl()) {
                    i = (PagedTileLayout.this.mPages.size() - 1) - i;
                }
                ViewGroup viewGroup2 = (ViewGroup) PagedTileLayout.this.mPages.get(i);
                if (viewGroup2.getParent() != null) {
                    viewGroup.removeView(viewGroup2);
                }
                viewGroup.addView(viewGroup2);
                PagedTileLayout.this.updateListening();
                return viewGroup2;
            }

            public int getCount() {
                return PagedTileLayout.this.mPages.size();
            }
        };
        this.mAdapter = r1;
        this.mCliMediaPanelVisibleState = PagedTileLayout$$ExternalSyntheticLambda1.INSTANCE;
        this.mScroller = new Scroller(context, SCROLL_CUBIC);
        setAdapter(r1);
        setOnPageChangeListener(r0);
        setCurrentItem(0, false);
        this.mLayoutOrientation = getResources().getConfiguration().orientation;
        this.mLayoutDirection = getLayoutDirection();
    }

    public void setPageMargin(int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        int i2 = -i;
        marginLayoutParams.setMarginStart(i2);
        marginLayoutParams.setMarginEnd(i2);
        setLayoutParams(marginLayoutParams);
        int size = this.mPages.size();
        for (int i3 = 0; i3 < size; i3++) {
            View view = this.mPages.get(i3);
            view.setPadding(i, view.getPaddingTop(), i, view.getPaddingBottom());
        }
    }

    public void saveInstanceState(Bundle bundle) {
        bundle.putInt("current_page", getCurrentItem());
    }

    public void restoreInstanceState(Bundle bundle) {
        this.mPageToRestore = bundle.getInt("current_page", -1);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = this.mLayoutOrientation;
        int i2 = configuration.orientation;
        if (i != i2) {
            this.mLayoutOrientation = i2;
            setCurrentItem(0, false);
            this.mPageToRestore = 0;
        }
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        if (this.mLayoutDirection != i) {
            this.mLayoutDirection = i;
            setAdapter(this.mAdapter);
            setCurrentItem(0, false);
            this.mPageToRestore = 0;
        }
    }

    public void setCurrentItem(int i, boolean z) {
        if (isLayoutRtl()) {
            i = (this.mPages.size() - 1) - i;
        }
        super.setCurrentItem(i, z);
    }

    private int getCurrentPageNumber() {
        int currentItem = getCurrentItem();
        return this.mLayoutDirection == 1 ? (this.mPages.size() - 1) - currentItem : currentItem;
    }

    private void logVisibleTiles(TileLayout tileLayout) {
        for (int i = 0; i < tileLayout.mRecords.size(); i++) {
            QSTile qSTile = tileLayout.mRecords.get(i).tile;
            this.mUiEventLogger.logWithInstanceId(QSEvent.QS_TILE_VISIBLE, 0, qSTile.getMetricsSpec(), qSTile.getInstanceId());
        }
    }

    public void setListening(boolean z, UiEventLogger uiEventLogger) {
        if (this.mListening != z) {
            this.mListening = z;
            updateListening();
        }
    }

    /* access modifiers changed from: private */
    public void updateListening() {
        Iterator<TileLayout> it = this.mPages.iterator();
        while (it.hasNext()) {
            TileLayout next = it.next();
            next.setListening(next.getParent() != null && this.mListening);
        }
    }

    public void fakeDragBy(float f) {
        try {
            super.fakeDragBy(f);
            postInvalidateOnAnimation();
        } catch (NullPointerException e) {
            Log.e("PagedTileLayout", "FakeDragBy called before begin", e);
            post(new PagedTileLayout$$ExternalSyntheticLambda2(this, this.mPages.size() - 1));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fakeDragBy$1(int i) {
        setCurrentItem(i, true);
        AnimatorSet animatorSet = this.mBounceAnimatorSet;
        if (animatorSet != null) {
            animatorSet.start();
        }
        setOffscreenPageLimit(1);
    }

    public void endFakeDrag() {
        try {
            super.endFakeDrag();
        } catch (NullPointerException e) {
            Log.e("PagedTileLayout", "endFakeDrag called without velocityTracker", e);
        }
    }

    public void computeScroll() {
        if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
            if (!isFakeDragging()) {
                beginFakeDrag();
            }
            fakeDragBy((float) (getScrollX() - this.mScroller.getCurrX()));
        } else if (isFakeDragging()) {
            endFakeDrag();
            AnimatorSet animatorSet = this.mBounceAnimatorSet;
            if (animatorSet != null) {
                animatorSet.start();
            }
            setOffscreenPageLimit(1);
        }
        super.computeScroll();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mBounceAnimatorSet != null) {
            return false;
        }
        if ((motionEvent.getAction() & 255) == 0) {
            this.mLastPosition = this.mCurPosition;
            this.mActivePointerId = motionEvent.getPointerId(0);
            this.mInitialMotionX = motionEvent.getX();
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPages.add(createTileLayout());
        this.mAdapter.notifyDataSetChanged();
    }

    private TileLayout createTileLayout() {
        TileLayout tileLayout = (TileLayout) LayoutInflater.from(getContext()).inflate(R$layout.qs_paged_page, this, false);
        tileLayout.setMinRows(this.mMinRows);
        tileLayout.setMaxColumns(this.mMaxColumns);
        tileLayout.setCliMediaPanelVisibleState(this.mCliMediaPanelVisibleState);
        return tileLayout;
    }

    public void setPageIndicator(PageIndicator pageIndicator) {
        this.mPageIndicator = pageIndicator;
        if (MotoFeature.isCliContext(this.mContext)) {
            this.mPageIndicator.setTintList(ColorStateList.valueOf(-1));
        }
        this.mPageIndicator.setNumPages(this.mPages.size());
        this.mPageIndicator.setLocation(this.mPageIndicatorPosition);
    }

    public int getOffsetTop(QSPanelControllerBase.TileRecord tileRecord) {
        ViewGroup viewGroup = (ViewGroup) tileRecord.tileView.getParent();
        if (viewGroup == null) {
            return 0;
        }
        return viewGroup.getTop() + getTop();
    }

    public void addTile(QSPanelControllerBase.TileRecord tileRecord) {
        this.mTiles.add(tileRecord);
        this.mDistributeTiles = true;
        requestLayout();
    }

    public void removeTile(QSPanelControllerBase.TileRecord tileRecord) {
        if (this.mTiles.remove(tileRecord)) {
            this.mDistributeTiles = true;
            requestLayout();
        }
    }

    public void setExpansion(float f, float f2) {
        this.mLastExpansion = f;
        updateSelected();
    }

    /* access modifiers changed from: private */
    public void updateSelected() {
        float f = this.mLastExpansion;
        if (f <= 0.0f || f >= 1.0f) {
            boolean z = f == 1.0f;
            setImportantForAccessibility(4);
            int currentPageNumber = getCurrentPageNumber();
            int i = 0;
            while (i < this.mPages.size()) {
                TileLayout tileLayout = this.mPages.get(i);
                tileLayout.setSelected(i == currentPageNumber ? z : false);
                if (tileLayout.isSelected()) {
                    logVisibleTiles(tileLayout);
                }
                i++;
            }
            DesktopQSPanelArrowLayout desktopQSPanelArrowLayout = this.mDesktopQSPanelArrowLayout;
            if (desktopQSPanelArrowLayout != null) {
                desktopQSPanelArrowLayout.setPosition(currentPageNumber);
            }
            setImportantForAccessibility(0);
        }
    }

    public void setPageListener(PageListener pageListener) {
        this.mPageListener = pageListener;
    }

    private void distributeTiles() {
        emptyAndInflateOrRemovePages();
        int maxTiles = this.mPages.get(0).maxTiles();
        int size = this.mTiles.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            QSPanelControllerBase.TileRecord tileRecord = this.mTiles.get(i2);
            if (this.mPages.get(i).mRecords.size() == maxTiles) {
                i++;
            }
            this.mPages.get(i).addTile(tileRecord);
        }
    }

    private void emptyAndInflateOrRemovePages() {
        int numPages = getNumPages();
        int size = this.mPages.size();
        for (int i = 0; i < size; i++) {
            this.mPages.get(i).removeAllViews();
        }
        if (size != numPages) {
            while (this.mPages.size() < numPages) {
                this.mPages.add(createTileLayout());
            }
            while (this.mPages.size() > numPages) {
                ArrayList<TileLayout> arrayList = this.mPages;
                arrayList.remove(arrayList.size() - 1);
            }
            this.mPageIndicator.setNumPages(this.mPages.size());
            DesktopQSPanelArrowLayout desktopQSPanelArrowLayout = this.mDesktopQSPanelArrowLayout;
            if (desktopQSPanelArrowLayout != null) {
                desktopQSPanelArrowLayout.setNumPages(this.mPages.size());
            }
            setAdapter(this.mAdapter);
            this.mAdapter.notifyDataSetChanged();
            int i2 = this.mPageToRestore;
            if (i2 != -1) {
                setCurrentItem(i2, false);
                this.mPageToRestore = -1;
            }
        }
    }

    public boolean updateResources() {
        boolean z = false;
        for (int i = 0; i < this.mPages.size(); i++) {
            z |= this.mPages.get(i).updateResources();
        }
        if (z) {
            this.mDistributeTiles = true;
            requestLayout();
        }
        return z;
    }

    public boolean setMinRows(int i) {
        this.mMinRows = i;
        boolean z = false;
        for (int i2 = 0; i2 < this.mPages.size(); i2++) {
            if (this.mPages.get(i2).setMinRows(i)) {
                this.mDistributeTiles = true;
                z = true;
            }
        }
        return z;
    }

    public boolean setMaxColumns(int i) {
        this.mMaxColumns = i;
        boolean z = false;
        for (int i2 = 0; i2 < this.mPages.size(); i2++) {
            if (this.mPages.get(i2).setMaxColumns(i)) {
                this.mDistributeTiles = true;
                z = true;
            }
        }
        return z;
    }

    public void setExcessHeight(int i) {
        this.mExcessHeight = i;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = this.mTiles.size();
        if (!(!this.mDistributeTiles && this.mLastMaxHeight == View.MeasureSpec.getSize(i2) && this.mLastExcessHeight == this.mExcessHeight)) {
            int size2 = View.MeasureSpec.getSize(i2);
            this.mLastMaxHeight = size2;
            int i3 = this.mExcessHeight;
            this.mLastExcessHeight = i3;
            if (this.mPages.get(0).updateMaxRows(size2 - i3, size) || this.mDistributeTiles) {
                this.mDistributeTiles = false;
                distributeTiles();
            }
            int i4 = this.mPages.get(0).mRows;
            for (int i5 = 0; i5 < this.mPages.size(); i5++) {
                this.mPages.get(i5).mRows = i4;
            }
        }
        super.onMeasure(i, i2);
        int childCount = getChildCount();
        int i6 = 0;
        for (int i7 = 0; i7 < childCount; i7++) {
            int measuredHeight = getChildAt(i7).getMeasuredHeight();
            if (measuredHeight > i6) {
                i6 = measuredHeight;
            }
        }
        setMeasuredDimension(getMeasuredWidth(), i6 + getPaddingBottom());
    }

    public int getColumnCount() {
        if (this.mPages.size() == 0) {
            return 0;
        }
        return this.mPages.get(0).mColumns;
    }

    public int getNumPages() {
        int size = this.mTiles.size();
        int max = Math.max(size / this.mPages.get(0).maxTiles(), 1);
        return size > this.mPages.get(0).maxTiles() * max ? max + 1 : max;
    }

    public int getNumVisibleTiles() {
        if (this.mPages.size() == 0) {
            return 0;
        }
        return this.mPages.get(getCurrentPageNumber()).mRecords.size();
    }

    public void startTileReveal(Set<String> set, final Runnable runnable) {
        if (!set.isEmpty() && this.mPages.size() >= 2 && getScrollX() == 0 && beginFakeDrag()) {
            int size = this.mPages.size() - 1;
            ArrayList arrayList = new ArrayList();
            Iterator<QSPanelControllerBase.TileRecord> it = this.mPages.get(size).mRecords.iterator();
            while (it.hasNext()) {
                QSPanelControllerBase.TileRecord next = it.next();
                if (set.contains(next.tile.getTileSpec())) {
                    arrayList.add(setupBounceAnimator(next.tileView, arrayList.size()));
                }
            }
            if (arrayList.isEmpty()) {
                endFakeDrag();
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.mBounceAnimatorSet = animatorSet;
            animatorSet.playTogether(arrayList);
            this.mBounceAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = PagedTileLayout.this.mBounceAnimatorSet = null;
                    runnable.run();
                }
            });
            setOffscreenPageLimit(size);
            int width = getWidth() * size;
            Scroller scroller = this.mScroller;
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            if (isLayoutRtl()) {
                width = -width;
            }
            scroller.startScroll(scrollX, scrollY, width, 0, 750);
            postInvalidateOnAnimation();
        }
    }

    private static Animator setupBounceAnimator(View view, int i) {
        view.setAlpha(0.0f);
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(View.ALPHA, new float[]{1.0f}), PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{1.0f}), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{1.0f})});
        ofPropertyValuesHolder.setDuration(450);
        ofPropertyValuesHolder.setStartDelay((long) (i * 85));
        ofPropertyValuesHolder.setInterpolator(new OvershootInterpolator(1.3f));
        return ofPropertyValuesHolder;
    }

    public void updateCliMediaViewVisibility(boolean z) {
        if (mCliMediaViewVisibility != z) {
            this.mDistributeTiles = true;
            mCliMediaViewVisibility = z;
        }
    }

    public void setPageOffsetRatio(float f) {
        if (f > 0.0f && f < 1.0f) {
            this.mPageOffsetRatio = f;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i;
        int action = motionEvent.getAction();
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        if (onTouchEvent && this.mPageOffsetRatio != 0.0f && ((i = action & 255) == 1 || i == 3)) {
            if (this.mLastPosition == this.mCurPosition) {
                int x = (int) (motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId)) - this.mInitialMotionX);
                if (((float) Math.abs(x)) > ((float) getMeasuredWidth()) * this.mPageOffsetRatio) {
                    setCurrentItem(determineTargetPage(this.mCurPosition, x), true);
                }
            }
            this.mActivePointerId = -1;
        }
        return onTouchEvent;
    }

    private int determineTargetPage(int i, int i2) {
        return MathUtils.constrain(i2 > 0 ? i - 1 : i + 1, 0, this.mPages.size() - 1);
    }

    public void setDesktopFooterPageArrow(DesktopQSPanelArrowLayout desktopQSPanelArrowLayout) {
        if (DesktopFeature.isDesktopDisplayContext(getContext())) {
            this.mDesktopQSPanelArrowLayout = desktopQSPanelArrowLayout;
            desktopQSPanelArrowLayout.setNumPages(this.mPages.size());
            this.mDesktopQSPanelArrowLayout.setPosition((int) this.mPageIndicatorPosition);
        }
    }
}
