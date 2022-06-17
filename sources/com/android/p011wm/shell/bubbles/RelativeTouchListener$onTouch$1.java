package com.android.p011wm.shell.bubbles;

import android.view.View;

/* renamed from: com.android.wm.shell.bubbles.RelativeTouchListener$onTouch$1 */
/* compiled from: RelativeTouchListener.kt */
final class RelativeTouchListener$onTouch$1 implements Runnable {

    /* renamed from: $v */
    final /* synthetic */ View f180$v;
    final /* synthetic */ RelativeTouchListener this$0;

    RelativeTouchListener$onTouch$1(View view, RelativeTouchListener relativeTouchListener) {
        this.f180$v = view;
        this.this$0 = relativeTouchListener;
    }

    public final void run() {
        if (this.f180$v.isLongClickable()) {
            this.this$0.performedLongClick = this.f180$v.performLongClick();
        }
    }
}
