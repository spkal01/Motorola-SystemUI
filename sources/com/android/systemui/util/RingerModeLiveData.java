package com.android.systemui.util;

import android.content.IntentFilter;
import android.os.UserHandle;
import androidx.lifecycle.MutableLiveData;
import com.android.systemui.broadcast.BroadcastDispatcher;
import java.util.concurrent.Executor;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: RingerModeTrackerImpl.kt */
public final class RingerModeLiveData extends MutableLiveData<Integer> {
    @NotNull
    private final BroadcastDispatcher broadcastDispatcher;
    @NotNull
    private final Executor executor;
    @NotNull
    private final IntentFilter filter;
    /* access modifiers changed from: private */
    @NotNull
    public final Function0<Integer> getter;
    /* access modifiers changed from: private */
    public boolean initialSticky;
    @NotNull
    private final RingerModeLiveData$receiver$1 receiver = new RingerModeLiveData$receiver$1(this);

    public RingerModeLiveData(@NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull Executor executor2, @NotNull String str, @NotNull Function0<Integer> function0) {
        Intrinsics.checkNotNullParameter(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(executor2, "executor");
        Intrinsics.checkNotNullParameter(str, "intent");
        Intrinsics.checkNotNullParameter(function0, "getter");
        this.broadcastDispatcher = broadcastDispatcher2;
        this.executor = executor2;
        this.getter = function0;
        this.filter = new IntentFilter(str);
    }

    public final boolean getInitialSticky() {
        return this.initialSticky;
    }

    @NotNull
    public Integer getValue() {
        Integer num = (Integer) super.getValue();
        return Integer.valueOf(num == null ? -1 : num.intValue());
    }

    /* access modifiers changed from: protected */
    public void onActive() {
        super.onActive();
        this.broadcastDispatcher.registerReceiver(this.receiver, this.filter, this.executor, UserHandle.ALL);
        this.executor.execute(new RingerModeLiveData$onActive$1(this));
    }

    /* access modifiers changed from: protected */
    public void onInactive() {
        super.onInactive();
        this.broadcastDispatcher.unregisterReceiver(this.receiver);
    }
}
