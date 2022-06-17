package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public class NotificationDecoratedCustomViewWrapper extends NotificationTemplateViewWrapper {
    private View mWrappedView = null;

    public static boolean hasCustomView(View view) {
        return getWrappedCustomView(view) != null;
    }

    private static View getWrappedCustomView(View view) {
        ViewGroup viewGroup;
        Integer num;
        if (view == null || (viewGroup = (ViewGroup) view.findViewById(16909239)) == null || (num = (Integer) viewGroup.getTag(16909235)) == null || num.intValue() == -1) {
            return null;
        }
        return viewGroup.getChildAt(num.intValue());
    }

    protected NotificationDecoratedCustomViewWrapper(Context context, View view, ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
    }

    public void onContentUpdated(ExpandableNotificationRow expandableNotificationRow) {
        this.mWrappedView = getWrappedCustomView(this.mView);
        if (needsInversion(resolveBackgroundColor(), this.mWrappedView)) {
            invertViewLuminosity(this.mWrappedView);
        }
        super.onContentUpdated(expandableNotificationRow);
    }
}
