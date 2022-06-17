package com.android.systemui.p006qs.dagger;

import android.view.View;
import com.android.systemui.p006qs.QSPrcFixedPanel;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentModule_ProvidesQSPrcFixedPanelFactory */
public final class QSFragmentModule_ProvidesQSPrcFixedPanelFactory implements Factory<QSPrcFixedPanel> {
    private final Provider<View> viewProvider;

    public QSFragmentModule_ProvidesQSPrcFixedPanelFactory(Provider<View> provider) {
        this.viewProvider = provider;
    }

    public QSPrcFixedPanel get() {
        return providesQSPrcFixedPanel(this.viewProvider.get());
    }

    public static QSFragmentModule_ProvidesQSPrcFixedPanelFactory create(Provider<View> provider) {
        return new QSFragmentModule_ProvidesQSPrcFixedPanelFactory(provider);
    }

    public static QSPrcFixedPanel providesQSPrcFixedPanel(View view) {
        return (QSPrcFixedPanel) Preconditions.checkNotNullFromProvides(QSFragmentModule.providesQSPrcFixedPanel(view));
    }
}
