package com.android.systemui.accessibility.floatingmenu;

import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;
import java.util.Arrays;
import java.util.Optional;

class AnnotationLinkSpan extends ClickableSpan {
    private final Optional<View.OnClickListener> mClickListener;

    private AnnotationLinkSpan(View.OnClickListener onClickListener) {
        this.mClickListener = Optional.ofNullable(onClickListener);
    }

    public void onClick(View view) {
        this.mClickListener.ifPresent(new AnnotationLinkSpan$$ExternalSyntheticLambda1(view));
    }

    static CharSequence linkify(CharSequence charSequence, LinkInfo... linkInfoArr) {
        SpannableString spannableString = new SpannableString(charSequence);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
        Arrays.asList((Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class)).forEach(new AnnotationLinkSpan$$ExternalSyntheticLambda2(linkInfoArr, spannableStringBuilder, spannableString));
        return spannableStringBuilder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$linkify$1(String str, LinkInfo linkInfo) {
        return linkInfo.mAnnotation.isPresent() && ((String) linkInfo.mAnnotation.get()).equals(str);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$linkify$3(SpannableStringBuilder spannableStringBuilder, SpannableString spannableString, Annotation annotation, View.OnClickListener onClickListener) {
        AnnotationLinkSpan annotationLinkSpan = new AnnotationLinkSpan(onClickListener);
        spannableStringBuilder.setSpan(annotationLinkSpan, spannableString.getSpanStart(annotation), spannableString.getSpanEnd(annotation), spannableString.getSpanFlags(annotationLinkSpan));
    }

    static class LinkInfo {
        /* access modifiers changed from: private */
        public final Optional<String> mAnnotation;
        /* access modifiers changed from: private */
        public final Optional<View.OnClickListener> mListener;

        LinkInfo(String str, View.OnClickListener onClickListener) {
            this.mAnnotation = Optional.of(str);
            this.mListener = Optional.ofNullable(onClickListener);
        }
    }
}
