package com.android.keyguard;

import android.graphics.fonts.Font;
import android.graphics.fonts.FontVariationAxis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: FontInterpolator.kt */
public final class FontInterpolator {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private static final FontVariationAxis[] EMPTY_AXES = new FontVariationAxis[0];
    @NotNull
    private final HashMap<InterpKey, Font> interpCache = new HashMap<>();
    @NotNull
    private final InterpKey tmpInterpKey = new InterpKey((Font) null, (Font) null, 0.0f);
    @NotNull
    private final VarFontKey tmpVarFontKey = new VarFontKey(0, 0, new ArrayList());
    @NotNull
    private final HashMap<VarFontKey, Font> verFontCache = new HashMap<>();

    /* compiled from: FontInterpolator.kt */
    private static final class InterpKey {
        @Nullable

        /* renamed from: l */
        private Font f50l;
        private float progress;
        @Nullable

        /* renamed from: r */
        private Font f51r;

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof InterpKey)) {
                return false;
            }
            InterpKey interpKey = (InterpKey) obj;
            return Intrinsics.areEqual((Object) this.f50l, (Object) interpKey.f50l) && Intrinsics.areEqual((Object) this.f51r, (Object) interpKey.f51r) && Intrinsics.areEqual((Object) Float.valueOf(this.progress), (Object) Float.valueOf(interpKey.progress));
        }

        public int hashCode() {
            Font font = this.f50l;
            int i = 0;
            int hashCode = (font == null ? 0 : font.hashCode()) * 31;
            Font font2 = this.f51r;
            if (font2 != null) {
                i = font2.hashCode();
            }
            return ((hashCode + i) * 31) + Float.hashCode(this.progress);
        }

        @NotNull
        public String toString() {
            return "InterpKey(l=" + this.f50l + ", r=" + this.f51r + ", progress=" + this.progress + ')';
        }

        public InterpKey(@Nullable Font font, @Nullable Font font2, float f) {
            this.f50l = font;
            this.f51r = font2;
            this.progress = f;
        }

        public final void set(@NotNull Font font, @NotNull Font font2, float f) {
            Intrinsics.checkNotNullParameter(font, "l");
            Intrinsics.checkNotNullParameter(font2, "r");
            this.f50l = font;
            this.f51r = font2;
            this.progress = f;
        }
    }

    /* compiled from: FontInterpolator.kt */
    private static final class VarFontKey {
        private int index;
        @NotNull
        private final List<FontVariationAxis> sortedAxes;
        private int sourceId;

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof VarFontKey)) {
                return false;
            }
            VarFontKey varFontKey = (VarFontKey) obj;
            return this.sourceId == varFontKey.sourceId && this.index == varFontKey.index && Intrinsics.areEqual((Object) this.sortedAxes, (Object) varFontKey.sortedAxes);
        }

        public int hashCode() {
            return (((Integer.hashCode(this.sourceId) * 31) + Integer.hashCode(this.index)) * 31) + this.sortedAxes.hashCode();
        }

        @NotNull
        public String toString() {
            return "VarFontKey(sourceId=" + this.sourceId + ", index=" + this.index + ", sortedAxes=" + this.sortedAxes + ')';
        }

        public VarFontKey(int i, int i2, @NotNull List<FontVariationAxis> list) {
            Intrinsics.checkNotNullParameter(list, "sortedAxes");
            this.sourceId = i;
            this.index = i2;
            this.sortedAxes = list;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public VarFontKey(@org.jetbrains.annotations.NotNull android.graphics.fonts.Font r4, @org.jetbrains.annotations.NotNull java.util.List<android.graphics.fonts.FontVariationAxis> r5) {
            /*
                r3 = this;
                java.lang.String r0 = "font"
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
                java.lang.String r0 = "axes"
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r5, r0)
                int r0 = r4.getSourceIdentifier()
                int r4 = r4.getTtcIndex()
                java.util.List r5 = kotlin.collections.CollectionsKt___CollectionsKt.toMutableList(r5)
                int r1 = r5.size()
                r2 = 1
                if (r1 <= r2) goto L_0x0025
                com.android.keyguard.FontInterpolator$VarFontKey$_init_$lambda-1$$inlined$sortBy$1 r1 = new com.android.keyguard.FontInterpolator$VarFontKey$_init_$lambda-1$$inlined$sortBy$1
                r1.<init>()
                kotlin.collections.CollectionsKt__MutableCollectionsJVMKt.sortWith(r5, r1)
            L_0x0025:
                kotlin.Unit r1 = kotlin.Unit.INSTANCE
                r3.<init>(r0, r4, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.FontInterpolator.VarFontKey.<init>(android.graphics.fonts.Font, java.util.List):void");
        }

        public final void set(@NotNull Font font, @NotNull List<FontVariationAxis> list) {
            Intrinsics.checkNotNullParameter(font, "font");
            Intrinsics.checkNotNullParameter(list, "axes");
            this.sourceId = font.getSourceIdentifier();
            this.index = font.getTtcIndex();
            this.sortedAxes.clear();
            this.sortedAxes.addAll(list);
            List<FontVariationAxis> list2 = this.sortedAxes;
            if (list2.size() > 1) {
                CollectionsKt__MutableCollectionsJVMKt.sortWith(list2, new FontInterpolator$VarFontKey$set$$inlined$sortBy$1());
            }
        }
    }

    @NotNull
    public final Font lerp(@NotNull Font font, @NotNull Font font2, float f) {
        Intrinsics.checkNotNullParameter(font, "start");
        Intrinsics.checkNotNullParameter(font2, "end");
        boolean z = true;
        if (f == 0.0f) {
            return font;
        }
        if (f == 1.0f) {
            return font2;
        }
        FontVariationAxis[] axes = font.getAxes();
        if (axes == null) {
            axes = EMPTY_AXES;
        }
        FontVariationAxis[] axes2 = font2.getAxes();
        if (axes2 == null) {
            axes2 = EMPTY_AXES;
        }
        if (axes.length == 0) {
            if (axes2.length != 0) {
                z = false;
            }
            if (z) {
                return font;
            }
        }
        this.tmpInterpKey.set(font, font2, f);
        Font font3 = this.interpCache.get(this.tmpInterpKey);
        if (font3 != null) {
            return font3;
        }
        List<FontVariationAxis> lerp = lerp(axes, axes2, (Function3<? super String, ? super Float, ? super Float, Float>) new FontInterpolator$lerp$newAxes$1(this, f));
        this.tmpVarFontKey.set(font, lerp);
        Font font4 = this.verFontCache.get(this.tmpVarFontKey);
        if (font4 != null) {
            this.interpCache.put(new InterpKey(font, font2, f), font4);
            return font4;
        }
        Font.Builder builder = new Font.Builder(font);
        Object[] array = lerp.toArray(new FontVariationAxis[0]);
        Objects.requireNonNull(array, "null cannot be cast to non-null type kotlin.Array<T>");
        Font build = builder.setFontVariationSettings((FontVariationAxis[]) array).build();
        HashMap<InterpKey, Font> hashMap = this.interpCache;
        InterpKey interpKey = new InterpKey(font, font2, f);
        Intrinsics.checkNotNullExpressionValue(build, "newFont");
        hashMap.put(interpKey, build);
        this.verFontCache.put(new VarFontKey(font, lerp), build);
        return build;
    }

    /* access modifiers changed from: private */
    public final float adjustWeight(float f) {
        return coerceInWithStep(f, 0.0f, 1000.0f, 10.0f);
    }

    /* access modifiers changed from: private */
    public final float adjustItalic(float f) {
        return coerceInWithStep(f, 0.0f, 1.0f, 0.1f);
    }

    private final float coerceInWithStep(float f, float f2, float f3, float f4) {
        return ((float) ((int) (RangesKt___RangesKt.coerceIn(f, f2, f3) / f4))) * f4;
    }

    /* compiled from: FontInterpolator.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final boolean canInterpolate(@NotNull Font font, @NotNull Font font2) {
            Intrinsics.checkNotNullParameter(font, "start");
            Intrinsics.checkNotNullParameter(font2, "end");
            return font.getTtcIndex() == font2.getTtcIndex() && font.getSourceIdentifier() == font2.getSourceIdentifier();
        }
    }

    private final List<FontVariationAxis> lerp(FontVariationAxis[] fontVariationAxisArr, FontVariationAxis[] fontVariationAxisArr2, Function3<? super String, ? super Float, ? super Float, Float> function3) {
        int i;
        FontVariationAxis fontVariationAxis;
        if (fontVariationAxisArr.length > 1) {
            ArraysKt___ArraysJvmKt.sortWith(fontVariationAxisArr, new FontInterpolator$lerp$$inlined$sortBy$1());
        }
        if (fontVariationAxisArr2.length > 1) {
            ArraysKt___ArraysJvmKt.sortWith(fontVariationAxisArr2, new FontInterpolator$lerp$$inlined$sortBy$2());
        }
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i2 >= fontVariationAxisArr.length && i3 >= fontVariationAxisArr2.length) {
                return arrayList;
            }
            String tag = i2 < fontVariationAxisArr.length ? fontVariationAxisArr[i2].getTag() : null;
            String tag2 = i3 < fontVariationAxisArr2.length ? fontVariationAxisArr2[i3].getTag() : null;
            int compareTo = tag == null ? 1 : tag2 == null ? -1 : tag.compareTo(tag2);
            if (compareTo == 0) {
                Intrinsics.checkNotNull(tag);
                int i4 = i3 + 1;
                fontVariationAxis = new FontVariationAxis(tag, function3.invoke(tag, Float.valueOf(fontVariationAxisArr[i2].getStyleValue()), Float.valueOf(fontVariationAxisArr2[i3].getStyleValue())).floatValue());
                i2++;
                i = i4;
            } else if (compareTo < 0) {
                Intrinsics.checkNotNull(tag);
                FontVariationAxis fontVariationAxis2 = new FontVariationAxis(tag, function3.invoke(tag, Float.valueOf(fontVariationAxisArr[i2].getStyleValue()), null).floatValue());
                i = i3;
                fontVariationAxis = fontVariationAxis2;
                i2++;
            } else {
                Intrinsics.checkNotNull(tag2);
                i = i3 + 1;
                fontVariationAxis = new FontVariationAxis(tag2, function3.invoke(tag2, null, Float.valueOf(fontVariationAxisArr2[i3].getStyleValue())).floatValue());
            }
            arrayList.add(fontVariationAxis);
            i3 = i;
        }
    }
}
