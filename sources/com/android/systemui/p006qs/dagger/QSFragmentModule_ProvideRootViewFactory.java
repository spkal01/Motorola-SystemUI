package com.android.systemui.p006qs.dagger;

import android.view.View;
import com.android.systemui.p006qs.QSFragment;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentModule_ProvideRootViewFactory */
public final class QSFragmentModule_ProvideRootViewFactory implements Factory<View> {
    private final Provider<QSFragment> qsFragmentProvider;

    public QSFragmentModule_ProvideRootViewFactory(Provider<QSFragment> provider) {
        this.qsFragmentProvider = provider;
    }

    public View get() {
        return provideRootView(this.qsFragmentProvider.get());
    }

    public static QSFragmentModule_ProvideRootViewFactory create(Provider<QSFragment> provider) {
        return new QSFragmentModule_ProvideRootViewFactory(provider);
    }

    public static View provideRootView(QSFragment qSFragment) {
        return (View) Preconditions.checkNotNullFromProvides(QSFragmentModule.provideRootView(qSFragment));
    }
}
