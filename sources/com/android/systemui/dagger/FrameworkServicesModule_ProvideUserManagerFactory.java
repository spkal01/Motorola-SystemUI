package com.android.systemui.dagger;

import android.content.Context;
import android.os.UserManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideUserManagerFactory implements Factory<UserManager> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideUserManagerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public UserManager get() {
        return provideUserManager(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideUserManagerFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideUserManagerFactory(provider);
    }

    public static UserManager provideUserManager(Context context) {
        return (UserManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideUserManager(context));
    }
}
