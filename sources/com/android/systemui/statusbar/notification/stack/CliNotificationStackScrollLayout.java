package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import com.android.settingslib.Utils;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public class CliNotificationStackScrollLayout extends ScrollView {
    private final long DELAY_MILLIS = 100;
    private final float PULL_DOWN_DY_HIDE = 200.0f;
    private final float PULL_DOWN_DY_START = 10.0f;
    private float mDownX;
    private float mDownY;
    private boolean mInPullDownArea = false;
    /* access modifiers changed from: private */
    public boolean mIsDown = false;
    private boolean mIsTopDown = false;
    /* access modifiers changed from: private */
    public long mLastScrollUpdate = -1;
    private int mOutlineRadius;
    private int mRowWidth;
    private Runnable mScrollerTask = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() - CliNotificationStackScrollLayout.this.mLastScrollUpdate <= 100 || CliNotificationStackScrollLayout.this.mIsDown) {
                CliNotificationStackScrollLayout.this.postDelayed(this, 100);
                return;
            }
            long unused = CliNotificationStackScrollLayout.this.mLastScrollUpdate = -1;
            CliNotificationStackScrollLayout cliNotificationStackScrollLayout = CliNotificationStackScrollLayout.this;
            cliNotificationStackScrollLayout.onScrollEnd(cliNotificationStackScrollLayout.getScrollX(), CliNotificationStackScrollLayout.this.getScrollY());
        }
    };
    private CliNotificationStackLayout mStackLayout;

    private void onScrollStart() {
    }

    public CliNotificationStackScrollLayout(Context context) {
        super(context);
    }

    public CliNotificationStackScrollLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CliNotificationStackScrollLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CliNotificationStackScrollLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int currentCardScrollY = this.mStackLayout.getCurrentCardScrollY();
        if (currentCardScrollY != 0) {
            setScrollY(currentCardScrollY);
            if (getScrollY() == currentCardScrollY) {
                this.mStackLayout.clearSelectedView();
            }
        }
    }

    public void scrollToDescendant(View view) {
        if (view instanceof ExpandableNotificationRow) {
            super.scrollToDescendant(view);
        }
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        setVerticalScrollBarEnabled(false);
        setWillNotDraw(false);
        this.mOutlineRadius = getResources().getDimensionPixelSize(Utils.getThemeAttr(this.mContext, 16844145));
        this.mRowWidth = getResources().getDimensionPixelSize(R$dimen.cli_row_width);
        this.mStackLayout = (CliNotificationStackLayout) getChildAt(0);
    }

    private boolean inMyArea(float f, float f2) {
        return f > this.mStackLayout.getX() && f < this.mStackLayout.getX() + ((float) this.mStackLayout.getWidth()) && f2 < this.mStackLayout.getY() + ((float) this.mStackLayout.getHeight());
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        if (this.mStackLayout.isMoving()) {
            this.mIsDown = false;
            return false;
        }
        if (motionEvent.getActionMasked() == 0) {
            this.mDownX = rawX;
            this.mDownY = rawY;
            this.mIsDown = true;
            if (getScrollY() == 0) {
                this.mIsTopDown = true;
            } else {
                this.mIsTopDown = false;
            }
            if (this.mStackLayout != null && inMyArea(rawX, rawY) && this.mStackLayout.getVisibility() == 0) {
                this.mInPullDownArea = true;
            }
        } else if (motionEvent.getActionMasked() == 2) {
            if (this.mInPullDownArea) {
                float f = rawY - this.mDownY;
                if (!this.mStackLayout.isMoving() && f > 10.0f && getScrollY() == 0) {
                    return true;
                }
            } else if (this.mStackLayout.getTranslationY() == ((float) getHeight())) {
                return true;
            }
        } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
            this.mIsDown = false;
            this.mInPullDownArea = false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float f;
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        boolean z = false;
        if (motionEvent.getActionMasked() == 0) {
            if (this.mStackLayout != null && (!inMyArea(rawX, rawY) || this.mStackLayout.getVisibility() != 0)) {
                return false;
            }
        } else if (motionEvent.getActionMasked() == 2) {
            if (this.mStackLayout.getLauncherDownY() == 0.0f) {
                f = rawY - this.mDownY;
            } else {
                f = this.mStackLayout.getLauncherDownY() - rawY;
            }
            if (this.mDownY != 0.0f && f > 0.0f && (this.mIsTopDown || this.mStackLayout.getLauncherDownY() != 0.0f)) {
                CliNotificationStackLayout cliNotificationStackLayout = this.mStackLayout;
                if (cliNotificationStackLayout.getLauncherDownY() != 0.0f) {
                    f = ((float) getHeight()) - f;
                }
                cliNotificationStackLayout.setTranslationY(f);
                this.mStackLayout.updateStackBackground(true);
                return true;
            }
        } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
            this.mIsDown = false;
            this.mInPullDownArea = false;
            float translationY = this.mStackLayout.getTranslationY();
            if (translationY > 0.0f) {
                if (this.mStackLayout.getLauncherDownY() != 0.0f ? this.mStackLayout.getLauncherDownY() - rawY > 200.0f : translationY < 200.0f) {
                    z = true;
                }
                this.mStackLayout.startTransitionYFromParent(translationY, z);
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        CliNotificationStackLayout cliNotificationStackLayout = this.mStackLayout;
        if (cliNotificationStackLayout != null && cliNotificationStackLayout.isStatusBarShade()) {
            if (this.mLastScrollUpdate == -1) {
                onScrollStart();
                postDelayed(this.mScrollerTask, 100);
            }
            this.mLastScrollUpdate = System.currentTimeMillis();
        }
    }

    /* access modifiers changed from: private */
    public void onScrollEnd(int i, int i2) {
        scrollToCard(i, i2);
    }

    private void scrollToCard(int i, int i2) {
        CliNotificationStackLayout cliNotificationStackLayout = this.mStackLayout;
        if (cliNotificationStackLayout != null) {
            int childCount = cliNotificationStackLayout.getChildCount();
            int i3 = 0;
            int stackPaddingTop = this.mStackLayout.getStackPaddingTop();
            while (true) {
                childCount--;
                if (childCount >= this.mStackLayout.getIndexRow()) {
                    ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) this.mStackLayout.getChildAt(childCount);
                    if (expandableNotificationRow.getVisibility() == 0) {
                        int translationY = (int) expandableNotificationRow.getTranslationY();
                        int i4 = translationY - stackPaddingTop;
                        if (i4 != i2) {
                            if (translationY >= i2) {
                                if (childCount == this.mStackLayout.getIndexRow()) {
                                    smoothScrollTo(i, i4);
                                }
                                i3 = translationY;
                            } else if (i3 != 0 && i3 + stackPaddingTop < i2 + getHeight()) {
                                smoothScrollTo(i, i3 - stackPaddingTop);
                                return;
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }
}
