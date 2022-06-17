package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.customview.widget.ViewDragHelper;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.notification.row.CliHeadsUpView;

public class CliHorizontalSlideView extends FrameLayout {
    /* access modifiers changed from: private */
    public static int VELOCITY = 3000;
    private ViewDragHelper.Callback mCallback;
    /* access modifiers changed from: private */
    public int mDirection;
    /* access modifiers changed from: private */
    public CliHeadsUpView mDragView;
    /* access modifiers changed from: private */
    public boolean mIsDismissForIdle;
    /* access modifiers changed from: private */
    public int mNHeadsUpHorizonMargin;
    /* access modifiers changed from: private */
    public int mNHeadsUpVerticalMargin;
    /* access modifiers changed from: private */
    public onSlideOut mOnSlideOutListener;
    /* access modifiers changed from: private */
    public boolean mSlideEnabled;
    /* access modifiers changed from: private */
    public int mSlideWidth;
    /* access modifiers changed from: private */
    public ViewDragHelper mViewDragHelper;
    /* access modifiers changed from: private */
    public int mWidth;

    public interface onSlideOut {
        void slideToTop(CliHeadsUpView cliHeadsUpView);
    }

    public CliHorizontalSlideView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliHorizontalSlideView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mWidth = 0;
        this.mSlideWidth = 0;
        this.mSlideEnabled = true;
        this.mNHeadsUpHorizonMargin = 0;
        this.mNHeadsUpVerticalMargin = 0;
        this.mIsDismissForIdle = false;
        this.mDirection = 0;
        this.mCallback = new ViewDragHelper.Callback() {
            public boolean tryCaptureView(View view, int i) {
                int unused = CliHorizontalSlideView.this.mDirection = 0;
                if (CliHorizontalSlideView.this.mDragView == view) {
                    return true;
                }
                return false;
            }

            public int clampViewPositionVertical(View view, int i, int i2) {
                if (CliHorizontalSlideView.this.mDirection == 0) {
                    int unused = CliHorizontalSlideView.this.mDirection = 2;
                }
                if (CliHorizontalSlideView.this.mDirection != 2) {
                    return view.getTop();
                }
                return i > CliHorizontalSlideView.this.mNHeadsUpVerticalMargin ? CliHorizontalSlideView.this.mNHeadsUpVerticalMargin : i;
            }

            public int clampViewPositionHorizontal(View view, int i, int i2) {
                if (CliHorizontalSlideView.this.mDirection == 0) {
                    int unused = CliHorizontalSlideView.this.mDirection = 1;
                }
                return CliHorizontalSlideView.this.mDirection != 1 ? view.getLeft() : i;
            }

            public void onViewReleased(View view, float f, float f2) {
                if (view != CliHorizontalSlideView.this.mDragView) {
                    return;
                }
                if (CliHorizontalSlideView.this.mDirection == 1) {
                    if (CliHorizontalSlideView.this.mSlideEnabled) {
                        boolean unused = CliHorizontalSlideView.this.mIsDismissForIdle = false;
                        int left = CliHorizontalSlideView.this.mDragView.getLeft();
                        if (left > CliHorizontalSlideView.this.mSlideWidth || f > ((float) CliHorizontalSlideView.VELOCITY)) {
                            if (CliHorizontalSlideView.this.mViewDragHelper.smoothSlideViewTo(CliHorizontalSlideView.this.mDragView, CliHorizontalSlideView.this.mWidth, CliHorizontalSlideView.this.mDragView.getTop())) {
                                CliHorizontalSlideView.this.invalidate();
                                boolean unused2 = CliHorizontalSlideView.this.mIsDismissForIdle = true;
                            }
                        } else if (left < (-CliHorizontalSlideView.this.mSlideWidth) || f < ((float) (-CliHorizontalSlideView.VELOCITY))) {
                            if (CliHorizontalSlideView.this.mViewDragHelper.smoothSlideViewTo(CliHorizontalSlideView.this.mDragView, -CliHorizontalSlideView.this.mDragView.getWidth(), CliHorizontalSlideView.this.mDragView.getTop())) {
                                CliHorizontalSlideView.this.invalidate();
                                boolean unused3 = CliHorizontalSlideView.this.mIsDismissForIdle = true;
                            }
                        } else if (CliHorizontalSlideView.this.mViewDragHelper.smoothSlideViewTo(CliHorizontalSlideView.this.mDragView, CliHorizontalSlideView.this.mNHeadsUpHorizonMargin, CliHorizontalSlideView.this.mDragView.getTop())) {
                            CliHorizontalSlideView.this.invalidate();
                        }
                    } else if (CliHorizontalSlideView.this.mViewDragHelper.smoothSlideViewTo(CliHorizontalSlideView.this.mDragView, CliHorizontalSlideView.this.mNHeadsUpHorizonMargin, CliHorizontalSlideView.this.mDragView.getTop())) {
                        CliHorizontalSlideView.this.invalidate();
                    }
                } else if (CliHorizontalSlideView.this.mDirection == 2) {
                    boolean unused4 = CliHorizontalSlideView.this.mIsDismissForIdle = false;
                    if (CliHorizontalSlideView.this.mDragView.getTop() < 0) {
                        if (CliHorizontalSlideView.this.mViewDragHelper.smoothSlideViewTo(CliHorizontalSlideView.this.mDragView, CliHorizontalSlideView.this.mDragView.getLeft(), -CliHorizontalSlideView.this.mDragView.getHeight())) {
                            CliHorizontalSlideView.this.invalidate();
                            boolean unused5 = CliHorizontalSlideView.this.mIsDismissForIdle = true;
                        }
                    } else if (CliHorizontalSlideView.this.mViewDragHelper.smoothSlideViewTo(CliHorizontalSlideView.this.mDragView, CliHorizontalSlideView.this.mDragView.getLeft(), CliHorizontalSlideView.this.mNHeadsUpVerticalMargin)) {
                        CliHorizontalSlideView.this.invalidate();
                    }
                }
            }

            public void onViewDragStateChanged(int i) {
                if (i != 0) {
                    return;
                }
                if (CliHorizontalSlideView.this.mDirection == 1) {
                    if (CliHorizontalSlideView.this.mIsDismissForIdle) {
                        CliHorizontalSlideView.this.mDragView.dismissWithSwipe();
                    }
                } else if (CliHorizontalSlideView.this.mDirection == 2 && CliHorizontalSlideView.this.mIsDismissForIdle && CliHorizontalSlideView.this.mOnSlideOutListener != null) {
                    CliHorizontalSlideView.this.mOnSlideOutListener.slideToTop(CliHorizontalSlideView.this.mDragView);
                }
            }
        };
        init();
    }

    private void init() {
        this.mViewDragHelper = ViewDragHelper.create(this, this.mCallback);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mNHeadsUpHorizonMargin = getResources().getDimensionPixelSize(R$dimen.cli_headup_padding_horizon);
        this.mNHeadsUpVerticalMargin = getResources().getDimensionPixelSize(R$dimen.cli_headup_padding_vertical);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        this.mWidth = size;
        this.mSlideWidth = size / 2;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        this.mViewDragHelper.shouldInterceptTouchEvent(motionEvent);
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mViewDragHelper.processTouchEvent(motionEvent);
        if (this.mDragView == null) {
            return true;
        }
        motionEvent.offsetLocation((float) (-this.mNHeadsUpHorizonMargin), (float) (-this.mNHeadsUpVerticalMargin));
        this.mDragView.dispatchTouchEvent(motionEvent);
        return true;
    }

    public void addView(View view, ViewGroup.LayoutParams layoutParams) {
        if (view instanceof CliHeadsUpView) {
            super.addView(view, layoutParams);
            this.mDragView = (CliHeadsUpView) view;
            return;
        }
        throw new IllegalArgumentException("Not allow to add a child that is not CliHeadsUp");
    }

    public void setOnDoubleClick(CliHeadsUpView.OnDoubleClickListener onDoubleClickListener) {
        CliHeadsUpView cliHeadsUpView = this.mDragView;
        if (cliHeadsUpView != null) {
            cliHeadsUpView.setOnDoubleClick(onDoubleClickListener);
        }
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public void enableSlide(boolean z) {
        this.mSlideEnabled = z;
    }

    public void setOnSlideOutListener(onSlideOut onslideout) {
        this.mOnSlideOutListener = onslideout;
    }
}
