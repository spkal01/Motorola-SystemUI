package com.android.systemui.p006qs.dagger;

import android.view.View;
import com.android.systemui.p006qs.QSPanel;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentModule_ProvideQSPanelFactory */
public final class QSFragmentModule_ProvideQSPanelFactory implements Factory<QSPanel> {
    private final Provider<View> viewProvider;

    public QSFragmentModule_ProvideQSPanelFactory(Provider<View> provider) {
        this.viewProvider = provider;
    }

    public QSPanel get() {
        return provideQSPanel(this.viewProvider.get());
    }

    public static QSFragmentModule_ProvideQSPanelFactory create(Provider<View> provider) {
        return new QSFragmentModule_ProvideQSPanelFactory(provider);
    }

    public static QSPanel provideQSPanel(View view) {
        return (QSPanel) Preconditions.checkNotNullFromProvides(QSFragmentModule.provideQSPanel(view));
    }
}
