package com.android.systemui.tuner;

import android.content.DialogInterface;
import android.widget.EditText;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

public final /* synthetic */ class NavBarTuner$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ NavBarTuner f$0;
    public final /* synthetic */ EditText f$1;
    public final /* synthetic */ Preference f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ ListPreference f$4;
    public final /* synthetic */ ListPreference f$5;

    public /* synthetic */ NavBarTuner$$ExternalSyntheticLambda0(NavBarTuner navBarTuner, EditText editText, Preference preference, String str, ListPreference listPreference, ListPreference listPreference2) {
        this.f$0 = navBarTuner;
        this.f$1 = editText;
        this.f$2 = preference;
        this.f$3 = str;
        this.f$4 = listPreference;
        this.f$5 = listPreference2;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$bindButton$8(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
