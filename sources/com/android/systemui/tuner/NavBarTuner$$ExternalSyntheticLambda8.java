package com.android.systemui.tuner;

import androidx.preference.ListPreference;

public final /* synthetic */ class NavBarTuner$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ ListPreference f$1;

    public /* synthetic */ NavBarTuner$$ExternalSyntheticLambda8(String str, ListPreference listPreference) {
        this.f$0 = str;
        this.f$1 = listPreference;
    }

    public final void run() {
        NavBarTuner.lambda$bindLayout$1(this.f$0, this.f$1);
    }
}
