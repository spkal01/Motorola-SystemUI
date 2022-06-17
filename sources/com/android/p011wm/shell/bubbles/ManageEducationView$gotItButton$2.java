package com.android.p011wm.shell.bubbles;

import android.widget.Button;
import com.android.p011wm.shell.C2219R;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.wm.shell.bubbles.ManageEducationView$gotItButton$2 */
/* compiled from: ManageEducationView.kt */
final class ManageEducationView$gotItButton$2 extends Lambda implements Function0<Button> {
    final /* synthetic */ ManageEducationView this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ManageEducationView$gotItButton$2(ManageEducationView manageEducationView) {
        super(0);
        this.this$0 = manageEducationView;
    }

    public final Button invoke() {
        return (Button) this.this$0.findViewById(C2219R.C2222id.got_it);
    }
}
