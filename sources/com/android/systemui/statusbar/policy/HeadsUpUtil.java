package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.view.View;
import com.android.systemui.R$id;
import com.android.systemui.moto.MotoFeature;

public final class HeadsUpUtil {
    private static final int TAG_CLICKED_NOTIFICATION = R$id.is_clicked_heads_up_tag;

    public static void setNeedsHeadsUpDisappearAnimationAfterClick(View view, boolean z) {
        view.setTag(TAG_CLICKED_NOTIFICATION, z ? Boolean.TRUE : null);
    }

    public static boolean isClickedHeadsUpNotification(View view) {
        Boolean bool = (Boolean) view.getTag(TAG_CLICKED_NOTIFICATION);
        return bool != null && bool.booleanValue();
    }

    public static boolean isCliHeadsUpNotification(StatusBarNotification statusBarNotification) {
        if (statusBarNotification == null || statusBarNotification.getNotification().actions == null || statusBarNotification.getNotification().actions.length <= 0) {
            return false;
        }
        return true;
    }

    public static boolean shouldCliHeadsUpForLock(Context context, StatusBarNotification statusBarNotification) {
        MotoFeature motoFeature;
        MotoFeature.getExistedInstance();
        if (context == null) {
            motoFeature = MotoFeature.getExistedInstance();
        } else {
            motoFeature = MotoFeature.getInstance(context);
        }
        if (!motoFeature.isLidClosed()) {
            return false;
        }
        if (statusBarNotification.getNotification().fullScreenIntent != null) {
            String packageName = statusBarNotification.getPackageName();
            String str = null;
            Intent intent = statusBarNotification.getNotification().fullScreenIntent.getIntent();
            if (!(intent == null || intent.getComponent() == null)) {
                str = intent.getComponent().getClassName();
            }
            if (motoFeature.isAllowedFullScreenOnCLI(packageName, str)) {
                return false;
            }
        }
        return true;
    }
}
