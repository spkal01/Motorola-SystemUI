package com.android.systemui.statusbar.notification.row.dagger;

import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideStatusBarNotificationFactory */
public final class C1638xc255c3ca implements Factory<StatusBarNotification> {
    private final Provider<NotificationEntry> notificationEntryProvider;

    public C1638xc255c3ca(Provider<NotificationEntry> provider) {
        this.notificationEntryProvider = provider;
    }

    public StatusBarNotification get() {
        return provideStatusBarNotification(this.notificationEntryProvider.get());
    }

    public static C1638xc255c3ca create(Provider<NotificationEntry> provider) {
        return new C1638xc255c3ca(provider);
    }

    public static StatusBarNotification provideStatusBarNotification(NotificationEntry notificationEntry) {
        return (StatusBarNotification) Preconditions.checkNotNullFromProvides(ExpandableNotificationRowComponent.ExpandableNotificationRowModule.provideStatusBarNotification(notificationEntry));
    }
}
