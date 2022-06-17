package com.android.systemui.statusbar.notification.row.dagger;

import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideNotificationKeyFactory */
public final class C1637xdc9a80a2 implements Factory<String> {
    private final Provider<StatusBarNotification> statusBarNotificationProvider;

    public C1637xdc9a80a2(Provider<StatusBarNotification> provider) {
        this.statusBarNotificationProvider = provider;
    }

    public String get() {
        return provideNotificationKey(this.statusBarNotificationProvider.get());
    }

    public static C1637xdc9a80a2 create(Provider<StatusBarNotification> provider) {
        return new C1637xdc9a80a2(provider);
    }

    public static String provideNotificationKey(StatusBarNotification statusBarNotification) {
        return (String) Preconditions.checkNotNullFromProvides(ExpandableNotificationRowComponent.ExpandableNotificationRowModule.provideNotificationKey(statusBarNotification));
    }
}
