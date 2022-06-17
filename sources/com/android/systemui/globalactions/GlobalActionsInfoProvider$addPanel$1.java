package com.android.systemui.globalactions;

import android.view.View;

/* compiled from: GlobalActionsInfoProvider.kt */
final class GlobalActionsInfoProvider$addPanel$1 implements View.OnClickListener {
    final /* synthetic */ Runnable $dismissParent;
    final /* synthetic */ GlobalActionsInfoProvider this$0;

    GlobalActionsInfoProvider$addPanel$1(Runnable runnable, GlobalActionsInfoProvider globalActionsInfoProvider) {
        this.$dismissParent = runnable;
        this.this$0 = globalActionsInfoProvider;
    }

    public final void onClick(View view) {
        this.$dismissParent.run();
        this.this$0.activityStarter.postStartActivityDismissingKeyguard(this.this$0.pendingIntent);
    }
}
