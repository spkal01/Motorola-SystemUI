package com.android.systemui.statusbar.notification.collection.coalescer;

public final /* synthetic */ class GroupCoalescer$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ GroupCoalescer f$0;
    public final /* synthetic */ EventBatch f$1;

    public /* synthetic */ GroupCoalescer$$ExternalSyntheticLambda0(GroupCoalescer groupCoalescer, EventBatch eventBatch) {
        this.f$0 = groupCoalescer;
        this.f$1 = eventBatch;
    }

    public final void run() {
        this.f$0.lambda$resetShortTimeout$0(this.f$1);
    }
}
