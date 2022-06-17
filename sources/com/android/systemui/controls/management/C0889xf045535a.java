package com.android.systemui.controls.management;

import com.android.systemui.controls.ControlsServiceInfo;
import java.util.Comparator;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.management.AppAdapter$callback$1$onServicesUpdated$1$run$$inlined$compareBy$1 */
/* compiled from: Comparisons.kt */
public final class C0889xf045535a<T> implements Comparator<T> {
    final /* synthetic */ Comparator $comparator;

    public C0889xf045535a(Comparator comparator) {
        this.$comparator = comparator;
    }

    public final int compare(T t, T t2) {
        Comparator comparator = this.$comparator;
        CharSequence loadLabel = ((ControlsServiceInfo) t).loadLabel();
        Intrinsics.checkNotNullExpressionValue(loadLabel, "it.loadLabel()");
        CharSequence loadLabel2 = ((ControlsServiceInfo) t2).loadLabel();
        Intrinsics.checkNotNullExpressionValue(loadLabel2, "it.loadLabel()");
        return comparator.compare(loadLabel, loadLabel2);
    }
}
