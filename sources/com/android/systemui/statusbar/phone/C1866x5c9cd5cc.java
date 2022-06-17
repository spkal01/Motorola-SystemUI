package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.phone.NotificationPanelViewController;

/* renamed from: com.android.systemui.statusbar.phone.NotificationPanelViewController$OnOverscrollTopChangedListener$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1866x5c9cd5cc implements Runnable {
    public final /* synthetic */ NotificationPanelViewController.OnOverscrollTopChangedListener f$0;

    public /* synthetic */ C1866x5c9cd5cc(NotificationPanelViewController.OnOverscrollTopChangedListener onOverscrollTopChangedListener) {
        this.f$0 = onOverscrollTopChangedListener;
    }

    public final void run() {
        this.f$0.lambda$flingTopOverscroll$0();
    }
}
