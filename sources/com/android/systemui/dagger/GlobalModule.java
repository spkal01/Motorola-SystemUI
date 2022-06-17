package com.android.systemui.dagger;

import android.app.ActivityManager;
import android.content.Context;
import android.util.DisplayMetrics;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;

public class GlobalModule {
    public DisplayMetrics provideDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    static UiEventLogger provideUiEventLogger() {
        return new UiEventLoggerImpl();
    }

    static boolean provideIsTestHarness() {
        return ActivityManager.isRunningInUserTestHarness();
    }
}
