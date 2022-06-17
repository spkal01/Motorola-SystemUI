package com.android.systemui.statusbar.phone;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class AudioFxControllerImpl_Factory implements Factory<AudioFxControllerImpl> {
    private final Provider<Context> contextProvider;

    public AudioFxControllerImpl_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public AudioFxControllerImpl get() {
        return newInstance(this.contextProvider.get());
    }

    public static AudioFxControllerImpl_Factory create(Provider<Context> provider) {
        return new AudioFxControllerImpl_Factory(provider);
    }

    public static AudioFxControllerImpl newInstance(Context context) {
        return new AudioFxControllerImpl(context);
    }
}
