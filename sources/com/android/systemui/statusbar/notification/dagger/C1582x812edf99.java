package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.statusbar.notification.collection.render.SectionHeaderController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationSectionHeadersModule_ProvidesPeopleHeaderControllerFactory */
public final class C1582x812edf99 implements Factory<SectionHeaderController> {
    private final Provider<SectionHeaderControllerSubcomponent> subcomponentProvider;

    public C1582x812edf99(Provider<SectionHeaderControllerSubcomponent> provider) {
        this.subcomponentProvider = provider;
    }

    public SectionHeaderController get() {
        return providesPeopleHeaderController(this.subcomponentProvider.get());
    }

    public static C1582x812edf99 create(Provider<SectionHeaderControllerSubcomponent> provider) {
        return new C1582x812edf99(provider);
    }

    public static SectionHeaderController providesPeopleHeaderController(SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        return (SectionHeaderController) Preconditions.checkNotNullFromProvides(NotificationSectionHeadersModule.providesPeopleHeaderController(sectionHeaderControllerSubcomponent));
    }
}
