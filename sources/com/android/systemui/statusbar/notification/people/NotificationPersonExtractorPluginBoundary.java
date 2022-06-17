package com.android.systemui.statusbar.notification.people;

import android.service.notification.StatusBarNotification;
import com.android.systemui.plugins.NotificationPersonExtractorPlugin;
import com.android.systemui.statusbar.policy.ExtensionController;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: PeopleHubNotificationListener.kt */
public final class NotificationPersonExtractorPluginBoundary implements NotificationPersonExtractor {
    /* access modifiers changed from: private */
    @Nullable
    public NotificationPersonExtractorPlugin plugin;

    public NotificationPersonExtractorPluginBoundary(@NotNull ExtensionController extensionController) {
        Class<NotificationPersonExtractorPlugin> cls = NotificationPersonExtractorPlugin.class;
        Intrinsics.checkNotNullParameter(extensionController, "extensionController");
        this.plugin = extensionController.newExtension(cls).withPlugin(cls).withCallback(new Consumer<NotificationPersonExtractorPlugin>(this) {
            final /* synthetic */ NotificationPersonExtractorPluginBoundary this$0;

            {
                this.this$0 = r1;
            }

            public final void accept(NotificationPersonExtractorPlugin notificationPersonExtractorPlugin) {
                this.this$0.plugin = notificationPersonExtractorPlugin;
            }
        }).build().get();
    }

    public boolean isPersonNotification(@NotNull StatusBarNotification statusBarNotification) {
        Intrinsics.checkNotNullParameter(statusBarNotification, "sbn");
        NotificationPersonExtractorPlugin notificationPersonExtractorPlugin = this.plugin;
        if (notificationPersonExtractorPlugin == null) {
            return false;
        }
        return notificationPersonExtractorPlugin.isPersonNotification(statusBarNotification);
    }
}
