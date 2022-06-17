package com.android.systemui.p006qs.dagger;

import android.view.View;
import com.android.systemui.p006qs.QuickStatusBarHeader;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentModule_ProvidesQuickStatusBarHeaderFactory */
public final class QSFragmentModule_ProvidesQuickStatusBarHeaderFactory implements Factory<QuickStatusBarHeader> {
    private final Provider<View> viewProvider;

    public QSFragmentModule_ProvidesQuickStatusBarHeaderFactory(Provider<View> provider) {
        this.viewProvider = provider;
    }

    public QuickStatusBarHeader get() {
        return providesQuickStatusBarHeader(this.viewProvider.get());
    }

    public static QSFragmentModule_ProvidesQuickStatusBarHeaderFactory create(Provider<View> provider) {
        return new QSFragmentModule_ProvidesQuickStatusBarHeaderFactory(provider);
    }

    public static QuickStatusBarHeader providesQuickStatusBarHeader(View view) {
        return (QuickStatusBarHeader) Preconditions.checkNotNullFromProvides(QSFragmentModule.providesQuickStatusBarHeader(view));
    }
}
