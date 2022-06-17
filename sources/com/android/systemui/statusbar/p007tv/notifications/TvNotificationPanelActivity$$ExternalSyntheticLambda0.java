package com.android.systemui.statusbar.p007tv.notifications;

import java.util.function.Consumer;

/* renamed from: com.android.systemui.statusbar.tv.notifications.TvNotificationPanelActivity$$ExternalSyntheticLambda0 */
public final /* synthetic */ class TvNotificationPanelActivity$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ TvNotificationPanelActivity f$0;

    public /* synthetic */ TvNotificationPanelActivity$$ExternalSyntheticLambda0(TvNotificationPanelActivity tvNotificationPanelActivity) {
        this.f$0 = tvNotificationPanelActivity;
    }

    public final void accept(Object obj) {
        this.f$0.enableBlur(((Boolean) obj).booleanValue());
    }
}
