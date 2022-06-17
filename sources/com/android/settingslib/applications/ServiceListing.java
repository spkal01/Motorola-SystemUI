package com.android.settingslib.applications;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Slog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ServiceListing {
    private final boolean mAddDeviceLockedFlags;
    private final List<Callback> mCallbacks;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final HashSet<ComponentName> mEnabledServices;
    private final String mIntentAction;
    private boolean mListening;
    private final String mNoun;
    private final BroadcastReceiver mPackageReceiver;
    private final String mPermission;
    private final List<ServiceInfo> mServices;
    private final String mSetting;
    private final ContentObserver mSettingsObserver;
    private final String mTag;

    public interface Callback {
        void onServicesReloaded(List<ServiceInfo> list);
    }

    private ServiceListing(Context context, String str, String str2, String str3, String str4, String str5, boolean z) {
        this.mEnabledServices = new HashSet<>();
        this.mServices = new ArrayList();
        this.mCallbacks = new ArrayList();
        this.mSettingsObserver = new ContentObserver(new Handler()) {
            public void onChange(boolean z, Uri uri) {
                ServiceListing.this.reload();
            }
        };
        this.mPackageReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ServiceListing.this.reload();
            }
        };
        this.mContentResolver = context.getContentResolver();
        this.mContext = context;
        this.mTag = str;
        this.mSetting = str2;
        this.mIntentAction = str3;
        this.mPermission = str4;
        this.mNoun = str5;
        this.mAddDeviceLockedFlags = z;
    }

    public void addCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void setListening(boolean z) {
        if (this.mListening != z) {
            this.mListening = z;
            if (z) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
                intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
                intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
                intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
                intentFilter.addDataScheme("package");
                this.mContext.registerReceiver(this.mPackageReceiver, intentFilter);
                this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor(this.mSetting), false, this.mSettingsObserver);
                return;
            }
            this.mContext.unregisterReceiver(this.mPackageReceiver);
            this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
        }
    }

    private void loadEnabledServices() {
        this.mEnabledServices.clear();
        String string = Settings.Secure.getString(this.mContentResolver, this.mSetting);
        if (string != null && !"".equals(string)) {
            for (String unflattenFromString : string.split(":")) {
                ComponentName unflattenFromString2 = ComponentName.unflattenFromString(unflattenFromString);
                if (unflattenFromString2 != null) {
                    this.mEnabledServices.add(unflattenFromString2);
                }
            }
        }
    }

    public void reload() {
        loadEnabledServices();
        this.mServices.clear();
        int currentUser = ActivityManager.getCurrentUser();
        int i = this.mAddDeviceLockedFlags ? 786564 : 132;
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServicesAsUser = packageManager.queryIntentServicesAsUser(new Intent(this.mIntentAction), i, currentUser);
        addCloneUserInstalledServices(this.mContext, packageManager, queryIntentServicesAsUser, i);
        for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (!this.mPermission.equals(serviceInfo.permission)) {
                String str = this.mTag;
                Slog.w(str, "Skipping " + this.mNoun + " service " + serviceInfo.packageName + "/" + serviceInfo.name + ": it does not require the permission " + this.mPermission);
            } else {
                this.mServices.add(serviceInfo);
            }
        }
        for (Callback onServicesReloaded : this.mCallbacks) {
            onServicesReloaded.onServicesReloaded(this.mServices);
        }
    }

    private void addCloneUserInstalledServices(Context context, PackageManager packageManager, List<ResolveInfo> list, int i) {
        UserManager userManager;
        if (Build.IS_PRC_PRODUCT && (userManager = (UserManager) context.getSystemService(UserManager.class)) != null) {
            for (UserInfo userInfo : userManager.getAppCloneProfiles()) {
                for (ResolveInfo resolveInfo : packageManager.queryIntentServicesAsUser(new Intent(this.mIntentAction), i, userInfo.id)) {
                    if ((resolveInfo.serviceInfo.applicationInfo.flags & 129) == 0) {
                        list.add(resolveInfo);
                    }
                }
            }
        }
    }

    public static class Builder {
        private boolean mAddDeviceLockedFlags = false;
        private final Context mContext;
        private String mIntentAction;
        private String mNoun;
        private String mPermission;
        private String mSetting;
        private String mTag;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTag(String str) {
            this.mTag = str;
            return this;
        }

        public Builder setSetting(String str) {
            this.mSetting = str;
            return this;
        }

        public Builder setIntentAction(String str) {
            this.mIntentAction = str;
            return this;
        }

        public Builder setPermission(String str) {
            this.mPermission = str;
            return this;
        }

        public Builder setNoun(String str) {
            this.mNoun = str;
            return this;
        }

        public Builder setAddDeviceLockedFlags(boolean z) {
            this.mAddDeviceLockedFlags = z;
            return this;
        }

        public ServiceListing build() {
            return new ServiceListing(this.mContext, this.mTag, this.mSetting, this.mIntentAction, this.mPermission, this.mNoun, this.mAddDeviceLockedFlags);
        }
    }
}
