package com.android.systemui.accessibility.floatingmenu;

import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import com.android.systemui.accessibility.floatingmenu.AnnotationLinkSpan;
import java.util.Arrays;
import java.util.function.Consumer;

public final /* synthetic */ class AnnotationLinkSpan$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ AnnotationLinkSpan.LinkInfo[] f$0;
    public final /* synthetic */ SpannableStringBuilder f$1;
    public final /* synthetic */ SpannableString f$2;

    public /* synthetic */ AnnotationLinkSpan$$ExternalSyntheticLambda2(AnnotationLinkSpan.LinkInfo[] linkInfoArr, SpannableStringBuilder spannableStringBuilder, SpannableString spannableString) {
        this.f$0 = linkInfoArr;
        this.f$1 = spannableStringBuilder;
        this.f$2 = spannableString;
    }

    public final void accept(Object obj) {
        Arrays.asList(this.f$0).stream().filter(new AnnotationLinkSpan$$ExternalSyntheticLambda4(((Annotation) obj).getValue())).findFirst().flatMap(AnnotationLinkSpan$$ExternalSyntheticLambda3.INSTANCE).ifPresent(new AnnotationLinkSpan$$ExternalSyntheticLambda0(this.f$1, this.f$2, (Annotation) obj));
    }
}
