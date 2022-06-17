package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ChannelEditorListView.kt */
public final class AppControlView extends LinearLayout {
    public TextView channelName;
    public ImageView iconView;

    /* renamed from: switch  reason: not valid java name */
    public Switch f201switch;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AppControlView(@NotNull Context context, @NotNull AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.checkNotNullParameter(context, "c");
        Intrinsics.checkNotNullParameter(attributeSet, "attrs");
    }

    @NotNull
    public final ImageView getIconView() {
        ImageView imageView = this.iconView;
        if (imageView != null) {
            return imageView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("iconView");
        throw null;
    }

    public final void setIconView(@NotNull ImageView imageView) {
        Intrinsics.checkNotNullParameter(imageView, "<set-?>");
        this.iconView = imageView;
    }

    @NotNull
    public final TextView getChannelName() {
        TextView textView = this.channelName;
        if (textView != null) {
            return textView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("channelName");
        throw null;
    }

    public final void setChannelName(@NotNull TextView textView) {
        Intrinsics.checkNotNullParameter(textView, "<set-?>");
        this.channelName = textView;
    }

    @NotNull
    public final Switch getSwitch() {
        Switch switchR = this.f201switch;
        if (switchR != null) {
            return switchR;
        }
        Intrinsics.throwUninitializedPropertyAccessException("switch");
        throw null;
    }

    public final void setSwitch(@NotNull Switch switchR) {
        Intrinsics.checkNotNullParameter(switchR, "<set-?>");
        this.f201switch = switchR;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        View findViewById = findViewById(R$id.icon);
        Intrinsics.checkNotNullExpressionValue(findViewById, "findViewById(R.id.icon)");
        setIconView((ImageView) findViewById);
        View findViewById2 = findViewById(R$id.app_name);
        Intrinsics.checkNotNullExpressionValue(findViewById2, "findViewById(R.id.app_name)");
        setChannelName((TextView) findViewById2);
        View findViewById3 = findViewById(R$id.toggle);
        Intrinsics.checkNotNullExpressionValue(findViewById3, "findViewById(R.id.toggle)");
        setSwitch((Switch) findViewById3);
        setOnClickListener(new AppControlView$onFinishInflate$1(this));
    }
}
