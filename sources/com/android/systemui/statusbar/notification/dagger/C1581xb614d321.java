package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.statusbar.notification.dagger.SectionHeaderControllerSubcomponent;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationSectionHeadersModule_ProvidesIncomingHeaderSubcomponentFactory */
public final class C1581xb614d321 implements Factory<SectionHeaderControllerSubcomponent> {
    private final Provider<SectionHeaderControllerSubcomponent.Builder> builderProvider;

    public C1581xb614d321(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        this.builderProvider = provider;
    }

    public SectionHeaderControllerSubcomponent get() {
        return providesIncomingHeaderSubcomponent(this.builderProvider);
    }

    public static C1581xb614d321 create(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        return new C1581xb614d321(provider);
    }

    public static SectionHeaderControllerSubcomponent providesIncomingHeaderSubcomponent(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        return (SectionHeaderControllerSubcomponent) Preconditions.checkNotNullFromProvides(NotificationSectionHeadersModule.providesIncomingHeaderSubcomponent(provider));
    }
}
