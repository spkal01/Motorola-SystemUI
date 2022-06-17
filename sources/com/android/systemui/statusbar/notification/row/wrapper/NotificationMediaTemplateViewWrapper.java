package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public class NotificationMediaTemplateViewWrapper extends NotificationTemplateViewWrapper {
    private View mActions;

    public boolean shouldClipToRounding(boolean z, boolean z2) {
        return true;
    }

    protected NotificationMediaTemplateViewWrapper(Context context, View view, ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
    }

    private void resolveViews() {
        this.mActions = this.mView.findViewById(16909167);
    }

    public void onContentUpdated(ExpandableNotificationRow expandableNotificationRow) {
        resolveViews();
        super.onContentUpdated(expandableNotificationRow);
    }

    /* access modifiers changed from: protected */
    public void updateTransformedTypes() {
        super.updateTransformedTypes();
        View view = this.mActions;
        if (view != null) {
            this.mTransformationHelper.addTransformedView(5, view);
        }
    }
}
