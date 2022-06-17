package com.android.systemui.p006qs.dagger;

import android.view.View;
import com.android.systemui.p006qs.customize.QSCustomizer;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentModule_ProvidesQSCutomizerFactory */
public final class QSFragmentModule_ProvidesQSCutomizerFactory implements Factory<QSCustomizer> {
    private final Provider<View> viewProvider;

    public QSFragmentModule_ProvidesQSCutomizerFactory(Provider<View> provider) {
        this.viewProvider = provider;
    }

    public QSCustomizer get() {
        return providesQSCutomizer(this.viewProvider.get());
    }

    public static QSFragmentModule_ProvidesQSCutomizerFactory create(Provider<View> provider) {
        return new QSFragmentModule_ProvidesQSCutomizerFactory(provider);
    }

    public static QSCustomizer providesQSCutomizer(View view) {
        return (QSCustomizer) Preconditions.checkNotNullFromProvides(QSFragmentModule.providesQSCutomizer(view));
    }
}
