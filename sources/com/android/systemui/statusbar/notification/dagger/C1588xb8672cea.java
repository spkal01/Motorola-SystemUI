package com.android.systemui.statusbar.notification.dagger;

import android.content.Context;
import android.os.PowerManager;
import android.service.dreams.IDreamManager;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProviderImpl;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationInterruptStateProviderFactory */
public final class C1588xb8672cea implements Factory<NotificationInterruptStateProvider> {
    private final Provider<Context> contextProvider;
    private final Provider<IDreamManager> dreamManagerProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<NotificationInterruptStateProviderImpl> implProvider;
    private final Provider<NotificationFilter> notificationFilterProvider;
    private final Provider<PowerManager> powerManagerProvider;

    public C1588xb8672cea(Provider<Context> provider, Provider<NotificationInterruptStateProviderImpl> provider2, Provider<PowerManager> provider3, Provider<IDreamManager> provider4, Provider<HeadsUpManager> provider5, Provider<NotificationFilter> provider6) {
        this.contextProvider = provider;
        this.implProvider = provider2;
        this.powerManagerProvider = provider3;
        this.dreamManagerProvider = provider4;
        this.headsUpManagerProvider = provider5;
        this.notificationFilterProvider = provider6;
    }

    public NotificationInterruptStateProvider get() {
        return provideNotificationInterruptStateProvider(this.contextProvider.get(), this.implProvider.get(), this.powerManagerProvider.get(), this.dreamManagerProvider.get(), this.headsUpManagerProvider.get(), this.notificationFilterProvider.get());
    }

    public static C1588xb8672cea create(Provider<Context> provider, Provider<NotificationInterruptStateProviderImpl> provider2, Provider<PowerManager> provider3, Provider<IDreamManager> provider4, Provider<HeadsUpManager> provider5, Provider<NotificationFilter> provider6) {
        return new C1588xb8672cea(provider, provider2, provider3, provider4, provider5, provider6);
    }

    public static NotificationInterruptStateProvider provideNotificationInterruptStateProvider(Context context, NotificationInterruptStateProviderImpl notificationInterruptStateProviderImpl, PowerManager powerManager, IDreamManager iDreamManager, HeadsUpManager headsUpManager, NotificationFilter notificationFilter) {
        return (NotificationInterruptStateProvider) Preconditions.checkNotNullFromProvides(NotificationsModule.provideNotificationInterruptStateProvider(context, notificationInterruptStateProviderImpl, powerManager, iDreamManager, headsUpManager, notificationFilter));
    }
}
