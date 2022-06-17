package com.android.systemui.statusbar.notification.stack;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.systemui.statusbar.notification.stack.PeopleHubView;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.Nullable;

/* compiled from: PeopleHubView.kt */
final class PeopleHubView$onFinishInflate$1 extends Lambda implements Function1<Integer, PeopleHubView.PersonDataListenerImpl> {
    final /* synthetic */ PeopleHubView this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    PeopleHubView$onFinishInflate$1(PeopleHubView peopleHubView) {
        super(1);
        this.this$0 = peopleHubView;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return invoke(((Number) obj).intValue());
    }

    @Nullable
    public final PeopleHubView.PersonDataListenerImpl invoke(int i) {
        ViewGroup access$getContents$p = this.this$0.contents;
        if (access$getContents$p != null) {
            View childAt = access$getContents$p.getChildAt(i);
            ImageView imageView = childAt instanceof ImageView ? (ImageView) childAt : null;
            if (imageView == null) {
                return null;
            }
            return new PeopleHubView.PersonDataListenerImpl(this.this$0, imageView);
        }
        Intrinsics.throwUninitializedPropertyAccessException("contents");
        throw null;
    }
}
