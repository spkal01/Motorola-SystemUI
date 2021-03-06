package com.android.systemui.statusbar.notification.row;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import java.util.List;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

/* compiled from: ChannelEditorDialogController.kt */
final class ChannelEditorDialogController$getDisplayableChannels$channels$1 extends Lambda implements Function1<NotificationChannelGroup, Sequence<? extends NotificationChannel>> {
    public static final ChannelEditorDialogController$getDisplayableChannels$channels$1 INSTANCE = new ChannelEditorDialogController$getDisplayableChannels$channels$1();

    ChannelEditorDialogController$getDisplayableChannels$channels$1() {
        super(1);
    }

    @NotNull
    public final Sequence<NotificationChannel> invoke(@NotNull NotificationChannelGroup notificationChannelGroup) {
        Intrinsics.checkNotNullParameter(notificationChannelGroup, "group");
        List<NotificationChannel> channels = notificationChannelGroup.getChannels();
        Intrinsics.checkNotNullExpressionValue(channels, "group.channels");
        return SequencesKt___SequencesKt.filterNot(CollectionsKt___CollectionsKt.asSequence(channels), C16041.INSTANCE);
    }
}
