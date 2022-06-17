package com.motorola.systemui.cli.navgesture;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.motorola.systemui.cli.navgesture.util.DeviceProfile;

public interface ActivityContext {
    ActivityOptions getActivityLaunchOptions(View view);

    DeviceProfile getDeviceProfile();

    static ActivityContext lookupContext(Context context) {
        if (context instanceof ActivityContext) {
            return (ActivityContext) context;
        }
        if (context instanceof ContextThemeWrapper) {
            return lookupContext(((ContextWrapper) context).getBaseContext());
        }
        throw new IllegalArgumentException("Cannot find ActivityContext in parent tree");
    }
}
