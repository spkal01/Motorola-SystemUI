package com.android.systemui.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: RingerModeTrackerImpl.kt */
public final class RingerModeLiveData$receiver$1 extends BroadcastReceiver {
    final /* synthetic */ RingerModeLiveData this$0;

    RingerModeLiveData$receiver$1(RingerModeLiveData ringerModeLiveData) {
        this.this$0 = ringerModeLiveData;
    }

    public void onReceive(@NotNull Context context, @NotNull Intent intent) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(intent, "intent");
        this.this$0.initialSticky = isInitialStickyBroadcast();
        this.this$0.postValue(Integer.valueOf(intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1)));
    }
}
