package com.android.keyguard.dagger;

import android.view.ViewGroup;
import com.android.keyguard.KeyguardHostView;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class KeyguardBouncerModule_ProvidesKeyguardHostViewFactory implements Factory<KeyguardHostView> {
    private final Provider<ViewGroup> rootViewProvider;

    public KeyguardBouncerModule_ProvidesKeyguardHostViewFactory(Provider<ViewGroup> provider) {
        this.rootViewProvider = provider;
    }

    public KeyguardHostView get() {
        return providesKeyguardHostView(this.rootViewProvider.get());
    }

    public static KeyguardBouncerModule_ProvidesKeyguardHostViewFactory create(Provider<ViewGroup> provider) {
        return new KeyguardBouncerModule_ProvidesKeyguardHostViewFactory(provider);
    }

    public static KeyguardHostView providesKeyguardHostView(ViewGroup viewGroup) {
        return (KeyguardHostView) Preconditions.checkNotNullFromProvides(KeyguardBouncerModule.providesKeyguardHostView(viewGroup));
    }
}
