package com.android.systemui.settings;

import com.android.systemui.settings.UserTracker;
import java.util.function.Predicate;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: UserTrackerImpl.kt */
final class UserTrackerImpl$removeCallback$1$1 implements Predicate<DataItem> {
    final /* synthetic */ UserTracker.Callback $callback;

    UserTrackerImpl$removeCallback$1$1(UserTracker.Callback callback) {
        this.$callback = callback;
    }

    public final boolean test(@NotNull DataItem dataItem) {
        Intrinsics.checkNotNullParameter(dataItem, "it");
        return dataItem.sameOrEmpty(this.$callback);
    }
}
