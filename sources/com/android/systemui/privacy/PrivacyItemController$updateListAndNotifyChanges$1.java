package com.android.systemui.privacy;

import com.android.systemui.util.concurrency.DelayableExecutor;

/* compiled from: PrivacyItemController.kt */
final class PrivacyItemController$updateListAndNotifyChanges$1 implements Runnable {
    final /* synthetic */ DelayableExecutor $uiExecutor;
    final /* synthetic */ PrivacyItemController this$0;

    PrivacyItemController$updateListAndNotifyChanges$1(PrivacyItemController privacyItemController, DelayableExecutor delayableExecutor) {
        this.this$0 = privacyItemController;
        this.$uiExecutor = delayableExecutor;
    }

    public final void run() {
        this.this$0.updatePrivacyList();
        this.$uiExecutor.execute(this.this$0.notifyChanges);
    }
}
