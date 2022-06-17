package com.android.systemui.util.animation;

import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: TransitionLayout.kt */
public final class WidgetState {
    private float alpha;
    private boolean gone;
    private int height;
    private int measureHeight;
    private int measureWidth;
    private float scale;
    private int width;

    /* renamed from: x */
    private float f140x;

    /* renamed from: y */
    private float f141y;

    public WidgetState() {
        this(0.0f, 0.0f, 0, 0, 0, 0, 0.0f, 0.0f, false, 511, (DefaultConstructorMarker) null);
    }

    public static /* synthetic */ WidgetState copy$default(WidgetState widgetState, float f, float f2, int i, int i2, int i3, int i4, float f3, float f4, boolean z, int i5, Object obj) {
        WidgetState widgetState2 = widgetState;
        int i6 = i5;
        return widgetState.copy((i6 & 1) != 0 ? widgetState2.f140x : f, (i6 & 2) != 0 ? widgetState2.f141y : f2, (i6 & 4) != 0 ? widgetState2.width : i, (i6 & 8) != 0 ? widgetState2.height : i2, (i6 & 16) != 0 ? widgetState2.measureWidth : i3, (i6 & 32) != 0 ? widgetState2.measureHeight : i4, (i6 & 64) != 0 ? widgetState2.alpha : f3, (i6 & 128) != 0 ? widgetState2.scale : f4, (i6 & 256) != 0 ? widgetState2.gone : z);
    }

    @NotNull
    public final WidgetState copy(float f, float f2, int i, int i2, int i3, int i4, float f3, float f4, boolean z) {
        return new WidgetState(f, f2, i, i2, i3, i4, f3, f4, z);
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WidgetState)) {
            return false;
        }
        WidgetState widgetState = (WidgetState) obj;
        return Intrinsics.areEqual((Object) Float.valueOf(this.f140x), (Object) Float.valueOf(widgetState.f140x)) && Intrinsics.areEqual((Object) Float.valueOf(this.f141y), (Object) Float.valueOf(widgetState.f141y)) && this.width == widgetState.width && this.height == widgetState.height && this.measureWidth == widgetState.measureWidth && this.measureHeight == widgetState.measureHeight && Intrinsics.areEqual((Object) Float.valueOf(this.alpha), (Object) Float.valueOf(widgetState.alpha)) && Intrinsics.areEqual((Object) Float.valueOf(this.scale), (Object) Float.valueOf(widgetState.scale)) && this.gone == widgetState.gone;
    }

    public int hashCode() {
        int hashCode = ((((((((((((((Float.hashCode(this.f140x) * 31) + Float.hashCode(this.f141y)) * 31) + Integer.hashCode(this.width)) * 31) + Integer.hashCode(this.height)) * 31) + Integer.hashCode(this.measureWidth)) * 31) + Integer.hashCode(this.measureHeight)) * 31) + Float.hashCode(this.alpha)) * 31) + Float.hashCode(this.scale)) * 31;
        boolean z = this.gone;
        if (z) {
            z = true;
        }
        return hashCode + (z ? 1 : 0);
    }

    @NotNull
    public String toString() {
        return "WidgetState(x=" + this.f140x + ", y=" + this.f141y + ", width=" + this.width + ", height=" + this.height + ", measureWidth=" + this.measureWidth + ", measureHeight=" + this.measureHeight + ", alpha=" + this.alpha + ", scale=" + this.scale + ", gone=" + this.gone + ')';
    }

    public WidgetState(float f, float f2, int i, int i2, int i3, int i4, float f3, float f4, boolean z) {
        this.f140x = f;
        this.f141y = f2;
        this.width = i;
        this.height = i2;
        this.measureWidth = i3;
        this.measureHeight = i4;
        this.alpha = f3;
        this.scale = f4;
        this.gone = z;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ WidgetState(float f, float f2, int i, int i2, int i3, int i4, float f3, float f4, boolean z, int i5, DefaultConstructorMarker defaultConstructorMarker) {
        this((i5 & 1) != 0 ? 0.0f : f, (i5 & 2) != 0 ? 0.0f : f2, (i5 & 4) != 0 ? 0 : i, (i5 & 8) != 0 ? 0 : i2, (i5 & 16) != 0 ? 0 : i3, (i5 & 32) != 0 ? 0 : i4, (i5 & 64) != 0 ? 1.0f : f3, (i5 & 128) != 0 ? 1.0f : f4, (i5 & 256) != 0 ? false : z);
    }

    public final float getX() {
        return this.f140x;
    }

    public final void setX(float f) {
        this.f140x = f;
    }

    public final float getY() {
        return this.f141y;
    }

    public final void setY(float f) {
        this.f141y = f;
    }

    public final int getWidth() {
        return this.width;
    }

    public final void setWidth(int i) {
        this.width = i;
    }

    public final int getHeight() {
        return this.height;
    }

    public final void setHeight(int i) {
        this.height = i;
    }

    public final int getMeasureWidth() {
        return this.measureWidth;
    }

    public final void setMeasureWidth(int i) {
        this.measureWidth = i;
    }

    public final int getMeasureHeight() {
        return this.measureHeight;
    }

    public final void setMeasureHeight(int i) {
        this.measureHeight = i;
    }

    public final float getAlpha() {
        return this.alpha;
    }

    public final void setAlpha(float f) {
        this.alpha = f;
    }

    public final float getScale() {
        return this.scale;
    }

    public final void setScale(float f) {
        this.scale = f;
    }

    public final boolean getGone() {
        return this.gone;
    }

    public final void setGone(boolean z) {
        this.gone = z;
    }

    public final void initFromLayout(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "view");
        boolean z = true;
        boolean z2 = view.getVisibility() == 8;
        this.gone = z2;
        if (z2) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type androidx.constraintlayout.widget.ConstraintLayout.LayoutParams");
            ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) layoutParams;
            this.f140x = (float) layoutParams2.getConstraintWidget().getLeft();
            this.f141y = (float) layoutParams2.getConstraintWidget().getTop();
            this.width = layoutParams2.getConstraintWidget().getWidth();
            int height2 = layoutParams2.getConstraintWidget().getHeight();
            this.height = height2;
            this.measureHeight = height2;
            this.measureWidth = this.width;
            this.alpha = 0.0f;
            this.scale = 0.0f;
            return;
        }
        this.f140x = (float) view.getLeft();
        this.f141y = (float) view.getTop();
        this.width = view.getWidth();
        int height3 = view.getHeight();
        this.height = height3;
        this.measureWidth = this.width;
        this.measureHeight = height3;
        if (view.getVisibility() != 8) {
            z = false;
        }
        this.gone = z;
        this.alpha = view.getAlpha();
        this.scale = 1.0f;
    }
}
