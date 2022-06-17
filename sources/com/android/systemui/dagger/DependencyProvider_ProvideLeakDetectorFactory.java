package com.android.systemui.dagger;

import com.android.systemui.util.leak.LeakDetector;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class DependencyProvider_ProvideLeakDetectorFactory implements Factory<LeakDetector> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideLeakDetectorFactory(DependencyProvider dependencyProvider) {
        this.module = dependencyProvider;
    }

    public LeakDetector get() {
        return provideLeakDetector(this.module);
    }

    public static DependencyProvider_ProvideLeakDetectorFactory create(DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvideLeakDetectorFactory(dependencyProvider);
    }

    public static LeakDetector provideLeakDetector(DependencyProvider dependencyProvider) {
        return (LeakDetector) Preconditions.checkNotNullFromProvides(dependencyProvider.provideLeakDetector());
    }
}
