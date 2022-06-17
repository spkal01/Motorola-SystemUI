package com.android.systemui.util.settings;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;

class SystemSettingsImpl implements SystemSettings {
    private final ContentResolver mContentResolver;

    SystemSettingsImpl(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }

    public ContentResolver getContentResolver() {
        return this.mContentResolver;
    }

    public Uri getUriFor(String str) {
        return Settings.System.getUriFor(str);
    }

    public String getStringForUser(String str, int i) {
        return Settings.System.getStringForUser(this.mContentResolver, str, i);
    }

    public boolean putStringForUser(String str, String str2, int i) {
        return Settings.System.putStringForUser(this.mContentResolver, str, str2, i);
    }
}
