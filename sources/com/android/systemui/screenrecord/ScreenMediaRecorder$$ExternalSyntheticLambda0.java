package com.android.systemui.screenrecord;

import android.media.MediaCodecInfo;
import java.util.function.Predicate;

public final /* synthetic */ class ScreenMediaRecorder$$ExternalSyntheticLambda0 implements Predicate {
    public final /* synthetic */ MediaCodecInfo f$0;

    public /* synthetic */ ScreenMediaRecorder$$ExternalSyntheticLambda0(MediaCodecInfo mediaCodecInfo) {
        this.f$0 = mediaCodecInfo;
    }

    public final boolean test(Object obj) {
        return this.f$0.getName().toLowerCase().startsWith((String) obj);
    }
}
