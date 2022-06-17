package com.android.systemui.p006qs.tileimpl;

import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.qs.tileimpl.QSTileViewImplKt */
/* compiled from: QSTileViewImpl.kt */
public final class QSTileViewImplKt {
    /* access modifiers changed from: private */
    public static final PropertyValuesHolder colorValuesHolder(String str, int... iArr) {
        int[] iArr2 = new int[iArr.length];
        System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
        PropertyValuesHolder ofInt = PropertyValuesHolder.ofInt(str, iArr2);
        ofInt.setEvaluator(ArgbEvaluator.getInstance());
        Intrinsics.checkNotNullExpressionValue(ofInt, "ofInt(name, *values).apply {\n        setEvaluator(ArgbEvaluator.getInstance())\n    }");
        return ofInt;
    }
}
