package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.keyguard.KeyguardViewMediator;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class CliStatusBarWindowController_Factory implements Factory<CliStatusBarWindowController> {
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;

    public CliStatusBarWindowController_Factory(Provider<Context> provider, Provider<KeyguardViewMediator> provider2) {
        this.contextProvider = provider;
        this.keyguardViewMediatorProvider = provider2;
    }

    public CliStatusBarWindowController get() {
        return newInstance(this.contextProvider.get(), this.keyguardViewMediatorProvider.get());
    }

    public static CliStatusBarWindowController_Factory create(Provider<Context> provider, Provider<KeyguardViewMediator> provider2) {
        return new CliStatusBarWindowController_Factory(provider, provider2);
    }

    public static CliStatusBarWindowController newInstance(Context context, KeyguardViewMediator keyguardViewMediator) {
        return new CliStatusBarWindowController(context, keyguardViewMediator);
    }
}
