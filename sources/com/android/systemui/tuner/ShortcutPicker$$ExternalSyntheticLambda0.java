package com.android.systemui.tuner;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import androidx.preference.PreferenceScreen;
import com.android.systemui.tuner.ShortcutParser;
import java.util.function.Consumer;

public final /* synthetic */ class ShortcutPicker$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ ShortcutPicker f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ LauncherActivityInfo f$2;
    public final /* synthetic */ PreferenceScreen f$3;

    public /* synthetic */ ShortcutPicker$$ExternalSyntheticLambda0(ShortcutPicker shortcutPicker, Context context, LauncherActivityInfo launcherActivityInfo, PreferenceScreen preferenceScreen) {
        this.f$0 = shortcutPicker;
        this.f$1 = context;
        this.f$2 = launcherActivityInfo;
        this.f$3 = preferenceScreen;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onCreatePreferences$0(this.f$1, this.f$2, this.f$3, (ShortcutParser.Shortcut) obj);
    }
}
