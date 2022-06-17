package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class MultiUserCliNavGestures_Factory implements Factory<MultiUserCliNavGestures> {
    private final Provider<Context> contextProvider;

    public MultiUserCliNavGestures_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public MultiUserCliNavGestures get() {
        return newInstance(this.contextProvider.get());
    }

    public static MultiUserCliNavGestures_Factory create(Provider<Context> provider) {
        return new MultiUserCliNavGestures_Factory(provider);
    }

    public static MultiUserCliNavGestures newInstance(Context context) {
        return new MultiUserCliNavGestures(context);
    }
}
