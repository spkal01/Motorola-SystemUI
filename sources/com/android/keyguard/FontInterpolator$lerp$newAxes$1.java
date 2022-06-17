package com.android.keyguard;

import android.util.MathUtils;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: FontInterpolator.kt */
final class FontInterpolator$lerp$newAxes$1 extends Lambda implements Function3<String, Float, Float, Float> {
    final /* synthetic */ float $progress;
    final /* synthetic */ FontInterpolator this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    FontInterpolator$lerp$newAxes$1(FontInterpolator fontInterpolator, float f) {
        super(3);
        this.this$0 = fontInterpolator;
        this.$progress = f;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj, Object obj2, Object obj3) {
        return Float.valueOf(invoke((String) obj, (Float) obj2, (Float) obj3));
    }

    public final float invoke(@NotNull String str, @Nullable Float f, @Nullable Float f2) {
        float f3;
        float f4;
        Intrinsics.checkNotNullParameter(str, "tag");
        if (Intrinsics.areEqual((Object) str, (Object) "wght")) {
            FontInterpolator fontInterpolator = this.this$0;
            float f5 = 400.0f;
            if (f == null) {
                f4 = 400.0f;
            } else {
                f4 = f.floatValue();
            }
            if (f2 != null) {
                f5 = f2.floatValue();
            }
            return fontInterpolator.adjustWeight(MathUtils.lerp(f4, f5, this.$progress));
        } else if (Intrinsics.areEqual((Object) str, (Object) "ital")) {
            FontInterpolator fontInterpolator2 = this.this$0;
            float f6 = 0.0f;
            if (f == null) {
                f3 = 0.0f;
            } else {
                f3 = f.floatValue();
            }
            if (f2 != null) {
                f6 = f2.floatValue();
            }
            return fontInterpolator2.adjustItalic(MathUtils.lerp(f3, f6, this.$progress));
        } else {
            if ((f == null || f2 == null) ? false : true) {
                return MathUtils.lerp(f.floatValue(), f2.floatValue(), this.$progress);
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("Unable to interpolate due to unknown default axes value : ", str).toString());
        }
    }
}
