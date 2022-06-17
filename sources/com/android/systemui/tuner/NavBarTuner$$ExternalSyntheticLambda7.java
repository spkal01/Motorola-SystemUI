package com.android.systemui.tuner;

import androidx.preference.ListPreference;
import androidx.preference.Preference;

public final /* synthetic */ class NavBarTuner$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ NavBarTuner f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ ListPreference f$3;
    public final /* synthetic */ ListPreference f$4;
    public final /* synthetic */ Preference f$5;

    public /* synthetic */ NavBarTuner$$ExternalSyntheticLambda7(NavBarTuner navBarTuner, String str, String str2, ListPreference listPreference, ListPreference listPreference2, Preference preference) {
        this.f$0 = navBarTuner;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = listPreference;
        this.f$4 = listPreference2;
        this.f$5 = preference;
    }

    public final void run() {
        this.f$0.lambda$bindButton$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
