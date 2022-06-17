package com.android.systemui.statusbar.policy;

import android.app.Notification;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.SmartReplyView;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.policy.SmartReplyStateInflaterImpl$inflateSmartReplyViewHolder$smartActionButtons$1$2 */
/* compiled from: SmartReplyStateInflater.kt */
final class C2063xa9afa0f1 extends Lambda implements Function2<Integer, Notification.Action, Button> {
    final /* synthetic */ boolean $delayOnClickListener;
    final /* synthetic */ NotificationEntry $entry;
    final /* synthetic */ SmartReplyView.SmartActions $smartActions;
    final /* synthetic */ SmartReplyView $smartReplyView;
    final /* synthetic */ ContextThemeWrapper $themedPackageContext;
    final /* synthetic */ SmartReplyStateInflaterImpl this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C2063xa9afa0f1(SmartReplyStateInflaterImpl smartReplyStateInflaterImpl, SmartReplyView smartReplyView, NotificationEntry notificationEntry, SmartReplyView.SmartActions smartActions, boolean z, ContextThemeWrapper contextThemeWrapper) {
        super(2);
        this.this$0 = smartReplyStateInflaterImpl;
        this.$smartReplyView = smartReplyView;
        this.$entry = notificationEntry;
        this.$smartActions = smartActions;
        this.$delayOnClickListener = z;
        this.$themedPackageContext = contextThemeWrapper;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj, Object obj2) {
        return invoke(((Number) obj).intValue(), (Notification.Action) obj2);
    }

    @NotNull
    public final Button invoke(int i, Notification.Action action) {
        SmartActionInflater access$getSmartActionsInflater$p = this.this$0.smartActionsInflater;
        SmartReplyView smartReplyView = this.$smartReplyView;
        Intrinsics.checkNotNullExpressionValue(smartReplyView, "smartReplyView");
        NotificationEntry notificationEntry = this.$entry;
        SmartReplyView.SmartActions smartActions = this.$smartActions;
        Intrinsics.checkNotNullExpressionValue(action, "action");
        return access$getSmartActionsInflater$p.inflateActionButton(smartReplyView, notificationEntry, smartActions, i, action, this.$delayOnClickListener, this.$themedPackageContext);
    }
}
