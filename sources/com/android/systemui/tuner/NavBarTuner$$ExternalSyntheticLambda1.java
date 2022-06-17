package com.android.systemui.tuner;

import androidx.preference.ListPreference;
import androidx.preference.Preference;

public final /* synthetic */ class NavBarTuner$$ExternalSyntheticLambda1 implements Preference.OnPreferenceChangeListener {
    public final /* synthetic */ NavBarTuner f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ ListPreference f$2;
    public final /* synthetic */ Preference f$3;
    public final /* synthetic */ ListPreference f$4;

    public /* synthetic */ NavBarTuner$$ExternalSyntheticLambda1(NavBarTuner navBarTuner, String str, ListPreference listPreference, Preference preference, ListPreference listPreference2) {
        this.f$0 = navBarTuner;
        this.f$1 = str;
        this.f$2 = listPreference;
        this.f$3 = preference;
        this.f$4 = listPreference2;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        return this.f$0.lambda$bindButton$7(this.f$1, this.f$2, this.f$3, this.f$4, preference, obj);
    }
}
