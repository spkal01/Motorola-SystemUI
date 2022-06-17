package com.android.systemui.statusbar.notification.collection.coordinator;

public final /* synthetic */ class VisualStabilityCoordinator$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ VisualStabilityCoordinator f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ VisualStabilityCoordinator$$ExternalSyntheticLambda0(VisualStabilityCoordinator visualStabilityCoordinator, String str) {
        this.f$0 = visualStabilityCoordinator;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.lambda$temporarilyAllowSectionChanges$0(this.f$1);
    }
}
