package com.android.systemui.statusbar;

import android.app.Notification;
import android.os.RemoteException;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.util.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationClickNotifier.kt */
public final class NotificationClickNotifier {
    @NotNull
    private final IStatusBarService barService;
    @NotNull
    private final List<NotificationInteractionListener> listeners = new ArrayList();
    @NotNull
    private final Executor mainExecutor;

    public NotificationClickNotifier(@NotNull IStatusBarService iStatusBarService, @NotNull Executor executor) {
        Intrinsics.checkNotNullParameter(iStatusBarService, "barService");
        Intrinsics.checkNotNullParameter(executor, "mainExecutor");
        this.barService = iStatusBarService;
        this.mainExecutor = executor;
    }

    public final void addNotificationInteractionListener(@NotNull NotificationInteractionListener notificationInteractionListener) {
        Intrinsics.checkNotNullParameter(notificationInteractionListener, "listener");
        Assert.isMainThread();
        this.listeners.add(notificationInteractionListener);
    }

    /* access modifiers changed from: private */
    public final void notifyListenersAboutInteraction(String str) {
        for (NotificationInteractionListener onNotificationInteraction : this.listeners) {
            onNotificationInteraction.onNotificationInteraction(str);
        }
    }

    public final void onNotificationActionClick(@NotNull String str, int i, @NotNull Notification.Action action, @NotNull NotificationVisibility notificationVisibility, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(action, "action");
        Intrinsics.checkNotNullParameter(notificationVisibility, "visibility");
        try {
            this.barService.onNotificationActionClick(str, i, action, notificationVisibility, z);
        } catch (RemoteException unused) {
        }
        this.mainExecutor.execute(new NotificationClickNotifier$onNotificationActionClick$1(this, str));
    }

    public final void onNotificationClick(@NotNull String str, @NotNull NotificationVisibility notificationVisibility) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(notificationVisibility, "visibility");
        try {
            this.barService.onNotificationClick(str, notificationVisibility);
        } catch (RemoteException unused) {
        }
        this.mainExecutor.execute(new NotificationClickNotifier$onNotificationClick$1(this, str));
    }
}
