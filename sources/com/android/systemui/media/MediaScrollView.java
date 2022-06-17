package com.android.systemui.media;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import com.android.p011wm.shell.animation.PhysicsAnimatorKt;
import com.android.systemui.Gefingerpoken;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaScrollView.kt */
public final class MediaScrollView extends HorizontalScrollView {
    private float animationTargetX;
    private ViewGroup contentContainer;
    @Nullable
    private Gefingerpoken touchListener;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public MediaScrollView(@NotNull Context context) {
        this(context, (AttributeSet) null, 0, 6, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(context, "context");
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public MediaScrollView(@NotNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0, 4, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(context, "context");
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ MediaScrollView(Context context, AttributeSet attributeSet, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i2 & 2) != 0 ? null : attributeSet, (i2 & 4) != 0 ? 0 : i);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public MediaScrollView(@NotNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Intrinsics.checkNotNullParameter(context, "context");
    }

    @NotNull
    public final ViewGroup getContentContainer() {
        ViewGroup viewGroup = this.contentContainer;
        if (viewGroup != null) {
            return viewGroup;
        }
        Intrinsics.throwUninitializedPropertyAccessException("contentContainer");
        throw null;
    }

    public final void setTouchListener(@Nullable Gefingerpoken gefingerpoken) {
        this.touchListener = gefingerpoken;
    }

    public final void setAnimationTargetX(float f) {
        this.animationTargetX = f;
    }

    public final float getContentTranslation() {
        if (PhysicsAnimatorKt.getPhysicsAnimator(getContentContainer()).isRunning()) {
            return this.animationTargetX;
        }
        return getContentContainer().getTranslationX();
    }

    private final int transformScrollX(int i) {
        return isLayoutRtl() ? (getContentContainer().getWidth() - getWidth()) - i : i;
    }

    public final int getRelativeScrollX() {
        return transformScrollX(getScrollX());
    }

    public final void setRelativeScrollX(int i) {
        setScrollX(transformScrollX(i));
    }

    public void scrollTo(int i, int i2) {
        int i3 = this.mScrollX;
        if (i3 != i || this.mScrollY != i2) {
            int i4 = this.mScrollY;
            this.mScrollX = i;
            this.mScrollY = i2;
            invalidateParentCaches();
            onScrollChanged(this.mScrollX, this.mScrollY, i3, i4);
            if (!awakenScrollBars()) {
                postInvalidateOnAnimation();
            }
        }
    }

    public boolean onInterceptTouchEvent(@Nullable MotionEvent motionEvent) {
        boolean z;
        Gefingerpoken gefingerpoken = this.touchListener;
        if (gefingerpoken == null) {
            z = false;
        } else {
            z = gefingerpoken.onInterceptTouchEvent(motionEvent);
        }
        if (super.onInterceptTouchEvent(motionEvent) || z) {
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(@Nullable MotionEvent motionEvent) {
        boolean z;
        Gefingerpoken gefingerpoken = this.touchListener;
        if (gefingerpoken == null) {
            z = false;
        } else {
            z = gefingerpoken.onTouchEvent(motionEvent);
        }
        if (super.onTouchEvent(motionEvent) || z) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View childAt = getChildAt(0);
        Objects.requireNonNull(childAt, "null cannot be cast to non-null type android.view.ViewGroup");
        this.contentContainer = (ViewGroup) childAt;
    }

    /* access modifiers changed from: protected */
    public boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        if (!(getContentTranslation() == 0.0f)) {
            return false;
        }
        return super.overScrollBy(i, i2, i3, i4, i5, i6, i7, i8, z);
    }

    public final void cancelCurrentScroll() {
        long uptimeMillis = SystemClock.uptimeMillis();
        MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
        obtain.setSource(4098);
        super.onTouchEvent(obtain);
        obtain.recycle();
    }
}
