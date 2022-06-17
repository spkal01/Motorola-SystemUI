package com.android.systemui.statusbar.policy;

import android.app.Notification;
import com.android.systemui.statusbar.policy.SmartReplyView;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: InflatedSmartReplyState.kt */
public final class InflatedSmartReplyState {
    private final boolean hasPhishingAction;
    @Nullable
    private final SmartReplyView.SmartActions smartActions;
    @Nullable
    private final SmartReplyView.SmartReplies smartReplies;
    @Nullable
    private final SuppressedActions suppressedActions;

    public InflatedSmartReplyState(@Nullable SmartReplyView.SmartReplies smartReplies2, @Nullable SmartReplyView.SmartActions smartActions2, @Nullable SuppressedActions suppressedActions2, boolean z) {
        this.smartReplies = smartReplies2;
        this.smartActions = smartActions2;
        this.suppressedActions = suppressedActions2;
        this.hasPhishingAction = z;
    }

    @Nullable
    public final SmartReplyView.SmartReplies getSmartReplies() {
        return this.smartReplies;
    }

    @Nullable
    public final SmartReplyView.SmartActions getSmartActions() {
        return this.smartActions;
    }

    public final boolean getHasPhishingAction() {
        return this.hasPhishingAction;
    }

    @NotNull
    public final List<CharSequence> getSmartRepliesList() {
        SmartReplyView.SmartReplies smartReplies2 = this.smartReplies;
        List<CharSequence> list = smartReplies2 == null ? null : smartReplies2.choices;
        return list == null ? CollectionsKt__CollectionsKt.emptyList() : list;
    }

    @NotNull
    public final List<Notification.Action> getSmartActionsList() {
        SmartReplyView.SmartActions smartActions2 = this.smartActions;
        List<Notification.Action> list = smartActions2 == null ? null : smartActions2.actions;
        return list == null ? CollectionsKt__CollectionsKt.emptyList() : list;
    }

    @NotNull
    public final List<Integer> getSuppressedActionIndices() {
        SuppressedActions suppressedActions2 = this.suppressedActions;
        return suppressedActions2 == null ? CollectionsKt__CollectionsKt.emptyList() : suppressedActions2.getSuppressedActionIndices();
    }

    /* compiled from: InflatedSmartReplyState.kt */
    public static final class SuppressedActions {
        @NotNull
        private final List<Integer> suppressedActionIndices;

        public SuppressedActions(@NotNull List<Integer> list) {
            Intrinsics.checkNotNullParameter(list, "suppressedActionIndices");
            this.suppressedActionIndices = list;
        }

        @NotNull
        public final List<Integer> getSuppressedActionIndices() {
            return this.suppressedActionIndices;
        }
    }
}
