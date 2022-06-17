package com.android.systemui.statusbar.policy;

import android.app.Notification;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.SmartReplyView;
import org.jetbrains.annotations.NotNull;

/* compiled from: SmartReplyStateInflater.kt */
public interface SmartActionInflater {
    @NotNull
    Button inflateActionButton(@NotNull ViewGroup viewGroup, @NotNull NotificationEntry notificationEntry, @NotNull SmartReplyView.SmartActions smartActions, int i, @NotNull Notification.Action action, boolean z, @NotNull Context context);
}
