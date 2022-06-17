package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.phone.NotificationTapHelper;

public final /* synthetic */ class ActivatableNotificationViewController$$ExternalSyntheticLambda2 implements NotificationTapHelper.SlideBackListener {
    public final /* synthetic */ ActivatableNotificationView f$0;

    public /* synthetic */ ActivatableNotificationViewController$$ExternalSyntheticLambda2(ActivatableNotificationView activatableNotificationView) {
        this.f$0 = activatableNotificationView;
    }

    public final boolean onSlideBack() {
        return this.f$0.handleSlideBack();
    }
}
