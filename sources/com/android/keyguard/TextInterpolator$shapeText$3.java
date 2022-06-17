package com.android.keyguard;

import android.graphics.text.PositionedGlyphs;
import android.text.TextPaint;
import android.text.TextShaper;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: TextInterpolator.kt */
final class TextInterpolator$shapeText$3 implements TextShaper.GlyphsConsumer {
    final /* synthetic */ List<PositionedGlyphs> $runs;

    TextInterpolator$shapeText$3(List<PositionedGlyphs> list) {
        this.$runs = list;
    }

    public final void accept(int i, int i2, PositionedGlyphs positionedGlyphs, TextPaint textPaint) {
        List<PositionedGlyphs> list = this.$runs;
        Intrinsics.checkNotNullExpressionValue(positionedGlyphs, "glyphs");
        list.add(positionedGlyphs);
    }
}
