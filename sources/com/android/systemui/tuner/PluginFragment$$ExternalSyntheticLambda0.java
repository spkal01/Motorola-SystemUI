package com.android.systemui.tuner;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.ArrayMap;
import androidx.preference.PreferenceScreen;
import com.android.systemui.shared.plugins.PluginManager;
import java.util.function.Consumer;

public final /* synthetic */ class PluginFragment$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ PluginFragment f$0;
    public final /* synthetic */ ArrayMap f$1;
    public final /* synthetic */ PluginManager f$2;
    public final /* synthetic */ Context f$3;
    public final /* synthetic */ PreferenceScreen f$4;

    public /* synthetic */ PluginFragment$$ExternalSyntheticLambda0(PluginFragment pluginFragment, ArrayMap arrayMap, PluginManager pluginManager, Context context, PreferenceScreen preferenceScreen) {
        this.f$0 = pluginFragment;
        this.f$1 = arrayMap;
        this.f$2 = pluginManager;
        this.f$3 = context;
        this.f$4 = preferenceScreen;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$loadPrefs$0(this.f$1, this.f$2, this.f$3, this.f$4, (PackageInfo) obj);
    }
}
