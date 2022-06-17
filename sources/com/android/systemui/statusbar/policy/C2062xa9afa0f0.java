package com.android.systemui.statusbar.policy;

import android.app.Notification;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.statusbar.policy.SmartReplyStateInflaterImpl$inflateSmartReplyViewHolder$smartActionButtons$1$1 */
/* compiled from: SmartReplyStateInflater.kt */
final class C2062xa9afa0f0 extends Lambda implements Function1<Notification.Action, Boolean> {
    public static final C2062xa9afa0f0 INSTANCE = new C2062xa9afa0f0();

    C2062xa9afa0f0() {
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Boolean.valueOf(invoke((Notification.Action) obj));
    }

    public final boolean invoke(Notification.Action action) {
        return action.actionIntent != null;
    }
}
