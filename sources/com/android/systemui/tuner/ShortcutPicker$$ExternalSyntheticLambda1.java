package com.android.systemui.tuner;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import java.util.function.Consumer;

public final /* synthetic */ class ShortcutPicker$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ ShortcutPicker f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ PreferenceScreen f$2;
    public final /* synthetic */ PreferenceCategory f$3;

    public /* synthetic */ ShortcutPicker$$ExternalSyntheticLambda1(ShortcutPicker shortcutPicker, Context context, PreferenceScreen preferenceScreen, PreferenceCategory preferenceCategory) {
        this.f$0 = shortcutPicker;
        this.f$1 = context;
        this.f$2 = preferenceScreen;
        this.f$3 = preferenceCategory;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onCreatePreferences$1(this.f$1, this.f$2, this.f$3, (LauncherActivityInfo) obj);
    }
}
