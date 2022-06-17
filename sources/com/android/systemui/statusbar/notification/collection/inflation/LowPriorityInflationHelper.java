package com.android.systemui.statusbar.notification.collection.inflation;

import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.collection.GroupEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;

public class LowPriorityInflationHelper {
    private final FeatureFlags mFeatureFlags;
    private final NotificationGroupManagerLegacy mGroupManager;
    private final RowContentBindStage mRowContentBindStage;

    LowPriorityInflationHelper(FeatureFlags featureFlags, NotificationGroupManagerLegacy notificationGroupManagerLegacy, RowContentBindStage rowContentBindStage) {
        this.mFeatureFlags = featureFlags;
        this.mGroupManager = notificationGroupManagerLegacy;
        this.mRowContentBindStage = rowContentBindStage;
    }

    public void recheckLowPriorityViewAndInflate(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow) {
        RowContentBindParams rowContentBindParams = (RowContentBindParams) this.mRowContentBindStage.getStageParams(notificationEntry);
        boolean shouldUseLowPriorityView = shouldUseLowPriorityView(notificationEntry);
        if (!expandableNotificationRow.isRemoved() && expandableNotificationRow.isLowPriority() != shouldUseLowPriorityView) {
            rowContentBindParams.setUseLowPriority(shouldUseLowPriorityView);
            this.mRowContentBindStage.requestRebind(notificationEntry, new LowPriorityInflationHelper$$ExternalSyntheticLambda0(expandableNotificationRow, shouldUseLowPriorityView));
        }
    }

    public boolean shouldUseLowPriorityView(NotificationEntry notificationEntry) {
        boolean z;
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            z = notificationEntry.getParent() != GroupEntry.ROOT_ENTRY;
        } else {
            z = this.mGroupManager.isChildInGroup(notificationEntry);
        }
        if (!notificationEntry.isAmbient() || z) {
            return false;
        }
        return true;
    }
}
