package com.android.systemui.statusbar.policy;

import android.view.View;
import android.widget.Button;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.SmartReplyView;

/* compiled from: SmartReplyStateInflater.kt */
final class SmartReplyInflaterImpl$inflateReplyButton$1$onClickListener$1 implements View.OnClickListener {
    final /* synthetic */ CharSequence $choice;
    final /* synthetic */ NotificationEntry $entry;
    final /* synthetic */ SmartReplyView $parent;
    final /* synthetic */ int $replyIndex;
    final /* synthetic */ SmartReplyView.SmartReplies $smartReplies;
    final /* synthetic */ Button $this_apply;
    final /* synthetic */ SmartReplyInflaterImpl this$0;

    SmartReplyInflaterImpl$inflateReplyButton$1$onClickListener$1(SmartReplyInflaterImpl smartReplyInflaterImpl, NotificationEntry notificationEntry, SmartReplyView.SmartReplies smartReplies, int i, SmartReplyView smartReplyView, Button button, CharSequence charSequence) {
        this.this$0 = smartReplyInflaterImpl;
        this.$entry = notificationEntry;
        this.$smartReplies = smartReplies;
        this.$replyIndex = i;
        this.$parent = smartReplyView;
        this.$this_apply = button;
        this.$choice = charSequence;
    }

    public final void onClick(View view) {
        this.this$0.onSmartReplyClick(this.$entry, this.$smartReplies, this.$replyIndex, this.$parent, this.$this_apply, this.$choice);
    }
}
