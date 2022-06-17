package com.android.systemui.statusbar.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.motorola.android.provider.MotorolaSettings;
import java.util.HashSet;

public class CliNotificationSettings {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!"user".equals(Build.TYPE));
    private static CliNotificationSettings sInstance;
    private HashSet<String> mBlockedApps = new HashSet<>();
    private final ContentObserver mContentObserver;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public boolean mHapticsEnabled;
    /* access modifiers changed from: private */
    public boolean mSafeReplyEnabled;
    private HashSet<String> mSettingsBlockedApps;

    public static CliNotificationSettings getInstance(Context context) {
        synchronized (CliNotificationSettings.class) {
            if (sInstance == null) {
                sInstance = new CliNotificationSettings(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    public CliNotificationSettings(Context context) {
        boolean z = true;
        this.mSafeReplyEnabled = true;
        this.mHapticsEnabled = true;
        this.mSettingsBlockedApps = new HashSet<>();
        C15111 r1 = new ContentObserver(new Handler()) {
            public void onChange(boolean z, Uri uri) {
                if (uri.equals(MotorolaSettings.Secure.getUriFor("aod_notifications_blocked_apps"))) {
                    CliNotificationSettings.this.loadSettingsBlockedApps();
                } else {
                    boolean z2 = false;
                    if (uri.equals(MotorolaSettings.Secure.getUriFor("is_safe_reply_enable"))) {
                        CliNotificationSettings cliNotificationSettings = CliNotificationSettings.this;
                        if (MotorolaSettings.Secure.getInt(cliNotificationSettings.mContext.getContentResolver(), "is_safe_reply_enable", 1) == 1) {
                            z2 = true;
                        }
                        boolean unused = cliNotificationSettings.mSafeReplyEnabled = z2;
                    } else if (uri.equals(MotorolaSettings.Secure.getUriFor("is_vibrate_on_touch_enable"))) {
                        CliNotificationSettings cliNotificationSettings2 = CliNotificationSettings.this;
                        if (MotorolaSettings.Secure.getInt(cliNotificationSettings2.mContext.getContentResolver(), "is_vibrate_on_touch_enable", 1) == 1) {
                            z2 = true;
                        }
                        boolean unused2 = cliNotificationSettings2.mHapticsEnabled = z2;
                    }
                }
                if (CliNotificationSettings.DEBUG) {
                    Log.d("Cli_NotificationSettings", "changed=;mSafeReplyEnabled=" + CliNotificationSettings.this.mSafeReplyEnabled + ";mHapticsEnabled=" + CliNotificationSettings.this.mHapticsEnabled);
                }
            }
        };
        this.mContentObserver = r1;
        this.mContext = context;
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(MotorolaSettings.Secure.getUriFor("aod_notifications_blocked_apps"), false, r1);
        loadSettingsBlockedApps();
        contentResolver.registerContentObserver(MotorolaSettings.Secure.getUriFor("is_safe_reply_enable"), false, r1);
        this.mSafeReplyEnabled = MotorolaSettings.Secure.getInt(contentResolver, "is_safe_reply_enable", 1) == 1;
        contentResolver.registerContentObserver(MotorolaSettings.Secure.getUriFor("is_vibrate_on_touch_enable"), false, r1);
        this.mHapticsEnabled = MotorolaSettings.Secure.getInt(contentResolver, "is_vibrate_on_touch_enable", 1) != 1 ? false : z;
        if (DEBUG) {
            Log.d("Cli_NotificationSettings", "init=;mSafeReplyEnabled=" + this.mSafeReplyEnabled + ";mHapticsEnabled=" + this.mHapticsEnabled);
        }
    }

    /* access modifiers changed from: private */
    public void loadSettingsBlockedApps() {
        String string = MotorolaSettings.Secure.getString(this.mContext.getContentResolver(), "aod_notifications_blocked_apps");
        if (DEBUG) {
            Log.d("Cli_NotificationSettings", "blockedAppsStr=" + string);
        }
        synchronized (this.mBlockedApps) {
            this.mSettingsBlockedApps.clear();
            if (!TextUtils.isEmpty(string)) {
                for (String add : string.split(";")) {
                    this.mSettingsBlockedApps.add(add);
                }
            }
        }
    }

    public boolean isBlockedApp(String str) {
        boolean contains;
        synchronized (this.mBlockedApps) {
            contains = this.mBlockedApps.contains(str);
        }
        return contains;
    }

    public boolean isSafeReplyEnabled() {
        return this.mSafeReplyEnabled;
    }

    public boolean isHapticsEnabled() {
        return this.mHapticsEnabled;
    }
}
