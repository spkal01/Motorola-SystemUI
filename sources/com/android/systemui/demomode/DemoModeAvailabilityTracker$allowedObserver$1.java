package com.android.systemui.demomode;

import android.database.ContentObserver;
import android.os.Handler;

/* compiled from: DemoModeAvailabilityTracker.kt */
public final class DemoModeAvailabilityTracker$allowedObserver$1 extends ContentObserver {
    final /* synthetic */ DemoModeAvailabilityTracker this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    DemoModeAvailabilityTracker$allowedObserver$1(DemoModeAvailabilityTracker demoModeAvailabilityTracker, Handler handler) {
        super(handler);
        this.this$0 = demoModeAvailabilityTracker;
    }

    public void onChange(boolean z) {
        boolean access$checkIsDemoModeAllowed = this.this$0.checkIsDemoModeAllowed();
        if (this.this$0.isDemoModeAvailable() != access$checkIsDemoModeAllowed) {
            this.this$0.setDemoModeAvailable(access$checkIsDemoModeAllowed);
            this.this$0.onDemoModeAvailabilityChanged();
        }
    }
}
