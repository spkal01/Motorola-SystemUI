package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.statusbar.notification.collection.render.NodeController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationSectionHeadersModule_ProvidesPeopleHeaderNodeControllerFactory */
public final class C1583xda791837 implements Factory<NodeController> {
    private final Provider<SectionHeaderControllerSubcomponent> subcomponentProvider;

    public C1583xda791837(Provider<SectionHeaderControllerSubcomponent> provider) {
        this.subcomponentProvider = provider;
    }

    public NodeController get() {
        return providesPeopleHeaderNodeController(this.subcomponentProvider.get());
    }

    public static C1583xda791837 create(Provider<SectionHeaderControllerSubcomponent> provider) {
        return new C1583xda791837(provider);
    }

    public static NodeController providesPeopleHeaderNodeController(SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        return (NodeController) Preconditions.checkNotNullFromProvides(NotificationSectionHeadersModule.providesPeopleHeaderNodeController(sectionHeaderControllerSubcomponent));
    }
}
