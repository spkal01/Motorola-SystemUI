package com.android.p011wm.shell.bubbles;

import android.view.View;
import com.android.p011wm.shell.C2219R;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.wm.shell.bubbles.StackEducationView$view$2 */
/* compiled from: StackEducationView.kt */
final class StackEducationView$view$2 extends Lambda implements Function0<View> {
    final /* synthetic */ StackEducationView this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    StackEducationView$view$2(StackEducationView stackEducationView) {
        super(0);
        this.this$0 = stackEducationView;
    }

    public final View invoke() {
        return this.this$0.findViewById(C2219R.C2222id.stack_education_layout);
    }
}
