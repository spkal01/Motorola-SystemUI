package com.android.p011wm.shell.bubbles;

import android.widget.TextView;
import com.android.p011wm.shell.C2219R;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.wm.shell.bubbles.StackEducationView$descTextView$2 */
/* compiled from: StackEducationView.kt */
final class StackEducationView$descTextView$2 extends Lambda implements Function0<TextView> {
    final /* synthetic */ StackEducationView this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    StackEducationView$descTextView$2(StackEducationView stackEducationView) {
        super(0);
        this.this$0 = stackEducationView;
    }

    public final TextView invoke() {
        return (TextView) this.this$0.findViewById(C2219R.C2222id.stack_education_description);
    }
}
