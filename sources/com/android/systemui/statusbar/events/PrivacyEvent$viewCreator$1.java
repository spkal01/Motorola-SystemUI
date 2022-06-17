package com.android.systemui.statusbar.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import com.android.systemui.privacy.OngoingPrivacyChip;
import java.util.Objects;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: StatusEvent.kt */
final class PrivacyEvent$viewCreator$1 extends Lambda implements Function1<Context, OngoingPrivacyChip> {
    final /* synthetic */ PrivacyEvent this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    PrivacyEvent$viewCreator$1(PrivacyEvent privacyEvent) {
        super(1);
        this.this$0 = privacyEvent;
    }

    @NotNull
    public final OngoingPrivacyChip invoke(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        View inflate = LayoutInflater.from(context).inflate(R$layout.ongoing_privacy_chip, (ViewGroup) null);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type com.android.systemui.privacy.OngoingPrivacyChip");
        OngoingPrivacyChip ongoingPrivacyChip = (OngoingPrivacyChip) inflate;
        ongoingPrivacyChip.setPrivacyList(this.this$0.getPrivacyItems());
        ongoingPrivacyChip.setContentDescription(this.this$0.getContentDescription());
        this.this$0.privacyChip = ongoingPrivacyChip;
        return ongoingPrivacyChip;
    }
}
