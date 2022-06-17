package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.function.Consumer;

public final /* synthetic */ class HeadsUpAppearanceController$$ExternalSyntheticLambda5 implements Consumer {
    public final /* synthetic */ HeadsUpAppearanceController f$0;

    public /* synthetic */ HeadsUpAppearanceController$$ExternalSyntheticLambda5(HeadsUpAppearanceController headsUpAppearanceController) {
        this.f$0 = headsUpAppearanceController;
    }

    public final void accept(Object obj) {
        this.f$0.setTrackingHeadsUp((ExpandableNotificationRow) obj);
    }
}
