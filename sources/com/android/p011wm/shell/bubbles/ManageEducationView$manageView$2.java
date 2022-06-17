package com.android.p011wm.shell.bubbles;

import android.view.View;
import com.android.p011wm.shell.C2219R;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.wm.shell.bubbles.ManageEducationView$manageView$2 */
/* compiled from: ManageEducationView.kt */
final class ManageEducationView$manageView$2 extends Lambda implements Function0<View> {
    final /* synthetic */ ManageEducationView this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ManageEducationView$manageView$2(ManageEducationView manageEducationView) {
        super(0);
        this.this$0 = manageEducationView;
    }

    public final View invoke() {
        return this.this$0.findViewById(C2219R.C2222id.manage_education_view);
    }
}
