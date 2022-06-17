package com.android.systemui.privacy;

import android.content.pm.UserInfo;
import java.util.ArrayList;
import java.util.List;

/* compiled from: PrivacyItemController.kt */
final class PrivacyItemController$update$1 implements Runnable {
    final /* synthetic */ boolean $updateUsers;
    final /* synthetic */ PrivacyItemController this$0;

    PrivacyItemController$update$1(boolean z, PrivacyItemController privacyItemController) {
        this.$updateUsers = z;
        this.this$0 = privacyItemController;
    }

    public final void run() {
        if (this.$updateUsers) {
            PrivacyItemController privacyItemController = this.this$0;
            List<UserInfo> userProfiles = privacyItemController.userTracker.getUserProfiles();
            ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(userProfiles, 10));
            for (UserInfo userInfo : userProfiles) {
                arrayList.add(Integer.valueOf(userInfo.id));
            }
            privacyItemController.currentUserIds = arrayList;
            this.this$0.logger.logCurrentProfilesChanged(this.this$0.currentUserIds);
        }
        this.this$0.updateListAndNotifyChanges.run();
    }
}
