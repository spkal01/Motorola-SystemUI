package com.android.systemui.theme;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.FabricatedOverlay;
import android.content.om.OverlayIdentifier;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.graphics.ColorUtils;
import com.android.systemui.R$string;
import com.android.systemui.SystemUI;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.monet.ColorScheme;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.util.settings.SecureSettings;
import com.motorola.internal.app.MotoDesktopManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;

public class ThemeOverlayController extends SystemUI {
    /* access modifiers changed from: private */
    public boolean mAcceptColorEvents = true;
    private final Executor mBgExecutor;
    private final Handler mBgHandler;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean equals = "android.intent.action.MANAGED_PROFILE_ADDED".equals(intent.getAction());
            boolean equals2 = "android.intent.action.USER_SWITCHED".equals(intent.getAction());
            boolean isManagedProfile = ThemeOverlayController.this.mUserManager.isManagedProfile(intent.getIntExtra("android.intent.extra.user_handle", 0));
            if (equals2 || equals) {
                if (ThemeOverlayController.this.mDeviceProvisionedController.isCurrentUserSetup() || !isManagedProfile) {
                    Log.d("ThemeOverlayController", "Updating overlays for user switch / profile added.");
                    if (ThemeOverlayController.this.mUserTracker.getUserId() != 0) {
                        boolean unused = ThemeOverlayController.this.handleDefaultPresetColor(true);
                    }
                    ThemeOverlayController.this.reevaluateSystemTheme(true);
                    return;
                }
                Log.i("ThemeOverlayController", "User setup not finished when " + intent.getAction() + " was received. Deferring... Managed profile? " + isManagedProfile);
            } else if ("android.intent.action.WALLPAPER_CHANGED".equals(intent.getAction())) {
                boolean unused2 = ThemeOverlayController.this.mAcceptColorEvents = true;
                Log.i("ThemeOverlayController", "Allowing color events again");
            }
        }
    };
    private ColorScheme mColorScheme;
    private WallpaperColors mCurrentColors;
    /* access modifiers changed from: private */
    public boolean mDeferredThemeEvaluation;
    /* access modifiers changed from: private */
    public WallpaperColors mDeferredWallpaperColors;
    /* access modifiers changed from: private */
    public int mDeferredWallpaperColorsFlags;
    protected int mDesktopWallpaperAccentColor = 0;
    /* access modifiers changed from: private */
    public DeviceProvisionedController mDeviceProvisionedController;
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedListener = new DeviceProvisionedController.DeviceProvisionedListener() {
        public void onUserSetupChanged() {
            if (ThemeOverlayController.this.mDeviceProvisionedController.isCurrentUserSetup() && ThemeOverlayController.this.mDeferredThemeEvaluation) {
                Log.i("ThemeOverlayController", "Applying deferred theme");
                boolean unused = ThemeOverlayController.this.mDeferredThemeEvaluation = false;
                ThemeOverlayController.this.reevaluateSystemTheme(true);
            }
        }
    };
    private final boolean mIsMonetEnabled;
    protected int mMainDesktopWallpaperColor = 0;
    private final Executor mMainExecutor;
    protected int mMainWallpaperColor = 0;
    private boolean mNeedsOverlayCreation;
    private FabricatedOverlay mNeutralOverlay;
    private final WallpaperManager.OnColorsChangedListener mOnColorsChangedListener = new ThemeOverlayController$$ExternalSyntheticLambda0(this);
    private boolean mPreset = false;
    private FabricatedOverlay mSecondaryOverlay;
    private SecureSettings mSecureSettings;
    /* access modifiers changed from: private */
    public boolean mSkipSettingChange;
    private final ThemeOverlayApplier mThemeManager;
    /* access modifiers changed from: private */
    public final UserManager mUserManager;
    /* access modifiers changed from: private */
    public UserTracker mUserTracker;
    private WakefulnessLifecycle mWakefulnessLifecycle;
    protected int mWallpaperAccentColor = 0;
    private WallpaperManager mWallpaperManager;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(WallpaperColors wallpaperColors, int i) {
        if (this.mAcceptColorEvents || this.mWakefulnessLifecycle.getWakefulness() == 0) {
            if (wallpaperColors != null) {
                this.mAcceptColorEvents = false;
                this.mDeferredWallpaperColors = null;
                this.mDeferredWallpaperColorsFlags = 0;
            }
            handleWallpaperColors(wallpaperColors, i);
            return;
        }
        this.mDeferredWallpaperColors = wallpaperColors;
        this.mDeferredWallpaperColorsFlags = i;
        Log.i("ThemeOverlayController", "colors received; processing deferred until screen off: " + wallpaperColors);
    }

    private int getLatestWallpaperType() {
        return this.mWallpaperManager.getWallpaperId(2) > this.mWallpaperManager.getWallpaperId(1) ? 2 : 1;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00d0, code lost:
        if (r8.has("android.theme.customization.system_palette") != false) goto L_0x00d2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleWallpaperColors(android.app.WallpaperColors r12, int r13) {
        /*
            r11 = this;
            java.lang.String r0 = "android.theme.customization.accent_color"
            java.lang.String r1 = "android.theme.customization.color_source"
            android.app.WallpaperColors r2 = r11.mCurrentColors
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x000c
            r2 = r4
            goto L_0x000d
        L_0x000c:
            r2 = r3
        L_0x000d:
            int r5 = r11.getLatestWallpaperType()
            r5 = r5 & r13
            java.lang.String r6 = "ThemeOverlayController"
            if (r5 == 0) goto L_0x0034
            r11.mCurrentColors = r12
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "got new colors: "
            r7.append(r8)
            r7.append(r12)
            java.lang.String r8 = " where: "
            r7.append(r8)
            r7.append(r13)
            java.lang.String r7 = r7.toString()
            android.util.Log.d(r6, r7)
        L_0x0034:
            com.android.systemui.statusbar.policy.DeviceProvisionedController r7 = r11.mDeviceProvisionedController
            if (r7 == 0) goto L_0x0093
            boolean r7 = r7.isCurrentUserSetup()
            if (r7 != 0) goto L_0x0093
            if (r2 == 0) goto L_0x0057
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r0 = "Wallpaper color event deferred until setup is finished: "
            r13.append(r0)
            r13.append(r12)
            java.lang.String r12 = r13.toString()
            android.util.Log.i(r6, r12)
            r11.mDeferredThemeEvaluation = r4
            return
        L_0x0057:
            boolean r7 = r11.mDeferredThemeEvaluation
            if (r7 == 0) goto L_0x0070
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r13 = "Wallpaper color event received, but we already were deferring eval: "
            r11.append(r13)
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            android.util.Log.i(r6, r11)
            return
        L_0x0070:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r7 = "During user setup, but allowing first color event: had? "
            r12.append(r7)
            r12.append(r2)
            java.lang.String r2 = " has? "
            r12.append(r2)
            android.app.WallpaperColors r2 = r11.mCurrentColors
            if (r2 == 0) goto L_0x0088
            r2 = r4
            goto L_0x0089
        L_0x0088:
            r2 = r3
        L_0x0089:
            r12.append(r2)
            java.lang.String r12 = r12.toString()
            android.util.Log.i(r6, r12)
        L_0x0093:
            com.android.systemui.settings.UserTracker r12 = r11.mUserTracker
            int r12 = r12.getUserId()
            com.android.systemui.util.settings.SecureSettings r2 = r11.mSecureSettings
            java.lang.String r7 = "theme_customization_overlay_packages"
            java.lang.String r12 = r2.getStringForUser(r7, r12)
            r2 = 3
            if (r13 != r2) goto L_0x00a6
            r2 = r4
            goto L_0x00a7
        L_0x00a6:
            r2 = r3
        L_0x00a7:
            if (r12 != 0) goto L_0x00af
            org.json.JSONObject r8 = new org.json.JSONObject     // Catch:{ JSONException -> 0x012d }
            r8.<init>()     // Catch:{ JSONException -> 0x012d }
            goto L_0x00b4
        L_0x00af:
            org.json.JSONObject r8 = new org.json.JSONObject     // Catch:{ JSONException -> 0x012d }
            r8.<init>(r12)     // Catch:{ JSONException -> 0x012d }
        L_0x00b4:
            java.lang.String r9 = "preset"
            java.lang.String r10 = r8.optString(r1)     // Catch:{ JSONException -> 0x012d }
            boolean r9 = r9.equals(r10)     // Catch:{ JSONException -> 0x012d }
            if (r9 != 0) goto L_0x0133
            if (r5 == 0) goto L_0x0133
            r11.mSkipSettingChange = r4     // Catch:{ JSONException -> 0x012d }
            boolean r4 = r8.has(r0)     // Catch:{ JSONException -> 0x012d }
            java.lang.String r5 = "android.theme.customization.system_palette"
            if (r4 != 0) goto L_0x00d2
            boolean r4 = r8.has(r5)     // Catch:{ JSONException -> 0x012d }
            if (r4 == 0) goto L_0x00dd
        L_0x00d2:
            r8.remove(r0)     // Catch:{ JSONException -> 0x012d }
            r8.remove(r5)     // Catch:{ JSONException -> 0x012d }
            java.lang.String r0 = "android.theme.customization.color_index"
            r8.remove(r0)     // Catch:{ JSONException -> 0x012d }
        L_0x00dd:
            java.lang.String r0 = "android.theme.customization.color_both"
            if (r2 == 0) goto L_0x00e4
            java.lang.String r2 = "1"
            goto L_0x00e6
        L_0x00e4:
            java.lang.String r2 = "0"
        L_0x00e6:
            r8.put(r0, r2)     // Catch:{ JSONException -> 0x012d }
            r0 = 2
            if (r13 != r0) goto L_0x00ef
            java.lang.String r13 = "lock_wallpaper"
            goto L_0x00f1
        L_0x00ef:
            java.lang.String r13 = "home_wallpaper"
        L_0x00f1:
            r8.put(r1, r13)     // Catch:{ JSONException -> 0x012d }
            java.lang.String r13 = "_applied_timestamp"
            long r0 = java.lang.System.currentTimeMillis()     // Catch:{ JSONException -> 0x012d }
            r8.put(r13, r0)     // Catch:{ JSONException -> 0x012d }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x012d }
            r13.<init>()     // Catch:{ JSONException -> 0x012d }
            java.lang.String r0 = "Updating theme setting from "
            r13.append(r0)     // Catch:{ JSONException -> 0x012d }
            r13.append(r12)     // Catch:{ JSONException -> 0x012d }
            java.lang.String r12 = " to "
            r13.append(r12)     // Catch:{ JSONException -> 0x012d }
            java.lang.String r12 = r8.toString()     // Catch:{ JSONException -> 0x012d }
            r13.append(r12)     // Catch:{ JSONException -> 0x012d }
            java.lang.String r12 = r13.toString()     // Catch:{ JSONException -> 0x012d }
            android.util.Log.d(r6, r12)     // Catch:{ JSONException -> 0x012d }
            com.android.systemui.util.settings.SecureSettings r12 = r11.mSecureSettings     // Catch:{ JSONException -> 0x012d }
            java.lang.String r13 = r8.toString()     // Catch:{ JSONException -> 0x012d }
            com.android.systemui.settings.UserTracker r0 = r11.mUserTracker     // Catch:{ JSONException -> 0x012d }
            int r0 = r0.getUserId()     // Catch:{ JSONException -> 0x012d }
            r12.putStringForUser(r7, r13, r0)     // Catch:{ JSONException -> 0x012d }
            goto L_0x0133
        L_0x012d:
            r12 = move-exception
            java.lang.String r13 = "Failed to parse THEME_CUSTOMIZATION_OVERLAY_PACKAGES."
            android.util.Log.i(r6, r13, r12)
        L_0x0133:
            r11.reevaluateSystemTheme(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.theme.ThemeOverlayController.handleWallpaperColors(android.app.WallpaperColors, int):void");
    }

    /* access modifiers changed from: private */
    public boolean handleDefaultPresetColor(boolean z) {
        int userId = this.mUserTracker.getUserId();
        String stringForUser = this.mSecureSettings.getStringForUser("theme_customization_overlay_packages", userId);
        if (!TextUtils.isEmpty(stringForUser)) {
            Log.i("ThemeOverlayController", "The start theme:" + stringForUser);
            if (!isRJson(stringForUser)) {
                return false;
            }
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("android.theme.customization.accent_color", this.mContext.getString(R$string.default_accent_color));
            jSONObject.put("android.theme.customization.system_palette", this.mContext.getString(R$string.default_palette_color));
            jSONObject.put("android.theme.customization.color_source", "preset");
            jSONObject.put("_applied_timestamp", System.currentTimeMillis());
            this.mSecureSettings.putStringForUser("theme_customization_overlay_packages", jSONObject.toString(), userId);
            if (z) {
                this.mSkipSettingChange = true;
            }
        } catch (JSONException e) {
            Log.i("ThemeOverlayController", "Failed to parse handleDefaultPresetColor.", e);
        }
        return true;
    }

    private boolean isRJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.has("android.theme.customization.system_palette") && !jSONObject.has("android.theme.customization.color_source")) {
                Log.e("ThemeOverlayController", "isRJson:" + str);
                return true;
            }
        } catch (JSONException e) {
            Log.e("ThemeOverlayController", "isRJson Failed to parse THEME_CUSTOMIZATION_OVERLAY_PACKAGES.", e);
        }
        return false;
    }

    public ThemeOverlayController(Context context, BroadcastDispatcher broadcastDispatcher, Handler handler, Executor executor, Executor executor2, ThemeOverlayApplier themeOverlayApplier, SecureSettings secureSettings, WallpaperManager wallpaperManager, UserManager userManager, DeviceProvisionedController deviceProvisionedController, UserTracker userTracker, DumpManager dumpManager, FeatureFlags featureFlags, WakefulnessLifecycle wakefulnessLifecycle) {
        super(context);
        this.mIsMonetEnabled = featureFlags.isMonetEnabled();
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mUserManager = userManager;
        this.mBgExecutor = executor2;
        this.mMainExecutor = executor;
        this.mBgHandler = handler;
        this.mThemeManager = themeOverlayApplier;
        this.mSecureSettings = secureSettings;
        this.mWallpaperManager = wallpaperManager;
        this.mUserTracker = userTracker;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        dumpManager.registerDumpable("ThemeOverlayController", this);
    }

    public void start() {
        Log.d("ThemeOverlayController", "Start");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        intentFilter.addAction("android.intent.action.WALLPAPER_CHANGED");
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter, this.mMainExecutor, UserHandle.ALL);
        this.mSecureSettings.registerContentObserverForUser(Settings.Secure.getUriFor("theme_customization_overlay_packages"), false, new ContentObserver(this.mBgHandler) {
            public void onChange(boolean z, Collection<Uri> collection, int i, int i2) {
                Log.d("ThemeOverlayController", "Overlay changed for user: " + i2);
                if (ThemeOverlayController.this.mUserTracker.getUserId() == i2) {
                    if (!ThemeOverlayController.this.mDeviceProvisionedController.isUserSetup(i2)) {
                        Log.i("ThemeOverlayController", "Theme application deferred when setting changed.");
                        boolean unused = ThemeOverlayController.this.mDeferredThemeEvaluation = true;
                    } else if (ThemeOverlayController.this.mSkipSettingChange) {
                        Log.d("ThemeOverlayController", "Skipping setting change");
                        boolean unused2 = ThemeOverlayController.this.mSkipSettingChange = false;
                    } else {
                        ThemeOverlayController.this.reevaluateSystemTheme(true);
                    }
                }
            }
        }, -1);
        if (this.mIsMonetEnabled) {
            handleDefaultPresetColor(false);
            this.mDeviceProvisionedController.addCallback(this.mDeviceProvisionedListener);
            ThemeOverlayController$$ExternalSyntheticLambda1 themeOverlayController$$ExternalSyntheticLambda1 = new ThemeOverlayController$$ExternalSyntheticLambda1(this);
            if (!this.mDeviceProvisionedController.isCurrentUserSetup()) {
                themeOverlayController$$ExternalSyntheticLambda1.run();
            } else {
                this.mBgExecutor.execute(themeOverlayController$$ExternalSyntheticLambda1);
            }
            this.mWallpaperManager.addOnColorsChangedListener(this.mOnColorsChangedListener, (Handler) null);
            this.mWakefulnessLifecycle.addObserver(new WakefulnessLifecycle.Observer() {
                public void onFinishedGoingToSleep() {
                    if (ThemeOverlayController.this.mDeferredWallpaperColors != null) {
                        WallpaperColors access$800 = ThemeOverlayController.this.mDeferredWallpaperColors;
                        int access$900 = ThemeOverlayController.this.mDeferredWallpaperColorsFlags;
                        WallpaperColors unused = ThemeOverlayController.this.mDeferredWallpaperColors = null;
                        int unused2 = ThemeOverlayController.this.mDeferredWallpaperColorsFlags = 0;
                        ThemeOverlayController.this.handleWallpaperColors(access$800, access$900);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$start$2() {
        ThemeOverlayController$$ExternalSyntheticLambda2 themeOverlayController$$ExternalSyntheticLambda2 = new ThemeOverlayController$$ExternalSyntheticLambda2(this, this.mWallpaperManager.getWallpaperColors(getLatestWallpaperType()));
        if (this.mDeviceProvisionedController.isCurrentUserSetup()) {
            this.mMainExecutor.execute(themeOverlayController$$ExternalSyntheticLambda2);
        } else {
            themeOverlayController$$ExternalSyntheticLambda2.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$start$1(WallpaperColors wallpaperColors) {
        Log.d("ThemeOverlayController", "Boot colors: " + wallpaperColors);
        this.mCurrentColors = wallpaperColors;
        reevaluateSystemTheme(false);
    }

    /* access modifiers changed from: private */
    public void reevaluateSystemTheme(boolean z) {
        int i;
        int i2;
        int i3;
        int i4;
        WallpaperColors wallpaperColors = this.mCurrentColors;
        if (wallpaperColors == null) {
            i2 = 0;
            i = 0;
        } else {
            i = getNeutralColor(wallpaperColors);
            i2 = getAccentColor(wallpaperColors);
        }
        WallpaperColors wallpaperColors2 = null;
        if (!MotoDesktopManager.isDesktopSupported() || (wallpaperColors2 = this.mWallpaperManager.getWallpaperColors(8)) == null) {
            i4 = 0;
            i3 = 0;
        } else {
            i4 = getNeutralColor(wallpaperColors2);
            i3 = getAccentColor(wallpaperColors2);
        }
        if (!z && this.mMainWallpaperColor == i && this.mWallpaperAccentColor == i2) {
            Log.d("ThemeOverlayController", "mMainWallpaperColor == mainColor");
            if (!MotoDesktopManager.isDesktopSupported()) {
                return;
            }
            if (this.mMainDesktopWallpaperColor == i4 && this.mDesktopWallpaperAccentColor == i3) {
                Log.d("ThemeOverlayController", "mMainDesktopWallpaperColor == mainDesktopColor");
                return;
            }
            Log.d("ThemeOverlayController", "got new desktop color" + wallpaperColors2);
        }
        this.mMainWallpaperColor = i;
        this.mWallpaperAccentColor = i2;
        if (MotoDesktopManager.isDesktopSupported()) {
            this.mMainDesktopWallpaperColor = i4;
            this.mDesktopWallpaperAccentColor = i3;
            if (!z && this.mPreset) {
                Log.d("ThemeOverlayController", "Preset color, just update color, not need to update theme!");
                return;
            }
        }
        if (this.mIsMonetEnabled) {
            if (MotoDesktopManager.isDesktopSupported()) {
                this.mSecondaryOverlay = getOverlay(this.mWallpaperAccentColor, this.mDesktopWallpaperAccentColor, 1);
                this.mNeutralOverlay = getOverlay(this.mMainWallpaperColor, this.mMainDesktopWallpaperColor, 0);
            } else {
                this.mSecondaryOverlay = getOverlay(this.mWallpaperAccentColor, 1);
                this.mNeutralOverlay = getOverlay(this.mMainWallpaperColor, 0);
            }
            this.mNeedsOverlayCreation = true;
            Log.d("ThemeOverlayController", "fetched overlays. accent: " + this.mSecondaryOverlay + " neutral: " + this.mNeutralOverlay);
        }
        updateThemeOverlays();
    }

    /* access modifiers changed from: protected */
    public int getNeutralColor(WallpaperColors wallpaperColors) {
        return ColorScheme.getSeedColor(wallpaperColors);
    }

    /* access modifiers changed from: protected */
    public int getAccentColor(WallpaperColors wallpaperColors) {
        return ColorScheme.getSeedColor(wallpaperColors);
    }

    /* access modifiers changed from: protected */
    public FabricatedOverlay getOverlay(int i, int i2) {
        return getOverlay(i, 0, i2);
    }

    /* access modifiers changed from: protected */
    public FabricatedOverlay getOverlay(int i, int i2, int i3) {
        String[] strArr = ThemeOverlayApplier.PREFIX_RESOURCE;
        FabricatedOverlay.Builder overlayBuilder = getOverlayBuilder((FabricatedOverlay.Builder) null, i, i3, strArr[0]);
        if (i2 != 0) {
            overlayBuilder = getOverlayBuilder(getOverlayBuilder(overlayBuilder, i2, i3, strArr[1]), i2, i3, strArr[2]);
        }
        return overlayBuilder.build();
    }

    private FabricatedOverlay.Builder getOverlayBuilder(FabricatedOverlay.Builder builder, int i, int i2, String str) {
        String str2;
        ColorScheme colorScheme = new ColorScheme(i, (this.mContext.getResources().getConfiguration().uiMode & 48) == 32);
        this.mColorScheme = colorScheme;
        List<Integer> allAccentColors = i2 == 1 ? colorScheme.getAllAccentColors() : colorScheme.getAllNeutralColors();
        String str3 = i2 == 1 ? "accent" : "neutral";
        if (builder == null) {
            builder = new FabricatedOverlay.Builder("com.android.systemui", str3, "android");
        }
        int size = this.mColorScheme.getAccent1().size();
        for (int i3 = 0; i3 < allAccentColors.size(); i3++) {
            int i4 = i3 % size;
            int i5 = (i3 / size) + 1;
            if (i4 == 0) {
                str2 = str + str3 + i5 + "_10";
            } else if (i4 != 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(str3);
                sb.append(i5);
                sb.append("_");
                sb.append(i4 - 1);
                sb.append("00");
                str2 = sb.toString();
            } else {
                str2 = str + str3 + i5 + "_50";
            }
            builder.setResourceValue(str2, 28, ColorUtils.setAlphaComponent(allAccentColors.get(i3).intValue(), 255));
        }
        return builder;
    }

    private void updateThemeOverlays() {
        FabricatedOverlay fabricatedOverlay;
        FabricatedOverlay fabricatedOverlay2;
        int userId = this.mUserTracker.getUserId();
        String stringForUser = this.mSecureSettings.getStringForUser("theme_customization_overlay_packages", userId);
        Log.d("ThemeOverlayController", "updateThemeOverlays. Setting: " + stringForUser);
        ArrayMap arrayMap = new ArrayMap();
        if (!TextUtils.isEmpty(stringForUser)) {
            if (!isRJson(stringForUser)) {
                try {
                    JSONObject jSONObject = new JSONObject(stringForUser);
                    for (String next : ThemeOverlayApplier.THEME_CATEGORIES) {
                        if (jSONObject.has(next)) {
                            arrayMap.put(next, new OverlayIdentifier(jSONObject.getString(next)));
                        }
                    }
                    this.mPreset = "preset".equals(jSONObject.optString("android.theme.customization.color_source"));
                } catch (JSONException e) {
                    Log.i("ThemeOverlayController", "Failed to parse THEME_CUSTOMIZATION_OVERLAY_PACKAGES.", e);
                }
            } else {
                return;
            }
        }
        OverlayIdentifier overlayIdentifier = (OverlayIdentifier) arrayMap.get("android.theme.customization.system_palette");
        if (this.mIsMonetEnabled && overlayIdentifier != null && overlayIdentifier.getPackageName() != null) {
            try {
                String lowerCase = overlayIdentifier.getPackageName().toLowerCase();
                if (!lowerCase.startsWith("#")) {
                    lowerCase = "#" + lowerCase;
                }
                int parseColor = Color.parseColor(lowerCase);
                if (!MotoDesktopManager.isDesktopSupported() || this.mPreset) {
                    this.mNeutralOverlay = getOverlay(parseColor, 0);
                } else {
                    this.mNeutralOverlay = getOverlay(parseColor, this.mMainDesktopWallpaperColor, 0);
                }
                this.mNeedsOverlayCreation = true;
                arrayMap.remove("android.theme.customization.system_palette");
            } catch (Exception e2) {
                Log.w("ThemeOverlayController", "Invalid color definition: " + overlayIdentifier.getPackageName(), e2);
            }
        } else if (!this.mIsMonetEnabled && overlayIdentifier != null) {
            try {
                arrayMap.remove("android.theme.customization.system_palette");
            } catch (NumberFormatException unused) {
            }
        }
        OverlayIdentifier overlayIdentifier2 = (OverlayIdentifier) arrayMap.get("android.theme.customization.accent_color");
        if (this.mIsMonetEnabled && overlayIdentifier2 != null && overlayIdentifier2.getPackageName() != null) {
            try {
                String lowerCase2 = overlayIdentifier2.getPackageName().toLowerCase();
                if (!lowerCase2.startsWith("#")) {
                    lowerCase2 = "#" + lowerCase2;
                }
                int parseColor2 = Color.parseColor(lowerCase2);
                if (!MotoDesktopManager.isDesktopSupported() || this.mPreset) {
                    this.mSecondaryOverlay = getOverlay(parseColor2, 1);
                } else {
                    this.mSecondaryOverlay = getOverlay(parseColor2, this.mDesktopWallpaperAccentColor, 1);
                }
                this.mNeedsOverlayCreation = true;
                arrayMap.remove("android.theme.customization.accent_color");
            } catch (Exception e3) {
                Log.w("ThemeOverlayController", "Invalid color definition: " + overlayIdentifier2.getPackageName(), e3);
            }
        } else if (!this.mIsMonetEnabled && overlayIdentifier2 != null) {
            try {
                Integer.parseInt(overlayIdentifier2.getPackageName().toLowerCase(), 16);
                arrayMap.remove("android.theme.customization.accent_color");
            } catch (NumberFormatException unused2) {
            }
        }
        if (!arrayMap.containsKey("android.theme.customization.system_palette") && (fabricatedOverlay2 = this.mNeutralOverlay) != null) {
            arrayMap.put("android.theme.customization.system_palette", fabricatedOverlay2.getIdentifier());
        }
        if (!arrayMap.containsKey("android.theme.customization.accent_color") && (fabricatedOverlay = this.mSecondaryOverlay) != null) {
            arrayMap.put("android.theme.customization.accent_color", fabricatedOverlay.getIdentifier());
        }
        HashSet hashSet = new HashSet();
        for (UserInfo userInfo : this.mUserManager.getEnabledProfiles(userId)) {
            if (userInfo.isManagedProfile()) {
                hashSet.add(userInfo.getUserHandle());
            }
        }
        Log.d("ThemeOverlayController", "Applying overlays: " + ((String) arrayMap.keySet().stream().map(new ThemeOverlayController$$ExternalSyntheticLambda3(arrayMap)).collect(Collectors.joining(", "))));
        if (this.mNeedsOverlayCreation) {
            this.mNeedsOverlayCreation = false;
            this.mThemeManager.applyCurrentUserOverlays(arrayMap, new FabricatedOverlay[]{this.mSecondaryOverlay, this.mNeutralOverlay}, userId, hashSet);
            return;
        }
        this.mThemeManager.applyCurrentUserOverlays(arrayMap, (FabricatedOverlay[]) null, userId, hashSet);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$updateThemeOverlays$3(Map map, String str) {
        return str + " -> " + map.get(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("mSystemColors=" + this.mCurrentColors);
        printWriter.println("mMainWallpaperColor=" + Integer.toHexString(this.mMainWallpaperColor));
        printWriter.println("mWallpaperAccentColor=" + Integer.toHexString(this.mWallpaperAccentColor));
        printWriter.println("mSecondaryOverlay=" + this.mSecondaryOverlay);
        printWriter.println("mNeutralOverlay=" + this.mNeutralOverlay);
        printWriter.println("mIsMonetEnabled=" + this.mIsMonetEnabled);
        printWriter.println("mColorScheme=" + this.mColorScheme);
        printWriter.println("mNeedsOverlayCreation=" + this.mNeedsOverlayCreation);
        printWriter.println("mAcceptColorEvents=" + this.mAcceptColorEvents);
        printWriter.println("mDeferredThemeEvaluation=" + this.mDeferredThemeEvaluation);
    }
}
