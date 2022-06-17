package com.motorola.systemui.cli.navgesture;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public final class OverviewComponentObserver {
    private ActivityControlHelper mActivityControlHelper = new LauncherActivityControllerHelper();
    private final Context mContext;
    private Intent mOverviewIntent;

    public OverviewComponentObserver(Context context) {
        this.mContext = context;
        Intent intent = new Intent();
        this.mOverviewIntent = intent;
        intent.setComponent(new ComponentName("com.android.systemui", "com.motorola.systemui.cli.navgesture.CliRecentsActivity"));
    }

    public Intent getOverviewIntent() {
        return this.mOverviewIntent;
    }

    public <T extends BaseGestureActivity> ActivityControlHelper<T> getActivityControlHelper() {
        return this.mActivityControlHelper;
    }
}
