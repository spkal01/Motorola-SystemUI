package com.android.systemui.p006qs;

import java.util.List;
import java.util.function.Predicate;

/* renamed from: com.android.systemui.qs.QSTileHost$$ExternalSyntheticLambda7 */
public final /* synthetic */ class QSTileHost$$ExternalSyntheticLambda7 implements Predicate {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ QSTileHost$$ExternalSyntheticLambda7(String str, int i) {
        this.f$0 = str;
        this.f$1 = i;
    }

    public final boolean test(Object obj) {
        return QSTileHost.lambda$addTile$5(this.f$0, this.f$1, (List) obj);
    }
}
