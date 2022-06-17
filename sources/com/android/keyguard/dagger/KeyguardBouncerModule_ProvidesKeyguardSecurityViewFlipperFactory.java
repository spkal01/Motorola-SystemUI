package com.android.keyguard.dagger;

import com.android.keyguard.KeyguardSecurityContainer;
import com.android.keyguard.KeyguardSecurityViewFlipper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class KeyguardBouncerModule_ProvidesKeyguardSecurityViewFlipperFactory implements Factory<KeyguardSecurityViewFlipper> {
    private final Provider<KeyguardSecurityContainer> containerViewProvider;

    public KeyguardBouncerModule_ProvidesKeyguardSecurityViewFlipperFactory(Provider<KeyguardSecurityContainer> provider) {
        this.containerViewProvider = provider;
    }

    public KeyguardSecurityViewFlipper get() {
        return providesKeyguardSecurityViewFlipper(this.containerViewProvider.get());
    }

    public static KeyguardBouncerModule_ProvidesKeyguardSecurityViewFlipperFactory create(Provider<KeyguardSecurityContainer> provider) {
        return new KeyguardBouncerModule_ProvidesKeyguardSecurityViewFlipperFactory(provider);
    }

    public static KeyguardSecurityViewFlipper providesKeyguardSecurityViewFlipper(KeyguardSecurityContainer keyguardSecurityContainer) {
        return (KeyguardSecurityViewFlipper) Preconditions.checkNotNullFromProvides(KeyguardBouncerModule.providesKeyguardSecurityViewFlipper(keyguardSecurityContainer));
    }
}
