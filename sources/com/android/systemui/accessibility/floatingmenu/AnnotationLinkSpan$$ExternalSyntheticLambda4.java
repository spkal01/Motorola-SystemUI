package com.android.systemui.accessibility.floatingmenu;

import com.android.systemui.accessibility.floatingmenu.AnnotationLinkSpan;
import java.util.function.Predicate;

public final /* synthetic */ class AnnotationLinkSpan$$ExternalSyntheticLambda4 implements Predicate {
    public final /* synthetic */ String f$0;

    public /* synthetic */ AnnotationLinkSpan$$ExternalSyntheticLambda4(String str) {
        this.f$0 = str;
    }

    public final boolean test(Object obj) {
        return AnnotationLinkSpan.lambda$linkify$1(this.f$0, (AnnotationLinkSpan.LinkInfo) obj);
    }
}
