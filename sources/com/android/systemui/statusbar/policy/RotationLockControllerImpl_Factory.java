package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class RotationLockControllerImpl_Factory implements Factory<RotationLockControllerImpl> {
    private final Provider<Context> contextProvider;

    public RotationLockControllerImpl_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public RotationLockControllerImpl get() {
        return newInstance(this.contextProvider.get());
    }

    public static RotationLockControllerImpl_Factory create(Provider<Context> provider) {
        return new RotationLockControllerImpl_Factory(provider);
    }

    public static RotationLockControllerImpl newInstance(Context context) {
        return new RotationLockControllerImpl(context);
    }
}
