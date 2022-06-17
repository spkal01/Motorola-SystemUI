package com.motorola.systemui.desktop.util;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class TooltipPopupManager_Factory implements Factory<TooltipPopupManager> {
    private final Provider<Context> contextProvider;

    public TooltipPopupManager_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public TooltipPopupManager get() {
        return newInstance(this.contextProvider.get());
    }

    public static TooltipPopupManager_Factory create(Provider<Context> provider) {
        return new TooltipPopupManager_Factory(provider);
    }

    public static TooltipPopupManager newInstance(Context context) {
        return new TooltipPopupManager(context);
    }
}
