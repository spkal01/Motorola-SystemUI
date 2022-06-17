package com.android.systemui.util;

import android.media.AudioManager;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

/* compiled from: RingerModeTrackerImpl.kt */
/* synthetic */ class RingerModeTrackerImpl$ringerMode$1 extends FunctionReferenceImpl implements Function0<Integer> {
    RingerModeTrackerImpl$ringerMode$1(AudioManager audioManager) {
        super(0, audioManager, AudioManager.class, "getRingerMode", "getRingerMode()I", 0);
    }

    public final int invoke() {
        return ((AudioManager) this.receiver).getRingerMode();
    }
}
