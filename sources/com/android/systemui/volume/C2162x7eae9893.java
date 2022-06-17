package com.android.systemui.volume;

import com.android.systemui.volume.VolumeDialogControllerImpl;

/* renamed from: com.android.systemui.volume.VolumeDialogControllerImpl$RingerModeObservers$1$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2162x7eae9893 implements Runnable {
    public final /* synthetic */ VolumeDialogControllerImpl.RingerModeObservers.C21581 f$0;
    public final /* synthetic */ Integer f$1;

    public /* synthetic */ C2162x7eae9893(VolumeDialogControllerImpl.RingerModeObservers.C21581 r1, Integer num) {
        this.f$0 = r1;
        this.f$1 = num;
    }

    public final void run() {
        this.f$0.lambda$onChanged$0(this.f$1);
    }
}
