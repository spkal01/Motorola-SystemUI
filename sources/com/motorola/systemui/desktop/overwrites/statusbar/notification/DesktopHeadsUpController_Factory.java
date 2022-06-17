package com.motorola.systemui.desktop.overwrites.statusbar.notification;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinder;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DesktopHeadsUpController_Factory implements Factory<DesktopHeadsUpController> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<Integer> displayIdProvider;
    private final Provider<NotificationGroupManagerLegacy> groupManagerLegacyProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<NotificationInterruptStateProvider> interruptStateProvideProvider;
    private final Provider<NotificationListener> listenerProvider;
    private final Provider<NotifBindPipeline> notifBindPipelineProvider;
    private final Provider<NotificationRowBinder> notificationRowBinderLazyProvider;

    public DesktopHeadsUpController_Factory(Provider<Integer> provider, Provider<Context> provider2, Provider<Handler> provider3, Provider<NotificationInterruptStateProvider> provider4, Provider<DeviceProvisionedController> provider5, Provider<NotificationRowBinder> provider6, Provider<ConfigurationController> provider7, Provider<NotificationGroupManagerLegacy> provider8, Provider<NotifBindPipeline> provider9, Provider<NotificationListener> provider10) {
        this.displayIdProvider = provider;
        this.contextProvider = provider2;
        this.handlerProvider = provider3;
        this.interruptStateProvideProvider = provider4;
        this.deviceProvisionedControllerProvider = provider5;
        this.notificationRowBinderLazyProvider = provider6;
        this.configurationControllerProvider = provider7;
        this.groupManagerLegacyProvider = provider8;
        this.notifBindPipelineProvider = provider9;
        this.listenerProvider = provider10;
    }

    public DesktopHeadsUpController get() {
        return newInstance(this.displayIdProvider.get().intValue(), this.contextProvider.get(), this.handlerProvider.get(), this.interruptStateProvideProvider.get(), this.deviceProvisionedControllerProvider.get(), DoubleCheck.lazy(this.notificationRowBinderLazyProvider), this.configurationControllerProvider.get(), this.groupManagerLegacyProvider.get(), this.notifBindPipelineProvider.get(), this.listenerProvider.get());
    }

    public static DesktopHeadsUpController_Factory create(Provider<Integer> provider, Provider<Context> provider2, Provider<Handler> provider3, Provider<NotificationInterruptStateProvider> provider4, Provider<DeviceProvisionedController> provider5, Provider<NotificationRowBinder> provider6, Provider<ConfigurationController> provider7, Provider<NotificationGroupManagerLegacy> provider8, Provider<NotifBindPipeline> provider9, Provider<NotificationListener> provider10) {
        return new DesktopHeadsUpController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10);
    }

    public static DesktopHeadsUpController newInstance(int i, Context context, Handler handler, NotificationInterruptStateProvider notificationInterruptStateProvider, DeviceProvisionedController deviceProvisionedController, Lazy<NotificationRowBinder> lazy, ConfigurationController configurationController, NotificationGroupManagerLegacy notificationGroupManagerLegacy, NotifBindPipeline notifBindPipeline, NotificationListener notificationListener) {
        return new DesktopHeadsUpController(i, context, handler, notificationInterruptStateProvider, deviceProvisionedController, lazy, configurationController, notificationGroupManagerLegacy, notifBindPipeline, notificationListener);
    }
}
