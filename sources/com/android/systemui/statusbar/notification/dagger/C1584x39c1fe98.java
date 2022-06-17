package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.statusbar.notification.dagger.SectionHeaderControllerSubcomponent;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationSectionHeadersModule_ProvidesPeopleHeaderSubcomponentFactory */
public final class C1584x39c1fe98 implements Factory<SectionHeaderControllerSubcomponent> {
    private final Provider<SectionHeaderControllerSubcomponent.Builder> builderProvider;

    public C1584x39c1fe98(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        this.builderProvider = provider;
    }

    public SectionHeaderControllerSubcomponent get() {
        return providesPeopleHeaderSubcomponent(this.builderProvider);
    }

    public static C1584x39c1fe98 create(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        return new C1584x39c1fe98(provider);
    }

    public static SectionHeaderControllerSubcomponent providesPeopleHeaderSubcomponent(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        return (SectionHeaderControllerSubcomponent) Preconditions.checkNotNullFromProvides(NotificationSectionHeadersModule.providesPeopleHeaderSubcomponent(provider));
    }
}
