package com.android.systemui.statusbar.notification.row.dagger;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideAppNameFactory */
public final class C1636x3e2d0aca implements Factory<String> {
    private final Provider<Context> contextProvider;
    private final Provider<StatusBarNotification> statusBarNotificationProvider;

    public C1636x3e2d0aca(Provider<Context> provider, Provider<StatusBarNotification> provider2) {
        this.contextProvider = provider;
        this.statusBarNotificationProvider = provider2;
    }

    public String get() {
        return provideAppName(this.contextProvider.get(), this.statusBarNotificationProvider.get());
    }

    public static C1636x3e2d0aca create(Provider<Context> provider, Provider<StatusBarNotification> provider2) {
        return new C1636x3e2d0aca(provider, provider2);
    }

    public static String provideAppName(Context context, StatusBarNotification statusBarNotification) {
        return (String) Preconditions.checkNotNullFromProvides(ExpandableNotificationRowComponent.ExpandableNotificationRowModule.provideAppName(context, statusBarNotification));
    }
}
