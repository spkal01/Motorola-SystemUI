package com.android.systemui.dagger;

import android.content.Context;
import com.android.internal.util.LatencyTracker;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideLatencyTrackerFactory implements Factory<LatencyTracker> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideLatencyTrackerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public LatencyTracker get() {
        return provideLatencyTracker(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideLatencyTrackerFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideLatencyTrackerFactory(provider);
    }

    public static LatencyTracker provideLatencyTracker(Context context) {
        return (LatencyTracker) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideLatencyTracker(context));
    }
}
