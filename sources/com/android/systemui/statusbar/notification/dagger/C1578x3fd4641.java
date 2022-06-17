package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.statusbar.notification.dagger.SectionHeaderControllerSubcomponent;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationSectionHeadersModule_ProvidesAlertingHeaderSubcomponentFactory */
public final class C1578x3fd4641 implements Factory<SectionHeaderControllerSubcomponent> {
    private final Provider<SectionHeaderControllerSubcomponent.Builder> builderProvider;

    public C1578x3fd4641(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        this.builderProvider = provider;
    }

    public SectionHeaderControllerSubcomponent get() {
        return providesAlertingHeaderSubcomponent(this.builderProvider);
    }

    public static C1578x3fd4641 create(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        return new C1578x3fd4641(provider);
    }

    public static SectionHeaderControllerSubcomponent providesAlertingHeaderSubcomponent(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        return (SectionHeaderControllerSubcomponent) Preconditions.checkNotNullFromProvides(NotificationSectionHeadersModule.providesAlertingHeaderSubcomponent(provider));
    }
}
