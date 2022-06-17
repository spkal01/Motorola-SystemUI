package com.android.p011wm.shell.animation;

import android.graphics.Rect;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.animation.FloatProperties$Companion$RECT_HEIGHT$1 */
/* compiled from: FloatProperties.kt */
public final class FloatProperties$Companion$RECT_HEIGHT$1 extends FloatPropertyCompat<Rect> {
    FloatProperties$Companion$RECT_HEIGHT$1() {
        super("RectHeight");
    }

    public float getValue(@NotNull Rect rect) {
        Intrinsics.checkNotNullParameter(rect, "rect");
        return (float) rect.height();
    }

    public void setValue(@NotNull Rect rect, float f) {
        Intrinsics.checkNotNullParameter(rect, "rect");
        rect.bottom = rect.top + ((int) f);
    }
}
