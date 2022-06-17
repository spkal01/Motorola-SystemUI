package com.android.systemui.statusbar.notification.stack;

public final /* synthetic */ class NotificationStackScrollLayout$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ NotificationStackScrollLayout f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ NotificationStackScrollLayout$$ExternalSyntheticLambda9(NotificationStackScrollLayout notificationStackScrollLayout, boolean z, Runnable runnable) {
        this.f$0 = notificationStackScrollLayout;
        this.f$1 = z;
        this.f$2 = runnable;
    }

    public final void run() {
        this.f$0.lambda$performDismissAllAnimations$7(this.f$1, this.f$2);
    }
}
