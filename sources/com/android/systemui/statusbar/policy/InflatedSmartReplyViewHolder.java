package com.android.systemui.statusbar.policy;

import android.widget.Button;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/* compiled from: InflatedSmartReplyViewHolder.kt */
public final class InflatedSmartReplyViewHolder {
    @Nullable
    private final SmartReplyView smartReplyView;
    @Nullable
    private final List<Button> smartSuggestionButtons;

    public InflatedSmartReplyViewHolder(@Nullable SmartReplyView smartReplyView2, @Nullable List<? extends Button> list) {
        this.smartReplyView = smartReplyView2;
        this.smartSuggestionButtons = list;
    }

    @Nullable
    public final SmartReplyView getSmartReplyView() {
        return this.smartReplyView;
    }

    @Nullable
    public final List<Button> getSmartSuggestionButtons() {
        return this.smartSuggestionButtons;
    }
}
