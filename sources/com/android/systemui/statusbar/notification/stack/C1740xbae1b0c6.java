package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.KeyguardBypassController;

/* renamed from: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController$$ExternalSyntheticLambda7 */
public final /* synthetic */ class C1740xbae1b0c6 implements NotificationStackScrollLayout.KeyguardBypassEnabledProvider {
    public final /* synthetic */ KeyguardBypassController f$0;

    public /* synthetic */ C1740xbae1b0c6(KeyguardBypassController keyguardBypassController) {
        this.f$0 = keyguardBypassController;
    }

    public final boolean getBypassEnabled() {
        return this.f$0.getBypassEnabled();
    }
}
