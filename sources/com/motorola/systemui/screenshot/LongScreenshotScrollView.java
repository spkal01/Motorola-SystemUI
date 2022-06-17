package com.motorola.systemui.screenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;
import java.lang.ref.SoftReference;

public class LongScreenshotScrollView extends View {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static SoftReference<int[]> sPixelsCache;
    /* access modifiers changed from: private */
    public Runnable mAutoScrollRunnable;
    /* access modifiers changed from: private */
    public float mAutoScrollStepOffset;
    private BitmapArrayDrawable mBitmapArrayDrawable;
    private int mBitmapWidth;
    private int mCurStatus;
    private boolean mEnableOutline;
    private boolean mEnableTouchScroll;
    private boolean mInvalidClick;
    /* access modifiers changed from: private */
    public boolean mIsAutoScrollStoped;
    private boolean mIsUserTouchedDown;
    private float mLastTouchX;
    private float mLastTouchY;
    private boolean mLimitScrollYByParentTop;
    private int mMaxShowingHeight;
    private OnScrollListener mOnScrollListener;
    private OnTouchStatusListener mOnTouchStatusListener;
    private Scroller mScroller;
    private float mScrollerY;
    private Paint mShadowPaint;
    private int mTotalBitmapHeight;
    private float mTouchDownX;
    private float mTouchDownY;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    public interface OnScrollListener {
        void onScrollToEnd();

        void onStopAutoScrolled();
    }

    public interface OnTouchStatusListener {
        void onTouchStatusChanged(int i);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public LongScreenshotScrollView(Context context) {
        this(context, (AttributeSet) null);
    }

    public LongScreenshotScrollView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LongScreenshotScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mScrollerY = 0.0f;
        this.mBitmapArrayDrawable = new BitmapArrayDrawable();
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mCurStatus = 0;
        this.mAutoScrollStepOffset = 1.0f;
        this.mIsAutoScrollStoped = true;
        this.mOnScrollListener = null;
        this.mIsUserTouchedDown = false;
        this.mEnableOutline = false;
        this.mEnableTouchScroll = true;
        this.mLimitScrollYByParentTop = false;
        this.mShadowPaint = new Paint(1);
        this.mMaxShowingHeight = 0;
        this.mInvalidClick = false;
        this.mAutoScrollRunnable = new Runnable() {
            public void run() {
                if (!LongScreenshotScrollView.this.mIsAutoScrollStoped) {
                    LongScreenshotScrollView longScreenshotScrollView = LongScreenshotScrollView.this;
                    longScreenshotScrollView.autoScroll(longScreenshotScrollView.mAutoScrollStepOffset);
                    LongScreenshotScrollView longScreenshotScrollView2 = LongScreenshotScrollView.this;
                    longScreenshotScrollView2.post(longScreenshotScrollView2.mAutoScrollRunnable);
                }
            }
        };
        init();
    }

    public LongScreenshotScrollView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mScrollerY = 0.0f;
        this.mBitmapArrayDrawable = new BitmapArrayDrawable();
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mCurStatus = 0;
        this.mAutoScrollStepOffset = 1.0f;
        this.mIsAutoScrollStoped = true;
        this.mOnScrollListener = null;
        this.mIsUserTouchedDown = false;
        this.mEnableOutline = false;
        this.mEnableTouchScroll = true;
        this.mLimitScrollYByParentTop = false;
        this.mShadowPaint = new Paint(1);
        this.mMaxShowingHeight = 0;
        this.mInvalidClick = false;
        this.mAutoScrollRunnable = new Runnable() {
            public void run() {
                if (!LongScreenshotScrollView.this.mIsAutoScrollStoped) {
                    LongScreenshotScrollView longScreenshotScrollView = LongScreenshotScrollView.this;
                    longScreenshotScrollView.autoScroll(longScreenshotScrollView.mAutoScrollStepOffset);
                    LongScreenshotScrollView longScreenshotScrollView2 = LongScreenshotScrollView.this;
                    longScreenshotScrollView2.post(longScreenshotScrollView2.mAutoScrollRunnable);
                }
            }
        };
        init();
    }

    private void init() {
        this.mScroller = new Scroller(getContext());
        float f = (float) ((int) (getResources().getDisplayMetrics().density * 2.0f));
        this.mAutoScrollStepOffset = f;
        if (f < 1.0f) {
            this.mAutoScrollStepOffset = 1.0f;
        }
        this.mShadowPaint.setAntiAlias(true);
        this.mShadowPaint.setColor(0);
        this.mShadowPaint.setShadowLayer(10.0f, 0.0f, 10.0f, Color.argb(100, 0, 0, 0));
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    private int getHeightInner() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    private int getWidthInner() {
        return (getWidth() - getPaddingLeft()) - getPaddingRight();
    }

    private void addBitmapInternal(Bitmap[] bitmapArr, boolean z) {
        BitmapArrayDrawable bitmapArrayDrawable = this.mBitmapArrayDrawable;
        if (bitmapArrayDrawable != null && bitmapArr != null && bitmapArr.length != 0) {
            synchronized (bitmapArrayDrawable) {
                if (z) {
                    Bitmap removeLastBitmap = this.mBitmapArrayDrawable.removeLastBitmap();
                    if (removeLastBitmap != null) {
                        this.mTotalBitmapHeight -= removeLastBitmap.getHeight();
                        removeLastBitmap.recycle();
                    }
                }
                for (Bitmap bitmap : bitmapArr) {
                    if (bitmap != null) {
                        if (this.mBitmapWidth == 0) {
                            this.mBitmapWidth = bitmap.getWidth();
                        }
                        this.mTotalBitmapHeight += bitmap.getHeight();
                        this.mBitmapArrayDrawable.addBitmap(bitmap);
                    }
                }
            }
        }
    }

    public void addBitmaps(Bitmap[] bitmapArr, boolean z) {
        addBitmapInternal(bitmapArr, z);
        invalidate();
    }

    public void addBitmap(Bitmap bitmap) {
        addBitmaps(new Bitmap[]{bitmap}, false);
    }

    private void clearAllBitmapsInternal() {
        synchronized (this.mBitmapArrayDrawable) {
            this.mBitmapWidth = 0;
            this.mTotalBitmapHeight = 0;
            this.mScrollerY = 0.0f;
            this.mBitmapArrayDrawable.clearAllBitmaps();
        }
    }

    public void clearAllBitmaps() {
        clearAllBitmapsInternal();
        invalidate();
    }

    public void autoScroll(float f) {
        computeScrollY(-f, true);
    }

    public void startAutoScrollAnim() {
        this.mIsAutoScrollStoped = false;
        post(this.mAutoScrollRunnable);
    }

    public void stopAutoScrollAnim() {
        OnScrollListener onScrollListener;
        this.mIsAutoScrollStoped = true;
        removeCallbacks(this.mAutoScrollRunnable);
        if (this.mIsUserTouchedDown && (onScrollListener = this.mOnScrollListener) != null) {
            onScrollListener.onStopAutoScrolled();
        }
    }

    public int getCurCropTotalHeight() {
        return (int) (((-this.mScrollerY) + ((float) getHeightInner())) / (((float) getWidthInner()) / ((float) this.mBitmapWidth)));
    }

    private void computeScrollY(float f, boolean z) {
        float f2 = this.mScrollerY + f;
        float f3 = 0.0f;
        boolean z2 = false;
        if (f2 <= 0.0f) {
            if (this.mLimitScrollYByParentTop) {
                int i = -getTop();
                float f4 = (float) i;
                if (f2 < f4) {
                    float heightInner = ((float) (getHeightInner() - i)) / ((float) (getHeightInner() - ((int) f2)));
                    float f5 = 1.0f;
                    float widthInner = ((1.0f - heightInner) * ((float) getWidthInner())) / 2.0f;
                    if (widthInner >= 1.0f) {
                        f5 = widthInner;
                    }
                    int paddingLeft = (int) (((float) getPaddingLeft()) + f5);
                    setPadding(paddingLeft, 0, paddingLeft, 0);
                    f2 = f4;
                    z2 = true;
                }
            } else if (!z && getMinScrollY() > 0) {
                float f6 = (float) (-getMinScrollY());
                if (f2 > f6) {
                    f2 = f6;
                }
            }
            float maxScrollY = (float) getMaxScrollY();
            if (maxScrollY > 0.0f) {
                f3 = -maxScrollY;
            }
            if (f2 < f3) {
                OnScrollListener onScrollListener = this.mOnScrollListener;
                if (onScrollListener != null && !this.mIsUserTouchedDown) {
                    onScrollListener.onScrollToEnd();
                }
            } else {
                f3 = f2;
            }
        }
        if (this.mScrollerY != f3 || z2) {
            this.mScrollerY = f3;
            invalidate();
        }
    }

    private int getMinScrollY() {
        int i = this.mMaxShowingHeight;
        if (i <= 0) {
            return 0;
        }
        int heightInner = i - getHeightInner();
        int maxScrollY = getMaxScrollY();
        return heightInner > maxScrollY ? maxScrollY : heightInner;
    }

    private int getMaxScrollY() {
        float widthInner = ((float) this.mTotalBitmapHeight) * (((float) getWidthInner()) / ((float) this.mBitmapWidth));
        float heightInner = (float) getHeightInner();
        if (widthInner > heightInner) {
            return (int) (widthInner - heightInner);
        }
        return 0;
    }

    private int getVelocityY(int i) {
        return Math.min(i, Math.max(-i, (int) this.mVelocityTracker.getYVelocity()));
    }

    public float getVaildYStart() {
        float f = this.mScrollerY;
        return f < ((float) (-getMinScrollY())) ? (float) (-getMinScrollY()) : f;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002f, code lost:
        if (r13 <= ((float) getHeight())) goto L_0x0032;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchOutsideTouchEvent(int r11, float r12, float r13) {
        /*
            r10 = this;
            if (r11 != 0) goto L_0x0032
            r0 = 0
            int r0 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            r1 = 0
            if (r0 < 0) goto L_0x0031
            int r0 = r10.getWidth()
            float r0 = (float) r0
            int r0 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0012
            goto L_0x0031
        L_0x0012:
            float r0 = r10.mScrollerY
            int r2 = r10.getMinScrollY()
            int r2 = -r2
            float r2 = (float) r2
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0024
            int r0 = r10.getMinScrollY()
            int r0 = -r0
            float r0 = (float) r0
        L_0x0024:
            int r0 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x0031
            int r0 = r10.getHeight()
            float r0 = (float) r0
            int r0 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0032
        L_0x0031:
            return r1
        L_0x0032:
            long r2 = java.lang.System.currentTimeMillis()
            long r4 = java.lang.System.currentTimeMillis()
            r9 = 0
            r6 = r11
            r7 = r12
            r8 = r13
            android.view.MotionEvent r11 = android.view.MotionEvent.obtain(r2, r4, r6, r7, r8, r9)
            boolean r10 = r10.onTouchEvent(r11)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.screenshot.LongScreenshotScrollView.dispatchOutsideTouchEvent(int, float, float):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0021, code lost:
        if (r2 != 3) goto L_0x00cb;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
            r14 = this;
            com.motorola.systemui.screenshot.LongScreenshotScrollView$OnTouchStatusListener r0 = r14.mOnTouchStatusListener
            if (r0 == 0) goto L_0x000b
            int r1 = r15.getAction()
            r0.onTouchStatusChanged(r1)
        L_0x000b:
            float r0 = r15.getX()
            float r1 = r15.getY()
            int r2 = r15.getAction()
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x009e
            r5 = 2
            if (r2 == r4) goto L_0x0054
            if (r2 == r5) goto L_0x0025
            r6 = 3
            if (r2 == r6) goto L_0x0054
            goto L_0x00cb
        L_0x0025:
            boolean r2 = r14.mInvalidClick
            if (r2 != 0) goto L_0x0041
            float r2 = r14.mTouchDownX
            float r2 = r2 - r0
            float r2 = java.lang.Math.abs(r2)
            int r2 = (int) r2
            float r5 = r14.mTouchDownY
            float r5 = r5 - r1
            float r5 = java.lang.Math.abs(r5)
            int r5 = (int) r5
            int r6 = r14.mTouchSlop
            if (r2 > r6) goto L_0x003f
            if (r5 <= r6) goto L_0x0041
        L_0x003f:
            r14.mInvalidClick = r4
        L_0x0041:
            boolean r2 = r14.mEnableTouchScroll
            if (r2 != 0) goto L_0x0046
            return r3
        L_0x0046:
            float r2 = r14.mLastTouchY
            float r2 = r1 - r2
            r14.computeScrollY(r2, r3)
            android.view.VelocityTracker r2 = r14.mVelocityTracker
            r2.addMovement(r15)
            goto L_0x00cb
        L_0x0054:
            r14.mIsUserTouchedDown = r3
            int r2 = r14.mCurStatus
            if (r2 != r4) goto L_0x008b
            r14.mCurStatus = r5
            android.view.VelocityTracker r2 = r14.mVelocityTracker
            r3 = 1000(0x3e8, float:1.401E-42)
            r2.computeCurrentVelocity(r3)
            android.widget.Scroller r2 = r14.mScroller
            float r3 = android.view.ViewConfiguration.getScrollFriction()
            r2.setFriction(r3)
            android.widget.Scroller r5 = r14.mScroller
            r6 = 0
            float r2 = r14.mScrollerY
            float r2 = -r2
            int r7 = (int) r2
            r8 = 0
            r2 = 20000(0x4e20, float:2.8026E-41)
            int r2 = r14.getVelocityY(r2)
            int r9 = -r2
            r10 = 0
            r11 = 0
            int r12 = r14.getMinScrollY()
            int r13 = r14.getMaxScrollY()
            r5.fling(r6, r7, r8, r9, r10, r11, r12, r13)
            r14.invalidate()
        L_0x008b:
            android.view.VelocityTracker r2 = r14.mVelocityTracker
            r2.clear()
            boolean r2 = r14.mInvalidClick
            if (r2 != 0) goto L_0x00cb
            int r15 = r15.getAction()
            if (r15 != r4) goto L_0x00cb
            r14.performClick()
            goto L_0x00cb
        L_0x009e:
            r14.mCurStatus = r4
            r14.mIsUserTouchedDown = r4
            r14.stopAutoScrollAnim()
            android.widget.Scroller r2 = r14.mScroller
            boolean r2 = r2.isFinished()
            if (r2 != 0) goto L_0x00b2
            android.widget.Scroller r2 = r14.mScroller
            r2.forceFinished(r4)
        L_0x00b2:
            android.view.VelocityTracker r2 = r14.mVelocityTracker
            if (r2 != 0) goto L_0x00bd
            android.view.VelocityTracker r2 = android.view.VelocityTracker.obtain()
            r14.mVelocityTracker = r2
            goto L_0x00c0
        L_0x00bd:
            r2.clear()
        L_0x00c0:
            android.view.VelocityTracker r2 = r14.mVelocityTracker
            r2.addMovement(r15)
            r14.mTouchDownX = r0
            r14.mTouchDownY = r1
            r14.mInvalidClick = r3
        L_0x00cb:
            r14.mLastTouchX = r0
            r14.mLastTouchY = r1
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.screenshot.LongScreenshotScrollView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mBitmapWidth != 0 && this.mBitmapArrayDrawable != null) {
            canvas.save();
            if (this.mCurStatus == 2) {
                if (!this.mScroller.computeScrollOffset()) {
                    this.mCurStatus = 0;
                }
                this.mScrollerY = (float) (-this.mScroller.getCurrY());
            }
            float f = this.mScrollerY;
            if (this.mMaxShowingHeight > 0) {
                float heightInner = (float) (getHeightInner() - this.mMaxShowingHeight);
                if (heightInner > f) {
                    f = heightInner;
                }
            }
            RectF rectF = new RectF((float) getPaddingLeft(), f, (float) (getPaddingLeft() + getWidthInner()), (float) getHeightInner());
            if (this.mEnableOutline) {
                canvas.drawRect(rectF, this.mShadowPaint);
            }
            canvas.clipRect(rectF);
            canvas.translate((float) getPaddingLeft(), this.mScrollerY);
            float widthInner = ((float) getWidthInner()) / ((float) this.mBitmapWidth);
            canvas.scale(widthInner, widthInner);
            this.mBitmapArrayDrawable.draw(canvas);
            canvas.restore();
            if (this.mCurStatus == 2) {
                invalidate();
            }
        }
    }

    public void setMaxBitmapheight(int i) {
        this.mTotalBitmapHeight = i;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00b5, code lost:
        return r8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Bitmap getFinalBitmap() {
        /*
            r15 = this;
            int r0 = r15.getCurCropTotalHeight()
            com.motorola.systemui.screenshot.BitmapArrayDrawable r1 = r15.mBitmapArrayDrawable
            java.util.List r1 = r1.getBitmapLists()
            com.motorola.systemui.screenshot.BitmapArrayDrawable r2 = r15.mBitmapArrayDrawable
            monitor-enter(r2)
            int r3 = r1.size()     // Catch:{ all -> 0x00b6 }
            r4 = 0
            if (r3 != 0) goto L_0x0016
            monitor-exit(r2)     // Catch:{ all -> 0x00b6 }
            return r4
        L_0x0016:
            int r5 = r15.mTotalBitmapHeight     // Catch:{ all -> 0x00b6 }
            r6 = 0
            r7 = 0
            if (r0 < r5) goto L_0x0048
            r5 = r4
            r8 = r5
            r0 = r7
        L_0x001f:
            if (r7 >= r3) goto L_0x00b4
            java.lang.Object r9 = r1.get(r7)     // Catch:{ all -> 0x00b6 }
            android.graphics.Bitmap r9 = (android.graphics.Bitmap) r9     // Catch:{ all -> 0x00b6 }
            if (r7 != 0) goto L_0x003c
            int r5 = r9.getWidth()     // Catch:{ all -> 0x00b6 }
            int r8 = r15.mTotalBitmapHeight     // Catch:{ all -> 0x00b6 }
            android.graphics.Bitmap$Config r10 = r9.getConfig()     // Catch:{ all -> 0x00b6 }
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r5, r8, r10)     // Catch:{ all -> 0x00b6 }
            android.graphics.Canvas r5 = new android.graphics.Canvas     // Catch:{ all -> 0x00b6 }
            r5.<init>(r8)     // Catch:{ all -> 0x00b6 }
        L_0x003c:
            float r10 = (float) r0     // Catch:{ all -> 0x00b6 }
            r5.drawBitmap(r9, r6, r10, r4)     // Catch:{ all -> 0x00b6 }
            int r9 = r9.getHeight()     // Catch:{ all -> 0x00b6 }
            int r0 = r0 + r9
            int r7 = r7 + 1
            goto L_0x001f
        L_0x0048:
            r9 = r0
            r8 = r4
            r10 = r8
            r5 = r7
            r11 = r5
        L_0x004d:
            if (r5 >= r3) goto L_0x00b4
            java.lang.Object r12 = r1.get(r5)     // Catch:{ all -> 0x00b6 }
            android.graphics.Bitmap r12 = (android.graphics.Bitmap) r12     // Catch:{ all -> 0x00b6 }
            int r13 = r12.getHeight()     // Catch:{ all -> 0x00b6 }
            if (r13 != 0) goto L_0x005c
            goto L_0x00a6
        L_0x005c:
            if (r5 != 0) goto L_0x0094
            int r8 = r12.getHeight()     // Catch:{ all -> 0x00b6 }
            int r9 = r9 - r8
            if (r9 > 0) goto L_0x007a
            boolean r15 = DEBUG     // Catch:{ all -> 0x00b6 }
            if (r15 == 0) goto L_0x0070
            java.lang.String r15 = "LongScreenshotScrollView"
            java.lang.String r0 = "getFinalBitmap only has first bitmap."
            android.util.Log.d(r15, r0)     // Catch:{ all -> 0x00b6 }
        L_0x0070:
            android.graphics.Bitmap$Config r15 = r12.getConfig()     // Catch:{ all -> 0x00b6 }
            android.graphics.Bitmap r15 = r12.copy(r15, r7)     // Catch:{ all -> 0x00b6 }
            monitor-exit(r2)     // Catch:{ all -> 0x00b6 }
            return r15
        L_0x007a:
            int r8 = r12.getWidth()     // Catch:{ all -> 0x00b6 }
            android.graphics.Bitmap$Config r10 = r12.getConfig()     // Catch:{ all -> 0x00b6 }
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r8, r0, r10)     // Catch:{ all -> 0x00b6 }
            android.graphics.Canvas r10 = new android.graphics.Canvas     // Catch:{ all -> 0x00b6 }
            r10.<init>(r8)     // Catch:{ all -> 0x00b6 }
            float r13 = (float) r11     // Catch:{ all -> 0x00b6 }
            r10.drawBitmap(r12, r6, r13, r4)     // Catch:{ all -> 0x00b6 }
            int r12 = r12.getHeight()     // Catch:{ all -> 0x00b6 }
            goto L_0x00a5
        L_0x0094:
            int r14 = r9 / r13
            if (r14 <= 0) goto L_0x00a9
            int r13 = r12.getHeight()     // Catch:{ all -> 0x00b6 }
            int r9 = r9 - r13
            float r13 = (float) r11     // Catch:{ all -> 0x00b6 }
            r10.drawBitmap(r12, r6, r13, r4)     // Catch:{ all -> 0x00b6 }
            int r12 = r12.getHeight()     // Catch:{ all -> 0x00b6 }
        L_0x00a5:
            int r11 = r11 + r12
        L_0x00a6:
            int r5 = r5 + 1
            goto L_0x004d
        L_0x00a9:
            int r9 = r9 % r13
            android.graphics.Bitmap r15 = r15.cropBitmap(r12, r7, r9, r7)     // Catch:{ all -> 0x00b6 }
            if (r15 == 0) goto L_0x00b4
            float r0 = (float) r11     // Catch:{ all -> 0x00b6 }
            r10.drawBitmap(r15, r6, r0, r4)     // Catch:{ all -> 0x00b6 }
        L_0x00b4:
            monitor-exit(r2)     // Catch:{ all -> 0x00b6 }
            return r8
        L_0x00b6:
            r15 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00b6 }
            throw r15
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.screenshot.LongScreenshotScrollView.getFinalBitmap():android.graphics.Bitmap");
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (DEBUG) {
            Log.d("LongScreenshotScrollView", "onDetachedFromWindow release bitmaps");
        }
        releaseBitmaps();
    }

    public void releaseBitmaps() {
        synchronized (this.mBitmapArrayDrawable) {
            for (Bitmap next : this.mBitmapArrayDrawable.getBitmapLists()) {
                if (next != null && !next.isRecycled()) {
                    next.recycle();
                }
            }
            clearAllBitmaps();
        }
    }

    private Bitmap cropBitmap(Bitmap bitmap, int i, int i2, boolean z) {
        int[] iArr = null;
        if (bitmap == null || i >= i2 || i < 0 || i2 <= 0 || i2 > bitmap.getHeight()) {
            Log.i("LongScreenshotScrollView", "cropBitmap srcBitmap = " + bitmap + "; beginY = " + i + "; endY = " + i2 + "; return null");
            return null;
        }
        int width = bitmap.getWidth();
        int i3 = i2 - i;
        SoftReference<int[]> softReference = sPixelsCache;
        if (softReference != null) {
            iArr = softReference.get();
        }
        if (iArr == null || iArr.length != width * i3) {
            iArr = new int[(width * i3)];
            sPixelsCache = new SoftReference<>(iArr);
        }
        bitmap.getPixels(iArr, 0, width, 0, i, width, i3);
        return Bitmap.createBitmap(iArr, width, i3, Bitmap.Config.ARGB_8888);
    }

    public void enableOutlineProvider() {
        this.mEnableOutline = true;
    }

    public void setTouchScrollEnable(boolean z) {
        this.mEnableTouchScroll = z;
    }

    public void setMaxShowingHeight(int i) {
        this.mMaxShowingHeight = i;
    }

    public float getScrollerY() {
        return this.mScrollerY;
    }

    public void setOnTouchStatusListener(OnTouchStatusListener onTouchStatusListener) {
        this.mOnTouchStatusListener = onTouchStatusListener;
    }
}
