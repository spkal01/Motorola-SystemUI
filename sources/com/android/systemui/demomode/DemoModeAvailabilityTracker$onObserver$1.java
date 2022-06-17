package com.android.systemui.demomode;

import android.database.ContentObserver;
import android.os.Handler;

/* compiled from: DemoModeAvailabilityTracker.kt */
public final class DemoModeAvailabilityTracker$onObserver$1 extends ContentObserver {
    final /* synthetic */ DemoModeAvailabilityTracker this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    DemoModeAvailabilityTracker$onObserver$1(DemoModeAvailabilityTracker demoModeAvailabilityTracker, Handler handler) {
        super(handler);
        this.this$0 = demoModeAvailabilityTracker;
    }

    public void onChange(boolean z) {
        boolean access$checkIsDemoModeOn = this.this$0.checkIsDemoModeOn();
        if (this.this$0.isInDemoMode() != access$checkIsDemoModeOn) {
            this.this$0.setInDemoMode(access$checkIsDemoModeOn);
            if (access$checkIsDemoModeOn) {
                this.this$0.onDemoModeStarted();
            } else {
                this.this$0.onDemoModeFinished();
            }
        }
    }
}
