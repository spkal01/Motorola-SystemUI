package com.android.systemui.statusbar.policy;

import android.widget.Button;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.SmartReplyView;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.policy.SmartReplyStateInflaterImpl$inflateSmartReplyViewHolder$smartReplyButtons$1$1 */
/* compiled from: SmartReplyStateInflater.kt */
final class C2064xfbefda06 extends Lambda implements Function2<Integer, CharSequence, Button> {
    final /* synthetic */ boolean $delayOnClickListener;
    final /* synthetic */ NotificationEntry $entry;
    final /* synthetic */ SmartReplyView.SmartReplies $smartReplies;
    final /* synthetic */ SmartReplyView $smartReplyView;
    final /* synthetic */ SmartReplyStateInflaterImpl this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C2064xfbefda06(SmartReplyStateInflaterImpl smartReplyStateInflaterImpl, SmartReplyView smartReplyView, NotificationEntry notificationEntry, SmartReplyView.SmartReplies smartReplies, boolean z) {
        super(2);
        this.this$0 = smartReplyStateInflaterImpl;
        this.$smartReplyView = smartReplyView;
        this.$entry = notificationEntry;
        this.$smartReplies = smartReplies;
        this.$delayOnClickListener = z;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj, Object obj2) {
        return invoke(((Number) obj).intValue(), (CharSequence) obj2);
    }

    @NotNull
    public final Button invoke(int i, CharSequence charSequence) {
        SmartReplyInflater access$getSmartRepliesInflater$p = this.this$0.smartRepliesInflater;
        SmartReplyView smartReplyView = this.$smartReplyView;
        Intrinsics.checkNotNullExpressionValue(smartReplyView, "smartReplyView");
        NotificationEntry notificationEntry = this.$entry;
        SmartReplyView.SmartReplies smartReplies = this.$smartReplies;
        Intrinsics.checkNotNullExpressionValue(charSequence, "choice");
        return access$getSmartRepliesInflater$p.inflateReplyButton(smartReplyView, notificationEntry, smartReplies, i, charSequence, this.$delayOnClickListener);
    }
}
