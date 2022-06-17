package com.android.keyguard;

import android.view.accessibility.AccessibilityManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class LiftToActivateListener_Factory implements Factory<LiftToActivateListener> {
    private final Provider<AccessibilityManager> accessibilityManagerProvider;

    public LiftToActivateListener_Factory(Provider<AccessibilityManager> provider) {
        this.accessibilityManagerProvider = provider;
    }

    public LiftToActivateListener get() {
        return newInstance(this.accessibilityManagerProvider.get());
    }

    public static LiftToActivateListener_Factory create(Provider<AccessibilityManager> provider) {
        return new LiftToActivateListener_Factory(provider);
    }

    public static LiftToActivateListener newInstance(AccessibilityManager accessibilityManager) {
        return new LiftToActivateListener(accessibilityManager);
    }
}
