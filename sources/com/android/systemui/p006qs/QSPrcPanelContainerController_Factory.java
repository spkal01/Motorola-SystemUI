package com.android.systemui.p006qs;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.media.MediaHierarchyManager;
import com.android.systemui.media.MediaHost;
import com.android.systemui.p006qs.QSTileRevealController;
import com.android.systemui.p006qs.customize.QSCustomizerController;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.settings.brightness.BrightnessController;
import com.android.systemui.settings.brightness.BrightnessSlider;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.tuner.TunerService;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.QSPrcPanelContainerController_Factory */
public final class QSPrcPanelContainerController_Factory implements Factory<QSPrcPanelContainerController> {
    private final Provider<BrightnessController.Factory> brightnessControllerFactoryProvider;
    private final Provider<BrightnessSlider.Factory> brightnessSliderFactoryProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<MediaHierarchyManager> mMediaHierarchyManagerProvider;
    private final Provider<MediaHost> mediaHostProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<QSCustomizerController> qsCustomizerControllerProvider;
    private final Provider<QSFooterView> qsFooterViewProvider;
    private final Provider<QSLogger> qsLoggerProvider;
    private final Provider<QSSecurityFooter> qsSecurityFooterProvider;
    private final Provider<QSTileRevealController.Factory> qsTileRevealControllerFactoryProvider;
    private final Provider<QSTileHost> qstileHostProvider;
    private final Provider<TunerService> tunerServiceProvider;
    private final Provider<UiEventLogger> uiEventLoggerProvider;
    private final Provider<Boolean> usingMediaPlayerProvider;
    private final Provider<QSPrcPanelContainer> viewProvider;

    public QSPrcPanelContainerController_Factory(Provider<QSPrcPanelContainer> provider, Provider<QSSecurityFooter> provider2, Provider<TunerService> provider3, Provider<QSTileHost> provider4, Provider<QSCustomizerController> provider5, Provider<Boolean> provider6, Provider<MediaHost> provider7, Provider<QSTileRevealController.Factory> provider8, Provider<DumpManager> provider9, Provider<MetricsLogger> provider10, Provider<UiEventLogger> provider11, Provider<QSLogger> provider12, Provider<BrightnessController.Factory> provider13, Provider<BrightnessSlider.Factory> provider14, Provider<FalsingManager> provider15, Provider<FeatureFlags> provider16, Provider<QSFooterView> provider17, Provider<MediaHierarchyManager> provider18) {
        this.viewProvider = provider;
        this.qsSecurityFooterProvider = provider2;
        this.tunerServiceProvider = provider3;
        this.qstileHostProvider = provider4;
        this.qsCustomizerControllerProvider = provider5;
        this.usingMediaPlayerProvider = provider6;
        this.mediaHostProvider = provider7;
        this.qsTileRevealControllerFactoryProvider = provider8;
        this.dumpManagerProvider = provider9;
        this.metricsLoggerProvider = provider10;
        this.uiEventLoggerProvider = provider11;
        this.qsLoggerProvider = provider12;
        this.brightnessControllerFactoryProvider = provider13;
        this.brightnessSliderFactoryProvider = provider14;
        this.falsingManagerProvider = provider15;
        this.featureFlagsProvider = provider16;
        this.qsFooterViewProvider = provider17;
        this.mMediaHierarchyManagerProvider = provider18;
    }

    public QSPrcPanelContainerController get() {
        QSPrcPanelContainerController newInstance = newInstance(this.viewProvider.get(), this.qsSecurityFooterProvider.get(), this.tunerServiceProvider.get(), this.qstileHostProvider.get(), this.qsCustomizerControllerProvider.get(), this.usingMediaPlayerProvider.get().booleanValue(), this.mediaHostProvider.get(), this.qsTileRevealControllerFactoryProvider.get(), this.dumpManagerProvider.get(), this.metricsLoggerProvider.get(), this.uiEventLoggerProvider.get(), this.qsLoggerProvider.get(), this.brightnessControllerFactoryProvider.get(), this.brightnessSliderFactoryProvider.get(), this.falsingManagerProvider.get(), this.featureFlagsProvider.get(), this.qsFooterViewProvider.get());
        QSPanelController_MembersInjector.injectMMediaHierarchyManager(newInstance, this.mMediaHierarchyManagerProvider.get());
        return newInstance;
    }

    public static QSPrcPanelContainerController_Factory create(Provider<QSPrcPanelContainer> provider, Provider<QSSecurityFooter> provider2, Provider<TunerService> provider3, Provider<QSTileHost> provider4, Provider<QSCustomizerController> provider5, Provider<Boolean> provider6, Provider<MediaHost> provider7, Provider<QSTileRevealController.Factory> provider8, Provider<DumpManager> provider9, Provider<MetricsLogger> provider10, Provider<UiEventLogger> provider11, Provider<QSLogger> provider12, Provider<BrightnessController.Factory> provider13, Provider<BrightnessSlider.Factory> provider14, Provider<FalsingManager> provider15, Provider<FeatureFlags> provider16, Provider<QSFooterView> provider17, Provider<MediaHierarchyManager> provider18) {
        return new QSPrcPanelContainerController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18);
    }

    public static QSPrcPanelContainerController newInstance(QSPrcPanelContainer qSPrcPanelContainer, Object obj, TunerService tunerService, QSTileHost qSTileHost, QSCustomizerController qSCustomizerController, boolean z, MediaHost mediaHost, Object obj2, DumpManager dumpManager, MetricsLogger metricsLogger, UiEventLogger uiEventLogger, QSLogger qSLogger, BrightnessController.Factory factory, BrightnessSlider.Factory factory2, FalsingManager falsingManager, FeatureFlags featureFlags, QSFooterView qSFooterView) {
        return new QSPrcPanelContainerController(qSPrcPanelContainer, (QSSecurityFooter) obj, tunerService, qSTileHost, qSCustomizerController, z, mediaHost, (QSTileRevealController.Factory) obj2, dumpManager, metricsLogger, uiEventLogger, qSLogger, factory, factory2, falsingManager, featureFlags, qSFooterView);
    }
}
