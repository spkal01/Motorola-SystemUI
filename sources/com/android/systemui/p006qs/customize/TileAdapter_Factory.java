package com.android.systemui.p006qs.customize;

import android.content.Context;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.p006qs.QSTileHost;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.customize.TileAdapter_Factory */
public final class TileAdapter_Factory implements Factory<TileAdapter> {
    private final Provider<Context> contextProvider;
    private final Provider<QSTileHost> qsHostProvider;
    private final Provider<UiEventLogger> uiEventLoggerProvider;

    public TileAdapter_Factory(Provider<Context> provider, Provider<QSTileHost> provider2, Provider<UiEventLogger> provider3) {
        this.contextProvider = provider;
        this.qsHostProvider = provider2;
        this.uiEventLoggerProvider = provider3;
    }

    public TileAdapter get() {
        return newInstance(this.contextProvider.get(), this.qsHostProvider.get(), this.uiEventLoggerProvider.get());
    }

    public static TileAdapter_Factory create(Provider<Context> provider, Provider<QSTileHost> provider2, Provider<UiEventLogger> provider3) {
        return new TileAdapter_Factory(provider, provider2, provider3);
    }

    public static TileAdapter newInstance(Context context, QSTileHost qSTileHost, UiEventLogger uiEventLogger) {
        return new TileAdapter(context, qSTileHost, uiEventLogger);
    }
}
