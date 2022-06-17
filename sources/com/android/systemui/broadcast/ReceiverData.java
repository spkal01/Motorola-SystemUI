package com.android.systemui.broadcast;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.UserHandle;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: BroadcastDispatcher.kt */
public final class ReceiverData {
    @NotNull
    private final Executor executor;
    @NotNull
    private final IntentFilter filter;
    @NotNull
    private final BroadcastReceiver receiver;
    @NotNull
    private final UserHandle user;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ReceiverData)) {
            return false;
        }
        ReceiverData receiverData = (ReceiverData) obj;
        return Intrinsics.areEqual((Object) this.receiver, (Object) receiverData.receiver) && Intrinsics.areEqual((Object) this.filter, (Object) receiverData.filter) && Intrinsics.areEqual((Object) this.executor, (Object) receiverData.executor) && Intrinsics.areEqual((Object) this.user, (Object) receiverData.user);
    }

    public int hashCode() {
        return (((((this.receiver.hashCode() * 31) + this.filter.hashCode()) * 31) + this.executor.hashCode()) * 31) + this.user.hashCode();
    }

    @NotNull
    public String toString() {
        return "ReceiverData(receiver=" + this.receiver + ", filter=" + this.filter + ", executor=" + this.executor + ", user=" + this.user + ')';
    }

    public ReceiverData(@NotNull BroadcastReceiver broadcastReceiver, @NotNull IntentFilter intentFilter, @NotNull Executor executor2, @NotNull UserHandle userHandle) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        Intrinsics.checkNotNullParameter(intentFilter, "filter");
        Intrinsics.checkNotNullParameter(executor2, "executor");
        Intrinsics.checkNotNullParameter(userHandle, "user");
        this.receiver = broadcastReceiver;
        this.filter = intentFilter;
        this.executor = executor2;
        this.user = userHandle;
    }

    @NotNull
    public final BroadcastReceiver getReceiver() {
        return this.receiver;
    }

    @NotNull
    public final IntentFilter getFilter() {
        return this.filter;
    }

    @NotNull
    public final Executor getExecutor() {
        return this.executor;
    }

    @NotNull
    public final UserHandle getUser() {
        return this.user;
    }
}
