package com.android.systemui.monet;

import android.app.WallpaperColors;
import com.android.internal.graphics.cam.Cam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ColorScheme.kt */
public final class ColorScheme {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final List<Integer> accent1;
    @NotNull
    private final List<Integer> accent2;
    @NotNull
    private final List<Integer> accent3;
    private final boolean darkTheme;
    @NotNull
    private final List<Integer> neutral1;
    @NotNull
    private final List<Integer> neutral2;

    public static final int getSeedColor(@NotNull WallpaperColors wallpaperColors) {
        return Companion.getSeedColor(wallpaperColors);
    }

    public ColorScheme(int i, boolean z) {
        this.darkTheme = z;
        Cam fromInt = Cam.fromInt((i == 0 || Cam.fromInt(i).getChroma() < 5.0f) ? -14979341 : i);
        float hue = fromInt.getHue();
        float coerceAtLeast = RangesKt___RangesKt.coerceAtLeast(fromInt.getChroma(), 48.0f);
        int access$wrapDegrees = Companion.wrapDegrees((int) (60.0f + hue));
        int[] of = Shades.m43of(hue, coerceAtLeast);
        Intrinsics.checkNotNullExpressionValue(of, "of(hue, chroma)");
        this.accent1 = ArraysKt___ArraysKt.toList(of);
        int[] of2 = Shades.m43of(hue, 16.0f);
        Intrinsics.checkNotNullExpressionValue(of2, "of(hue, ACCENT2_CHROMA)");
        this.accent2 = ArraysKt___ArraysKt.toList(of2);
        int[] of3 = Shades.m43of((float) access$wrapDegrees, 32.0f);
        Intrinsics.checkNotNullExpressionValue(of3, "of(tertiaryHue.toFloat(), ACCENT3_CHROMA)");
        this.accent3 = ArraysKt___ArraysKt.toList(of3);
        int[] of4 = Shades.m43of(hue, 4.0f);
        Intrinsics.checkNotNullExpressionValue(of4, "of(hue, NEUTRAL1_CHROMA)");
        this.neutral1 = ArraysKt___ArraysKt.toList(of4);
        int[] of5 = Shades.m43of(hue, 8.0f);
        Intrinsics.checkNotNullExpressionValue(of5, "of(hue, NEUTRAL2_CHROMA)");
        this.neutral2 = ArraysKt___ArraysKt.toList(of5);
    }

    @NotNull
    public final List<Integer> getAccent1() {
        return this.accent1;
    }

    @NotNull
    public final List<Integer> getAllAccentColors() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.accent1);
        arrayList.addAll(this.accent2);
        arrayList.addAll(this.accent3);
        return arrayList;
    }

    @NotNull
    public final List<Integer> getAllNeutralColors() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.neutral1);
        arrayList.addAll(this.neutral2);
        return arrayList;
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ColorScheme {\n  neutral1: ");
        Companion companion = Companion;
        sb.append(companion.humanReadable(this.neutral1));
        sb.append("\n  neutral2: ");
        sb.append(companion.humanReadable(this.neutral2));
        sb.append("\n  accent1: ");
        sb.append(companion.humanReadable(this.accent1));
        sb.append("\n  accent2: ");
        sb.append(companion.humanReadable(this.accent2));
        sb.append("\n  accent3: ");
        sb.append(companion.humanReadable(this.accent3));
        sb.append("\n}");
        return sb.toString();
    }

    /* compiled from: ColorScheme.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final int getSeedColor(@NotNull WallpaperColors wallpaperColors) {
            Intrinsics.checkNotNullParameter(wallpaperColors, "wallpaperColors");
            return ((Number) CollectionsKt___CollectionsKt.first(getSeedColors(wallpaperColors))).intValue();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:83:0x0310, code lost:
            if (r2 != 15) goto L_0x0320;
         */
        @org.jetbrains.annotations.NotNull
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final java.util.List<java.lang.Integer> getSeedColors(@org.jetbrains.annotations.NotNull android.app.WallpaperColors r22) {
            /*
                r21 = this;
                java.lang.String r0 = "wallpaperColors"
                r1 = r22
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r1, r0)
                java.util.Map r0 = r22.getAllColors()
                java.util.Collection r0 = r0.values()
                java.util.Iterator r0 = r0.iterator()
                boolean r2 = r0.hasNext()
                if (r2 == 0) goto L_0x0323
                java.lang.Object r2 = r0.next()
            L_0x001e:
                boolean r3 = r0.hasNext()
                if (r3 == 0) goto L_0x003f
                java.lang.Object r3 = r0.next()
                java.lang.Integer r3 = (java.lang.Integer) r3
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r2 = r2.intValue()
                java.lang.String r4 = "b"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r4)
                int r3 = r3.intValue()
                int r2 = r2 + r3
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                goto L_0x001e
            L_0x003f:
                java.lang.Number r2 = (java.lang.Number) r2
                int r0 = r2.intValue()
                double r2 = (double) r0
                r4 = 0
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                r6 = 0
                r7 = 1
                if (r0 != 0) goto L_0x0050
                r0 = r7
                goto L_0x0051
            L_0x0050:
                r0 = r6
            L_0x0051:
                r8 = -14979341(0xffffffffff1b6ef3, float:-2.0660642E38)
                r9 = 1084227584(0x40a00000, float:5.0)
                if (r0 == 0) goto L_0x00cf
                java.util.List r0 = r22.getMainColors()
                java.lang.String r1 = "wallpaperColors.mainColors"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)
                java.util.ArrayList r1 = new java.util.ArrayList
                r2 = 10
                int r2 = kotlin.collections.CollectionsKt__IterablesKt.collectionSizeOrDefault(r0, r2)
                r1.<init>(r2)
                java.util.Iterator r0 = r0.iterator()
            L_0x0071:
                boolean r2 = r0.hasNext()
                if (r2 == 0) goto L_0x0089
                java.lang.Object r2 = r0.next()
                android.graphics.Color r2 = (android.graphics.Color) r2
                int r2 = r2.toArgb()
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r1.add(r2)
                goto L_0x0071
            L_0x0089:
                java.util.List r0 = kotlin.collections.CollectionsKt___CollectionsKt.distinct(r1)
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.Iterator r0 = r0.iterator()
            L_0x0096:
                boolean r2 = r0.hasNext()
                if (r2 == 0) goto L_0x00bc
                java.lang.Object r2 = r0.next()
                r3 = r2
                java.lang.Number r3 = (java.lang.Number) r3
                int r3 = r3.intValue()
                com.android.internal.graphics.cam.Cam r3 = com.android.internal.graphics.cam.Cam.fromInt(r3)
                float r3 = r3.getChroma()
                int r3 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r3 < 0) goto L_0x00b5
                r3 = r7
                goto L_0x00b6
            L_0x00b5:
                r3 = r6
            L_0x00b6:
                if (r3 == 0) goto L_0x0096
                r1.add(r2)
                goto L_0x0096
            L_0x00bc:
                java.util.List r0 = kotlin.collections.CollectionsKt___CollectionsKt.toList(r1)
                boolean r1 = r0.isEmpty()
                if (r1 == 0) goto L_0x00ce
                java.lang.Integer r0 = java.lang.Integer.valueOf(r8)
                java.util.List r0 = kotlin.collections.CollectionsKt__CollectionsJVMKt.listOf(r0)
            L_0x00ce:
                return r0
            L_0x00cf:
                java.util.Map r10 = r22.getAllColors()
                java.lang.String r11 = "wallpaperColors.allColors"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r10, r11)
                java.util.LinkedHashMap r12 = new java.util.LinkedHashMap
                int r13 = r10.size()
                int r13 = kotlin.collections.MapsKt__MapsJVMKt.mapCapacity(r13)
                r12.<init>(r13)
                java.util.Set r10 = r10.entrySet()
                java.util.Iterator r10 = r10.iterator()
            L_0x00ee:
                boolean r13 = r10.hasNext()
                if (r13 == 0) goto L_0x0114
                java.lang.Object r13 = r10.next()
                java.util.Map$Entry r13 = (java.util.Map.Entry) r13
                java.lang.Object r14 = r13.getKey()
                java.lang.Object r13 = r13.getValue()
                java.lang.Number r13 = (java.lang.Number) r13
                int r13 = r13.intValue()
                double r4 = (double) r13
                double r4 = r4 / r2
                java.lang.Double r4 = java.lang.Double.valueOf(r4)
                r12.put(r14, r4)
                r4 = 0
                goto L_0x00ee
            L_0x0114:
                java.util.Map r2 = r22.getAllColors()
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r2, r11)
                java.util.LinkedHashMap r3 = new java.util.LinkedHashMap
                int r4 = r2.size()
                int r4 = kotlin.collections.MapsKt__MapsJVMKt.mapCapacity(r4)
                r3.<init>(r4)
                java.util.Set r2 = r2.entrySet()
                java.util.Iterator r2 = r2.iterator()
            L_0x0130:
                boolean r4 = r2.hasNext()
                java.lang.String r5 = "it.key"
                if (r4 == 0) goto L_0x0157
                java.lang.Object r4 = r2.next()
                java.util.Map$Entry r4 = (java.util.Map.Entry) r4
                java.lang.Object r10 = r4.getKey()
                java.lang.Object r4 = r4.getKey()
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r4, r5)
                java.lang.Number r4 = (java.lang.Number) r4
                int r4 = r4.intValue()
                com.android.internal.graphics.cam.Cam r4 = com.android.internal.graphics.cam.Cam.fromInt(r4)
                r3.put(r10, r4)
                goto L_0x0130
            L_0x0157:
                r4 = r21
                java.util.List r2 = r4.huePopulations(r3, r12)
                java.util.Map r1 = r22.getAllColors()
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r11)
                java.util.LinkedHashMap r4 = new java.util.LinkedHashMap
                int r10 = r1.size()
                int r10 = kotlin.collections.MapsKt__MapsJVMKt.mapCapacity(r10)
                r4.<init>(r10)
                java.util.Set r1 = r1.entrySet()
                java.util.Iterator r1 = r1.iterator()
            L_0x0179:
                boolean r10 = r1.hasNext()
                r11 = 15
                if (r10 == 0) goto L_0x01c8
                java.lang.Object r10 = r1.next()
                java.util.Map$Entry r10 = (java.util.Map.Entry) r10
                java.lang.Object r12 = r10.getKey()
                java.lang.Object r10 = r10.getKey()
                java.lang.Object r10 = r3.get(r10)
                com.android.internal.graphics.cam.Cam r10 = (com.android.internal.graphics.cam.Cam) r10
                kotlin.jvm.internal.Intrinsics.checkNotNull(r10)
                float r10 = r10.getHue()
                int r10 = kotlin.math.MathKt__MathJVMKt.roundToInt(r10)
                int r13 = r10 + -15
                int r10 = r10 + r11
                r17 = 0
                if (r13 > r10) goto L_0x01c0
            L_0x01a7:
                int r11 = r13 + 1
                com.android.systemui.monet.ColorScheme$Companion r14 = com.android.systemui.monet.ColorScheme.Companion
                int r14 = r14.wrapDegrees(r13)
                java.lang.Object r14 = r2.get(r14)
                java.lang.Number r14 = (java.lang.Number) r14
                double r19 = r14.doubleValue()
                double r17 = r17 + r19
                if (r13 != r10) goto L_0x01be
                goto L_0x01c0
            L_0x01be:
                r13 = r11
                goto L_0x01a7
            L_0x01c0:
                java.lang.Double r10 = java.lang.Double.valueOf(r17)
                r4.put(r12, r10)
                goto L_0x0179
            L_0x01c8:
                java.util.LinkedHashMap r1 = new java.util.LinkedHashMap
                r1.<init>()
                java.util.Set r2 = r3.entrySet()
                java.util.Iterator r2 = r2.iterator()
            L_0x01d5:
                boolean r10 = r2.hasNext()
                if (r10 == 0) goto L_0x022c
                java.lang.Object r10 = r2.next()
                java.util.Map$Entry r10 = (java.util.Map.Entry) r10
                java.lang.Object r12 = r10.getValue()
                com.android.internal.graphics.cam.Cam r12 = (com.android.internal.graphics.cam.Cam) r12
                java.lang.Object r13 = r10.getKey()
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r13, r5)
                java.lang.Number r13 = (java.lang.Number) r13
                int r13 = r13.intValue()
                com.android.internal.graphics.cam.CamUtils.lstarFromInt(r13)
                java.lang.Object r13 = r10.getKey()
                java.lang.Object r13 = r4.get(r13)
                java.lang.Double r13 = (java.lang.Double) r13
                kotlin.jvm.internal.Intrinsics.checkNotNull(r13)
                double r13 = r13.doubleValue()
                float r12 = r12.getChroma()
                int r12 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
                if (r12 < 0) goto L_0x021d
                if (r0 != 0) goto L_0x021b
                r15 = 4576918229304087675(0x3f847ae147ae147b, double:0.01)
                int r12 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
                if (r12 <= 0) goto L_0x021d
            L_0x021b:
                r12 = r7
                goto L_0x021e
            L_0x021d:
                r12 = r6
            L_0x021e:
                if (r12 == 0) goto L_0x01d5
                java.lang.Object r12 = r10.getKey()
                java.lang.Object r10 = r10.getValue()
                r1.put(r12, r10)
                goto L_0x01d5
            L_0x022c:
                java.util.LinkedHashMap r0 = new java.util.LinkedHashMap
                int r2 = r1.size()
                int r2 = kotlin.collections.MapsKt__MapsJVMKt.mapCapacity(r2)
                r0.<init>(r2)
                java.util.Set r1 = r1.entrySet()
                java.util.Iterator r1 = r1.iterator()
            L_0x0241:
                boolean r2 = r1.hasNext()
                if (r2 == 0) goto L_0x027b
                java.lang.Object r2 = r1.next()
                java.util.Map$Entry r2 = (java.util.Map.Entry) r2
                java.lang.Object r5 = r2.getKey()
                com.android.systemui.monet.ColorScheme$Companion r9 = com.android.systemui.monet.ColorScheme.Companion
                java.lang.Object r10 = r2.getValue()
                java.lang.String r12 = "it.value"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r10, r12)
                com.android.internal.graphics.cam.Cam r10 = (com.android.internal.graphics.cam.Cam) r10
                java.lang.Object r2 = r2.getKey()
                java.lang.Object r2 = r4.get(r2)
                java.lang.Double r2 = (java.lang.Double) r2
                kotlin.jvm.internal.Intrinsics.checkNotNull(r2)
                double r12 = r2.doubleValue()
                double r9 = r9.score(r10, r12)
                java.lang.Double r2 = java.lang.Double.valueOf(r9)
                r0.put(r5, r2)
                goto L_0x0241
            L_0x027b:
                java.util.Set r0 = r0.entrySet()
                java.util.List r0 = kotlin.collections.CollectionsKt___CollectionsKt.toMutableList(r0)
                int r1 = r0.size()
                if (r1 <= r7) goto L_0x0291
                com.android.systemui.monet.ColorScheme$Companion$getSeedColors$$inlined$sortByDescending$1 r1 = new com.android.systemui.monet.ColorScheme$Companion$getSeedColors$$inlined$sortByDescending$1
                r1.<init>()
                kotlin.collections.CollectionsKt__MutableCollectionsJVMKt.sortWith(r0, r1)
            L_0x0291:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                r2 = 90
            L_0x0298:
                int r4 = r2 + -1
                r1.clear()
                java.util.Iterator r5 = r0.iterator()
            L_0x02a1:
                boolean r9 = r5.hasNext()
                if (r9 == 0) goto L_0x0310
                java.lang.Object r9 = r5.next()
                java.util.Map$Entry r9 = (java.util.Map.Entry) r9
                java.lang.Object r9 = r9.getKey()
                java.lang.Integer r9 = (java.lang.Integer) r9
                java.util.Iterator r10 = r1.iterator()
            L_0x02b7:
                boolean r12 = r10.hasNext()
                if (r12 == 0) goto L_0x02f7
                java.lang.Object r12 = r10.next()
                r13 = r12
                java.lang.Number r13 = (java.lang.Number) r13
                int r13 = r13.intValue()
                java.lang.Object r14 = r3.get(r9)
                com.android.internal.graphics.cam.Cam r14 = (com.android.internal.graphics.cam.Cam) r14
                kotlin.jvm.internal.Intrinsics.checkNotNull(r14)
                float r14 = r14.getHue()
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                java.lang.Object r13 = r3.get(r13)
                com.android.internal.graphics.cam.Cam r13 = (com.android.internal.graphics.cam.Cam) r13
                kotlin.jvm.internal.Intrinsics.checkNotNull(r13)
                float r13 = r13.getHue()
                com.android.systemui.monet.ColorScheme$Companion r15 = com.android.systemui.monet.ColorScheme.Companion
                float r13 = r15.hueDiff(r14, r13)
                float r14 = (float) r2
                int r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
                if (r13 >= 0) goto L_0x02f3
                r13 = r7
                goto L_0x02f4
            L_0x02f3:
                r13 = r6
            L_0x02f4:
                if (r13 == 0) goto L_0x02b7
                goto L_0x02f8
            L_0x02f7:
                r12 = 0
            L_0x02f8:
                if (r12 == 0) goto L_0x02fc
                r10 = r7
                goto L_0x02fd
            L_0x02fc:
                r10 = r6
            L_0x02fd:
                if (r10 == 0) goto L_0x0300
                goto L_0x02a1
            L_0x0300:
                java.lang.String r10 = "int"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r9, r10)
                r1.add(r9)
                int r9 = r1.size()
                r10 = 4
                if (r9 < r10) goto L_0x02a1
                goto L_0x0312
            L_0x0310:
                if (r2 != r11) goto L_0x0320
            L_0x0312:
                boolean r0 = r1.isEmpty()
                if (r0 == 0) goto L_0x031f
                java.lang.Integer r0 = java.lang.Integer.valueOf(r8)
                r1.add(r0)
            L_0x031f:
                return r1
            L_0x0320:
                r2 = r4
                goto L_0x0298
            L_0x0323:
                java.lang.UnsupportedOperationException r0 = new java.lang.UnsupportedOperationException
                java.lang.String r1 = "Empty collection can't be reduced."
                r0.<init>(r1)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.monet.ColorScheme.Companion.getSeedColors(android.app.WallpaperColors):java.util.List");
        }

        /* access modifiers changed from: private */
        public final int wrapDegrees(int i) {
            if (i < 0) {
                return (i % 360) + 360;
            }
            return i >= 360 ? i % 360 : i;
        }

        private final float hueDiff(float f, float f2) {
            return 180.0f - Math.abs(Math.abs(f - f2) - 180.0f);
        }

        /* access modifiers changed from: private */
        public final String humanReadable(List<Integer> list) {
            return CollectionsKt___CollectionsKt.joinToString$default(list, (CharSequence) null, (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, ColorScheme$Companion$humanReadable$1.INSTANCE, 31, (Object) null);
        }

        private final double score(Cam cam, double d) {
            float f;
            double d2;
            double d3 = d * 70.0d;
            if (cam.getChroma() < 48.0f) {
                d2 = 0.1d;
                f = cam.getChroma();
            } else {
                d2 = 0.3d;
                f = cam.getChroma();
            }
            return (((double) (f - 48.0f)) * d2) + d3;
        }

        private final List<Double> huePopulations(Map<Integer, ? extends Cam> map, Map<Integer, Double> map2) {
            ArrayList arrayList = new ArrayList(360);
            for (int i = 0; i < 360; i++) {
                arrayList.add(Double.valueOf(0.0d));
            }
            List<Double> mutableList = CollectionsKt___CollectionsKt.toMutableList(arrayList);
            for (Map.Entry next : map2.entrySet()) {
                Double d = map2.get(next.getKey());
                Intrinsics.checkNotNull(d);
                double doubleValue = d.doubleValue();
                Cam cam = map.get(next.getKey());
                Intrinsics.checkNotNull(cam);
                int roundToInt = MathKt__MathJVMKt.roundToInt(cam.getHue()) % 360;
                mutableList.set(roundToInt, Double.valueOf(mutableList.get(roundToInt).doubleValue() + doubleValue));
            }
            return mutableList;
        }
    }
}
