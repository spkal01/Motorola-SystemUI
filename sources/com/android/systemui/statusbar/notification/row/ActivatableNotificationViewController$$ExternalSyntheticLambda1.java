package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.phone.NotificationTapHelper;

public final /* synthetic */ class ActivatableNotificationViewController$$ExternalSyntheticLambda1 implements NotificationTapHelper.DoubleTapListener {
    public final /* synthetic */ ActivatableNotificationView f$0;

    public /* synthetic */ ActivatableNotificationViewController$$ExternalSyntheticLambda1(ActivatableNotificationView activatableNotificationView) {
        this.f$0 = activatableNotificationView;
    }

    public final boolean onDoubleTap() {
        return this.f$0.performClick();
    }
}
