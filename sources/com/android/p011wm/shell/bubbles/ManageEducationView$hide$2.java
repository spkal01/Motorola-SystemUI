package com.android.p011wm.shell.bubbles;

/* renamed from: com.android.wm.shell.bubbles.ManageEducationView$hide$2 */
/* compiled from: ManageEducationView.kt */
final class ManageEducationView$hide$2 implements Runnable {
    final /* synthetic */ ManageEducationView this$0;

    ManageEducationView$hide$2(ManageEducationView manageEducationView) {
        this.this$0 = manageEducationView;
    }

    public final void run() {
        this.this$0.isHiding = false;
        this.this$0.setVisibility(8);
    }
}
