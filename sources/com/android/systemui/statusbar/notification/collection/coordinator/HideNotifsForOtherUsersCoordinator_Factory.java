package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class HideNotifsForOtherUsersCoordinator_Factory implements Factory<HideNotifsForOtherUsersCoordinator> {
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<SharedCoordinatorLogger> loggerProvider;

    public HideNotifsForOtherUsersCoordinator_Factory(Provider<NotificationLockscreenUserManager> provider, Provider<SharedCoordinatorLogger> provider2) {
        this.lockscreenUserManagerProvider = provider;
        this.loggerProvider = provider2;
    }

    public HideNotifsForOtherUsersCoordinator get() {
        return newInstance(this.lockscreenUserManagerProvider.get(), this.loggerProvider.get());
    }

    public static HideNotifsForOtherUsersCoordinator_Factory create(Provider<NotificationLockscreenUserManager> provider, Provider<SharedCoordinatorLogger> provider2) {
        return new HideNotifsForOtherUsersCoordinator_Factory(provider, provider2);
    }

    public static HideNotifsForOtherUsersCoordinator newInstance(NotificationLockscreenUserManager notificationLockscreenUserManager, SharedCoordinatorLogger sharedCoordinatorLogger) {
        return new HideNotifsForOtherUsersCoordinator(notificationLockscreenUserManager, sharedCoordinatorLogger);
    }
}
