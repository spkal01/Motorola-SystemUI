package androidx.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class ContentLoadingProgressBar extends ProgressBar {
    private final Runnable mDelayedHide = new ContentLoadingProgressBar$$ExternalSyntheticLambda0(this);
    private final Runnable mDelayedShow = new ContentLoadingProgressBar$$ExternalSyntheticLambda1(this);
    boolean mDismissed = false;
    boolean mPostedHide = false;
    boolean mPostedShow = false;
    long mStartTime = -1;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mPostedHide = false;
        this.mStartTime = -1;
        setVisibility(8);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.mPostedShow = false;
        if (!this.mDismissed) {
            this.mStartTime = System.currentTimeMillis();
            setVisibility(0);
        }
    }

    public ContentLoadingProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks();
    }

    private void removeCallbacks() {
        removeCallbacks(this.mDelayedHide);
        removeCallbacks(this.mDelayedShow);
    }
}
