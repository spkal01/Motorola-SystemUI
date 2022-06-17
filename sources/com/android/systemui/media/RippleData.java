package com.android.systemui.media;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: LightSourceDrawable.kt */
final class RippleData {
    private float alpha;
    private float highlight;
    private float maxSize;
    private float minSize;
    private float progress;

    /* renamed from: x */
    private float f104x;

    /* renamed from: y */
    private float f105y;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RippleData)) {
            return false;
        }
        RippleData rippleData = (RippleData) obj;
        return Intrinsics.areEqual((Object) Float.valueOf(this.f104x), (Object) Float.valueOf(rippleData.f104x)) && Intrinsics.areEqual((Object) Float.valueOf(this.f105y), (Object) Float.valueOf(rippleData.f105y)) && Intrinsics.areEqual((Object) Float.valueOf(this.alpha), (Object) Float.valueOf(rippleData.alpha)) && Intrinsics.areEqual((Object) Float.valueOf(this.progress), (Object) Float.valueOf(rippleData.progress)) && Intrinsics.areEqual((Object) Float.valueOf(this.minSize), (Object) Float.valueOf(rippleData.minSize)) && Intrinsics.areEqual((Object) Float.valueOf(this.maxSize), (Object) Float.valueOf(rippleData.maxSize)) && Intrinsics.areEqual((Object) Float.valueOf(this.highlight), (Object) Float.valueOf(rippleData.highlight));
    }

    public int hashCode() {
        return (((((((((((Float.hashCode(this.f104x) * 31) + Float.hashCode(this.f105y)) * 31) + Float.hashCode(this.alpha)) * 31) + Float.hashCode(this.progress)) * 31) + Float.hashCode(this.minSize)) * 31) + Float.hashCode(this.maxSize)) * 31) + Float.hashCode(this.highlight);
    }

    @NotNull
    public String toString() {
        return "RippleData(x=" + this.f104x + ", y=" + this.f105y + ", alpha=" + this.alpha + ", progress=" + this.progress + ", minSize=" + this.minSize + ", maxSize=" + this.maxSize + ", highlight=" + this.highlight + ')';
    }

    public RippleData(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        this.f104x = f;
        this.f105y = f2;
        this.alpha = f3;
        this.progress = f4;
        this.minSize = f5;
        this.maxSize = f6;
        this.highlight = f7;
    }

    public final float getX() {
        return this.f104x;
    }

    public final void setX(float f) {
        this.f104x = f;
    }

    public final float getY() {
        return this.f105y;
    }

    public final void setY(float f) {
        this.f105y = f;
    }

    public final float getAlpha() {
        return this.alpha;
    }

    public final void setAlpha(float f) {
        this.alpha = f;
    }

    public final float getProgress() {
        return this.progress;
    }

    public final void setProgress(float f) {
        this.progress = f;
    }

    public final float getMinSize() {
        return this.minSize;
    }

    public final void setMinSize(float f) {
        this.minSize = f;
    }

    public final float getMaxSize() {
        return this.maxSize;
    }

    public final void setMaxSize(float f) {
        this.maxSize = f;
    }

    public final void setHighlight(float f) {
        this.highlight = f;
    }
}
