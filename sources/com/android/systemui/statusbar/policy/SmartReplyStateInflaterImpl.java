package com.android.systemui.statusbar.policy;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.SmartReplyView;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SmartReplyStateInflater.kt */
public final class SmartReplyStateInflaterImpl implements SmartReplyStateInflater {
    @NotNull
    private final ActivityManagerWrapper activityManagerWrapper;
    @NotNull
    private final SmartReplyConstants constants;
    @NotNull
    private final DevicePolicyManagerWrapper devicePolicyManagerWrapper;
    @NotNull
    private final PackageManagerWrapper packageManagerWrapper;
    /* access modifiers changed from: private */
    @NotNull
    public final SmartActionInflater smartActionsInflater;
    /* access modifiers changed from: private */
    @NotNull
    public final SmartReplyInflater smartRepliesInflater;

    public SmartReplyStateInflaterImpl(@NotNull SmartReplyConstants smartReplyConstants, @NotNull ActivityManagerWrapper activityManagerWrapper2, @NotNull PackageManagerWrapper packageManagerWrapper2, @NotNull DevicePolicyManagerWrapper devicePolicyManagerWrapper2, @NotNull SmartReplyInflater smartReplyInflater, @NotNull SmartActionInflater smartActionInflater) {
        Intrinsics.checkNotNullParameter(smartReplyConstants, "constants");
        Intrinsics.checkNotNullParameter(activityManagerWrapper2, "activityManagerWrapper");
        Intrinsics.checkNotNullParameter(packageManagerWrapper2, "packageManagerWrapper");
        Intrinsics.checkNotNullParameter(devicePolicyManagerWrapper2, "devicePolicyManagerWrapper");
        Intrinsics.checkNotNullParameter(smartReplyInflater, "smartRepliesInflater");
        Intrinsics.checkNotNullParameter(smartActionInflater, "smartActionsInflater");
        this.constants = smartReplyConstants;
        this.activityManagerWrapper = activityManagerWrapper2;
        this.packageManagerWrapper = packageManagerWrapper2;
        this.devicePolicyManagerWrapper = devicePolicyManagerWrapper2;
        this.smartRepliesInflater = smartReplyInflater;
        this.smartActionsInflater = smartActionInflater;
    }

    @NotNull
    public InflatedSmartReplyState inflateSmartReplyState(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        return chooseSmartRepliesAndActions(notificationEntry);
    }

    @NotNull
    public InflatedSmartReplyViewHolder inflateSmartReplyViewHolder(@NotNull Context context, @NotNull Context context2, @NotNull NotificationEntry notificationEntry, @Nullable InflatedSmartReplyState inflatedSmartReplyState, @NotNull InflatedSmartReplyState inflatedSmartReplyState2) {
        boolean z;
        Sequence<R> sequence;
        Intrinsics.checkNotNullParameter(context, "sysuiContext");
        Intrinsics.checkNotNullParameter(context2, "notifPackageContext");
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        Intrinsics.checkNotNullParameter(inflatedSmartReplyState2, "newSmartReplyState");
        Sequence<R> sequence2 = null;
        if (!SmartReplyStateInflaterKt.shouldShowSmartReplyView(notificationEntry, inflatedSmartReplyState2)) {
            return new InflatedSmartReplyViewHolder((SmartReplyView) null, (List<? extends Button>) null);
        }
        boolean z2 = !SmartReplyStateInflaterKt.areSuggestionsSimilar(inflatedSmartReplyState, inflatedSmartReplyState2);
        SmartReplyView inflate = SmartReplyView.inflate(context, this.constants);
        SmartReplyView.SmartReplies smartReplies = inflatedSmartReplyState2.getSmartReplies();
        if (smartReplies == null) {
            z = false;
        } else {
            z = smartReplies.fromAssistant;
        }
        inflate.setSmartRepliesGeneratedByAssistant(z);
        if (smartReplies == null) {
            sequence = null;
        } else {
            List<CharSequence> list = smartReplies.choices;
            Intrinsics.checkNotNullExpressionValue(list, "smartReplies.choices");
            sequence = SequencesKt___SequencesKt.mapIndexed(CollectionsKt___CollectionsKt.asSequence(list), new C2064xfbefda06(this, inflate, notificationEntry, smartReplies, z2));
        }
        if (sequence == null) {
            sequence = SequencesKt__SequencesKt.emptySequence();
        }
        Sequence<R> sequence3 = sequence;
        SmartReplyView.SmartActions smartActions = inflatedSmartReplyState2.getSmartActions();
        if (smartActions != null) {
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context2, context.getTheme());
            List<Notification.Action> list2 = smartActions.actions;
            Intrinsics.checkNotNullExpressionValue(list2, "smartActions.actions");
            sequence2 = SequencesKt___SequencesKt.mapIndexed(SequencesKt___SequencesKt.filter(CollectionsKt___CollectionsKt.asSequence(list2), C2062xa9afa0f0.INSTANCE), new C2063xa9afa0f1(this, inflate, notificationEntry, smartActions, z2, contextThemeWrapper));
        }
        if (sequence2 == null) {
            sequence2 = SequencesKt__SequencesKt.emptySequence();
        }
        return new InflatedSmartReplyViewHolder(inflate, SequencesKt___SequencesKt.toList(SequencesKt___SequencesKt.plus(sequence3, sequence2)));
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0116 A[EDGE_INSN: B:92:0x0116->B:56:0x0116 ?: BREAK  , SYNTHETIC] */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.android.systemui.statusbar.policy.InflatedSmartReplyState chooseSmartRepliesAndActions(@org.jetbrains.annotations.NotNull com.android.systemui.statusbar.notification.collection.NotificationEntry r13) {
        /*
            r12 = this;
            java.lang.String r0 = "entry"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r13, r0)
            android.service.notification.StatusBarNotification r0 = r13.getSbn()
            android.app.Notification r0 = r0.getNotification()
            r1 = 0
            android.util.Pair r2 = r0.findRemoteInputActionPair(r1)
            r3 = 1
            android.util.Pair r4 = r0.findRemoteInputActionPair(r3)
            com.android.systemui.statusbar.policy.SmartReplyConstants r5 = r12.constants
            boolean r5 = r5.isEnabled()
            r6 = 0
            if (r5 != 0) goto L_0x003f
            boolean r12 = com.android.systemui.statusbar.policy.SmartReplyStateInflaterKt.DEBUG
            if (r12 == 0) goto L_0x0039
            android.service.notification.StatusBarNotification r12 = r13.getSbn()
            java.lang.String r12 = r12.getKey()
            java.lang.String r13 = "Smart suggestions not enabled, not adding suggestions for "
            java.lang.String r12 = kotlin.jvm.internal.Intrinsics.stringPlus(r13, r12)
            java.lang.String r13 = "SmartReplyViewInflater"
            android.util.Log.d(r13, r12)
        L_0x0039:
            com.android.systemui.statusbar.policy.InflatedSmartReplyState r12 = new com.android.systemui.statusbar.policy.InflatedSmartReplyState
            r12.<init>(r6, r6, r6, r1)
            return r12
        L_0x003f:
            com.android.systemui.statusbar.policy.SmartReplyConstants r5 = r12.constants
            boolean r5 = r5.requiresTargetingP()
            if (r5 == 0) goto L_0x0050
            int r5 = r13.targetSdk
            r7 = 28
            if (r5 < r7) goto L_0x004e
            goto L_0x0050
        L_0x004e:
            r5 = r1
            goto L_0x0051
        L_0x0050:
            r5 = r3
        L_0x0051:
            java.util.List r7 = r0.getContextualActions()
            if (r5 == 0) goto L_0x009d
            if (r2 != 0) goto L_0x005a
            goto L_0x009d
        L_0x005a:
            java.lang.Object r5 = r2.second
            android.app.Notification$Action r5 = (android.app.Notification.Action) r5
            android.app.PendingIntent r5 = r5.actionIntent
            if (r5 != 0) goto L_0x0063
            goto L_0x009d
        L_0x0063:
            java.lang.Object r8 = r2.first
            android.app.RemoteInput r8 = (android.app.RemoteInput) r8
            java.lang.CharSequence[] r8 = r8.getChoices()
            if (r8 != 0) goto L_0x006f
            r8 = r6
            goto L_0x007a
        L_0x006f:
            int r8 = r8.length
            if (r8 != 0) goto L_0x0074
            r8 = r3
            goto L_0x0075
        L_0x0074:
            r8 = r1
        L_0x0075:
            r8 = r8 ^ r3
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r8)
        L_0x007a:
            java.lang.Boolean r9 = java.lang.Boolean.TRUE
            boolean r8 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r8, (java.lang.Object) r9)
            if (r8 == 0) goto L_0x009d
            com.android.systemui.statusbar.policy.SmartReplyView$SmartReplies r8 = new com.android.systemui.statusbar.policy.SmartReplyView$SmartReplies
            java.lang.Object r9 = r2.first
            android.app.RemoteInput r9 = (android.app.RemoteInput) r9
            java.lang.CharSequence[] r9 = r9.getChoices()
            java.lang.String r10 = "pair.first.choices"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r9, r10)
            java.util.List r9 = kotlin.collections.ArraysKt___ArraysJvmKt.asList(r9)
            java.lang.Object r2 = r2.first
            android.app.RemoteInput r2 = (android.app.RemoteInput) r2
            r8.<init>(r9, r2, r5, r1)
            goto L_0x009e
        L_0x009d:
            r8 = r6
        L_0x009e:
            java.lang.String r2 = "appGeneratedSmartActions"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r7, r2)
            boolean r2 = r7.isEmpty()
            r2 = r2 ^ r3
            if (r2 == 0) goto L_0x00b0
            com.android.systemui.statusbar.policy.SmartReplyView$SmartActions r2 = new com.android.systemui.statusbar.policy.SmartReplyView$SmartActions
            r2.<init>(r7, r1)
            goto L_0x00b1
        L_0x00b0:
            r2 = r6
        L_0x00b1:
            if (r8 != 0) goto L_0x010e
            if (r2 != 0) goto L_0x010e
            java.util.List r5 = r13.getSmartReplies()
            java.lang.String r7 = "entry.smartReplies"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r7)
            java.util.List r13 = r13.getSmartActions()
            java.lang.String r7 = "entry.smartActions"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r13, r7)
            boolean r7 = r5.isEmpty()
            r7 = r7 ^ r3
            if (r7 == 0) goto L_0x00f0
            if (r4 == 0) goto L_0x00f0
            java.lang.Object r7 = r4.second
            android.app.Notification$Action r7 = (android.app.Notification.Action) r7
            boolean r7 = r7.getAllowGeneratedReplies()
            if (r7 == 0) goto L_0x00f0
            java.lang.Object r7 = r4.second
            r9 = r7
            android.app.Notification$Action r9 = (android.app.Notification.Action) r9
            android.app.PendingIntent r9 = r9.actionIntent
            if (r9 == 0) goto L_0x00f0
            com.android.systemui.statusbar.policy.SmartReplyView$SmartReplies r8 = new com.android.systemui.statusbar.policy.SmartReplyView$SmartReplies
            java.lang.Object r4 = r4.first
            android.app.RemoteInput r4 = (android.app.RemoteInput) r4
            android.app.Notification$Action r7 = (android.app.Notification.Action) r7
            android.app.PendingIntent r7 = r7.actionIntent
            r8.<init>(r5, r4, r7, r3)
        L_0x00f0:
            boolean r4 = r13.isEmpty()
            r4 = r4 ^ r3
            if (r4 == 0) goto L_0x010e
            boolean r4 = r0.getAllowSystemGeneratedContextualActions()
            if (r4 == 0) goto L_0x010e
            com.android.systemui.shared.system.ActivityManagerWrapper r2 = r12.activityManagerWrapper
            boolean r2 = r2.isLockTaskKioskModeActive()
            if (r2 == 0) goto L_0x0109
            java.util.List r13 = r12.filterAllowlistedLockTaskApps(r13)
        L_0x0109:
            com.android.systemui.statusbar.policy.SmartReplyView$SmartActions r2 = new com.android.systemui.statusbar.policy.SmartReplyView$SmartActions
            r2.<init>(r13, r3)
        L_0x010e:
            if (r2 != 0) goto L_0x0112
            r12 = r6
            goto L_0x0114
        L_0x0112:
            java.util.List<android.app.Notification$Action> r12 = r2.actions
        L_0x0114:
            if (r12 != 0) goto L_0x0118
        L_0x0116:
            r12 = r1
            goto L_0x0143
        L_0x0118:
            boolean r13 = r12.isEmpty()
            if (r13 == 0) goto L_0x011f
            goto L_0x0116
        L_0x011f:
            java.util.Iterator r12 = r12.iterator()
        L_0x0123:
            boolean r13 = r12.hasNext()
            if (r13 == 0) goto L_0x0116
            java.lang.Object r13 = r12.next()
            android.app.Notification$Action r13 = (android.app.Notification.Action) r13
            boolean r4 = r13.isContextual()
            if (r4 == 0) goto L_0x013f
            int r13 = r13.getSemanticAction()
            r4 = 12
            if (r13 != r4) goto L_0x013f
            r13 = r3
            goto L_0x0140
        L_0x013f:
            r13 = r1
        L_0x0140:
            if (r13 == 0) goto L_0x0123
            r12 = r3
        L_0x0143:
            if (r12 == 0) goto L_0x0189
            android.app.Notification$Action[] r13 = r0.actions
            java.lang.String r0 = "notification.actions"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r13, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r4 = r13.length
            r5 = r1
            r7 = r5
        L_0x0154:
            if (r5 >= r4) goto L_0x0184
            r9 = r13[r5]
            int r10 = r7 + 1
            android.app.RemoteInput[] r9 = r9.getRemoteInputs()
            if (r9 != 0) goto L_0x0162
            r9 = r6
            goto L_0x016d
        L_0x0162:
            int r9 = r9.length
            if (r9 != 0) goto L_0x0167
            r9 = r3
            goto L_0x0168
        L_0x0167:
            r9 = r1
        L_0x0168:
            r9 = r9 ^ r3
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r9)
        L_0x016d:
            java.lang.Boolean r11 = java.lang.Boolean.TRUE
            boolean r9 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r9, (java.lang.Object) r11)
            if (r9 == 0) goto L_0x017a
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            goto L_0x017b
        L_0x017a:
            r7 = r6
        L_0x017b:
            if (r7 == 0) goto L_0x0180
            r0.add(r7)
        L_0x0180:
            int r5 = r5 + 1
            r7 = r10
            goto L_0x0154
        L_0x0184:
            com.android.systemui.statusbar.policy.InflatedSmartReplyState$SuppressedActions r6 = new com.android.systemui.statusbar.policy.InflatedSmartReplyState$SuppressedActions
            r6.<init>(r0)
        L_0x0189:
            com.android.systemui.statusbar.policy.InflatedSmartReplyState r13 = new com.android.systemui.statusbar.policy.InflatedSmartReplyState
            r13.<init>(r8, r2, r6, r12)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.SmartReplyStateInflaterImpl.chooseSmartRepliesAndActions(com.android.systemui.statusbar.notification.collection.NotificationEntry):com.android.systemui.statusbar.policy.InflatedSmartReplyState");
    }

    private final List<Notification.Action> filterAllowlistedLockTaskApps(List<? extends Notification.Action> list) {
        Intent intent;
        ArrayList arrayList = new ArrayList();
        for (T next : list) {
            PendingIntent pendingIntent = ((Notification.Action) next).actionIntent;
            boolean z = false;
            ResolveInfo resolveInfo = null;
            if (!(pendingIntent == null || (intent = pendingIntent.getIntent()) == null)) {
                resolveInfo = this.packageManagerWrapper.resolveActivity(intent, 0);
            }
            if (resolveInfo != null) {
                z = this.devicePolicyManagerWrapper.isLockTaskPermitted(resolveInfo.activityInfo.packageName);
            }
            if (z) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }
}
