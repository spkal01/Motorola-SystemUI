package com.android.systemui.accessibility;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class AccessibilityButtonTargetsObserver_Factory implements Factory<AccessibilityButtonTargetsObserver> {
    private final Provider<Context> contextProvider;

    public AccessibilityButtonTargetsObserver_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public AccessibilityButtonTargetsObserver get() {
        return newInstance(this.contextProvider.get());
    }

    public static AccessibilityButtonTargetsObserver_Factory create(Provider<Context> provider) {
        return new AccessibilityButtonTargetsObserver_Factory(provider);
    }

    public static AccessibilityButtonTargetsObserver newInstance(Context context) {
        return new AccessibilityButtonTargetsObserver(context);
    }
}
