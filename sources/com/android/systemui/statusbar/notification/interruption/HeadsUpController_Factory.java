package com.android.systemui.statusbar.notification.interruption;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class HeadsUpController_Factory implements Factory<HeadsUpController> {
    private final Provider<Context> contextProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<HeadsUpViewBinder> headsUpViewBinderProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider;
    private final Provider<NotificationListener> notificationListenerProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;

    public HeadsUpController_Factory(Provider<Context> provider, Provider<HeadsUpViewBinder> provider2, Provider<NotificationInterruptStateProvider> provider3, Provider<HeadsUpManager> provider4, Provider<NotificationRemoteInputManager> provider5, Provider<StatusBarStateController> provider6, Provider<VisualStabilityManager> provider7, Provider<NotificationListener> provider8) {
        this.contextProvider = provider;
        this.headsUpViewBinderProvider = provider2;
        this.notificationInterruptStateProvider = provider3;
        this.headsUpManagerProvider = provider4;
        this.remoteInputManagerProvider = provider5;
        this.statusBarStateControllerProvider = provider6;
        this.visualStabilityManagerProvider = provider7;
        this.notificationListenerProvider = provider8;
    }

    public HeadsUpController get() {
        return newInstance(this.contextProvider.get(), this.headsUpViewBinderProvider.get(), this.notificationInterruptStateProvider.get(), this.headsUpManagerProvider.get(), this.remoteInputManagerProvider.get(), this.statusBarStateControllerProvider.get(), this.visualStabilityManagerProvider.get(), this.notificationListenerProvider.get());
    }

    public static HeadsUpController_Factory create(Provider<Context> provider, Provider<HeadsUpViewBinder> provider2, Provider<NotificationInterruptStateProvider> provider3, Provider<HeadsUpManager> provider4, Provider<NotificationRemoteInputManager> provider5, Provider<StatusBarStateController> provider6, Provider<VisualStabilityManager> provider7, Provider<NotificationListener> provider8) {
        return new HeadsUpController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }

    public static HeadsUpController newInstance(Context context, HeadsUpViewBinder headsUpViewBinder, NotificationInterruptStateProvider notificationInterruptStateProvider2, HeadsUpManager headsUpManager, NotificationRemoteInputManager notificationRemoteInputManager, StatusBarStateController statusBarStateController, VisualStabilityManager visualStabilityManager, NotificationListener notificationListener) {
        return new HeadsUpController(context, headsUpViewBinder, notificationInterruptStateProvider2, headsUpManager, notificationRemoteInputManager, statusBarStateController, visualStabilityManager, notificationListener);
    }
}
