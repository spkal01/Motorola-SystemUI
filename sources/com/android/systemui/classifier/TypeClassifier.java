package com.android.systemui.classifier;

public class TypeClassifier extends FalsingClassifier {
    TypeClassifier(FalsingDataProvider falsingDataProvider) {
        super(falsingDataProvider);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002c, code lost:
        if (r10 != false) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0031, code lost:
        if (r10 != false) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0036, code lost:
        if (r10 != false) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003a, code lost:
        r1 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003e, code lost:
        if (r10 == false) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0041, code lost:
        if (r9 == false) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        return com.android.systemui.classifier.FalsingClassifier.Result.passed(0.5d);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return falsed(r1, getReason(r6));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001e, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0022, code lost:
        if (r10 != false) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0025, code lost:
        r9 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.systemui.classifier.FalsingClassifier.Result calculateFalsingResult(int r6, double r7, double r9) {
        /*
            r5 = this;
            r7 = 0
            r9 = 13
            if (r6 == r9) goto L_0x0053
            r9 = 14
            if (r6 != r9) goto L_0x000b
            goto L_0x0053
        L_0x000b:
            boolean r9 = r5.isVertical()
            boolean r10 = r5.isUp()
            boolean r0 = r5.isRight()
            r1 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r3 = 0
            r4 = 1
            switch(r6) {
                case 0: goto L_0x003c;
                case 1: goto L_0x0039;
                case 2: goto L_0x003c;
                case 3: goto L_0x001e;
                case 4: goto L_0x0034;
                case 5: goto L_0x002f;
                case 6: goto L_0x002a;
                case 7: goto L_0x001e;
                case 8: goto L_0x0034;
                case 9: goto L_0x003c;
                case 10: goto L_0x003a;
                case 11: goto L_0x0027;
                case 12: goto L_0x0020;
                case 13: goto L_0x001e;
                case 14: goto L_0x001e;
                case 15: goto L_0x0041;
                default: goto L_0x001e;
            }
        L_0x001e:
            r9 = r4
            goto L_0x0041
        L_0x0020:
            if (r9 == 0) goto L_0x001e
            if (r10 != 0) goto L_0x0025
            goto L_0x001e
        L_0x0025:
            r9 = r3
            goto L_0x0041
        L_0x0027:
            r9 = r9 ^ 1
            goto L_0x0041
        L_0x002a:
            if (r0 != 0) goto L_0x001e
            if (r10 != 0) goto L_0x0025
            goto L_0x001e
        L_0x002f:
            if (r0 == 0) goto L_0x001e
            if (r10 != 0) goto L_0x0025
            goto L_0x001e
        L_0x0034:
            if (r9 == 0) goto L_0x001e
            if (r10 != 0) goto L_0x0025
            goto L_0x001e
        L_0x0039:
            r7 = r1
        L_0x003a:
            r1 = r7
            goto L_0x0041
        L_0x003c:
            if (r9 == 0) goto L_0x001e
            if (r10 == 0) goto L_0x0025
            goto L_0x001e
        L_0x0041:
            if (r9 == 0) goto L_0x004c
            java.lang.String r6 = r5.getReason(r6)
            com.android.systemui.classifier.FalsingClassifier$Result r5 = r5.falsed(r1, r6)
            goto L_0x0052
        L_0x004c:
            r5 = 4602678819172646912(0x3fe0000000000000, double:0.5)
            com.android.systemui.classifier.FalsingClassifier$Result r5 = com.android.systemui.classifier.FalsingClassifier.Result.passed(r5)
        L_0x0052:
            return r5
        L_0x0053:
            com.android.systemui.classifier.FalsingClassifier$Result r5 = com.android.systemui.classifier.FalsingClassifier.Result.passed(r7)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.classifier.TypeClassifier.calculateFalsingResult(int, double, double):com.android.systemui.classifier.FalsingClassifier$Result");
    }

    private String getReason(int i) {
        return String.format("{interaction=%s, vertical=%s, up=%s, right=%s}", new Object[]{Integer.valueOf(i), Boolean.valueOf(isVertical()), Boolean.valueOf(isUp()), Boolean.valueOf(isRight())});
    }
}
