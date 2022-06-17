package com.android.systemui.statusbar.lockscreen;

import android.content.Context;
import android.content.pm.UserInfo;
import com.android.systemui.settings.UserTracker;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LockscreenSmartspaceController.kt */
public final class LockscreenSmartspaceController$userTrackerCallback$1 implements UserTracker.Callback {
    final /* synthetic */ LockscreenSmartspaceController this$0;

    public void onProfilesChanged(@NotNull List<? extends UserInfo> list) {
        Intrinsics.checkNotNullParameter(list, "profiles");
    }

    LockscreenSmartspaceController$userTrackerCallback$1(LockscreenSmartspaceController lockscreenSmartspaceController) {
        this.this$0 = lockscreenSmartspaceController;
    }

    public void onUserChanged(int i, @NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "userContext");
        this.this$0.execution.assertIsMainThread();
        this.this$0.reloadSmartspace();
    }
}
