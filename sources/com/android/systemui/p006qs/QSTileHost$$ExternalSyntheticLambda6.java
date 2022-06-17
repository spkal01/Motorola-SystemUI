package com.android.systemui.p006qs;

import java.util.List;
import java.util.function.Predicate;

/* renamed from: com.android.systemui.qs.QSTileHost$$ExternalSyntheticLambda6 */
public final /* synthetic */ class QSTileHost$$ExternalSyntheticLambda6 implements Predicate {
    public final /* synthetic */ String f$0;

    public /* synthetic */ QSTileHost$$ExternalSyntheticLambda6(String str) {
        this.f$0 = str;
    }

    public final boolean test(Object obj) {
        return ((List) obj).remove(this.f$0);
    }
}
