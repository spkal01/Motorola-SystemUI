package com.android.systemui.privacy;

/* compiled from: PrivacyItemController.kt */
final class PrivacyItemController$MyExecutor$updateListeningState$1 implements Runnable {
    final /* synthetic */ PrivacyItemController this$0;

    PrivacyItemController$MyExecutor$updateListeningState$1(PrivacyItemController privacyItemController) {
        this.this$0 = privacyItemController;
    }

    public final void run() {
        this.this$0.setListeningState();
    }
}
