package com.android.systemui.settings;

import com.android.systemui.settings.UserTracker;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: UserTrackerImpl.kt */
final class DataItem {
    @NotNull
    private final WeakReference<UserTracker.Callback> callback;
    @NotNull
    private final Executor executor;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DataItem)) {
            return false;
        }
        DataItem dataItem = (DataItem) obj;
        return Intrinsics.areEqual((Object) this.callback, (Object) dataItem.callback) && Intrinsics.areEqual((Object) this.executor, (Object) dataItem.executor);
    }

    public int hashCode() {
        return (this.callback.hashCode() * 31) + this.executor.hashCode();
    }

    @NotNull
    public String toString() {
        return "DataItem(callback=" + this.callback + ", executor=" + this.executor + ')';
    }

    public DataItem(@NotNull WeakReference<UserTracker.Callback> weakReference, @NotNull Executor executor2) {
        Intrinsics.checkNotNullParameter(weakReference, "callback");
        Intrinsics.checkNotNullParameter(executor2, "executor");
        this.callback = weakReference;
        this.executor = executor2;
    }

    @NotNull
    public final WeakReference<UserTracker.Callback> getCallback() {
        return this.callback;
    }

    @NotNull
    public final Executor getExecutor() {
        return this.executor;
    }

    public final boolean sameOrEmpty(@NotNull UserTracker.Callback callback2) {
        Intrinsics.checkNotNullParameter(callback2, "other");
        UserTracker.Callback callback3 = (UserTracker.Callback) this.callback.get();
        if (callback3 == null) {
            return true;
        }
        return callback3.equals(callback2);
    }
}
