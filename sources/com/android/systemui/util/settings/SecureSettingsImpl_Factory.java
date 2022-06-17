package com.android.systemui.util.settings;

import android.content.ContentResolver;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class SecureSettingsImpl_Factory implements Factory<SecureSettingsImpl> {
    private final Provider<ContentResolver> contentResolverProvider;

    public SecureSettingsImpl_Factory(Provider<ContentResolver> provider) {
        this.contentResolverProvider = provider;
    }

    public SecureSettingsImpl get() {
        return newInstance(this.contentResolverProvider.get());
    }

    public static SecureSettingsImpl_Factory create(Provider<ContentResolver> provider) {
        return new SecureSettingsImpl_Factory(provider);
    }

    public static SecureSettingsImpl newInstance(ContentResolver contentResolver) {
        return new SecureSettingsImpl(contentResolver);
    }
}
