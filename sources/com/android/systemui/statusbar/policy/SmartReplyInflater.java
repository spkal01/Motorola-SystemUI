package com.android.systemui.statusbar.policy;

import android.widget.Button;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.SmartReplyView;
import org.jetbrains.annotations.NotNull;

/* compiled from: SmartReplyStateInflater.kt */
public interface SmartReplyInflater {
    @NotNull
    Button inflateReplyButton(@NotNull SmartReplyView smartReplyView, @NotNull NotificationEntry notificationEntry, @NotNull SmartReplyView.SmartReplies smartReplies, int i, @NotNull CharSequence charSequence, boolean z);
}
