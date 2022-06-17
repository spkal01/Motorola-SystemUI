package com.android.systemui.statusbar.policy;

import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.policy.SmartReplyView;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: SmartReplyStateInflater.kt */
public final class SmartReplyInflaterImpl implements SmartReplyInflater {
    /* access modifiers changed from: private */
    @NotNull
    public final SmartReplyConstants constants;
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    @NotNull
    private final KeyguardDismissUtil keyguardDismissUtil;
    /* access modifiers changed from: private */
    @NotNull
    public final NotificationRemoteInputManager remoteInputManager;
    /* access modifiers changed from: private */
    @NotNull
    public final SmartReplyController smartReplyController;

    public SmartReplyInflaterImpl(@NotNull SmartReplyConstants smartReplyConstants, @NotNull KeyguardDismissUtil keyguardDismissUtil2, @NotNull NotificationRemoteInputManager notificationRemoteInputManager, @NotNull SmartReplyController smartReplyController2, @NotNull Context context2) {
        Intrinsics.checkNotNullParameter(smartReplyConstants, "constants");
        Intrinsics.checkNotNullParameter(keyguardDismissUtil2, "keyguardDismissUtil");
        Intrinsics.checkNotNullParameter(notificationRemoteInputManager, "remoteInputManager");
        Intrinsics.checkNotNullParameter(smartReplyController2, "smartReplyController");
        Intrinsics.checkNotNullParameter(context2, "context");
        this.constants = smartReplyConstants;
        this.keyguardDismissUtil = keyguardDismissUtil2;
        this.remoteInputManager = notificationRemoteInputManager;
        this.smartReplyController = smartReplyController2;
        this.context = context2;
    }

    /* JADX WARNING: type inference failed for: r0v11, types: [com.android.systemui.statusbar.policy.DelayedOnClickListener] */
    /* JADX WARNING: Multi-variable type inference failed */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.widget.Button inflateReplyButton(@org.jetbrains.annotations.NotNull com.android.systemui.statusbar.policy.SmartReplyView r12, @org.jetbrains.annotations.NotNull com.android.systemui.statusbar.notification.collection.NotificationEntry r13, @org.jetbrains.annotations.NotNull com.android.systemui.statusbar.policy.SmartReplyView.SmartReplies r14, int r15, @org.jetbrains.annotations.NotNull java.lang.CharSequence r16, boolean r17) {
        /*
            r11 = this;
            r8 = r12
            r7 = r16
            java.lang.String r0 = "parent"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r12, r0)
            java.lang.String r0 = "entry"
            r2 = r13
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r13, r0)
            java.lang.String r0 = "smartReplies"
            r3 = r14
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r14, r0)
            java.lang.String r0 = "choice"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r7, r0)
            android.content.Context r0 = r12.getContext()
            android.view.LayoutInflater r0 = android.view.LayoutInflater.from(r0)
            int r1 = com.android.systemui.R$layout.smart_reply_button
            r4 = 0
            android.view.View r0 = r0.inflate(r1, r12, r4)
            java.lang.String r1 = "null cannot be cast to non-null type android.widget.Button"
            java.util.Objects.requireNonNull(r0, r1)
            r9 = r0
            android.widget.Button r9 = (android.widget.Button) r9
            r9.setText(r7)
            com.android.systemui.statusbar.policy.SmartReplyInflaterImpl$inflateReplyButton$1$onClickListener$1 r10 = new com.android.systemui.statusbar.policy.SmartReplyInflaterImpl$inflateReplyButton$1$onClickListener$1
            r0 = r10
            r1 = r11
            r2 = r13
            r3 = r14
            r4 = r15
            r5 = r12
            r6 = r9
            r7 = r16
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            if (r17 == 0) goto L_0x0050
            com.android.systemui.statusbar.policy.DelayedOnClickListener r0 = new com.android.systemui.statusbar.policy.DelayedOnClickListener
            r1 = r11
            com.android.systemui.statusbar.policy.SmartReplyConstants r1 = r1.constants
            long r1 = r1.getOnClickInitDelay()
            r0.<init>(r10, r1)
            r10 = r0
        L_0x0050:
            r9.setOnClickListener(r10)
            com.android.systemui.statusbar.policy.SmartReplyInflaterImpl$inflateReplyButton$1$1 r0 = new com.android.systemui.statusbar.policy.SmartReplyInflaterImpl$inflateReplyButton$1$1
            r0.<init>(r12)
            r9.setAccessibilityDelegate(r0)
            android.view.ViewGroup$LayoutParams r0 = r9.getLayoutParams()
            java.lang.String r1 = "null cannot be cast to non-null type com.android.systemui.statusbar.policy.SmartReplyView.LayoutParams"
            java.util.Objects.requireNonNull(r0, r1)
            com.android.systemui.statusbar.policy.SmartReplyView$LayoutParams r0 = (com.android.systemui.statusbar.policy.SmartReplyView.LayoutParams) r0
            com.android.systemui.statusbar.policy.SmartReplyView$SmartButtonType r1 = com.android.systemui.statusbar.policy.SmartReplyView.SmartButtonType.REPLY
            r0.mButtonType = r1
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.SmartReplyInflaterImpl.inflateReplyButton(com.android.systemui.statusbar.policy.SmartReplyView, com.android.systemui.statusbar.notification.collection.NotificationEntry, com.android.systemui.statusbar.policy.SmartReplyView$SmartReplies, int, java.lang.CharSequence, boolean):android.widget.Button");
    }

    /* access modifiers changed from: private */
    public final void onSmartReplyClick(NotificationEntry notificationEntry, SmartReplyView.SmartReplies smartReplies, int i, SmartReplyView smartReplyView, Button button, CharSequence charSequence) {
        SmartReplyStateInflaterKt.executeWhenUnlocked(this.keyguardDismissUtil, smartReplyView.getIsCliSmartReply(), !notificationEntry.isRowPinned(), new SmartReplyInflaterImpl$onSmartReplyClick$1(this, smartReplies, button, charSequence, i, notificationEntry, smartReplyView));
    }

    /* access modifiers changed from: private */
    public final Intent createRemoteInputIntent(SmartReplyView.SmartReplies smartReplies, CharSequence charSequence) {
        Bundle bundle = new Bundle();
        bundle.putString(smartReplies.remoteInput.getResultKey(), charSequence.toString());
        Intent addFlags = new Intent().addFlags(268435456);
        RemoteInput.addResultsToIntent(new RemoteInput[]{smartReplies.remoteInput}, addFlags, bundle);
        RemoteInput.setResultsSource(addFlags, 1);
        Intrinsics.checkNotNullExpressionValue(addFlags, "intent");
        return addFlags;
    }
}
