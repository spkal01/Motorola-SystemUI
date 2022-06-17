package com.android.systemui.tuner;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.systemui.tuner.TunerService;

public final /* synthetic */ class NavBarTuner$$ExternalSyntheticLambda5 implements TunerService.Tunable {
    public final /* synthetic */ NavBarTuner f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ ListPreference f$2;
    public final /* synthetic */ ListPreference f$3;
    public final /* synthetic */ Preference f$4;

    public /* synthetic */ NavBarTuner$$ExternalSyntheticLambda5(NavBarTuner navBarTuner, String str, ListPreference listPreference, ListPreference listPreference2, Preference preference) {
        this.f$0 = navBarTuner;
        this.f$1 = str;
        this.f$2 = listPreference;
        this.f$3 = listPreference2;
        this.f$4 = preference;
    }

    public final void onTuningChanged(String str, String str2) {
        this.f$0.lambda$bindButton$5(this.f$1, this.f$2, this.f$3, this.f$4, str, str2);
    }
}
