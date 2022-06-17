package com.android.systemui.statusbar.events;

import com.android.systemui.R$string;
import com.android.systemui.privacy.PrivacyChipBuilder;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: SystemEventCoordinator.kt */
final class SystemEventCoordinator$notifyPrivacyItemsChanged$1 extends Lambda implements Function0<String> {
    final /* synthetic */ PrivacyEvent $event;
    final /* synthetic */ SystemEventCoordinator this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SystemEventCoordinator$notifyPrivacyItemsChanged$1(SystemEventCoordinator systemEventCoordinator, PrivacyEvent privacyEvent) {
        super(0);
        this.this$0 = systemEventCoordinator;
        this.$event = privacyEvent;
    }

    public final String invoke() {
        String joinTypes = new PrivacyChipBuilder(this.this$0.context, this.$event.getPrivacyItems()).joinTypes();
        return this.this$0.context.getString(R$string.ongoing_privacy_chip_content_multiple_apps, new Object[]{joinTypes});
    }
}
