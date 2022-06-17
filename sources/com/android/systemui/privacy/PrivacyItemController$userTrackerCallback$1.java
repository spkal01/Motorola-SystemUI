package com.android.systemui.privacy;

import android.content.Context;
import android.content.pm.UserInfo;
import com.android.systemui.settings.UserTracker;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: PrivacyItemController.kt */
public final class PrivacyItemController$userTrackerCallback$1 implements UserTracker.Callback {
    final /* synthetic */ PrivacyItemController this$0;

    PrivacyItemController$userTrackerCallback$1(PrivacyItemController privacyItemController) {
        this.this$0 = privacyItemController;
    }

    public void onUserChanged(int i, @NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "userContext");
        this.this$0.update(true);
    }

    public void onProfilesChanged(@NotNull List<? extends UserInfo> list) {
        Intrinsics.checkNotNullParameter(list, "profiles");
        this.this$0.update(true);
    }
}
