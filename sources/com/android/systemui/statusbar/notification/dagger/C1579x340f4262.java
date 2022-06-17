package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.statusbar.notification.collection.render.SectionHeaderController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationSectionHeadersModule_ProvidesIncomingHeaderControllerFactory */
public final class C1579x340f4262 implements Factory<SectionHeaderController> {
    private final Provider<SectionHeaderControllerSubcomponent> subcomponentProvider;

    public C1579x340f4262(Provider<SectionHeaderControllerSubcomponent> provider) {
        this.subcomponentProvider = provider;
    }

    public SectionHeaderController get() {
        return providesIncomingHeaderController(this.subcomponentProvider.get());
    }

    public static C1579x340f4262 create(Provider<SectionHeaderControllerSubcomponent> provider) {
        return new C1579x340f4262(provider);
    }

    public static SectionHeaderController providesIncomingHeaderController(SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        return (SectionHeaderController) Preconditions.checkNotNullFromProvides(NotificationSectionHeadersModule.providesIncomingHeaderController(sectionHeaderControllerSubcomponent));
    }
}
