package com.android.systemui.volume;

import com.android.systemui.volume.VolumeDialogControllerImpl;

/* renamed from: com.android.systemui.volume.VolumeDialogControllerImpl$RingerModeObservers$2$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2163xb7ffa954 implements Runnable {
    public final /* synthetic */ VolumeDialogControllerImpl.RingerModeObservers.C21592 f$0;
    public final /* synthetic */ Integer f$1;

    public /* synthetic */ C2163xb7ffa954(VolumeDialogControllerImpl.RingerModeObservers.C21592 r1, Integer num) {
        this.f$0 = r1;
        this.f$1 = num;
    }

    public final void run() {
        this.f$0.lambda$onChanged$0(this.f$1);
    }
}
