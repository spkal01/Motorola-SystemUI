package com.android.systemui.dagger;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.app.IWallpaperManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.app.smartspace.SmartspaceManager;
import android.app.trust.TrustManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.om.OverlayManager;
import android.content.pm.IPackageManager;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.DisplayManager;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.media.AudioManager;
import android.media.IAudioService;
import android.media.MediaRouter2Manager;
import android.media.session.MediaSessionManager;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.ServiceManager;
import android.os.UserManager;
import android.os.Vibrator;
import android.permission.PermissionManager;
import android.service.dreams.IDreamManager;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.CrossWindowBlurListeners;
import android.view.IWindowManager;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.app.IBatteryStats;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.LatencyTracker;
import com.android.systemui.shared.system.PackageManagerWrapper;
import java.util.Optional;

public class FrameworkServicesModule {
    static AccessibilityManager provideAccessibilityManager(Context context) {
        return (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
    }

    static ActivityManager provideActivityManager(Context context) {
        return (ActivityManager) context.getSystemService(ActivityManager.class);
    }

    static AlarmManager provideAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(AlarmManager.class);
    }

    static AudioManager provideAudioManager(Context context) {
        return (AudioManager) context.getSystemService(AudioManager.class);
    }

    static ColorDisplayManager provideColorDisplayManager(Context context) {
        return (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
    }

    static ConnectivityManager provideConnectivityManagager(Context context) {
        return (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    static ContentResolver provideContentResolver(Context context) {
        return context.getContentResolver();
    }

    static DevicePolicyManager provideDevicePolicyManager(Context context) {
        return (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    static CrossWindowBlurListeners provideCrossWindowBlurListeners() {
        return CrossWindowBlurListeners.getInstance();
    }

    static int provideDisplayId(Context context) {
        return context.getDisplayId();
    }

    static DisplayManager provideDisplayManager(Context context) {
        return (DisplayManager) context.getSystemService(DisplayManager.class);
    }

    static IActivityManager provideIActivityManager() {
        return ActivityManager.getService();
    }

    static ActivityTaskManager provideActivityTaskManager() {
        return ActivityTaskManager.getInstance();
    }

    static IActivityTaskManager provideIActivityTaskManager() {
        return ActivityTaskManager.getService();
    }

    static IAudioService provideIAudioService() {
        return IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
    }

    static IBatteryStats provideIBatteryStats() {
        return IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
    }

    static IDreamManager provideIDreamManager() {
        return IDreamManager.Stub.asInterface(ServiceManager.checkService("dreams"));
    }

    static FaceManager provideFaceManager(Context context) {
        return (FaceManager) context.getSystemService(FaceManager.class);
    }

    static FingerprintManager providesFingerprintManager(Context context) {
        return (FingerprintManager) context.getSystemService(FingerprintManager.class);
    }

    static InputMethodManager provideInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(InputMethodManager.class);
    }

    static IPackageManager provideIPackageManager() {
        return IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    }

    static IStatusBarService provideIStatusBarService() {
        return IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
    }

    static IWallpaperManager provideIWallPaperManager() {
        return IWallpaperManager.Stub.asInterface(ServiceManager.getService("wallpaper"));
    }

    static IWindowManager provideIWindowManager() {
        return WindowManagerGlobal.getWindowManagerService();
    }

    static KeyguardManager provideKeyguardManager(Context context) {
        return (KeyguardManager) context.getSystemService(KeyguardManager.class);
    }

    static LatencyTracker provideLatencyTracker(Context context) {
        return LatencyTracker.getInstance(context);
    }

    static LauncherApps provideLauncherApps(Context context) {
        return (LauncherApps) context.getSystemService(LauncherApps.class);
    }

    static MediaRouter2Manager provideMediaRouter2Manager(Context context) {
        return MediaRouter2Manager.getInstance(context);
    }

    static MediaSessionManager provideMediaSessionManager(Context context) {
        return (MediaSessionManager) context.getSystemService(MediaSessionManager.class);
    }

    static NetworkScoreManager provideNetworkScoreManager(Context context) {
        return (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class);
    }

    static NotificationManager provideNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(NotificationManager.class);
    }

    static PackageManager providePackageManager(Context context) {
        return context.getPackageManager();
    }

    static PackageManagerWrapper providePackageManagerWrapper() {
        return PackageManagerWrapper.getInstance();
    }

    static PowerManager providePowerManager(Context context) {
        return (PowerManager) context.getSystemService(PowerManager.class);
    }

    static Resources provideResources(Context context) {
        return context.getResources();
    }

    static SensorManager providesSensorManager(Context context) {
        return (SensorManager) context.getSystemService(SensorManager.class);
    }

    static SensorPrivacyManager provideSensorPrivacyManager(Context context) {
        return (SensorPrivacyManager) context.getSystemService(SensorPrivacyManager.class);
    }

    static ShortcutManager provideShortcutManager(Context context) {
        return (ShortcutManager) context.getSystemService(ShortcutManager.class);
    }

    static SubscriptionManager provideSubcriptionManager(Context context) {
        return (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
    }

    static TelecomManager provideTelecomManager(Context context) {
        return (TelecomManager) context.getSystemService(TelecomManager.class);
    }

    static TelephonyManager provideTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(TelephonyManager.class);
    }

    static TrustManager provideTrustManager(Context context) {
        return (TrustManager) context.getSystemService(TrustManager.class);
    }

    static Vibrator provideVibrator(Context context) {
        return (Vibrator) context.getSystemService(Vibrator.class);
    }

    static Optional<Vibrator> provideOptionalVibrator(Context context) {
        return Optional.ofNullable((Vibrator) context.getSystemService(Vibrator.class));
    }

    static ViewConfiguration provideViewConfiguration(Context context) {
        return ViewConfiguration.get(context);
    }

    static UserManager provideUserManager(Context context) {
        return (UserManager) context.getSystemService(UserManager.class);
    }

    static WallpaperManager provideWallpaperManager(Context context) {
        return (WallpaperManager) context.getSystemService(WallpaperManager.class);
    }

    static WifiManager provideWifiManager(Context context) {
        return (WifiManager) context.getSystemService(WifiManager.class);
    }

    static OverlayManager provideOverlayManager(Context context) {
        return (OverlayManager) context.getSystemService(OverlayManager.class);
    }

    static WindowManager provideWindowManager(Context context) {
        return (WindowManager) context.getSystemService(WindowManager.class);
    }

    static PermissionManager providePermissionManager(Context context) {
        PermissionManager permissionManager = (PermissionManager) context.getSystemService(PermissionManager.class);
        if (permissionManager != null) {
            permissionManager.initializeUsageHelper();
        }
        return permissionManager;
    }

    static SmartspaceManager provideSmartspaceManager(Context context) {
        return (SmartspaceManager) context.getSystemService(SmartspaceManager.class);
    }
}
