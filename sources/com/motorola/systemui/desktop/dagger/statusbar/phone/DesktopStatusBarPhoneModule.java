package com.motorola.systemui.desktop.dagger.statusbar.phone;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.phone.CliStatusBar;
import com.android.systemui.statusbar.phone.DozeScrimController;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.util.InjectionInflationController;
import com.motorola.systemui.desktop.DesktopStatusBar;
import com.motorola.systemui.desktop.dagger.statusbar.phone.DesktopStatusBarComponent;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopNotificationActivityStarter;
import javax.inject.Provider;

public interface DesktopStatusBarPhoneModule {
    static StatusBar provideStatusBar(Context context, NotificationsController notificationsController, HeadsUpManagerPhone headsUpManagerPhone, NotificationRemoteInputManager notificationRemoteInputManager, VisualStabilityManager visualStabilityManager, InjectionInflationController injectionInflationController, Provider<DesktopStatusBarComponent.Builder> provider, DesktopNotificationActivityStarter desktopNotificationActivityStarter, SuperStatusBarViewFactory superStatusBarViewFactory, NotificationIconAreaController notificationIconAreaController, ExtensionController extensionController, ConfigurationController configurationController, NotificationViewHierarchyManager notificationViewHierarchyManager, StatusBarKeyguardViewManager statusBarKeyguardViewManager, Handler handler) {
        return new DesktopStatusBar(context, notificationsController, headsUpManagerPhone, notificationRemoteInputManager, visualStabilityManager, injectionInflationController, provider, desktopNotificationActivityStarter, superStatusBarViewFactory, notificationIconAreaController, extensionController, configurationController, notificationViewHierarchyManager, statusBarKeyguardViewManager, handler);
    }

    static CliStatusBar provideCliStatusBar(Context context, HeadsUpManagerPhone headsUpManagerPhone, KeyguardViewMediator keyguardViewMediator, NotificationGroupManagerLegacy notificationGroupManagerLegacy, DozeScrimController dozeScrimController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, KeyguardBypassController keyguardBypassController, FalsingManager falsingManager, NotificationLockscreenUserManager notificationLockscreenUserManager, InjectionInflationController injectionInflationController, DozeServiceHost dozeServiceHost, ScrimController scrimController, HighPriorityProvider highPriorityProvider) {
        return new CliStatusBar(context, headsUpManagerPhone, keyguardViewMediator, notificationGroupManagerLegacy, dozeScrimController, statusBarKeyguardViewManager, keyguardBypassController, falsingManager, notificationLockscreenUserManager, injectionInflationController, dozeServiceHost, scrimController, highPriorityProvider);
    }
}
