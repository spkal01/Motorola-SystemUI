package com.motorola.systemui.cli.navgesture.util;

import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import com.motorola.systemui.cli.navgesture.MainThreadInitializedObject;
import com.motorola.systemui.cli.navgesture.display.SecondaryDisplay;
import java.util.ArrayList;
import java.util.List;

public class DeviceProfileProvider {
    public static final MainThreadInitializedObject<DeviceProfileProvider> INSTANCE = new MainThreadInitializedObject<>(DeviceProfileProvider$$ExternalSyntheticLambda1.INSTANCE);
    private static final String LOG_TAG = "DeviceProfileProvider";
    private DeviceProfile landscapeProfile;
    private final List<DeviceProfileChangeListener> mChangeListeners = new ArrayList();
    private ConfigMonitor mConfigMonitor;
    private DeviceProfile portraitProfile;

    public interface DeviceProfileChangeListener {
        void onDeviceProfileChanged(int i, DeviceProfileProvider deviceProfileProvider);
    }

    /* renamed from: $r8$lambda$h5-inMyPsuUp0xT88jCDqa_djEc  reason: not valid java name */
    public static /* synthetic */ DeviceProfileProvider m825$r8$lambda$h5inMyPsuUp0xT88jCDqa_djEc(Context context) {
        return new DeviceProfileProvider(context);
    }

    private DeviceProfileProvider(Context context) {
        updateDeviceProfile(context);
        this.mConfigMonitor = new ConfigMonitor(context, new DeviceProfileProvider$$ExternalSyntheticLambda0(this), new DeviceProfileProvider$$ExternalSyntheticLambda3((DisplayManager) context.getSystemService(DisplayManager.class), SecondaryDisplay.INSTANCE.lambda$get$0(context)));
    }

    private DeviceProfileProvider(DeviceProfileProvider deviceProfileProvider) {
    }

    private void updateDeviceProfile(Context context) {
        SecondaryDisplay secondaryDisplay = SecondaryDisplay.INSTANCE.lambda$get$0(context);
        Point point = new Point();
        Point point2 = new Point();
        secondaryDisplay.getCurrentSizeRange(point, point2);
        Point displaySize = secondaryDisplay.getDisplaySize();
        int min = Math.min(displaySize.x, displaySize.y);
        int max = Math.max(displaySize.x, displaySize.y);
        String str = LOG_TAG;
        DebugLog.m98d(str, "DeviceProfileProvider:  smallestSize " + point + " largestSize " + point2 + " smallSide " + min + " largeSide " + max + " realSize " + displaySize + " configuration " + context.getResources().getConfiguration());
        Context context2 = context;
        Point point3 = point;
        Point point4 = point2;
        this.landscapeProfile = new DeviceProfile(context2, point3, point4, max, min, true);
        this.portraitProfile = new DeviceProfile(context2, point3, point4, min, max, false);
    }

    public DeviceProfile getDeviceProfile(Context context) {
        if (context.getResources().getConfiguration().orientation == 2) {
            return this.landscapeProfile;
        }
        return this.portraitProfile;
    }

    public void addOnChangeListener(DeviceProfileChangeListener deviceProfileChangeListener) {
        this.mChangeListeners.add(deviceProfileChangeListener);
    }

    public void removeOnChangeListener(DeviceProfileChangeListener deviceProfileChangeListener) {
        this.mChangeListeners.remove(deviceProfileChangeListener);
    }

    /* access modifiers changed from: private */
    public void onConfigChanged(Context context) {
        new DeviceProfileProvider(this);
        updateDeviceProfile(context);
        this.mConfigMonitor.unregister();
        this.mConfigMonitor = new ConfigMonitor(context, new DeviceProfileProvider$$ExternalSyntheticLambda0(this), new DeviceProfileProvider$$ExternalSyntheticLambda2((DisplayManager) context.getSystemService(DisplayManager.class), context));
        for (DeviceProfileChangeListener onDeviceProfileChanged : this.mChangeListeners) {
            onDeviceProfileChanged.onDeviceProfileChanged(0, this);
        }
    }
}
