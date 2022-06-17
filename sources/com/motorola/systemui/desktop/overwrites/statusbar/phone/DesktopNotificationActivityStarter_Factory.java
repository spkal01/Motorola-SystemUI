package com.motorola.systemui.desktop.overwrites.statusbar.phone;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Handler;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationClickNotifier;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.row.OnUserInteractionCallback;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DesktopNotificationActivityStarter_Factory implements Factory<DesktopNotificationActivityStarter> {
    private final Provider<NotificationClickNotifier> clickNotifierProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<KeyguardManager> keyguardManagerProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<StatusBarNotificationActivityStarterLogger> loggerProvider;
    private final Provider<Handler> mainThreadHandlerProvider;
    private final Provider<NotifPipeline> notifPipelineProvider;
    private final Provider<OnUserInteractionCallback> onUserInteractionCallbackProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;

    public DesktopNotificationActivityStarter_Factory(Provider<Context> provider, Provider<Handler> provider2, Provider<FeatureFlags> provider3, Provider<LockPatternUtils> provider4, Provider<NotificationEntryManager> provider5, Provider<NotificationClickNotifier> provider6, Provider<KeyguardManager> provider7, Provider<NotifPipeline> provider8, Provider<NotificationRemoteInputManager> provider9, Provider<OnUserInteractionCallback> provider10, Provider<StatusBarNotificationActivityStarterLogger> provider11) {
        this.contextProvider = provider;
        this.mainThreadHandlerProvider = provider2;
        this.featureFlagsProvider = provider3;
        this.lockPatternUtilsProvider = provider4;
        this.entryManagerProvider = provider5;
        this.clickNotifierProvider = provider6;
        this.keyguardManagerProvider = provider7;
        this.notifPipelineProvider = provider8;
        this.remoteInputManagerProvider = provider9;
        this.onUserInteractionCallbackProvider = provider10;
        this.loggerProvider = provider11;
    }

    public DesktopNotificationActivityStarter get() {
        return newInstance(this.contextProvider.get(), this.mainThreadHandlerProvider.get(), this.featureFlagsProvider.get(), this.lockPatternUtilsProvider.get(), this.entryManagerProvider.get(), this.clickNotifierProvider.get(), this.keyguardManagerProvider.get(), this.notifPipelineProvider.get(), this.remoteInputManagerProvider.get(), this.onUserInteractionCallbackProvider.get(), this.loggerProvider.get());
    }

    public static DesktopNotificationActivityStarter_Factory create(Provider<Context> provider, Provider<Handler> provider2, Provider<FeatureFlags> provider3, Provider<LockPatternUtils> provider4, Provider<NotificationEntryManager> provider5, Provider<NotificationClickNotifier> provider6, Provider<KeyguardManager> provider7, Provider<NotifPipeline> provider8, Provider<NotificationRemoteInputManager> provider9, Provider<OnUserInteractionCallback> provider10, Provider<StatusBarNotificationActivityStarterLogger> provider11) {
        return new DesktopNotificationActivityStarter_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
    }

    public static DesktopNotificationActivityStarter newInstance(Context context, Handler handler, FeatureFlags featureFlags, LockPatternUtils lockPatternUtils, NotificationEntryManager notificationEntryManager, NotificationClickNotifier notificationClickNotifier, KeyguardManager keyguardManager, NotifPipeline notifPipeline, NotificationRemoteInputManager notificationRemoteInputManager, OnUserInteractionCallback onUserInteractionCallback, StatusBarNotificationActivityStarterLogger statusBarNotificationActivityStarterLogger) {
        return new DesktopNotificationActivityStarter(context, handler, featureFlags, lockPatternUtils, notificationEntryManager, notificationClickNotifier, keyguardManager, notifPipeline, notificationRemoteInputManager, onUserInteractionCallback, statusBarNotificationActivityStarterLogger);
    }
}
