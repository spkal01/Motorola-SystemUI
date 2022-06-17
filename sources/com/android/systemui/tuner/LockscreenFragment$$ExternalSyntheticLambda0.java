package com.android.systemui.tuner;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.systemui.tuner.TunerService;

public final /* synthetic */ class LockscreenFragment$$ExternalSyntheticLambda0 implements TunerService.Tunable {
    public final /* synthetic */ LockscreenFragment f$0;
    public final /* synthetic */ SwitchPreference f$1;
    public final /* synthetic */ Preference f$2;

    public /* synthetic */ LockscreenFragment$$ExternalSyntheticLambda0(LockscreenFragment lockscreenFragment, SwitchPreference switchPreference, Preference preference) {
        this.f$0 = lockscreenFragment;
        this.f$1 = switchPreference;
        this.f$2 = preference;
    }

    public final void onTuningChanged(String str, String str2) {
        this.f$0.lambda$setupGroup$1(this.f$1, this.f$2, str, str2);
    }
}
