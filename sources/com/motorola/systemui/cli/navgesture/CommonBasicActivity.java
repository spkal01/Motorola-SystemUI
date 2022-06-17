package com.motorola.systemui.cli.navgesture;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import com.motorola.systemui.cli.navgesture.animation.AppTransitionManager;
import com.motorola.systemui.cli.navgesture.animation.GestureRecentsAppTransitionManager;
import com.motorola.systemui.cli.navgesture.util.DebugLog;
import com.motorola.systemui.cli.navgesture.util.DeviceProfile;
import com.motorola.systemui.cli.navgesture.util.DeviceProfileProvider;

public abstract class CommonBasicActivity extends FragmentActivity implements ActivityContext, DeviceProfileProvider.DeviceProfileChangeListener {
    private static final String LOG_TAG = "CommonBasicActivity";
    private AppTransitionManager mAppTransitionManager;
    private DeviceProfile mDeviceProfile;
    private View mLauncherRootContainerView;
    private SystemUiController mSystemUiController;

    /* access modifiers changed from: protected */
    public abstract View onCreateInflaterView(LayoutInflater layoutInflater);

    /* access modifiers changed from: protected */
    public void onCreateSetupViews(Bundle bundle) {
    }

    /* access modifiers changed from: protected */
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        onCreateBeforeInflaterView(bundle);
        this.mLauncherRootContainerView = onCreateInflaterView(LayoutInflater.from(this));
        onCreateSetupViews(bundle);
        setContentView(this.mLauncherRootContainerView);
        onCreateAfterSetupViews();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        DeviceProfileProvider.INSTANCE.lambda$get$0(this).removeOnChangeListener(this);
        this.mAppTransitionManager.dispose();
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onCreateBeforeInflaterView(Bundle bundle) {
        DeviceProfileProvider deviceProfileProvider = DeviceProfileProvider.INSTANCE.lambda$get$0(this);
        this.mDeviceProfile = deviceProfileProvider.getDeviceProfile(this);
        deviceProfileProvider.addOnChangeListener(this);
    }

    /* access modifiers changed from: protected */
    public void onCreateAfterSetupViews() {
        this.mAppTransitionManager = new GestureRecentsAppTransitionManager(this);
    }

    public <T extends View> T findViewById(int i) {
        return this.mLauncherRootContainerView.findViewById(i);
    }

    public ActivityOptions getActivityLaunchOptions(View view) {
        return this.mAppTransitionManager.getActivityLaunchOptions(this, view);
    }

    public AppTransitionManager getAppTransitionManager() {
        return this.mAppTransitionManager;
    }

    public DeviceProfile getDeviceProfile() {
        return this.mDeviceProfile;
    }

    public void onDeviceProfileChanged(int i, DeviceProfileProvider deviceProfileProvider) {
        String str = LOG_TAG;
        DebugLog.m98d(str, "onDeviceProfileChanged: changeFlags = " + i);
        this.mDeviceProfile = deviceProfileProvider.getDeviceProfile(this);
    }

    public SystemUiController getSystemUiController() {
        if (this.mSystemUiController == null) {
            this.mSystemUiController = new SystemUiController(getWindow());
        }
        return this.mSystemUiController;
    }
}
