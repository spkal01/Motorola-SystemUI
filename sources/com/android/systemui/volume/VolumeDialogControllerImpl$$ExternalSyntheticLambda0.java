package com.android.systemui.volume;

import android.os.VibrationEffect;
import android.os.Vibrator;
import java.util.function.Consumer;

public final /* synthetic */ class VolumeDialogControllerImpl$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ VibrationEffect f$0;

    public /* synthetic */ VolumeDialogControllerImpl$$ExternalSyntheticLambda0(VibrationEffect vibrationEffect) {
        this.f$0 = vibrationEffect;
    }

    public final void accept(Object obj) {
        ((Vibrator) obj).vibrate(this.f$0, VolumeDialogControllerImpl.SONIFICIATION_VIBRATION_ATTRIBUTES);
    }
}
