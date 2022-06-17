package com.android.systemui.statusbar.phone.dagger;

import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.phone.dagger.StatusBarPhoneDependenciesModule_ProvideNotificationGroupAlertTransferHelperFactory */
public final class C1981x3053f5c5 implements Factory<NotificationGroupAlertTransferHelper> {
    private final Provider<RowContentBindStage> bindStageProvider;

    public C1981x3053f5c5(Provider<RowContentBindStage> provider) {
        this.bindStageProvider = provider;
    }

    public NotificationGroupAlertTransferHelper get() {
        return provideNotificationGroupAlertTransferHelper(this.bindStageProvider.get());
    }

    public static C1981x3053f5c5 create(Provider<RowContentBindStage> provider) {
        return new C1981x3053f5c5(provider);
    }

    public static NotificationGroupAlertTransferHelper provideNotificationGroupAlertTransferHelper(RowContentBindStage rowContentBindStage) {
        return (NotificationGroupAlertTransferHelper) Preconditions.checkNotNullFromProvides(StatusBarPhoneDependenciesModule.provideNotificationGroupAlertTransferHelper(rowContentBindStage));
    }
}
