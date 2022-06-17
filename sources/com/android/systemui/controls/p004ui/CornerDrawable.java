package com.android.systemui.controls.p004ui;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.CornerDrawable */
/* compiled from: CornerDrawable.kt */
public final class CornerDrawable extends DrawableWrapper {
    private final float cornerRadius;
    @NotNull
    private final Path path = new Path();
    @NotNull
    private final Drawable wrapped;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CornerDrawable(@NotNull Drawable drawable, float f) {
        super(drawable);
        Intrinsics.checkNotNullParameter(drawable, "wrapped");
        this.wrapped = drawable;
        this.cornerRadius = f;
        updatePath(new RectF(getBounds()));
    }

    public void draw(@NotNull Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        canvas.clipPath(this.path);
        super.draw(canvas);
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        updatePath(new RectF((float) i, (float) i2, (float) i3, (float) i4));
        super.setBounds(i, i2, i3, i4);
    }

    public void setBounds(@NotNull Rect rect) {
        Intrinsics.checkNotNullParameter(rect, "r");
        updatePath(new RectF(rect));
        super.setBounds(rect);
    }

    private final void updatePath(RectF rectF) {
        this.path.reset();
        Path path2 = this.path;
        float f = this.cornerRadius;
        path2.addRoundRect(rectF, f, f, Path.Direction.CW);
    }
}
