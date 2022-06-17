package com.android.systemui.screenrecord;

import android.os.UserHandle;

public final /* synthetic */ class RecordingService$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ RecordingService f$0;
    public final /* synthetic */ UserHandle f$1;

    public /* synthetic */ RecordingService$$ExternalSyntheticLambda1(RecordingService recordingService, UserHandle userHandle) {
        this.f$0 = recordingService;
        this.f$1 = userHandle;
    }

    public final void run() {
        this.f$0.lambda$saveRecording$1(this.f$1);
    }
}
