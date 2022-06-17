package com.android.systemui.statusbar.notification.row;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.util.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ChannelEditorListView.kt */
public final class ChannelEditorListView extends LinearLayout {
    private AppControlView appControlRow;
    @Nullable
    private Drawable appIcon;
    @Nullable
    private String appName;
    @NotNull
    private List<NotificationChannelGroup> channelGroups = new ArrayList();
    @NotNull
    private final List<ChannelRow> channelRows = new ArrayList();
    @NotNull
    private List<NotificationChannel> channels = new ArrayList();
    public ChannelEditorDialogController controller;
    @Nullable
    private RestrictedLockUtils.EnforcedAdmin suspendedAppsAdmin;
    private boolean systemApp;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChannelEditorListView(@NotNull Context context, @NotNull AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.checkNotNullParameter(context, "c");
        Intrinsics.checkNotNullParameter(attributeSet, "attrs");
    }

    @NotNull
    public final ChannelEditorDialogController getController() {
        ChannelEditorDialogController channelEditorDialogController = this.controller;
        if (channelEditorDialogController != null) {
            return channelEditorDialogController;
        }
        Intrinsics.throwUninitializedPropertyAccessException("controller");
        throw null;
    }

    public final void setController(@NotNull ChannelEditorDialogController channelEditorDialogController) {
        Intrinsics.checkNotNullParameter(channelEditorDialogController, "<set-?>");
        this.controller = channelEditorDialogController;
    }

    public final void setAppIcon(@Nullable Drawable drawable) {
        this.appIcon = drawable;
    }

    public final void setAppName(@Nullable String str) {
        this.appName = str;
    }

    public final void setSystemApp(boolean z) {
        this.systemApp = z;
    }

    @Nullable
    public final RestrictedLockUtils.EnforcedAdmin getSuspendedAppsAdmin() {
        return this.suspendedAppsAdmin;
    }

    public final void setSuspendedAppsAdmin(@Nullable RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.suspendedAppsAdmin = enforcedAdmin;
    }

    public final void setChannelGroups(@NotNull List<NotificationChannelGroup> list) {
        Intrinsics.checkNotNullParameter(list, "newValue");
        this.channelGroups = list;
    }

    public final void setChannels(@NotNull List<NotificationChannel> list) {
        Intrinsics.checkNotNullParameter(list, "newValue");
        this.channels = list;
        updateRows();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(R$id.app_control);
        Intrinsics.checkNotNullExpressionValue(findViewById, "findViewById(R.id.app_control)");
        this.appControlRow = (AppControlView) findViewById;
    }

    public final void highlightChannel(@NotNull NotificationChannel notificationChannel) {
        Intrinsics.checkNotNullParameter(notificationChannel, "channel");
        Assert.isMainThread();
        for (ChannelRow next : this.channelRows) {
            if (Intrinsics.areEqual((Object) next.getChannel(), (Object) notificationChannel)) {
                next.playHighlight();
            }
        }
    }

    /* access modifiers changed from: private */
    public final void updateRows() {
        boolean areAppNotificationsEnabled = getController().areAppNotificationsEnabled();
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(200);
        autoTransition.addListener(new ChannelEditorListView$updateRows$1(this));
        TransitionManager.beginDelayedTransition(this, autoTransition);
        for (ChannelRow removeView : this.channelRows) {
            removeView(removeView);
        }
        this.channelRows.clear();
        updateAppControlRow(areAppNotificationsEnabled);
        if (areAppNotificationsEnabled) {
            LayoutInflater from = LayoutInflater.from(getContext());
            boolean z = true;
            for (NotificationChannel next : this.channels) {
                Intrinsics.checkNotNullExpressionValue(from, "inflater");
                addChannelRow(next, from);
                if (isChannelBlockable(next)) {
                    z = false;
                }
            }
            getController().setApplyVisible(!z);
        }
    }

    private final boolean isChannelBlockable(NotificationChannel notificationChannel) {
        if (this.systemApp && !notificationChannel.isBlockable() && notificationChannel.getImportance() != 0) {
            return false;
        }
        return true;
    }

    private final void addChannelRow(NotificationChannel notificationChannel, LayoutInflater layoutInflater) {
        View inflate = layoutInflater.inflate(R$layout.notif_half_shelf_row, (ViewGroup) null);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type com.android.systemui.statusbar.notification.row.ChannelRow");
        ChannelRow channelRow = (ChannelRow) inflate;
        channelRow.setController(getController());
        channelRow.setChannel(notificationChannel);
        channelRow.getSwitch().setEnabled(this.suspendedAppsAdmin == null && isChannelBlockable(notificationChannel));
        channelRow.setEnabled(channelRow.getSwitch().isEnabled());
        this.channelRows.add(channelRow);
        addView(channelRow);
    }

    /* JADX WARNING: Removed duplicated region for block: B:1:0x0008 A[LOOP:0: B:1:0x0008->B:4:0x0018, LOOP_START, PHI: r2 
      PHI: (r2v1 boolean) = (r2v0 boolean), (r2v14 boolean) binds: [B:0:0x0000, B:4:0x0018] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void updateAppControlRow(boolean r9) {
        /*
            r8 = this;
            java.util.List<android.app.NotificationChannelGroup> r0 = r8.channelGroups
            java.util.Iterator r0 = r0.iterator()
            r1 = 1
            r2 = r1
        L_0x0008:
            boolean r3 = r0.hasNext()
            if (r3 == 0) goto L_0x001a
            java.lang.Object r2 = r0.next()
            android.app.NotificationChannelGroup r2 = (android.app.NotificationChannelGroup) r2
            boolean r2 = r2.isBlocked()
            if (r2 != 0) goto L_0x0008
        L_0x001a:
            com.android.systemui.statusbar.notification.row.AppControlView r0 = r8.appControlRow
            r3 = 0
            java.lang.String r4 = "appControlRow"
            if (r0 == 0) goto L_0x00a7
            android.widget.Switch r0 = r0.getSwitch()
            com.android.settingslib.RestrictedLockUtils$EnforcedAdmin r5 = r8.suspendedAppsAdmin
            r6 = 0
            if (r5 != 0) goto L_0x0032
            if (r2 != 0) goto L_0x0030
            boolean r2 = r8.systemApp
            if (r2 != 0) goto L_0x0032
        L_0x0030:
            r2 = r1
            goto L_0x0033
        L_0x0032:
            r2 = r6
        L_0x0033:
            r0.setEnabled(r2)
            com.android.systemui.statusbar.notification.row.AppControlView r0 = r8.appControlRow
            if (r0 == 0) goto L_0x00a3
            if (r0 == 0) goto L_0x009f
            android.widget.Switch r2 = r0.getSwitch()
            boolean r2 = r2.isEnabled()
            r0.setEnabled(r2)
            com.android.systemui.statusbar.notification.row.AppControlView r0 = r8.appControlRow
            if (r0 == 0) goto L_0x009b
            android.widget.ImageView r0 = r0.getIconView()
            android.graphics.drawable.Drawable r2 = r8.appIcon
            r0.setImageDrawable(r2)
            com.android.systemui.statusbar.notification.row.AppControlView r0 = r8.appControlRow
            if (r0 == 0) goto L_0x0097
            android.widget.TextView r0 = r0.getChannelName()
            android.content.Context r2 = r8.getContext()
            android.content.res.Resources r2 = r2.getResources()
            int r5 = com.android.systemui.R$string.notification_channel_dialog_title
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r7 = r8.appName
            r1[r6] = r7
            java.lang.String r1 = r2.getString(r5, r1)
            r0.setText(r1)
            com.android.systemui.statusbar.notification.row.AppControlView r0 = r8.appControlRow
            if (r0 == 0) goto L_0x0093
            android.widget.Switch r0 = r0.getSwitch()
            r0.setChecked(r9)
            com.android.systemui.statusbar.notification.row.AppControlView r9 = r8.appControlRow
            if (r9 == 0) goto L_0x008f
            android.widget.Switch r9 = r9.getSwitch()
            com.android.systemui.statusbar.notification.row.ChannelEditorListView$updateAppControlRow$1 r0 = new com.android.systemui.statusbar.notification.row.ChannelEditorListView$updateAppControlRow$1
            r0.<init>(r8)
            r9.setOnCheckedChangeListener(r0)
            return
        L_0x008f:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            throw r3
        L_0x0093:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            throw r3
        L_0x0097:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            throw r3
        L_0x009b:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            throw r3
        L_0x009f:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            throw r3
        L_0x00a3:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            throw r3
        L_0x00a7:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.ChannelEditorListView.updateAppControlRow(boolean):void");
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(@Nullable Configuration configuration) {
        super.onConfigurationChanged(configuration);
        getController().updateDialog();
        int childCount = getChildCount();
        if (childCount >= 0) {
            while (true) {
                int i = childCount - 1;
                View childAt = getChildAt(childCount);
                if (childAt instanceof ChannelRow) {
                    ((ChannelRow) childAt).updateViews();
                }
                if (i >= 0) {
                    childCount = i;
                } else {
                    return;
                }
            }
        }
    }
}
