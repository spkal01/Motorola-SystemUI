package com.android.systemui.broadcast;

import android.content.Context;
import android.content.Intent;
import android.util.ArraySet;

/* compiled from: ActionReceiver.kt */
final class ActionReceiver$onReceive$1 implements Runnable {
    final /* synthetic */ Context $context;
    final /* synthetic */ int $id;
    final /* synthetic */ Intent $intent;
    final /* synthetic */ ActionReceiver this$0;

    ActionReceiver$onReceive$1(ActionReceiver actionReceiver, Intent intent, Context context, int i) {
        this.this$0 = actionReceiver;
        this.$intent = intent;
        this.$context = context;
        this.$id = i;
    }

    public final void run() {
        ArraySet<ReceiverData> access$getReceiverDatas$p = this.this$0.receiverDatas;
        Intent intent = this.$intent;
        ActionReceiver actionReceiver = this.this$0;
        Context context = this.$context;
        int i = this.$id;
        for (ReceiverData receiverData : access$getReceiverDatas$p) {
            if (receiverData.getFilter().matchCategories(intent.getCategories()) == null) {
                receiverData.getExecutor().execute(new ActionReceiver$onReceive$1$1$1(receiverData, actionReceiver, context, intent, i));
            }
        }
    }
}
