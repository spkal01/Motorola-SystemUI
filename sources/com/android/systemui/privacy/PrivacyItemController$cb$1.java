package com.android.systemui.privacy;

import android.os.UserHandle;
import com.android.systemui.appops.AppOpsController;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: PrivacyItemController.kt */
public final class PrivacyItemController$cb$1 implements AppOpsController.Callback {
    final /* synthetic */ PrivacyItemController this$0;

    PrivacyItemController$cb$1(PrivacyItemController privacyItemController) {
        this.this$0 = privacyItemController;
    }

    public void onActiveStateChanged(int i, int i2, @NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "packageName");
        if (!ArraysKt___ArraysKt.contains(PrivacyItemController.Companion.getOPS_LOCATION(), i) || this.this$0.getLocationAvailable()) {
            if (this.this$0.currentUserIds.contains(Integer.valueOf(UserHandle.getUserId(i2))) || i == 100 || i == 101) {
                this.this$0.logger.logUpdatedItemFromAppOps(i, i2, str, z);
                this.this$0.update(false);
            }
        }
    }
}
