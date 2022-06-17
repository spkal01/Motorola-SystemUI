package com.android.systemui.statusbar.phone.ongoingcall;

import android.app.IUidObserver;
import com.android.systemui.statusbar.phone.ongoingcall.OngoingCallController;

/* compiled from: OngoingCallController.kt */
public final class OngoingCallController$setUpUidObserver$1 extends IUidObserver.Stub {
    final /* synthetic */ OngoingCallController.CallNotificationInfo $currentCallNotificationInfo;
    final /* synthetic */ OngoingCallController this$0;

    public void onUidActive(int i) {
    }

    public void onUidCachedChanged(int i, boolean z) {
    }

    public void onUidGone(int i, boolean z) {
    }

    public void onUidIdle(int i, boolean z) {
    }

    OngoingCallController$setUpUidObserver$1(OngoingCallController.CallNotificationInfo callNotificationInfo, OngoingCallController ongoingCallController) {
        this.$currentCallNotificationInfo = callNotificationInfo;
        this.this$0 = ongoingCallController;
    }

    public void onUidStateChanged(int i, int i2, long j, int i3) {
        if (i == this.$currentCallNotificationInfo.getUid()) {
            boolean access$isCallAppVisible$p = this.this$0.isCallAppVisible;
            OngoingCallController ongoingCallController = this.this$0;
            ongoingCallController.isCallAppVisible = ongoingCallController.isProcessVisibleToUser(i2);
            if (access$isCallAppVisible$p != this.this$0.isCallAppVisible) {
                this.this$0.mainExecutor.execute(new OngoingCallController$setUpUidObserver$1$onUidStateChanged$1(this.this$0));
            }
        }
    }
}
