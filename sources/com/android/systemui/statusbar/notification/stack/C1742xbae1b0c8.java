package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.tuner.TunerService;

/* renamed from: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController$$ExternalSyntheticLambda9 */
public final /* synthetic */ class C1742xbae1b0c8 implements TunerService.Tunable {
    public final /* synthetic */ NotificationStackScrollLayoutController f$0;

    public /* synthetic */ C1742xbae1b0c8(NotificationStackScrollLayoutController notificationStackScrollLayoutController) {
        this.f$0 = notificationStackScrollLayoutController;
    }

    public final void onTuningChanged(String str, String str2) {
        this.f$0.lambda$attach$5(str, str2);
    }
}
