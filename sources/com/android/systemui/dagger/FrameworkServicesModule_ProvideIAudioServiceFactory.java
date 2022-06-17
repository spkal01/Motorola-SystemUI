package com.android.systemui.dagger;

import android.media.IAudioService;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class FrameworkServicesModule_ProvideIAudioServiceFactory implements Factory<IAudioService> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final FrameworkServicesModule_ProvideIAudioServiceFactory INSTANCE = new FrameworkServicesModule_ProvideIAudioServiceFactory();
    }

    public IAudioService get() {
        return provideIAudioService();
    }

    public static FrameworkServicesModule_ProvideIAudioServiceFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static IAudioService provideIAudioService() {
        return (IAudioService) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideIAudioService());
    }
}
