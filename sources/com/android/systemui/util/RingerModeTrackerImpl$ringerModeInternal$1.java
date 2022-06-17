package com.android.systemui.util;

import android.media.AudioManager;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

/* compiled from: RingerModeTrackerImpl.kt */
/* synthetic */ class RingerModeTrackerImpl$ringerModeInternal$1 extends FunctionReferenceImpl implements Function0<Integer> {
    RingerModeTrackerImpl$ringerModeInternal$1(AudioManager audioManager) {
        super(0, audioManager, AudioManager.class, "getRingerModeInternal", "getRingerModeInternal()I", 0);
    }

    public final int invoke() {
        return ((AudioManager) this.receiver).getRingerModeInternal();
    }
}
