package com.android.systemui.moto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.internal.app.MotoPrcPermissionManager;
import motorola.core_services.cli.CLIManager;

public class MotoFeature {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static MotoFeature sInstance;
    private final int mAudioDeviceType;
    private final boolean mBelowCarrierName;
    private Context mCliContext;
    private final Context mContext;
    private boolean mEnableCustomPanelView;
    private final boolean mInnerBatteryPercentage;
    private final boolean mIsOledProduct;
    private boolean mLeftCarrierName;
    public String mRoCarrier = "unknown";
    private final boolean mShowRightSideClock;
    private final boolean mSupportCli;
    private final boolean mSupportDsSkipped;
    private final boolean mSupportEdgeTouch;
    private final boolean mSupportFod;
    private final boolean mSupportFolio;
    private final boolean mSupportLockscreenWeather;
    private final boolean mSupportMods;
    private boolean mSupportPrivacySpace;
    private final boolean mSupportRelativeVolume;
    private final boolean mSupportSideFps;
    private boolean mSupportUdfps;
    private boolean mUdfpsUseAospTriggerFingerDown;

    MotoFeature(Context context) {
        this.mContext = context;
        Resources resources = context.getResources();
        int notchWidth = getNotchWidth();
        this.mBelowCarrierName = notchWidth > 0;
        this.mLeftCarrierName = resources.getBoolean(R$bool.config_show_carrier_name_in_statusbar);
        this.mInnerBatteryPercentage = notchWidth > 0 && !resources.getBoolean(R$bool.config_force_battery_percentage_outside);
        this.mSupportFod = false;
        this.mIsOledProduct = resources.getBoolean(R$bool.config_is_oled_product);
        this.mSupportDsSkipped = resources.getBoolean(R$bool.config_support_ds_pin_skipped);
        this.mSupportMods = context.getPackageManager().hasSystemFeature("com.motorola.hardware.mods");
        this.mSupportLockscreenWeather = resources.getBoolean(R$bool.config_support_lockscreen_weather);
        this.mSupportFolio = resources.getBoolean(R$bool.zz_moto_folio_product);
        boolean z = CLIManager.isCLISupported() && ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplays().length > 1;
        this.mSupportCli = z;
        if (z) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.motorola.hardware.action.ACTION_LID_STATE_CHANGE");
            context.registerReceiverAsUser(new FlipReceiver(), UserHandle.CURRENT, intentFilter, (String) null, new Handler(Looper.getMainLooper()));
        }
        this.mSupportRelativeVolume = getRelativeVolumeFeature();
        this.mSupportSideFps = resources.getBoolean(17891838);
        this.mShowRightSideClock = resources.getBoolean(R$bool.zz_moto_config_show_right_side_clock);
        this.mSupportEdgeTouch = resources.getBoolean(17891837);
        this.mAudioDeviceType = 0;
        String string = MotorolaSettings.Global.getString(context.getContentResolver(), "channel_id");
        this.mRoCarrier = string;
        if (string == null) {
            this.mRoCarrier = "unknown";
        }
        this.mEnableCustomPanelView = resources.getBoolean(R$bool.zz_moto_config_custom_panelview_prc);
        this.mSupportPrivacySpace = resources.getBoolean(17891689);
        this.mSupportUdfps = !ArrayUtils.isEmpty(context.getResources().getIntArray(17236166));
        this.mUdfpsUseAospTriggerFingerDown = context.getResources().getBoolean(R$bool.config_udfps_use_aosp_trigger_finger_down);
        if (DEBUG) {
            Log.d("MotoFeature", "MotoFeature: " + toString());
        }
    }

    public static MotoFeature getInstance(Context context) {
        synchronized (MotoFeature.class) {
            if (sInstance == null) {
                sInstance = new MotoFeature(context.getApplicationContext());
                DesktopFeature.initInstance(context);
            }
        }
        return sInstance;
    }

    public static MotoFeature getExistedInstance() {
        MotoFeature motoFeature = sInstance;
        if (motoFeature != null) {
            return motoFeature;
        }
        throw new IllegalArgumentException("You should init the MotoFeature before using!");
    }

    public String getRoCarrier() {
        return this.mRoCarrier;
    }

    public void updateLeftCarrrierName() {
        this.mLeftCarrierName = this.mContext.getResources().getBoolean(R$bool.config_show_carrier_name_in_statusbar);
    }

    public boolean isBelowCarrierName() {
        return this.mBelowCarrierName && !this.mLeftCarrierName;
    }

    public boolean isInnerBatteryPercentage() {
        return this.mInnerBatteryPercentage;
    }

    public boolean isSupportCli() {
        return this.mSupportCli;
    }

    public static boolean isLidClosed(Context context) {
        return MotorolaSettings.Global.getInt(context.getContentResolver(), "lid_state", 1) == 0;
    }

    public boolean isLidClosed() {
        return MotorolaSettings.Global.getInt(this.mContext.getContentResolver(), "lid_state", 1) == 0;
    }

    public Context getCliBaseContext(Context context) {
        if (this.mCliContext == null) {
            this.mCliContext = getCliContext(context);
        }
        return this.mCliContext;
    }

    public static Context getCliContext(Context context) {
        if (isCliContext(context)) {
            return context;
        }
        Display display = ((DisplayManager) context.getSystemService("display")).getDisplay(1);
        if (display != null) {
            return context.createDisplayContext(display);
        }
        Log.i("MotoFeature", "getCliContext failed");
        return context;
    }

    public static boolean isCliContext(Context context) {
        return context.getDisplay() != null && context.getDisplay().getDisplayId() == 1;
    }

    public boolean isAllowedFullScreenOnCLI(String str, String str2) {
        if (str == null) {
            return false;
        }
        return "com.android.dialer".equals(str);
    }

    private class FlipReceiver extends BroadcastReceiver {
        private FlipReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Class cls = ScreenLifecycle.class;
            if (intent.getIntExtra("com.motorola.hardware.extra.LID_STATE", -1) == 0) {
                ((ScreenLifecycle) Dependency.get(cls)).dispatchLidClosed();
            } else {
                ((ScreenLifecycle) Dependency.get(cls)).dispatchLidOpen();
            }
        }
    }

    public boolean isSupportFod() {
        return this.mSupportFod;
    }

    public int getNotchWidth() {
        int i;
        DisplayInfo displayInfo = new DisplayInfo();
        this.mContext.getDisplay().getDisplayInfo(displayInfo);
        DisplayCutout displayCutout = displayInfo.displayCutout;
        if (displayCutout != null) {
            Rect rect = new Rect();
            ScreenDecorations.DisplayCutoutView.boundsFromDirection(displayCutout, 48, rect);
            i = rect.width();
        } else {
            i = 0;
        }
        if (DEBUG) {
            Log.d("MotoFeature", "notchWidth=" + i);
        }
        return i;
    }

    public int getNotchHeight() {
        DisplayInfo displayInfo = new DisplayInfo();
        this.mContext.getDisplay().getDisplayInfo(displayInfo);
        DisplayCutout displayCutout = displayInfo.displayCutout;
        if (displayCutout == null) {
            return 0;
        }
        Rect rect = new Rect();
        ScreenDecorations.DisplayCutoutView.boundsFromDirection(displayCutout, 48, rect);
        return rect.height();
    }

    public static int getBottomNotchHeight(Context context) {
        DisplayInfo displayInfo = new DisplayInfo();
        context.getDisplay().getDisplayInfo(displayInfo);
        DisplayCutout displayCutout = displayInfo.displayCutout;
        if (displayCutout == null) {
            return 0;
        }
        Rect rect = new Rect();
        ScreenDecorations.DisplayCutoutView.boundsFromDirection(displayCutout, 80, rect);
        return rect.height();
    }

    public int getCarrierLabelTopMargin() {
        return Math.max(getNotchHeight() + 0, this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_header_height_keyguard));
    }

    public boolean isOledProduct() {
        return this.mIsOledProduct;
    }

    public boolean supportDualSimPINSkiped() {
        return this.mSupportDsSkipped;
    }

    public boolean supportLockscreenWeather() {
        return this.mSupportLockscreenWeather;
    }

    public boolean isSupportFolio() {
        return this.mSupportFolio;
    }

    public static Context getSubContext(Context context, SubscriptionInfo subscriptionInfo) {
        Configuration configuration = new Configuration();
        if (subscriptionInfo.getMcc() == 0 && subscriptionInfo.getMnc() == 0) {
            Configuration configuration2 = context.getResources().getConfiguration();
            configuration.mcc = configuration2.mcc;
            configuration.mnc = configuration2.mnc;
            if (DEBUG) {
                Log.i("MotoFeature", "No mcc/mnc for sub: " + subscriptionInfo + " using mcc/mnc from main context: " + configuration.mcc + "/" + configuration.mnc);
            }
        } else {
            if (DEBUG) {
                Log.i("MotoFeature", "mcc/mnc for sub: " + subscriptionInfo);
            }
            configuration.mcc = subscriptionInfo.getMcc();
            int mnc = subscriptionInfo.getMnc();
            if (mnc == 0) {
                mnc = 65535;
            }
            configuration.mnc = mnc;
        }
        return context.createConfigurationContext(configuration);
    }

    public String toString() {
        return "mBelowCarrierName=" + this.mBelowCarrierName + ";mSupportFod=" + this.mSupportFod + ";mInnerBatteryPercentage=" + this.mInnerBatteryPercentage + ";mSupportDsSkipped=" + this.mSupportDsSkipped + ";mIsOledProduct=" + this.mIsOledProduct + ";mSupportMods=" + this.mSupportMods + ";mSupportSideFps=" + this.mSupportSideFps + ";mSupportEdgeTouch=" + this.mSupportEdgeTouch + ";mShowRightSideClock=" + this.mShowRightSideClock + ";mSupportRelativeVolume=" + this.mSupportRelativeVolume + ";mAudioDeviceType=" + this.mAudioDeviceType + ";mRoCarrier=" + this.mRoCarrier + ";mSupportLockscreenWeather=" + this.mSupportLockscreenWeather;
    }

    public boolean isSupportRelativeVolume() {
        return this.mSupportRelativeVolume;
    }

    private boolean getRelativeVolumeFeature() {
        try {
            boolean z = SystemProperties.getBoolean("ro.audio.relative_volume", false);
            boolean isAppExist = isAppExist(this.mContext, "com.motorola.dynamicvolume");
            if (DEBUG) {
                Log.d("MotoFeature", "RelativeVolumeFeature: " + z + ", DynamicVolumeAppExist: " + isAppExist);
            }
            if (!z || !isAppExist) {
                return false;
            }
            return true;
        } catch (Exception e) {
            if (DEBUG) {
                Log.d("MotoFeature", "getRelativeVolumeFeature", e);
            }
        }
    }

    public boolean isAppExist(Context context, String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            if (DEBUG) {
                Log.d("MotoFeature", "package not found: " + str);
            }
            applicationInfo = null;
        }
        if (applicationInfo != null) {
            return true;
        }
        return false;
    }

    public boolean supportSideFps() {
        return this.mSupportSideFps;
    }

    public boolean supportEdgeTouch() {
        return this.mSupportEdgeTouch;
    }

    public boolean showRightSideClock() {
        return this.mShowRightSideClock;
    }

    public static boolean isPrcProduct() {
        return MotoPrcPermissionManager.isPrcProduct();
    }

    public boolean isCustomPanelView() {
        return MotoPrcPermissionManager.isPrcProduct() && this.mEnableCustomPanelView;
    }

    public boolean isSupportPrivacySpace() {
        boolean z = SystemProperties.getBoolean("persist.debug.privacy_space", false);
        if (!MotoPrcPermissionManager.isPrcProduct()) {
            return false;
        }
        if (this.mSupportPrivacySpace || z) {
            return true;
        }
        return false;
    }

    public boolean isSupportUdfps() {
        return this.mSupportUdfps;
    }

    public boolean udfpsUseAospTriggerFingerDown() {
        return this.mUdfpsUseAospTriggerFingerDown;
    }

    public static boolean isProductWaveAtleastRefWave(String str) {
        String str2 = SystemProperties.get("ro.mot.product_wave");
        boolean z = false;
        if (!(str2 == null || str == null)) {
            String[] split = str2.split("\\.");
            String[] split2 = str.split("\\.");
            if (split != null && split.length == 2 && split2 != null && split2.length == 2) {
                try {
                    int parseInt = Integer.parseInt(split[0]);
                    int parseInt2 = Integer.parseInt(split[1]);
                    int parseInt3 = Integer.parseInt(split2[0]);
                    int parseInt4 = Integer.parseInt(split2[1]);
                    if (parseInt >= parseInt3 && (parseInt > parseInt3 || parseInt2 >= parseInt4)) {
                        z = true;
                    }
                } catch (NumberFormatException e) {
                    Log.d("MotoFeature", "isProductWaveAtleastRefWave" + e.getMessage());
                }
            }
            if (DEBUG) {
                Log.d("MotoFeature", "QSTileHost productWave = " + str2 + " refWave = " + str + "isWaveAtleast =" + z);
            }
        }
        return z;
    }
}
