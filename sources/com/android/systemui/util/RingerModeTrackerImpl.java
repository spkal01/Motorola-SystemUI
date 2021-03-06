package com.android.systemui.util;

import android.media.AudioManager;
import androidx.lifecycle.LiveData;
import com.android.systemui.broadcast.BroadcastDispatcher;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: RingerModeTrackerImpl.kt */
public final class RingerModeTrackerImpl implements RingerModeTracker {
    @NotNull
    private final LiveData<Integer> ringerMode;
    @NotNull
    private final LiveData<Integer> ringerModeInternal;

    public RingerModeTrackerImpl(@NotNull AudioManager audioManager, @NotNull BroadcastDispatcher broadcastDispatcher, @NotNull Executor executor) {
        Intrinsics.checkNotNullParameter(audioManager, "audioManager");
        Intrinsics.checkNotNullParameter(broadcastDispatcher, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(executor, "executor");
        this.ringerMode = new RingerModeLiveData(broadcastDispatcher, executor, "android.media.RINGER_MODE_CHANGED", new RingerModeTrackerImpl$ringerMode$1(audioManager));
        this.ringerModeInternal = new RingerModeLiveData(broadcastDispatcher, executor, "android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION", new RingerModeTrackerImpl$ringerModeInternal$1(audioManager));
    }

    @NotNull
    public LiveData<Integer> getRingerMode() {
        return this.ringerMode;
    }

    @NotNull
    public LiveData<Integer> getRingerModeInternal() {
        return this.ringerModeInternal;
    }
}
