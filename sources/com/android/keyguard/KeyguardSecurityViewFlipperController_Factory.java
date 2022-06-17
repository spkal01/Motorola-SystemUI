package com.android.keyguard;

import com.android.keyguard.EmergencyButtonController;
import com.android.keyguard.KeyguardInputViewController;
import com.android.systemui.moto.DisplayLayoutInflater;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class KeyguardSecurityViewFlipperController_Factory implements Factory<KeyguardSecurityViewFlipperController> {
    private final Provider<DisplayLayoutInflater> displayLayoutInflaterProvider;
    private final Provider<EmergencyButtonController.Factory> emergencyButtonControllerFactoryProvider;
    private final Provider<KeyguardInputViewController.Factory> keyguardSecurityViewControllerFactoryProvider;
    private final Provider<KeyguardSecurityViewFlipper> viewProvider;

    public KeyguardSecurityViewFlipperController_Factory(Provider<KeyguardSecurityViewFlipper> provider, Provider<DisplayLayoutInflater> provider2, Provider<KeyguardInputViewController.Factory> provider3, Provider<EmergencyButtonController.Factory> provider4) {
        this.viewProvider = provider;
        this.displayLayoutInflaterProvider = provider2;
        this.keyguardSecurityViewControllerFactoryProvider = provider3;
        this.emergencyButtonControllerFactoryProvider = provider4;
    }

    public KeyguardSecurityViewFlipperController get() {
        return newInstance(this.viewProvider.get(), this.displayLayoutInflaterProvider.get(), this.keyguardSecurityViewControllerFactoryProvider.get(), this.emergencyButtonControllerFactoryProvider.get());
    }

    public static KeyguardSecurityViewFlipperController_Factory create(Provider<KeyguardSecurityViewFlipper> provider, Provider<DisplayLayoutInflater> provider2, Provider<KeyguardInputViewController.Factory> provider3, Provider<EmergencyButtonController.Factory> provider4) {
        return new KeyguardSecurityViewFlipperController_Factory(provider, provider2, provider3, provider4);
    }

    public static KeyguardSecurityViewFlipperController newInstance(KeyguardSecurityViewFlipper keyguardSecurityViewFlipper, DisplayLayoutInflater displayLayoutInflater, KeyguardInputViewController.Factory factory, EmergencyButtonController.Factory factory2) {
        return new KeyguardSecurityViewFlipperController(keyguardSecurityViewFlipper, displayLayoutInflater, factory, factory2);
    }
}
