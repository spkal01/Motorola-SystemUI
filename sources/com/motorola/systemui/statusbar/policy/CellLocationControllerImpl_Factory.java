package com.motorola.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class CellLocationControllerImpl_Factory implements Factory<CellLocationControllerImpl> {
    private final Provider<Context> contextProvider;

    public CellLocationControllerImpl_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public CellLocationControllerImpl get() {
        return newInstance(this.contextProvider.get());
    }

    public static CellLocationControllerImpl_Factory create(Provider<Context> provider) {
        return new CellLocationControllerImpl_Factory(provider);
    }

    public static CellLocationControllerImpl newInstance(Context context) {
        return new CellLocationControllerImpl(context);
    }
}
