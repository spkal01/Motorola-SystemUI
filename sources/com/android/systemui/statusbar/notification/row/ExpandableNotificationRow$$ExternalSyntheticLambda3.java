package com.android.systemui.statusbar.notification.row;

import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public final /* synthetic */ class ExpandableNotificationRow$$ExternalSyntheticLambda3 implements View.OnClickListener {
    public final /* synthetic */ ExpandableNotificationRow f$0;
    public final /* synthetic */ ExpandableNotificationRow.CoordinateOnClickListener f$1;

    public /* synthetic */ ExpandableNotificationRow$$ExternalSyntheticLambda3(ExpandableNotificationRow expandableNotificationRow, ExpandableNotificationRow.CoordinateOnClickListener coordinateOnClickListener) {
        this.f$0 = expandableNotificationRow;
        this.f$1 = coordinateOnClickListener;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setOnFeedbackClickListener$3(this.f$1, view);
    }
}
