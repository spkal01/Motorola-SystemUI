package com.android.systemui.statusbar.policy;

import android.app.Notification;
import android.app.PendingIntent;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.SmartReplyView;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: SmartReplyStateInflater.kt */
public final class SmartActionInflaterImpl implements SmartActionInflater {
    @NotNull
    private final ActivityStarter activityStarter;
    @NotNull
    private final SmartReplyConstants constants;
    @NotNull
    private final HeadsUpManager headsUpManager;
    /* access modifiers changed from: private */
    @NotNull
    public final SmartReplyController smartReplyController;

    public SmartActionInflaterImpl(@NotNull SmartReplyConstants smartReplyConstants, @NotNull ActivityStarter activityStarter2, @NotNull SmartReplyController smartReplyController2, @NotNull HeadsUpManager headsUpManager2) {
        Intrinsics.checkNotNullParameter(smartReplyConstants, "constants");
        Intrinsics.checkNotNullParameter(activityStarter2, "activityStarter");
        Intrinsics.checkNotNullParameter(smartReplyController2, "smartReplyController");
        Intrinsics.checkNotNullParameter(headsUpManager2, "headsUpManager");
        this.constants = smartReplyConstants;
        this.activityStarter = activityStarter2;
        this.smartReplyController = smartReplyController2;
        this.headsUpManager = headsUpManager2;
    }

    /* JADX WARNING: type inference failed for: r9v3, types: [com.android.systemui.statusbar.policy.DelayedOnClickListener] */
    /* JADX WARNING: Multi-variable type inference failed */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.widget.Button inflateActionButton(@org.jetbrains.annotations.NotNull android.view.ViewGroup r8, @org.jetbrains.annotations.NotNull com.android.systemui.statusbar.notification.collection.NotificationEntry r9, @org.jetbrains.annotations.NotNull com.android.systemui.statusbar.policy.SmartReplyView.SmartActions r10, int r11, @org.jetbrains.annotations.NotNull android.app.Notification.Action r12, boolean r13, @org.jetbrains.annotations.NotNull android.content.Context r14) {
        /*
            r7 = this;
            java.lang.String r0 = "parent"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            java.lang.String r0 = "entry"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r9, r0)
            java.lang.String r0 = "smartActions"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r10, r0)
            java.lang.String r0 = "action"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r12, r0)
            java.lang.String r0 = "packageContext"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r14, r0)
            android.content.Context r0 = r8.getContext()
            android.view.LayoutInflater r0 = android.view.LayoutInflater.from(r0)
            int r1 = com.android.systemui.R$layout.smart_action_button
            r2 = 0
            android.view.View r8 = r0.inflate(r1, r8, r2)
            java.lang.String r0 = "null cannot be cast to non-null type android.widget.Button"
            java.util.Objects.requireNonNull(r8, r0)
            android.widget.Button r8 = (android.widget.Button) r8
            java.lang.CharSequence r0 = r12.title
            r8.setText(r0)
            android.graphics.drawable.Icon r0 = r12.getIcon()
            android.graphics.drawable.Drawable r14 = r0.loadDrawable(r14)
            android.content.Context r0 = r8.getContext()
            android.content.res.Resources r0 = r0.getResources()
            int r1 = com.android.systemui.R$dimen.smart_action_button_icon_size
            int r0 = r0.getDimensionPixelSize(r1)
            r14.setBounds(r2, r2, r0, r0)
            r0 = 0
            r8.setCompoundDrawables(r14, r0, r0, r0)
            com.android.systemui.statusbar.policy.SmartActionInflaterImpl$inflateActionButton$1$onClickListener$1 r14 = new com.android.systemui.statusbar.policy.SmartActionInflaterImpl$inflateActionButton$1$onClickListener$1
            r1 = r14
            r2 = r7
            r3 = r9
            r4 = r10
            r5 = r11
            r6 = r12
            r1.<init>(r2, r3, r4, r5, r6)
            if (r13 == 0) goto L_0x006a
            com.android.systemui.statusbar.policy.DelayedOnClickListener r9 = new com.android.systemui.statusbar.policy.DelayedOnClickListener
            com.android.systemui.statusbar.policy.SmartReplyConstants r7 = r7.constants
            long r10 = r7.getOnClickInitDelay()
            r9.<init>(r14, r10)
            r14 = r9
        L_0x006a:
            r8.setOnClickListener(r14)
            android.view.ViewGroup$LayoutParams r7 = r8.getLayoutParams()
            java.lang.String r9 = "null cannot be cast to non-null type com.android.systemui.statusbar.policy.SmartReplyView.LayoutParams"
            java.util.Objects.requireNonNull(r7, r9)
            com.android.systemui.statusbar.policy.SmartReplyView$LayoutParams r7 = (com.android.systemui.statusbar.policy.SmartReplyView.LayoutParams) r7
            com.android.systemui.statusbar.policy.SmartReplyView$SmartButtonType r9 = com.android.systemui.statusbar.policy.SmartReplyView.SmartButtonType.ACTION
            r7.mButtonType = r9
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.SmartActionInflaterImpl.inflateActionButton(android.view.ViewGroup, com.android.systemui.statusbar.notification.collection.NotificationEntry, com.android.systemui.statusbar.policy.SmartReplyView$SmartActions, int, android.app.Notification$Action, boolean, android.content.Context):android.widget.Button");
    }

    /* access modifiers changed from: private */
    public final void onSmartActionClick(NotificationEntry notificationEntry, SmartReplyView.SmartActions smartActions, int i, Notification.Action action) {
        if (!smartActions.fromAssistant || 11 != action.getSemanticAction()) {
            ActivityStarter activityStarter2 = this.activityStarter;
            PendingIntent pendingIntent = action.actionIntent;
            Intrinsics.checkNotNullExpressionValue(pendingIntent, "action.actionIntent");
            SmartReplyStateInflaterKt.startPendingIntentDismissingKeyguard(activityStarter2, pendingIntent, notificationEntry.getRow(), new SmartActionInflaterImpl$onSmartActionClick$1(this, notificationEntry, i, action, smartActions));
            return;
        }
        notificationEntry.getRow().doSmartActionClick(((int) notificationEntry.getRow().getX()) / 2, ((int) notificationEntry.getRow().getY()) / 2, 11);
        this.smartReplyController.smartActionClicked(notificationEntry, i, action, smartActions.fromAssistant);
    }
}
