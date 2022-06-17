package com.android.systemui.statusbar.phone.dagger;

import android.content.Context;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.phone.CliStatusBar;
import com.android.systemui.statusbar.phone.DozeScrimController;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.util.InjectionInflationController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class StatusBarPhoneModule_ProvideCliStatusBarFactory implements Factory<CliStatusBar> {
    private final Provider<Context> contextProvider;
    private final Provider<DozeScrimController> dozeScrimControllerProvider;
    private final Provider<DozeServiceHost> dozeServiceHostProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<NotificationGroupManagerLegacy> groupManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerPhoneProvider;
    private final Provider<HighPriorityProvider> highPriorityProvider;
    private final Provider<InjectionInflationController> injectionInflationControllerProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;

    public StatusBarPhoneModule_ProvideCliStatusBarFactory(Provider<Context> provider, Provider<HeadsUpManagerPhone> provider2, Provider<KeyguardViewMediator> provider3, Provider<NotificationGroupManagerLegacy> provider4, Provider<DozeScrimController> provider5, Provider<StatusBarKeyguardViewManager> provider6, Provider<KeyguardBypassController> provider7, Provider<FalsingManager> provider8, Provider<NotificationLockscreenUserManager> provider9, Provider<InjectionInflationController> provider10, Provider<DozeServiceHost> provider11, Provider<ScrimController> provider12, Provider<HighPriorityProvider> provider13) {
        this.contextProvider = provider;
        this.headsUpManagerPhoneProvider = provider2;
        this.keyguardViewMediatorProvider = provider3;
        this.groupManagerProvider = provider4;
        this.dozeScrimControllerProvider = provider5;
        this.statusBarKeyguardViewManagerProvider = provider6;
        this.keyguardBypassControllerProvider = provider7;
        this.falsingManagerProvider = provider8;
        this.notificationLockscreenUserManagerProvider = provider9;
        this.injectionInflationControllerProvider = provider10;
        this.dozeServiceHostProvider = provider11;
        this.scrimControllerProvider = provider12;
        this.highPriorityProvider = provider13;
    }

    public CliStatusBar get() {
        return provideCliStatusBar(this.contextProvider.get(), this.headsUpManagerPhoneProvider.get(), this.keyguardViewMediatorProvider.get(), this.groupManagerProvider.get(), this.dozeScrimControllerProvider.get(), this.statusBarKeyguardViewManagerProvider.get(), this.keyguardBypassControllerProvider.get(), this.falsingManagerProvider.get(), this.notificationLockscreenUserManagerProvider.get(), this.injectionInflationControllerProvider.get(), this.dozeServiceHostProvider.get(), this.scrimControllerProvider.get(), this.highPriorityProvider.get());
    }

    public static StatusBarPhoneModule_ProvideCliStatusBarFactory create(Provider<Context> provider, Provider<HeadsUpManagerPhone> provider2, Provider<KeyguardViewMediator> provider3, Provider<NotificationGroupManagerLegacy> provider4, Provider<DozeScrimController> provider5, Provider<StatusBarKeyguardViewManager> provider6, Provider<KeyguardBypassController> provider7, Provider<FalsingManager> provider8, Provider<NotificationLockscreenUserManager> provider9, Provider<InjectionInflationController> provider10, Provider<DozeServiceHost> provider11, Provider<ScrimController> provider12, Provider<HighPriorityProvider> provider13) {
        return new StatusBarPhoneModule_ProvideCliStatusBarFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13);
    }

    public static CliStatusBar provideCliStatusBar(Context context, HeadsUpManagerPhone headsUpManagerPhone, KeyguardViewMediator keyguardViewMediator, NotificationGroupManagerLegacy notificationGroupManagerLegacy, DozeScrimController dozeScrimController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, KeyguardBypassController keyguardBypassController, FalsingManager falsingManager, NotificationLockscreenUserManager notificationLockscreenUserManager, InjectionInflationController injectionInflationController, DozeServiceHost dozeServiceHost, ScrimController scrimController, HighPriorityProvider highPriorityProvider2) {
        return (CliStatusBar) Preconditions.checkNotNullFromProvides(StatusBarPhoneModule.provideCliStatusBar(context, headsUpManagerPhone, keyguardViewMediator, notificationGroupManagerLegacy, dozeScrimController, statusBarKeyguardViewManager, keyguardBypassController, falsingManager, notificationLockscreenUserManager, injectionInflationController, dozeServiceHost, scrimController, highPriorityProvider2));
    }
}
