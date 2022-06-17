package com.android.keyguard.dagger;

import android.view.ViewGroup;
import com.android.systemui.moto.DisplayLayoutInflater;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class KeyguardBouncerModule_ProvidesRootViewFactory implements Factory<ViewGroup> {
    private final Provider<DisplayLayoutInflater> displayLayoutInflaterProvider;

    public KeyguardBouncerModule_ProvidesRootViewFactory(Provider<DisplayLayoutInflater> provider) {
        this.displayLayoutInflaterProvider = provider;
    }

    public ViewGroup get() {
        return providesRootView(this.displayLayoutInflaterProvider.get());
    }

    public static KeyguardBouncerModule_ProvidesRootViewFactory create(Provider<DisplayLayoutInflater> provider) {
        return new KeyguardBouncerModule_ProvidesRootViewFactory(provider);
    }

    public static ViewGroup providesRootView(DisplayLayoutInflater displayLayoutInflater) {
        return (ViewGroup) Preconditions.checkNotNullFromProvides(KeyguardBouncerModule.providesRootView(displayLayoutInflater));
    }
}
