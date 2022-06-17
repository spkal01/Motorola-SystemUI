package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.statusbar.notification.collection.render.NodeController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationSectionHeadersModule_ProvidesIncomingHeaderNodeControllerFactory */
public final class C1580x8d68ee80 implements Factory<NodeController> {
    private final Provider<SectionHeaderControllerSubcomponent> subcomponentProvider;

    public C1580x8d68ee80(Provider<SectionHeaderControllerSubcomponent> provider) {
        this.subcomponentProvider = provider;
    }

    public NodeController get() {
        return providesIncomingHeaderNodeController(this.subcomponentProvider.get());
    }

    public static C1580x8d68ee80 create(Provider<SectionHeaderControllerSubcomponent> provider) {
        return new C1580x8d68ee80(provider);
    }

    public static NodeController providesIncomingHeaderNodeController(SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        return (NodeController) Preconditions.checkNotNullFromProvides(NotificationSectionHeadersModule.providesIncomingHeaderNodeController(sectionHeaderControllerSubcomponent));
    }
}
