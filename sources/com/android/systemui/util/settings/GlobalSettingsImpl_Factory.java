package com.android.systemui.util.settings;

import android.content.ContentResolver;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class GlobalSettingsImpl_Factory implements Factory<GlobalSettingsImpl> {
    private final Provider<ContentResolver> contentResolverProvider;

    public GlobalSettingsImpl_Factory(Provider<ContentResolver> provider) {
        this.contentResolverProvider = provider;
    }

    public GlobalSettingsImpl get() {
        return newInstance(this.contentResolverProvider.get());
    }

    public static GlobalSettingsImpl_Factory create(Provider<ContentResolver> provider) {
        return new GlobalSettingsImpl_Factory(provider);
    }

    public static GlobalSettingsImpl newInstance(ContentResolver contentResolver) {
        return new GlobalSettingsImpl(contentResolver);
    }
}
