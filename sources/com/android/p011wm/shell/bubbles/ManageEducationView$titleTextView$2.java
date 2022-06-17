package com.android.p011wm.shell.bubbles;

import android.widget.TextView;
import com.android.p011wm.shell.C2219R;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.wm.shell.bubbles.ManageEducationView$titleTextView$2 */
/* compiled from: ManageEducationView.kt */
final class ManageEducationView$titleTextView$2 extends Lambda implements Function0<TextView> {
    final /* synthetic */ ManageEducationView this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ManageEducationView$titleTextView$2(ManageEducationView manageEducationView) {
        super(0);
        this.this$0 = manageEducationView;
    }

    public final TextView invoke() {
        return (TextView) this.this$0.findViewById(C2219R.C2222id.user_education_title);
    }
}
