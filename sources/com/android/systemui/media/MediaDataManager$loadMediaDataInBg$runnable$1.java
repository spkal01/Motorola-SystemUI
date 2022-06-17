package com.android.systemui.media;

import android.app.Notification;
import android.app.PendingIntent;
import com.android.systemui.plugins.ActivityStarter;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MediaDataManager.kt */
final class MediaDataManager$loadMediaDataInBg$runnable$1 implements Runnable {
    final /* synthetic */ Notification.Action $action;
    final /* synthetic */ MediaDataManager this$0;

    MediaDataManager$loadMediaDataInBg$runnable$1(Notification.Action action, MediaDataManager mediaDataManager) {
        this.$action = action;
        this.this$0 = mediaDataManager;
    }

    public final void run() {
        if (this.$action.isAuthenticationRequired()) {
            ActivityStarter access$getActivityStarter$p = this.this$0.activityStarter;
            final MediaDataManager mediaDataManager = this.this$0;
            final Notification.Action action = this.$action;
            access$getActivityStarter$p.dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() {
                public final boolean onDismiss() {
                    MediaDataManager mediaDataManager = mediaDataManager;
                    PendingIntent pendingIntent = action.actionIntent;
                    Intrinsics.checkNotNullExpressionValue(pendingIntent, "action.actionIntent");
                    return mediaDataManager.sendPendingIntent(pendingIntent);
                }
            }, C10202.INSTANCE, true);
            return;
        }
        MediaDataManager mediaDataManager2 = this.this$0;
        PendingIntent pendingIntent = this.$action.actionIntent;
        Intrinsics.checkNotNullExpressionValue(pendingIntent, "action.actionIntent");
        boolean unused = mediaDataManager2.sendPendingIntent(pendingIntent);
    }
}
