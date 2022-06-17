package com.android.systemui.moto;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import com.android.settingslib.display.DisplayDensityUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.motorola.android.provider.MotorolaSettings;
import java.util.ArrayList;
import java.util.Iterator;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: DualSimIconController.kt */
public final class DualSimIconController {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private final boolean DEBUG = (!Build.IS_USER);
    @NotNull
    private final String TAG = "DualSimIconController";
    private int activitySubsCount;
    private boolean airplaneMode;
    private boolean configDynamicDoubleSim = true;
    @NotNull
    private final DualSimIconController$configurationListener$1 configurationListener;
    @NotNull
    private final Context context;
    private boolean forceHideCutoutSpace;
    private boolean forceShowDoubleSim;
    /* access modifiers changed from: private */
    public boolean isBiggerDisplaySize;
    @NotNull
    private final Object lock = new Object();
    @NotNull
    private final ArrayList<Callback> mCallbacks;
    private int narrowStatusbarWidth;
    private boolean showBatteryPercent;
    private boolean showDualSimIcon;
    private boolean showNetworkSpeed;

    /* compiled from: DualSimIconController.kt */
    public interface Callback {
        void onActiveSubsCountChanged(int i);

        void onAirplaneModeChanged(boolean z);
    }

    public DualSimIconController(@NotNull Context context2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        DualSimIconController$configurationListener$1 dualSimIconController$configurationListener$1 = new DualSimIconController$configurationListener$1(this);
        this.configurationListener = dualSimIconController$configurationListener$1;
        this.context = context2;
        Resources resources = context2.getResources();
        Intrinsics.checkNotNullExpressionValue(resources, "context.getResources()");
        this.forceShowDoubleSim = resources.getBoolean(R$bool.zz_moto_config_force_show_double_sim_icon);
        this.narrowStatusbarWidth = resources.getDimensionPixelSize(R$dimen.zz_moto_narrow_statusbar);
        this.forceHideCutoutSpace = resources.getBoolean(R$bool.zz_moto_hide_cutout_space_in_status_bar);
        this.configDynamicDoubleSim = resources.getBoolean(R$bool.config_dynamic_double_sim);
        Object obj = Dependency.get(ConfigurationController.class);
        Intrinsics.checkNotNullExpressionValue(obj, "get<ConfigurationController>(ConfigurationController::class.java)");
        ((ConfigurationController) obj).addCallback(dualSimIconController$configurationListener$1);
        this.showNetworkSpeed = isNetworkSpeedEnabled();
        this.showBatteryPercent = isShowBatteryPercent();
        this.isBiggerDisplaySize = isDisplayDensityBiggerThanDefualt();
        this.showDualSimIcon = isShowDualSimIcon();
        this.mCallbacks = new ArrayList<>();
    }

    public final boolean getShowDualSimIcon() {
        return this.showDualSimIcon;
    }

    public final void setShowDualSimIcon(boolean z) {
        this.showDualSimIcon = z;
    }

    /* compiled from: DualSimIconController.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public final boolean isShowDualSimIcon() {
        boolean z = false;
        if (this.activitySubsCount != 2 || this.context.getResources().getConfiguration().orientation == 2) {
            return false;
        }
        if (this.forceShowDoubleSim || this.showBatteryPercent || this.showNetworkSpeed) {
            return true;
        }
        int notchWidth = getNotchWidth();
        if (notchWidth > this.narrowStatusbarWidth && !this.forceHideCutoutSpace) {
            z = true;
        }
        if (z || !this.configDynamicDoubleSim || notchWidth <= 0 || !this.isBiggerDisplaySize) {
            return z;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public final boolean isDisplayDensityBiggerThanDefualt() {
        int i;
        DisplayDensityUtils displayDensityUtils = new DisplayDensityUtils(this.context);
        int[] values = displayDensityUtils.getValues();
        Intrinsics.checkNotNullExpressionValue(values, "displayDensityUtils.getValues()");
        int currentIndex = displayDensityUtils.getCurrentIndex();
        int defaultDensity = displayDensityUtils.getDefaultDensity();
        int length = values.length - 1;
        if (length >= 0) {
            i = 0;
            while (true) {
                int i2 = i + 1;
                if (values[i] == defaultDensity) {
                    break;
                } else if (i == length) {
                    break;
                } else {
                    i = i2;
                }
            }
        }
        i = 0;
        if (currentIndex <= i) {
            return false;
        }
        return true;
    }

    public final int getNotchWidth() {
        int i;
        DisplayInfo displayInfo = new DisplayInfo();
        this.context.getDisplay().getDisplayInfo(displayInfo);
        DisplayCutout displayCutout = displayInfo.displayCutout;
        if (displayCutout != null) {
            Rect rect = new Rect();
            ScreenDecorations.DisplayCutoutView.boundsFromDirection(displayCutout, 48, rect);
            i = rect.width();
        } else {
            i = 0;
        }
        if (this.DEBUG) {
            Log.d(this.TAG, Intrinsics.stringPlus("notchWidth=", Integer.valueOf(i)));
        }
        return i;
    }

    public final void updateActivitySubsCount(int i) {
        if (this.activitySubsCount != i) {
            this.activitySubsCount = i;
            updateMobileControllers();
            fireActiveSubsCountChangedCallback();
        }
    }

    /* access modifiers changed from: private */
    public final void updateMobileControllers() {
        synchronized (this.lock) {
            boolean isShowDualSimIcon = isShowDualSimIcon();
            if (this.DEBUG) {
                String str = this.TAG;
                Log.d(str, "updateMobileControllers showDual: " + isShowDualSimIcon + " showDualSimIcon: " + getShowDualSimIcon());
            }
            if (getShowDualSimIcon() != isShowDualSimIcon) {
                setShowDualSimIcon(isShowDualSimIcon);
                Object obj = Dependency.get(NetworkController.class);
                Intrinsics.checkNotNullExpressionValue(obj, "get(NetworkController::class.java)");
                ((NetworkController) obj).forceUpdateMobileControllers();
            }
            Unit unit = Unit.INSTANCE;
        }
    }

    private final boolean isNetworkSpeedEnabled() {
        int i = MotorolaSettings.Global.getInt(this.context.getContentResolver(), "internet_speed_switch", 0);
        if (this.DEBUG) {
            Log.d(this.TAG, Intrinsics.stringPlus("isNetworkSpeedEnabled: ", Integer.valueOf(i)));
        }
        if (i == 1) {
            return true;
        }
        return false;
    }

    private final boolean isShowBatteryPercent() {
        int intForUser = Settings.System.getIntForUser(this.context.getContentResolver(), "status_bar_show_battery_percent", 0, ActivityManager.getCurrentUser());
        if (this.DEBUG) {
            Log.d(this.TAG, Intrinsics.stringPlus("isShowBatteryPercent: ", Integer.valueOf(intForUser)));
        }
        if (intForUser != 0) {
            return true;
        }
        return false;
    }

    public final void updateShowNetworkSpeed(boolean z) {
        if (this.showNetworkSpeed != z) {
            this.showNetworkSpeed = z;
            updateMobileControllers();
        }
    }

    public final void updateShowBatteryPercent() {
        boolean isShowBatteryPercent = isShowBatteryPercent();
        if (this.showBatteryPercent != isShowBatteryPercent) {
            this.showBatteryPercent = isShowBatteryPercent;
            updateMobileControllers();
        }
    }

    public final void updateAirplaneMode(boolean z) {
        if (this.airplaneMode != z) {
            this.airplaneMode = z;
            firAirplaneModeChangedCallback();
        }
    }

    @NotNull
    public String toString() {
        return " showDualSimIcon =  " + this.showDualSimIcon + " showBatteryPercent = " + this.showBatteryPercent + " showNetworkSpeed = " + this.showNetworkSpeed + " forceHideCutoutSpace = " + this.forceHideCutoutSpace + " forceShowDoubleSim = " + this.forceShowDoubleSim + " isBiggerDisplaySize = " + this.isBiggerDisplaySize;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0034, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void addCallback(@org.jetbrains.annotations.Nullable com.android.systemui.moto.DualSimIconController.Callback r4) {
        /*
            r3 = this;
            java.util.ArrayList<com.android.systemui.moto.DualSimIconController$Callback> r0 = r3.mCallbacks
            monitor-enter(r0)
            if (r4 == 0) goto L_0x0033
            java.util.ArrayList<com.android.systemui.moto.DualSimIconController$Callback> r1 = r3.mCallbacks     // Catch:{ all -> 0x0030 }
            boolean r1 = r1.contains(r4)     // Catch:{ all -> 0x0030 }
            if (r1 == 0) goto L_0x000e
            goto L_0x0033
        L_0x000e:
            boolean r1 = r3.DEBUG     // Catch:{ all -> 0x0030 }
            if (r1 == 0) goto L_0x001d
            java.lang.String r1 = r3.TAG     // Catch:{ all -> 0x0030 }
            java.lang.String r2 = "addCallback "
            java.lang.String r2 = kotlin.jvm.internal.Intrinsics.stringPlus(r2, r4)     // Catch:{ all -> 0x0030 }
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x0030 }
        L_0x001d:
            java.util.ArrayList<com.android.systemui.moto.DualSimIconController$Callback> r1 = r3.mCallbacks     // Catch:{ all -> 0x0030 }
            r1.add(r4)     // Catch:{ all -> 0x0030 }
            int r1 = r3.activitySubsCount     // Catch:{ all -> 0x0030 }
            r4.onActiveSubsCountChanged(r1)     // Catch:{ all -> 0x0030 }
            boolean r3 = r3.airplaneMode     // Catch:{ all -> 0x0030 }
            r4.onAirplaneModeChanged(r3)     // Catch:{ all -> 0x0030 }
            kotlin.Unit r3 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0030 }
            monitor-exit(r0)
            return
        L_0x0030:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        L_0x0033:
            monitor-exit(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.moto.DualSimIconController.addCallback(com.android.systemui.moto.DualSimIconController$Callback):void");
    }

    public final void removeCallback(@Nullable Callback callback) {
        if (callback != null) {
            if (this.DEBUG) {
                Log.d(this.TAG, Intrinsics.stringPlus("removeCallback ", callback));
            }
            synchronized (this.mCallbacks) {
                this.mCallbacks.remove(callback);
            }
        }
    }

    private final void fireActiveSubsCountChangedCallback() {
        ArrayList arrayList;
        synchronized (this.mCallbacks) {
            arrayList = new ArrayList(this.mCallbacks);
            Unit unit = Unit.INSTANCE;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((Callback) it.next()).onActiveSubsCountChanged(this.activitySubsCount);
        }
    }

    private final void firAirplaneModeChangedCallback() {
        ArrayList arrayList;
        synchronized (this.mCallbacks) {
            arrayList = new ArrayList(this.mCallbacks);
            Unit unit = Unit.INSTANCE;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((Callback) it.next()).onAirplaneModeChanged(this.airplaneMode);
        }
    }
}
