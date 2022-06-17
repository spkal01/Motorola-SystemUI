package com.android.systemui.p006qs.dagger;

import com.android.systemui.p006qs.QuickQSPanel;
import com.android.systemui.p006qs.QuickStatusBarHeader;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.dagger.QSFragmentModule_ProvidesQuickQSPanelFactory */
public final class QSFragmentModule_ProvidesQuickQSPanelFactory implements Factory<QuickQSPanel> {
    private final Provider<QuickStatusBarHeader> quickStatusBarHeaderProvider;

    public QSFragmentModule_ProvidesQuickQSPanelFactory(Provider<QuickStatusBarHeader> provider) {
        this.quickStatusBarHeaderProvider = provider;
    }

    public QuickQSPanel get() {
        return providesQuickQSPanel(this.quickStatusBarHeaderProvider.get());
    }

    public static QSFragmentModule_ProvidesQuickQSPanelFactory create(Provider<QuickStatusBarHeader> provider) {
        return new QSFragmentModule_ProvidesQuickQSPanelFactory(provider);
    }

    public static QuickQSPanel providesQuickQSPanel(QuickStatusBarHeader quickStatusBarHeader) {
        return (QuickQSPanel) Preconditions.checkNotNullFromProvides(QSFragmentModule.providesQuickQSPanel(quickStatusBarHeader));
    }
}
