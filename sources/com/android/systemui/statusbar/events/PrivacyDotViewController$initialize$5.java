package com.android.systemui.statusbar.events;

/* compiled from: PrivacyDotViewController.kt */
final class PrivacyDotViewController$initialize$5 implements Runnable {
    final /* synthetic */ PrivacyDotViewController this$0;

    PrivacyDotViewController$initialize$5(PrivacyDotViewController privacyDotViewController) {
        this.this$0 = privacyDotViewController;
    }

    public final void run() {
        this.this$0.animationScheduler.addCallback(this.this$0.systemStatusAnimationCallback);
    }
}
