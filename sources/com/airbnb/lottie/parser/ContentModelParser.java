package com.airbnb.lottie.parser;

import com.airbnb.lottie.parser.moshi.JsonReader;

class ContentModelParser {
    private static JsonReader.Options NAMES = JsonReader.Options.m6of("ty", "d");

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00b8, code lost:
        if (r2.equals("gf") == false) goto L_0x0034;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.airbnb.lottie.model.content.ContentModel parse(com.airbnb.lottie.parser.moshi.JsonReader r7, com.airbnb.lottie.LottieComposition r8) throws java.io.IOException {
        /*
            r7.beginObject()
            r0 = 2
            r1 = r0
        L_0x0005:
            boolean r2 = r7.hasNext()
            r3 = 1
            r4 = 0
            if (r2 == 0) goto L_0x0028
            com.airbnb.lottie.parser.moshi.JsonReader$Options r2 = NAMES
            int r2 = r7.selectName(r2)
            if (r2 == 0) goto L_0x0023
            if (r2 == r3) goto L_0x001e
            r7.skipName()
            r7.skipValue()
            goto L_0x0005
        L_0x001e:
            int r1 = r7.nextInt()
            goto L_0x0005
        L_0x0023:
            java.lang.String r2 = r7.nextString()
            goto L_0x0029
        L_0x0028:
            r2 = r4
        L_0x0029:
            if (r2 != 0) goto L_0x002c
            return r4
        L_0x002c:
            r5 = -1
            int r6 = r2.hashCode()
            switch(r6) {
                case 3239: goto L_0x00c8;
                case 3270: goto L_0x00bc;
                case 3295: goto L_0x00b2;
                case 3307: goto L_0x00a7;
                case 3308: goto L_0x009c;
                case 3488: goto L_0x0091;
                case 3633: goto L_0x0086;
                case 3646: goto L_0x007b;
                case 3669: goto L_0x006f;
                case 3679: goto L_0x0061;
                case 3681: goto L_0x0053;
                case 3705: goto L_0x0045;
                case 3710: goto L_0x0037;
                default: goto L_0x0034;
            }
        L_0x0034:
            r0 = r5
            goto L_0x00d3
        L_0x0037:
            java.lang.String r0 = "tr"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x0041
            goto L_0x0034
        L_0x0041:
            r0 = 12
            goto L_0x00d3
        L_0x0045:
            java.lang.String r0 = "tm"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x004f
            goto L_0x0034
        L_0x004f:
            r0 = 11
            goto L_0x00d3
        L_0x0053:
            java.lang.String r0 = "st"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x005d
            goto L_0x0034
        L_0x005d:
            r0 = 10
            goto L_0x00d3
        L_0x0061:
            java.lang.String r0 = "sr"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x006b
            goto L_0x0034
        L_0x006b:
            r0 = 9
            goto L_0x00d3
        L_0x006f:
            java.lang.String r0 = "sh"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x0078
            goto L_0x0034
        L_0x0078:
            r0 = 8
            goto L_0x00d3
        L_0x007b:
            java.lang.String r0 = "rp"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x0084
            goto L_0x0034
        L_0x0084:
            r0 = 7
            goto L_0x00d3
        L_0x0086:
            java.lang.String r0 = "rc"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x008f
            goto L_0x0034
        L_0x008f:
            r0 = 6
            goto L_0x00d3
        L_0x0091:
            java.lang.String r0 = "mm"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x009a
            goto L_0x0034
        L_0x009a:
            r0 = 5
            goto L_0x00d3
        L_0x009c:
            java.lang.String r0 = "gs"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x00a5
            goto L_0x0034
        L_0x00a5:
            r0 = 4
            goto L_0x00d3
        L_0x00a7:
            java.lang.String r0 = "gr"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x00b0
            goto L_0x0034
        L_0x00b0:
            r0 = 3
            goto L_0x00d3
        L_0x00b2:
            java.lang.String r3 = "gf"
            boolean r3 = r2.equals(r3)
            if (r3 != 0) goto L_0x00d3
            goto L_0x0034
        L_0x00bc:
            java.lang.String r0 = "fl"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x00c6
            goto L_0x0034
        L_0x00c6:
            r0 = r3
            goto L_0x00d3
        L_0x00c8:
            java.lang.String r0 = "el"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x00d2
            goto L_0x0034
        L_0x00d2:
            r0 = 0
        L_0x00d3:
            switch(r0) {
                case 0: goto L_0x012c;
                case 1: goto L_0x0127;
                case 2: goto L_0x0122;
                case 3: goto L_0x011d;
                case 4: goto L_0x0118;
                case 5: goto L_0x010e;
                case 6: goto L_0x0109;
                case 7: goto L_0x0104;
                case 8: goto L_0x00ff;
                case 9: goto L_0x00fa;
                case 10: goto L_0x00f5;
                case 11: goto L_0x00f0;
                case 12: goto L_0x00eb;
                default: goto L_0x00d6;
            }
        L_0x00d6:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "Unknown shape type "
            r8.append(r0)
            r8.append(r2)
            java.lang.String r8 = r8.toString()
            com.airbnb.lottie.utils.Logger.warning(r8)
            goto L_0x0130
        L_0x00eb:
            com.airbnb.lottie.model.animatable.AnimatableTransform r4 = com.airbnb.lottie.parser.AnimatableTransformParser.parse(r7, r8)
            goto L_0x0130
        L_0x00f0:
            com.airbnb.lottie.model.content.ShapeTrimPath r4 = com.airbnb.lottie.parser.ShapeTrimPathParser.parse(r7, r8)
            goto L_0x0130
        L_0x00f5:
            com.airbnb.lottie.model.content.ShapeStroke r4 = com.airbnb.lottie.parser.ShapeStrokeParser.parse(r7, r8)
            goto L_0x0130
        L_0x00fa:
            com.airbnb.lottie.model.content.PolystarShape r4 = com.airbnb.lottie.parser.PolystarShapeParser.parse(r7, r8)
            goto L_0x0130
        L_0x00ff:
            com.airbnb.lottie.model.content.ShapePath r4 = com.airbnb.lottie.parser.ShapePathParser.parse(r7, r8)
            goto L_0x0130
        L_0x0104:
            com.airbnb.lottie.model.content.Repeater r4 = com.airbnb.lottie.parser.RepeaterParser.parse(r7, r8)
            goto L_0x0130
        L_0x0109:
            com.airbnb.lottie.model.content.RectangleShape r4 = com.airbnb.lottie.parser.RectangleShapeParser.parse(r7, r8)
            goto L_0x0130
        L_0x010e:
            com.airbnb.lottie.model.content.MergePaths r4 = com.airbnb.lottie.parser.MergePathsParser.parse(r7)
            java.lang.String r0 = "Animation contains merge paths. Merge paths are only supported on KitKat+ and must be manually enabled by calling enableMergePathsForKitKatAndAbove()."
            r8.addWarning(r0)
            goto L_0x0130
        L_0x0118:
            com.airbnb.lottie.model.content.GradientStroke r4 = com.airbnb.lottie.parser.GradientStrokeParser.parse(r7, r8)
            goto L_0x0130
        L_0x011d:
            com.airbnb.lottie.model.content.ShapeGroup r4 = com.airbnb.lottie.parser.ShapeGroupParser.parse(r7, r8)
            goto L_0x0130
        L_0x0122:
            com.airbnb.lottie.model.content.GradientFill r4 = com.airbnb.lottie.parser.GradientFillParser.parse(r7, r8)
            goto L_0x0130
        L_0x0127:
            com.airbnb.lottie.model.content.ShapeFill r4 = com.airbnb.lottie.parser.ShapeFillParser.parse(r7, r8)
            goto L_0x0130
        L_0x012c:
            com.airbnb.lottie.model.content.CircleShape r4 = com.airbnb.lottie.parser.CircleShapeParser.parse(r7, r8, r1)
        L_0x0130:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x013a
            r7.skipValue()
            goto L_0x0130
        L_0x013a:
            r7.endObject()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.airbnb.lottie.parser.ContentModelParser.parse(com.airbnb.lottie.parser.moshi.JsonReader, com.airbnb.lottie.LottieComposition):com.airbnb.lottie.model.content.ContentModel");
    }
}
