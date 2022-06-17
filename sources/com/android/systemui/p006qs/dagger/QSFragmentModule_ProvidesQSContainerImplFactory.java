package com.android.systemui.p006qs.dagger;

import android.view.View;
import com.android.systemui.p006qs.QSContainerImpl;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentModule_ProvidesQSContainerImplFactory */
public final class QSFragmentModule_ProvidesQSContainerImplFactory implements Factory<QSContainerImpl> {
    private final Provider<View> viewProvider;

    public QSFragmentModule_ProvidesQSContainerImplFactory(Provider<View> provider) {
        this.viewProvider = provider;
    }

    public QSContainerImpl get() {
        return providesQSContainerImpl(this.viewProvider.get());
    }

    public static QSFragmentModule_ProvidesQSContainerImplFactory create(Provider<View> provider) {
        return new QSFragmentModule_ProvidesQSContainerImplFactory(provider);
    }

    public static QSContainerImpl providesQSContainerImpl(View view) {
        return (QSContainerImpl) Preconditions.checkNotNullFromProvides(QSFragmentModule.providesQSContainerImpl(view));
    }
}
