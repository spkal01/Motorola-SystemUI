package com.android.systemui.p006qs.dagger;

import com.android.systemui.p006qs.QSFooter;
import com.android.systemui.p006qs.QSFooterViewController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentModule_ProvidesQSFooterFactory */
public final class QSFragmentModule_ProvidesQSFooterFactory implements Factory<QSFooter> {
    private final Provider<QSFooterViewController> qsFooterViewControllerProvider;

    public QSFragmentModule_ProvidesQSFooterFactory(Provider<QSFooterViewController> provider) {
        this.qsFooterViewControllerProvider = provider;
    }

    public QSFooter get() {
        return providesQSFooter(this.qsFooterViewControllerProvider.get());
    }

    public static QSFragmentModule_ProvidesQSFooterFactory create(Provider<QSFooterViewController> provider) {
        return new QSFragmentModule_ProvidesQSFooterFactory(provider);
    }

    public static QSFooter providesQSFooter(QSFooterViewController qSFooterViewController) {
        return (QSFooter) Preconditions.checkNotNullFromProvides(QSFragmentModule.providesQSFooter(qSFooterViewController));
    }
}
