package com.android.systemui.plugins.p005qs;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(version = 1)
/* renamed from: com.android.systemui.plugins.qs.DetailAdapter */
public interface DetailAdapter {
    public static final UiEventLogger.UiEventEnum INVALID = DetailAdapter$$ExternalSyntheticLambda0.INSTANCE;
    public static final int VERSION = 1;

    /* access modifiers changed from: private */
    static /* synthetic */ int lambda$static$0() {
        return 0;
    }

    View createDetailView(Context context, View view, ViewGroup viewGroup);

    int getDoneText() {
        return 0;
    }

    int getMetricsCategory();

    Intent getSettingsIntent();

    int getSettingsText() {
        return 0;
    }

    CharSequence getTitle();

    boolean getToggleEnabled() {
        return true;
    }

    Boolean getToggleState();

    boolean hasHeader() {
        return true;
    }

    boolean onDoneButtonClicked() {
        return false;
    }

    void setToggleState(boolean z);

    boolean shouldAnimate() {
        return true;
    }

    UiEventLogger.UiEventEnum openDetailEvent() {
        return INVALID;
    }

    UiEventLogger.UiEventEnum closeDetailEvent() {
        return INVALID;
    }

    UiEventLogger.UiEventEnum moreSettingsEvent() {
        return INVALID;
    }
}
