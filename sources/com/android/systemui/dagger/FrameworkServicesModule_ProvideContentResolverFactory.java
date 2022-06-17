package com.android.systemui.dagger;

import android.content.ContentResolver;
import android.content.Context;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideContentResolverFactory implements Factory<ContentResolver> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideContentResolverFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public ContentResolver get() {
        return provideContentResolver(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideContentResolverFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideContentResolverFactory(provider);
    }

    public static ContentResolver provideContentResolver(Context context) {
        return (ContentResolver) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideContentResolver(context));
    }
}
