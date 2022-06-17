package com.android.systemui.statusbar.notification.collection;

import java.util.List;
import java.util.function.Predicate;

public final /* synthetic */ class ShadeListBuilder$$ExternalSyntheticLambda7 implements Predicate {
    public final /* synthetic */ ShadeListBuilder f$0;
    public final /* synthetic */ List f$1;

    public /* synthetic */ ShadeListBuilder$$ExternalSyntheticLambda7(ShadeListBuilder shadeListBuilder, List list) {
        this.f$0 = shadeListBuilder;
        this.f$1 = list;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$promoteNotifs$0(this.f$1, (NotificationEntry) obj);
    }
}
