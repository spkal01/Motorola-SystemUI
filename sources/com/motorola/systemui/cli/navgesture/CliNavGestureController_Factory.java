package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class CliNavGestureController_Factory implements Factory<CliNavGestureController> {
    private final Provider<Context> contextProvider;
    private final Provider<Optional<Lazy<StatusBar>>> statusBarLazyProvider;

    public CliNavGestureController_Factory(Provider<Context> provider, Provider<Optional<Lazy<StatusBar>>> provider2) {
        this.contextProvider = provider;
        this.statusBarLazyProvider = provider2;
    }

    public CliNavGestureController get() {
        return newInstance(this.contextProvider.get(), this.statusBarLazyProvider.get());
    }

    public static CliNavGestureController_Factory create(Provider<Context> provider, Provider<Optional<Lazy<StatusBar>>> provider2) {
        return new CliNavGestureController_Factory(provider, provider2);
    }

    public static CliNavGestureController newInstance(Context context, Optional<Lazy<StatusBar>> optional) {
        return new CliNavGestureController(context, optional);
    }
}
