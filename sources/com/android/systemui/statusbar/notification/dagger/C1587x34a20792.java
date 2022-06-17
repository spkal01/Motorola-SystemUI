package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.statusbar.notification.dagger.SectionHeaderControllerSubcomponent;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationSectionHeadersModule_ProvidesSilentHeaderSubcomponentFactory */
public final class C1587x34a20792 implements Factory<SectionHeaderControllerSubcomponent> {
    private final Provider<SectionHeaderControllerSubcomponent.Builder> builderProvider;

    public C1587x34a20792(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        this.builderProvider = provider;
    }

    public SectionHeaderControllerSubcomponent get() {
        return providesSilentHeaderSubcomponent(this.builderProvider);
    }

    public static C1587x34a20792 create(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        return new C1587x34a20792(provider);
    }

    public static SectionHeaderControllerSubcomponent providesSilentHeaderSubcomponent(Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        return (SectionHeaderControllerSubcomponent) Preconditions.checkNotNullFromProvides(NotificationSectionHeadersModule.providesSilentHeaderSubcomponent(provider));
    }
}
