package com.android.systemui.statusbar.events;

import android.content.Context;
import android.view.View;
import com.android.systemui.statusbar.events.StatusEvent;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: StatusEvent.kt */
public final class BatteryEvent implements StatusEvent {
    @Nullable
    private String contentDescription = "";
    private final boolean forceVisible;
    private final int priority = 50;
    private final boolean showAnimation = true;
    @NotNull
    private final Function1<Context, View> viewCreator = BatteryEvent$viewCreator$1.INSTANCE;

    public boolean shouldUpdateFromEvent(@Nullable StatusEvent statusEvent) {
        return StatusEvent.DefaultImpls.shouldUpdateFromEvent(this, statusEvent);
    }

    public void updateFromEvent(@Nullable StatusEvent statusEvent) {
        StatusEvent.DefaultImpls.updateFromEvent(this, statusEvent);
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean getForceVisible() {
        return this.forceVisible;
    }

    public boolean getShowAnimation() {
        return this.showAnimation;
    }

    @Nullable
    public String getContentDescription() {
        return this.contentDescription;
    }

    @NotNull
    public Function1<Context, View> getViewCreator() {
        return this.viewCreator;
    }

    @NotNull
    public String toString() {
        String simpleName = BatteryEvent.class.getSimpleName();
        Intrinsics.checkNotNullExpressionValue(simpleName, "javaClass.simpleName");
        return simpleName;
    }
}
