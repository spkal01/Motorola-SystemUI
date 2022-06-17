package com.android.systemui.util.settings;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;

class SecureSettingsImpl implements SecureSettings {
    private final ContentResolver mContentResolver;

    SecureSettingsImpl(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }

    public ContentResolver getContentResolver() {
        return this.mContentResolver;
    }

    public Uri getUriFor(String str) {
        return Settings.Secure.getUriFor(str);
    }

    public String getStringForUser(String str, int i) {
        return Settings.Secure.getStringForUser(this.mContentResolver, str, i);
    }

    public boolean putStringForUser(String str, String str2, int i) {
        return Settings.Secure.putStringForUser(this.mContentResolver, str, str2, i);
    }

    public boolean putStringForUser(String str, String str2, String str3, boolean z, int i, boolean z2) {
        return Settings.Secure.putStringForUser(this.mContentResolver, str, str2, str3, z, i, z2);
    }
}
