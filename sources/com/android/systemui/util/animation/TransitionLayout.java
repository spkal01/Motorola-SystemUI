package com.android.systemui.util.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: TransitionLayout.kt */
public final class TransitionLayout extends ConstraintLayout {
    @NotNull
    private final Rect boundsRect;
    @NotNull
    private TransitionViewState currentState;
    private int desiredMeasureHeight;
    private int desiredMeasureWidth;
    /* access modifiers changed from: private */
    public boolean isPreDrawApplicatorRegistered;
    private boolean measureAsConstraint;
    @NotNull
    private TransitionViewState measureState;
    @NotNull
    private final Set<Integer> originalGoneChildrenSet;
    @NotNull
    private final Map<Integer, Float> originalViewAlphas;
    @NotNull
    private final TransitionLayout$preDrawApplicator$1 preDrawApplicator;
    private int transitionVisibility;
    /* access modifiers changed from: private */
    public boolean updateScheduled;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public TransitionLayout(@NotNull Context context) {
        this(context, (AttributeSet) null, 0, 6, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(context, "context");
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public TransitionLayout(@NotNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0, 4, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(context, "context");
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ TransitionLayout(Context context, AttributeSet attributeSet, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i2 & 2) != 0 ? null : attributeSet, (i2 & 4) != 0 ? 0 : i);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TransitionLayout(@NotNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Intrinsics.checkNotNullParameter(context, "context");
        this.boundsRect = new Rect();
        this.originalGoneChildrenSet = new LinkedHashSet();
        this.originalViewAlphas = new LinkedHashMap();
        this.currentState = new TransitionViewState();
        this.measureState = new TransitionViewState();
        this.preDrawApplicator = new TransitionLayout$preDrawApplicator$1(this);
    }

    public final void setMeasureState(@NotNull TransitionViewState transitionViewState) {
        Intrinsics.checkNotNullParameter(transitionViewState, "value");
        int width = transitionViewState.getWidth();
        int height = transitionViewState.getHeight();
        if (width != this.desiredMeasureWidth || height != this.desiredMeasureHeight) {
            this.desiredMeasureWidth = width;
            this.desiredMeasureHeight = height;
            if (isInLayout()) {
                forceLayout();
            } else {
                requestLayout();
            }
        }
    }

    public void setTransitionVisibility(int i) {
        super.setTransitionVisibility(i);
        this.transitionVisibility = i;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 0) {
            int i = 0;
            while (true) {
                int i2 = i + 1;
                View childAt = getChildAt(i);
                if (childAt.getId() == -1) {
                    childAt.setId(i);
                }
                if (childAt.getVisibility() == 8) {
                    this.originalGoneChildrenSet.add(Integer.valueOf(childAt.getId()));
                }
                this.originalViewAlphas.put(Integer.valueOf(childAt.getId()), Float.valueOf(childAt.getAlpha()));
                if (i2 < childCount) {
                    i = i2;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.isPreDrawApplicatorRegistered) {
            getViewTreeObserver().removeOnPreDrawListener(this.preDrawApplicator);
            this.isPreDrawApplicatorRegistered = false;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x010a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void applyCurrentState() {
        /*
            r13 = this;
            int r0 = r13.getChildCount()
            com.android.systemui.util.animation.TransitionViewState r1 = r13.currentState
            android.graphics.PointF r1 = r1.getContentTranslation()
            float r1 = r1.x
            int r1 = (int) r1
            com.android.systemui.util.animation.TransitionViewState r2 = r13.currentState
            android.graphics.PointF r2 = r2.getContentTranslation()
            float r2 = r2.y
            int r2 = (int) r2
            if (r0 <= 0) goto L_0x0124
            r3 = 0
            r4 = r3
        L_0x001a:
            int r5 = r4 + 1
            android.view.View r4 = r13.getChildAt(r4)
            com.android.systemui.util.animation.TransitionViewState r6 = r13.currentState
            java.util.Map r6 = r6.getWidgetStates()
            int r7 = r4.getId()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.Object r6 = r6.get(r7)
            com.android.systemui.util.animation.WidgetState r6 = (com.android.systemui.util.animation.WidgetState) r6
            if (r6 != 0) goto L_0x0038
            goto L_0x011e
        L_0x0038:
            boolean r7 = r4 instanceof android.widget.TextView
            if (r7 == 0) goto L_0x006a
            r7 = r4
            android.widget.TextView r7 = (android.widget.TextView) r7
            android.text.Layout r8 = r7.getLayout()
            if (r8 == 0) goto L_0x006a
            int r8 = r6.getWidth()
            int r9 = r6.getMeasureWidth()
            if (r8 >= r9) goto L_0x006a
            android.text.Layout r7 = r7.getLayout()
            int r7 = r7.getParagraphDirection(r3)
            r8 = -1
            if (r7 != r8) goto L_0x0064
            int r7 = r6.getMeasureWidth()
            int r8 = r6.getWidth()
            int r7 = r7 - r8
            goto L_0x0065
        L_0x0064:
            r7 = r3
        L_0x0065:
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            goto L_0x006b
        L_0x006a:
            r7 = 0
        L_0x006b:
            int r8 = r4.getMeasuredWidth()
            int r9 = r6.getMeasureWidth()
            if (r8 != r9) goto L_0x007f
            int r8 = r4.getMeasuredHeight()
            int r9 = r6.getMeasureHeight()
            if (r8 == r9) goto L_0x009f
        L_0x007f:
            int r8 = r6.getMeasureWidth()
            r9 = 1073741824(0x40000000, float:2.0)
            int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r9)
            int r10 = r6.getMeasureHeight()
            int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r9)
            r4.measure(r8, r9)
            int r8 = r4.getMeasuredWidth()
            int r9 = r4.getMeasuredHeight()
            r4.layout(r3, r3, r8, r9)
        L_0x009f:
            if (r7 != 0) goto L_0x00a3
            r8 = r3
            goto L_0x00a7
        L_0x00a3:
            int r8 = r7.intValue()
        L_0x00a7:
            float r9 = r6.getX()
            int r9 = (int) r9
            int r9 = r9 + r1
            int r9 = r9 - r8
            float r10 = r6.getY()
            int r10 = (int) r10
            int r10 = r10 + r2
            r11 = 1
            if (r7 == 0) goto L_0x00b9
            r7 = r11
            goto L_0x00ba
        L_0x00b9:
            r7 = r3
        L_0x00ba:
            if (r7 == 0) goto L_0x00c1
            int r12 = r6.getMeasureWidth()
            goto L_0x00c5
        L_0x00c1:
            int r12 = r6.getWidth()
        L_0x00c5:
            if (r7 == 0) goto L_0x00cc
            int r7 = r6.getMeasureHeight()
            goto L_0x00d0
        L_0x00cc:
            int r7 = r6.getHeight()
        L_0x00d0:
            int r12 = r12 + r9
            int r7 = r7 + r10
            r4.setLeftTopRightBottom(r9, r10, r12, r7)
            float r7 = r6.getScale()
            r4.setScaleX(r7)
            float r7 = r6.getScale()
            r4.setScaleY(r7)
            android.graphics.Rect r7 = r4.getClipBounds()
            if (r7 != 0) goto L_0x00ee
            android.graphics.Rect r7 = new android.graphics.Rect
            r7.<init>()
        L_0x00ee:
            int r9 = r6.getWidth()
            int r9 = r9 + r8
            int r10 = r6.getHeight()
            r7.set(r8, r3, r9, r10)
            r4.setClipBounds(r7)
            float r7 = r6.getAlpha()
            com.android.systemui.statusbar.CrossFadeHelper.fadeIn(r4, r7)
            boolean r7 = r6.getGone()
            if (r7 != 0) goto L_0x011a
            float r6 = r6.getAlpha()
            r7 = 0
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 != 0) goto L_0x0114
            goto L_0x0115
        L_0x0114:
            r11 = r3
        L_0x0115:
            if (r11 == 0) goto L_0x0118
            goto L_0x011a
        L_0x0118:
            r6 = r3
            goto L_0x011b
        L_0x011a:
            r6 = 4
        L_0x011b:
            r4.setVisibility(r6)
        L_0x011e:
            if (r5 < r0) goto L_0x0121
            goto L_0x0124
        L_0x0121:
            r4 = r5
            goto L_0x001a
        L_0x0124:
            r13.updateBounds()
            com.android.systemui.util.animation.TransitionViewState r0 = r13.currentState
            android.graphics.PointF r0 = r0.getTranslation()
            float r0 = r0.x
            r13.setTranslationX(r0)
            com.android.systemui.util.animation.TransitionViewState r0 = r13.currentState
            android.graphics.PointF r0 = r0.getTranslation()
            float r0 = r0.y
            r13.setTranslationY(r0)
            com.android.systemui.util.animation.TransitionViewState r0 = r13.currentState
            float r0 = r0.getAlpha()
            com.android.systemui.statusbar.CrossFadeHelper.fadeIn(r13, r0)
            int r0 = r13.transitionVisibility
            if (r0 == 0) goto L_0x014d
            r13.setTransitionVisibility(r0)
        L_0x014d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.util.animation.TransitionLayout.applyCurrentState():void");
    }

    private final void applyCurrentStateOnPredraw() {
        if (!this.updateScheduled) {
            this.updateScheduled = true;
            if (!this.isPreDrawApplicatorRegistered) {
                getViewTreeObserver().addOnPreDrawListener(this.preDrawApplicator);
                this.isPreDrawApplicatorRegistered = true;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.measureAsConstraint) {
            super.onMeasure(i, i2);
            return;
        }
        int i3 = 0;
        int childCount = getChildCount();
        if (childCount > 0) {
            while (true) {
                int i4 = i3 + 1;
                View childAt = getChildAt(i3);
                WidgetState widgetState = this.currentState.getWidgetStates().get(Integer.valueOf(childAt.getId()));
                if (widgetState != null) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec(widgetState.getMeasureWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(widgetState.getMeasureHeight(), 1073741824));
                }
                if (i4 >= childCount) {
                    break;
                }
                i3 = i4;
            }
        }
        setMeasuredDimension(this.desiredMeasureWidth, this.desiredMeasureHeight);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.measureAsConstraint) {
            super.onLayout(z, getLeft(), getTop(), getRight(), getBottom());
            return;
        }
        int childCount = getChildCount();
        if (childCount > 0) {
            int i5 = 0;
            while (true) {
                int i6 = i5 + 1;
                View childAt = getChildAt(i5);
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
                if (i6 >= childCount) {
                    break;
                }
                i5 = i6;
            }
        }
        applyCurrentState();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(@Nullable Canvas canvas) {
        if (canvas != null) {
            canvas.save();
        }
        if (canvas != null) {
            canvas.clipRect(this.boundsRect);
        }
        super.dispatchDraw(canvas);
        if (canvas != null) {
            canvas.restore();
        }
    }

    private final void updateBounds() {
        int left = getLeft();
        int top = getTop();
        setLeftTopRightBottom(left, top, this.currentState.getWidth() + left, this.currentState.getHeight() + top);
        this.boundsRect.set(0, 0, getWidth(), getHeight());
    }

    @NotNull
    public final TransitionViewState calculateViewState(@NotNull MeasurementInput measurementInput, @NotNull ConstraintSet constraintSet, @Nullable TransitionViewState transitionViewState) {
        Intrinsics.checkNotNullParameter(measurementInput, "input");
        Intrinsics.checkNotNullParameter(constraintSet, "constraintSet");
        if (transitionViewState == null) {
            transitionViewState = new TransitionViewState();
        }
        applySetToFullLayout(constraintSet);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        this.measureAsConstraint = true;
        measure(measurementInput.getWidthMeasureSpec(), measurementInput.getHeightMeasureSpec());
        int left = getLeft();
        int top = getTop();
        layout(left, top, getMeasuredWidth() + left, getMeasuredHeight() + top);
        this.measureAsConstraint = false;
        transitionViewState.initFromLayout(this);
        ensureViewsNotGone();
        setMeasuredDimension(measuredWidth, measuredHeight);
        applyCurrentStateOnPredraw();
        return transitionViewState;
    }

    private final void applySetToFullLayout(ConstraintSet constraintSet) {
        int childCount = getChildCount();
        if (childCount > 0) {
            int i = 0;
            while (true) {
                int i2 = i + 1;
                View childAt = getChildAt(i);
                if (this.originalGoneChildrenSet.contains(Integer.valueOf(childAt.getId()))) {
                    childAt.setVisibility(8);
                }
                Float f = this.originalViewAlphas.get(Integer.valueOf(childAt.getId()));
                childAt.setAlpha(f == null ? 1.0f : f.floatValue());
                if (i2 >= childCount) {
                    break;
                }
                i = i2;
            }
        }
        constraintSet.applyTo(this);
    }

    private final void ensureViewsNotGone() {
        Boolean bool;
        int childCount = getChildCount();
        if (childCount > 0) {
            int i = 0;
            while (true) {
                int i2 = i + 1;
                View childAt = getChildAt(i);
                WidgetState widgetState = this.currentState.getWidgetStates().get(Integer.valueOf(childAt.getId()));
                if (widgetState == null) {
                    bool = null;
                } else {
                    bool = Boolean.valueOf(widgetState.getGone());
                }
                childAt.setVisibility(!Intrinsics.areEqual((Object) bool, (Object) Boolean.FALSE) ? 4 : 0);
                if (i2 < childCount) {
                    i = i2;
                } else {
                    return;
                }
            }
        }
    }

    public final void setState(@NotNull TransitionViewState transitionViewState) {
        Intrinsics.checkNotNullParameter(transitionViewState, "state");
        this.currentState = transitionViewState;
        applyCurrentState();
    }
}
