package com.android.systemui.assist.p003ui;

import android.content.Context;
import com.android.systemui.assist.AssistLogger;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.assist.ui.DefaultUiController_Factory */
public final class DefaultUiController_Factory implements Factory<DefaultUiController> {
    private final Provider<AssistLogger> assistLoggerProvider;
    private final Provider<Context> contextProvider;

    public DefaultUiController_Factory(Provider<Context> provider, Provider<AssistLogger> provider2) {
        this.contextProvider = provider;
        this.assistLoggerProvider = provider2;
    }

    public DefaultUiController get() {
        return newInstance(this.contextProvider.get(), this.assistLoggerProvider.get());
    }

    public static DefaultUiController_Factory create(Provider<Context> provider, Provider<AssistLogger> provider2) {
        return new DefaultUiController_Factory(provider, provider2);
    }

    public static DefaultUiController newInstance(Context context, AssistLogger assistLogger) {
        return new DefaultUiController(context, assistLogger);
    }
}
