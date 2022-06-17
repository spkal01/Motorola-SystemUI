package com.android.systemui.statusbar.notification.row;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ChannelEditorListView.kt */
public final class ChannelRow extends LinearLayout {
    @Nullable
    private NotificationChannel channel;
    private TextView channelDescription;
    private TextView channelName;
    public ChannelEditorDialogController controller;
    private boolean gentle;
    private final int highlightColor = Utils.getColorAttrDefaultColor(getContext(), 16843820);

    /* renamed from: switch  reason: not valid java name */
    public Switch f202switch;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChannelRow(@NotNull Context context, @NotNull AttributeSet attributeSet) {
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

    @NotNull
    public final Switch getSwitch() {
        Switch switchR = this.f202switch;
        if (switchR != null) {
            return switchR;
        }
        Intrinsics.throwUninitializedPropertyAccessException("switch");
        throw null;
    }

    public final void setSwitch(@NotNull Switch switchR) {
        Intrinsics.checkNotNullParameter(switchR, "<set-?>");
        this.f202switch = switchR;
    }

    @Nullable
    public final NotificationChannel getChannel() {
        return this.channel;
    }

    public final void setChannel(@Nullable NotificationChannel notificationChannel) {
        this.channel = notificationChannel;
        updateImportance();
        updateViews();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(R$id.channel_name);
        Intrinsics.checkNotNullExpressionValue(findViewById, "findViewById(R.id.channel_name)");
        this.channelName = (TextView) findViewById;
        View findViewById2 = findViewById(R$id.channel_description);
        Intrinsics.checkNotNullExpressionValue(findViewById2, "findViewById(R.id.channel_description)");
        this.channelDescription = (TextView) findViewById2;
        View findViewById3 = findViewById(R$id.toggle);
        Intrinsics.checkNotNullExpressionValue(findViewById3, "findViewById(R.id.toggle)");
        setSwitch((Switch) findViewById3);
        getSwitch().setOnCheckedChangeListener(new ChannelRow$onFinishInflate$1(this));
        setOnClickListener(new ChannelRow$onFinishInflate$2(this));
    }

    public final void playHighlight() {
        ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[]{0, Integer.valueOf(this.highlightColor)});
        ofObject.setDuration(200);
        ofObject.addUpdateListener(new ChannelRow$playHighlight$1(this));
        ofObject.setRepeatMode(2);
        ofObject.setRepeatCount(5);
        ofObject.start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x0079  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void updateViews() {
        /*
            r6 = this;
            android.app.NotificationChannel r0 = r6.channel
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            android.widget.TextView r1 = r6.channelName
            r2 = 0
            if (r1 == 0) goto L_0x0086
            java.lang.CharSequence r3 = r0.getName()
            if (r3 != 0) goto L_0x0012
            java.lang.String r3 = ""
        L_0x0012:
            r1.setText(r3)
            java.lang.String r1 = r0.getGroup()
            java.lang.String r3 = "channelDescription"
            if (r1 != 0) goto L_0x001e
            goto L_0x002d
        L_0x001e:
            android.widget.TextView r4 = r6.channelDescription
            if (r4 == 0) goto L_0x0082
            com.android.systemui.statusbar.notification.row.ChannelEditorDialogController r5 = r6.getController()
            java.lang.CharSequence r1 = r5.groupNameForId(r1)
            r4.setText(r1)
        L_0x002d:
            java.lang.String r1 = r0.getGroup()
            r4 = 0
            if (r1 == 0) goto L_0x0053
            android.widget.TextView r1 = r6.channelDescription
            if (r1 == 0) goto L_0x004f
            java.lang.CharSequence r1 = r1.getText()
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0043
            goto L_0x0053
        L_0x0043:
            android.widget.TextView r1 = r6.channelDescription
            if (r1 == 0) goto L_0x004b
            r1.setVisibility(r4)
            goto L_0x005c
        L_0x004b:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r3)
            throw r2
        L_0x004f:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r3)
            throw r2
        L_0x0053:
            android.widget.TextView r1 = r6.channelDescription
            if (r1 == 0) goto L_0x007e
            r2 = 8
            r1.setVisibility(r2)
        L_0x005c:
            com.android.systemui.statusbar.notification.row.ChannelEditorDialogController r1 = r6.getController()
            int r1 = r1.isInEditList(r0)
            int r2 = r0.getImportance()
            if (r2 == r1) goto L_0x006f
            r2 = -1000(0xfffffffffffffc18, float:NaN)
            if (r1 == r2) goto L_0x006f
            goto L_0x0073
        L_0x006f:
            int r1 = r0.getImportance()
        L_0x0073:
            android.widget.Switch r6 = r6.getSwitch()
            if (r1 == 0) goto L_0x007a
            r4 = 1
        L_0x007a:
            r6.setChecked(r4)
            return
        L_0x007e:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r3)
            throw r2
        L_0x0082:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r3)
            throw r2
        L_0x0086:
            java.lang.String r6 = "channelName"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r6)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.ChannelRow.updateViews():void");
    }

    private final void updateImportance() {
        NotificationChannel notificationChannel = this.channel;
        boolean z = false;
        int importance = notificationChannel == null ? 0 : notificationChannel.getImportance();
        if (importance != -1000 && importance < 3) {
            z = true;
        }
        this.gentle = z;
    }
}
