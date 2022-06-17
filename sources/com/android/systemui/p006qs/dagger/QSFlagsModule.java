package com.android.systemui.p006qs.dagger;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.util.settings.GlobalSettings;

/* renamed from: com.android.systemui.qs.dagger.QSFlagsModule */
public interface QSFlagsModule {
    static boolean isReduceBrightColorsAvailable(Context context) {
        return ColorDisplayManager.isReduceBrightColorsAvailable(context);
    }

    static boolean isPMLiteEnabled(FeatureFlags featureFlags, GlobalSettings globalSettings) {
        if (!featureFlags.isPMLiteEnabled() || globalSettings.getInt("sysui_pm_lite", 1) == 0) {
            return false;
        }
        return true;
    }
}
