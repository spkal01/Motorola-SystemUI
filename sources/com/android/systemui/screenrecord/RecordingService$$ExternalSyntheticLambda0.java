package com.android.systemui.screenrecord;

import android.content.Intent;
import android.os.UserHandle;
import com.android.systemui.plugins.ActivityStarter;

public final /* synthetic */ class RecordingService$$ExternalSyntheticLambda0 implements ActivityStarter.OnDismissAction {
    public final /* synthetic */ RecordingService f$0;
    public final /* synthetic */ Intent f$1;
    public final /* synthetic */ UserHandle f$2;

    public /* synthetic */ RecordingService$$ExternalSyntheticLambda0(RecordingService recordingService, Intent intent, UserHandle userHandle) {
        this.f$0 = recordingService;
        this.f$1 = intent;
        this.f$2 = userHandle;
    }

    public final boolean onDismiss() {
        return this.f$0.lambda$onStartCommand$0(this.f$1, this.f$2);
    }
}
