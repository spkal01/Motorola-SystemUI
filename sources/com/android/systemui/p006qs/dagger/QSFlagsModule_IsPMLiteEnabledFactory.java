package com.android.systemui.p006qs.dagger;

import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.util.settings.GlobalSettings;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFlagsModule_IsPMLiteEnabledFactory */
public final class QSFlagsModule_IsPMLiteEnabledFactory implements Factory<Boolean> {
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<GlobalSettings> globalSettingsProvider;

    public QSFlagsModule_IsPMLiteEnabledFactory(Provider<FeatureFlags> provider, Provider<GlobalSettings> provider2) {
        this.featureFlagsProvider = provider;
        this.globalSettingsProvider = provider2;
    }

    public Boolean get() {
        return Boolean.valueOf(isPMLiteEnabled(this.featureFlagsProvider.get(), this.globalSettingsProvider.get()));
    }

    public static QSFlagsModule_IsPMLiteEnabledFactory create(Provider<FeatureFlags> provider, Provider<GlobalSettings> provider2) {
        return new QSFlagsModule_IsPMLiteEnabledFactory(provider, provider2);
    }

    public static boolean isPMLiteEnabled(FeatureFlags featureFlags, GlobalSettings globalSettings) {
        return QSFlagsModule.isPMLiteEnabled(featureFlags, globalSettings);
    }
}
