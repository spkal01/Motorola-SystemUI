package com.motorola.systemui.desktop.dagger.statusbar.phone;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.util.InjectionInflationController;
import com.motorola.systemui.desktop.dagger.statusbar.phone.DesktopStatusBarComponent;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopNotificationActivityStarter;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class DesktopStatusBarPhoneModule_ProvideStatusBarFactory implements Factory<StatusBar> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ExtensionController> extensionControllerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerPhoneProvider;
    private final Provider<InjectionInflationController> injectionInflationControllerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<DesktopNotificationActivityStarter> notificationActivityStarterProvider;
    private final Provider<NotificationIconAreaController> notificationIconAreaControllerProvider;
    private final Provider<NotificationViewHierarchyManager> notificationViewHierarchyManagerProvider;
    private final Provider<NotificationsController> notificationsControllerProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<DesktopStatusBarComponent.Builder> statusBarComponentBuilderProvider;
    private final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;
    private final Provider<SuperStatusBarViewFactory> superStatusBarViewFactoryProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;

    public DesktopStatusBarPhoneModule_ProvideStatusBarFactory(Provider<Context> provider, Provider<NotificationsController> provider2, Provider<HeadsUpManagerPhone> provider3, Provider<NotificationRemoteInputManager> provider4, Provider<VisualStabilityManager> provider5, Provider<InjectionInflationController> provider6, Provider<DesktopStatusBarComponent.Builder> provider7, Provider<DesktopNotificationActivityStarter> provider8, Provider<SuperStatusBarViewFactory> provider9, Provider<NotificationIconAreaController> provider10, Provider<ExtensionController> provider11, Provider<ConfigurationController> provider12, Provider<NotificationViewHierarchyManager> provider13, Provider<StatusBarKeyguardViewManager> provider14, Provider<Handler> provider15) {
        this.contextProvider = provider;
        this.notificationsControllerProvider = provider2;
        this.headsUpManagerPhoneProvider = provider3;
        this.remoteInputManagerProvider = provider4;
        this.visualStabilityManagerProvider = provider5;
        this.injectionInflationControllerProvider = provider6;
        this.statusBarComponentBuilderProvider = provider7;
        this.notificationActivityStarterProvider = provider8;
        this.superStatusBarViewFactoryProvider = provider9;
        this.notificationIconAreaControllerProvider = provider10;
        this.extensionControllerProvider = provider11;
        this.configurationControllerProvider = provider12;
        this.notificationViewHierarchyManagerProvider = provider13;
        this.statusBarKeyguardViewManagerProvider = provider14;
        this.mainHandlerProvider = provider15;
    }

    public StatusBar get() {
        return provideStatusBar(this.contextProvider.get(), this.notificationsControllerProvider.get(), this.headsUpManagerPhoneProvider.get(), this.remoteInputManagerProvider.get(), this.visualStabilityManagerProvider.get(), this.injectionInflationControllerProvider.get(), this.statusBarComponentBuilderProvider, this.notificationActivityStarterProvider.get(), this.superStatusBarViewFactoryProvider.get(), this.notificationIconAreaControllerProvider.get(), this.extensionControllerProvider.get(), this.configurationControllerProvider.get(), this.notificationViewHierarchyManagerProvider.get(), this.statusBarKeyguardViewManagerProvider.get(), this.mainHandlerProvider.get());
    }

    public static DesktopStatusBarPhoneModule_ProvideStatusBarFactory create(Provider<Context> provider, Provider<NotificationsController> provider2, Provider<HeadsUpManagerPhone> provider3, Provider<NotificationRemoteInputManager> provider4, Provider<VisualStabilityManager> provider5, Provider<InjectionInflationController> provider6, Provider<DesktopStatusBarComponent.Builder> provider7, Provider<DesktopNotificationActivityStarter> provider8, Provider<SuperStatusBarViewFactory> provider9, Provider<NotificationIconAreaController> provider10, Provider<ExtensionController> provider11, Provider<ConfigurationController> provider12, Provider<NotificationViewHierarchyManager> provider13, Provider<StatusBarKeyguardViewManager> provider14, Provider<Handler> provider15) {
        return new DesktopStatusBarPhoneModule_ProvideStatusBarFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15);
    }

    public static StatusBar provideStatusBar(Context context, NotificationsController notificationsController, HeadsUpManagerPhone headsUpManagerPhone, NotificationRemoteInputManager notificationRemoteInputManager, VisualStabilityManager visualStabilityManager, InjectionInflationController injectionInflationController, Provider<DesktopStatusBarComponent.Builder> provider, DesktopNotificationActivityStarter desktopNotificationActivityStarter, SuperStatusBarViewFactory superStatusBarViewFactory, NotificationIconAreaController notificationIconAreaController, ExtensionController extensionController, ConfigurationController configurationController, NotificationViewHierarchyManager notificationViewHierarchyManager, StatusBarKeyguardViewManager statusBarKeyguardViewManager, Handler handler) {
        return (StatusBar) Preconditions.checkNotNullFromProvides(DesktopStatusBarPhoneModule.provideStatusBar(context, notificationsController, headsUpManagerPhone, notificationRemoteInputManager, visualStabilityManager, injectionInflationController, provider, desktopNotificationActivityStarter, superStatusBarViewFactory, notificationIconAreaController, extensionController, configurationController, notificationViewHierarchyManager, statusBarKeyguardViewManager, handler));
    }
}
