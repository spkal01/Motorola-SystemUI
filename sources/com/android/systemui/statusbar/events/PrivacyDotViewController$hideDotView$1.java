package com.android.systemui.statusbar.events;

import android.view.View;

/* compiled from: PrivacyDotViewController.kt */
final class PrivacyDotViewController$hideDotView$1 implements Runnable {
    final /* synthetic */ View $dot;
    final /* synthetic */ PrivacyDotViewController this$0;

    PrivacyDotViewController$hideDotView$1(View view, PrivacyDotViewController privacyDotViewController) {
        this.$dot = view;
        this.this$0 = privacyDotViewController;
    }

    public final void run() {
        this.$dot.setVisibility(4);
        this.this$0.notifyDotViewStateChanged(false);
    }
}
