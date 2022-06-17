package com.android.systemui.settings;

import com.android.systemui.settings.UserTracker;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.settings.UserTrackerImpl$handleProfilesChanged$$inlined$notifySubscribers$1 */
/* compiled from: UserTrackerImpl.kt */
public final class C1388xc58954eb implements Runnable {
    final /* synthetic */ DataItem $it;
    final /* synthetic */ List $profiles$inlined;

    public C1388xc58954eb(DataItem dataItem, List list) {
        this.$it = dataItem;
        this.$profiles$inlined = list;
    }

    public final void run() {
        UserTracker.Callback callback = (UserTracker.Callback) this.$it.getCallback().get();
        if (callback != null) {
            Intrinsics.checkNotNullExpressionValue(this.$profiles$inlined, "profiles");
            callback.onProfilesChanged(this.$profiles$inlined);
        }
    }
}
