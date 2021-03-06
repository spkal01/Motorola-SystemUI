package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.statusbar.notification.ImageTransformState;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public class NotificationBigPictureTemplateViewWrapper extends NotificationTemplateViewWrapper {
    protected NotificationBigPictureTemplateViewWrapper(Context context, View view, ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
    }

    public void onContentUpdated(ExpandableNotificationRow expandableNotificationRow) {
        super.onContentUpdated(expandableNotificationRow);
        updateImageTag(expandableNotificationRow.getEntry().getSbn());
    }

    private void updateImageTag(StatusBarNotification statusBarNotification) {
        Bundle bundle = statusBarNotification.getNotification().extras;
        if (bundle.containsKey("android.largeIcon.big")) {
            Icon icon = (Icon) bundle.getParcelable("android.largeIcon.big");
            ImageView imageView = this.mRightIcon;
            int i = ImageTransformState.ICON_TAG;
            imageView.setTag(i, icon);
            this.mLeftIcon.setTag(i, icon);
            return;
        }
        this.mRightIcon.setTag(ImageTransformState.ICON_TAG, getLargeIcon(statusBarNotification.getNotification()));
    }
}
