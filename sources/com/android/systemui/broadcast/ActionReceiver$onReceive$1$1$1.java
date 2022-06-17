package com.android.systemui.broadcast;

import android.content.Context;
import android.content.Intent;

/* compiled from: ActionReceiver.kt */
final class ActionReceiver$onReceive$1$1$1 implements Runnable {
    final /* synthetic */ Context $context;
    final /* synthetic */ int $id;
    final /* synthetic */ Intent $intent;
    final /* synthetic */ ReceiverData $it;
    final /* synthetic */ ActionReceiver this$0;

    ActionReceiver$onReceive$1$1$1(ReceiverData receiverData, ActionReceiver actionReceiver, Context context, Intent intent, int i) {
        this.$it = receiverData;
        this.this$0 = actionReceiver;
        this.$context = context;
        this.$intent = intent;
        this.$id = i;
    }

    public final void run() {
        this.$it.getReceiver().setPendingResult(this.this$0.getPendingResult());
        this.$it.getReceiver().onReceive(this.$context, this.$intent);
        this.this$0.logger.logBroadcastDispatched(this.$id, this.this$0.action, this.$it.getReceiver());
    }
}
