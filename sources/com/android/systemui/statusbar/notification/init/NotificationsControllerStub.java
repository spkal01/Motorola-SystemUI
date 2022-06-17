package com.android.systemui.statusbar.notification.init;

import android.service.notification.StatusBarNotification;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.StatusBar;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Optional;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationsControllerStub.kt */
public final class NotificationsControllerStub implements NotificationsController {
    @NotNull
    private final NotificationListener notificationListener;

    public int getActiveNotificationsCount() {
        return 0;
    }

    public void requestNotificationUpdate(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "reason");
    }

    public void resetUserExpandedStates() {
    }

    public void setNotificationSnoozed(@NotNull StatusBarNotification statusBarNotification, @NotNull NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        Intrinsics.checkNotNullParameter(statusBarNotification, "sbn");
        Intrinsics.checkNotNullParameter(snoozeOption, "snoozeOption");
    }

    public NotificationsControllerStub(@NotNull NotificationListener notificationListener2) {
        Intrinsics.checkNotNullParameter(notificationListener2, "notificationListener");
        this.notificationListener = notificationListener2;
    }

    public void initialize(@NotNull StatusBar statusBar, @NotNull Optional<Bubbles> optional, @NotNull NotificationPresenter notificationPresenter, @NotNull NotificationListContainer notificationListContainer, @NotNull NotificationActivityStarter notificationActivityStarter, @NotNull NotificationRowBinderImpl.BindRowCallback bindRowCallback) {
        Intrinsics.checkNotNullParameter(statusBar, "statusBar");
        Intrinsics.checkNotNullParameter(optional, "bubblesOptional");
        Intrinsics.checkNotNullParameter(notificationPresenter, "presenter");
        Intrinsics.checkNotNullParameter(notificationListContainer, "listContainer");
        Intrinsics.checkNotNullParameter(notificationActivityStarter, "notificationActivityStarter");
        Intrinsics.checkNotNullParameter(bindRowCallback, "bindRowCallback");
        this.notificationListener.registerAsSystemService();
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr, boolean z) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println();
        printWriter.println("Notification handling disabled");
        printWriter.println();
    }
}
