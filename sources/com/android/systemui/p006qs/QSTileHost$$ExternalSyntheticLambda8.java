package com.android.systemui.p006qs;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/* renamed from: com.android.systemui.qs.QSTileHost$$ExternalSyntheticLambda8 */
public final /* synthetic */ class QSTileHost$$ExternalSyntheticLambda8 implements Predicate {
    public final /* synthetic */ List f$0;

    public /* synthetic */ QSTileHost$$ExternalSyntheticLambda8(List list) {
        this.f$0 = list;
    }

    public final boolean test(Object obj) {
        return QSTileHost.lambda$onTuningChanged$2(this.f$0, (Map.Entry) obj);
    }
}
