package com.android.systemui.p006qs.dagger;

import android.view.View;
import com.android.systemui.p006qs.QSPrcPanelContainer;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentModule_ProvidesQSPrcPanelContainerFactory */
public final class QSFragmentModule_ProvidesQSPrcPanelContainerFactory implements Factory<QSPrcPanelContainer> {
    private final Provider<View> viewProvider;

    public QSFragmentModule_ProvidesQSPrcPanelContainerFactory(Provider<View> provider) {
        this.viewProvider = provider;
    }

    public QSPrcPanelContainer get() {
        return providesQSPrcPanelContainer(this.viewProvider.get());
    }

    public static QSFragmentModule_ProvidesQSPrcPanelContainerFactory create(Provider<View> provider) {
        return new QSFragmentModule_ProvidesQSPrcPanelContainerFactory(provider);
    }

    public static QSPrcPanelContainer providesQSPrcPanelContainer(View view) {
        return (QSPrcPanelContainer) Preconditions.checkNotNullFromProvides(QSFragmentModule.providesQSPrcPanelContainer(view));
    }
}
